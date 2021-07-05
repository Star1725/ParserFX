import com.gargoylesoftware.htmlunit.html.*;
import com.microsoft.playwright.*;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class SupplierHtmlPage {

    private static final Logger loggerSupplierHtmlPage = Logger.getLogger(SupplierHtmlPage.class.getName());

    static {
        loggerSupplierHtmlPage.addHandler(Main.fileHandler);
    }

    static HtmlPage getHtmlPage(String url) {
        System.out.println("проверка - lock свободен: " + LowerProductFinder.lockOzon.toString());
        LowerProductFinder.lockOzon.lock();
        HtmlPage page = null;
        boolean isBloking = true;
        String blocking = "Блокировка сервером";
        while (isBloking) {
            int count = 10;//количество попыток получения валидной станицы ozon
            //webClientForOzon.getOptions().setTimeout(15000);
            int countPageNull = 0;
            while (count > 0) {
                System.out.println("Непосредственно получение страницы. Время таймаута = " + LowerProductFinder.webClientForOzon.getOptions().getTimeout() / 1000 + " c");
                try {
                    if (countPageNull == 2){
                        System.out.println("Кол-во полученных страниц NULL = 2. Меняем IP");
                        switchIpForProxy();
                    }
                    if (LowerProductFinder.countUseIP == 5){
                        System.out.println("Кол-во использования IP № " + LowerProductFinder.countSwitchIP + " = " + LowerProductFinder.countUseIP + ". Меняем IP");
                        switchIpForProxy();
                        LowerProductFinder.countUseIP = 0;
                        LowerProductFinder.countSwitchIP++;
                    }

                    page = LowerProductFinder.webClientForOzon.getPage(url);

                    Document document = (Document) page.getOwnerDocument();

                    LowerProductFinder.countUseIP++;

                } catch (Exception ignored) {
                    System.out.println("Ошибка при получении страницы для запроса \"" + LowerProductFinder.myQuery + "\": " + ignored.getMessage());
                    if (count == 0) {
                        System.out.println("Попытки получения страницы поискового запроса закончились");
                        LowerProductFinder.webClientForOzon.close();
                        LowerProductFinder.lockOzon.unlock();
                        return null;
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    count--;
                    System.out.println("Осталось попыток: " + count);
                }

                //проверка на null
                if (page == null) {
                    System.out.println("countPageNull = " + countPageNull);
                    countPageNull++;
                    continue;
                }
                count = 0;
            }
            //проверка на бан сервером (name="ROBOTS")
            try {
                DomNodeList<DomElement> metas = page.getElementsByTagName("meta");
                if (metas.get(0).getAttribute("name").equals("ROBOTS")) {
                    System.out.println(Constants.getRedString(blocking) + ". Попытка смены IP");
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
        LowerProductFinder.webClientForOzon.close();
        return page;
    }

    public static void switchIpForProxy() throws InterruptedException {
        int count = 999;//попытки смены IP
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
                page = LowerProductFinder.webClientForOzon.getPage(uri);
                count = 0;
                DomNodeList<DomElement> metas = page.getElementsByTagName("body");
                //проверка ответа сервером (<body>ok</body>)
                if (metas.get(0).asText().equals("ok")) {
                    System.out.println("смена IP успешна");
                    Thread.sleep(2000);
                }
            } catch (IOException e) {
                System.out.println("проблема при смене IP");
                e.printStackTrace();
                if (count == 0) {
//                    webClientForOzon.close();
                    break;
                }
                count--;
            }
        }
    }

//для Wildberries
    static HtmlPage getWildberriesPageFromHtmlUnit(String url) {

//        Document page = null;
        HtmlPage page = null;
        while (page == null){
            try {
                System.out.println("Получение страницы для url = " + url);

//                //page = Jsoup.parse(new URL(url), 30000);
//                Thread.sleep((long)(Math.random() + 1));
//                page = Jsoup.connect(url)
//                        .userAgent("Mozilla")
//                        .timeout(20000)
//                        .referrer("https://google.com")
//                        .get();

                page = LowerProductFinder.webClientForOzon.getPage(url);
//                LowerProductFinder.webClientForOzon.waitForBackgroundJavaScriptStartingBefore (2000);

            } catch (IOException ignored) {
                try {
                    System.out.println("Получаем пустую страницу. Видимо проблемы с соединением");
                    ignored.printStackTrace();
                    //(HttpStatusException) ignored.getCause().
                    Thread.sleep((long)(Math.random() * 5000));
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        //HtmlUnit
        List<HtmlElement> itemsCountSearch = page.getByXPath("//div[@class='searching-results-inner']");
//                                                                         "div[class=searching-results-inner]"
        HtmlDivision divCatalog = null;

        int tries = 20;  // Amount of tries to avoid infinite loop
        while (tries > 0 && itemsCountSearch.size() == 0) {
            tries--;
            synchronized(page) {
                try {
                    page.wait(2000);
                    itemsCountSearch = page.getByXPath("//div[@class='searching-results-inner']");
                    System.out.println(tries);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(page.asXml());

        if (itemsCountSearch == null) {
            System.out.println("не нашёл html-элемент - div[@class='b6e2']");
        } else {
            try {
                String querySearchAndCount = itemsCountSearch.get(0).asText();
                LowerProductFinder.resultSearch = querySearchAndCount;
                System.out.println(Constants.getYellowString(url));
                System.out.println(Constants.getRedString(querySearchAndCount));
                //System.out.println(page.asXml());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                System.out.println(page.asXml());
                HtmlBody htmlBody = (HtmlBody) page.getBody();
                String hrefForNewCatalog = htmlBody.getTextContent();
                System.out.println(hrefForNewCatalog);
                String[] strBuff = hrefForNewCatalog.split("=", 2);
                String temp = strBuff[1].substring(strBuff[1].indexOf("\"") + 1, strBuff[1].lastIndexOf("\""));
                String newUrlForNewCatalog = "https://www.ozon.ru" + temp;
                url = newUrlForNewCatalog;
//                    versionPage = 2;
//                    continue;
            }
        }
        return page;
    }

    static Document getWildberriesPageFromJsoup(String url) {

        Document page = null;
        while (page == null){
            try {
                System.out.println("Получение страницы для url = " + url);

                //page = Jsoup.parse(new URL(url), 30000);
                Thread.sleep((long)(Math.random() + 1));
                page = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .timeout(20000)
                        .referrer("https://google.com")
                        .get();


            } catch (IOException ignored) {
                try {
                    System.out.println("Получаем пустую страницу. Видимо проблемы с соединением");
                    ignored.printStackTrace();
                    //(HttpStatusException) ignored.getCause().
                    Thread.sleep((long)(Math.random() * 5000));
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return page;
    }

    static HtmlPage getWildberriesPageFromSelenium(String url) {
//        WebDriver driver = new FirefoxDriver();
        WebDriver driver = new ChromeDriver();
        driver.navigate().to(url);

        WebElement searches = driver.findElement(By.className("searching-results-inner"));


        HtmlPage page = null;
        return page;
    }

    public static Page getWildberriesPageFromPlaywright(String url) {

        loggerSupplierHtmlPage.info("Получение страницы для url = " + url);
        System.out.println("Получение страницы для url = " + url);

        final Page page = Main.browser.newPage();
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //final ElementHandle bodyContentElement = page.querySelector("xpath=//html/body/div");//работает
        //System.out.println(bodyContentElement.innerHTML());

        //final ElementHandle rootContentElement = page.querySelector("css=div[id=catalog-content]");//работает
        //System.out.println(rootContentElement.innerHTML());

        return page;
    }
}
