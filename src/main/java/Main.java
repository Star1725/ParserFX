import controllers.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Main extends Application implements Controller.ActionInController {

    private Controller controller;

    ExelTask exelTask;

    ParserWildBer parserWildBer = new ParserWildBer();

    private ExecutorService es;

    private static final Desktop desktop = Desktop.getDesktop();
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
        exelTask = new ExelTask(file);

        controller.getAreaLog().appendText("Чтение файла \"" + file.getName() + "\"");

        controller.getProgressBar().setProgress(0);
        // Unbind progress property
        controller.getProgressBar().progressProperty().unbind();
        // Bind progress property
        controller.getProgressBar().progressProperty().bind(exelTask.progressProperty());


        // When completed tasks
        exelTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                resultMap = exelTask.getValue();
                exelTask.cancel(true);
                controller.getAreaLog().appendText(" - ok!\n");

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.getProgressBar().setProgress(0);
                    }
                });
                controller.getAreaLog().appendText("анализ артикулов:\n");
                // Unbind progress property
                controller.getProgressBar().progressProperty().unbind();

                getResultProduct(resultMap);

            }
        });

        new Thread(exelTask).start();

        int countRows = resultMap.size();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
//                    String key = entry.getKey();
//                    String category = entry.getValue().getCategory();
//                    String brand = entry.getValue().getBrand();
//                    Product product = ParserWildBer.getResultProduct(key, category, brand);
//
////            Platform.runLater(new Runnable() {
////                @Override
////                public void run() {
////                    controller.getAreaLog().appendText("артикул: " + key + " - " + "\n");
////                }
////            });
//
//                    System.out.println("key = " + key);
//                    ResultProduct resultProduct = entry.getValue();
//
//                    if (product == null){
//                        resultProduct.setProductName("-");
//                    } else {
//                        //resultProduct.setCategory(category);
//                        resultProduct.setVendorCode(product.getVendorCode());
//                        resultProduct.setProductName(product.getProductName());
//                        resultProduct.setRefForPage(product.getRefForPage());
//                        resultProduct.setRefForImage(product.getRefForImage());
//                        resultProduct.setPriceU(product.getPriceU());
//                        resultProduct.setBasicSale(product.getBasicSale());
//                        resultProduct.setBasicPriceU(product.getBasicPriceU());
//                        resultProduct.setPromoSale(product.getPromoSale());
//                        resultProduct.setPromoPriceU(product.getPromoPriceU());
//                        resultProduct.setSpecAction(product.getSpecAction());
//                        resultProduct.setRating(product.getRating());
//                        resultProduct.setRefFromRequest(product.getRefFromRequest());
//
//                        //установка рекомендуемой скидки и розничной цены на основании процента демпинга
//                        double preFld = Double.parseDouble(controller.percentTxtFld.getText());
//
//                        double present = 1 - preFld / 100;
//
//                        if (resultProduct.getMyVendorCode().equals(resultProduct.getVendorCode())){
//                            resultProduct.setRecommendedPriceU(resultProduct.getMyPromoPriceU());
//                            resultProduct.setRecommendedSale(resultProduct.getMyLowerSale());
//                        } else {
//                            resultProduct.setRecommendedPriceU((int) Math.round(resultProduct.getLowerPriceU() * present));
//                            resultProduct.setRecommendedSale((100 - (int)Math.round((double) resultProduct.getRecommendedPriceU() / resultProduct.getMyPriceU() * 100)));
//                        }
//                    }
//                }
//                File file1 = ExelTask.writeWorkbook(resultMap);
//                openFile(file1);
//            }
//        }).start();
    }

    private static void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getResultProduct(Map<String, ResultProduct> resultMap){
        es = Executors.newFixedThreadPool(4);

        java.util.List<MyCall> myCalls = new ArrayList<>();

        for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
            String key = entry.getKey();
            String category = entry.getValue().getCategory();
            String brand = entry.getValue().getBrand();

            myCalls.add(new MyCall(key, category, brand));
        }

        List<Future<Product>> futureList = null;
        try {
            futureList = es.invokeAll(myCalls);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Future<Product> productFuture : futureList) {

            try {
                String myVendorCode = productFuture.get().getVendorCodeFromRequest();
                String vendorCode = productFuture.get().getVendorCode();
                String productName = productFuture.get().getProductName();
                String refForPage = productFuture.get().getRefForPage();
                String refForImage = productFuture.get().getRefForImage();
                int priceU = productFuture.get().getPriceU();
                int basicSale = productFuture.get().getBasicSale();
                int basicPriceU = productFuture.get().getBasicPriceU();
                int promoSale = productFuture.get().getPromoSale();
                int promoPriceU = productFuture.get().getPromoPriceU();
                String specAction = productFuture.get().getSpecAction();
                int rating = productFuture.get().getRating();

                ResultProduct resultProductTemp = resultMap.get(myVendorCode);

                resultProductTemp.setVendorCode(vendorCode);
                resultProductTemp.setProductName(productName);
                resultProductTemp.setRefForPage(refForPage);
                resultProductTemp.setRefForImage(refForImage);
                resultProductTemp.setPriceU(priceU);
                resultProductTemp.setBasicSale(basicSale);
                resultProductTemp.setBasicPriceU(basicPriceU);
                resultProductTemp.setPromoSale(promoSale);
                resultProductTemp.setPromoPriceU(promoPriceU);
                resultProductTemp.setSpecAction(specAction);
                resultProductTemp.setSpecAction(specAction);
                resultProductTemp.setRating(rating);

                //установка рекомендуемой скидки и розничной цены на основании процента демпинга
                double preFld = Double.parseDouble(controller.percentTxtFld.getText());

                double present = 1 - preFld / 100;

                if (myVendorCode.equals(vendorCode)){
                    resultProductTemp.setRecommendedPriceU(resultProductTemp.getMyPromoPriceU());
                    resultProductTemp.setRecommendedSale(resultProductTemp.getMyLowerSale());
                } else {
                    resultProductTemp.setRecommendedPriceU((int) Math.round(resultProductTemp.getLowerPriceU() * present));
                    resultProductTemp.setRecommendedSale((100 - (int)Math.round((double) resultProductTemp.getRecommendedPriceU() / resultProductTemp.getMyPriceU() * 100)));
                }

                resultMap.put(resultProductTemp.getMyVendorCode(), resultProductTemp);

            } catch (InterruptedException | ExecutionException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        es.shutdown();
        openFile(ExelTask.writeWorkbook(resultMap));
    }

    //колобэл, который выполняет запросы на wildberries
    static class MyCall implements Callable<Product> {

        String key;
        String category;
        String brand;

        public MyCall(String key, String category, String brand) {
            this.key = key;
            this.category = category;
            this.brand = brand;
        }

        @Override
        public Product call() throws Exception {
            return ParserWildBer.getProduct(key, category, brand);
        }
    }
}
