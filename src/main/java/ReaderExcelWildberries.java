import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

public class ReaderExcelWildberries implements ReaderExcel{

    UnifierDataFromExcelFiles unifierDataFromExcelFiles;

    public ReaderExcelWildberries(UnifierDataFromExcelFiles unifierDataFromExcelFiles) {
        this.unifierDataFromExcelFiles = unifierDataFromExcelFiles;
    }

    @Override
    public void getDataFromExcelFile(Sheet sheetReport) {
        int i = 1;

        //считываем информацию с отчёта Wildberies
        System.out.println("считываем информацию с отчёта Wildberies");
        Iterator rowIterator = sheetReport.rowIterator();
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

            UnifierDataFromExcelFiles.resultProductHashMap.put(myVendorCodeWildberies, new ResultProduct(
                    0,
                    brand,
                    category,
                    "-",
                    "-",
                    0,
                    null,
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

//            unifierDataFromExcelFiles.updateProgress(i, UnifierDataFromExcelFiles.countFull);
            unifierDataFromExcelFiles.countReadsRows = i;
            i++;
        }
        System.out.println("Кол-во строк - " + unifierDataFromExcelFiles.countReadsRows);
        System.out.println();
    }

    @Override
    public boolean checkExcelFile(Sheet sheet) {
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
}
