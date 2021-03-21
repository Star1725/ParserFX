import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import controllers.Controller;
import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main extends Application implements Controller.ActionInController {

    private Object mon = new Object();
    private static WebClient webClient = null;
    public static int countSwitchIP = 0;
    public static Lock lock = new ReentrantLock();

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
        webClient.getOptions().setTimeout(20000);

        /* Clearing Cache and Cookies */
        webClient.getCookieManager().clearCookies();
        webClient.getCache().clear();

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

                executorService = Executors.newFixedThreadPool(1);
                CompletionService<Product> executorCompletionService= new ExecutorCompletionService<>(executorService);

                List<MyCall> myCalls = new ArrayList<>();
                setMyVendorCodes = resultMap.keySet();

                for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
                    String key = entry.getKey();
                    String category = entry.getValue().getCategory();
                    String brand = entry.getValue().getMyBrand();
                    String productType = entry.getValue().getProductType();
                    String querySearchForOzon = entry.getValue().getQuerySearchForWildberiesOrOzon();

                    myCalls.add(new MyCall(key, category, brand, productType, setMyVendorCodes, querySearchForOzon, webClient, lock));
                }

                List<Future<Product>> futureList = new ArrayList<>();

                int number = 1;

                try {
                    //смена IP в самом начале
                    switchIpForProxy();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (MyCall myCall : myCalls) {
                    futureList.add(executorCompletionService.submit(myCall));

                    String myVendorCode = "-";
                    String myRefForPage = "-";
                    String myRefForImage = "-";
                    String myProductName = "-";
                    String mySpecAction = "-";
                    String competitorVendorCode = "-";
                    String competitorProductName = "-";
                    String competitorRefForPage = "-";
                    String competitorRefForImage = "-";
                    String competitorName = "-";
                    String queryForSearch = "-";
                    String competitorSpecAction = "-";
                    int countSearch = 0;
                    int competitorPriceU = 0;
                    int competitorBasicSale = 0;
                    int competitorBasicPriceU = 0;
                    int competitorPromoSale = 0;
                    int competitorPromoPriceU = 0;
                    int competitorPremiumPriceForOzon = 0;
                    int competitorRating = 0;

                    try {
                        //установка рекомендуемой скидки и розничной цены на основании процента демпинга
                        double present = 1 - preFld / 100;
                        if (marketplaceFlag == 2){
                            Product product = executorCompletionService.take().get();
                            //получение моего кода необходимо для того, чтобы достать из map тот ResultProduct, по которому производился поиск аналога
                            myVendorCode = product.getMyVendorCodeFromRequest();

                            myRefForPage = product.getMyRefForPage();
                            myRefForImage = product.getMyRefForImage();
                            myProductName = product.getMyProductName();
                            mySpecAction = product.getMySpecAction();
                            competitorVendorCode = product.getCompetitorVendorCode();
                            competitorProductName = product.getCompetitorProductName();
                            competitorRefForPage = product.getCompetitorRefForPage();
                            competitorRefForImage = product.getCompetitorRefForImage();
                            competitorName = product.getCompetitorName();
                            queryForSearch = product.getQueryForSearch();
                            countSearch = product.getCountSearch();
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
                            if (number % 3 == 0){
                            //переключение на новый IP после трёх удачных запросов
                                System.out.println("В main number кратен 3, смена IP");
                                switchIpForProxy();
                            }
                            //получение моего кода необходимо для того, чтобы достать из map тот ResultProduct, по которому производился поиск аналога
                            myVendorCode = product.getMyVendorCodeFromRequest();
                            System.out.println(number + " - получили результат для задачи № " + myVendorCode);
                            myRefForPage = product.getMyRefForPage();
                            competitorVendorCode = product.getCompetitorVendorCode();
                            competitorProductName = product.getCompetitorProductName();
                            competitorRefForPage = product.getCompetitorRefForPage();
                            competitorRefForImage = product.getCompetitorRefForImage();
                            competitorName = product.getCompetitorName();
                            competitorSpecAction = product.getCompetitorSpecAction();
                            queryForSearch = product.getQueryForSearch();
                            competitorBasicPriceU = product.getCompetitorBasicPriceU();
                            competitorPremiumPriceForOzon = product.getCompetitorPremiumPriceForOzon();

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

                            synchronized (mon) {
                                if (competitorVendorCode.equals("-")) {
                                    if (competitorSpecAction.equals(Constants.BLOCKING)){
                                        controller.getAreaLog().appendText(number + " - " + myVendorCode + " - блокировка!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
                                    } else {
                                        controller.getAreaLog().appendText(number + " - " + myVendorCode + " - ошибка\n");
                                    }
                                } else {
                                    controller.getAreaLog().appendText(number + " - " + myVendorCode + " - ok\n");
                                }
                            }
                        }
                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        System.out.println("для артикула " + myVendorCode + " ошибка - " + e.getMessage());
                        //lock.unlock();
                        e.printStackTrace();
                        outputExcelFile(number);
                    }
                    number++;
                    System.out.println("resultProduct для " + myVendorCode + " записан в map");
                }
                outputExcelFile(number);
            }
        }).start();
    }

    private void outputExcelFile(int number) {
        controller.getAreaLog().appendText("Количество проанализированных позицый - " + number + "\n");
        executorService.shutdown();
        controller.getAreaLog().appendText("Загрузка изображений...");

        if (marketplaceFlag == 1){
            taskWriteExelForOzon.run();
        } else {
            taskWriteExelForWildberries.run();
        }
    }

    public static void switchIpForProxy() throws InterruptedException {
        System.out.println("прверка - lock свободен: " + lock.toString());
        lock.lock();
        int count = 10;//попытки смены IP
        HtmlPage page = null;
        URL uri = null;
        try {
            uri = new URL(Constants.URL_FOR_SWITCH_IP);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("вход в цикл");
        while (count > 0){
            System.out.println("количество попыток смены IP - " + count);
            try {
                webClient.getOptions().setTimeout(10000);
                page = webClient.getPage(uri);
                count = 0;
                DomNodeList<DomElement> metas = page.getElementsByTagName("body");
                //проверка ответа сервером (<body>ok</body>)
                if (metas.get(0).asText().equals("ok")) {
                    System.out.println("смена IP успешна");
                    countSwitchIP++;
                    Thread.sleep(5000);
                }
            } catch (IOException ignored) {
                System.out.println("проблема при смене IP. тело ответа - " + page.asText());
                if (count == 0) {
                    webClient.close();
                    lock.unlock();
                    break;
                }
                count--;
            }
        }
        webClient.close();
        lock.unlock();
        System.out.println("прверка - lock свободен: " + lock.toString());
        System.out.println();
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
        Lock lock;

        public MyCall(String key, String category, String brand, String productType, Set myVendorCodes, String querySearchForOzon, WebClient webClient, Lock lock) {
            this.key = key;
            this.category = category;
            this.brand = brand;
            this.productType = productType;
            this.querySearchForOzon = querySearchForOzon;
            this.myVendorCodes = myVendorCodes;
            this.webClient = webClient;
            this.lock = lock;
        }

        @Override
        //1 - флаг для Ozon
        //2 - флаг для Wildberies
        public Product call() throws Exception {
            if (marketplaceFlag == 1){
                return parserOzon.getProduct(key, category, brand, productType, myVendorCodes, querySearchForOzon, webClient, lock);
            } else if (marketplaceFlag == 2){
                return parserWildBer.getProduct(key, category, brand, productType, myVendorCodes, querySearchForOzon, webClient);
            } else return null;
        }
    }
}
