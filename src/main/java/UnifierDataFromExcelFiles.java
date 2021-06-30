import javafx.concurrent.Task;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UnifierDataFromExcelFiles extends Task<Map>{
    static int countReadsRows;
    static int countReadsRows_1C;
    static int countFull;

    int marketPlaceFlag;

    Workbook workbookReport;
    Workbook workbook_1C;

    Sheet sheetReport;
    Sheet sheet_1C;

    static final Map<String, ResultProduct> resultProductHashMap = new LinkedHashMap<>();
    static Map<String, Supplier> supplierSpecPriceHashMapWithKeyCode_1C = new HashMap<>();
    static Map<String, Integer> mapCountForMyProductName = new HashMap<>();

    ReaderExcel readerExcel;
    ReaderExcelFor_1C readerExcelFor_1C = new ReaderExcelFor_1C(this);

    public UnifierDataFromExcelFiles(List<File> files, int marketPlaceFlag) {
        this.files = files;
        this.marketPlaceFlag = marketPlaceFlag;
        if (marketPlaceFlag == 1){readerExcel = new ReaderExcelOzon(this);}
        else if (marketPlaceFlag == 2){readerExcel = new ReaderExcelWildberries(this);}
    }

    private final List<File> files;

    public Map<String, ResultProduct> readWorkbook(List<File> files) {

        //читаем файл отчёта и файл 1С
        System.out.println("читаем файл отчёта и файл 1С");
        try {
            workbookReport = new XSSFWorkbook(files.get(0));
            workbook_1C = new XSSFWorkbook(files.get(1));
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

        //получаем страницы
        System.out.println("получаем страницы");
        if (marketPlaceFlag == 1){
            try {
                sheetReport = workbookReport.getSheetAt(1);
                sheet_1C = workbook_1C.getSheetAt(0);
            } catch (Exception e) {
                sheetReport = workbook_1C.getSheetAt(1);
                sheet_1C = workbookReport.getSheetAt(0);
            }
        } else if (marketPlaceFlag == 2){
            sheetReport = workbookReport.getSheetAt(0);
            sheet_1C = workbook_1C.getSheetAt(0);
        }

        //проверяем, правильно ли мы прочитали файлы
        System.out.println("проверяем, правильно ли мы прочитали файлы");
        if (!readerExcel.checkExcelFile(sheetReport) || !readerExcelFor_1C.checkFile_1C(sheet_1C)) {
            Sheet sheetBuff = sheet_1C;
            sheet_1C = sheetReport;
            sheetReport = sheetBuff;
            if (!readerExcel.checkExcelFile(sheetReport) || !readerExcelFor_1C.checkFile_1C(sheet_1C)) {
                System.out.println("ошибка чтения файлов Excel. Проверьте правильность написания названий столбцов, и их очерёдность\n" + "");
                resultProductHashMap.put("Ошибка чтения файла Excel с остатками Ozon", null);
                return resultProductHashMap;
            }
        }

        //считаем кол-во строк в файлах для работы ProgressBar
        System.out.println("считаем кол-во строк в файлах для работы ProgressBar");
        int countRowsInReport = sheetReport.getLastRowNum();
        int countRowsIn_1C = sheet_1C.getLastRowNum();
        countFull = countRowsInReport + countRowsIn_1C;
        System.out.println("countRowsInReport = " + countRowsInReport);
        System.out.println("countRowsIn_1C = " + countRowsIn_1C);


        readerExcel.getDataFromExcelFile(sheetReport);
        readerExcelFor_1C.getDataFromBase_1C(sheet_1C, marketPlaceFlag);

//пытаемся привязать specPrice_1C и productName к ResultProduct
        for (Map.Entry<String, ResultProduct> entry : resultProductHashMap.entrySet()) {
            String key = entry.getKey();
            String code_1C = entry.getValue().getCode_1C();
            Supplier supplier1 = supplierSpecPriceHashMapWithKeyCode_1C.get(code_1C);
            if (supplier1 != null){

                entry.getValue().setIsFind(1);//
                entry.getValue().setSpecPrice(supplier1.getSpecPrice());//
                entry.getValue().setMyBrand(supplier1.getMyBrand());//
                entry.getValue().setMyProductModel(supplier1.getMyProductModel());//
                entry.getValue().setArrayListParams((ArrayList<String>) supplier1.getArrayListParams());//
                entry.getValue().setMyNomenclature_1C(supplier1.getNomenclature());//
                entry.getValue().setSpecQuerySearchForWildberiesOrOzon(supplier1.getSpecQuerySearch());//
                entry.getValue().setProductType(supplier1.getProductType());//

                if (marketPlaceFlag == 2){
                    entry.getValue().setMyCommissionForOzonOrWildberries(supplier1.getCommission());
                    entry.getValue().setMyLastMileForOzonOrWildberries(supplier1.getDelivery());
                }

                String brand = entry.getValue().getMyBrand();
                String productModel = entry.getValue().getMyProductModel();
                int countProducts = mapCountForMyProductName.get(brand + " " + productModel);
                entry.getValue().setCountMyProductModel(countProducts);
                System.out.println("Для " + brand + " " + productModel + " кол-во совпадений - " + countProducts);
            } else {
                entry.getValue().setSpecPrice(0);
            }
        }
        System.out.println("Добавили в итоговый мап все ResultProducts");
        System.out.println();
        return resultProductHashMap;

    }

    @Override
    protected Map call() throws Exception {
        return this.readWorkbook(files);
    }
}
