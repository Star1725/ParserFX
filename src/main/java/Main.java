import controllers.Controller;
import javafx.application.Application;
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

    TaskReadExcel taskReadExcel;
    TaskReadExcelForOzon taskReadExcelForOzon;
    TaskWriteExel taskWriteExel;

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
    public void selectFile(List<File> files) {
//        taskReadExcel = new TaskReadExcel(files);
        taskReadExcelForOzon = new TaskReadExcelForOzon(files);

        if (files.size() == 1){
            controller.getAreaLog().appendText("Чтение файла \"" + files.get(0).getName() + "\"");
        } else if (files.size() == 2){
            controller.getAreaLog().appendText("Чтение файлов \"" + files.get(0).getName() + "\" и \"" + files.get(1).getName() + "\"");
        }

        controller.getProgressBar().setProgress(0);
        // Unbind progress property
        controller.getProgressBar().progressProperty().unbind();
        // Bind progress property
//        controller.getProgressBar().progressProperty().bind(taskReadExcel.progressProperty());
        controller.getProgressBar().progressProperty().bind(taskReadExcelForOzon.progressProperty());

        // When completed taskReadExcel
//        taskReadExcel.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                resultMap = taskReadExcel.getValue();
//                taskReadExcel.cancel(true);
//                controller.getAreaLog().appendText(" - ok!\n");
//                controller.getAreaLog().appendText("Объём анализа - " + resultMap.size() + " позиций\n");
//                controller.getProgressBar().progressProperty().unbind();
//
//                controller.getAreaLog().appendText("Анализ артикулов:\n");
//
//                getResultProduct(resultMap);
//            }
//        });
//        new Thread(taskReadExcel).start();

        // When completed taskReadExcelForOzon
        taskReadExcelForOzon.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                resultMap = taskReadExcelForOzon.getValue();
                taskReadExcelForOzon.cancel(true);
                controller.getAreaLog().appendText(" - ok!\n");
                controller.getAreaLog().appendText("Объём анализа - " + resultMap.size() + " позиций\n");
                controller.getProgressBar().progressProperty().unbind();

                controller.getAreaLog().appendText("Анализ артикулов:\n");

                getResultProduct(resultMap);
            }
        });
        new Thread(taskReadExcelForOzon).start();
    }

    private static void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getResultProduct(Map<String, ResultProduct> resultMap){

        taskWriteExel = new TaskWriteExel(resultMap);

        controller.getProgressBar().progressProperty().bind(taskWriteExel.progressProperty());

        taskWriteExel.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                openFile(taskWriteExel.writeWorkbook(resultMap));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                executorService = Executors.newFixedThreadPool(4);
                CompletionService<Product> executorCompletionService= new ExecutorCompletionService<>(executorService);

                List<MyCall> myCalls = new ArrayList<>();

                for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
                    String key = entry.getKey();
                    String category = entry.getValue().getCategory();
                    String querySearch = entry.getValue().getQueryForSearch();
                    String brand = entry.getValue().getCompetitorBrand();

                    myCalls.add(new MyCall(key, category ,querySearch, brand));
                }

                List<Future<Product>> futureList = new ArrayList<>();

                for (MyCall myCall : myCalls) {
                    futureList.add(executorCompletionService.submit(myCall));
                }

                for (int i =0; i < futureList.size(); i++) {
                    try {
                        Product resultProduct = executorCompletionService.take().get();

                        String myVendorCode = resultProduct.getMyVendorCodeFromRequest();
                        String myRefForPage = resultProduct.getMyRefForPage();
                        String myRefForImage = resultProduct.getMyRefForImage();
                        String myProductName = resultProduct.getMyProductName();
                        String mySpecAction = resultProduct.getMySpecAction();
                        String vendorCode = resultProduct.getCompetitorVendorCode();
                        String productName = resultProduct.getCompetitorProductName();
                        String refForPage = resultProduct.getCompetitorRefForPage();
                        String refForImage = resultProduct.getCompetitorRefForImage();
                        String refFromRequest = resultProduct.getQueryForSearch();
                        int priceU = resultProduct.getCompetitorPriceU();
                        int basicSale = resultProduct.getCompetitorBasicSale();
                        int basicPriceU = resultProduct.getCompetitorBasicPriceU();
                        int promoSale = resultProduct.getCompetitorPromoSale();
                        int promoPriceU = resultProduct.getCompetitorPromoPriceU();
                        String specAction = resultProduct.getCompetitorSpecAction();
                        int rating = resultProduct.getCompetitorRating();

                        ResultProduct resultProductTemp = resultMap.get(myVendorCode);

                        resultProductTemp.setMyRefForPage(myRefForPage);
                        resultProductTemp.setMyRefForImage(myRefForImage);
                        resultProductTemp.setMyProductName(myProductName);
                        resultProductTemp.setMySpecAction(mySpecAction);
                        resultProductTemp.setCompetitorVendorCode(vendorCode);
                        resultProductTemp.setCompetitorProductName(productName);
                        resultProductTemp.setCompetitorRefForPage(refForPage);
                        resultProductTemp.setCompetitorRefForImage(refForImage);
                        resultProductTemp.setQueryForSearch(refFromRequest);
                        resultProductTemp.setCompetitorPriceU(priceU);
                        resultProductTemp.setCompetitorBasicSale(basicSale);
                        resultProductTemp.setCompetitorBasicPriceU(basicPriceU);
                        resultProductTemp.setCompetitorPromoSale(promoSale);
                        resultProductTemp.setCompetitorPromoPriceU(promoPriceU);
                        resultProductTemp.setCompetitorSpecAction(specAction);
                        resultProductTemp.setCompetitorSpecAction(specAction);
                        resultProductTemp.setCompetitorRating(rating);

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

                        resultMap.put(resultProductTemp.getMyVendorCodeForMarketPlace(), resultProductTemp);

                        int finalI = i + 1;

                        //controller.getProgressBar().setProgress(finalI /futureList.size());

                        if (vendorCode.equals("-")){
                            controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ошибка\n");
                        } else {
                            controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ok\n");
                        }
                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                controller.getAreaLog().appendText("Количество проанализированных позицый - " + futureList.size() + "\n");
                executorService.shutdown();
                controller.getAreaLog().appendText("Загрузка изображений...");
                taskWriteExel.run();
            }
        }).start();
    }

    //колобэл, который выполняет запросы на MarketPlace
    static class MyCall implements Callable<Product> {

        private String key;
        private String category;
        private String querySearch;
        private String brand;

        public MyCall(String key, String category, String querySearch, String brand) {
            this.key = key;
            this.category = category;
            this.querySearch = querySearch;
            this.brand = brand;
        }

        @Override
        //0 - флаг для Wildberies
        //1 - флаг для Ozon
        public Product call() throws Exception {
            return parserWildBer.getProduct(key, category, querySearch, brand, 1);
        }
    }
}
