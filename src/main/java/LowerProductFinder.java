import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.microsoft.playwright.Page;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

import static java.util.Comparator.comparing;

public class LowerProductFinder {

    static String refUrlForResult = "-";
    static String resultSearch = "Для данного запроса ничего не найдено";
    static String myQuery;

    static WebClient webClientForOzon;
    static Lock lockOzon;

    static String myVendorCodeFromRequest;

    static int countUseIP_ForOzon;
    static int countUseIP_ForWB;
    public static int countSwitchIP = 1;

    private static final Map<String, List<Product>> resultMapForQueries = new LinkedHashMap<>();

    private static final Logger loggerLowerProductFinder = Logger.getLogger(LowerProductFinder.class.getName());

    static {
        loggerLowerProductFinder.addHandler(Main.fileHandler);
    }

    public Product getProduct(int marketplaceFlag, String myVendorCodeFromRequest, String productBrand, String productType, String productModel, List<List<String>> arrayParams, Set myVendorCodes, String specQuerySearch, WebClient webClient, Lock lock) {
        List<Product> productList = null;

        if (specQuerySearch.equals("-") || specQuerySearch.equals("")) {
            myQuery = productBrand.toLowerCase() + " " + productModel.toLowerCase();
        } else {
            myQuery = specQuerySearch;
        }

        Product product = new Product(myVendorCodeFromRequest,
                "-",
                "-",
                "-",
                "-",

                myQuery,
                "-",

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
        LowerProductFinder.myVendorCodeFromRequest = myVendorCodeFromRequest;

        if (productBrand.toLowerCase().equals("xivi")){
            loggerLowerProductFinder.info("Xivi - пропускаем запрос");
            product.setCompetitorProductName("запросы для бренда \"Xivi\" игнорируются");
            return product;
        } else if (productBrand.toLowerCase().equals("mietubl")){
            loggerLowerProductFinder.info("Mietubl - пропускаем запрос");
            product.setCompetitorProductName("запросы для бренда \"Mietubl\" игнорируются");
            return product;
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//получение страницы с моим товаром по коду myVendorCodeFromRequest
        String refForMyImage = "-";
        String mySpecAction = "-";
        Document myPage = null;
        HtmlPage pageForMyProduct = null;

        loggerLowerProductFinder.info(Constants.getGreenString("Получение страницы моего товара, чтобы получить информацию о моём товаре (картинка, спецакция ...)"));

        if (marketplaceFlag == 1){//Flag = 1 - Для Ozon. Получение html-страницы для моего артикула через HtmlUnit (Jsoup ozon сразу банит)
        try {
            pageForMyProduct = SupplierHtmlPage.getOzonPageFromHtmlUnit("https://www.ozon.ru/search/?from_global=true&text=" + myVendorCodeFromRequest);
            refForMyImage = ParserHTMLForOzon.getRefMyProductsImage(pageForMyProduct);
        } catch (Exception e) {
            loggerLowerProductFinder.info("Ошибка при получении страницы моего товара!");
            e.printStackTrace();
            loggerLowerProductFinder.info(pageForMyProduct.asXml());
        }
    } else if (marketplaceFlag == 2){//Flag = 2 - Для WB. Получение html-страницы для моего артикула
        pageForMyProduct = SupplierHtmlPage.getWBPageFromHtmlUnit("https://www.wildberries.ru/catalog/" + myVendorCodeFromRequest + "/detail.aspx?targetUrl=SP");
        refForMyImage = ParserHtmlForWildberries.getMyProductImageFromHtmlUnit(pageForMyProduct);
        mySpecAction = ParserHtmlForWildberries.getMySpecActionFromHtmlUnit(pageForMyProduct);
        //если ничего не вернулось(страница не существует) то возвращаем нулевой продукт
        if (pageForMyProduct == null){
            return product;
        }
    } else {}

    if (productModel.equals("-")){
        System.out.println("");
        return  product;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //получение каталога аналогов и поиск товара конкурента с наименьшей ценой

        //для продуктов с дополнительными параметрами поиска КЕШ не используем
//        if (arrayParams.size() != 0) {
//            System.out.println("Получение каталога аналогов для запроса \"" + myQuery + "\"");
//            System.out.println("дополнительный параметр поиска - " + arrayParams.toString());
//            productList = getCatalogProducts(marketplaceFlag, myQuery, productType, brand, productModel, arrayParams);
//
//            if (productList.size() != 0) {
//                System.out.println("Размер полученного каталога аналогов = " + Constants.getRedString(String.valueOf(productList.size())));
//                //проходимся по всему списку и находим продукт с наименьшей ценой
//                System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
//                Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, LowerProductFinder.myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
//                if (buffProduct != null) {
//                    product = buffProduct;
//                }
//            } else {
//                System.out.println("Для данного запроса ничего не найдено");
//            }
//
//
//
//        //для продуктов без параметров используем КЕШ
//        } else {
//        resultMapForQueries.clear();
//            if (resultMapForQueries.size() == 0) {//если наш кеш пустой, то заносим туда первый каталог
//                System.out.println("Получение каталога аналогов для запроса \"" + myQuery + "\" (мой артикул = " + myVendorCodeFromRequest + ")");
//                System.out.println("дополнительный параметр поиска  - " + arrayParams.toString());
//                productList = getCatalogProducts(marketplaceFlag, myQuery, productType, brand, productModel, arrayParams);
////                System.out.println("Добавляем в кеш - " + brand + " " + productModel + " " + arrayParams.toString());
////                resultMapForQueries.put(brand + " " + productModel + " " + arrayParams.toString(), productList);
//
//                if (productList.size() != 0) {
//                    System.out.println("Размер полученного каталога аналогов = " + Constants.getRedString(String.valueOf(productList.size())));
//                    //проходимся по всему списку и находим продукт с наименьшей ценой
//                    System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
//                    Product buffProduct = getProductWithLowerPrice(marketplaceFlag, productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
//                    if (buffProduct != null) {
//                        product = buffProduct;
//                    }
//                } else {
//                    System.out.println("Для данного запроса ничего не найдено");
//                }
//                //проверяем наш кеш на наличие каталога для запроса brand, productModel
//            }
//            else if (resultMapForQueries.containsKey(brand + " " + productModel + " " + arrayParams.toString())) {
//                System.out.println("Для запроса \"" + brand + " " + productModel + " + arrayParams = " + arrayParams.toString() + "\" в кеше найден каталог аналогов для запросов \"" + brand + " " + productModel + "\", поэтому запрос на ozon не осуществляем!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                productList = resultMapForQueries.get(brand + " " + productModel + " " + arrayParams.toString());
//                if (productList.size() != 0) {
//                    System.out.println("Размер каталога аналогов = " + productList.size());
//                    //проходимся по всему списку и находим продукт с наименьшей ценой
//                    System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
//                    Product buffProduct = getProductWithLowerPrice(marketplaceFlag, productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
//                    if (buffProduct != null) {
//                        product = buffProduct;
//                        resultSearch = product.getQueryForSearch();
//                        refUrlForResult = product.getRefUrlForResultSearch();
//                    }
//                } else {
//                    System.out.println("Для данного запроса ничего не найдено");
//                }
//            }
//            else {

        //если наш кеш содержит ключ myQuery, то берём productList из кеша
        if (resultMapForQueries.containsKey(myQuery)) {
            productList = resultMapForQueries.get(myQuery);

            if (productList.size() != 0) {

                loggerLowerProductFinder.info(Constants.getYellowString("Размер полученного каталога аналогов = " + Constants.getRedString(String.valueOf(productList.size()))) + "\n" +
                        Constants.getYellowString("Получение аналога с мин. ценой и подходящего по параметрам productModel = " + productModel + " и arrayParams:\n" +
                                arrayParams.toString()));

                Product buffProduct = getProductWithLowerPrice(marketplaceFlag, productList, myVendorCodes, myVendorCodeFromRequest, productType, productBrand, productModel, arrayParams);
                if (buffProduct != null) {
                    product = buffProduct;
                }
            } else {
                loggerLowerProductFinder.info("Для данного запроса ничего не найдено");
            }
        //иначе получаем каталог аналогов с сайта
        } else {
            loggerLowerProductFinder.info("Получение каталога аналогов для запроса \"" + myQuery + "\" (мой артикул = " + myVendorCodeFromRequest + ")\n" +
                    "дополнительный параметр поиска - " + arrayParams.toString());

            String url;
            url = getUrlForSearchQuery(myQuery);

            if (Main.marketplaceFlag == 1){
                productList = ParserHTMLForOzon.getCatalogFromFPageForHtmlUnit(url, productType, productBrand, productModel, arrayParams);
            } else if (Main.marketplaceFlag == 2){
                Page pageForCatalog = ParserHtmlForWildberries.getPageForCatalogProductsFromPlaywright(url);
                if (pageForCatalog != null){
                    productList = ParserHtmlForWildberries.getListProductsForPageCatalogPlaywright(pageForCatalog);
                    if (productList.size() != 0) {

                        loggerLowerProductFinder.info("Добавляем в кеш каталог для - " + myQuery);
                        resultMapForQueries.put(myQuery, productList);

                        loggerLowerProductFinder.info(Constants.getYellowString("Размер полученного каталога аналогов = " + Constants.getRedString(String.valueOf(productList.size()))) + "\n" +
                                Constants.getYellowString("Получение аналога с мин. ценой и подходящего по параметрам productModel = " + productModel + " и arrayParams:\n" +
                                        arrayParams.toString()));

                        Product buffProduct = getProductWithLowerPrice(marketplaceFlag, productList, myVendorCodes, myVendorCodeFromRequest, productType, productBrand, productModel, arrayParams);
                        if (buffProduct != null) {
                            product = buffProduct;
                        }
                    } else {
                        loggerLowerProductFinder.info("Для данного запроса ничего не найдено");
                    }
                } else {
                    loggerLowerProductFinder.info("Для данного запроса ничего не найдено");
                }
            }
        }

        //устанавливаем результат поискоого запроса аналогов
        product.setQueryForSearch(resultSearch);

        //устанавливаем ссылку на страницу поискового запроса аналогов
        product.setRefUrlForResultSearch(refUrlForResult);

        if (marketplaceFlag == 1){
            //устанавливаем ссылку на страницу моего товара
            product.setMyRefForPage(getString("https://www.ozon.ru/search/?text=", myVendorCodeFromRequest, "&from_global=true"));

            //устанавливаем ссылку на картинку моего товара
            product.setMyRefForImage(refForMyImage);

        } else if (marketplaceFlag == 2){
            //устанавливаем ссылку на страницу моего товара
            product.setMyRefForPage(getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP"));

            //устанавливаем ссылку на картинку моего товара
            product.setMyRefForImage(refForMyImage);

            //устанавливаем мою спецакцию, если она есть
            product.setMySpecAction(mySpecAction);

        } else {}

        //устанавливаем мой vendorCode
        product.setMyVendorCodeFromRequest(myVendorCodeFromRequest);

        resultSearch = "-";
        refUrlForResult = "-";

        return product;
    }

    private static String getUrlForSearchQuery(String query) {
        String url = "-";
        if (Main.marketplaceFlag == 1){
            url = getString("https://www.ozon.ru/search/?from_global=true&sorting=price&text=", getQueryUTF8(query), "");
        } else if (Main.marketplaceFlag == 2){
            url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&xsearch=true&sort=priceup");// для вер. 2
            //url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&&sort=priceup");// для вер. 1
        }

        refUrlForResult = url;

        return url;
    }

    private static Product getProductWithLowerPrice(int marketPlaceFlag, List<Product> productList, Set myVendorCodes, String myVendorCodeFromRequest, String productType, String brand, String productModel, List<List<String>> arrayParams) {

        boolean myProductIsLower = true;

        if (productList.size() == 0) {
            return null;
        } else if (productList.size() == 1) {
            return productList.get(0);
        } else {
            Product product = null;
            productList.sort(comparing(Product::getCompetitorLowerPriceU));

            boolean isFindAnalogProduct = false;
            boolean isNotFindMyProduct = true;
            boolean isFindMyParams = false;
            for (Product p : productList) {
                //исключаем мои продукты кроме первого
                if (isNotFindMyProduct && (myVendorCodes.contains(p.getCompetitorVendorCode())
                        || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase())
                        || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase()))) {
                    product = p;
                    isNotFindMyProduct = false;
                } else {
                    String titleFromPageProduct = "-";
                    String descriptionAndParams = "-";
                    String sellerName = "-";
                    if (marketPlaceFlag == 1) {//для Ozon
                        //////////////////////////////////////////////////
                        //////////////////////////////////////////////////
                        //////////////////////////////////////////////////
                        //////////////////////////////////////////////////
                    } else if (marketPlaceFlag == 2) {//для WB
                        //если нет дополнительных параметров поиска, то заходим на каждую страницу, и анализируем заголовок,
                        //описание и характеристики продукта на наличие названия нашей модели
//                        Document pageProduct = SupplierHtmlPage.getWBPageFromJsoup(p.getCompetitorRefForPage());
                        HtmlPage pageProduct = SupplierHtmlPage.getWBPageFromHtmlUnit(p.getCompetitorRefForPage());
                        titleFromPageProduct = ParserHtmlForWildberries.getTitleFromPageProduct(pageProduct);
                        descriptionAndParams = ParserHtmlForWildberries.getDescriptionAndParam(pageProduct);
                        loggerLowerProductFinder.info("На странице товара - " + p.getCompetitorRefForPage() + " получили\n заголовок:\n" +
                                Constants.getYellowString(titleFromPageProduct) + "\n" +
                                "и описание:\n" + Constants.getYellowString(descriptionAndParams));
                        if (pageProduct instanceof Page){
                            ((Page)pageProduct).close();
                        }
                        if (arrayParams.size() != 0) {
                            if (arrayParams.get(0).contains("без кабеля")) {
                                if (Constants.checkTitleDescriptionAndParamsForConnectorType(titleFromPageProduct + " " + descriptionAndParams)) {
                                    continue;
                                }
                            } else {
                                int countFind = 0;
                                countFind = getCountFindConcurrence(arrayParams, countFind, titleFromPageProduct + " " + descriptionAndParams);
                                if (!(arrayParams.size() == countFind)) {
                                    continue;
                                }
                            }
                        }
                    }
                    //проверяем наличие названия модели в заголовоке ,описание и характеристиках продукта
                    isFindAnalogProduct = isFindAnalogProductFromModel(productModel, isFindAnalogProduct, titleFromPageProduct + " " + descriptionAndParams);

                    if (isFindAnalogProduct){
                        product = p;
                        loggerLowerProductFinder.info(Constants.getGreenString("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Аналог с минимальной ценой найден !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + "\n" +
                                "С помощью browserPlaywright получаем имя продовца"));
                        Page page = SupplierHtmlPage.getWBPageFromPlaywright(product.getCompetitorRefForPage());
                        sellerName = ParserHtmlForWildberries.getSellerNameFromPlaywright(page);
                        loggerLowerProductFinder.info(Constants.getGreenString("Продавец - " + sellerName));
                        product.setCompetitorName(sellerName);
                        page.close();
                        break;
                    }
                }
            }
            //если мы не нашли аналог, то возвращаем первый продукт из списка аналогов
            if (!isFindAnalogProduct) {
                for (Product p : productList) {
                    //исключаем мои продукты
                    if (myVendorCodes.contains(p.getCompetitorVendorCode())
                            || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase())
                            || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
                        if (myProductIsLower) {
                            product = p;
                            myProductIsLower = false;
                        }
                    } else {
                        product = p;
                        break;
                    }
                }
            }
            return product;
        }
    }

    private static int getCountFindConcurrence(List<List<String>> arrayParams, int countFind, String textForAnalise) {
        textForAnalise = Constants.replaceNoValidConcurrence(textForAnalise);
        boolean isFindMyParams;
        for (List<String> paramsList: arrayParams){
            for (String param: paramsList){
                if (textForAnalise.toLowerCase().contains(param.toLowerCase())){
                    isFindMyParams = true;
                    countFind++;
                    break;
                }
            }
        }
        return countFind;
    }

    private static boolean isFindAnalogProductFromModel(String productModel, boolean isFindAnalogProduct, String textForAnalise) {
        if (textForAnalise.toLowerCase().contains(productModel + ",")
                || textForAnalise.toLowerCase().contains(productModel + " ")
                || textForAnalise.toLowerCase().contains("(" + productModel + ")")
                || textForAnalise.toLowerCase().contains(", " + productModel + "")
                || textForAnalise.toLowerCase().contains(" " + productModel + "")) {
            isFindAnalogProduct = true;
        }
        return isFindAnalogProduct;
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
