import javafx.concurrent.Task;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TaskReadExcelForOzon extends Task<Map> {

    private int countReadsRows;
    private int countReadsRows_1C;

    public TaskReadExcelForOzon(List<File> files) {
        this.files = files;
    }

    private final List<File> files;

    public Map<String, ResultProduct> readWorkbook(List<File> files) {

        Map<String, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
        Map<String, Supplier> supplierSpecPriceHashMapWithKeyCode_1C = new HashMap<>();
        Map<String, Integer> mapCountForMyProductName = new HashMap<>();

        //читаем файл отчёта Ozon и файл 1С
        Workbook workbookOzon = null;
        Workbook workbook_1C = null;
        try {
            workbookOzon = new XSSFWorkbook(files.get(0));
            workbook_1C = new XSSFWorkbook(files.get(1));
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

        //получаем страницы
        Sheet sheetOzon = null;
        Sheet sheet_1C = null;
        try {
            sheetOzon = workbookOzon.getSheetAt(1);
            sheet_1C = workbook_1C.getSheetAt(0);
        } catch (Exception e) {
            sheetOzon = workbook_1C.getSheetAt(1);
            sheet_1C = workbookOzon.getSheetAt(0);
        }

        //проверяем, правильно ли мы прочитали файлы
        if (!checkFileOzon(sheetOzon) || !checkFile_1C(sheet_1C)) {
            Sheet sheetBuff = sheet_1C;
            sheet_1C = sheetOzon;
            sheetOzon = sheetBuff;
            if (!checkFileOzon(sheetOzon) || !checkFile_1C(sheet_1C)) {
                System.out.println("ошибка чтения файлов Excel. Проверьте правильность написания названий столбцов, и их очерёдность\n" +
                        "");
                resultProductHashMap.put("Ошибка чтения файла Excel с остатками Ozon", null);
                return resultProductHashMap;
            }
        }

        //считаем кол-во строк в файлах для работы ProgressBar
        int countRowsInWildberies = sheetOzon.getLastRowNum();
        int countRowsIn_1C = sheet_1C.getLastRowNum();
        int countFull = countRowsInWildberies + countRowsIn_1C;
        int i = 1;

        try {
            //считываем информацию с отчёта Ozon
            System.out.println("считываем информацию с отчёта Ozon");
            Iterator rowIterator = sheetOzon.rowIterator();
            while (rowIterator.hasNext()) {

                //получаем строку
                Row row = (Row) rowIterator.next();
                if (row.getRowNum() == 0 || row.getRowNum() == 1 || row.getRowNum() == 2 || row.getRowNum() == 3) {
                    continue;
                }

                if (row.getPhysicalNumberOfCells() == 0) {
                    break;
                }

                //получаем артикул поставщика (code_1C)
                Cell cell = row.getCell(0);
                String code_1C = cell.getRichStringCellValue().getString();

                //получаем артикл Ozon
                cell = row.getCell(1);
                int code = (int) cell.getNumericCellValue();
                if (code == 0) {
                    continue;
                }
                String myVendorCodeOzon = String.valueOf(code);

                //получаем Размер комиссии, %
                cell = row.getCell(6);
                double myCommissionForOzon = cell.getNumericCellValue();

                //получаем Сборка заказа, FBO
                cell = row.getCell(7);
                double myOrderAssemblyForOzon = cell.getNumericCellValue();

                //получаем Магистраль, максимум, FBO
                cell = row.getCell(9);
                double myTrunkForOzon = cell.getNumericCellValue();

                //получаем Последняя миля, FBO
                cell = row.getCell(10);
                double myLastMileForOzon = cell.getNumericCellValue();

                //получаем цену до скидки
                cell = row.getCell(16);
                int myPriceU = 0;
                try {
                    myPriceU = (int) (cell.getNumericCellValue() * 100);
                } catch (Exception ignored) {
                    myPriceU = Integer.parseInt(cell.getRichStringCellValue().getString());
                }

                //получаем текущую цену (со скидкой)
                cell = row.getCell(17);
                int myBasicPrice = (int) (cell.getNumericCellValue() * 100);

                //получаем базоваю скидку
                cell = row.getCell(18);
                int myBasicSale = (int) cell.getNumericCellValue();

                //получаем цену с учётом акции(promo-цену)
                cell = row.getCell(20);
                int myPromoPrice = (int) (cell.getNumericCellValue() * 100);

                //получаем промо-скидку(акционную скидку)
                cell = row.getCell(21);
                int myPromoSale = (int) cell.getNumericCellValue();

                //получаем цену с ozon Premium
                cell = row.getCell(23);
                int myPremiumPrice = (int) (cell.getNumericCellValue() * 100);

                resultProductHashMap.put(myVendorCodeOzon, new ResultProduct(
                        0,
                        "-",
                        "-",
                        "-",
                        "-",
                        1,
                        null,
                        code_1C,
                        "-",
                        myVendorCodeOzon,

                        myCommissionForOzon,
                        myOrderAssemblyForOzon,
                        myTrunkForOzon,
                        myLastMileForOzon,

                        "-",
                        0,
                        myPriceU,
                        myBasicSale,
                        myBasicPrice,
                        myPromoSale,
                        myPromoPrice,
                        myPremiumPrice,
                        0,
                        0,
                        0
                ));
                //увеличиваем ProgressBar
                this.updateProgress(i, countFull);
                countReadsRows = i;
                i++;
            }
            System.out.println("Кол-во строк - " + countReadsRows);
        } catch (Exception e) {
            System.out.println("ошибка при чтении файла Excel с отчётом Ozon. Смотри строку - " + countReadsRows);
            return null;
        }

//считываем информацию с отчёта 1C
        try {
            System.out.println("считываем информацию с базы 1C");

            Iterator rowIterator = sheet_1C.rowIterator();
            countReadsRows_1C = 1;
            while (rowIterator.hasNext()){
                List<String> arrayParams = new ArrayList<>();
                //получаем строку
                Row row = (Row) rowIterator.next();
                if (row.getRowNum() == 0 ){
                    continue;
                }

                //получаем код товара по 1С
                Cell cell = row.getCell(1);
                if (cell.getRichStringCellValue().getString().equals("")) {
                    System.out.println("Пустая ячейка в базе 1С для строки " + row.getRowNum());
                    break;
                }
                String code_1C = cell.getRichStringCellValue().getString();

                //получаем код товара по 1С
                cell = row.getCell(2);
                String brand = cell.getRichStringCellValue().getString().trim();


                //получаем бренд и наименование продукта и сразу пытаемся получить поисковый запрос
                cell = row.getCell(3);
                String myNomenclature = cell.getRichStringCellValue().getString().trim();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////// Анализ номенклатуры и формирование поискового запроса ////////////////////////////////////
                //FM-трансмиттер Borofone, BC16, пластик, цвет: чёрный

                //определяем какой бренд
//                String myBrand = "-";
//                for (String s: Constants.listForBrands){
//                    if (myNomenclature.contains(s)){
//                        myBrand = s;
//                        break;
//                    }
//                }
                //делим брендом myNomenclature на тип продукта и модель продукта с характеристиками
                String[] buff1 = myNomenclature.toLowerCase().split(brand.toLowerCase());
                //определяем тип продукта
                String productType = "-";
                for (String s: Constants.listForCategoryBy_1C){
                    if (buff1[0].startsWith(s.toLowerCase())){
                        productType = s;
                        break;
                    }
                }
                if (productType.equals("-")){
                    productType = "Новая категория ";
                }

                //для некоторых типов продуктов необходимы дополнительные параметры для запроса(кабели, )
                StringBuilder paramsBuilder = new StringBuilder(" ");
                String params = "";
                switch (productType){
                    //для масок кол-во штук в упаковке
                    case Constants.PRODUCT_TYPE_1C_78:
                        int start = myNomenclature.indexOf('(');
                        int stop = myNomenclature.indexOf(')');
                        if (start == -1 || stop == -1){
                            arrayParams.add("одноразовая трехслойная не медицинская");
                        } else {
                            arrayParams.add(myNomenclature.substring(start + 1, stop) + " шт");
                        }
                        break;

                    //для зарядок - с каким кабелем
                    case Constants.PRODUCT_TYPE_1C_10:
                    case Constants.PRODUCT_TYPE_1C_37:
                    //case Constants.PRODUCT_TYPE_1C_38:
                    case Constants.PRODUCT_TYPE_1C_39:
                    case Constants.PRODUCT_TYPE_1C_40:
                    case Constants.PRODUCT_TYPE_1C_132:
                    //case Constants.PRODUCT_TYPE_1C_133:
                    case Constants.PRODUCT_TYPE_1C_153:
                    case Constants.PRODUCT_TYPE_1C_154:
                        if (myNomenclature.contains("с кабелем") || myNomenclature.contains("кабель")){
                            for (String type: Constants.listForCharging){
                                if (myNomenclature.contains(type)){
                                    arrayParams.add(type);
                                    break;
                                }
                            }
                        }
                        params = paramsBuilder.toString().trim();
                        break;

                    //для кабелей - длина
                    case Constants.PRODUCT_TYPE_1C_48:
                    case Constants.PRODUCT_TYPE_1C_49:
                    case Constants.PRODUCT_TYPE_1C_50:
                    case Constants.PRODUCT_TYPE_1C_61:
                    //case Constants.PRODUCT_TYPE_1C_62:
                    case Constants.PRODUCT_TYPE_1C_63:
                    case Constants.PRODUCT_TYPE_1C_64:
                    case Constants.PRODUCT_TYPE_1C_65:
                    case Constants.PRODUCT_TYPE_1C_66:
                    case Constants.PRODUCT_TYPE_1C_67:
                    case Constants.PRODUCT_TYPE_1C_68:
                    case Constants.PRODUCT_TYPE_1C_69:
                    case Constants.PRODUCT_TYPE_1C_70:
                        for (String type : Constants.listForCabel){
                            if (myNomenclature.replaceAll(",", "").contains(type)) {
                                if (type.contains("1.0")){
                                    arrayParams.add("1 м");
                                } else if (type.contains("2.0")){
                                    arrayParams.add("2 м");
                                } else if (type.contains("3.0")){
                                    arrayParams.add("3 м");
                                } else {
                                    arrayParams.add(type);
                                }
                            }
                        }
                        break;

                    //для защитных стекол -  его тип
                    case Constants.PRODUCT_TYPE_1C_139:
                        for (String type : Constants.listForTypeGlass){
                            if (myNomenclature.replaceAll(",", "").contains(type)) {
                                arrayParams.add(type);
                            }
                        }
                        break;
                }

                //если модель сразу за брендом после запятой
                String model ="-";
                if (buff1[1].startsWith(",")){
                    String[] buff2 = buff1[1].trim().split(",", 3);
                    model = buff2[1].trim();
                } else {
                    //если запятой нет
                    String[] buff2 = buff1[1].trim().split(",", 2);
                    model = buff2[0].trim();
                }

                //получаем поисковый запрос
                String querySearch = "-";
                if (!brand.isEmpty() || !model.isEmpty()) {
                    switch (productType){
                        //для маски - маска одноразовая 50 шт
                        case Constants.PRODUCT_TYPE_1C_78:
                            querySearch = productType + " " + model + " " + params;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());
                            System.out.println();
                            break;
                        //для этих типов в поисковом запросе указываем только бренд и модель
                        case Constants.PRODUCT_TYPE_1C_1  :
                        case Constants.PRODUCT_TYPE_1C_2  :
                        case Constants.PRODUCT_TYPE_1C_3  :
                        case Constants.PRODUCT_TYPE_1C_5  :
                        case Constants.PRODUCT_TYPE_1C_6  :
                        case Constants.PRODUCT_TYPE_1C_7  :
                        case Constants.PRODUCT_TYPE_1C_8  :
                        case Constants.PRODUCT_TYPE_1C_9  :

                        case Constants.PRODUCT_TYPE_1C_11 :
                        case Constants.PRODUCT_TYPE_1C_12 :
                        case Constants.PRODUCT_TYPE_1C_13 :
                        case Constants.PRODUCT_TYPE_1C_15 :
                        case Constants.PRODUCT_TYPE_1C_16 :
                        case Constants.PRODUCT_TYPE_1C_17 :
                        case Constants.PRODUCT_TYPE_1C_18 :
                        case Constants.PRODUCT_TYPE_1C_19 :
                        case Constants.PRODUCT_TYPE_1C_20 :
                        case Constants.PRODUCT_TYPE_1C_21 :
                        case Constants.PRODUCT_TYPE_1C_22 :
                        case Constants.PRODUCT_TYPE_1C_23 :
                        case Constants.PRODUCT_TYPE_1C_24 :
                        case Constants.PRODUCT_TYPE_1C_25 :
                        case Constants.PRODUCT_TYPE_1C_26 :
                        case Constants.PRODUCT_TYPE_1C_27 :
                        case Constants.PRODUCT_TYPE_1C_28 :
                        case Constants.PRODUCT_TYPE_1C_29 :
                        case Constants.PRODUCT_TYPE_1C_30 :
                        case Constants.PRODUCT_TYPE_1C_31 :
                        case Constants.PRODUCT_TYPE_1C_32 :
                        case Constants.PRODUCT_TYPE_1C_33 :
                        case Constants.PRODUCT_TYPE_1C_34 :
                        case Constants.PRODUCT_TYPE_1C_35 :
                        case Constants.PRODUCT_TYPE_1C_36 :


                        case Constants.PRODUCT_TYPE_1C_41 :
                        case Constants.PRODUCT_TYPE_1C_42 :
                        case Constants.PRODUCT_TYPE_1C_43 :
                        case Constants.PRODUCT_TYPE_1C_46 :

                        case Constants.PRODUCT_TYPE_1C_51 :
                        case Constants.PRODUCT_TYPE_1C_52 :
                        case Constants.PRODUCT_TYPE_1C_53 :
                        case Constants.PRODUCT_TYPE_1C_54 :
                        case Constants.PRODUCT_TYPE_1C_55 :
                        case Constants.PRODUCT_TYPE_1C_56 :
                        case Constants.PRODUCT_TYPE_1C_57 :
                        case Constants.PRODUCT_TYPE_1C_58 :
                        case Constants.PRODUCT_TYPE_1C_59 :
                        case Constants.PRODUCT_TYPE_1C_60 :

                        case Constants.PRODUCT_TYPE_1C_62 :


                        case Constants.PRODUCT_TYPE_1C_72 :
                        case Constants.PRODUCT_TYPE_1C_73 :
                        case Constants.PRODUCT_TYPE_1C_74 :
                        case Constants.PRODUCT_TYPE_1C_75 :
                        case Constants.PRODUCT_TYPE_1C_76 :
                        case Constants.PRODUCT_TYPE_1C_77 :

                        case Constants.PRODUCT_TYPE_1C_79 :
                        case Constants.PRODUCT_TYPE_1C_83 :
                        case Constants.PRODUCT_TYPE_1C_84 :

                        case Constants.PRODUCT_TYPE_1C_86 :
                        case Constants.PRODUCT_TYPE_1C_87 :
                        case Constants.PRODUCT_TYPE_1C_88 :
                        case Constants.PRODUCT_TYPE_1C_89 :
                        case Constants.PRODUCT_TYPE_1C_90 :
                        case Constants.PRODUCT_TYPE_1C_91 :
                        case Constants.PRODUCT_TYPE_1C_92 :
                        case Constants.PRODUCT_TYPE_1C_93 :
                        case Constants.PRODUCT_TYPE_1C_94 :
                        case Constants.PRODUCT_TYPE_1C_96 :
                        case Constants.PRODUCT_TYPE_1C_97 :
                        case Constants.PRODUCT_TYPE_1C_98 :
                        case Constants.PRODUCT_TYPE_1C_99 :
                        case Constants.PRODUCT_TYPE_1C_100:
                        case Constants.PRODUCT_TYPE_1C_101:
                        case Constants.PRODUCT_TYPE_1C_102:
                        case Constants.PRODUCT_TYPE_1C_103:
                        case Constants.PRODUCT_TYPE_1C_104:
                        case Constants.PRODUCT_TYPE_1C_105:
                        case Constants.PRODUCT_TYPE_1C_106:
                        case Constants.PRODUCT_TYPE_1C_107:
                        case Constants.PRODUCT_TYPE_1C_108:
                        case Constants.PRODUCT_TYPE_1C_109:
                        case Constants.PRODUCT_TYPE_1C_110:
                        case Constants.PRODUCT_TYPE_1C_111:
                        case Constants.PRODUCT_TYPE_1C_112:
                        case Constants.PRODUCT_TYPE_1C_113:
                        case Constants.PRODUCT_TYPE_1C_114:
                        case Constants.PRODUCT_TYPE_1C_115:
                        case Constants.PRODUCT_TYPE_1C_116:
                        case Constants.PRODUCT_TYPE_1C_117:
                        case Constants.PRODUCT_TYPE_1C_118:
                        case Constants.PRODUCT_TYPE_1C_119:
                        case Constants.PRODUCT_TYPE_1C_120:
                        case Constants.PRODUCT_TYPE_1C_121:
                        case Constants.PRODUCT_TYPE_1C_122:
                        case Constants.PRODUCT_TYPE_1C_123:
                        case Constants.PRODUCT_TYPE_1C_124:
                        case Constants.PRODUCT_TYPE_1C_125:
                        case Constants.PRODUCT_TYPE_1C_126:
                        case Constants.PRODUCT_TYPE_1C_127:
                        case Constants.PRODUCT_TYPE_1C_128:
                        case Constants.PRODUCT_TYPE_1C_129:
                        case Constants.PRODUCT_TYPE_1C_130:
                        case Constants.PRODUCT_TYPE_1C_131:

                        case Constants.PRODUCT_TYPE_1C_134:
                        case Constants.PRODUCT_TYPE_1C_135:
                        case Constants.PRODUCT_TYPE_1C_136:
                        case Constants.PRODUCT_TYPE_1C_137:
                        case Constants.PRODUCT_TYPE_1C_138:
                        case Constants.PRODUCT_TYPE_1C_139:
                        case Constants.PRODUCT_TYPE_1C_140:
                        case Constants.PRODUCT_TYPE_1C_142:
                        case Constants.PRODUCT_TYPE_1C_143:
                        case Constants.PRODUCT_TYPE_1C_144:
                        case Constants.PRODUCT_TYPE_1C_145:
                        case Constants.PRODUCT_TYPE_1C_146:
                        case Constants.PRODUCT_TYPE_1C_147:
                        case Constants.PRODUCT_TYPE_1C_148:
                        case Constants.PRODUCT_TYPE_1C_149:
                        case Constants.PRODUCT_TYPE_1C_150:
                        case Constants.PRODUCT_TYPE_1C_151:
                        case Constants.PRODUCT_TYPE_1C_152:

                        case Constants.PRODUCT_TYPE_1C_155:
                        case Constants.PRODUCT_TYPE_1C_156:
                        case Constants.PRODUCT_TYPE_1C_157:
                        case Constants.PRODUCT_TYPE_1C_158:
                        case Constants.PRODUCT_TYPE_1C_159:
                        case Constants.PRODUCT_TYPE_1C_160:
                        case Constants.PRODUCT_TYPE_1C_161:
                        case Constants.PRODUCT_TYPE_1C_162:
                        case Constants.PRODUCT_TYPE_1C_163:
                        case Constants.PRODUCT_TYPE_1C_164:

                            querySearch = brand + " " + model;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
                        //для этих типов в поисковом запросе указываем бренд, модель и некоторыу параметры
                        //case Constants.PRODUCT_TYPE_1C_38:
                        case Constants.PRODUCT_TYPE_1C_10 :
                        case Constants.PRODUCT_TYPE_1C_37 :
                        case Constants.PRODUCT_TYPE_1C_39 :
                        case Constants.PRODUCT_TYPE_1C_40 :
                        case Constants.PRODUCT_TYPE_1C_49:
                        case Constants.PRODUCT_TYPE_1C_50:
                        case Constants.PRODUCT_TYPE_1C_61:
                        case Constants.PRODUCT_TYPE_1C_67 :
                        case Constants.PRODUCT_TYPE_1C_132:
                        case Constants.PRODUCT_TYPE_1C_153:
                        case Constants.PRODUCT_TYPE_1C_154:
                            querySearch = brand + " " + model + " " + params;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;

                        //для этих кабелей в поисковом запросе указываем бренд, модель тип коннектора и длину
                        case Constants.PRODUCT_TYPE_1C_63:
                        case Constants.PRODUCT_TYPE_1C_64:
                        case Constants.PRODUCT_TYPE_1C_65:
                        case Constants.PRODUCT_TYPE_1C_66:
                            String connect = "-";
                            if (productType.equals(Constants.PRODUCT_TYPE_1C_63) || productType.equals(Constants.PRODUCT_TYPE_1C_64)){
                                connect = "apple";
                            }
                            if (productType.equals(Constants.PRODUCT_TYPE_1C_65)){
                                connect = "type-c";
                            }
                            if (productType.equals(Constants.PRODUCT_TYPE_1C_66)){
                                connect = "micro USB";
                            }
                            querySearch = brand + " " + model + " " + connect + " " + params;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
//                        case Constants.PRODUCT_TYPE_1C_48:
//                            querySearch = myBrand + " " + model + " 3 в 1 " + params;
//                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
//                            System.out.println("querySearch = " + querySearch);
//                            System.out.println();
//                            break;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        //для этих типов в поисковом запросе тип немного видоизменяем
                        case Constants.PRODUCT_TYPE_1C_68:
                            //productType = "Кабель USB 2 в 1";
                            querySearch = brand + " " + model + " 2 в 1";
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
                        case Constants.PRODUCT_TYPE_1C_48:
                        case Constants.PRODUCT_TYPE_1C_69:
                        case Constants.PRODUCT_TYPE_1C_85:
                            //productType = "Кабель USB 3 в 1";
                            querySearch = brand + " " + model + " 3 в 1 " + params;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
                        case Constants.PRODUCT_TYPE_1C_70:
                            //productType = "Кабель USB 4 в 1";
                            querySearch = brand + " " + model + " 4 в 1";
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
                        case "Новая категория":
                            querySearch = "-";
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature + " - новая категория");
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;

                        default:
                            querySearch = productType + " " + brand + " " + model + " " + params;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                    }
                }
                if (mapCountForMyProductName.size() != 0){
                    if (!mapCountForMyProductName.containsKey(brand + " " + model)){
                        mapCountForMyProductName.put(brand + " " + model, 1);
                    } else {
                        int count = mapCountForMyProductName.get(brand + " " + model);
                        count++;
                        mapCountForMyProductName.put(brand + " " + model, count);
                    }
                } else {
                    mapCountForMyProductName.put(brand + " " + model, 1);
                }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                //получаем спец-цену
                int specPrice_1C = 0;
                try {
                    cell = row.getCell(5);
                    specPrice_1C = (int) cell.getNumericCellValue() * 100;
                } catch (Exception ignored) {
                    //e.printStackTrace();
                }

                String specQuery = "-";
                try {
                    cell = row.getCell(8);
                    specQuery = cell.getRichStringCellValue().getString();
                    System.out.println("для кода 1С = " + code_1C + " query заменяется на спец QUERY = " + specQuery);
                    querySearch = specQuery;
                } catch (Exception ignored) {
                    System.out.println();
                }

                supplierSpecPriceHashMapWithKeyCode_1C.put(code_1C, new Supplier(code_1C, brand, productType, myNomenclature, model, arrayParams,querySearch, specPrice_1C));
                //увеличиваем ProgressBar
                this.updateProgress(i, countFull);
                countReadsRows_1C++;
                i++;
            }
            System.out.println("Кол-во строк в базе 1С - " + countReadsRows_1C);
        } catch (Exception e) {
            System.out.println("ошибка при чтении файла Excel с базой 1C. Смотри строку - " + countReadsRows_1C);
            return null;
        }


//пытаемся привязать specPrice_1C и productName к ResultProduct
        for (Map.Entry<String, ResultProduct> entry : resultProductHashMap.entrySet()) {
            String key = entry.getKey();
            String code_1C = entry.getValue().getCode_1C();
            Supplier supplier1 = supplierSpecPriceHashMapWithKeyCode_1C.get(code_1C);
            if (supplier1 != null){
                entry.getValue().setIsFind(1);
                entry.getValue().setSpecPrice(supplier1.getSpecPrice());
                entry.getValue().setMyBrand(supplier1.getMyBrand());
                entry.getValue().setProductType(supplier1.getProductType());
                entry.getValue().setMyNomenclature_1C(supplier1.getNomenclature());
                entry.getValue().setMyProductModel(supplier1.getMyProductModel());
                entry.getValue().setArrayListParams((ArrayList<String>) supplier1.getArrayListParams());
                entry.getValue().setQuerySearchForWildberiesOrOzon(supplier1.getQuerySearch());
                String brand = entry.getValue().getMyBrand();
                String productModel = entry.getValue().getMyProductModel();
                int countProducts = mapCountForMyProductName.get(brand + " " + productModel);
                entry.getValue().setCountMyProductModel(countProducts);
                System.out.println("Для " + brand + " " + productModel + " кол-во совпадений - " + countProducts);
            } else {
                entry.getValue().setSpecPrice(0);
            }
        }
        System.out.println("Добавили в итоговый мап все ResultProducts");
        System.out.println();
        return resultProductHashMap;
    }

    private boolean checkFileOzon(Sheet sheet){
        Row headRow = sheet.getRow(2);
        try {
            boolean checkVendorCode_1C = headRow.getCell(0).getRichStringCellValue().getString().equals(Constants.VENDOR_CODE_1C_IN_FILE_OZON);
            boolean checkVendorCodeOzon = headRow.getCell(1).getRichStringCellValue().getString().equals(Constants.VENDOR_CODE_IN_FILE_OZON);
            boolean checkPriceU = headRow.getCell(16).getRichStringCellValue().getString().equals(Constants.PRICE_U_IN_FILE_OZON);
            boolean checkBasicPrice = headRow.getCell(17).getRichStringCellValue().getString().equals(Constants.BASIC_PRICE_IN_FILE_OZON);
            boolean checkPromoPrice = headRow.getCell(20).getRichStringCellValue().getString().equals(Constants.PROMO_PRICE_IN_FILE_OZON);
            boolean checkPremiumPrice = headRow.getCell(23).getRichStringCellValue().getString().equals(Constants.PREMIUM_PRICE_IN_FILE_OZON);
            return checkVendorCode_1C & checkVendorCodeOzon & checkPriceU & checkBasicPrice & checkPromoPrice & checkPremiumPrice;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkFile_1C(Sheet sheet){
        Row headRow = sheet.getRow(0);
        try {
            boolean checkCode_1C = headRow.getCell(1).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.CODE_1C);
            boolean checkProductName = headRow.getCell(3).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.NOMENCLATURE_1C);
            boolean checkSpecPrice = headRow.getCell(5).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.SPEC_PRICE_1C);
            return checkCode_1C & checkProductName & checkProductName & checkSpecPrice;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected Map call() throws Exception {
        return this.readWorkbook(files);
    }
}
