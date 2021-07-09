import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.html.*;
import com.microsoft.playwright.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
        System.out.println("проверка - lock свободен: " + Main.lock.toString());
        Main.lock.lock();
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
                        switchIpForProxyFromHtmlUnit();
                    }
                    if (LowerProductFinder.countUseIP_ForOzon == 5){

                        loggerSupplierHtmlPage.info("Кол-во использования IP № " + LowerProductFinder.countSwitchIP + " = " + LowerProductFinder.countUseIP_ForOzon + ". Меняем IP");

                        switchIpForProxyFromHtmlUnit();
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
                        Main.lock.unlock();
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

                    switchIpForProxyFromHtmlUnit();
                } else {
                    isBloking = false;
                }
            } catch (Exception ignored) {
            }
        }
        System.out.println(Constants.getGreenString("IP №" + LowerProductFinder.countSwitchIP) + ". Страница ozon для запроса \"" + LowerProductFinder.myQuery + "\" получена");
        Main.lock.unlock();
        System.out.println("проверка - lock свободен: " + Main.lock.toString());
        Main.webClientHtmlUnit.close();
        return page;
    }

    public static void switchIpForProxyFromHtmlUnit() throws InterruptedException {
        int count = 100;//попытки смены IP
        HtmlPage page = null;
        URL uri = null;
        try {
            uri = new URL(Constants.URL_FOR_SWITCH_IP);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        while (count > 0) {

            loggerSupplierHtmlPage.info("количество попыток смены IP через HtmlUnit - " + count);
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
        }
    }

    public static void switchIpForProxyFromPlaywright() throws InterruptedException {
        int count = 999;//попытки смены IP
        final Page page = Main.browserPlaywright.newPage();
        URL uri = null;
        try {
            uri = new URL(Constants.URL_FOR_SWITCH_IP);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        while (count > 0) {

            loggerSupplierHtmlPage.info("количество попыток смены IP через Playwright - " + count);
            try {
                page.navigate(Constants.URL_FOR_SWITCH_IP);
                count = 0;
                ElementHandle elementHandle = page.querySelector("css=body");
                //проверка ответа сервером (<body>ok</body>)
                if (elementHandle.innerText().equals("ok")) {

                    loggerSupplierHtmlPage.info("смена IP успешна");

                    Thread.sleep(2000);
                    break;
                }
            } catch (Exception e) {

                loggerSupplierHtmlPage.info("проблема при смене IP");

                e.printStackTrace();
                if (count == 0) {
                    break;
                }
                count--;
            }
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
                    switchIpForProxyFromHtmlUnit();
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
                    switchIpForProxyFromHtmlUnit();
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
//        page.setDefaultTimeout(20000);
        page.reload(new Page.ReloadOptions().setTimeout(30000));
        boolean pageIsNotOK = true;
        while (pageIsNotOK) {
            try {

                page.setDefaultTimeout(25000);
                loggerSupplierHtmlPage.info(Constants.getYellowString("непосредственное получение страницы через Playwright."));
                page.navigate(url);
                loggerSupplierHtmlPage.info(Constants.getYellowString("страница получена!!!!!!!!!!!!!!!!!!!!!"));
                pageIsNotOK = false;
            } catch (Exception ignored) {
                System.out.println("Проблемы при получении страницы");
                ignored.printStackTrace();
                try {
                    switchIpForProxyFromPlaywright();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

        Object page = null;
        while (page == null){
            try {
                loggerSupplierHtmlPage.info("Количество использования IP для WB = " + LowerProductFinder.countUseIP_ForWB);
                if (LowerProductFinder.countUseIP_ForWB == 40){
                    loggerSupplierHtmlPage.info(Constants.getYellowString("меняем IP для WB"));
                    switchIpForProxyFromHtmlUnit();
                    LowerProductFinder.countUseIP_ForWB = 0;
                }

                loggerSupplierHtmlPage.info(Constants.getYellowString("непосредственное получение страницы через HtmlUnit"));
                page = Main.webClientHtmlUnit.getPage(url);
                loggerSupplierHtmlPage.info(Constants.getYellowString("страница получена!!!!!!!!!!!!!!!!!!!"));

                if (page instanceof HtmlPage){
                    return (HtmlPage) page;
                } else if (page instanceof TextPage){
                    loggerSupplierHtmlPage.info(Constants.getYellowString("В странице получено сообщение - " + ((TextPage) page).getContent()) + "\n" +
                    "Меняем Ip на прокси-сервере");
                    switchIpForProxyFromPlaywright();
                    page = null;
                    continue;
                }

            } catch (IOException | FailingHttpStatusCodeException ignored) {
                try {
                    System.out.println("Получаем пустую страницу. Видимо проблемы с соединением\n" +
                            ignored.getMessage());

                    switchIpForProxyFromPlaywright();
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
        return (HtmlPage) page;
    }
}
