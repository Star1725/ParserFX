import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
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

    public Product getProduct(String myVendorCodeFromRequest, String category, String brand, String productType, Set myVendorCodes, String querySearchForOzon, WebClient webClient, Lock lock){
        List<Product> productList = new ArrayList<>();
        Product product = new Product(myVendorCodeFromRequest,
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
        webClientForOzon = webClient;
        lockOzon = lock;
        myQuery = querySearchForOzon;

        StringBuilder query = new StringBuilder(querySearchForOzon);
        //в заввисимости от категории ozon определяем параметры запроса для поиска конкурентов
        switch (productType) {
            case Constants.PRODUCT_TYPE_1C_1 :
            case Constants.PRODUCT_TYPE_1C_2 :
            case Constants.PRODUCT_TYPE_1C_3 :
            case Constants.PRODUCT_TYPE_1C_4 :
            case Constants.PRODUCT_TYPE_1C_5 :
            case Constants.PRODUCT_TYPE_1C_6 :
            case Constants.PRODUCT_TYPE_1C_7 :
            case Constants.PRODUCT_TYPE_1C_8 :
            case Constants.PRODUCT_TYPE_1C_9 :
            case Constants.PRODUCT_TYPE_1C_10 :
            case Constants.PRODUCT_TYPE_1C_11 :
            case Constants.PRODUCT_TYPE_1C_12 :
            case Constants.PRODUCT_TYPE_1C_13 :
            case Constants.PRODUCT_TYPE_1C_14 :
            case Constants.PRODUCT_TYPE_1C_15 :
            case Constants.PRODUCT_TYPE_1C_16 :
            case Constants.PRODUCT_TYPE_1C_17 :
            case Constants.PRODUCT_TYPE_1C_18 :
            case Constants.PRODUCT_TYPE_1C_20 :
            case Constants.PRODUCT_TYPE_1C_21 :
            case Constants.PRODUCT_TYPE_1C_22 :
            case Constants.PRODUCT_TYPE_1C_23 :
            case Constants.PRODUCT_TYPE_1C_24 :
            case Constants.PRODUCT_TYPE_1C_25 :
            case Constants.PRODUCT_TYPE_1C_26 :
            case Constants.PRODUCT_TYPE_1C_27 :
            case Constants.PRODUCT_TYPE_1C_28 :
            case Constants.PRODUCT_TYPE_1C_29 :
            case Constants.PRODUCT_TYPE_1C_30 :
            case Constants.PRODUCT_TYPE_1C_31 :
            case Constants.PRODUCT_TYPE_1C_32 :
            case Constants.PRODUCT_TYPE_1C_33 :
            case Constants.PRODUCT_TYPE_1C_34 :
            case Constants.PRODUCT_TYPE_1C_35 :
            case Constants.PRODUCT_TYPE_1C_36 :
            case Constants.PRODUCT_TYPE_1C_37 :
            case Constants.PRODUCT_TYPE_1C_38 :
            case Constants.PRODUCT_TYPE_1C_39 :
            case Constants.PRODUCT_TYPE_1C_40 :
            case Constants.PRODUCT_TYPE_1C_41 :
            case Constants.PRODUCT_TYPE_1C_42 :
            case Constants.PRODUCT_TYPE_1C_43 :
            case Constants.PRODUCT_TYPE_1C_44 :
            case Constants.PRODUCT_TYPE_1C_45 :
            case Constants.PRODUCT_TYPE_1C_46 :
            case Constants.PRODUCT_TYPE_1C_47 :
            case Constants.PRODUCT_TYPE_1C_48 :
            case Constants.PRODUCT_TYPE_1C_49 :
            case Constants.PRODUCT_TYPE_1C_50 :
            case Constants.PRODUCT_TYPE_1C_51 :
            case Constants.PRODUCT_TYPE_1C_52 :
            case Constants.PRODUCT_TYPE_1C_53 :
            case Constants.PRODUCT_TYPE_1C_54 :
            case Constants.PRODUCT_TYPE_1C_55 :
            case Constants.PRODUCT_TYPE_1C_56 :
            case Constants.PRODUCT_TYPE_1C_57 :
            case Constants.PRODUCT_TYPE_1C_58 :
            case Constants.PRODUCT_TYPE_1C_59 :
            case Constants.PRODUCT_TYPE_1C_60 :
            case Constants.PRODUCT_TYPE_1C_61 :
            case Constants.PRODUCT_TYPE_1C_62 :
            case Constants.PRODUCT_TYPE_1C_63 :
            case Constants.PRODUCT_TYPE_1C_64 :
            case Constants.PRODUCT_TYPE_1C_65 :
            case Constants.PRODUCT_TYPE_1C_66 :
            case Constants.PRODUCT_TYPE_1C_67 :
            case Constants.PRODUCT_TYPE_1C_68 :
            case Constants.PRODUCT_TYPE_1C_69 :
            case Constants.PRODUCT_TYPE_1C_70 :
            case Constants.PRODUCT_TYPE_1C_71 :
            case Constants.PRODUCT_TYPE_1C_72 :
            case Constants.PRODUCT_TYPE_1C_73 :
            case Constants.PRODUCT_TYPE_1C_74 :
            case Constants.PRODUCT_TYPE_1C_75  :
            case Constants.PRODUCT_TYPE_1C_76  :
            case Constants.PRODUCT_TYPE_1C_77  :
            case Constants.PRODUCT_TYPE_1C_78  :
            case Constants.PRODUCT_TYPE_1C_79  :
            case Constants.PRODUCT_TYPE_1C_83  :
            case Constants.PRODUCT_TYPE_1C_84  :
            case Constants.PRODUCT_TYPE_1C_85  :
            case Constants.PRODUCT_TYPE_1C_86  :
            case Constants.PRODUCT_TYPE_1C_87  :
            case Constants.PRODUCT_TYPE_1C_88  :
            case Constants.PRODUCT_TYPE_1C_89  :
            case Constants.PRODUCT_TYPE_1C_90  :
            case Constants.PRODUCT_TYPE_1C_91  :
            case Constants.PRODUCT_TYPE_1C_92  :
            case Constants.PRODUCT_TYPE_1C_93  :
            case Constants.PRODUCT_TYPE_1C_94  :
            case Constants.PRODUCT_TYPE_1C_96  :
            case Constants.PRODUCT_TYPE_1C_97  :
            case Constants.PRODUCT_TYPE_1C_98  :
            case Constants.PRODUCT_TYPE_1C_99  :
            case Constants.PRODUCT_TYPE_1C_100 :
            case Constants.PRODUCT_TYPE_1C_101 :
            case Constants.PRODUCT_TYPE_1C_102 :
            case Constants.PRODUCT_TYPE_1C_103 :
            case Constants.PRODUCT_TYPE_1C_104 :
            case Constants.PRODUCT_TYPE_1C_105 :
            case Constants.PRODUCT_TYPE_1C_106 :
            case Constants.PRODUCT_TYPE_1C_107 :
            case Constants.PRODUCT_TYPE_1C_108 :
            case Constants.PRODUCT_TYPE_1C_109 :
            case Constants.PRODUCT_TYPE_1C_110 :
            case Constants.PRODUCT_TYPE_1C_111:
            case Constants.PRODUCT_TYPE_1C_112:
            case Constants.PRODUCT_TYPE_1C_113:
            case Constants.PRODUCT_TYPE_1C_114:
            case Constants.PRODUCT_TYPE_1C_115:
            case Constants.PRODUCT_TYPE_1C_116:
            case Constants.PRODUCT_TYPE_1C_117:
            case Constants.PRODUCT_TYPE_1C_118:
            case Constants.PRODUCT_TYPE_1C_119:
            case Constants.PRODUCT_TYPE_1C_120:

                System.out.println( "IP №" + Main.countSwitchIP + ".Получение страницы ozon для запроса - " + querySearchForOzon + ". Артикул Ozon - " + myVendorCodeFromRequest);
                productList = getCatalogProducts(query.toString().toLowerCase(), brand);

                if (productList == null){
                    product.setCompetitorProductName(Constants.BLOCKING);
                    product.setQueryForSearch(Constants.BLOCKING);
                    product.setCompetitorRefForPage(Constants.BLOCKING);
                    product.setCompetitorRefForPage(Constants.BLOCKING);
                    product.setCompetitorName(Constants.BLOCKING);
                    product.setCompetitorSpecAction(Constants.BLOCKING);
                } else if (productList.get(0).getCountSearch() == -1){
                    product.setQueryForSearch(productList.get(0).getQueryForSearch());
                } else {
                    Product productbuff = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest);
                    if (productbuff != null){
                        product = productbuff;
                    }
                }
                break;
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
        product.setMyRefForPage(getString("https://www.ozon.ru/search/?text=", myVendorCodeFromRequest, "&from_global=true"));
//                                            https://www.ozon.ru/search/?text=210646439&from_global=true
        //устанавливаем мой vendorCode
        product.setMyVendorCodeFromRequest(myVendorCodeFromRequest);

        return product;
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
        String blocking = "блокировка сервером";

        //final WebClient webClient = new WebClient(BrowserVersion.CHROME);

        int count = 5;//количество попыток получения валидной станицы ozon
        boolean isBloking = true;
        System.out.println("проверка - lock свободен: " + lockOzon.toString());
        lockOzon.lock();
        while (count > 0) {
            try {
                //webClientForOzon.waitForBackgroundJavaScript(5000);
                while (isBloking) {
                    page = webClientForOzon.getPage(url);
                    //проверка на бан сервером (name="ROBOTS")
                    try {
                        DomNodeList<DomElement> metas = page.getElementsByTagName("meta");
                        if (metas.get(0).getAttribute("name").equals("ROBOTS")) {
                            System.out.println(blocking + " Попытка смены IP");
                            Main.switchIpForProxy();
                        } else {
                            isBloking = false;
                        }
                    } catch (Exception ignored) {
                    }
                }
                count = 0;
            } catch (IOException e) {
                System.out.println("Ошибка при получении страницы для запроса \"" + myQuery + "\": " + e.getMessage());
                if (count == 0) {
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
            }
        }
        System.out.println("IP №" + Main.countSwitchIP + ". Страница ozon для запроса \"" + myQuery + "\" получена");
        lockOzon.unlock();
        System.out.println("проверка - lock свободен: " + lockOzon.toString());
        webClientForOzon.close();

        assert page != null;
        String pageString = page.asXml();


        //получаем кол-во найденных аналогов
        List<HtmlElement> itemsCountSearch = page.getByXPath("//div[@class='b6r7']");
        if (itemsCountSearch == null) {
            System.out.println("не нашёл html-элемент - div[@class='b6r7']");
        } else {
            try {
                querySearchAndCount = itemsCountSearch.get(0).asText();
                System.out.println(querySearchAndCount);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("///////////////////////////////////////////////////////////////////////////////////");
                System.out.println(page.asText());
                System.out.println("///////////////////////////////////////////////////////////////////////////////////");
                lockOzon.unlock();
                return null;
            }
        }

        if (querySearchAndCount.contains("товаров сейчас нет")){
            productList.add(new Product("Запрос - " + myQuery + ". " + querySearchAndCount, -1));
            return productList;
        }

        //получаем список продуктов, полученный по поисковому запросу аналогов
        List<HtmlElement> itemsForListProducts = page.getByXPath("//div[@class='a0c4']");

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
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("///////////////////////////////////////////////////////////////////////////////////");
                    System.out.println(page.asText());
                    System.out.println("///////////////////////////////////////////////////////////////////////////////////");
                    lockOzon.unlock();
                    return null;
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
                            DomNodeList<HtmlElement> spanFor_b5v4 = null;
                            String currentBasicPriceString = null;
                            try {
                                divsFor_a0y9 = asFor_a0s9.get(0).getElementsByTagName("div");
                                spanFor_b5v4 = divsFor_a0y9.get(0).getElementsByTagName("span");
                                currentBasicPriceString = spanFor_b5v4.get(0).asText();
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println("///////////////////////////////////////////////////////////////////////////////////");
                                System.out.println(page.asText());
                                System.out.println("///////////////////////////////////////////////////////////////////////////////////");
                                lockOzon.unlock();
                                return null;
                            }
                            competitorBasicPriceU = getPriceFromStringPrice(currentBasicPriceString) * 100;

                            //получение цены currentPriceUString
                            try {
                                String currentPriceUString = spanFor_b5v4.get(1).asText();
                                competitorPriceU = getPriceFromStringPrice(currentPriceUString) * 100;
                            } catch (Exception ignored) {
                            }

                            //получение цены premiumPriceString
                            try {
                                //пробуем получить премиум цену, если есть
                                String premiumPriceString = divsFor_a0y9.get(divsFor_a0y9.size() - 1).asText();
                                if (premiumPriceString.contains("Premium")) {
                                    competitorPremiumPriceForOzon = getPriceFromStringPrice(premiumPriceString) * 100;
                                }
                            } catch (Exception ignored) {
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
                            } catch (Exception ignored) {
                            }

                            //получение цены premiumPriceString
                            try {
                                DomNodeList<HtmlElement> divsFor_a0y9 = asFor_a0s9.get(0).getElementsByTagName("div");
                                //пробуем получить премиум цену, если есть
                                String premiumPriceString = divsFor_a0y9.get(divsFor_a0y9.size() - 1).asText();
                                if (premiumPriceString.contains("Premium")) {
                                    competitorPremiumPriceForOzon = getPriceFromStringPrice(premiumPriceString) * 100;
                                }
                            } catch (Exception ignored) {
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
}