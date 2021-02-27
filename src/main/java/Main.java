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
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class Main extends Application implements Controller.ActionInController {

    private Object mon = new Object();

    private Controller controller;
    private static double preFld;

    TaskReadExcel taskReadExcel;
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
        preFld = Double.parseDouble(controller.percentTxtFld.getText());

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void selectFile(List<File> files) {
        taskReadExcel = new TaskReadExcel(files);

        controller.getAreaLog().appendText("Чтение файлов \"" + files.get(0).getName() + "\" и \"" + files.get(1).getName() + "\"");

        controller.getProgressBar().setProgress(0);
        // Unbind progress property
        controller.getProgressBar().progressProperty().unbind();
        // Bind progress property
        controller.getProgressBar().progressProperty().bind(taskReadExcel.progressProperty());

        // When completed tasks
        taskReadExcel.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                resultMap = taskReadExcel.getValue();
                taskReadExcel.cancel(true);
                controller.getAreaLog().appendText(" - ok!\n");
                controller.getAreaLog().appendText("Объём анализа - " + resultMap.size() + " позиций\n");
                controller.getProgressBar().progressProperty().unbind();
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        controller.getProgressBar().setProgress(0);
//                    }
//                });

                controller.getAreaLog().appendText("Анализ артикулов:\n");

                getResultProduct(resultMap);
            }
        });
        new Thread(taskReadExcel).start();
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
                Set<String> setMyVendorCodes = resultMap.keySet();

                for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
                    String key = entry.getKey();
                    String category = entry.getValue().getCategory();
                    String brand = entry.getValue().getMyBrand();

                    myCalls.add(new MyCall(key, category, brand));
                }

                List<Future<Product>> futureList = new ArrayList<>();

                for (MyCall myCall : myCalls) {
                    futureList.add(executorCompletionService.submit(myCall));
                }

                for (int i =0; i < futureList.size(); i++) {
                    try {
                        Product resultProduct = executorCompletionService.take().get();
                        //получение моего кода необходимо для того, чтобы достать из map тот ResultProduct, по которому производился поиск аналога
                        String myVendorCode = resultProduct.getMyVendorCodeFromRequest();

                        String myRefForPage = resultProduct.getMyRefForPage();
                        String myRefForImage = resultProduct.getMyRefForImage();
                        String myProductName = resultProduct.getMyProductName();
                        String mySpecAction = resultProduct.getMySpecAction();
                        String competitorVendorCode = resultProduct.getCompetitorVendorCode();
                        String competitorProductName = resultProduct.getCompetitorProductName();
                        String competitorRefForPage = resultProduct.getCompetitorRefForPage();
                        String competitorRefForImage = resultProduct.getCompetitorRefForImage();
                        String queryForSearch = resultProduct.getQueryForSearch();
                        int countSearch = resultProduct.getCountSearch();
                        int competitorPriceU = resultProduct.getCompetitorPriceU();
                        int competitorBasicSale = resultProduct.getCompetitorBasicSale();
                        int competitorBasicPriceU = resultProduct.getCompetitorBasicPriceU();
                        int competitorPromoSale = resultProduct.getCompetitorPromoSale();
                        int competitorPromoPriceU = resultProduct.getCompetitorPromoPriceU();
                        String specAction = resultProduct.getCompetitorSpecAction();
                        int competitorRating = resultProduct.getCompetitorRating();

                        ResultProduct resultProductTemp = resultMap.get(myVendorCode);

                        resultProductTemp.setMyRefForPage(myRefForPage);
                        resultProductTemp.setMyRefForImage(myRefForImage);
                        resultProductTemp.setMyProductName(myProductName);
                        resultProductTemp.setMySpecAction(mySpecAction);
                        resultProductTemp.setCompetitorVendorCode(competitorVendorCode);
                        resultProductTemp.setCompetitorProductName(competitorProductName);
                        resultProductTemp.setCompetitorRefForPage(competitorRefForPage);
                        resultProductTemp.setCompetitorRefForImage(competitorRefForImage);
                        resultProductTemp.setQueryForSearch(queryForSearch);
                        resultProductTemp.setCountSearch(countSearch);
                        resultProductTemp.setCompetitorPriceU(competitorPriceU);
                        resultProductTemp.setCompetitorBasicSale(competitorBasicSale);
                        resultProductTemp.setCompetitorBasicPriceU(competitorBasicPriceU);
                        resultProductTemp.setCompetitorPromoSale(competitorPromoSale);
                        resultProductTemp.setCompetitorPromoPriceU(competitorPromoPriceU);
                        resultProductTemp.setCompetitorSpecAction(specAction);
                        resultProductTemp.setCompetitorSpecAction(specAction);
                        resultProductTemp.setCompetitorRating(competitorRating);

                        //установка рекомендуемой скидки и розничной цены на основании процента демпинга

                        double present = 1 - preFld / 100;

                        if (myVendorCode.equals(competitorVendorCode) || competitorVendorCode.equals("-")){
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

                        resultMap.put(resultProductTemp.getMyVendorCodeForWildberies(), resultProductTemp);

                        int finalI = i + 1;

                        synchronized (mon) {
                            if (competitorVendorCode.equals("-")){
                                controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ошибка\n");
                            } else {
                                controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ok\n");
                            }
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
