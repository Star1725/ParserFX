import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.util.*;

public class TaskReadExcel extends Task<Map> {

    public TaskReadExcel(List<File> files) {
        this.files = files;
    }

    private final List<File> files;

    public Map<String, ResultProduct> readWorkbook(List<File> files) {
        try {
            Map<String, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
            Map<String, SupplierSpecPrice> supplierSpecPriceHashMapWithKeyCode_1C = new HashMap<>();
            Map<String, SupplierSpecPrice> supplierSpecPriceHashMapWithKeyVendorCode_1C = new HashMap<>();


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

                //получаем артикул поставщика (code_1C)
                cell = row.getCell(3);
                String code_1C = cell.getRichStringCellValue().getString();

                //получаем артикл wildberies
                cell = row.getCell(4);
                int code = (int) cell.getNumericCellValue();
                if (code == 0){
                    continue;
                }
                String myVendorCodeWildberies = String.valueOf(code);

                //получаем последний баркод (vendorCode_1C)
                cell = row.getCell(5);
                String vendorCode_1C;
                if (cell == null){
                    vendorCode_1C = String.valueOf(i);
                } else {
                    try {
                        vendorCode_1C = cell.getRichStringCellValue().getString();
                    } catch (Exception e) {
                        vendorCode_1C = String.valueOf(cell.getNumericCellValue());
                    }
                }

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
                        brand,
                        category,
                        code_1C,
                        myVendorCodeWildberies,
                        vendorCode_1C,
                        0,
                        myPriceU,
                        myBasicSale,
                        myBasicPriceU,
                        myPromoSale,
                        myPromoPriceU,
                        0,
                        0,
                        0
                ));

                this.updateProgress(i, countFull);
                i++;
            }

            //считываем информацию с отчёта 1C
            rowIterator = sheet_1C.rowIterator();
            while (rowIterator.hasNext()){
                //получаем строку
                Row row = (Row) rowIterator.next();
                if (row.getRowNum() == 0 ){
                    continue;
                }

                //получаем код товара по 1С
                Cell cell = row.getCell(1);
                String code_1C = cell.getRichStringCellValue().getString();

                //получаем артикул товара по 1С(последний баркод по Wildberies)
                cell = row.getCell(2);

                String vendorCode_1C = null;
                try {
                    vendorCode_1C = String.valueOf((long)cell.getNumericCellValue());
                } catch (Exception e) {
                    vendorCode_1C = cell.getRichStringCellValue().getString();
                }

                //получаем спец-цену
                cell = row.getCell(5);
                int specPrice_1C = (int) cell.getNumericCellValue() * 100;

                supplierSpecPriceHashMapWithKeyCode_1C.put(code_1C, new SupplierSpecPrice(code_1C, vendorCode_1C, specPrice_1C));
                supplierSpecPriceHashMapWithKeyVendorCode_1C.put(vendorCode_1C, new SupplierSpecPrice(code_1C, vendorCode_1C, specPrice_1C));
            }

            //пытаемся привязать specPrice_1C к ResultProduct
            for (Map.Entry<String, ResultProduct> entry : resultProductHashMap.entrySet()) {
                String key = entry.getKey();
                String code_1C = entry.getValue().getCode_1C();
                String vendorCode_1C = entry.getValue().getVendorCode_1C();
                SupplierSpecPrice supplierSpecPrice1 = supplierSpecPriceHashMapWithKeyCode_1C.get(code_1C);
                SupplierSpecPrice supplierSpecPrice2 = supplierSpecPriceHashMapWithKeyCode_1C.get(vendorCode_1C);
                if (supplierSpecPrice1 != null){
                    entry.getValue().setSpecPrice(supplierSpecPrice1.getSpecPrice());
                } else if (supplierSpecPrice2 != null){
                    entry.getValue().setSpecPrice(supplierSpecPrice2.getSpecPrice());
                } else entry.getValue().setSpecPrice(0);

            }
            return resultProductHashMap;
        }
        catch (Exception e) {
            System.out.println("ошибка при чтении файла .xls");
            return null;
        }

    }

    private boolean checkFileWildberies(Sheet sheet){
        Row headRow = sheet.getRow(0);
        boolean checkBrand = false;
        boolean checkCategory = false;
        boolean checkCode_1C = false;
        boolean checkVendorCode = false;
        boolean checkVendorCode_1C = false;
        boolean checkPriceU = false;
        boolean checkBasicSale = false;
        boolean checkPromoSale = false;
        try {
            checkBrand = headRow.getCell(0).getRichStringCellValue().getString().equals(Constants.BRAND_NAME_IN_FILE_WILDBERIES);
            checkCategory = headRow.getCell(1).getRichStringCellValue().getString().equals(Constants.CATEGORY_NAME_IN_FILE_WILDBERIES);
            checkCode_1C = headRow.getCell(3).getRichStringCellValue().getString().equals(Constants.CODE_1C_IN_FILE_WILDBERIES);
            checkVendorCode = headRow.getCell(4).getRichStringCellValue().getString().equals(Constants.VENDOR_CODE_IN_FILE_WILDBERIES);
            checkVendorCode_1C = headRow.getCell(5).getRichStringCellValue().getString().equals(Constants.VENDOR_CODE_1C_IN_FILE_WILDBERIES);
            checkPriceU = headRow.getCell(11).getRichStringCellValue().getString().equals(Constants.PRICE_U_IN_FILE_WILDBERIES);
            checkBasicSale = headRow.getCell(13).getRichStringCellValue().getString().equals(Constants.BASIC_SALE_IN_FILE_WILDBERIES);
            checkPromoSale = headRow.getCell(16).getRichStringCellValue().getString().equals(Constants.PROMO_SALE_IN_FILE_WILDBERIES);
        } catch (Exception e) {
            return false;
        }

        return checkBrand & checkCategory & checkCode_1C & checkVendorCode & checkVendorCode_1C & checkPriceU & checkBasicSale & checkPromoSale;
    }

    private boolean checkFile_1C(Sheet sheet){
        Row headRow = sheet.getRow(0);
        boolean checkCode_1C = false;
        boolean checkVendorCode = false;
        boolean checkSpecPrice = false;
        try {
            checkCode_1C = headRow.getCell(1).getRichStringCellValue().getString().equals(Constants.CODE_1C);
            checkVendorCode = headRow.getCell(2).getRichStringCellValue().getString().equals(Constants.VENDOR_CODE_1C);
            checkSpecPrice = headRow.getCell(5).getRichStringCellValue().getString().equals(Constants.SPEC_PRICE_1C);
        } catch (Exception e) {
            return false;
        }

        return checkCode_1C & checkVendorCode & checkSpecPrice;
    }

    @Override
    protected Map call() throws Exception {
        return this.readWorkbook(files);
    }
}
