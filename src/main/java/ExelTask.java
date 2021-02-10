import controllers.Controller;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExelTask extends Task<Map> {

    public ExelTask(File file) {
        this.file = file;
    }

    private final File file;

    public Map<String, ResultProduct> readWorkbook(File file) {
        try {
            Map<String, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
            Workbook workbook = new XSSFWorkbook(file);

            //получаем страницу
            Sheet sheet = workbook.getSheetAt(0);

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
                this.updateMessage("i");
                i++;
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
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Бренд");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Мой артикул");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("SpecAction для моего артикула");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Товар");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Моя тек. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Роз. цена конкурента");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue("Рекомендуемая роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("Реком. согласованная скидка");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue("Реком. новая скидка по промокоду");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(9);
        headerCell.setCellValue("Моя базовая цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(10);
        headerCell.setCellValue("Ссылка на конкурента");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(11);
        headerCell.setCellValue("Ссылка на мой товар");
        headerCell.setCellStyle(headerStyle);


        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        CellStyle styleMyProduct = workbook.createCellStyle();
        styleMyProduct.setWrapText(true);
        styleMyProduct.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleMyProduct.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleMyProductAnalog = workbook.createCellStyle();
        styleMyProductAnalog.setWrapText(true);
        styleMyProductAnalog.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        styleMyProductAnalog.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle styleException = workbook.createCellStyle();
        styleException.setWrapText(true);
        styleException.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleException.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Set<String> myVendorCodesSet = new HashSet<>(productMap.keySet());

        ArrayList<ResultProduct> productArrayList = new ArrayList<>(productMap.values());

        for (int i = 0; i < productArrayList.size(); i++) {

            Row row = sheet.createRow(i + 1);

            boolean isMy = productArrayList.get(i).getMyVendorCode().equals(productArrayList.get(i).getVendorCode());

            boolean isMyAnalog = myVendorCodesSet.contains(productArrayList.get(i).getVendorCode());

            Cell cell = row.createCell(0);
            cell.setCellValue(productArrayList.get(i).getBrand());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(productArrayList.get(i).getMyVendorCode());
            cell.setCellStyle(style);


            cell = row.createCell(2);
            cell.setCellValue(productArrayList.get(i).getMySpecAction());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            if (productArrayList.get(i).getProductName().equals("-")){
                cell.setCellValue(Constants.NOT_FOUND_PAGE);
                cell.setCellStyle(styleException);
            } else {
                cell.setCellValue((productArrayList.get(i).getProductName()));
                cell.setCellStyle(style);
            }

            //Моя тек. роз. цена
            cell = row.createCell(4);
            cell.setCellValue(Math.round(productArrayList.get(i).getMyLowerPriceU() / 100));
            cell.setCellStyle(style);

            //Роз. цена конкурента
            cell = row.createCell(5);
            cell.setCellValue(Math.round(productArrayList.get(i).getLowerPriceU() / 100));
            cell.setCellStyle(style);

            //Рекомендуемая роз. цена
            cell = row.createCell(6);
            if (isMyAnalog){
                cell.setCellValue(Math.round(productArrayList.get(i).getLowerPriceU() / 100));
            } else {
                cell.setCellValue(Math.round(productArrayList.get(i).getRecommendedPriceU() / 100));
            }
            cell.setCellStyle(style);

            //"Реком. согласованная скидка"
            cell = row.createCell(7);
            if (isMyAnalog){
                cell.setCellValue((productArrayList.get(i).getMyBasicSale()));
            } else {
                cell.setCellValue((productArrayList.get(i).getRecommendedSale()));
            }
            cell.setCellStyle(style);

            //"Реком. новая скидка по промокоду"
            cell = row.createCell(8);
            if (isMyAnalog){
                cell.setCellValue((productArrayList.get(i).getMyPromoSale()));
            } else {
                cell.setCellValue((productArrayList.get(i).getRecommendedPromoSale()));
            }
            cell.setCellStyle(style);

            //Моя базовая цена
            cell = row.createCell(9);
            cell.setCellValue(Math.round(productArrayList.get(i).getMyPriceU() / 100));
            cell.setCellStyle(style);

            //ссылка на конкурента(или самого себя)
            cell = row.createCell(10);
            if (!productArrayList.get(i).getRefFromRequest().startsWith("https")){
                cell.setCellValue(productArrayList.get(i).getRefFromRequest());
                cell.setCellStyle(styleException);
            } else {
                cell.setCellValue(productArrayList.get(i).getRefForPage());
                if (isMy && isMyAnalog){
                    cell.setCellStyle(styleMyProduct);
                } else if (!isMy && isMyAnalog){
                    cell.setCellStyle(styleMyProductAnalog);
                } else {
                    cell.setCellStyle(style);
                }
            }


            //ссылка на мой товар (или самого себя)
            cell = row.createCell(11);
            cell.setCellValue((productArrayList.get(i).getMyRefForPage()));
            if (isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else if (!isMy && isMyAnalog){
                cell.setCellStyle(styleMyProductAnalog);
            } else {
                cell.setCellStyle(style);
            }
        }

        //настройка автоширины ячейки по содержимому
        for (int columnIndex = 0; columnIndex < 12; columnIndex++) {
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

    @Override
    protected Map call() throws Exception {
        return this.readWorkbook(file);
    }
}
