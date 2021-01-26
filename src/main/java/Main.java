import controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application implements Controller.ActionInController {

    private Controller controller;
    private List<String> listQuery;
    private List<Product> productList;
    private static Map<Integer, ResultProduct> resultMap = new LinkedHashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader mainWindowLoader = new FXMLLoader();
        mainWindowLoader.setLocation(getClass().getResource("/sample.fxml"));
        Parent mainRoot = mainWindowLoader.load();
        primaryStage.setTitle("Parser 1.0");
        primaryStage.setScene(new Scene(mainRoot, 700, 350));
        Controller.subscribe(this);
        controller = mainWindowLoader.getController();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void selectFile(File file) {
        resultMap = ExelHandler.readWorkbook(file, controller);


        for (Map.Entry<Integer, ResultProduct> entry : resultMap.entrySet()) {
            Integer key = entry.getKey();
            controller.getAreaLog().appendText(key.toString() + "\n");

            ParserWildBer.getPageForProductName(key);

            //Object value = entry.getValue();
        }


//        for (Integer i : resultMap.get) {
//
//            controller.getAreaLog().appendText(s + "\n");
//
//            try {
//                productList = ParserWildBer.extracted(s);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            for (Product product : productList) {
//                controller.getAreaLog().appendText(product.toString());
//            }
//
//        }
    }
}
