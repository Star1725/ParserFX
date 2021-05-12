import com.gargoylesoftware.htmlunit.*;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends Application implements Controller.ActionInController {

    private long start;
    private long stop;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    private Object mon = new Object();
    private static WebClient webClient = null;
    public static Lock lock = new ReentrantLock();

    private Controller controller;
    private static double preFld;

    private static Set<String> setMyVendorCodes;

    static int marketplaceFlag;
    static int stepFlag;
    private TaskReadExcelForWildberies taskReadExcelForWildberies;
    private TaskReadExcelForOzon taskReadExcelForOzon;

    private UnifierDataFromExcelFiles unifierDataFromExcelFiles;

    private TaskWriteExelForWildberries taskWriteExelForWildberries;
    private TaskWriteExelForOzon taskWriteExelForOzon;

//    static ParserWildBer parserWildBer = new ParserWildBer();
    static LowerProductFinder lowerProductFinder = new LowerProductFinder();

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

        webClient = new WebClient(BrowserVersion.CHROME);

        ProxyConfig proxyConfig = new ProxyConfig(Constants.PROXY_HOST, Constants.PROXY_PORT);
        DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
        //указываем логин и пароль для прокси-сервера
        credentialsProvider.addCredentials(Constants.LOGIN, Constants.PASSWORD);
        webClient.setCredentialsProvider(credentialsProvider);

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setTimeout(15000);

        /* Clearing Cache and Cookies */
        //webClient.getCookieManager().clearCookies();
        //webClient.getCache().clear();

        /* Setting proxy to be used */
        webClient.getOptions().setProxyConfig(proxyConfig);

        //попробовать в классе ParserOzon
        /* Create ProxyStatus record
        WebResponse response = startPage.getWebResponse();
    }catch (Exception e) {
        e.printStackTrace();
    }
        */

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void selectFile(List<File> files, String marketPlace, String step) {
        System.out.println("selectFile - " + marketPlace + ", " + step);

        if (marketPlace.equals(Constants.OZON)) marketplaceFlag = 1;//Ozon
        else if (marketPlace.equals(Constants.WILDBERIES)) marketplaceFlag = 2;//Wildberies
        if (step.equals(Constants.RUB)) stepFlag = 1;//рубли
        else if (step.equals(Constants.PERCENT)) stepFlag = 2;//проценты

//        System.out.println("selectFile - " + marketplaceFlag);
//        if (marketplaceFlag == 1){
//            taskReadExcelForOzon = new TaskReadExcelForOzon(files, marketplaceFlag);
//            controller.getAreaLog().appendText("Чтение файлов для аналитики Ozon - \"" + files.get(0).getName() + "\" и \"" + files.get(1).getName() + "\"");
//        } else if (marketplaceFlag == 2){
//            taskReadExcelForWildberies = new TaskReadExcelForWildberies(files);
//            controller.getAreaLog().appendText("Чтение файлов для аналитики Wildberies - \"" + files.get(0).getName() + "\" и \"" + files.get(1).getName() + "\"");
//        }

        controller.getAreaLog().appendText("Чтение файлов для аналитики " + marketPlace + " - \"" + files.get(0).getName() + "\" и \"" + files.get(1).getName() + "\"");
        unifierDataFromExcelFiles = new UnifierDataFromExcelFiles(files, marketplaceFlag);

        controller.getProgressBar().setProgress(0);
        // Unbind progress property
        controller.getProgressBar().progressProperty().unbind();

        // Bind progress property
        controller.getProgressBar().progressProperty().bind(unifierDataFromExcelFiles.progressProperty());
        // When completed taskReadExcelForOzon
        unifierDataFromExcelFiles.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                resultMap = unifierDataFromExcelFiles.getValue();
                unifierDataFromExcelFiles.cancel(true);
                controller.getAreaLog().appendText(" - ok!\n");
                controller.getAreaLog().appendText("Объём анализа - " + resultMap.size() + " позиций\n");
                controller.getProgressBar().progressProperty().unbind();

                controller.getAreaLog().appendText("Анализ артикулов:\n");

                getResultProduct(resultMap);
            }
        });
        new Thread(unifierDataFromExcelFiles).start();


//        if (marketplaceFlag == 1){
//            // Bind progress property
//            controller.getProgressBar().progressProperty().bind(taskReadExcelForOzon.progressProperty());
//            // When completed taskReadExcelForOzon
//            taskReadExcelForOzon.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
//                @Override
//                public void handle(WorkerStateEvent event) {
//                    resultMap = taskReadExcelForOzon.getValue();
//                    taskReadExcelForOzon.cancel(true);
//                    controller.getAreaLog().appendText(" - ok!\n");
//                    controller.getAreaLog().appendText("Объём анализа - " + resultMap.size() + " позиций\n");
//                    controller.getProgressBar().progressProperty().unbind();
//
//                    controller.getAreaLog().appendText("Анализ артикулов:\n");
//
//                    getResultProduct(resultMap);
//                }
//            });
//            new Thread(taskReadExcelForOzon).start();
//        } else if (marketplaceFlag == 2){
//            // Bind progress property
//            controller.getProgressBar().progressProperty().bind(taskReadExcelForWildberies.progressProperty());
//            // When completed tasks for Wildberies
//            taskReadExcelForWildberies.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
//                @Override
//                public void handle(WorkerStateEvent event) {
//                    resultMap = taskReadExcelForWildberies.getValue();
//                    taskReadExcelForWildberies.cancel(true);
//                    controller.getAreaLog().appendText(" - ok!\n");
//                    controller.getAreaLog().appendText("Объём анализа - " + resultMap.size() + " позиций\n");
//                    controller.getProgressBar().progressProperty().unbind();
//                    controller.getAreaLog().appendText("Анализ артикулов:\n");
//
//                    getResultProduct(resultMap);
//                }
//            });
//            new Thread(taskReadExcelForWildberies).start();
//        }
    }

    private static void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getResultProduct(Map<String, ResultProduct> resultMap){

        start = System.currentTimeMillis();
        Date dateStart = new Date(start);
        controller.getAreaLog().appendText(dateFormat.format(dateStart) + " - старт\n");


        preFld = Double.parseDouble(controller.percentTxtFld.getText());

        if (marketplaceFlag == 1){
            taskWriteExelForOzon = new TaskWriteExelForOzon(resultMap, webClient);
            controller.getProgressBar().progressProperty().bind(taskWriteExelForOzon.progressProperty());
            taskWriteExelForOzon.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
//                    openFile(taskWriteExelForOzon.writeWorkbook(resultMap));
                    openFile(taskWriteExelForOzon.getValue());
                    stop = System.currentTimeMillis();
                    Date dateStart = new Date(stop);
                    long timeWorkCode = stop - start;
                    controller.getAreaLog().appendText(dateFormat.format(dateStart) + " - стоп\n " +
                            "Длительность парсинга - " + (int) timeWorkCode/(1000 * 60) + " мин. " + (timeWorkCode % (1000 * 60))/1000 + " сек.");
                }
            });
        } else {
            taskWriteExelForWildberries = new TaskWriteExelForWildberries(resultMap);
            controller.getProgressBar().progressProperty().bind(taskWriteExelForWildberries.progressProperty());
            taskWriteExelForWildberries.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    openFile(taskWriteExelForWildberries.getValue());
                    stop = System.currentTimeMillis();
                    Date dateStart = new Date(stop);
                    long timeWorkCode = stop - start;
                    controller.getAreaLog().appendText(dateFormat.format(dateStart) + " - стоп\n " +
                            "Длительность парсинга - " + (int) timeWorkCode/(1000 * 60) + " мин. " + (timeWorkCode % (1000 * 60))/1000 + " сек.");
                }
            });
        }

        new Thread(() -> {

            executorService = Executors.newFixedThreadPool(1);
            CompletionService<Product> executorCompletionService= new ExecutorCompletionService<>(executorService);

            List<MyCall> myCalls = new ArrayList<>();
            setMyVendorCodes = resultMap.keySet();

            for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
                String key = entry.getKey();
                String category = entry.getValue().getCategory();
                String brand = entry.getValue().getMyBrand();
                String productType = entry.getValue().getProductType();
                String specQuerySearchForWildberiesOrOzon = entry.getValue().getSpecQuerySearchForWildberiesOrOzon();
                String myProductModel = entry.getValue().getMyProductModel();
                List<String> arrayParams = entry.getValue().getArrayListParams();

                myCalls.add(new MyCall(marketplaceFlag, key, category, brand, productType, myProductModel, arrayParams, setMyVendorCodes, specQuerySearchForWildberiesOrOzon, webClient, lock));
            }

            List<Future<Product>> futureList = new ArrayList<>();

            int number = 1;
//
//                try {
//                    //смена IP в самом начале
//                    switchIpForProxy();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            for (MyCall myCall : myCalls) {
                futureList.add(executorCompletionService.submit(myCall));
                String myVendorCode = "-";
                String myRefForPage = "-";
                String myRefForImage = "-";
//                String myProductName = "-";
                String mySpecAction = "-";
                String competitorVendorCode = "-";
                String competitorProductName = "-";
                String competitorRefForPage = "-";
                String competitorRefForImage = "-";
                String competitorName = "-";
                String queryForSearch = "-";
                List<String> arrayListParams = new ArrayList<>();
                String competitorSpecAction = "-";
                String refUrlForResult = "-";
                int competitorPriceU = 0;
                int competitorBasicSale = 0;
                int competitorBasicPriceU = 0;
                int competitorPromoSale = 0;
                int competitorPromoPriceU = 0;
                int competitorPremiumPriceForOzon = 0;
                int competitorRating = 0;

                try {
                    //установка рекомендуемой скидки и розничной цены на основании величины скидки(рубли или проценты)
                    double rub = 0;
                    double present = 0;
                    if (stepFlag == 1){
                        rub = preFld * 100;
                    } else {
                        present = 1 - preFld / 100;
                    }

////////////////////////для wildberries//////////////////////////////////////////////////////////////////////////////////
                    if (marketplaceFlag == 2){
                        System.out.println("В main запускаем executorCompletionService.take().get() № " + number);
                        Product product = executorCompletionService.take().get();
                        //получение моего кода необходимо для того, чтобы достать из map тот ResultProduct, по которому производился поиск аналога
                        myVendorCode = product.getMyVendorCodeFromRequest();

                        myRefForPage = product.getMyRefForPage();
                        myRefForImage = product.getMyRefForImage();
//                        myProductName = product.getMyProductName();
                        mySpecAction = product.getMySpecAction();
                        competitorVendorCode = product.getCompetitorVendorCode();
                        competitorProductName = product.getCompetitorProductName();
                        competitorRefForPage = product.getCompetitorRefForPage();
                        competitorRefForImage = product.getCompetitorRefForImage();
                        competitorName = product.getCompetitorName();
                        queryForSearch = product.getQueryForSearch();
                        refUrlForResult = product.getRefUrlForResultSearch();
                        competitorPriceU = product.getCompetitorPriceU();
                        competitorBasicSale = product.getCompetitorBasicSale();
                        competitorBasicPriceU = product.getCompetitorBasicPriceU();
                        competitorPromoSale = product.getCompetitorPromoSale();
                        competitorPromoPriceU = product.getCompetitorPromoPriceU();
                        competitorSpecAction = product.getCompetitorSpecAction();
                        competitorRating = product.getCompetitorRating();

                        ResultProduct resultProduct = resultMap.get(myVendorCode);

                        resultProduct.setMyRefForPage(myRefForPage);
                        resultProduct.setMyRefForImage(myRefForImage);
//                        resultProduct.setMyProductName(myProductName);
                        resultProduct.setMySpecAction(mySpecAction);
                        resultProduct.setCompetitorVendorCode(competitorVendorCode);
                        resultProduct.setCompetitorProductName(competitorProductName);
                        resultProduct.setCompetitorRefForPage(competitorRefForPage);
                        resultProduct.setCompetitorRefForImage(competitorRefForImage);
                        resultProduct.setCompetitorName(competitorName);
                        resultProduct.setQueryForSearch(queryForSearch);
                        resultProduct.setRefUrlForResultSearch(refUrlForResult);
                        resultProduct.setCompetitorPriceU(competitorPriceU);
                        resultProduct.setCompetitorBasicSale(competitorBasicSale);
                        resultProduct.setCompetitorBasicPriceU(competitorBasicPriceU);
                        resultProduct.setCompetitorPromoSale(competitorPromoSale);
                        resultProduct.setCompetitorPromoPriceU(competitorPromoPriceU);
                        resultProduct.setCompetitorSpecAction(competitorSpecAction);
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
                            //расчитываем рекомендованню розничную цену с учётом уровня демпинга
                            double count1 = 0;
                            if (stepFlag == 1){
                                count1 = (double) competitorLowerPriceU - rub;
                            } else {
                                count1 = (double) competitorLowerPriceU * present;
                            }
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

                        synchronized (mon) {
                            if (competitorVendorCode.equals("-")) {
                                controller.getAreaLog().appendText(number + " - " + myVendorCode + " - ошибка\n");
                            } else {
                                controller.getAreaLog().appendText(number + " - " + myVendorCode + " - ok\n");
                            }
                        }
///////////////////Ozon//////////////////////////////////////////////////////////////////////
                    } else {
                        System.out.println("В main запускаем executorCompletionService.take().get() № " + number);
                        Product product = executorCompletionService.take().get();
                        //получение моего кода необходимо для того, чтобы достать из map тот ResultProduct, по которому производился поиск аналога
                        myVendorCode = product.getMyVendorCodeFromRequest();
                        System.out.println(number + "/" + resultMap.size() + " - получили результат для задачи № " + myVendorCode);
                        myRefForPage = product.getMyRefForPage();
                        myRefForImage = product.getMyRefForImage();
                        competitorVendorCode = product.getCompetitorVendorCode();
                        competitorProductName = product.getCompetitorProductName();
                        competitorRefForPage = product.getCompetitorRefForPage();
                        competitorRefForImage = product.getCompetitorRefForImage();
                        competitorName = product.getCompetitorName();
                        competitorSpecAction = product.getCompetitorSpecAction();
                        queryForSearch = product.getQueryForSearch();
                        refUrlForResult = product.getRefUrlForResultSearch();
                        competitorBasicPriceU = product.getCompetitorBasicPriceU();
                        competitorPremiumPriceForOzon = product.getCompetitorPremiumPriceForOzon();

                        ResultProduct resultProduct = resultMap.get(myVendorCode);

                        resultProduct.setMyRefForPage(myRefForPage);
                        resultProduct.setMyRefForImage(myRefForImage);
                        resultProduct.setCompetitorVendorCode(competitorVendorCode);
                        resultProduct.setCompetitorProductName(competitorProductName);
                        resultProduct.setCompetitorRefForPage(competitorRefForPage);
                        resultProduct.setCompetitorRefForImage(competitorRefForImage);
                        resultProduct.setCompetitorName(competitorName);
                        resultProduct.setQueryForSearch(queryForSearch);
                        resultProduct.setRefUrlForResultSearch(refUrlForResult);
                        resultProduct.setCompetitorBasicPriceU(competitorBasicPriceU);
                        resultProduct.setCompetitorPremiumPriceForOzon(competitorPremiumPriceForOzon);

                        int myCurrentPriceU = resultProduct.getMyPriceU();
                        int myCurrentLowerPriceU = resultProduct.getMyLowerPriceU();
                        int competitorLowerPriceU = resultProduct.getCompetitorLowerPriceU();

                        int dumpingPresent = (int) (Math.round(100 - 100 * ((double) myCurrentLowerPriceU/competitorLowerPriceU)));
                        double count1 = 0;
                        int recommendedMyLowerPrice = 0;
                        if (competitorName.equals(Constants.MY_SELLER)) {
                            resultProduct.setRecommendedMyLowerPrice(myCurrentLowerPriceU);
                        } else {
                            if (stepFlag == 1){
                                count1 = (double) competitorLowerPriceU - rub;
                                //расчитываем рекомендованню розничную цену с учётом величины демпинга
                                long count2 = Math.round(count1);
                                int count3 = (int) count2;
                                recommendedMyLowerPrice = count3;
                                resultProduct.setRecommendedMyLowerPrice(recommendedMyLowerPrice);
                            } else {
                                if ((dumpingPresent == 1)){
                                    resultProduct.setRecommendedMyLowerPrice(myCurrentLowerPriceU);
                                } else {
                                    count1 = (double) competitorLowerPriceU * present;
                                    //расчитываем рекомендованню розничную цену с учётом величины демпинга
                                    long count2 = Math.round(count1);
                                    int count3 = (int) count2;
                                    recommendedMyLowerPrice = count3;
                                    resultProduct.setRecommendedMyLowerPrice(recommendedMyLowerPrice);
                                }
                            }
                        }

                        //дублирование кода
                        resultMap.put(resultProduct.getMyVendorCodeForWildberiesOrOzon(), resultProduct);

//                            lock.tryLock();
//                                if (competitorVendorCode.equals("-")) {
//                                    if (competitorSpecAction.equals(Constants.BLOCKING)){
//                                        controller.getAreaLog().appendText(number + " - " + myVendorCode + " - блокировка!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
//                                    } else {
//                                        controller.getAreaLog().appendText(number + " - " + myVendorCode + " - ошибка\n");
//                                    }
//                                } else {
//                                    controller.getAreaLog().appendText(number + " - " + myVendorCode + " - ok\n");
//                                }
//                            lock.unlock();
                    }
                } catch (InterruptedException | ExecutionException | NullPointerException e) {
                    System.out.println("для артикула " + myVendorCode + " ошибка - " + e.getMessage());
                    //lock.unlock();
                    e.printStackTrace();
                }
                number++;
                System.out.println("resultProduct для " + myVendorCode + " записан в map");
                System.out.println();
            }
            //outputExcelFile(number);
            controller.getAreaLog().appendText("Количество проанализированных позицый - " + number + "\n");
            executorService.shutdown();
            controller.getAreaLog().appendText("Загрузка изображений...\n");

            System.out.println("Запуск записи excel из main");
            if (marketplaceFlag == 1){
                taskWriteExelForOzon.run();
            } else {
                taskWriteExelForWildberries.run();
            }
        }).start();
    }

    //колобэл, который выполняет запросы на wildberries
    static class MyCall implements Callable<Product> {
        int marketplaceFlag;
        String key;
        String category;
        String brand;
        String productType;
        String productModel;
        List<String> arrayParams;
        String specQuerySearch;
        Set myVendorCodes;
        WebClient webClient;
        Lock lock;

        public MyCall(int marketplaceFlag, String key, String category, String brand, String productType, String productModel, List<String> arrayParams, Set myVendorCodes, String specQuerySearch, WebClient webClient, Lock lock) {
            this.key = key;
            this.category = category;
            this.brand = brand;
            this.productType = productType;
            this.productModel = productModel;
            this.arrayParams = arrayParams;
            this.specQuerySearch = specQuerySearch;
            this.myVendorCodes = myVendorCodes;
            this.webClient = webClient;
            this.lock = lock;
            this.marketplaceFlag = marketplaceFlag;
        }

        @Override
        //1 - флаг для Ozon
        //2 - флаг для Wildberies
        public Product call() throws Exception {
            System.out.println("Запуск задачи lowerProductFinder.getProduct() для маркетплейса = " + marketplaceFlag + " артикула Ozon = " + key + ", где brand = " + brand + ", productModel = " + productModel);
            return lowerProductFinder.getProduct(marketplaceFlag, key, category, brand, productType, productModel, arrayParams, myVendorCodes, specQuerySearch, webClient, lock);

//            if (marketplaceFlag == 1){
//                System.out.println("Запуск задачи parserOzon.getProduct() для артикула Ozon = " + key + ", где brand = " + brand + ", productModel = " + productModel);
//                return lowerProductFinder.getProduct(marketplaceFlag, key, category, brand, productType, productModel, arrayParams, myVendorCodes, specQuerySearch, webClient, lock);
//            } else if (marketplaceFlag == 2){
//                System.out.println("Запуск задачи parserWildBer.getProduct()для артикула WB = " + key + ", где brand = " + brand + ", productModel = " + productModel);
//                return parserWildBer.getProduct(marketplaceFlag, key, category, brand, productType, productModel, arrayParams, myVendorCodes, specQuerySearch, webClient);
//            } else return null;
        }
    }
}
