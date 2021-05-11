import org.apache.poi.ss.usermodel.Sheet;

public interface ReaderExcel {
    void getDataFromExcelFile(Sheet sheetReport);
    boolean checkExcelFile(Sheet sheet);
}
