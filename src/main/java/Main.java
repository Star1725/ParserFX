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

    private static Set<String> setMyVendorCodes;

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

        preFld = Double.parseDouble(controller.percentTxtFld.getText());

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
                setMyVendorCodes = resultMap.keySet();

                for (Map.Entry<String, ResultProduct> entry : resultMap.entrySet()) {
                    String key = entry.getKey();
                    String category = entry.getValue().getCategory();
                    String brand = entry.getValue().getMyBrand();

                    myCalls.add(new MyCall(key, category, brand, setMyVendorCodes));
                }

                List<Future<Product>> futureList = new ArrayList<>();

                for (MyCall myCall : myCalls) {
                    futureList.add(executorCompletionService.submit(myCall));
                }

                for (int i =0; i < futureList.size(); i++) {
                    String myVendorCode = null;
                    try {
                        Product resultProduct = executorCompletionService.take().get();
                        //получение моего кода необходимо для того, чтобы достать из map тот ResultProduct, по которому производился поиск аналога
                        myVendorCode = resultProduct.getMyVendorCodeFromRequest();

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

                        //если аналог это мой товар, то всё оставляю без изменений
                        if (myVendorCode.equals(competitorVendorCode) || competitorVendorCode.equals("-") || setMyVendorCodes.contains(competitorVendorCode)) {
                            resultProductTemp.setRecommendedPromoPriceU(resultProductTemp.getMyLowerPriceU());
                            resultProductTemp.setRecommendedSale(resultProductTemp.getMyBasicSale());
                            resultProductTemp.setRecommendedPromoSale(resultProductTemp.getMyPromoSale());

                        }
                        //иначе
                        //если розничная цена конкурента меньше моей розничной цены
                        else if (resultProductTemp.getLowerPriceU() < resultProductTemp.getMyLowerPriceU()) {
                            //расчитываем рекомендованню розничную цену с учётом процента демпинга
                            int recommendedPriceU = (int) Math.round(resultProductTemp.getLowerPriceU() * present);
                            resultProductTemp.setRecommendedPromoPriceU(recommendedPriceU);
                            //и на основании этой рекомендованной цены расчитываем базоваю скидку и промо-скидку
                            //если у моего товара нет ни базовой ни промо скидки или есть только промо скидка
                            if ((resultProductTemp.getMyBasicSale() == 0 & resultProductTemp.getMyPromoSale() == 0) || (resultProductTemp.getMyBasicSale() == 0 & resultProductTemp.getMyPromoSale() != 0)) {
                                //пробуем базоваю скидку = 25%
                                int recommendedSale = 25;
                                //и расчитываем промо скидку
                                int recommendedPromoSale = 100 - (int) Math.round((double) resultProductTemp.getRecommendedPromoPriceU() / (resultProductTemp.getMyPriceU() * 0.75) * 100);
                                //если промо скидка получается отрицательной
                                if (recommendedPromoSale < 0) {
                                    //то расчитываем только базоваю скидку
                                    recommendedSale = 100 - (int) Math.round((double) resultProductTemp.getRecommendedPromoPriceU() / (resultProductTemp.getMyPriceU()) * 100);
                                    //и устанавливаем её в качестве рекомендованной базовой скидки
                                    resultProductTemp.setRecommendedSale(recommendedSale);
                                    //а рекомендованную промо устанавливаем в 0
                                    resultProductTemp.setRecommendedPromoSale(0);
                                    //иначе устанавливаем рекомендованную базоваю в 25% и рекомендованную расчитанную промо
                                } else {
                                    resultProductTemp.setRecommendedSale(recommendedSale);
                                    resultProductTemp.setRecommendedPromoSale(recommendedPromoSale);
                                }
                                //если у моего товара есть только базовая скидка или есть и базовая и промо
                            } else if ((resultProductTemp.getMyBasicSale() != 0 & resultProductTemp.getMyPromoSale() == 0) || (resultProductTemp.getMyBasicSale() != 0 & resultProductTemp.getMyPromoSale() != 0)) {
                                //установливаем рекомендованную базоваю без изменений
                                resultProductTemp.setRecommendedSale(resultProductTemp.getMyBasicSale());
                                //а рекомендованную промо расчитываем
                                resultProductTemp.setRecommendedPromoSale((100 - (int) Math.round((double) resultProductTemp.getRecommendedPromoPriceU() / resultProductTemp.getMyBasicPriceU() * 100)));
                            }
                        }
                        //иначе (если розничная цена конкурента больше моей розничной цены) --- Стратегия на повышение
                        else {
                            //расчитываем рекомендованню розничную цену с учётом процента демпинга
                            int recommendedPriceU = (int) Math.round(resultProductTemp.getLowerPriceU() * present);
                            resultProductTemp.setRecommendedPromoPriceU(recommendedPriceU);
                            //устанавливаем рекомендованную базоваю скидку = 25% и промо в 10%
                            //и на основании этих данных расчитывае новую рекомендованнюую цену до скидки
                            int basicPriceU = Math.toIntExact(Math.round(recommendedPriceU / (0.9)));
                            int priceU = Math.toIntExact(Math.round(basicPriceU / (0.75)));
                            resultProductTemp.setRecommendedPriceU(priceU);
                            resultProductTemp.setRecommendedSale(25);
                            resultProductTemp.setRecommendedPromoSale(10);
                        }

                        resultMap.put(resultProductTemp.getMyVendorCodeForWildberies(), resultProductTemp);

                        int finalI = i + 1;

                        synchronized (mon) {
                            if (competitorVendorCode.equals("-")) {
                                controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ошибка\n");
                            } else {
                                controller.getAreaLog().appendText(finalI + " - " + myVendorCode + " - ok\n");
                            }
                        }
                    } catch (InterruptedException | ExecutionException | NullPointerException e) {
                        System.out.println("для артикула " + myVendorCode + " ошибка - " + e.getMessage());
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
        Set myVendorCodes;

        public MyCall(String key, String category, String brand, Set myVendorCodes) {
            this.key = key;
            this.category = category;
            this.brand = brand;
            this.myVendorCodes = myVendorCodes;
        }

        @Override
        public Product call() throws Exception {
            return parserWildBer.getProduct(key, category, brand, myVendorCodes);
        }
    }
}
