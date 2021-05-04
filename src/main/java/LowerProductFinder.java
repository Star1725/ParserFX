import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.locks.Lock;

import static java.util.Comparator.comparing;

public class LowerProductFinder {

    static String refUrlForResult = "-";
    static String resultSearch = "-";
    static String myQuery;

    static WebClient webClientForOzon;
    static Lock lockOzon;

    static String myVendorCodeFromRequest;

    static int countUseIP;
    public static int countSwitchIP = 1;

    private static final Map<String, List<Product>> resultMapForQueries = new LinkedHashMap<>();

    public Product getProduct(int marketplaceFlag, String myVendorCodeFromRequest, String category, String brand, String productType, String productModel, List<String> arrayParams, Set myVendorCodes, String specQuerySearch, WebClient webClient, Lock lock) {
        List<Product> productList;

        if (specQuerySearch.equals("-") || specQuerySearch.equals("")) {
            myQuery = brand.toLowerCase() + " " + productModel.toLowerCase();
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//получение страницы с моим товаром по коду myVendorCodeFromRequest
        String refForMyImage = "-";
        Document myPage = null;
    if (marketplaceFlag == 1){
        //Flag = 1 - Для Ozon. Получение html-страницы для моего артикула
        HtmlPage pageForMyProduct = null;
        System.out.println(Constants.getYellowString("Получение ссылки на картинку моего товара"));
        try {
            pageForMyProduct = SupplierHtmlPage.getHtmlPage("https://www.ozon.ru/search/?from_global=true&text=" + myVendorCodeFromRequest);
            //ссылка на картинку товара
            final HtmlDivision div_class_a0i7 = (HtmlDivision) pageForMyProduct.getByXPath("//div[@class='a0i7']").get(0);
            refForMyImage = div_class_a0i7.getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
            System.out.println(Constants.getGreenString("Ссылка на картинку моего товара получена!"));
        } catch (Exception e) {
            System.out.println(Constants.getRedString("Ошибка при получении ссылки на картинку моего товара получена!"));
            e.printStackTrace();
            System.out.println(pageForMyProduct.asXml());
        }
    } else if (marketplaceFlag == 2){
        //Flag = 2 - Для WB. Получение html-страницы для моего артикула
        myPage = ParserHtmlForWildberries.getDocumentPageForVendorCode(myVendorCodeFromRequest);
        //если ничего не вернулось(страница не существует) то возвращаем нулевой продукт
        if (myPage == null){
            return product;
        }
    } else {}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //получение каталога аналогов и поиск товара конкурента с наименьшей ценой

        //для продуктов с дополнительными параметрами поиска КЕШ не используем
        if (arrayParams.size() != 0) {
            System.out.println("Получение каталога аналогов для запроса \"" + myQuery + "\"");
            System.out.println("дополнительный параметр поиска - " + arrayParams.toString());
            productList = getCatalogProducts(marketplaceFlag, myQuery, productType, brand, productModel, arrayParams);

            if (productList.size() != 0) {
                System.out.println("Размер полученного каталога аналогов = " + Constants.getRedString(String.valueOf(productList.size())));
                //проходимся по всему списку и находим продукт с наименьшей ценой
                System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
                Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, LowerProductFinder.myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
                if (buffProduct != null) {
                    product = buffProduct;
                }
            } else {
                System.out.println("Для данного запроса ничего не найдено");
            }
        //для продуктов без параметров используем КЕШ
        } else {
            if (resultMapForQueries.size() == 0) {//если наш кеш пустой, то заносим туда первый каталог
                System.out.println("Получение каталога аналогов для запроса \"" + myQuery + "\"");
                System.out.println("дополнительный параметр поиска должен отсутствовать - " + arrayParams.toString());
                productList = getCatalogProducts(marketplaceFlag, myQuery, productType, brand, productModel, arrayParams);

                resultMapForQueries.put(brand + " " + productModel, productList);

                if (productList.size() != 0) {
                    System.out.println("Размер полученного каталога аналогов = " + Constants.getRedString(String.valueOf(productList.size())));
                    //проходимся по всему списку и находим продукт с наименьшей ценой
                    System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
                    Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
                    if (buffProduct != null) {
                        product = buffProduct;
                    }
                } else {
                    System.out.println("Для данного запроса ничего не найдено");
                }
                //проверяем наш кеш на наличие каталога для запроса brand, productModel
            } else if (resultMapForQueries.containsKey(brand + " " + productModel)) {
                System.out.println("Для запроса \"" + brand + " " + productModel + " + arrayParams = " + arrayParams.toString() + "\" в кеше найден каталог аналогов для запросов \"" + brand + " " + productModel + "\", поэтому запрос на ozon не осуществляем!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                productList = resultMapForQueries.get(brand + " " + productModel);
                if (productList.size() != 0) {
                    System.out.println("Размер каталога аналогов = " + productList.size());
                    //проходимся по всему списку и находим продукт с наименьшей ценой
                    System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
                    Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
                    if (buffProduct != null) {
                        product = buffProduct;
                    }
                } else {
                    System.out.println("Для данного запроса ничего не найдено");
                }
            } else {
                System.out.println("Получение каталога аналогов для \"" + myQuery + "\"");
                System.out.println("дополнительный параметр поиска должен отсутствовать - " + arrayParams.toString());
                productList = getCatalogProducts(marketplaceFlag, myQuery, productType, brand, productModel, arrayParams);

                resultMapForQueries.put(brand + " " + productModel, productList);

                if (productList.size() != 0) {
                    System.out.println("Размер полученного каталога аналогов = " + Constants.getRedString(String.valueOf(productList.size())));
                    //проходимся по всему списку и находим продукт с наименьшей ценой
                    System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
                    Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
                    if (buffProduct != null) {
                        product = buffProduct;
                    }
                } else {
                    System.out.println("Для данного запроса ничего не найдено");
                }
            }
        }

        //устанавливаем результат поискоого запроса аналогов
        product.setQueryForSearch(resultSearch);

        //устанавливаем ссылку на страницу поискового запроса аналогов
        product.setRefUrlForResultSearch(refUrlForResult);

        if (marketplaceFlag == 1){
            //устанавливаем ссылку на артикул моего товара
            product.setMyRefForPage(getString("https://www.ozon.ru/search/?text=", myVendorCodeFromRequest, "&from_global=true"));

            //устанавливаем ссылку на картинку моего товара
            product.setMyRefForImage(refForMyImage);

        } else if (marketplaceFlag == 2){
            //устанавливаем ссылку на артикул моего товара
            product.setMyRefForPage(getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP"));

            //устанавливаем ссылку на картинку моего товара
            product.setMyRefForImage(ParserHtmlForWildberries.getMyProductsPhoto(myPage));

            //устанавливаем мою спецакцию, если она есть
            product.setMySpecAction(ParserHtmlForWildberries.getMySpecAction(myPage));

        } else {}

        //устанавливаем мой vendorCode
        product.setMyVendorCodeFromRequest(myVendorCodeFromRequest);

        resultSearch = "-";
        refUrlForResult = "-";

        return product;
    }

    private static List<Product> getCatalogProducts(int marketplaceFlag, String query, String productType, String brand, String model, List<String> arrayParams) {
        List<Product> productList = null;
        String url = "-";
        url = getUrlForSearchQuery(query, marketplaceFlag);

        if (marketplaceFlag == 1){
            //получение бренда, артикула, имени товара, ссылки на страницу товара, ссылки на картинкау товара, спец-акции, рейтинга
            productList = ParserHTMLForOzon.getCatalogFromFPageForHtmlUnit(url, productType, brand, model, arrayParams);

        } else if (marketplaceFlag == 2){
            Document page;
//            page = ParserHtmlForWildberries.getPageForUrl(url);
            page = SupplierHtmlPage.getPageForUrl(url);
            //получение бренда, артикула, имени товара, ссылки на страницу товара, ссылки на картинкау товара, спец-акции, рейтинга
            productList = ParserHtmlForWildberries.getCatalogProductsForRequestPage(page, brand, model, arrayParams);
            if (productList.size() == 0){
                return productList;
            }
            //получение цены и скидок через json
            HttpUrlConnectionHandler.getCatalog(productList, query);
        }

        return productList;
    }

    private static String getUrlForSearchQuery(String query, int marketplaceFlag) {
        String url = "-";
        if (marketplaceFlag == 1){
            url = getString("https://www.ozon.ru/search/?from_global=true&sorting=price&text=", getQueryUTF8(query), "");
        } else if (marketplaceFlag == 2){
            url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&xsearch=true&sort=priceup");// для вер. 2
            //url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&&sort=priceup");// для вер. 1
        }

        refUrlForResult = url;

        return url;
    }

    private static Product getProductWithLowerPrice(List<Product> productList, Set myVendorCodes, String myVendorCodeFromRequest, String productType, String brand, String productModel, List<String> arrayParams) {

        boolean myProductIsLower = true;

        if (productList.size() == 0) {
            return null;
        } else if (productList.size() == 1){
            return productList.get(0);
        } else {
            Product product = null;
            productList.sort(comparing(Product::getCompetitorLowerPriceU));

            switch (productType){
                case Constants.PRODUCT_TYPE_1C_10:
                case Constants.PRODUCT_TYPE_1C_39:
                case Constants.PRODUCT_TYPE_1C_40:
                case Constants.PRODUCT_TYPE_1C_132:
                    try {
                        if (productModel != null){
                            //если в запросе только бренд и модель, то нам нужен первый product, в описании которого только модель
                            if (arrayParams.size() == 0){
                                for (Product p : productList) {
                                    String nomenclature = p.getCompetitorProductName().toLowerCase();
                                    if (nomenclature.toLowerCase().contains(productModel + ",")
                                            || nomenclature.toLowerCase().contains(productModel + " ")
                                            || nomenclature.toLowerCase().contains("(" + productModel + ")")
                                            || nomenclature.toLowerCase().contains(", " + productModel + "")) {
                                        int check = 0;
                                        for (String s : Constants.listForCharging) {
                                            if (nomenclature.contains(s.toLowerCase())) {
                                                check++;
                                                break;
                                            }
                                        }
                                        if (check == 0) {
                                            if (myVendorCodes.contains(p.getCompetitorVendorCode()) || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase()) || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
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
                                }
                            }
                            //если в запросе бренд, модель и кабель то нам нужен первый product, в описании которого наш param
                            else {
                                //определяем коллекцию с разными названиями нашего param
                                List<String> listWithCable = Constants.getCollectionsParam(arrayParams, brand + productModel);
                                for (Product p : productList) {
                                    String nomenclature = p.getCompetitorProductName().toLowerCase();
                                    if (nomenclature.toLowerCase().contains(productModel + ",")
                                            || nomenclature.toLowerCase().contains(productModel + " ")
                                            || nomenclature.toLowerCase().contains("(" + productModel + ")")
                                            || nomenclature.toLowerCase().contains(", " + productModel + "")) {
                                        int check = 0;
                                        for (String s : listWithCable) {
                                            if (nomenclature.contains(s)) {
                                                if (myVendorCodes.contains(p.getCompetitorVendorCode()) || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase()) || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
                                                    if (myProductIsLower) {
                                                        product = p;
                                                        myProductIsLower = false;
                                                    }
                                                } else {
                                                    product = p;
                                                    check++;
                                                    break;
                                                }
                                            }
                                        }
                                        if (check != 0) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка при обработке списка аналогов на поиск по параметру");
                    }
                    break;

                case Constants.PRODUCT_TYPE_1C_48:
                case Constants.PRODUCT_TYPE_1C_49:
                case Constants.PRODUCT_TYPE_1C_50:
                case Constants.PRODUCT_TYPE_1C_61:
                    //case Constants.PRODUCT_TYPE_1C_62:
                case Constants.PRODUCT_TYPE_1C_63:
                case Constants.PRODUCT_TYPE_1C_64:
                case Constants.PRODUCT_TYPE_1C_65:
                case Constants.PRODUCT_TYPE_1C_66:
                case Constants.PRODUCT_TYPE_1C_166:
                case Constants.PRODUCT_TYPE_1C_67:
                case Constants.PRODUCT_TYPE_1C_68:
                case Constants.PRODUCT_TYPE_1C_69:
                case Constants.PRODUCT_TYPE_1C_70:
                    try {
                        if (productModel != null){
                            //определяем коллекцию с разными названиями нашего param
                            List<String> listWithCableParam_1;
                            List<String> listWithCableParam_2 = null;
                            if (arrayParams.size() == 1){
                                listWithCableParam_1 = Constants.getCollectionsParamCable(arrayParams.get(0), brand + productModel);
                            } else {
                                listWithCableParam_1 = Constants.getCollectionsParamCable(arrayParams.get(0), brand + productModel);
                                listWithCableParam_2 = Constants.getCollectionsParamCable(arrayParams.get(1), brand + productModel);
                            }

                            for (Product p : productList) {
                                String nomenclature = p.getCompetitorProductName().toLowerCase();
                                if (nomenclature.toLowerCase().contains(productModel + ",")
                                        || nomenclature.toLowerCase().contains(productModel + " ")
                                        || nomenclature.toLowerCase().contains("(" + productModel + ")")
                                        || nomenclature.toLowerCase().contains(", " + productModel + "")) {
                                    int check = 0;
                                    for (String s1 : listWithCableParam_1) {
                                        if (nomenclature.contains(s1)) {
                                            if (listWithCableParam_2 != null){
                                                for (String s2 : listWithCableParam_2) {
                                                    if (nomenclature.contains(s2)) {
                                                        if (myVendorCodes.contains(p.getCompetitorVendorCode())
                                                                || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase())
                                                                || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
                                                            if (myProductIsLower) {
                                                                product = p;
                                                                myProductIsLower = false;
                                                            }
                                                        } else {
                                                            product = p;
                                                            check++;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (check != 0) {
                                                    break;
                                                }
                                            } else {
                                                if (myVendorCodes.contains(p.getCompetitorVendorCode())
                                                        || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase())
                                                        || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
                                                    if (myProductIsLower) {
                                                        product = p;
                                                        myProductIsLower = false;
                                                    }
                                                } else {
                                                    product = p;
                                                    check++;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (check != 0) {
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка при обработке списка аналогов на поиск по параметру");
                    }
                    break;

                case Constants.PRODUCT_TYPE_1C_139:
                    try {
                        if (productModel != null){
                            if (arrayParams.size() == 1){
                                for (Product p : productList) {
                                    String nomenclature = p.getCompetitorProductName().replaceAll(",", "").toLowerCase();
                                    if (nomenclature.contains(arrayParams.get(0))) {
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
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Ошибка при обработке списка аналогов на поиск по параметру");
                    }
                    break;
                default:
                    //исключаем мои продукты
                    for (Product p : productList) {
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
