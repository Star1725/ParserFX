import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.util.*;

public class TaskReadExcelForWildberies extends Task<Map> {

    private int countRows;

    public TaskReadExcelForWildberies(List<File> files) {
        this.files = files;
    }

    private final List<File> files;

    public Map<String, ResultProduct> readWorkbook(List<File> files) {
        try {
            Map<String, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
            Map<String, SupplierSpecPriceAndNameProduct> supplierSpecPriceHashMapWithKeyCode_1C = new HashMap<>();

            //читаем файл отчёта Wildberies и файл 1С
            Workbook workbookWildberies = new XSSFWorkbook(files.get(0));
            Workbook workbook_1C = new XSSFWorkbook(files.get(1));

            //получаем страницы
            Sheet sheetWildberies = workbookWildberies.getSheetAt(0);
            Sheet sheet_1C = workbook_1C.getSheetAt(0);

            //проверяем, правильно ли мы прочитали файлы
            if (!checkFileWildberies(sheetWildberies) || !checkFile_1C(sheet_1C)){
                Sheet sheetBuff = sheet_1C;
                sheet_1C = sheetWildberies;
                sheetWildberies = sheetBuff;
                if (!checkFileWildberies(sheetWildberies) || !checkFile_1C(sheet_1C)){
                    System.out.println("ошибка чтения файлов Excel. Проверьте правильность написания названий столбцов, и их очерёдность\n" +
                            "");
                    resultProductHashMap.put("Ошибка чтения файло Excel", null);
                    return resultProductHashMap;
                }
            }

            //считаем кол-во строк в файлах для работы ProgressBar
            int countRowsInWildberies = sheetWildberies.getLastRowNum();
            int countRowsIn_1C = sheet_1C.getLastRowNum();
            int countFull = countRowsInWildberies + countRowsIn_1C;
            int i = 1;

            //считываем информацию с отчёта Wildberies
            Iterator rowIterator = sheetWildberies.rowIterator();
            while (rowIterator.hasNext()) {

                //получаем строку
                Row row = (Row) rowIterator.next();
                if (row.getRowNum() == 0 ){
                    continue;
                }

                //получаем бренд
                Cell cell = row.getCell(0);
                String brand = cell.getRichStringCellValue().getString();

                //получаем категорию товара(Предмет)
                cell = row.getCell(1);
                String category = cell.getRichStringCellValue().getString();

                //получаем артикул поставщика (code_1C)!!!
                cell = row.getCell(3);
                String buffStr = cell.getRichStringCellValue().getString();
                String code_1C = "-";
                if (buffStr.length() == 24) {
                    code_1C = buffStr.substring(13);
                } else if (buffStr.length() == 22 && (buffStr.startsWith("AA-") || buffStr.startsWith("RD-"))){
                    code_1C = buffStr.substring(11);
                } else if (buffStr.startsWith("AA-") || buffStr.startsWith("RD-")){
                    code_1C = buffStr.substring(0, 11);
                }

                //получаем артикл wildberies
                cell = row.getCell(4);
                int code = (int) cell.getNumericCellValue();
                if (code == 0){
                    continue;
                }
                String myVendorCodeWildberies = String.valueOf(code);

                //получаем розничную цену до скидки
                cell = row.getCell(11);
                int myPriceU = (int) (cell.getNumericCellValue() * 100);

                //получаем базоваю скидку
                cell = row.getCell(13);
                int myBasicSale = (int) cell.getNumericCellValue();

                //получаем розничную цену с базовой скидкой
                int myBasicPriceU = (int) Math.round(((1 - (double) myBasicSale/100) * myPriceU));

                //получаем промо-скидку
                cell = row.getCell(16);
                int myPromoSale = (int) cell.getNumericCellValue();

                //получаем розничную цену с базовой и промо-скидкой
                int myPromoPriceU = (int) Math.round(((1 - (double) myPromoSale/100) * myBasicPriceU));

                resultProductHashMap.put(myVendorCodeWildberies, new ResultProduct(
                        0,
                        brand,
                        category,
                        "-",
                        code_1C,
                        "-",
                        myVendorCodeWildberies,

                        0,
                        0,
                        0,
                        0,

                        "-",//для Ozon(сразу формируем поисковый запрос)
                        0,
                        myPriceU,
                        myBasicSale,
                        myBasicPriceU,
                        myPromoSale,
                        myPromoPriceU,
                        0,
                        0,
                        0,
                        0
                ));

                this.updateProgress(i, countFull);
                countRows = i;
                i++;
            }

            //считываем информацию с отчёта 1C
            System.out.println("считываем информацию с отчёта 1C");
            rowIterator = sheet_1C.rowIterator();
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

                //если модель сразу за брендом после запятой
                String model ="-";
                if (buff1[1].startsWith(",")){
                    String[] buff2 = buff1[1].trim().split(",", 3);
                    model = buff2[0];
                } else {
                    //если запятой нет
                    String[] buff2 = buff1[1].trim().split(",", 2);
                    model = buff2[0];
                }

                //получаем поисковый запрос и категорию продукта
                String querySearch = "-";
                if (!myBrand.isEmpty() || !model.isEmpty()) {
                    querySearch = myBrand + " " + model;
                }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                //получаем спец-цену
                cell = row.getCell(4);
                int specPrice_1C = (int) cell.getNumericCellValue() * 100;

                supplierSpecPriceHashMapWithKeyCode_1C.put(code_1C, new SupplierSpecPriceAndNameProduct(code_1C, myBrand, productType, myNomenclature, querySearch, specPrice_1C));
            }

            //пытаемся привязать specPrice_1C и productName к ResultProduct
            for (Map.Entry<String, ResultProduct> entry : resultProductHashMap.entrySet()) {
                String key = entry.getKey();
                String code_1C = entry.getValue().getCode_1C();
                SupplierSpecPriceAndNameProduct supplierSpecPriceAndNameProduct = supplierSpecPriceHashMapWithKeyCode_1C.get(code_1C);
                if (supplierSpecPriceAndNameProduct != null){
                    entry.getValue().setIsFind(1);
                    entry.getValue().setSpecPrice(supplierSpecPriceAndNameProduct.getSpecPrice());
                    entry.getValue().setProductType(supplierSpecPriceAndNameProduct.getProductType());
                    entry.getValue().setMyNomenclature_1C(supplierSpecPriceAndNameProduct.getNomenclature());
                    entry.getValue().setQuerySearchForWildberiesOrOzon(supplierSpecPriceAndNameProduct.getQuerySearch());
                } else entry.getValue().setSpecPrice(0);
            }
            return resultProductHashMap;
        }
        catch (Exception e) {
            System.out.println("ошибка при чтении файла Excel. Смотри строку - " + countRows);
            return null;
        }
    }

    private boolean checkFileWildberies(Sheet sheet){
        Row headRow = sheet.getRow(0);
        boolean checkBrand = false;
        boolean checkCategory = false;
        boolean checkCode_1C = false;
        boolean checkVendorCode = false;
        boolean checkPriceU = false;
        boolean checkBasicSale = false;
        boolean checkPromoSale = false;
        try {
            checkBrand = headRow.getCell(0).getRichStringCellValue().getString().equals(Constants.BRAND_NAME_IN_FILE_WILDBERIES);
            checkCategory = headRow.getCell(1).getRichStringCellValue().getString().equals(Constants.CATEGORY_NAME_IN_FILE_WILDBERIES);
            checkCode_1C = headRow.getCell(3).getRichStringCellValue().getString().equals(Constants.CODE_1C_IN_FILE_WILDBERIES);
            checkVendorCode = headRow.getCell(4).getRichStringCellValue().getString().equals(Constants.VENDOR_CODE_IN_FILE_WILDBERIES);
            checkPriceU = headRow.getCell(11).getRichStringCellValue().getString().equals(Constants.PRICE_U_IN_FILE_WILDBERIES);
            checkBasicSale = headRow.getCell(13).getRichStringCellValue().getString().equals(Constants.BASIC_SALE_IN_FILE_WILDBERIES);
            checkPromoSale = headRow.getCell(16).getRichStringCellValue().getString().equals(Constants.PROMO_SALE_IN_FILE_WILDBERIES);
            return checkBrand & checkCategory & checkCode_1C & checkVendorCode & checkPriceU & checkBasicSale & checkPromoSale;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkFile_1C(Sheet sheet){
        Row headRow = sheet.getRow(0);
        try {
            boolean checkCode_1C = headRow.getCell(1).getRichStringCellValue().getString().equals(Constants.CODE_1C);
            boolean checkProductName = headRow.getCell(2).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.NOMENCLATURE_1C.toLowerCase());
            boolean checkSpecPrice = headRow.getCell(4).getRichStringCellValue().getString().toLowerCase().trim().equals(Constants.SPEC_PRICE_1C.toLowerCase());
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
