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

    static ParserWildBer parserWildBer = new ParserWildBer();

    private ExecutorService executorService;

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
                // Unbind progress property
                controller.getProgressBar().progressProperty().unbind();

                controller.getAreaLog().appendText("анализ артикулов:\n");

                getResultProduct(resultMap);

            }
        });

        new Thread(exelTask).start();
    }

    private static void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getResultProduct(Map<String, ResultProduct> resultMap){
        new Thread(new Runnable() {
            @Override
            public void run() {

                executorService = Executors.newFixedThreadPool(4);
                CompletionService<Product> executorCompletionService= new ExecutorCompletionService<>(executorService);

                List<MyCall> myCalls = new ArrayList<>();

                for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
                    String key = entry.getKey();
                    String category = entry.getValue().getCategory();
                    String brand = entry.getValue().getBrand();

                    myCalls.add(new MyCall(key, category, brand));
                }

                List<Future<Product>> futureList = new ArrayList<>();

                for (MyCall myCall : myCalls) {
                    futureList.add(executorCompletionService.submit(myCall));
                }

                for (int i =0; i < futureList.size(); i++) {
                    try {
                        Product resultProduct = executorCompletionService.take().get();

                        String myVendorCode = resultProduct.getVendorCodeFromRequest();
                        String myRefForPage = resultProduct.getMyRefForPage();
                        String mySpecAction = resultProduct.getMySpecAction();
                        String vendorCode = resultProduct.getVendorCode();
                        String productName = resultProduct.getProductName();
                        String refForPage = resultProduct.getRefForPage();
                        String refForImage = resultProduct.getRefForImage();
                        String refFromRequest = resultProduct.getRefFromRequest();
                        int priceU = resultProduct.getPriceU();
                        int basicSale = resultProduct.getBasicSale();
                        int basicPriceU = resultProduct.getBasicPriceU();
                        int promoSale = resultProduct.getPromoSale();
                        int promoPriceU = resultProduct.getPromoPriceU();
                        String specAction = resultProduct.getSpecAction();
                        int rating = resultProduct.getRating();

                        ResultProduct resultProductTemp = resultMap.get(myVendorCode);

                        resultProductTemp.setMyRefForPage(myRefForPage);
                        resultProductTemp.setMySpecAction(mySpecAction);
                        resultProductTemp.setVendorCode(vendorCode);
                        resultProductTemp.setProductName(productName);
                        resultProductTemp.setRefForPage(refForPage);
                        resultProductTemp.setRefForImage(refForImage);
                        resultProductTemp.setRefFromRequest(refFromRequest);
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

                        if (myVendorCode.equals(vendorCode) || vendorCode.equals("-")){
                            resultProductTemp.setRecommendedPriceU(resultProductTemp.getMyPromoPriceU());
                            resultProductTemp.setRecommendedSale(resultProductTemp.getMyBasicSale());
                            resultProductTemp.setRecommendedPromoSale(resultProductTemp.getMyPromoSale());

                        } else {
                            int recommendedPriceU = (int) Math.round(resultProductTemp.getLowerPriceU() * present);
                            resultProductTemp.setRecommendedPriceU(recommendedPriceU);

                            if ((resultProductTemp.getMyBasicSale() == 0 & resultProductTemp.getMyPromoSale() == 0) || (resultProductTemp.getMyBasicSale() == 0 & resultProductTemp.getMyPromoSale() != 0) ){

                                int recommendedSale = 25;
                                int recommendedPromoSale = 100 - (int)Math.round((double) resultProductTemp.getRecommendedPriceU() / (resultProductTemp.getMyPriceU() * 0.75) * 100);
                                if (recommendedPromoSale < 0){
                                    recommendedSale = 100 - (int)Math.round((double) resultProductTemp.getRecommendedPriceU() / (resultProductTemp.getMyPriceU()) * 100);
                                    resultProductTemp.setRecommendedSale(recommendedSale);
                                    resultProductTemp.setRecommendedPromoSale(0);
                                } else {
                                    resultProductTemp.setRecommendedSale(recommendedSale);
                                    resultProductTemp.setRecommendedPromoSale(recommendedPromoSale);
                                }

                            } else if (resultProductTemp.getMyBasicSale() != 0 & resultProductTemp.getMyPromoSale() == 0) {
                                resultProductTemp.setRecommendedSale(resultProductTemp.getMyBasicSale());
                                resultProductTemp.setRecommendedPromoSale((100 - (int)Math.round((double) resultProductTemp.getRecommendedPriceU() / resultProductTemp.getMyBasicPriceU() * 100)));
                            } else if (resultProductTemp.getMyBasicSale() != 0 & resultProductTemp.getMyPromoSale() != 0) {
                                resultProductTemp.setRecommendedSale(resultProductTemp.getMyBasicSale());
                                resultProductTemp.setRecommendedPromoSale((100 - (int)Math.round((double) resultProductTemp.getRecommendedPriceU() / resultProductTemp.getMyBasicPriceU() * 100)));
                            }
                        }

                        resultMap.put(resultProductTemp.getMyVendorCode(), resultProductTemp);

                        int finalI = i;

                        controller.getProgressBar().setProgress(finalI /futureList.size());

                        if (vendorCode.equals("-")){
                            controller.getAreaLog().appendText("- " + myVendorCode + " - ошибка\n");
                        } else {
                            controller.getAreaLog().appendText("- " + myVendorCode + " - ok\n");
                        }
                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                controller.getAreaLog().appendText("Количество проанализированных позицый - " + futureList.size() + "\n");
                executorService.shutdown();
                openFile(ExelTask.writeWorkbook(resultMap));
            }
        }).start();

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
            return parserWildBer.getProduct(key, category, brand);
        }
    }
}
