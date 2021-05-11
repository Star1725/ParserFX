import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

public class ReaderExcelOzon implements ReaderExcel{

    UnifierDataFromExcelFiles unifierDataFromExcelFiles;

    public ReaderExcelOzon(UnifierDataFromExcelFiles unifierDataFromExcelFiles) {
        this.unifierDataFromExcelFiles = unifierDataFromExcelFiles;
    }

    @Override
    public void getDataFromExcelFile(Sheet sheetReport) {
        int i = 1;
        try {
            //считываем информацию с отчёта Ozon
            System.out.println("считываем информацию с отчёта Ozon");
            Iterator rowIterator = sheetReport.rowIterator();
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

                UnifierDataFromExcelFiles.resultProductHashMap.put(myVendorCodeOzon, new ResultProduct(
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
                //unifierDataFromExcelFiles.updateProgress(i, UnifierDataFromExcelFiles.countFull);
                UnifierDataFromExcelFiles.countReadsRows = i;
                i++;
            }
            System.out.println("Кол-во строк - " + UnifierDataFromExcelFiles.countReadsRows);
        } catch (Exception e) {
            System.out.println("ошибка при чтении файла Excel с отчётом Ozon. Смотри строку - " + UnifierDataFromExcelFiles.countReadsRows);
        }
    }

    @Override
    public boolean checkExcelFile(Sheet sheet) {
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
}
