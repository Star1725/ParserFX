import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class TaskWriteExel extends Task<File> {

    private Workbook workbook;
    private Sheet sheet;
    private CreationHelper helper;
    private ClientAnchor anchor;
    private Drawing drawing;
    private int countRows;

    TaskWriteExel(Map map){
        this.resultProductHashMap = map;
    }

    private final Map resultProductHashMap;

    public File writeWorkbook(Map<String, ResultProduct> productMap) {

        workbook = new XSSFWorkbook();
        helper = workbook.getCreationHelper();
        anchor = helper.createClientAnchor();

        sheet = workbook.createSheet("Аналитика");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        drawing = sheet.createDrawingPatriarch();

        countRows = resultProductHashMap.size();

        Row header = sheet.createRow(0);

 ////////настройка стиля названий столбцов
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Бренд");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Предмет (категория товара)");
        headerCell.setCellStyle(headerStyle);
////////
        headerCell = header.createCell(2);
        headerCell.setCellValue("Мой артикул поставщика (по 1С)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Мой артикул по Wild");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Моё наименование товара");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Ссылка на мой товар");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue("поисковый запрос");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("Наименование товара конкур.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue("Ссылка на конкурента");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(9);
        headerCell.setCellValue("Мои спец-акции");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(10);
        headerCell.setCellValue("Тек. роз. цена конкур.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(11);
        headerCell.setCellValue("Моя тек. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(12);
        headerCell.setCellValue("Спец-цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(13);
        headerCell.setCellValue("Реком. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(14);
        headerCell.setCellValue("Реком. согласованная скидка");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(15);
        headerCell.setCellValue("Реком. новая скидка по промокоду");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(16);
        headerCell.setCellValue("Моя базовая цена");
        headerCell.setCellStyle(headerStyle);

        //настройка автоширины ячейки по содержимому
        for (int columnIndex = 0; columnIndex < 17; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }

////////настройка стиля отображения остального текста
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        XSSFFont font2 = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font2);

        //настройка стиля для ячейки с сылкой на мой продукт
        CellStyle styleMyProduct = workbook.createCellStyle();
        styleMyProduct.setWrapText(true);
        styleMyProduct.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        styleMyProduct.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleMyProduct.setFont(font2);

        //настройка стиля для ячейки с сылкой на мой аналогичный продукт
        CellStyle styleMyProductAnalog = workbook.createCellStyle();
        styleMyProductAnalog.setWrapText(true);
        styleMyProductAnalog.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        styleMyProductAnalog.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleMyProductAnalog.setFont(font2);

        //настройка стиля для ячейки с ошибкой
        CellStyle styleException = workbook.createCellStyle();
        styleException.setWrapText(true);
        styleException.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleException.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleException.setFont(font2);

        //паолучаем множество моих артикулов, чтобы можно было понять относится ли итотговый продукт к моим артикулам(для окрашивания ячеек)
        Set<String> myVendorCodesSet = new HashSet<>(productMap.keySet());

        ArrayList<ResultProduct> productArrayList = new ArrayList<>(productMap.values());

        for (int i = 0; i < productArrayList.size(); i++) {

            Row row = sheet.createRow(i + 1);
            row.setHeightInPoints(70);

            boolean isMy = productArrayList.get(i).getMyVendorCodeForWildberies().equals(productArrayList.get(i).getCompetitorVendorCode());

            boolean isMyAnalog = myVendorCodesSet.contains(productArrayList.get(i).getCompetitorVendorCode());

            //Бренд
            Cell cell = row.createCell(0);
            cell.setCellValue(productArrayList.get(i).getCompetitorBrand());
            cell.setCellStyle(style);

            //Категория товара
            cell = row.createCell(1);
            cell.setCellValue(productArrayList.get(i).getCategory());
            cell.setCellStyle(style);

//Мой артикул поставщика(по 1С)

            //Мой артикул по Wildberies
            cell = row.createCell(3);
            cell.setCellValue(productArrayList.get(i).getMyVendorCodeForWildberies());
            if (isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else if (!isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else {
                cell.setCellStyle(style);
            }


            //моё наименование товара
            cell = row.createCell(4);
            cell.setCellValue(productArrayList.get(i).getMyProductName());
            if (isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else if (!isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else {
                cell.setCellStyle(style);
            }

            //ссылка на мой товар
            cell = row.createCell(5);
            cell.setCellValue((productArrayList.get(i).getMyRefForPage()));
            if (isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else if (!isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else {
                cell.setCellStyle(style);
            }

            //поисковый запрос
            cell = row.createCell(6);
            cell.setCellValue(productArrayList.get(i).getQueryForSearch());
            cell.setCellStyle(style);

            //наименование продукта конкурента(если нашли)
            cell = row.createCell(7);
            if (productArrayList.get(i).getCompetitorProductName().equals("-")){
                cell.setCellValue(Constants.NOT_FOUND_PAGE);
                cell.setCellStyle(styleException);
            } else {
                cell.setCellValue((productArrayList.get(i).getCompetitorProductName()));
                if (isMy && isMyAnalog){
                    cell.setCellStyle(styleMyProduct);
                } else if (!isMy && isMyAnalog){
                    cell.setCellStyle(styleMyProductAnalog);
                } else {
                    cell.setCellStyle(style);
                }
            }

            //ссылка на конкурента, или самого себя, или мой аналогичный товар
            cell = row.createCell(8);
            cell.setCellValue(productArrayList.get(i).getCompetitorRefForPage());
            if (isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else if (!isMy && isMyAnalog){
                cell.setCellStyle(styleMyProductAnalog);
            } else {
                cell.setCellStyle(style);
            }

            //моя спецакция
            cell = row.createCell(9);
            cell.setCellValue(productArrayList.get(i).getMySpecAction());
            cell.setCellStyle(style);

            //Тек. роз. цена конкурента
            cell = row.createCell(10);
            cell.setCellValue(Math.round(productArrayList.get(i).getLowerPriceU() / 100));
            cell.setCellStyle(style);

            //Моя тек. роз. цена
            cell = row.createCell(11);
            cell.setCellValue(Math.round(productArrayList.get(i).getMyLowerPriceU() / 100));
            cell.setCellStyle(style);

//Спец-цуна(по 1С)

            //Рекомендуемая роз. цена
            cell = row.createCell(13);
            if (isMyAnalog){
                cell.setCellValue(Math.round(productArrayList.get(i).getLowerPriceU() / 100));
            } else {
                cell.setCellValue(Math.round(productArrayList.get(i).getRecommendedPriceU() / 100));
            }
            cell.setCellStyle(style);

            //"Реком. согласованная скидка"
            cell = row.createCell(14);
            if (isMyAnalog){
                cell.setCellValue((productArrayList.get(i).getMyBasicSale()));
            } else {
                cell.setCellValue((productArrayList.get(i).getRecommendedSale()));
            }
            cell.setCellStyle(style);

            //"Реком. новая скидка по промокоду"
            cell = row.createCell(15);
            if (isMyAnalog){
                cell.setCellValue((productArrayList.get(i).getMyPromoSale()));
            } else {
                cell.setCellValue((productArrayList.get(i).getRecommendedPromoSale()));
            }
            cell.setCellStyle(style);

            //Моя базовая цена
            cell = row.createCell(16);
            cell.setCellValue(Math.round(productArrayList.get(i).getMyPriceU() / 100));
            cell.setCellStyle(style);

            //установка картинки для моего товара
            String myImageUrl = productArrayList.get(i).getMyRefForImage();
            if (!myImageUrl.equals("-")){
                setImageForCell(myImageUrl, 5, i, 0.125, 0.125);
            }

            //установка картинки для конкурента
            String imageUrl = productArrayList.get(i).getCompetitorRefForImage();
            if (!imageUrl.equals("-")){
                setImageForCell(imageUrl, 8, i, 0.25, 0.25);
            }

            this.updateProgress(i + 1, countRows);
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

    private void setImageForCell(String url, int columnIndex, int rowIndex, double scaleX, double scaleY) {
        try {
            byte[] bytes = Jsoup.connect(url).timeout(30000).ignoreContentType(true).execute().bodyAsBytes();

            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);

            helper = workbook.getCreationHelper();

            drawing = sheet.createDrawingPatriarch();

            anchor = helper.createClientAnchor();

            anchor.setCol1(columnIndex);
            anchor.setRow1(rowIndex + 1);

            Picture pict = drawing.createPicture(anchor, pictureIdx);

            pict.resize(scaleX, scaleY);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected File call() throws Exception {
        return this.writeWorkbook(resultProductHashMap);
    }
}
