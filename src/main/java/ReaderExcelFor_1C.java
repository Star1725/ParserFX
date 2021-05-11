import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReaderExcelFor_1C {

    UnifierDataFromExcelFiles unifierDataFromExcelFiles;

    public ReaderExcelFor_1C(UnifierDataFromExcelFiles unifierDataFromExcelFiles){
        this.unifierDataFromExcelFiles = unifierDataFromExcelFiles;
    }

    void getDataFromBase_1C(Sheet sheet_1C){
        int i = 1;
        try {
            System.out.println("считываем информацию с базы 1C");

            Iterator rowIterator = sheet_1C.rowIterator();
            TaskReadExcelForOzon.countReadsRows_1C = 1;
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

                //получаем brand
                cell = row.getCell(2);
                String brand = cell.getRichStringCellValue().getString().toLowerCase().trim();

                //получаем бренд и наименование продукта и сразу пытаемся получить поисковый запрос
                cell = row.getCell(3);
                String myNomenclature = cell.getRichStringCellValue().getString().toLowerCase().trim();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////// Анализ номенклатуры и формирование поискового запроса ////////////////////////////////////
                //FM-трансмиттер Borofone, BC16, пластик, цвет: чёрный

                //делим брендом myNomenclature на тип продукта и модель продукта с характеристиками
                String[] buff1 = myNomenclature.toLowerCase().split(brand.toLowerCase());
                //определяем тип продукта
                String productType = "-";
                for (String s: Constants.listForCategoryBy_1C){
                    if (buff1[0]
                            .toLowerCase()
                            .trim()
                            .startsWith(s.toLowerCase())){
                        productType = s;
                        break;
                    }
                }
                if (productType.equals("-")){
                    productType = "Новая категория";
                }

                //для некоторых типов продуктов необходимы дополнительные параметры при поиске аналогов в каталоге запроса(кабели, блоки питания зарядные устройства и т.п.)
                //StringBuilder paramsBuilder = new StringBuilder(" ");
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
                            String[] buffParams = myNomenclature.split("кабелем");
                            if (buffParams.length == 1){
                                buffParams = myNomenclature.split("кабель");
                            }
                            for (String type: Constants.listForCharging){
                                if (buffParams[1].toLowerCase().contains(type)){
                                    if (arrayParams.size() == 0){
                                        arrayParams.add(type);
                                    } else {
                                        for (int q = 0; q < arrayParams.size(); q++){
                                            if (!arrayParams.get(q).contains(type)){
                                                arrayParams.add(type);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;

                    //для кабелей - длина
                    //case Constants.PRODUCT_TYPE_1C_48:
//                    case Constants.PRODUCT_TYPE_1C_49:
//                    case Constants.PRODUCT_TYPE_1C_50:
//                    case Constants.PRODUCT_TYPE_1C_61:
//                        //case Constants.PRODUCT_TYPE_1C_62:
//                    case Constants.PRODUCT_TYPE_1C_63:
//                    case Constants.PRODUCT_TYPE_1C_64:
//                    case Constants.PRODUCT_TYPE_1C_65:
//                    case Constants.PRODUCT_TYPE_1C_66:
//                    case Constants.PRODUCT_TYPE_1C_166:
//                    case Constants.PRODUCT_TYPE_1C_67:
////                    case Constants.PRODUCT_TYPE_1C_68:
////                    case Constants.PRODUCT_TYPE_1C_69:
////                    case Constants.PRODUCT_TYPE_1C_70:
//                        addedParamForCableLenght(arrayParams, myNomenclature);
//                        break;

                    //для защитных стекол -  его тип
                    case Constants.PRODUCT_TYPE_1C_139:
                        for (String type : Constants.listForTypeGlass){
                            if (myNomenclature.replaceAll(",", "").toLowerCase().contains(type.toLowerCase())) {
                                arrayParams.add(type.toLowerCase());
                            }
                        }
                        break;
                }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                if (!brand.isEmpty() || !model.isEmpty()) {
                    switch (productType) {
                        //для маски - маска одноразовая 50 шт
                        case Constants.PRODUCT_TYPE_1C_78:
                            querySearch = productType + " " + model + " " + arrayParams.get(0);
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());

                            System.out.println("querySearch = " + querySearch);

                            break;
                        //для этих типов в поисковом запросе указываем только бренд и модель
                        case Constants.PRODUCT_TYPE_1C_1:
                        case Constants.PRODUCT_TYPE_1C_2:
                        case Constants.PRODUCT_TYPE_1C_3:
                        case Constants.PRODUCT_TYPE_1C_5:
                        case Constants.PRODUCT_TYPE_1C_6:
                        case Constants.PRODUCT_TYPE_1C_7:
                        case Constants.PRODUCT_TYPE_1C_8:
                        case Constants.PRODUCT_TYPE_1C_9:
                        case Constants.PRODUCT_TYPE_1C_10:
                        case Constants.PRODUCT_TYPE_1C_11:
                        case Constants.PRODUCT_TYPE_1C_12:
                        case Constants.PRODUCT_TYPE_1C_13:
                        case Constants.PRODUCT_TYPE_1C_15:
                        case Constants.PRODUCT_TYPE_1C_16:
                        case Constants.PRODUCT_TYPE_1C_17:
                        case Constants.PRODUCT_TYPE_1C_18:
                        case Constants.PRODUCT_TYPE_1C_19:
                        case Constants.PRODUCT_TYPE_1C_20:
                        case Constants.PRODUCT_TYPE_1C_21:
                        case Constants.PRODUCT_TYPE_1C_22:
                        case Constants.PRODUCT_TYPE_1C_23:
                        case Constants.PRODUCT_TYPE_1C_24:
                        case Constants.PRODUCT_TYPE_1C_25:
                        case Constants.PRODUCT_TYPE_1C_26:
                        case Constants.PRODUCT_TYPE_1C_27:
                        case Constants.PRODUCT_TYPE_1C_28:
                        case Constants.PRODUCT_TYPE_1C_29:
                        case Constants.PRODUCT_TYPE_1C_30:
                        case Constants.PRODUCT_TYPE_1C_31:
                        case Constants.PRODUCT_TYPE_1C_32:
                        case Constants.PRODUCT_TYPE_1C_33:
                        case Constants.PRODUCT_TYPE_1C_34:
                        case Constants.PRODUCT_TYPE_1C_35:
                        case Constants.PRODUCT_TYPE_1C_36:
                        case Constants.PRODUCT_TYPE_1C_37:

                            //case Constants.PRODUCT_TYPE_1C_39:

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

                        case Constants.PRODUCT_TYPE_1C_72:
                        case Constants.PRODUCT_TYPE_1C_73:
                        case Constants.PRODUCT_TYPE_1C_74:
                        case Constants.PRODUCT_TYPE_1C_75:
                        case Constants.PRODUCT_TYPE_1C_76:
                        case Constants.PRODUCT_TYPE_1C_77:
                        case Constants.PRODUCT_TYPE_1C_79:
                        case Constants.PRODUCT_TYPE_1C_83:
                        case Constants.PRODUCT_TYPE_1C_84:
                        case Constants.PRODUCT_TYPE_1C_86:
                        case Constants.PRODUCT_TYPE_1C_87:
                        case Constants.PRODUCT_TYPE_1C_88:
                        case Constants.PRODUCT_TYPE_1C_89:
                        case Constants.PRODUCT_TYPE_1C_90:
                        case Constants.PRODUCT_TYPE_1C_91:
//                        case Constants.PRODUCT_TYPE_1C_92:

                        case Constants.PRODUCT_TYPE_1C_94:
                        case Constants.PRODUCT_TYPE_1C_96:
                        case Constants.PRODUCT_TYPE_1C_97:
                        case Constants.PRODUCT_TYPE_1C_98:
                        case Constants.PRODUCT_TYPE_1C_99:
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
//                        case Constants.PRODUCT_TYPE_1C_136:
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
                        case Constants.PRODUCT_TYPE_1C_153:
                        case Constants.PRODUCT_TYPE_1C_154:
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
                        case Constants.PRODUCT_TYPE_1C_39:
                        case Constants.PRODUCT_TYPE_1C_40:

                        case Constants.PRODUCT_TYPE_1C_132:

                            querySearch = brand + " " + model;
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());

                            System.out.println("querySearch = " + querySearch);

                            break;

                        case Constants.PRODUCT_TYPE_1C_93:
                        case Constants.PRODUCT_TYPE_1C_136:
                        case Constants.PRODUCT_TYPE_1C_167:
                        case Constants.PRODUCT_TYPE_1C_92:
                            String series = "-";
                            Constants.addedParamForSeriesCover(arrayParams, myNomenclature);
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());

                            break;

                        //для этих кабелей в поисковом запросе указываем бренд, модель тип коннектора и длину
                        case Constants.PRODUCT_TYPE_1C_49:
                            String connect = "-";
                            connect = "type-c - type-c";
                            arrayParams.add(connect);
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());
                            break;
                        case Constants.PRODUCT_TYPE_1C_50:
                        case Constants.PRODUCT_TYPE_1C_62:
                            connect = "type-c - apple";
                            arrayParams.add(connect);
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());
                            break;
                        case Constants.PRODUCT_TYPE_1C_61:
                        case Constants.PRODUCT_TYPE_1C_63:
                        case Constants.PRODUCT_TYPE_1C_64:
                        case Constants.PRODUCT_TYPE_1C_65:
                        case Constants.PRODUCT_TYPE_1C_66:
                        case Constants.PRODUCT_TYPE_1C_67:
                        case Constants.PRODUCT_TYPE_1C_166:

                            if (productType.equals(Constants.PRODUCT_TYPE_1C_63)
                                    || productType.equals(Constants.PRODUCT_TYPE_1C_64)
                                    ||  productType.equals(Constants.PRODUCT_TYPE_1C_62)
                                    ||  productType.equals(Constants.PRODUCT_TYPE_1C_50)) {
                                connect = "apple";
                                arrayParams.add(connect);
                            }
                            if (productType.equals(Constants.PRODUCT_TYPE_1C_65) || productType.equals(Constants.PRODUCT_TYPE_1C_49)) {
                                connect = "type-c";
                                arrayParams.add(connect);
                            }
                            if (productType.equals(Constants.PRODUCT_TYPE_1C_66) || productType.equals(Constants.PRODUCT_TYPE_1C_166)) {
                                connect = "micro";
                                arrayParams.add(connect);
                            }
                            Constants.addedParamForCableLenght(arrayParams, myNomenclature);
                            //querySearch = brand + " " + model + " " + connect;
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());

                            //System.out.println("querySearch = " + querySearch);

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
                            arrayParams.add("2 в 1");
                            querySearch = brand + " " + model + " 2 в 1";
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());

                            System.out.println("querySearch = " + querySearch);

                            break;
                        case Constants.PRODUCT_TYPE_1C_48:
                        case Constants.PRODUCT_TYPE_1C_69:
                        case Constants.PRODUCT_TYPE_1C_85:
                            //productType = "Кабель USB 3 в 1";
                            arrayParams.add("3 в 1");
                            querySearch = brand + " " + model + " 3 в 1";
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());

                            System.out.println("querySearch = " + querySearch);

                            break;
                        case Constants.PRODUCT_TYPE_1C_70:
                            //productType = "Кабель USB 4 в 1";
                            arrayParams.add("4 в 1");
                            querySearch = brand + " " + model + " 4 в 1";
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());

                            System.out.println("querySearch = " + querySearch);

                            break;
                        case "Новая категория":
                            querySearch = "-";
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature + " - новая категория");

                            System.out.println("querySearch = " + querySearch);

                            break;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        default:
                            querySearch = brand + " " + model;
                            System.out.println(UnifierDataFromExcelFiles.countReadsRows_1C + " - myNomenclature = " + myNomenclature);
                            System.out.println("productType = " + productType);
                            System.out.println("model = " + model);
                            System.out.println("arrayParams = " + arrayParams.toString());

                            System.out.println("querySearch = " + querySearch);

                    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }
                if (UnifierDataFromExcelFiles.mapCountForMyProductName.size() != 0){
                    if (!UnifierDataFromExcelFiles.mapCountForMyProductName.containsKey(brand + " " + model)){
                        UnifierDataFromExcelFiles.mapCountForMyProductName.put(brand + " " + model, 1);
                    } else {
                        int count = UnifierDataFromExcelFiles.mapCountForMyProductName.get(brand + " " + model);
                        count++;
                        UnifierDataFromExcelFiles.mapCountForMyProductName.put(brand + " " + model, count);
                    }
                } else {
                    UnifierDataFromExcelFiles.mapCountForMyProductName.put(brand + " " + model, 1);
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
                    System.out.println("для кода 1С = " + code_1C + " запрос по-умолчанию заменяется на спец QUERY = " + specQuery);
                    System.out.println();
                } catch (Exception ignored) {
                    System.out.println();
                }

                UnifierDataFromExcelFiles.supplierSpecPriceHashMapWithKeyCode_1C.put(code_1C, new Supplier(code_1C, brand, productType, myNomenclature, model, arrayParams,  specQuery, specPrice_1C));
                //увеличиваем ProgressBar
                //unifierDataFromExcelFiles.updateProgress(i, UnifierDataFromExcelFiles.countFull);
                UnifierDataFromExcelFiles.countReadsRows_1C++;
                i++;
            }
            System.out.println("Кол-во строк в базе 1С - " + UnifierDataFromExcelFiles.countReadsRows_1C);
        } catch (Exception e) {
            System.out.println("ошибка при чтении файла Excel с базой 1C. Смотри строку - " + UnifierDataFromExcelFiles.countReadsRows_1C);
        }
    }

    boolean checkFile_1C(Sheet sheet){
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
}
