import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.*;

public class TaskReadExcelForOzon extends Task<Map> {

    private int countReadsRows;
    private int countReadsRows_1C;

    public TaskReadExcelForOzon(List<File> files) {
        this.files = files;
    }

    private final List<File> files;

    public Map<String, ResultProduct> readWorkbook(List<File> files) {
        try {
            Map<String, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
            Map<String, Supplier> supplierSpecPriceHashMapWithKeyCode_1C = new HashMap<>();

            //читаем файл отчёта Ozon и файл 1С
            Workbook workbookOzon = new XSSFWorkbook(files.get(0));
            Workbook workbook_1C = new XSSFWorkbook(files.get(1));

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
            if (!checkFileOzon(sheetOzon) || !checkFile_1C(sheet_1C)){
                Sheet sheetBuff = sheet_1C;
                sheet_1C = sheetOzon;
                sheetOzon = sheetBuff;
                if (!checkFileOzon(sheetOzon) || !checkFile_1C(sheet_1C)){
                    System.out.println("ошибка чтения файлов Excel. Проверьте правильность написания названий столбцов, и их очерёдность\n" +
                            "");
                    resultProductHashMap.put("Ошибка чтения файло Excel", null);
                    return resultProductHashMap;
                }
            }

            //считаем кол-во строк в файлах для работы ProgressBar
            int countRowsInWildberies = sheetOzon.getLastRowNum();
            int countRowsIn_1C = sheet_1C.getLastRowNum();
            int countFull = countRowsInWildberies + countRowsIn_1C;
            int i = 1;

            //считываем информацию с отчёта Ozon
            System.out.println("считываем информацию с отчёта Ozon");
            Iterator rowIterator = sheetOzon.rowIterator();
            while (rowIterator.hasNext()) {

                //получаем строку
                Row row = (Row) rowIterator.next();
                if (row.getRowNum() == 0 || row.getRowNum() == 1 || row.getRowNum() == 2 || row.getRowNum() == 3){
                    continue;
                }

                if (row.getPhysicalNumberOfCells() == 0){
                    break;
                }

                //получаем артикул поставщика (code_1C)
                Cell cell = row.getCell(0);
                String code_1C = cell.getRichStringCellValue().getString();

                //получаем артикл Ozon
                cell = row.getCell(1);
                int code = (int) cell.getNumericCellValue();
                if (code == 0){
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
                int myPriceU = (int) (cell.getNumericCellValue() * 100);

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

//считываем информацию с отчёта 1C
            System.out.println("считываем информацию с отчёта 1C");
            rowIterator = sheet_1C.rowIterator();
            countReadsRows_1C = 1;
            while (rowIterator.hasNext()){
                //получаем строку
                Row row = (Row) rowIterator.next();
                if (row.getRowNum() == 0 ){
                    continue;
                }

                //получаем код товара по 1С
                Cell cell = row.getCell(1);
                if (cell == null){
                    continue;
                }
                String code_1C = cell.getRichStringCellValue().getString();

                //получаем бренд и наименование продукта и сразу пытаемся получить поисковый запрос
                cell = row.getCell(2);
                String myNomenclature = cell.getRichStringCellValue().getString();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////// Анализ номенклатуры и формирование поискового запроса ////////////////////////////////////
                //FM-трансмиттер Borofone, BC16, пластик, цвет: чёрный

                //определяем какой бренд
                String myBrand = "-";
                for (String s: Constants.listForBrands){
                    if (myNomenclature.contains(s)){
                        myBrand = s;
                        break;
                    }
                }
                //делим брендом myNomenclature на тип продукта и модель продукта с характеристиками
                String[] buff1 = myNomenclature.split(myBrand);
                //определяем тип продукта
                String productType = "-";
                for (String s: Constants.listForCategoryBy_1C){
                    if (buff1[0].startsWith(s)){
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
                            params = "одноразовая трехслойная не медицинская";
                        } else {
                            params = myNomenclature.substring(start + 1, stop) + " шт";
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
                                    paramsBuilder.append("c кабелем ").append(type);
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
                                    paramsBuilder.append("1 м").append(" ");
                                } else if (type.contains("2.0")){
                                    paramsBuilder.append("2 м").append(" ");
                                } else if (type.contains("3.0")){
                                    paramsBuilder.append("3 м").append(" ");
                                } else {
                                    paramsBuilder.append(type).append(" ");
                                }
                            }
                        }
                        params = paramsBuilder.toString().trim();
                        break;

                    //для защитных стекол -  его тип
                    case Constants.PRODUCT_TYPE_1C_139:
                        for (String type : Constants.listForTypeGlass){
                            if (myNomenclature.replaceAll(",", "").contains(type)) {
                                paramsBuilder.append(type).append(" ");
                            }
                        }
                        params = paramsBuilder.toString().trim();
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
                    model = buff2[0];
                }

                //получаем поисковый запрос
                String querySearch = "-";
                if (!myBrand.isEmpty() || !model.isEmpty()) {
                    switch (productType){
                        //для маски - маска одноразовая 50 шт
                        case Constants.PRODUCT_TYPE_1C_78:
                            querySearch = productType + " " + model + " " + params;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
                        //для этих типов в поисковом запросе указываем только бренд и модель
                        case Constants.PRODUCT_TYPE_1C_1://
                        case Constants.PRODUCT_TYPE_1C_2:
                        case Constants.PRODUCT_TYPE_1C_5:
                        case Constants.PRODUCT_TYPE_1C_9:
                        case Constants.PRODUCT_TYPE_1C_12:
                        case Constants.PRODUCT_TYPE_1C_13:
                        case Constants.PRODUCT_TYPE_1C_16:
                        case Constants.PRODUCT_TYPE_1C_17:
                        case Constants.PRODUCT_TYPE_1C_24:
                        case Constants.PRODUCT_TYPE_1C_26:
                        case Constants.PRODUCT_TYPE_1C_29:
                        case Constants.PRODUCT_TYPE_1C_28:
                        case Constants.PRODUCT_TYPE_1C_30:
                        case Constants.PRODUCT_TYPE_1C_31:
                        case Constants.PRODUCT_TYPE_1C_35:
                        case Constants.PRODUCT_TYPE_1C_37:
                        case Constants.PRODUCT_TYPE_1C_39:
                        case Constants.PRODUCT_TYPE_1C_40://
                        case Constants.PRODUCT_TYPE_1C_41:
                        case Constants.PRODUCT_TYPE_1C_42:
                        case Constants.PRODUCT_TYPE_1C_43:
                        case Constants.PRODUCT_TYPE_1C_46:
                        case Constants.PRODUCT_TYPE_1C_51:
                        case Constants.PRODUCT_TYPE_1C_52:
                        case Constants.PRODUCT_TYPE_1C_53:
                        case Constants.PRODUCT_TYPE_1C_54:
                        case Constants.PRODUCT_TYPE_1C_55:
                        case Constants.PRODUCT_TYPE_1C_56:
                        case Constants.PRODUCT_TYPE_1C_57:
                        case Constants.PRODUCT_TYPE_1C_58:
                        case Constants.PRODUCT_TYPE_1C_59:
                        case Constants.PRODUCT_TYPE_1C_60:
                        case Constants.PRODUCT_TYPE_1C_62:
                        case Constants.PRODUCT_TYPE_1C_67:
                        case Constants.PRODUCT_TYPE_1C_72:
                        case Constants.PRODUCT_TYPE_1C_73:
                        case Constants.PRODUCT_TYPE_1C_74:
                        case Constants.PRODUCT_TYPE_1C_75:
                        case Constants.PRODUCT_TYPE_1C_79:
                        case Constants.PRODUCT_TYPE_1C_83:
                        case Constants.PRODUCT_TYPE_1C_84:
                        case Constants.PRODUCT_TYPE_1C_86:
                        case Constants.PRODUCT_TYPE_1C_113:
                        case Constants.PRODUCT_TYPE_1C_114:
                        case Constants.PRODUCT_TYPE_1C_115:
                        case Constants.PRODUCT_TYPE_1C_116:

                            querySearch = myBrand + " " + model;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
                        //для этих типов в поисковом запросе указываем бренд, модель и некоторыу параметры
                        //case Constants.PRODUCT_TYPE_1C_38:
                        case Constants.PRODUCT_TYPE_1C_49:
                        case Constants.PRODUCT_TYPE_1C_50:
                        case Constants.PRODUCT_TYPE_1C_61:
                            querySearch = myBrand + " " + model + " " + params;
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
                            querySearch = myBrand + " " + model + " " + connect + " " + params;
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
                            querySearch = myBrand + " " + model + " 2 в 1";
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
                        case Constants.PRODUCT_TYPE_1C_48:
                        case Constants.PRODUCT_TYPE_1C_69:
                        case Constants.PRODUCT_TYPE_1C_85:
                            //productType = "Кабель USB 3 в 1";
                            querySearch = myBrand + " " + model + " 3 в 1 " + params;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                            break;
                        case Constants.PRODUCT_TYPE_1C_70:
                            //productType = "Кабель USB 4 в 1";
                            querySearch = myBrand + " " + model + " 4 в 1";
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        default:
                            querySearch = productType + " " + myBrand + " " + model + " " + params;
                            System.out.println(countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("querySearch = " + querySearch);
                            System.out.println();
                    }
                }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                //получаем спец-цену
                int specPrice_1C = 0;
                try {
                    cell = row.getCell(4);
                    specPrice_1C = (int) cell.getNumericCellValue() * 100;
                } catch (Exception ignored) {
                    //e.printStackTrace();
                }

                supplierSpecPriceHashMapWithKeyCode_1C.put(code_1C, new Supplier(code_1C, myBrand, productType, myNomenclature, querySearch, specPrice_1C));
                //увеличиваем ProgressBar
                this.updateProgress(i, countFull);
                countReadsRows_1C++;
                i++;
            }
            System.out.println("Кол-во строк - " + countReadsRows);

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
                    entry.getValue().setQuerySearchForWildberiesOrOzon(supplier1.getQuerySearch());
                } else entry.getValue().setSpecPrice(0);
            }
            return resultProductHashMap;
        }
        catch (Exception e) {
            System.out.println("ошибка при чтении файла Excel. Смотри строку - " + countReadsRows);
            System.out.println("ошибка при чтении файла Excel. Смотри строку - " + countReadsRows_1C);
            return null;
        }
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
            boolean checkProductName = headRow.getCell(2).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.NOMENCLATURE_1C);
            boolean checkSpecPrice = headRow.getCell(4).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.SPEC_PRICE_1C);
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
