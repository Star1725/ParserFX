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
////////вывод столбцов
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Бренд");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Предмет (категория товара)");
        headerCell.setCellStyle(headerStyle);

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
        headerCell.setCellValue("поисковый запрос и кол-во аналогов");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("Наименование товара конкур.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue("Ссылка на конкурента");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(9);
        headerCell.setCellValue("Имя продавца");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(10);
        headerCell.setCellValue("Участие в акции");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(11);
        headerCell.setCellValue("Мои базовая цена(до скидки)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(12);
        headerCell.setCellValue("Моя тек. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(13);
        headerCell.setCellValue("Спец-цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(14);
        headerCell.setCellValue("Комиссия (%)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(15);
        headerCell.setCellValue("Логистика");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(16);
        headerCell.setCellValue("Наша пороговая цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(17);
        headerCell.setCellValue("Тек. роз. цена конкур.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(18);
        headerCell.setCellValue("Реком. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(19);
        headerCell.setCellValue("В случае повышения - новая реком. розн. цена (до скидки)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(20);
        headerCell.setCellValue("Реком. согласованная скидка");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(21);
        headerCell.setCellValue("Реком. новая скидка по промокоду");
        headerCell.setCellStyle(headerStyle);

        //настройка автоширины ячейки по содержимому
        for (int columnIndex = 0; columnIndex < 22; columnIndex++) {
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

        //настройка стиля для розовой ячейки
        CellStyle styleRoseCell = workbook.createCellStyle();
        styleRoseCell.setWrapText(true);
        styleRoseCell.setFillForegroundColor(IndexedColors.fromInt(45).getIndex());
        styleRoseCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleRoseCell.setFont(font2);

        //настройка стиля для красной ячейки
        CellStyle styleRedCell = workbook.createCellStyle();
        styleRedCell.setWrapText(true);
        styleRedCell.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleRedCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleRedCell.setFont(font2);

        //настройка стиля для ячейки с ошибкой
        CellStyle styleGreenCell = workbook.createCellStyle();
        styleGreenCell.setWrapText(true);
        styleGreenCell.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        styleGreenCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleGreenCell.setFont(font2);

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
            cell.setCellValue(productArrayList.get(i).getMyBrand());
            cell.setCellStyle(style);

            //Категория товара
            cell = row.createCell(1);
            String category = productArrayList.get(i).getCategory();
            cell.setCellValue(category);
            cell.setCellStyle(style);

//Мой артикул поставщика(по 1С)
            cell = row.createCell(2);
            cell.setCellValue(productArrayList.get(i).getCode_1C());
            cell.setCellStyle(style);

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
            cell.setCellValue(productArrayList.get(i).getQueryForSearch() + ". Найдено - " + productArrayList.get(i).getCountSearch());
            cell.setCellStyle(style);

            //наименование продукта конкурента(если нашли)
            cell = row.createCell(7);
            if (productArrayList.get(i).getCompetitorProductName().equals("-")){
                cell.setCellValue(Constants.NOT_FOUND_PAGE);
                cell.setCellStyle(styleGreenCell);
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

            //Имя продавца
            cell = row.createCell(9);
            cell.setCellValue(productArrayList.get(i).getCompetitorName());
            cell.setCellStyle(style);

            //Спец-акция
            cell = row.createCell(10);
            String specAction = productArrayList.get(i).getMySpecAction();
            if (specAction.equals(Constants.NOT_FOUND_HTML_ITEM)){
                cell.setCellValue("-");
                cell.setCellStyle(style);
            } else {
                cell.setCellValue(specAction);
                cell.setCellStyle(styleRedCell);
            }

            //Моя базовая цена
            cell = row.createCell(11);
            int myPriceU = Math.round(productArrayList.get(i).getMyPriceU() / 100);
            cell.setCellValue(myPriceU);
            cell.setCellStyle(style);

            //Моя тек. роз. цена
            cell = row.createCell(12);
            int myLowerPrice = Math.round(productArrayList.get(i).getMyLowerPriceU() / 100);
            cell.setCellValue(myLowerPrice);
            cell.setCellStyle(style);

            //Спец-цена
            cell = row.createCell(13);
            int specPrice = productArrayList.get(i).getSpecPrice() / 100;
            cell.setCellValue(specPrice);
            cell.setCellStyle(style);

            //Коммисия
            cell = row.createCell(14);
            int commissionPercentage = 0;
            switch (category){
                case Constants.CATEGORY_1:
                    commissionPercentage = 3;
                    break;

                case Constants.CATEGORY_5 :
                case Constants.CATEGORY_13:
                case Constants.CATEGORY_18:
                    commissionPercentage = 5;
                    break;

                case Constants.CATEGORY_2 :
                case Constants.CATEGORY_15:
                    commissionPercentage = 7;
                    break;

                case Constants.CATEGORY_3 :
                case Constants.CATEGORY_6 :
                case Constants.CATEGORY_7 :
                case Constants.CATEGORY_8 :
                case Constants.CATEGORY_9 :
                case Constants.CATEGORY_11:
                case Constants.CATEGORY_12:
                case Constants.CATEGORY_14:
                case Constants.CATEGORY_16:
                case Constants.CATEGORY_17:
                case Constants.CATEGORY_24:
                case Constants.CATEGORY_25:
                case Constants.CATEGORY_26:
                case Constants.CATEGORY_27:
                case Constants.CATEGORY_28:
                case Constants.CATEGORY_29:
                case Constants.CATEGORY_30:
                case Constants.CATEGORY_32:
                case Constants.CATEGORY_33:
                case Constants.CATEGORY_35:
                case Constants.CATEGORY_36:
                case Constants.CATEGORY_37:
                case Constants.CATEGORY_38:
                case Constants.CATEGORY_39:
                case Constants.CATEGORY_40:
                    commissionPercentage = 12;
                    break;

                case Constants.CATEGORY_4 :
                case Constants.CATEGORY_10:
                case Constants.CATEGORY_19:
                case Constants.CATEGORY_20:
                case Constants.CATEGORY_21:
                case Constants.CATEGORY_22:
                case Constants.CATEGORY_23:
                case Constants.CATEGORY_31:
                case Constants.CATEGORY_34:
                    commissionPercentage = 15;
                    break;
            }
            cell.setCellValue(commissionPercentage + "%");
            cell.setCellStyle(style);

            //Логистика
            cell = row.createCell(15);
            cell.setCellValue("33 р");
            cell.setCellStyle(style);

            //Наша пороговая цена
            cell = row.createCell(16);
            if (productArrayList.get(i).getSpecPrice() == 0){
                cell.setCellValue("-");
            } else {
                cell.setCellValue(specPrice + specPrice * commissionPercentage/100 + 33);
                cell.setCellStyle(style);
            }

            //Тек. роз. цена конкурента
            cell = row.createCell(17);
            int competitorLowerPrice = Math.round(productArrayList.get(i).getCompetitorLowerPriceU() / 100);
            cell.setCellValue(competitorLowerPrice);
            if (competitorLowerPrice == myLowerPrice){
                cell.setCellStyle(style);
            } else if (competitorLowerPrice < myLowerPrice){
                cell.setCellStyle(styleRoseCell);
            } else {
                cell.setCellStyle(styleGreenCell);
            }

            //Рекомендуемая роз. цена
            cell = row.createCell(18);
//            if (isMyAnalog){
//                cell.setCellValue(Math.round(productArrayList.get(i).getLowerPriceU() / 100));
//            } else {
                cell.setCellValue(Math.round(productArrayList.get(i).getRecommendedMyLowerPrice() / 100));
//            }
            cell.setCellStyle(style);

            //Если баз. скидка < 3 или > 90%, то новая реком. розн. цена (до скидки)
            cell = row.createCell(19);
            cell.setCellValue(Math.round(productArrayList.get(i).getRecommendedPriceU()/100));
            cell.setCellStyle(style);

            //"Реком. согласованная скидка"
            cell = row.createCell(20);
//            if (isMyAnalog){
//                cell.setCellValue((productArrayList.get(i).getMyBasicSale()));
//            } else {
                cell.setCellValue((productArrayList.get(i).getRecommendedSale()));
//            }
            cell.setCellStyle(style);

            //"Реком. новая скидка по промокоду"
            cell = row.createCell(21);
//            if (isMyAnalog){
//                cell.setCellValue((productArrayList.get(i).getMyPromoSale()));
//            } else {
                cell.setCellValue((productArrayList.get(i).getRecommendedPromoSale()));
//            }
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
