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

            //читаем файл отчёта Wildberies и файл 1С

            Workbook workbookWildberies = new XSSFWorkbook(files.get(0));
            Workbook workbook_1C = new XSSFWorkbook(files.get(1));

            //получаем страницы
            Sheet sheetWildberies = workbookWildberies.getSheetAt(0);
            Sheet sheet_1C = workbook_1C.getSheetAt(0);

            //проверяем, правильно ли мы прочитали файлы

            Map<Integer, List<String>> data = new HashMap<>();

            int countRows = sheet.getLastRowNum();
            int i = 1;

            Iterator rowIter = sheet.rowIterator();
            while (rowIter.hasNext()) {

                //получаем строку
                Row row = (Row) rowIter.next();
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
                String myVendorCode = String.valueOf(code);

                //получаем последний баркод (vendorCode_1C)
                cell = row.getCell(5);
                String vendorCode_1C = cell.getRichStringCellValue().getString();

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

                resultProductHashMap.put(myVendorCode, new ResultProduct(
                        "-",
                        "-",
                        "-",
                        "-",
                        "-",
                        category,
                        brand,
                        myVendorCode,
                        myPriceU,
                        myBasicSale,
                        myBasicPriceU,
                        myPromoSale,
                        myPromoPriceU,
                        "-",
                        "-",
                        "-",
                        "-",
                        0,
                        0,
                        0,
                        0,
                        0,
                        "-",
                        0,
                        "-",
                        0,
                        0,
                        0
                        ));

                Thread.sleep(10);

                this.updateProgress(i, countRows);
                i++;
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
        boolean checkBrand = headRow.getCell(0).getRichStringCellValue().getString().equals(Constants.BRAND_NAME);
        boolean checkCategory = headRow.getCell(1).getRichStringCellValue().getString().equals(Constants.CATEGORY_NAME);
        boolean checkCode_1C = headRow.getCell(3).getRichStringCellValue().getString().equals(Constants.CODE_1C);
        boolean checkVendorCode = headRow.getCell(4).getRichStringCellValue().getString().equals(Constants.VENDOR_CODE);
        boolean checkVendorCode_1C = headRow.getCell(5).getRichStringCellValue().getString().equals(Constants.VENDOR_CODE_1C);
        boolean checkPriceU = headRow.getCell(11).getRichStringCellValue().getString().equals(Constants.PRICE_U);
        boolean checkBasicSale = headRow.getCell(13).getRichStringCellValue().getString().equals(Constants.BASIC_SALE);
    }

    @Override
    protected Map call() throws Exception {
        return this.readWorkbook(file);
    }
}
