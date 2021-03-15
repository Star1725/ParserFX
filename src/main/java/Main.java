import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
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
    final WebClient webClient = new WebClient(BrowserVersion.CHROME);

    private Controller controller;
    private static double preFld;

    private static Set<String> setMyVendorCodes;

    static int marketplaceFlag;
    private TaskReadExcelForWildberies taskReadExcelForWildberies;
    private TaskReadExcelForOzon taskReadExcelForOzon;
    private TaskWriteExelForWildberries taskWriteExelForWildberries;
    private TaskWriteExelForOzon taskWriteExelForOzon;

    static ParserWildBer parserWildBer = new ParserWildBer();
    static ParserOzon parserOzon = new ParserOzon();

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
    public void selectFile(List<File> files, String marketPlace) {
        System.out.println("selectFile - " + marketPlace);
        if (marketPlace.equals(Constants.OZON)) marketplaceFlag = 1;//Ozon
        else if (marketPlace.equals(Constants.WILDBERIES)) marketplaceFlag = 2;//Wildberies
        System.out.println("selectFile - " + marketplaceFlag);
        if (marketplaceFlag == 1){
            taskReadExcelForOzon = new TaskReadExcelForOzon(files);
            controller.getAreaLog().appendText("Чтение файлов для аналитики Ozon - \"" + files.get(0).getName() + "\" и \"" + files.get(1).getName() + "\"");
        } else if (marketplaceFlag == 2){
            taskReadExcelForWildberies = new TaskReadExcelForWildberies(files);
            controller.getAreaLog().appendText("Чтение файлов для аналитики Wildberies - \"" + files.get(0).getName() + "\" и \"" + files.get(1).getName() + "\"");
        }

        controller.getProgressBar().setProgress(0);
        // Unbind progress property
        controller.getProgressBar().progressProperty().unbind();

        if (marketplaceFlag == 1){
            // Bind progress property
            controller.getProgressBar().progressProperty().bind(taskReadExcelForOzon.progressProperty());
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
        } else if (marketplaceFlag == 2){
            // Bind progress property
            controller.getProgressBar().progressProperty().bind(taskReadExcelForWildberies.progressProperty());
            // When completed tasks for Wildberies
            taskReadExcelForWildberies.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    resultMap = taskReadExcelForWildberies.getValue();
                    taskReadExcelForWildberies.cancel(true);
                    controller.getAreaLog().appendText(" - ok!\n");
                    controller.getAreaLog().appendText("Объём анализа - " + resultMap.size() + " позиций\n");
                    controller.getProgressBar().progressProperty().unbind();
                    controller.getAreaLog().appendText("Анализ артикулов:\n");

                    getResultProduct(resultMap);
                }
            });
            new Thread(taskReadExcelForWildberies).start();
        }
    }

    private static void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getResultProduct(Map<String, ResultProduct> resultMap){

        preFld = Double.parseDouble(controller.percentTxtFld.getText());

        if (marketplaceFlag == 1){
            taskWriteExelForOzon = new TaskWriteExelForOzon(resultMap, webClient);
            controller.getProgressBar().progressProperty().bind(taskWriteExelForOzon.progressProperty());
            taskWriteExelForOzon.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    openFile(taskWriteExelForOzon.writeWorkbook(resultMap));
                }
            });
        } else {
            taskWriteExelForWildberries = new TaskWriteExelForWildberries(resultMap);
            controller.getProgressBar().progressProperty().bind(taskWriteExelForWildberries.progressProperty());
            taskWriteExelForWildberries.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    openFile(taskWriteExelForWildberries.writeWorkbook(resultMap));
                }
            });
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                executorService = Executors.newFixedThreadPool(4);
                CompletionService<Product> executorCompletionService= new ExecutorCompletionService<>(executorService);

                List<MyCall> myCalls = new ArrayList<>();
                setMyVendorCodes = resultMap.keySet();

                for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
                    String key = entry.getKey();
                    String category = entry.getValue().getCategory();
                    String brand = entry.getValue().getMyBrand();
                    String productType = entry.getValue().getProductType();
                    String querySearchForOzon = entry.getValue().getQuerySearchForWildberiesOrOzon();

                    myCalls.add(new MyCall(key, category, brand, productType, setMyVendorCodes, querySearchForOzon, webClient));
                }

                List<Future<Product>> futureList = new ArrayList<>();

                for (MyCall myCall : myCalls) {
                    futureList.add(executorCompletionService.submit(myCall));
                }

                for (int i =0; i < futureList.size(); i++) {

                    String myVendorCode = null;
                    try {
                        //установка рекомендуемой скидки и розничной цены на основании процента демпинга
                        double present = 1 - preFld / 100;
                        if (marketplaceFlag == 2){
                            Product product = executorCompletionService.take().get();
                            //получение моего кода необходимо для того, чтобы достать из map тот ResultProduct, по которому производился поиск аналога
                            myVendorCode = product.getMyVendorCodeFromRequest();

                            String myRefForPage = product.getMyRefForPage();
                            String myRefForImage = product.getMyRefForImage();
                            String myProductName = product.getMyProductName();
                            String mySpecAction = product.getMySpecAction();
                            String competitorVendorCode = product.getCompetitorVendorCode();
                            String competitorProductName = product.getCompetitorProductName();
                            String competitorRefForPage = product.getCompetitorRefForPage();
                            String competitorRefForImage = product.getCompetitorRefForImage();
                            String competitorName = product.getCompetitorName();
                            String queryForSearch = product.getQueryForSearch();
                            int countSearch = product.getCountSearch();
                            int competitorPriceU = product.getCompetitorPriceU();
                            int competitorBasicSale = product.getCompetitorBasicSale();
                            int competitorBasicPriceU = product.getCompetitorBasicPriceU();
                            int competitorPromoSale = product.getCompetitorPromoSale();
                            int competitorPromoPriceU = product.getCompetitorPromoPriceU();
                            String specAction = product.getCompetitorSpecAction();
                            int competitorRating = product.getCompetitorRating();

                            ResultProduct resultProduct = resultMap.get(myVendorCode);

                            resultProduct.setMyRefForPage(myRefForPage);
                            resultProduct.setMyRefForImage(myRefForImage);
                            resultProduct.setMyProductName(myProductName);
                            resultProduct.setMySpecAction(mySpecAction);
                            resultProduct.setCompetitorVendorCode(competitorVendorCode);
                            resultProduct.setCompetitorProductName(competitorProductName);
                            resultProduct.setCompetitorRefForPage(competitorRefForPage);
                            resultProduct.setCompetitorRefForImage(competitorRefForImage);
                            resultProduct.setCompetitorName(competitorName);
                            resultProduct.setQueryForSearch(queryForSearch);
                            resultProduct.setCountSearch(countSearch);
                            resultProduct.setCompetitorPriceU(competitorPriceU);
                            resultProduct.setCompetitorBasicSale(competitorBasicSale);
                            resultProduct.setCompetitorBasicPriceU(competitorBasicPriceU);
                            resultProduct.setCompetitorPromoSale(competitorPromoSale);
                            resultProduct.setCompetitorPromoPriceU(competitorPromoPriceU);
                            resultProduct.setCompetitorSpecAction(specAction);
                            resultProduct.setCompetitorSpecAction(specAction);
                            resultProduct.setCompetitorRating(competitorRating);


                            int currentPriceU = resultProduct.getMyPriceU();
                            int myCurrentLowerPriceU = resultProduct.getMyLowerPriceU();
                            int myCurrentBasicSale = resultProduct.getMyBasicSale();

                            int competitorLowerPriceU = resultProduct.getCompetitorLowerPriceU();

                            int dumpingPresent = (int) (Math.round(100 - 100 * ((double) myCurrentLowerPriceU/competitorLowerPriceU)));

                            //если аналог это мой товар, то всё оставляю без изменений
                            if (myVendorCode.equals(competitorVendorCode) || competitorVendorCode.equals("-") || setMyVendorCodes.contains(competitorVendorCode) || (dumpingPresent == 1)){
                                resultProduct.setRecommendedMyLowerPrice(myCurrentLowerPriceU);
                                resultProduct.setRecommendedBasicSale(myCurrentBasicSale);
                                resultProduct.setRecommendedPromoSale(resultProduct.getMyPromoSale());

                            } else {
                                //расчитываем рекомендованню розничную цену с учётом процента демпинга
                                double count1 = (double) competitorLowerPriceU * present;
                                long count2 = Math.round(count1);
                                int count3 = (int) count2;
                                int recommendedMyLowerPrice = count3;
                                //и на основании этой рекомендованной цены расчитываем базоваю скидку
                                double count4 = (double) recommendedMyLowerPrice / currentPriceU * 100;
                                long count5 = Math.round(count4);
                                int newMyBasicSale = 100 - (int) count5 ;
                                //проверка базовой скидки:
                                int checkPrice = (int )Math.round(((1 - (double)newMyBasicSale/100)) * currentPriceU);
                                if (recommendedMyLowerPrice < checkPrice){
                                    newMyBasicSale = newMyBasicSale + 1;
                                }
                                recommendedMyLowerPrice = (int )Math.round(((1 - (double)newMyBasicSale/100)) * currentPriceU);
                                resultProduct.setRecommendedMyLowerPrice(recommendedMyLowerPrice);

                                //если новая базовая скидка меньше 3%
                                if (newMyBasicSale < 3){
                                    newMyBasicSale = 3;
                                    resultProduct.setRecommendedBasicSale(newMyBasicSale);//устанавливаем скидку в 3%
                                    int newRecomendPriceU = (int)Math.round((double) recommendedMyLowerPrice/0.97);//расчитываем новую розничную цену до скидки
                                    resultProduct.setRecommendedPriceU(newRecomendPriceU);//устанавливаем её
                                } else
                                    //если новая базовая скидка больше 90%
                                    if (newMyBasicSale > 90){
                                        newMyBasicSale = 90;
                                        resultProduct.setRecommendedBasicSale(newMyBasicSale);//устанавливаем скидку в 90%
                                        int newRecommendPriceU = (int)Math.round((double) recommendedMyLowerPrice/0.1);//расчитываем новую розничную цену до скидки
                                        resultProduct.setRecommendedPriceU(newRecommendPriceU);//устанавливаем её
                                    } else {
                                        resultProduct.setRecommendedBasicSale(newMyBasicSale);
                                    }
                            }
                            //дублирование кода
                            resultMap.put(resultProduct.getMyVendorCodeForWildberiesOrOzon(), resultProduct);

                            int finalI = i + 1;
                            synchronized (mon) {
                                if (competitorVendorCode.equals("-")) {
                                    controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ошибка\n");
                                } else {
                                    controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ok\n");
                                }
                            }
                            ///////////////////
                        } else {
                            Product product = executorCompletionService.take().get();
                            //получение моего кода необходимо для того, чтобы достать из map тот ResultProduct, по которому производился поиск аналога
                            myVendorCode = product.getMyVendorCodeFromRequest();

                            String myRefForPage = product.getMyRefForPage();
                            String competitorVendorCode = product.getCompetitorVendorCode();
                            String competitorProductName = product.getCompetitorProductName();
                            String competitorRefForPage = product.getCompetitorRefForPage();
                            String competitorRefForImage = product.getCompetitorRefForImage();
                            String competitorName = product.getCompetitorName();
                            String queryForSearch = product.getQueryForSearch();
                            int competitorBasicPriceU = product.getCompetitorBasicPriceU();
                            int competitorPremiumPriceForOzon = product.getCompetitorPremiumPriceForOzon();

                            ResultProduct resultProduct = resultMap.get(myVendorCode);

                            resultProduct.setMyRefForPage(myRefForPage);
                            resultProduct.setCompetitorVendorCode(competitorVendorCode);
                            resultProduct.setCompetitorProductName(competitorProductName);
                            resultProduct.setCompetitorRefForPage(competitorRefForPage);
                            resultProduct.setCompetitorRefForImage(competitorRefForImage);
                            resultProduct.setCompetitorName(competitorName);
                            resultProduct.setQueryForSearch(queryForSearch);
                            resultProduct.setCompetitorBasicPriceU(competitorBasicPriceU);
                            resultProduct.setCompetitorPremiumPriceForOzon(competitorPremiumPriceForOzon);

                            int myCurrentPriceU = resultProduct.getMyPriceU();
                            int myCurrentLowerPriceU = resultProduct.getMyLowerPriceU();
                            int competitorLowerPriceU = resultProduct.getCompetitorLowerPriceU();

                            int dumpingPresent = (int) (Math.round(100 - 100 * ((double) myCurrentLowerPriceU/competitorLowerPriceU)));

                            if (competitorName.equals(Constants.MY_SELLER) || (dumpingPresent == 1)){
                                resultProduct.setRecommendedMyLowerPrice(myCurrentLowerPriceU);
                            } else {
                                //расчитываем рекомендованню розничную цену с учётом процента демпинга
                                double count1 = (double) competitorLowerPriceU * present;
                                long count2 = Math.round(count1);
                                int count3 = (int) count2;
                                int recommendedMyLowerPrice = count3;

                                resultProduct.setRecommendedMyLowerPrice(recommendedMyLowerPrice);
                            }
                            //дублирование кода
                            resultMap.put(resultProduct.getMyVendorCodeForWildberiesOrOzon(), resultProduct);

                            int finalI = i + 1;
                            synchronized (mon) {
                                if (competitorVendorCode.equals("-")) {
                                    controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ошибка\n");
                                } else {
                                    controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ok\n");
                                }
                            }
                            ///////////////////
                        }

                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        System.out.println("для артикула " + myVendorCode + " ошибка - " + e.getMessage());
                    }
                }
                controller.getAreaLog().appendText("Количество проанализированных позицый - " + futureList.size() + "\n");
                executorService.shutdown();
                controller.getAreaLog().appendText("Загрузка изображений...");

                if (marketplaceFlag == 1){
                    taskWriteExelForOzon.run();
                } else {
                    taskWriteExelForWildberries.run();
                }
            }
        }).start();
    }

    //колобэл, который выполняет запросы на wildberries
    static class MyCall implements Callable<Product> {
        String key;
        String category;
        String brand;
        String productType;
        String querySearchForOzon;
        Set myVendorCodes;
        WebClient webClient;

        public MyCall(String key, String category, String brand, String productType, Set myVendorCodes, String querySearchForOzon, WebClient webClient) {
            this.key = key;
            this.category = category;
            this.brand = brand;
            this.productType = productType;
            this.querySearchForOzon = querySearchForOzon;
            this.myVendorCodes = myVendorCodes;
            this.webClient = webClient;
        }

        @Override
        //1 - флаг для Ozon
        //2 - флаг для Wildberies
        public Product call() throws Exception {
            System.out.println("выполнение задачи - " + marketplaceFlag);
            if (marketplaceFlag == 1){
                return parserOzon.getProduct(key, category, brand, productType, myVendorCodes, querySearchForOzon, webClient, marketplaceFlag);
            } else if (marketplaceFlag == 2){
                return parserWildBer.getProduct(key, category, brand, productType, myVendorCodes, querySearchForOzon, webClient, marketplaceFlag);
            } else return null;
        }
    }
}
