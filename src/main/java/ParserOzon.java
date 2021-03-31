import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.cassandra.streaming.StreamOut;
import org.w3c.dom.DOMException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;

public class ParserOzon {

    private static final Object mon = new Object();
    private static WebClient webClientForOzon;
    private static Lock lockOzon;
    private static String myQuery;
    private static String myVendorCodeFromRequest;

    public Product getProduct(String vendorCodeFromRequest, String category, String brand, String productType, Set myVendorCodes, String querySearchForOzon, WebClient webClient, Lock lock){
        List<Product> productList = new ArrayList<>();
        Product product = new Product(vendorCodeFromRequest,
                "-",
                "-",
                "-",
                "-",

                "-",
                0,

                "-",
                "-",
                "по вашему запросу ничего не найдено",
                "-",
                "-",
                "-",
                0,

                0,
                0,
                0,
                0,
                0,
                0,

                "-");

        if (querySearchForOzon.equals("-")){
            product.setQueryForSearch("отсутствует поисковый запрос");
            return product;
        } else {
            webClientForOzon = webClient;
            lockOzon = lock;
            myQuery = querySearchForOzon;
            myVendorCodeFromRequest = vendorCodeFromRequest;

            StringBuilder query = new StringBuilder(querySearchForOzon);

            productList = getCatalogProducts(query.toString().toLowerCase(), brand);

            if (productList == null) {
                product.setCompetitorProductName(Constants.BLOCKING);
                product.setQueryForSearch(Constants.BLOCKING);
                product.setCompetitorRefForPage(Constants.BLOCKING);
                product.setCompetitorRefForPage(Constants.BLOCKING);
                product.setCompetitorName(Constants.BLOCKING);
                product.setCompetitorSpecAction(Constants.BLOCKING);
            } else if (productList.get(0).getCountSearch() == -1) {
                product.setQueryForSearch(productList.get(0).getQueryForSearch());
            } else {
                Product productbuff = getProductWithLowerPrice(productList, myVendorCodes, vendorCodeFromRequest);
                if (productbuff != null) {
                    product = productbuff;
                }
            }


//
//        //устанавливаем мою спецакцию, если она есть
//        product.setMySpecAction(getMySpecAction(page));
//
//        //устанавливаем ссылку на картинку моего товара
//        product.setMyRefForImage(getMyProductsPhoto(page));
            product.setMyRefForImage("-");
//
//        //устанавливаем поисковый запрос аналогов
//        product.setQueryForSearch(query.toString());
//
//        //устанавливаем наименование моего товара
//        product.setMyProductName(getMyProductsTitle(page));
//
            //устанавливаем ссылку на артикул моего товара
            product.setMyRefForPage(getString("https://www.ozon.ru/search/?text=", vendorCodeFromRequest, "&from_global=true"));
//                                            https://www.ozon.ru/search/?text=210646439&from_global=true
            //устанавливаем мой vendorCode
            product.setMyVendorCodeFromRequest(vendorCodeFromRequest);

            return product;
        }
    }

    private static List<Product> getCatalogProducts(String query, String brand) {
        List<Product> productList;
        String url = "-";
        url = getUrlForSearchQuery(query);

        //получение бренда, артикула, имени товара, ссылки на страницу товара, ссылки на картинкау товара, спец-акции, рейтинга
        productList = getCatalogFromFPageForHtmlUnit(url);

        return productList;
    }

    private static String getUrlForSearchQuery(String query) {
        String url = "-";
        url = getString("https://www.ozon.ru/search/?from_global=true&sorting=price&text=", getQueryUTF8(query), "");
        return url;
    }

    private static List<Product> getCatalogFromFPageForHtmlUnit(String url) {
        List<Product> productList = new ArrayList<>();
        HtmlPage page = null;
        String querySearchAndCount = "-";
        String category = "-";
        String blocking = "Блокировка сервером";

        //final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        System.out.println("IP №" + Main.countSwitchIP + ".Получение страницы ozon для запроса - " + myQuery + ". Артикул Ozon - " + myVendorCodeFromRequest);
        boolean isNotGetValidPage = true;
        while (isNotGetValidPage){
            System.out.println("проверка - lock свободен: " + lockOzon.toString());
            lockOzon.lock();
            boolean isBloking = true;
            while (isBloking) {
                int count = 10;//количество попыток получения валидной станицы ozon
                webClientForOzon.getOptions().setTimeout(15000);
                int countPageNull = 0;
                while (count > 0) {
                    System.out.println("Непосредственно получение страницы. Время таймаута = " + webClientForOzon.getOptions().getTimeout() / 1000 + " c");
                    try {
                        if (countPageNull == 2){
                            System.out.println("Кол-во полученных страниц NULL = 2. Меняем IP");
                            Main.switchIpForProxy();
                        }
                        page = webClientForOzon.getPage(url);
                    } catch (Exception ignored) {
                        System.out.println("Ошибка при получении страницы для запроса \"" + myQuery + "\": " + ignored.getMessage());
                        if (count == 0) {
                            System.out.println("Попытки получения страницы поискового запроса закончились");
                            webClientForOzon.close();
                            lockOzon.unlock();
                            return productList;
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
                        System.out.println(getRedString(blocking) + ". Попытка смены IP");
                        Main.switchIpForProxy();
                    } else {
                        isBloking = false;
                    }
                } catch (Exception ignored) {
                }
            }
            System.out.println(getGreenString("IP №" + Main.countSwitchIP) + ". Страница ozon для запроса \"" + myQuery + "\" получена");
            lockOzon.unlock();
            System.out.println("проверка - lock свободен: " + lockOzon.toString());
            webClientForOzon.close();

            //получаем кол-во найденных аналогов
            List<HtmlElement> itemsCountSearch = page.getByXPath("//div[@class='b6e2']");
            if (itemsCountSearch == null) {
                System.out.println("не нашёл html-элемент - div[@class='b6e2']");
            } else {
                try {
                    querySearchAndCount = itemsCountSearch.get(0).asText();
                    System.out.println(querySearchAndCount);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                    System.out.println(page.asXml());
                    continue;
                }
            }

            if (querySearchAndCount.contains("товаров сейчас нет")){
                productList.add(new Product("Запрос - " + myQuery + ". " + querySearchAndCount, -1));
                return productList;
            }

            //получаем список продуктов, полученный по поисковому запросу аналогов
            List<HtmlElement> itemsForListProducts = page.getByXPath("//div[@class='a0c4']");
            boolean isException1 = false;
            boolean isException2 = false;
            boolean isException3 = false;
            boolean isException4 = false;
            boolean isException5 = false;
            boolean isException6 = false;
            if (itemsForListProducts.isEmpty()) {
            } else {
                for (HtmlElement itemProduct: itemsForListProducts) {
                    String competitorBrand = "-";
                    String refForProduct = "-";
                    String refImage = "-";
                    String vendorCode = "-";
                    String productDescription = "-";
                    String seller = "-";
                    int intSale = 0;
                    int competitorBasicPriceU = 0;
                    int competitorPriceU = 0;
                    int competitorPremiumPriceForOzon = 0;

                    int versionPage = 0;

                    //получение ссылки на продукт
                    try {
                        refForProduct = "https://www.ozon.ru" + itemProduct.getFirstChild().getAttributes().getNamedItem("href").getNodeValue();
                        versionPage = 1;
                    } catch (NullPointerException e) {
                        refForProduct = "https://www.ozon.ru" + itemProduct.getFirstChild().getFirstChild().getFirstChild().getAttributes().getNamedItem("href").getNodeValue();
                        versionPage = 2;
                    }
                    String[] arrayBuff1 = refForProduct.split("/");
                    for (int i = 0; i < arrayBuff1.length; i++) {
                        if (arrayBuff1[i].equals("id")){
                            vendorCode = arrayBuff1[i + 1];
                            break;
                        }
                    }
                    if (vendorCode.equals("-")){
                        String[] arrayBuff2 = refForProduct.split("-");
                        String vendorCodeBuff = arrayBuff2[arrayBuff2.length - 1];
                        vendorCode = vendorCodeBuff.substring(0, vendorCodeBuff.length() - 1);
                    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //исчем 3 элемента,
                    // если versionPage = 1, то 1 элемент - ссылка на картинку, 2 элемент - цены, описание, продавец
                    // если versionPage = 2, то 1 элемент - ссылка на картинку, 2 элемент - описание, продавец, 3 элемент - цены
                    Iterable<DomElement> elementsFor_a0c4 = null;
                    try {
                        elementsFor_a0c4 = getDomElements(itemProduct);
                    } catch (Exception ignored) {
                        //e.printStackTrace();
                        System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                        isException6 = true;
                        System.out.println("isException6 = " + isException6);
                    }

                    int childFor_a0c4 = 1;

                    for (DomElement elementFor_a0t0 : elementsFor_a0c4) {
                        //1 элемент - получение ссылки на картинку
                        if (childFor_a0c4 == 1) {
                            //refImage = elementFor_a0t0.getFirstChild().getFirstChild().getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
                            refImage = elementFor_a0t0.getElementsByTagName("img").get(0).getAttributes().getNamedItem("src").getNodeValue();
                        }

                        //2 элемент
                        if (childFor_a0c4 == 2) {
                            DomNodeList<HtmlElement> asFor_a0s9 = elementFor_a0t0.getElementsByTagName("a");
                            if (versionPage == 1) {//цены, описание, продавец
                                //получение цен: currentBasicPriceString, competitorPriceU
                                DomNodeList<HtmlElement> divsFor_a0y9 = null;
                                DomNodeList<HtmlElement> elementsFor_b5v4 = null;
                                String currentBasicPriceString = null;
                                try {
                                    divsFor_a0y9 = asFor_a0s9.get(0).getElementsByTagName("div");
                                    elementsFor_b5v4 = divsFor_a0y9.get(0).getElementsByTagName("span");
                                    currentBasicPriceString = elementsFor_b5v4.get(0).asText();
                                } catch (Exception e) {
                                    if (divsFor_a0y9.size() == 5){
                                        elementsFor_b5v4 = divsFor_a0y9.get(2).getElementsByTagName("span");
                                        currentBasicPriceString = elementsFor_b5v4.get(0).asText();
                                    }
                                    if (divsFor_a0y9.size() == 6){
                                        elementsFor_b5v4 = divsFor_a0y9.get(3).getElementsByTagName("span");
                                        currentBasicPriceString = elementsFor_b5v4.get(0).asText();
                                    }
                                }
                                competitorBasicPriceU = getPriceFromStringPrice(currentBasicPriceString) * 100;

                                //получение цены currentPriceUString
                                try {
                                    String currentPriceUString = elementsFor_b5v4.get(1).asText();
                                    competitorPriceU = getPriceFromStringPrice(currentPriceUString) * 100;
                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
//                                    isException2 = true;
//                                    System.out.println("isException2 = " + isException2);
                                }

                                //получение цены premiumPriceString
                                try {
                                    //пробуем получить премиум цену, если есть
                                    String premiumPriceString = divsFor_a0y9.get(divsFor_a0y9.size() - 1).asText();
                                    if (premiumPriceString.contains("Premium")) {
                                        competitorPremiumPriceForOzon = getPriceFromStringPrice(premiumPriceString) * 100;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                                    isException3 = true;
                                    System.out.println("isException3 = " + isException3);
                                }

                                //получение описания продукта
                                productDescription = asFor_a0s9.get(1).asText();
                                //определяем какой бренд
                                competitorBrand = "-";
                                for (String s : Constants.listForBrands) {
                                    if (productDescription.contains(s)) {
                                        competitorBrand = s;
                                        break;
                                    }
                                }

                                //получение имени продавца
                                DomNodeList<HtmlElement> spanFor_a0s9 = elementFor_a0t0.getElementsByTagName("span");
                                //DomNodeList<HtmlElement> spansFor_a0t6 = divsFor_a0s9.get(1).getElementsByTagName("span");
                                seller = spanFor_a0s9.get(spanFor_a0s9.size() - 1).asText();
                            }
                            if (versionPage == 2) {//описание, продавец
                                //получение описания продукта
                                productDescription = asFor_a0s9.get(0).asText();
                                //получение имени продавца
                                DomNodeList<HtmlElement> spanFor_a0s9 = elementFor_a0t0.getElementsByTagName("span");
                                //DomNodeList<HtmlElement> spansFor_a0t6 = divsFor_a0s9.get(1).getElementsByTagName("span");
                                seller = spanFor_a0s9.get(spanFor_a0s9.size() - 1).asText();
                            }
                        }
                        //3 элемент
                        if (childFor_a0c4 == 3){
                            DomNodeList<HtmlElement> asFor_a0s9 = elementFor_a0t0.getElementsByTagName("a");
                            if (versionPage == 2){// - цены
                                //получение цен: currentBasicPriceString, competitorPriceU
                                DomNodeList<HtmlElement> spanFor_a0t0 = asFor_a0s9.get(0).getElementsByTagName("span");
                                String currentBasicPriceString = spanFor_a0t0.get(0).asText();
                                competitorBasicPriceU = getPriceFromStringPrice(currentBasicPriceString) * 100;

                                //получение цены currentPriceUString
                                try {
                                    String currentPriceUString = spanFor_a0t0.get(1).asText();
                                    competitorPriceU = getPriceFromStringPrice(currentPriceUString) * 100;
                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
//                                    isException4 = true;
//                                    System.out.println("isException4 = " + isException4);
                                }

                                //получение цены premiumPriceString
                                try {
                                    DomNodeList<HtmlElement> divsFor_a0y9 = asFor_a0s9.get(0).getElementsByTagName("div");
                                    //пробуем получить премиум цену, если есть
                                    String premiumPriceString = divsFor_a0y9.get(divsFor_a0y9.size() - 1).asText();
                                    if (premiumPriceString.contains("Premium")) {
                                        competitorPremiumPriceForOzon = getPriceFromStringPrice(premiumPriceString) * 100;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                                    isException5 = true;
                                    System.out.println("isException5 = " + isException5);
                                }
                            }
                        }
                        childFor_a0c4++;
                    }

                    productList.add(new Product(
                            "-",
                            "-",
                            "-",
                            "-",
                            "-",

                            querySearchAndCount,
                            0,

                            competitorBrand,
                            vendorCode,
                            productDescription,
                            refForProduct,
                            refImage,
                            "-",
                            0,
                            competitorPriceU,
                            intSale,
                            competitorBasicPriceU,
                            0,
                            0,
                            competitorPremiumPriceForOzon,

                            seller
                    ));
                }
            }
            isNotGetValidPage = isException1 || isException2 || isException3 || isException4 || isException5 || isException6;
            if (isNotGetValidPage){
                System.out.println("//////////////////////////////////////Попытка получения новой валидной страницы//////////////////////////////////////");
                System.out.println("isNotGetValidPage = " + isNotGetValidPage);
            }
        }
        return productList;
    }

    private static Iterable<DomElement> getDomElements(HtmlElement itemProduct) throws Exception {

        while (itemProduct.getChildElementCount() != 3) {
            itemProduct = (HtmlElement) itemProduct.getFirstChild();
        }
        return itemProduct.getChildElements();
    }

    private static int getPriceFromStringPrice(String price) {
        StringBuilder resultPrice = new StringBuilder();

        Pattern p = Pattern.compile("-?\\d+");

        Matcher m = p.matcher(price);
        while (m.find()) {
            resultPrice.append(m.group());
        }
        return Integer.parseInt(resultPrice.toString());
    }

    private static Product getProductWithLowerPrice(List<Product> productList, Set myVendorCodes, String myVendorCodeFromRequest) {
        if (productList.size() == 1){
            return productList.get(0);
        } else {
            Product product = null;
            productList.sort(comparing(Product::getCompetitorLowerPriceU));
            for (Product p : productList) {
                if (!p.getCompetitorName().equals(Constants.MY_SELLER)) {
                    product = p;
                    break;
                }
            }
            if (product == null){
                try {
                    product = productList.get(0);
                } catch (Exception e) {
                    System.out.println("Ошибка IndexOutOfBoundsException для " + myVendorCodeFromRequest + ". productList = " + productList.size());
                }
            }
            return product;
        }
    }

    private static String getString(String s, String queryUTF8, String s2) {
        return s + queryUTF8 + s2;
    }
//
    private static String getQueryUTF8(String query){
        String queryUTF8 = null;
        try {
            queryUTF8 = URLEncoder.encode(query, "UTF-8");
            //замена символа "+" на код "%20"
            queryUTF8 = queryUTF8.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Ошибка декодирования в UTF8");
        }
        return queryUTF8;
    }

    private static String getRedString(String s){
        /*
        Если хочешь другой цвет, то измени "[31mWarning!". Например, на "[35mWarning!". Текст будет пурпурным.
        30 - черный. 31 - красный. 32 - зеленый. 33 - желтый. 34 - синий. 35 - пурпурный. 36 - голубой. 37 - белый.
         */
        return (char) 27 + "[31m" + s + (char)27 + "[0m";
    }

    private static String getBlueString(String s){
        /*
        Если хочешь другой цвет, то измени "[31mWarning!". Например, на "[35mWarning!". Текст будет пурпурным.
        30 - черный. 31 - красный. 32 - зеленый. 33 - желтый. 34 - синий. 35 - пурпурный. 36 - голубой. 37 - белый.
         */
        return (char) 27 + "[34m" + s + (char)27 + "[0m";
    }

    private static String getGreenString(String s){
        /*
        Если хочешь другой цвет, то измени "[31mWarning!". Например, на "[35mWarning!". Текст будет пурпурным.
        30 - черный. 31 - красный. 32 - зеленый. 33 - желтый. 34 - синий. 35 - пурпурный. 36 - голубой. 37 - белый.
         */
        return (char) 27 + "[32m" + s + (char)27 + "[0m";
    }
}
