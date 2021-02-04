import controllers.Controller;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExelHandler {

    private static final String NOT_FOUND_PAGE = "Не найдена страница товара";

    public static Map<String, ResultProduct> readWorkbook(File file, Controller controller) {
        try {
            Map<String, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
            Workbook workbook = new XSSFWorkbook(file);

            //получаем страницу
            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, List<String>> data = new HashMap<>();

            double fullLoad = sheet.getLastRowNum();
            double i = 1;

            Iterator rowIter = sheet.rowIterator();
            while (rowIter.hasNext()) {
                String strQuery = "";

               //data.put(i, new ArrayList<String>());

                //получаем строку
                Row row = (Row) rowIter.next();
                if (row.getRowNum() == 0 ){
                    continue;
                }

                //получаем артикл
                Cell cell = row.getCell(4);
                int code = (int) cell.getNumericCellValue();
                if (code == 0){
                    continue;
                }
                String myVendorCode = String.valueOf(code);
                //получаем бренд
                cell = row.getCell(0);
                String brand = cell.getRichStringCellValue().getString();
                //получаем категорию товара
                cell = row.getCell(1);
                String category = cell.getRichStringCellValue().getString();
                //получаем розничную цену до скидки
                cell = row.getCell(11);
                double myPrice = cell.getNumericCellValue();
                //получаем текущую скидку
                cell = row.getCell(13);
                int mySale = (int) cell.getNumericCellValue();
                //получаем промо-скидку
                cell = row.getCell(16);
                int myPromoSale = (int) cell.getNumericCellValue();

                double myLowerPrice = 0;
                if (mySale != 0){
                    myLowerPrice = myPrice * (1 - (double)mySale/100);
                } else {
                    myLowerPrice = myPrice;
                }
                if (myPromoSale != 0){
                    myLowerPrice = myLowerPrice * (1 - (double)myPromoSale/100);
                }

                resultProductHashMap.put(myVendorCode, new ResultProduct(
                        myVendorCode,
                        "-",
                        category,
                        brand,
                        "-",
                        "-",
                        "-",
                        0,
                        0,
                        "-",
                        0,
                        "-",
                        Math.round(myLowerPrice),
                        myPrice,
                        mySale,
                        myPromoSale,
                        0,
                        0));
            }
            return resultProductHashMap;
        }
        catch (Exception e) {
            System.out.println("ошибка при чтении файла .xls");
            return null;
        }
    }



    public static File writeWorkbook(Map<String, ResultProduct> productMap) {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Аналитика");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        //headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Бренд");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Мой артикул");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Товар");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Моя тек. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Роз. цена конк.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Моя базовая цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue("Рекомендуемая скидка");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("Рекомендуемая роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue("Ссылка на конкурента");
        headerCell.setCellStyle(headerStyle);


        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        CellStyle styleMyProduct = workbook.createCellStyle();
        styleMyProduct.setWrapText(true);
        styleMyProduct.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleMyProduct.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleExeption = workbook.createCellStyle();
        styleExeption.setWrapText(true);
        styleExeption.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleExeption.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        ArrayList<ResultProduct> productArrayList = new ArrayList<>(productMap.values());

        for (int i = 0; i < productArrayList.size(); i++) {

            Row row = sheet.createRow(i + 1);

            boolean isMy = productArrayList.get(i).getMyVendorCode().equals(productArrayList.get(i).getVendorCode());

            Cell cell = row.createCell(0);
            cell.setCellValue(productArrayList.get(i).getBrand());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(productArrayList.get(i).getMyVendorCode());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            if (productArrayList.get(i).getProductName().equals("-")){
                cell.setCellValue(NOT_FOUND_PAGE);
                cell.setCellStyle(styleExeption);
            } else {
                cell.setCellValue((productArrayList.get(i).getProductName()));
                cell.setCellStyle(style);
            }

            cell = row.createCell(3);
            cell.setCellValue((productArrayList.get(i).getMyLoverPrice()));
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue((productArrayList.get(i).getLowerPrice()));
            cell.setCellStyle(style);

            cell = row.createCell(5);
            cell.setCellValue((productArrayList.get(i).getMyPriceU()));
            cell.setCellStyle(style);

            cell = row.createCell(6);
            cell.setCellValue((productArrayList.get(i).getRecommendedSale()));
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue((productArrayList.get(i).getRecommendedPrice()));
            cell.setCellStyle(style);

            cell = row.createCell(8);
            cell.setCellValue((productArrayList.get(i).getRefForPage()));
            if (isMy){
                cell.setCellStyle(styleMyProduct);
            } else {
                cell.setCellStyle(style);
            }
        }

        for (int columnIndex = 0; columnIndex < 8; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "Analytics.xlsx";

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return currDir;
    }
}
