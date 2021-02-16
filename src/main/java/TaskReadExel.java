import controllers.Controller;
import javafx.concurrent.Task;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class TaskReadExel extends Task<Map> {

    public TaskReadExel(File file) {
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

    @Override
    protected Map call() throws Exception {
        return this.readWorkbook(file);
    }
}
