import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.*;

public class TaskReadExcelForOzon extends Task<Map> {

    private int countRows;

    public TaskReadExcelForOzon(List<File> files) {
        this.files = files;
    }

    private final List<File> files;

    public Map<String, ResultProduct> readWorkbook(List<File> files) {
        try {
            Map<String, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
            Map<String, SupplierSpecPriceAndNameProduct> supplierSpecPriceHashMapWithKeyCode_1C = new HashMap<>();

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

                //получаем бренд и наименование продукта и сразу пытаемся получить поисковый запрос
                cell = row.getCell(2);
                String categoryBrandAndProductName = cell.getRichStringCellValue().getString();
                //определяем какой бренд
                String myBrand = "-";
                for (String s: Constants.listForBrands){
                    if (categoryBrandAndProductName.contains(s)){
                        myBrand = s;
                        break;
                    }
                }
                //делим брендом categoryBrandAndProductName на категорию и модель с характеристиками
                String[] buff1 = categoryBrandAndProductName.split(myBrand);
                //определяем категорию
                String categoryName = "-";
                for (String s: Constants.listForCategoryOzon){
                    if (buff1[0].startsWith(s)){
                        categoryName = s;
                        break;
                    }
                }

                //если модель сразу за брендом запяьтая
                String model ="-";
                if (buff1[1].startsWith(",")){
                    String[] buff2 = buff1[1].trim().split(",", 2);
                    model = buff2[0];
                } else {
                    String[] buff2 = buff1[1].trim().split(",", 3);
                    model = buff2[0];
                }

                //получаем поисковый запрос и категорию продукта
                String querySearch = "-";
                if (!myBrand.isEmpty() || !model.isEmpty()) {
                    querySearch = myBrand + " " + model;
                }

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
                        myBrand,
                        categoryName,
                        code_1C,
                        myVendorCodeOzon,
                        querySearch,
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

                //получаем артикул товара по 1С(последний баркод по Wildberies)
                cell = row.getCell(2);
                String vendorCode_1C = "-";
                if (cell != null){
                    try {
                        vendorCode_1C = String.valueOf((long)cell.getNumericCellValue());
                    } catch (Exception e) {
                        vendorCode_1C = cell.getRichStringCellValue().getString();
                    }
                }

                //получаем спец-цену
                cell = row.getCell(5);
                int specPrice_1C = (int) cell.getNumericCellValue() * 100;

                supplierSpecPriceHashMapWithKeyCode_1C.put(code_1C, new SupplierSpecPriceAndNameProduct(code_1C, specPrice_1C));
            }

            //пытаемся привязать specPrice_1C к ResultProduct
            for (Map.Entry<String, ResultProduct> entry : resultProductHashMap.entrySet()) {
                String key = entry.getKey();
                String code_1C = entry.getValue().getCode_1C();
                SupplierSpecPriceAndNameProduct supplierSpecPriceAndNameProduct1 = supplierSpecPriceHashMapWithKeyCode_1C.get(code_1C);
                if (supplierSpecPriceAndNameProduct1 != null){
                    entry.getValue().setSpecPrice(supplierSpecPriceAndNameProduct1.getSpecPrice());
                } else entry.getValue().setSpecPrice(0);

            }
            return resultProductHashMap;
        }
        catch (Exception e) {
            System.out.println("ошибка при чтении файла Excel. Смотри строку - " + countRows);
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
            boolean checkCode_1C = headRow.getCell(1).getRichStringCellValue().getString().equals(Constants.CODE_1C);
            boolean checkProductName = headRow.getCell(2).getRichStringCellValue().getString().equals(Constants.NOMENCLATURE_1C);
            boolean checkSpecPrice = headRow.getCell(4).getRichStringCellValue().getString().equals(Constants.SPEC_PRICE_1C);
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
