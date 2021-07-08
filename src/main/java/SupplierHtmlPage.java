import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.*;
import com.microsoft.playwright.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class SupplierHtmlPage {

    private static final Logger loggerSupplierHtmlPage = Logger.getLogger(SupplierHtmlPage.class.getName());

    static {
        loggerSupplierHtmlPage.addHandler(Main.fileHandler);
    }

//для Ozon
    static HtmlPage getOzonPageFromHtmlUnit(String url) {
        System.out.println("проверка - lock свободен: " + LowerProductFinder.lockOzon.toString());
        LowerProductFinder.lockOzon.lock();
        HtmlPage page = null;
        boolean isBloking = true;
        String blocking = "Блокировка сервером";
        while (isBloking) {
            int count = 10;//количество попыток получения валидной станицы ozon
            int countPageNull = 0;
            while (count > 0) {

                loggerSupplierHtmlPage.info("Непосредственно получение страницы. Время таймаута = " + Main.webClientHtmlUnit.getOptions().getTimeout() / 1000 + " c");

                try {
                    if (countPageNull == 2){
                        loggerSupplierHtmlPage.info("Кол-во полученных страниц NULL = 2. Меняем IP");
                        switchIpForProxy();
                    }
                    if (LowerProductFinder.countUseIP_ForOzon == 5){

                        loggerSupplierHtmlPage.info("Кол-во использования IP № " + LowerProductFinder.countSwitchIP + " = " + LowerProductFinder.countUseIP_ForOzon + ". Меняем IP");

                        switchIpForProxy();
                        LowerProductFinder.countUseIP_ForOzon = 0;
                        LowerProductFinder.countSwitchIP++;
                    }

                    page = Main.webClientHtmlUnit.getPage(url);

                    LowerProductFinder.countUseIP_ForOzon++;

                } catch (Exception ignored) {

                    loggerSupplierHtmlPage.info("Ошибка при получении страницы для запроса \"" + LowerProductFinder.myQuery + "\": " + ignored.getMessage());
                    if (count == 0) {

                        loggerSupplierHtmlPage.info("Попытки получения страницы поискового запроса закончились");

                        Main.webClientHtmlUnit.close();
                        LowerProductFinder.lockOzon.unlock();
                        return null;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    count--;

                    loggerSupplierHtmlPage.info("Осталось попыток: " + count);
                }

                //проверка на null
                if (page == null) {

                    loggerSupplierHtmlPage.info("countPageNull = " + countPageNull);

                    countPageNull++;
                    continue;
                }
                count = 0;
            }
            //проверка на бан сервером (name="ROBOTS")
            try {
                DomNodeList<DomElement> metas = page.getElementsByTagName("meta");
                if (metas.get(0).getAttribute("name").equals("ROBOTS")) {

                    loggerSupplierHtmlPage.info(Constants.getRedString(blocking) + ". Попытка смены IP");

                    switchIpForProxy();
                } else {
                    isBloking = false;
                }
            } catch (Exception ignored) {
            }
        }
        System.out.println(Constants.getGreenString("IP №" + LowerProductFinder.countSwitchIP) + ". Страница ozon для запроса \"" + LowerProductFinder.myQuery + "\" получена");
        LowerProductFinder.lockOzon.unlock();
        System.out.println("проверка - lock свободен: " + LowerProductFinder.lockOzon.toString());
        Main.webClientHtmlUnit.close();
        return page;
    }

    public static void switchIpForProxy() throws InterruptedException {
        int count = 999;//попытки смены IP
        HtmlPage page = null;
        Document pageJsoup = null;
        URL uri = null;
        try {
            uri = new URL(Constants.URL_FOR_SWITCH_IP);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        while (count > 0){

            loggerSupplierHtmlPage.info("количество попыток смены IP - " + count);
//            if (Main.marketplaceFlag == 1){
                try {
                    page = Main.webClientHtmlUnit.getPage(uri);
                    count = 0;
                    DomNodeList<DomElement> metas = page.getElementsByTagName("body");
                    //проверка ответа сервером (<body>ok</body>)
                    if (metas.get(0).asText().equals("ok")) {

                        loggerSupplierHtmlPage.info("смена IP успешна");

                        Thread.sleep(2000);
                        break;
                    }
                } catch (IOException e) {

                    loggerSupplierHtmlPage.info("проблема при смене IP");

                    e.printStackTrace();
                    if (count == 0) {
                        break;
                    }
                    count--;
                }
//            } else if (Main.marketplaceFlag == 2){
//                try {
//                    pageJsoup = Jsoup.connect(Constants.URL_FOR_SWITCH_IP)
//                            .get();
//
//                    Element elementBody = pageJsoup.select("body").first();
//                    //проверка ответа сервером (<body>ok</body>)
//                    if (elementBody.text().equals("ok")){
//                        loggerSupplierHtmlPage.info("смена IP успешна");
//
//                        Thread.sleep(2000);
//                        break;
//                    }
//                } catch (IOException e) {
//
//                    loggerSupplierHtmlPage.info("проблема при смене IP");
//
//                    e.printStackTrace();
//                    if (count == 0) {
//                        break;
//                    }
//                    count--;
//                }
//            }
        }
    }

//для Wildberries
    static Document getWBPageFromJsoup(String url) {

        Document page = null;
        while (page == null){
            try {
                loggerSupplierHtmlPage.info("Количество использования IP для WB = " + LowerProductFinder.countUseIP_ForWB);
                if (LowerProductFinder.countUseIP_ForWB == 20){
                    loggerSupplierHtmlPage.info(Constants.getYellowString("меняем IP для WB"));
                    switchIpForProxy();
                    LowerProductFinder.countUseIP_ForWB = 0;
                }

                loggerSupplierHtmlPage.info("Получение страницы для url = " + url);

                Thread.sleep((long)(Math.random() + 1));
                page = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .timeout(20000)
                        .get();

                LowerProductFinder.countUseIP_ForWB++;

            } catch (IOException ignored) {
                try {
                    System.out.println("Получаем пустую страницу. Видимо проблемы с соединением\n" +
                            ignored.getMessage());
                    switchIpForProxy();
                    LowerProductFinder.countUseIP_ForWB = 0;
                    Thread.sleep((long)(Math.random() * 5000) + 300000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return page;
    }

    public static Page getWBPageFromPlaywright(String url) {

        loggerSupplierHtmlPage.info("Получение с помощью Playwright страницы для url = " + url);

        final Page page = Main.browserPlaywright.newPage();
        page.setDefaultTimeout(15000);
        boolean pageIsNotOK = true;
        while (pageIsNotOK) {
            try {
                page.navigate(url);
                pageIsNotOK = false;
            } catch (Exception ignored) {
                ignored.getMessage();
            }
        }
        //задержка, что бы javascript прогрузился до конца
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return page;
    }

    public static HtmlPage getWBPageFromHtmlUnit(String url) {

        loggerSupplierHtmlPage.info("Получение с помощью HtmlUnit страницы для url = " + url);

        HtmlPage page = null;
        while (page == null){
            try {
                loggerSupplierHtmlPage.info("Количество использования IP для WB = " + LowerProductFinder.countUseIP_ForWB);
                if (LowerProductFinder.countUseIP_ForWB == 40){
                    loggerSupplierHtmlPage.info(Constants.getYellowString("меняем IP для WB"));
                    switchIpForProxy();
                    LowerProductFinder.countUseIP_ForWB = 0;
                }

                loggerSupplierHtmlPage.info(Constants.getYellowString("непосредственное получение страницы"));

                page = Main.webClientHtmlUnit.getPage(url);

            } catch (IOException | FailingHttpStatusCodeException ignored) {
                try {
                    System.out.println("Получаем пустую страницу. Видимо проблемы с соединением\n" +
                            ignored.getMessage());
                    switchIpForProxy();
                    LowerProductFinder.countUseIP_ForWB = 0;
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loggerSupplierHtmlPage.info(Constants.getYellowString("страница получена!!!!!!!!!!!!!!!!"));

            LowerProductFinder.countUseIP_ForWB++;
        }
        return page;
    }
}
