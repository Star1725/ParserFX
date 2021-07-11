import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;
import java.util.logging.Logger;

public class ReaderExcelFor_1C_ver_2 {

    private static final Logger loggerReaderExcelFor_1C = Logger.getLogger(ReaderExcelFor_1C_ver_2.class.getName());

    static {
        loggerReaderExcelFor_1C.addHandler(Main.fileHandler);
    }

    UnifierDataFromExcelFiles unifierDataFromExcelFiles;

    public ReaderExcelFor_1C_ver_2(UnifierDataFromExcelFiles unifierDataFromExcelFiles){
        this.unifierDataFromExcelFiles = unifierDataFromExcelFiles;
    }

    void getDataFromBase_1C(Sheet sheet_1C, int marketPlaceFlag){
        int i = 0;
        try {
//            System.out.println("считываем информацию с базы 1C");
            loggerReaderExcelFor_1C.info("считываем информацию с базы 1C");
            Iterator rowIterator = sheet_1C.rowIterator();
            TaskReadExcelForOzon.countReadsRows_1C = 1;
            while (rowIterator.hasNext()){
                List<List<String>> arrayParamsForSearch = new ArrayList<>();
                //получаем строку
                Row row = (Row) rowIterator.next();
                if (row.getRowNum() == 0 ){
                    i++;
                    continue;
                }

                //получаем код товара по 1С
                Cell cell = row.getCell(1);
                if (cell.getRichStringCellValue().getString().equals("")) {
//                    System.out.println("Пустая ячейка в базе 1С для строки " + row.getRowNum());
                    loggerReaderExcelFor_1C.info("Пустая ячейка в базе 1С для строки " + row.getRowNum());
                    break;
                }
                String code_1C = cell.getRichStringCellValue().getString();

                //получаем brand
                cell = row.getCell(2);
                String brand = cell.getRichStringCellValue().getString().toLowerCase().trim();

                if (brand.toLowerCase().equals("xivi")){
                    loggerReaderExcelFor_1C.info("Xivi - пропускаем запрос");
                    continue;
                } else if (brand.toLowerCase().equals("mietubl")){
                    loggerReaderExcelFor_1C.info("Mietubl - пропускаем запрос");
                    continue;
                }

                //получаем комиссию
                double commission = 0;
                cell = row.getCell(7);
                try {
                    commission = cell.getNumericCellValue();
                } catch (Exception ignored) {
                }

                //получаем доставку
                cell = row.getCell(8);
                int delivery = 0;
                try {
                    delivery = (int) cell.getNumericCellValue();
                } catch (Exception ignored) {
                }

                //получаем наименование продукта
                cell = row.getCell(3);
                String myNomenclature = cell.getRichStringCellValue().getString().toLowerCase().trim();
                //делим брендом myNomenclature на тип продукта и модель продукта с характеристиками
                String[] buff1 = myNomenclature.toLowerCase().split(brand.toLowerCase());
                //если модель сразу за брендом после запятой
                String model = null;
                try {
                    model = "-";
                    if (buff1[1].startsWith(",")){
                        String[] buff2 = buff1[1].trim().split(",", 3);
                        model = buff2[1].trim();
                    } else {
                        //если запятой нет
                        String[] buff2 = buff1[1].trim().split(",", 2);
                        model = buff2[0];
                    }
                } catch (Exception ignored) {
//                    System.out.println(Constants.getRedString("Ошибка поиска наименования модели(не нашёл имя бренда)"));
                    loggerReaderExcelFor_1C.info(Constants.getRedString("Ошибка поиска наименования модели(не нашёл имя бренда)"));
                }

                //получаем дополнительный параметр поиска
                cell = row.getCell(4);
                String additionalParameter = cell.getRichStringCellValue().getString().toLowerCase().trim();

                //анализируем дополнительный параметр и номенклатуру на дополнительные характеристики поиска аналогов
                List<String> arrayListAdditionalParams = new ArrayList<>(Arrays.asList(additionalParameter.split(", ")));
//получение допонительных параметров поиска!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                for (String param: arrayListAdditionalParams){
                    arrayParamsForSearch.add(Constants.getCollectionsParamsForProduct(param, code_1C));
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
                    cell = row.getCell(6);
                    specPrice_1C = (int) cell.getNumericCellValue() * 100;
                } catch (Exception ignored) {
                    //e.printStackTrace();
                }

//                //Вся информация о прочитанной позиции
//                loggerReaderExcelFor_1C.info("прочитана " + i + " строка файла \"Спец код\":\n" +
//                        " - бренд = " + brand + "\n" +
//                        " - модель = " + model + "\n" +
//                        " - номенклатура = " + myNomenclature + "\n" +
//                        " - дополнительные параметры поиска:\n");
//                for (List<String> arrayParam: arrayParamsForSearch){
//                    loggerReaderExcelFor_1C.info(arrayParam.toString() + "\n");
//                }

                String specQuery = "-";
                try {
                    if (marketPlaceFlag == 1){
                        cell = row.getCell(9);
                    } else if (marketPlaceFlag == 2){
                        cell = row.getCell(11);
                    }
                    if (cell != null){
                        specQuery = cell.getRichStringCellValue().getString();
                        //                    loggerReaderExcelFor_1C.info("для кода 1С = " + code_1C + " запрос по-умолчанию заменяется на спец QUERY = " + specQuery);
//                    loggerReaderExcelFor_1C.info("\n\r");
                    }
                } catch (Exception ignored) {
                }

                //получаем поисковый запрос
                String querySearch = "-";
                if (specQuery.contains("-")){
                    querySearch = String.format("%s %s", brand, model);
                } else {
                    querySearch = specQuery;
                }

                if (marketPlaceFlag == 1){
                    UnifierDataFromExcelFiles.supplierSpecPriceHashMapWithKeyCode_1C.put(code_1C, new Supplier(code_1C, brand, myNomenclature, model, arrayParamsForSearch,  specQuery, specPrice_1C));
                } else if (marketPlaceFlag == 2){
                    UnifierDataFromExcelFiles.supplierSpecPriceHashMapWithKeyCode_1C.put(code_1C, new Supplier(code_1C, brand, myNomenclature, model, arrayParamsForSearch, specQuery, specPrice_1C, commission, delivery));
                }

                //увеличиваем ProgressBar
//                unifierDataFromExcelFiles.updateProgress(i, UnifierDataFromExcelFiles.countFull);
                UnifierDataFromExcelFiles.countReadsRows_1C++;
                i++;
            }
            loggerReaderExcelFor_1C.info("Кол-во строк в базе 1С - " + UnifierDataFromExcelFiles.countReadsRows_1C);
        } catch (Exception e) {
            loggerReaderExcelFor_1C.info("ошибка при чтении файла Excel с базой 1C. Смотри строку - " + UnifierDataFromExcelFiles.countReadsRows_1C);
        }
    }

    boolean checkFile_1C(Sheet sheet){
        Row headRow = sheet.getRow(0);
        try {
            boolean checkCode_1C = headRow.getCell(1).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.CODE_1C);
            boolean checkProductName = headRow.getCell(3).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.NOMENCLATURE_1C);
            boolean checkSpecPrice = headRow.getCell(6).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.SPEC_PRICE_1C);
            return checkCode_1C & checkProductName & checkProductName & checkSpecPrice;
        } catch (Exception e) {
            return false;
        }
    }
}
