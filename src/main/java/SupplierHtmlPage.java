import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.locks.Lock;

public class SupplierHtmlPage {

    static HtmlPage getHtmlPage(String url) {
        System.out.println("проверка - lock свободен: " + ParserOzon.lockOzon.toString());
        ParserOzon.lockOzon.lock();
        HtmlPage page = null;
        boolean isBloking = true;
        String blocking = "Блокировка сервером";
        while (isBloking) {
            int count = 10;//количество попыток получения валидной станицы ozon
            //webClientForOzon.getOptions().setTimeout(15000);
            int countPageNull = 0;
            while (count > 0) {
                System.out.println("Непосредственно получение страницы. Время таймаута = " + ParserOzon.webClientForOzon.getOptions().getTimeout() / 1000 + " c");
                try {
                    if (countPageNull == 2){
                        System.out.println("Кол-во полученных страниц NULL = 2. Меняем IP");
                        switchIpForProxy();
                    }
                    if (ParserOzon.countUseIP == 5){
                        System.out.println("Кол-во использования IP № " + ParserOzon.countSwitchIP + " = " + ParserOzon.countUseIP + ". Меняем IP");
                        switchIpForProxy();
                        ParserOzon.countUseIP = 0;
                        ParserOzon.countSwitchIP++;
                    }

                    page = ParserOzon.webClientForOzon.getPage(url);

                    ParserOzon.countUseIP++;

                } catch (Exception ignored) {
                    System.out.println("Ошибка при получении страницы для запроса \"" + ParserOzon.myQuery + "\": " + ignored.getMessage());
                    if (count == 0) {
                        System.out.println("Попытки получения страницы поискового запроса закончились");
                        ParserOzon.webClientForOzon.close();
                        ParserOzon.lockOzon.unlock();
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
        System.out.println(Constants.getGreenString("IP №" + ParserOzon.countSwitchIP) + ". Страница ozon для запроса \"" + ParserOzon.myQuery + "\" получена");
        ParserOzon.lockOzon.unlock();
        System.out.println("проверка - lock свободен: " + ParserOzon.lockOzon.toString());
        ParserOzon.webClientForOzon.close();
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
                page = ParserOzon.webClientForOzon.getPage(uri);
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
}
