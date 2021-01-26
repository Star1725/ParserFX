import controllers.Controller;
import javafx.application.Platform;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class ExelHandler {

    public static Map<Integer, ResultProduct> readWorkbook(File file, Controller controller) {
        try {
            Map<Integer, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
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
                int vendorCode = (int) cell.getNumericCellValue();
                if (vendorCode == 0){
                    continue;
                }
                //получаем бренд
                cell = row.getCell(0);
                String brand = cell.getRichStringCellValue().getString();
                //получаем розничную цену до скидки
                cell = row.getCell(11);
                double myPrice = cell.getNumericCellValue();
                //получаем текущую скидку
                cell = row.getCell(13);
                int mySale = (int) cell.getNumericCellValue();
                //получаем промо-скидку
                cell = row.getCell(16);
                int myPromoSale = (int) cell.getNumericCellValue();

                double myLowerPrice = 1;
                if (mySale != 0){
                    myLowerPrice = myPrice * mySale;
                }
                if (myPromoSale != 0){
                    myLowerPrice = myLowerPrice * myPromoSale;
                }

                resultProductHashMap.put(vendorCode, new ResultProduct(vendorCode, brand, "productName", "refForPage", "refForImage",
                        0, 0, 0, "specAction", 0,
                        myLowerPrice,
                        myPrice,
                        mySale,
                        myPromoSale,
                        0,
                        0));
//                for (Cell cell : row) {
//                double myPrice = cell.getNumericCellValue();
//
//                    switch (cell.getCellType()) {
//                        case STRING:
//                            data.get(new Integer(i)).add(cell.getRichStringCellValue().getString());
//                            strQuery = strQuery + cell.getRichStringCellValue().getString() + " ";
//                            break;
//                        case NUMERIC:
//                            if (DateUtil.isCellDateFormatted(cell)) {
//                                data.get(i).add(cell.getDateCellValue() + "");
//                            } else {
//                                data.get(i).add(cell.getNumericCellValue() + "");
//                            }
//                            break;
//                        case BOOLEAN:
//                            data.get(i).add(cell.getBooleanCellValue() + "");
//                            break;
//                        case FORMULA:
//                            data.get(i).add(cell.getCellFormula() + "");
//                            break;
//                    }
//                }
//
////                Iterator cellIter = row.cellIterator();
////                while (cellIter.hasNext()) {
////                    //получаем ячейку
////                    Cell cell = (Cell) cellIter.next();
////                    strQuery = strQuery + cell.getRichStringCellValue().getString() + " ";
////                }
//                i++;
//                listQuery.add(strQuery);

//                double load = i/fullLoad;
//                System.out.println(load);
//                Platform.runLater(() -> controller.getProcessingRequest().setProgress(load));
//                i++;
//                Thread.sleep(5);
            }
            return resultProductHashMap;
        }
        catch (Exception e) {
            System.out.println("ошибка при чтении файла .xls");
            return null;
        }
    }



    public static void writeWorkbook(HSSFWorkbook wb, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();
        }
        catch (Exception e) {
            //Обработка ошибки
        }
    }
}
