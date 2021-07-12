import com.gargoylesoftware.htmlunit.WebClient;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class TaskWriteExelForOzon extends Task<File> {

    private Workbook workbook;
    private Sheet sheet;
    private CreationHelper helper;
    private ClientAnchor anchor;
    private Drawing drawing;
    private int countRows;
    private final WebClient webClient;

    TaskWriteExelForOzon(Map map, WebClient webClient){
        this.resultProductHashMap = map;
        this.webClient = webClient;
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
        headerCell.setCellValue("Бренд  ");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Тип товара");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Мой артикул поставщика (по 1С)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Мой артикул по Ozon");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Моё наименование товара");//номенклатура
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Ссылка на мой товар");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue("результат поискового запроса");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("дополнит. парам. поиска");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue("ссылка на страницу поискового запроса");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(9);
        headerCell.setCellValue("Наименование товара конкур.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(10);
        headerCell.setCellValue("Ссылка на конкурента");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(11);
        headerCell.setCellValue("Имя продавца");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(12);
        headerCell.setCellValue("Тек. роз. цена конкур.");
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
        headerCell.setCellValue("Зарабаток");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(18);
        headerCell.setCellValue("Реком. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(19);
        headerCell.setCellValue("Участие в акции");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(20);
        headerCell.setCellValue("Мои базовая цена(до скидки)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(21);
        headerCell.setCellValue("Моя тек. роз. цена");
        headerCell.setCellStyle(headerStyle);

//        headerCell = header.createCell(19);
//        headerCell.setCellValue("В случае повышения - новая реком. розн. цена (до скидки)");
//        headerCell.setCellStyle(headerStyle);
//
//        headerCell = header.createCell(20);
//        headerCell.setCellValue("Реком. согласованная скидка");
//        headerCell.setCellStyle(headerStyle);
//
//        headerCell = header.createCell(21);
//        headerCell.setCellValue("Реком. новая скидка по промокоду");
//        headerCell.setCellStyle(headerStyle);

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

        //настройка стиля для голубой ячейки
        CellStyle styleBlueCell = workbook.createCellStyle();
        styleBlueCell.setWrapText(true);
        styleBlueCell.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        styleBlueCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleBlueCell.setFont(font2);

        //настройка стиля для зелёной ячейки
        CellStyle styleGreenCell = workbook.createCellStyle();
        styleGreenCell.setWrapText(true);
        styleGreenCell.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        styleGreenCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleGreenCell.setFont(font2);

        //паолучаем множество моих артикулов, чтобы можно было понять относится ли итотговый продукт к моим артикулам(для окрашивания ячеек)
        Set<String> myVendorCodesSet = new HashSet<>(productMap.keySet());

        ArrayList<ResultProduct> productArrayList = new ArrayList<>(productMap.values());

        int countRows = 1;
        for (int i = 0; i < productArrayList.size(); i++) {
            try {

                Row row = sheet.createRow(i + 1);
                row.setHeightInPoints(70);

                boolean isMy = false;
                try {
                    isMy = productArrayList.get(i).getCompetitorName().equals(Constants.MY_SELLER) || productArrayList.get(i).getCompetitorName().equals(Constants.MY_SELLER_2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //boolean isMyAnalog = myVendorCodesSet.contains(productArrayList.get(i).getCompetitorVendorCode());

                //Бренд
                Cell cell = row.createCell(0);
                cell.setCellValue(productArrayList.get(i).getMyBrand());
                if (productArrayList.get(i).getMyBrand().equals("xivi") || productArrayList.get(i).getMyBrand().equals("mietubl")){
                    cell.setCellStyle(styleBlueCell);//настройка для игнорируемых брендов "xivi" и "mietubl"
                } else {
                    cell.setCellStyle(style);
                }


                //тип товара
                cell = row.createCell(1);
                String productType = productArrayList.get(i).getProductType();
                cell.setCellValue(productType);
                cell.setCellStyle(style);

                //Мой артикул поставщика(по 1С)
                cell = row.createCell(2);
                String myVendorCode = productArrayList.get(i).getCode_1C();
                if (productArrayList.get(i).getIsFind() == 0) {
                    cell.setCellValue(myVendorCode + " - не найден в базе 1С");
                    cell.setCellStyle(styleRedCell);
                }
                if (productArrayList.get(i).getIsFind() == 1) {
                    cell.setCellValue(myVendorCode);
                    cell.setCellStyle(style);
                }

                //Мой артикул по Ozon
                cell = row.createCell(3);
                cell.setCellValue(productArrayList.get(i).getMyVendorCodeForWildberiesOrOzon());
                cell.setCellStyle(style);

                //моё наименование товара (номенклатура)
                cell = row.createCell(4);
                cell.setCellValue(productArrayList.get(i).getMyNomenclature_1C());
                cell.setCellStyle(style);

                //ссылка на мой товар
                cell = row.createCell(5);
                cell.setCellValue(productArrayList.get(i).getMyRefForPage());
                cell.setCellStyle(style);

                //результат поискового запроса аналогов
                cell = row.createCell(6);
                String querySearchAndResult = productArrayList.get(i).getResultForSearch();
                cell.setCellValue(querySearchAndResult);
                cell.setCellStyle(style);

                //дополнительные параметры поиска
                cell = row.createCell(7);
                if (productArrayList.get(i).getArrayListParams() != null){
                    if (productArrayList.get(i).getArrayListParams().size() == 0){
                        cell.setCellValue("Дополнит. парам. поиска \n\r не найдены");
                    } else {
                        String result = "";
                        for (List<String> stringList: productArrayList.get(i).getArrayListParams()){
                            for (String param: stringList){
                                result = result + param + " ";
                            }
                            result = result + "\n\r";
                        }
                        cell.setCellValue(result);
                    }
                    cell.setCellStyle(style);
                }

                //ссылка на страницу поискового запроса
                cell = row.createCell(8);
                String refUrlForResult = productArrayList.get(i).getRefUrlForResultSearch();
                cell.setCellValue(refUrlForResult);
                cell.setCellStyle(style);

                //наименование продукта конкурента(если нашли)
                cell = row.createCell(9);
                if (querySearchAndResult.equals(Constants.BLOCKING)) {
                    cell.setCellValue(Constants.BLOCKING);
                    cell.setCellStyle(styleRedCell);
                } else {
                    if (productArrayList.get(i).getCompetitorProductName().equals("-")) {
                        cell.setCellValue(Constants.NOT_FOUND_PAGE);
                        cell.setCellStyle(styleGreenCell);
                    } else {
                        cell.setCellValue(productArrayList.get(i).getCompetitorProductName());
                        if (isMy) {
                            cell.setCellStyle(styleMyProduct);
                        } else {
                            cell.setCellStyle(style);
                        }
                    }
                }


                //ссылка на конкурента, или самого себя, или мой аналогичный товар
                cell = row.createCell(10);
                if (querySearchAndResult.equals(Constants.BLOCKING)) {
                    cell.setCellValue(Constants.BLOCKING);
                    cell.setCellStyle(styleRedCell);
                } else {
                    cell.setCellValue(productArrayList.get(i).getCompetitorRefForPage());
                    if (isMy) {
                        cell.setCellStyle(styleMyProduct);
                    } else {
                        cell.setCellStyle(style);
                    }
                }

                //Имя продавца
                cell = row.createCell(11);
                if (querySearchAndResult.equals(Constants.BLOCKING)) {
                    cell.setCellValue(Constants.BLOCKING);
                    cell.setCellStyle(styleRedCell);
                } else {
                    cell.setCellValue(productArrayList.get(i).getCompetitorName());
                    cell.setCellStyle(style);
                }

                //////////////расчитываем все необходимые цены /////////////////////////////////////////////////////////////////////////
                double myLowerPrice = Math.round(productArrayList.get(i).getMyLowerPriceU() / 100);
                double competitorLowerPrice = Math.round(productArrayList.get(i).getCompetitorLowerPriceU() / 100);
                double specPrice = productArrayList.get(i).getSpecPrice() / 100;
                double recommendLowerPrice = Math.round(productArrayList.get(i).getRecommendedMyLowerPrice() / 100);

                //Тек. роз. цена конкурента
                cell = row.createCell(12);
                cell.setCellValue(competitorLowerPrice);
                if (competitorLowerPrice == myLowerPrice) {
                    cell.setCellStyle(style);
                } else if (competitorLowerPrice < myLowerPrice) {
                    cell.setCellStyle(styleRoseCell);
                } else {
                    cell.setCellStyle(styleGreenCell);
                }

                //Спец-цена
                cell = row.createCell(13);
                if (specPrice == 0) {
                    cell.setCellValue("н/д");
                    cell.setCellStyle(style);
                } else {
                    cell.setCellValue(specPrice);
                    cell.setCellStyle(style);
                }

                //Коммисия
                cell = row.createCell(14);
                double commissionPercentage = productArrayList.get(i).getMyCommissionForOzonOrWildberries();
                cell.setCellValue(1 - (commissionPercentage / 100));
                cell.setCellStyle(style);

                //Логистика
                cell = row.createCell(15);
                double myOrderAssemblyForOzon = productArrayList.get(i).getMyOrderAssemblyForOzon();
                double myTrunkForOzon = productArrayList.get(i).getMyTrunkForOzon();
                double myLastMileForOzon = productArrayList.get(i).getMyLastMileForOzonOrWildberries();
                double logistic = myOrderAssemblyForOzon + myTrunkForOzon + myLastMileForOzon;
                cell.setCellValue(logistic);
                cell.setCellStyle(style);

                //Наша пороговая цена
                cell = row.createCell(16);
                int criticPrice = (int) Math.round(myLowerPrice * (1 - (commissionPercentage / 100)) - logistic);
                cell.setCellValue(criticPrice);
                if (specPrice < criticPrice) {
                    cell.setCellStyle(style);
                } else {
                    cell.setCellStyle(styleRedCell);
                }

                //Заработок
                cell = row.createCell(17);
                double earnings = Math.round((specPrice / criticPrice - 1) * 100);
                cell.setCellValue(earnings);
                if (earnings >= 0 && earnings <= 5) {
                    cell.setCellStyle(styleMyProduct);
                } else if (earnings > 5) {
                    cell.setCellStyle(styleRedCell);
                } else {
                    cell.setCellStyle(style);
                }

                //Рекомендуемая роз. цена
                cell = row.createCell(18);
                cell.setCellValue(recommendLowerPrice);
                if (competitorLowerPrice == myLowerPrice) {
                    cell.setCellStyle(style);
                } else if (competitorLowerPrice < myLowerPrice) {
                    cell.setCellStyle(styleRoseCell);
                } else {
                    cell.setCellStyle(styleGreenCell);
                }

                //Спец-акция
                //            cell = row.createCell(10);
                //            String specAction = productArrayList.get(i).getMySpecAction();
                //            if (specAction.equals(Constants.NOT_FOUND_HTML_ITEM)){
                //                cell.setCellValue("-");
                //                cell.setCellStyle(style);
                //            } else {
                //                cell.setCellValue(specAction);
                //                cell.setCellStyle(styleRedCell);
                //            }

                //Моя базовая цена
                cell = row.createCell(20);
                int myPriceU = Math.round(productArrayList.get(i).getMyPriceU() / 100);
                cell.setCellValue(myPriceU);
                cell.setCellStyle(style);


                //Моя тек. роз. цена
                cell = row.createCell(21);
                cell.setCellValue(myLowerPrice);
                if (competitorLowerPrice == myLowerPrice) {
                    cell.setCellStyle(style);
                } else if (competitorLowerPrice < myLowerPrice) {
                    cell.setCellStyle(styleRoseCell);
                } else {
                    cell.setCellStyle(styleGreenCell);
                }

                //установка картинки для моего товара
                String myImageUrl = productArrayList.get(i).getMyRefForImage();
                if (myImageUrl != null) {
                    if (!myImageUrl.equals("-")) {
                        System.out.println("Моя картинка url = " + myImageUrl);
                        setImageForCell(myImageUrl, 5, i, 0.3, 0.3, myVendorCode);
                    }
                }
                myImageUrl = "-";

                //установка картинки для конкурента
                String imageUrl = productArrayList.get(i).getCompetitorRefForImage();
                if (imageUrl != null) {
                    if (!imageUrl.equals("-")) {
//                        System.out.println("Картинка конкурента url = " + imageUrl);
                        setImageForCell(imageUrl, 10, i, 0.3, 0.3, myVendorCode);
                    }
                }
                imageUrl = "-";

                this.updateProgress(i + 1, countRows);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "AnalyticForOzon.xlsx";

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            System.out.println("Запись файла");
            workbook.write(outputStream);
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return currDir;
    }

    private void setImageForCell(String url, int columnIndex, int rowIndex, double scaleX, double scaleY, String myVendorCode) {
        try {
            byte[] bytes = Jsoup.connect(url)
                    .userAgent("Chrome")
                    .timeout(30000)
                    .ignoreContentType(true)
                    .execute().bodyAsBytes();

            int size = bytes.length;

            FileOutputStream file = new FileOutputStream("test.webp");
            file.write(bytes);
            file.close();

            //the "dwebp.exe" path
            String str1 = "C:/Users/User/IdeaProjects/ParserFX/libwebp-0.4.2-windows-x64/bin/dwebp.exe";
            //the webp picture path
            String str2 = "C:/Users/User/IdeaProjects/ParserFX/test.webp";
            //the converted picture path
            String str3 = "C:/Users/User/IdeaProjects/ParserFX/converted.png";
            String[] args = new String[]{str1, str2, "-o", str3};

            try {
                Runtime.getRuntime().exec(args);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Thread.sleep(500);

            File convertedFile = new File("converted.png");

            BufferedImage image = ImageIO.read(convertedFile);
            int h = image.getHeight();
            //int w = image.getWidth();

            double relatively = (double) 250/h;
            System.out.println("Отношение высоты картинки к 250 pix = " + relatively);
            if (h > 250) {
                scaleX = relatively * scaleX;
                scaleY = relatively * scaleY;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
            ImageIO.write(image, "png", baos);
            baos.flush();

            String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
            baos.close();

            byte[] resByteArray = Base64.getDecoder().decode(base64String);

            int size2 = resByteArray.length;

            int pictureIdx = workbook.addPicture(resByteArray, Workbook.PICTURE_TYPE_PNG);

            helper = workbook.getCreationHelper();
            drawing = sheet.createDrawingPatriarch();
            anchor = helper.createClientAnchor();

            anchor.setCol1(columnIndex);
            anchor.setRow1(rowIndex + 1);

            Picture pict = drawing.createPicture(anchor, pictureIdx);

            pict.resize(scaleX, scaleY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected File call() throws Exception {
        return this.writeWorkbook(resultProductHashMap);
    }
}
