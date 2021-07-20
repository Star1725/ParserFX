import com.microsoft.playwright.Page;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;

import static java.util.Comparator.comparing;

public class LowerProductFinder {

    static String refUrlForResult = "-";
    static String resultSearch = "Для данного запроса ничего не найдено";
    static String myQuery;
    static String urlForMyQuery;

    static String myVendorCodeFromRequest;

    static int countUseIP_ForOzon;
    static int countUseIP_ForWB;
    public static int countSwitchIP = 1;

    private static final Map<String, List<Product>> resultMapForQueries = new LinkedHashMap<>();

    private static final Logger loggerLowerProductFinder = Logger.getLogger(LowerProductFinder.class.getName());

    static {
        loggerLowerProductFinder.addHandler(Main.fileHandler);
    }

    public Product getProduct(int marketplaceFlag, String myVendorCodeFromRequest, String productBrand, String productType, String productModel, List<List<String>> arrayParams, Set myVendorCodes, String specQuerySearch) {
        List<Product> productList = new ArrayList<>();

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

        LowerProductFinder.myVendorCodeFromRequest = myVendorCodeFromRequest;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//получение страницы с моим товаром по коду myVendorCodeFromRequest
        String refForMyImage = "-";
        String mySpecAction = "-";
        Document myPage = null;
        Page pageForMyProduct = null;

        loggerLowerProductFinder.info(Constants.getWhiteString("Получение страницы моего товара, чтобы получить информацию о моём товаре (картинка, спецакция ...)"));

        String urlForPageMyProduct = null;

        if (marketplaceFlag == 1) {//Flag = 1 - Для Ozon. Получение html-страницы для моего артикула через HtmlUnit (Jsoup ozon сразу банит)
            try {
                urlForPageMyProduct = "https://www.ozon.ru/search/?from_global=true&text=" + myVendorCodeFromRequest;
                pageForMyProduct = SupplierHtmlPage.getOzonPageFromPlaywrightWithoutJavaScript(urlForPageMyProduct);
//                pageForMyProduct = (HtmlPage) SupplierHtmlPage.getOzonPageFromHtmlUnit("https://www.ozon.ru/search/?from_global=true&text=" + myVendorCodeFromRequest);
                refForMyImage = ParserHTMLForOzon.getRefMyProductsImageFromPlaywright(pageForMyProduct, urlForPageMyProduct);
            } catch (Exception e) {
                loggerLowerProductFinder.info("Ошибка при получении страницы моего товара!");
                e.printStackTrace();
                assert pageForMyProduct != null;
                loggerLowerProductFinder.info(pageForMyProduct.innerHTML("body"));
            }
        } else if (marketplaceFlag == 2) {//Flag = 2 - Для WB. Получение html-страницы для моего артикула
            try {
                urlForPageMyProduct = "https://www.wildberries.ru/catalog/" + myVendorCodeFromRequest + "/detail.aspx?targetUrl=SP";
                pageForMyProduct = SupplierHtmlPage.getWBPageFromPlaywrightWithoutJavaScript(urlForPageMyProduct);
//            pageForMyProduct = SupplierHtmlPage.getWBPageFromHtmlUnit("https://www.wildberries.ru/catalog/" + myVendorCodeFromRequest + "/detail.aspx?targetUrl=SP");
                refForMyImage = ParserHtmlForWildberries.getMyProductImageFromPlaywright(pageForMyProduct, urlForPageMyProduct);
                mySpecAction = ParserHtmlForWildberries.getMySpecActionFromPlaywright(pageForMyProduct, urlForPageMyProduct);
            } catch (Exception e) {
                loggerLowerProductFinder.info("Ошибка при получении страницы моего товара!");
                e.printStackTrace();
                assert pageForMyProduct != null;
                loggerLowerProductFinder.info(pageForMyProduct.innerHTML("body"));
            }
        }
        assert pageForMyProduct != null;
        pageForMyProduct.close();

        if (productModel.equals("-")) {
            System.out.println("");
            return product;
        }

        if (productBrand.toLowerCase().equals("xivi")){
            loggerLowerProductFinder.info("Xivi - пропускаем запрос");
            return product;
        } else if (productBrand.toLowerCase().equals("mietubl")){
            loggerLowerProductFinder.info("Mietubl - пропускаем запрос");
            return product;
        }

        //если наш кеш содержит ключ myQuery, то берём productList из кеша
        if (resultMapForQueries.containsKey(myQuery)) {
            loggerLowerProductFinder.info(Constants.getYellowString("Для запроса \"" + myQuery + "\" нашли каталог аналогов в КЕШЕ!!!!!! На \"" + Main.marketplace + "\" запрос не осуществляем"));
            productList = resultMapForQueries.get(myQuery);

            if (productList.size() != 0) {

                loggerLowerProductFinder.info(Constants.getYellowString("Размер найденного каталога аналогов = " + Constants.getBlueString(String.valueOf(productList.size()))) + "\n" +
                        Constants.getYellowString("Получение аналога с мин. ценой и подходящего по параметрам productModel = " + productModel + " и arrayParams:\n" +
                                arrayParams.toString()));

                Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, productBrand, productModel, arrayParams);
                if (buffProduct != null) {
                    product = buffProduct;
                }
            } else {
                loggerLowerProductFinder.info("Для данного запроса ничего не найдено");
            }
            //иначе получаем каталог аналогов с сайта
        } else {
            loggerLowerProductFinder.info(Constants.getWhiteString("Получение на сайте " + Main.marketplace + " каталога аналогов для запроса " +
                    "\"" + myQuery + "\" (мой артикул = " + myVendorCodeFromRequest + ")\n" + "дополнительный параметр поиска - " + arrayParams.toString()));

            urlForMyQuery = getUrlForSearchQuery(myQuery);

            if (Main.marketplaceFlag == 1) {
                Page pageForCatalog = ParserHTMLForOzon.getPageForCatalogProductsFromPlaywright(urlForMyQuery);
                if (pageForCatalog != null) {
                    productList = ParserHTMLForOzon.getListProductsForPageCatalogPlaywright(pageForCatalog);
                } else {
                    loggerLowerProductFinder.info("Для данного запроса ничего не найдено");
                }
            } else if (Main.marketplaceFlag == 2) {
                Page pageForCatalog = ParserHtmlForWildberries.getPageForCatalogProductsFromPlaywright(urlForMyQuery);
                if (pageForCatalog != null) {
                    productList = ParserHtmlForWildberries.getListProductsForPageCatalogPlaywright(pageForCatalog);
                } else {
                    loggerLowerProductFinder.info("Для данного запроса ничего не найдено");
                }
            }
            if (productList.size() != 0) {

                loggerLowerProductFinder.info("Добавляем в кеш каталог для - " + myQuery);
                resultMapForQueries.put(myQuery, productList);

                loggerLowerProductFinder.info(Constants.getWhiteString("Размер полученного каталога аналогов = " + Constants.getBlueString(String.valueOf(productList.size()))) + "\n" +
                        Constants.getYellowString("Получение аналога с мин. ценой и подходящего по параметрам productModel = " + productModel + " и arrayParams:\n" +
                                arrayParams.toString()));

                Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, productBrand, productModel, arrayParams);
                if (buffProduct != null) {
                    product = buffProduct;
                }
            } else {
                loggerLowerProductFinder.info("Для данного запроса ничего не найдено");
            }
        }

        //устанавливаем ссылку на страницу моего товара
        product.setMyRefForPage(urlForPageMyProduct);

        //устанавливаем ссылку на картинку моего товара
        product.setMyRefForImage(refForMyImage);

        //устанавливаем мою спецакцию, если она есть
        product.setMySpecAction(mySpecAction);


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

    private static Product getProductWithLowerPrice(List<Product> productList, Set myVendorCodes, String myVendorCodeFromRequest, String productType, String brand, String productModel, List<List<String>> arrayParams) {

        boolean myProductIsLower = true;

        int positionProduct = 1;

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
                loggerLowerProductFinder.info(Constants.getYellowString("<<<<<<<<<<<<< Анализ продукта № " + positionProduct + " >>>>>>>>>>>>>"));
                //исключаем мои продукты кроме первого
                boolean isMyProduct1 = myVendorCodes.contains(p.getCompetitorVendorCode());
                boolean isMyProduct2 = p.getCompetitorName().toLowerCase().contains(Constants.MY_SELLER.toLowerCase());
                boolean isMyProduct3 = p.getCompetitorName().toLowerCase().contains(Constants.MY_SELLER_2.toLowerCase());
                if (isMyProduct1 || isMyProduct2 || isMyProduct3) {
                    if (isNotFindMyProduct){
                        product = p;
                        isNotFindMyProduct = false;
                    }
                    loggerLowerProductFinder.info(Constants.getYellowString("продукт № " + positionProduct + " - мой. Пропускаем"));
                    positionProduct++;
                } else {
                    String titleFromPageProduct = "-";
                    String descriptionAndParams = "-";
                    String sellerName = "-";
                    Object pageProduct = null;

                    List<String> typeOfCharacteristic = new ArrayList<>();
                    typeOfCharacteristic.add("Коннектор 1");
                    typeOfCharacteristic.add("Коннектор 2");
                    typeOfCharacteristic.add("Длина, м");
                    //для Ozon
                    if (Main.marketplaceFlag == 1) {
//                        pageProduct = SupplierHtmlPage.getOzonPageFromHtmlUnit(p.getCompetitorRefForPage());
                        descriptionAndParams = HttpUrlConnectionWorkerJSON.getDescriptionsForOzon(p, typeOfCharacteristic);
//                        descriptionAndParams = ParserHTMLForOzon.getParams(pageProduct);
//                        titleFromPageProduct = ParserHTMLForOzon.checkAndGetLengthInDescriptionAndParam(pageProduct, p.getCompetitorProductName());
                        titleFromPageProduct = p.getCompetitorProductName();
                    }
                    //для WB
                    else if (Main.marketplaceFlag == 2) {
//                        Document pageProduct = SupplierHtmlPage.getWBPageFromJsoup(p.getCompetitorRefForPage());
                        pageProduct = SupplierHtmlPage.getWBPageFromHtmlUnit(p.getCompetitorRefForPage());
                        titleFromPageProduct = ParserHtmlForWildberries.getTitleFromPageProduct(pageProduct);
                        descriptionAndParams = ParserHtmlForWildberries.getDescriptionAndParam(pageProduct);
                    }
                    positionProduct++;
                    loggerLowerProductFinder.info("На странице товара - " + p.getCompetitorRefForPage() + " получили\n  заголовок:\n" +
                            Constants.getYellowString(titleFromPageProduct) + "\n" +
                            "   и описание:\n" + Constants.getYellowString(descriptionAndParams));
                    if (pageProduct instanceof Page) {
                        ((Page) pageProduct).close();
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

                    //проверяем наличие названия модели в заголовоке ,описание и характеристиках продукта
                    isFindAnalogProduct = isFindAnalogProductFromModel(productModel, isFindAnalogProduct, titleFromPageProduct + " " + descriptionAndParams);

                    if (isFindAnalogProduct){
                        product = p;
                        loggerLowerProductFinder.info(Constants.getWhiteString("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Аналог с минимальной ценой найден !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"));
                        if (Main.marketplaceFlag == 2){
                            loggerLowerProductFinder.info(Constants.getWhiteString("С помощью browserPlaywright получаем имя продовца"));
                            Page page = null;
                            try {
                                page = SupplierHtmlPage.getWBPageFromPlaywrightForJavaScript(product.getCompetitorRefForPage());
                                sellerName = ParserHtmlForWildberries.getSellerNameFromPlaywright(page);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            loggerLowerProductFinder.info(Constants.getWhiteString("Продавец - " + sellerName));
                            product.setCompetitorName(sellerName);
                            page.close();
                        }
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
