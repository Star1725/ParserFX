import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.*;

public class TaskWriteExelForWildberries extends Task<File> {

    private Workbook workbook;
    private Sheet sheet;
    private CreationHelper helper;
    private ClientAnchor anchor;
    private Drawing drawing;
    private int countRows;

    TaskWriteExelForWildberries(Map map){
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
        headerCell.setCellValue("Предмет");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Мой арт. поставщика (по 1С)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Мой арт. по WB");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Моя номенкл. по 1С");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Ссылка на мой товар");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue("Моя спецАкция");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("Мои цена (до скидки)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue("Моя тек. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(9);
        headerCell.setCellValue("результат запроса");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(10);
        headerCell.setCellValue("доп. парам. поиска");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(11);
        headerCell.setCellValue("ссылка на результат запроса");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(12);
        headerCell.setCellValue("Наименование товара конкур.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(13);
        headerCell.setCellValue("Ссылка на конкурента");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(14);
        headerCell.setCellValue("Имя продавца");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(15);
        headerCell.setCellValue("Участие в акции");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(16);
        headerCell.setCellValue("Тек. роз. цена конкур.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(17);
        headerCell.setCellValue("Комиссия (%)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(18);
        headerCell.setCellValue("Логистика");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(19);
        headerCell.setCellValue("Спец-цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(21);
        headerCell.setCellValue("Реком. роз. цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(20);
        headerCell.setCellValue("Наша пороговая цена");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(22);
        headerCell.setCellValue("В случае повышения - новая реком. розн. цена (до скидки)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(23);
        headerCell.setCellValue("Реком. согласованная скидка");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(24);
        headerCell.setCellValue("Реком. новая скидка по промокоду");
        headerCell.setCellStyle(headerStyle);

        //настройка автоширины ячейки по содержимому
        for (int columnIndex = 0; columnIndex < 25; columnIndex++) {
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

            boolean isMy = productArrayList.get(i).getMyVendorCodeForWildberiesOrOzon().equals(productArrayList.get(i).getCompetitorVendorCode());

            boolean isMyAnalog = myVendorCodesSet.contains(productArrayList.get(i).getCompetitorVendorCode());

            System.out.println();
            System.out.println("Начало записи строки - " + (i + 1));

            //Бренд
            Cell cell = row.createCell(0);
            cell.setCellValue(productArrayList.get(i).getMyBrand());
            if (productArrayList.get(i).getMyBrand().equals("xivi") || productArrayList.get(i).getMyBrand().equals("mietubl")){
                cell.setCellStyle(styleBlueCell);//настройка для игнорируемых брендов "xivi" и "mietubl"
            } else {
                cell.setCellStyle(style);
            }

            //Категория товара
            cell = row.createCell(1);
            String category = productArrayList.get(i).getCategory();
            cell.setCellValue(category);
            cell.setCellStyle(style);

//Мой артикул поставщика(по 1С)
            cell = row.createCell(2);
            if (productArrayList.get(i).getIsFind() == 0) {
                System.out.println(productArrayList.get(i).getCode_1C() + " - не найден в базе 1С");
                cell.setCellValue(productArrayList.get(i).getCode_1C() + " - не найден в базе 1С");
                cell.setCellStyle(styleRedCell);
            }
            if (productArrayList.get(i).getIsFind() == 1) {
                System.out.println(productArrayList.get(i).getCode_1C());
                cell.setCellValue(productArrayList.get(i).getCode_1C());
                cell.setCellStyle(style);
            }

            //Мой артикул по Wildberies
            cell = row.createCell(3);
            String vendorCodeWildberries = productArrayList.get(i).getMyVendorCodeForWildberiesOrOzon();
            String competitorVendorCodeWildberries = "-";
            try {
                competitorVendorCodeWildberries = productArrayList.get(i).getCompetitorVendorCode();
            } catch (Exception ignored) {
            }
            cell.setCellValue(vendorCodeWildberries);
            if (isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else if (!isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else {
                cell.setCellStyle(style);
            }

            //моё наименование товара
            cell = row.createCell(4);
            cell.setCellValue(productArrayList.get(i).getMyNomenclature_1C());
            if (isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else if (!isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else {
                cell.setCellStyle(style);
            }

            //ссылка на мой товар
            cell = row.createCell(5);
            try {
                cell.setCellValue((productArrayList.get(i).getMyRefForPage()));
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }
            if (isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else if (!isMy && isMyAnalog){
                cell.setCellStyle(styleMyProduct);
            } else {
                cell.setCellStyle(style);
            }

            //моя спец-акция
            cell = row.createCell(6);
            try {
                String mySpecAction = productArrayList.get(i).getMySpecAction();
                if (mySpecAction.equals("-")){
                    cell.setCellValue(mySpecAction);
                    cell.setCellStyle(style);
                } else {
                    cell.setCellValue(mySpecAction);
                    cell.setCellStyle(styleRedCell);
                }
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Моя базовая цена
            cell = row.createCell(7);
            try {
                int myPriceU = Math.round(productArrayList.get(i).getMyPriceU() / 100);
                cell.setCellValue(myPriceU);
                cell.setCellStyle(style);
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //расчитываем все необходимые цены
            int myLowerPrice = 0;
            int competitorLowerPrice = 0;
            int specPrice = 0;
            try {
                myLowerPrice = Math.round(productArrayList.get(i).getMyLowerPriceU() / 100);
                competitorLowerPrice = Math.round(productArrayList.get(i).getCompetitorLowerPriceU() / 100);
                specPrice = productArrayList.get(i).getSpecPrice() / 100;
            } catch (Exception ignored) {
            }
            //int logistic = 33;

            //Моя тек. роз. цена
            cell = row.createCell(8);
            try {
                cell.setCellValue(myLowerPrice);
                if (competitorLowerPrice == myLowerPrice){
                    cell.setCellStyle(style);
                } else if (competitorLowerPrice < myLowerPrice){
                    cell.setCellStyle(styleRoseCell);
                } else {
                    cell.setCellStyle(styleGreenCell);
                }
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //результат поискового запроса
            cell = row.createCell(9);
            try {
                cell.setCellValue(productArrayList.get(i).getResultForSearch());
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }
            cell.setCellStyle(style);

            //дополнительный параметр поиска
            cell = row.createCell(10);
            if (productArrayList.get(i).getArrayListParams() != null){
                if (productArrayList.get(i).getArrayListParams().size() == 0){
                    cell.setCellValue("Дополнит. парам. поиска \n\r не найдены");
                } else {
                    String result = "";
                    for (List<String> stringList: productArrayList.get(i).getArrayListParams()){
                        for (String param: stringList){
                            result = result + param + ", ";
                        }
                        result = result + "\n\r";
                    }
                    cell.setCellValue(result);
                }
                cell.setCellStyle(style);
            }

            //ссылка на результат поискового запроса
            cell = row.createCell(11);
            try {
                cell.setCellValue(productArrayList.get(i).getRefUrlForResultSearch());
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }
            cell.setCellStyle(style);

            //наименование продукта конкурента(если нашли)
            cell = row.createCell(12);
            try {
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
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //ссылка на конкурента, или самого себя, или мой аналогичный товар
            cell = row.createCell(13);
            try {
                cell.setCellValue(productArrayList.get(i).getCompetitorRefForPage());
                if (isMy && isMyAnalog){
                    cell.setCellStyle(styleMyProduct);
                } else if (!isMy && isMyAnalog){
                    cell.setCellStyle(styleMyProductAnalog);
                } else {
                    cell.setCellStyle(style);
                }
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Имя продавца
            cell = row.createCell(14);
            try {
                cell.setCellValue(productArrayList.get(i).getCompetitorName());
                cell.setCellStyle(style);
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Спец-акция конкурента
            cell = row.createCell(15);
            try {
                String specAction = productArrayList.get(i).getCompetitorSpecAction();
                if (specAction.equals("-")){
                    cell.setCellValue(specAction);
                    cell.setCellStyle(style);
                } else {
                    cell.setCellValue(specAction);
                    cell.setCellStyle(styleRedCell);
                }
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Тек. роз. цена конкурента
            cell = row.createCell(16);
            try {
                cell.setCellValue(competitorLowerPrice);
                if (competitorLowerPrice == myLowerPrice){
                    cell.setCellStyle(style);
                } else if (competitorLowerPrice < myLowerPrice){
                    cell.setCellStyle(styleRoseCell);
                } else {
                    cell.setCellStyle(styleGreenCell);
                }
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Коммисия
            cell = row.createCell(17);
            double commissionPercentage = 0;
            try {
                commissionPercentage = productArrayList.get(i).getMyCommissionForOzonOrWildberries();
                cell.setCellValue(commissionPercentage);
                cell.setCellStyle(style);
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Логистика
            cell = row.createCell(18);
            double logistic = 0;
            try {
                logistic = productArrayList.get(i).getMyLastMileForOzonOrWildberries();
                cell.setCellValue(logistic);
                cell.setCellStyle(style);
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Спец-цена
            cell = row.createCell(19);
            if (specPrice == 0){
                cell.setCellValue("н/д");
                cell.setCellStyle(style);
            } else {
                cell.setCellValue(specPrice);
                cell.setCellStyle(style);
            }

            //Рекомендуемая роз. цена
            cell = row.createCell(20);
            int recommendLowerPrice = 0;
            try {
                recommendLowerPrice = Math.round(productArrayList.get(i).getRecommendedMyLowerPrice() / 100);
                cell.setCellValue(recommendLowerPrice);
                if (competitorLowerPrice == myLowerPrice){
                    cell.setCellStyle(style);
                } else if (competitorLowerPrice < myLowerPrice){
                    cell.setCellStyle(styleRoseCell);
                } else {
                    cell.setCellStyle(styleGreenCell);
                }
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Наша пороговая цена
            cell = row.createCell(21);
            try {
                int criticPrice = (int) Math.round(recommendLowerPrice * commissionPercentage - logistic);
                cell.setCellValue(criticPrice);
                if (specPrice < criticPrice){
                    cell.setCellStyle(style);
                } else {
                    cell.setCellStyle(styleRedCell);
                }
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //Если баз. скидка < 3 или > 90%, то новая реком. розн. цена (до скидки)
            cell = row.createCell(22);
            try {
                cell.setCellValue(Math.round(productArrayList.get(i).getRecommendedPriceU()/100));
                cell.setCellStyle(style);
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //"Реком. согласованная скидка"
            cell = row.createCell(23);
            try {
                cell.setCellValue((productArrayList.get(i).getRecommendedBasicSale()));
                cell.setCellStyle(style);
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //"Реком. новая скидка по промокоду"
            cell = row.createCell(24);
            try {
                cell.setCellValue((productArrayList.get(i).getRecommendedPromoSale()));
                cell.setCellStyle(style);
            } catch (Exception ignored) {
                cell.setCellValue("-");
            }

            //установка картинки для моего товара
            String myImageUrl = "-";
            try {
                myImageUrl = productArrayList.get(i).getMyRefForImage();
                if (!myImageUrl.equals("-")){
                    setImageForCell(myImageUrl, 5, i, 0.0625, 0.0625, vendorCodeWildberries);
                }
            } catch (Exception ignored) {
            }

            //установка картинки для конкурента imageUrl
            String imageUrl = "-";
            try {
                imageUrl = productArrayList.get(i).getCompetitorRefForImage();
                if (!imageUrl.equals("-")){
                    setImageForCell(imageUrl, 10, i, 0.25, 0.25, competitorVendorCodeWildberries);
                }
            } catch (Exception ignored) {
            }
            System.out.println();
            System.out.println("Конец записи строки - " + (i + 1));
            this.updateProgress(i + 1, countRows);
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "AnalyticForWB.xlsx";

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            System.out.println("Запись данных в файл Excel");
            workbook.write(outputStream);
            workbook.close();
            System.out.println("Запись закончена!");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return currDir;
    }

    private void setImageForCell(String url, int columnIndex, int rowIndex, double scaleX, double scaleY, String vendorCodeWildberries) {
        try {
            System.out.println("Загрузка картинки для артикула WB = " + vendorCodeWildberries);

            byte[] bytes = Jsoup.connect(url).timeout(30000).ignoreContentType(true).execute().bodyAsBytes();


////////Чтение из файла на диске////////////////////////////////////////////////////////////////////////////////////////

//            com.sun.image.codec.jpeg.JPEGImageDecoder jpegDecoder =  JPEGCodec.createJPEGDecoder (new FileInputStream("test1.jpg"));
//
//            BufferedImage image5 = jpegDecoder.decodeAsBufferedImage();
//
//            URL url3 = new URL("https://images.wbstatic.net/big/new/15110000/15119113-1.jpg");
////            URL url3 = new URL("https://cdn1.ozone.ru/s3/multimedia-5/wc250/6015239165.jpg");
//            BufferedImage img = ImageIO.read(url3);
//            File file3 = new File("downloaded.jpg");
//            ImageIO.write(img, "jpg", file3);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
//            BufferedImage image = ImageIO.read(new File("downloaded.jpg"));
//
//            // явно указываем расширение файла для простоты реализации
//            ImageIO.write(image, "jpg", baos);
//            baos.flush();
//
////            String base64String = Base64.encode(baos.toByteArray());
//            String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
//            baos.close();
//
//            // декодируем полученную строку в массив байт
////            byte[] resByteArray = Base64.decode(base64String);
//            byte[] resByteArray = Base64.getDecoder().decode(base64String);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);

            helper = workbook.getCreationHelper();
            anchor = helper.createClientAnchor();
            drawing = sheet.createDrawingPatriarch();

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
