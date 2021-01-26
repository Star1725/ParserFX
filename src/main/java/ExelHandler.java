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

    public static Map<Integer, ResultProduct> readWorkbook(File file) {
        try {
            Map<Integer, ResultProduct> resultProductHashMap = new HashMap<>();
            Workbook workbook = new XSSFWorkbook(file);

            //получаем страницу
            Sheet sheet = workbook.getSheetAt(0);

            Map<Integer, List<String>> data = new HashMap<>();
            int i = 0;

            Iterator rowIter = sheet.rowIterator();
            while (rowIter.hasNext()) {
                String strQuery = "";

                data.put(i, new ArrayList<String>());

                //получаем строку
                Row row = (Row) rowIter.next();
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING:
                            data.get(new Integer(i)).add(cell.getRichStringCellValue().getString());
                            strQuery = strQuery + cell.getRichStringCellValue().getString() + " ";
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                data.get(i).add(cell.getDateCellValue() + "");
                            } else {
                                data.get(i).add(cell.getNumericCellValue() + "");
                            }
                            break;
                        case BOOLEAN:
                            data.get(i).add(cell.getBooleanCellValue() + "");
                            break;
                        case FORMULA:
                            data.get(i).add(cell.getCellFormula() + "");
                            break;
                    }
                }

//                Iterator cellIter = row.cellIterator();
//                while (cellIter.hasNext()) {
//                    //получаем ячейку
//                    Cell cell = (Cell) cellIter.next();
//                    strQuery = strQuery + cell.getRichStringCellValue().getString() + " ";
//                }
                i++;
                listQuery.add(strQuery);
            }
            return listQuery;
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
