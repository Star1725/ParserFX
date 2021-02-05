import controllers.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application implements Controller.ActionInController {

    private Controller controller;
    private static final Desktop desktop = Desktop.getDesktop();
    private List<String> listQuery;
    private List<Product> productList;
    private static Map<String, ResultProduct> resultMap = new LinkedHashMap<>();

    private static final String NOT_FOUND_PAGE = "Не найдена страница товара";

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


        for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {


            String key = entry.getKey();
            //controller.getAreaLog().appendText(key.toString() + "\n");
            String category = entry.getValue().getCategory();
            String brand = entry.getValue().getBrand();
            Product product = ParserWildBer.getResultProduct(key, category, brand);
            System.out.println("key = " + key);
            ResultProduct resultProduct = entry.getValue();

            if (product == null){
                resultProduct.setProductName("-");
            } else {
                //resultProduct.setCategory(category);
                resultProduct.setVendorCode(product.getVendorCode());
                resultProduct.setProductName(product.getProductName());
                resultProduct.setRefForPage(product.getRefForPage());
                resultProduct.setRefForImage(product.getRefForImage());
                resultProduct.setPriceU(product.getPriceU());
                resultProduct.setBasicSale(product.getBasicSale());
                resultProduct.setBasicPriceU(product.getBasicPriceU());
                resultProduct.setPromoSale(product.getPromoSale());
                resultProduct.setPromoPriceU(product.getPromoPriceU());
                resultProduct.setSpecAction(product.getSpecAction());
                resultProduct.setRating(product.getRating());
                resultProduct.setRefFromRequest(product.getRefFromRequest());

                //установка рекомендуемой скидки и розничной цены на основании процента демпинга
                int preFld = Integer.parseInt(controller.percentTxtFld.getText());

                double present = 1 - (double) preFld / 100;

                //double present = Double.parseDouble(controller.percentTxtFld.getText()) / 100;

                if (resultProduct.getMyVendorCode().equals(resultProduct.getVendorCode())){
                    resultProduct.setRecommendedPriceU(resultProduct.getMyPromoPriceU());
                    resultProduct.setRecommendedSale(resultProduct.getMyLowerSale());
                } else {

                    resultProduct.setRecommendedPriceU((int) Math.round(resultProduct.getLowerPriceU() * present));
                    resultProduct.setRecommendedSale((100 - (int)Math.round((double) resultProduct.getRecommendedPriceU() / resultProduct.getMyPriceU() * 100)));
                }
            }
        }
        File file1 = ExelHandler.writeWorkbook(resultMap);
        openFile(file1);
    }

    private static void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
