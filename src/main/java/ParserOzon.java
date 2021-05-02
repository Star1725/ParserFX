import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;

public class ParserOzon {

    static String refUrlForResult = "-";
    static String myQuery;

    private static final Object mon = new Object();
    static WebClient webClientForOzon;
    static Lock lockOzon;

    static String myVendorCodeFromRequest;

    static int countUseIP;
    public static int countSwitchIP = 1;

    private static Map<String, List<Product>> resultMapForQueries = new LinkedHashMap<>();

    public Product getProduct(String vendorCodeFromRequest, String category, String brand, String productType, String productModel, List<String> arrayParams, Set myVendorCodes, String specQuerySearch, WebClient webClient, Lock lock) {
        List<Product> productList = new ArrayList<>();
        String params = "";
        if (arrayParams != null) {
            if (arrayParams.size() != 0) {
                for (String s : arrayParams) {
                    params = params + s + " ";
                }
            } else {
                params = "-";
            }
        }
        Product product = new Product(vendorCodeFromRequest,
                "-",
                "-",
                "-",
                "-",

                "-",
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

        if (specQuerySearch.equals("-") || specQuerySearch.equals("")) {
            myQuery = brand.toLowerCase() + " " + productModel.toLowerCase();
        } else {
            myQuery = specQuerySearch;
        }

        webClientForOzon = webClient;
        lockOzon = lock;
        myVendorCodeFromRequest = vendorCodeFromRequest;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//получение страницы с моим товаром по коду Ozon
        String refForMyImage = "-";
        HtmlPage pageForMyProduct = null;
        System.out.println(Constants.getYellowString("Получение ссылки на картинку моего товара"));
        try {
            pageForMyProduct = getHtmlPage("https://www.ozon.ru/search/?from_global=true&text=" + vendorCodeFromRequest);
            //ссылка на картинку товара
            final HtmlDivision div_class_a0i7 = (HtmlDivision) pageForMyProduct.getByXPath("//div[@class='a0i7']").get(0);
            refForMyImage = div_class_a0i7.getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
            System.out.println(Constants.getGreenString("Ссылка на картинку моего товара получена!"));
        } catch (Exception e) {
            System.out.println(Constants.getRedString("Ошибка при получении ссылки на картинку моего товара получена!"));
            e.printStackTrace();
            System.out.println(pageForMyProduct.asXml());
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (arrayParams.size() != 0) {
            System.out.println("Получение каталога аналогов для запроса \"" + myQuery + "\"");
            System.out.println("дополнительный параметр поиска - " + arrayParams.toString());
            productList = getCatalogProducts(myQuery, productType, brand, productModel, arrayParams);

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
        } else {
            //для продуктов без параметров используем КЕШ
            if (resultMapForQueries.size() == 0) {//если наш кеш пустой, то заносим туда первый каталог
                System.out.println("Получение каталога аналогов для запроса \"" + myQuery + "\"");
                System.out.println("дополнительный параметр поиска - " + arrayParams.toString());
                productList = getCatalogProducts(myQuery, productType, brand, productModel, arrayParams);

                resultMapForQueries.put(brand + " " + productModel, productList);

                if (productList.size() != 0) {
                    System.out.println("Размер полученного каталога аналогов = " + Constants.getRedString(String.valueOf(productList.size())));
                    //проходимся по всему списку и находим продукт с наименьшей ценой
                    System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
                    Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
                    //                                            (productList, myVendorCodes, vendorCodeFromRequest, brand, productModel, arrayParams)
                    if (buffProduct != null) {
                        product = buffProduct;
                    }
                } else {
                    System.out.println("Для данного запроса ничего не найдено");
                }
                //проверяем наш кеш на наличие каталога для запроса brand, productModel
            } else if (resultMapForQueries.containsKey(brand + " " + productModel)) {
                System.out.println("Для запроса \"" + brand + " " + productModel + " + arrayParams = " + arrayParams.toString() + "\" в кеше найден каталог аналогов для запросов \"" + brand + " " + productModel + "\", поэтому запрос на ozon не осуществляем");
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
                System.out.println("дополнительный параметр поиска - " + arrayParams.toString());
                productList = getCatalogProducts(myQuery, productType, brand, productModel, arrayParams);

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

        //устанавливаем ссылку на картинку моего товара
        product.setMyRefForImage(refForMyImage);

        //устанавливаем ссылку на страницу поискового запроса аналогов


        //устанавливаем ссылку на артикул моего товара
        product.setMyRefForPage(getString("https://www.ozon.ru/search/?text=", vendorCodeFromRequest, "&from_global=true"));
//                                            https://www.ozon.ru/search/?text=210646439&from_global=true
        //устанавливаем мой vendorCode
        product.setMyVendorCodeFromRequest(vendorCodeFromRequest);

        return product;
    }

    private static List<Product> getCatalogProducts(String query, String productType, String brand, String model, List<String> arrayParams) {
        List<Product> productList;
        String url = "-";
        url = getUrlForSearchQuery(query);

        refUrlForResult = url;

        //получение бренда, артикула, имени товара, ссылки на страницу товара, ссылки на картинкау товара, спец-акции, рейтинга
        productList = ParserHTMLForOzon.getCatalogFromFPageForHtmlUnit(url, productType, brand, model, arrayParams);

        return productList;
    }

    private static String getUrlForSearchQuery(String query) {
        String url = "-";
        url = getString("https://www.ozon.ru/search/?from_global=true&sorting=price&text=", getQueryUTF8(query), "");
        return url;
    }

    private static List<Product> getCatalogFromFPageForHtmlUnit(String url, String productType, String brand, String model, List<String> arrayParams) {
        List<Product> productList = new ArrayList<>();
        HtmlPage page = null;
        String stringPage = null;
        String querySearchAndCount = "-";
        String category = "-";

        System.out.println("IP №" + countSwitchIP + ".Получение страницы ozon для запроса - " + myQuery + ". Артикул Ozon - " + myVendorCodeFromRequest);
        boolean isNotGetValidPage = true;
        while (isNotGetValidPage){
            //получение страницы поискового запроса с аналогами
            page = getHtmlPage(url);

            if (page == null) {
                System.out.println("Запрашиваемая страница = null");
                return null;
            }

            String sPage = page.asXml();

            //получаем кол-во найденных аналогов с уже известными ценами
            List<HtmlElement> itemsCountSearch = page.getByXPath("//div[@class='b6e2']");
            if (itemsCountSearch == null) {
                System.out.println("не нашёл html-элемент - div[@class='b6e2']");
            } else {
                try {
                    querySearchAndCount = itemsCountSearch.get(0).asText();
                    System.out.println(Constants.getRedString(querySearchAndCount));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                    System.out.println(page.asXml());
                    continue;
                }
            }

            if (querySearchAndCount.contains("товаров сейчас нет")){
                productList.add(new Product("Запрос - " + myQuery + ". " + querySearchAndCount, "-1"));
                return productList;
            }

            //получаем список продуктов, полученный по поисковому запросу аналогов
            List<HtmlElement> itemsForListProducts1 = page.getByXPath("//div[@class='a0c6 a0c9']");
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

                    String discountedItem = "Уцененный товар";

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
                        String[] arrayBuff3 = vendorCodeBuff.split("/");
                        vendorCode = arrayBuff3[0];
                    }

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
                    boolean isDiscountedItem = false;

                    for (DomElement elementFor_a0t0 : elementsFor_a0c4) {
                        //1 элемент - получение ссылки на картинку
                        if (childFor_a0c4 == 1) {
                            //refImage = elementFor_a0t0.getFirstChild().getFirstChild().getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
                            refImage = elementFor_a0t0.getElementsByTagName("img").get(0).getAttributes().getNamedItem("src").getNodeValue();
                        }

                        //2 элемент
                        if (childFor_a0c4 == 2) {
                            DomNodeList<HtmlElement> asFor_a0s9 = elementFor_a0t0.getElementsByTagName("a");
                            //определяем не уценённый ли товар
                            DomNodeList<HtmlElement> divsFor_a0s9 = elementFor_a0t0.getElementsByTagName("div");
                            for (HtmlElement element: divsFor_a0s9){
                                if (element.asText().contains(discountedItem)) {
                                    System.out.println(discountedItem);
                                    isDiscountedItem = true;
                                    break;
                                }
                            }
                            if (isDiscountedItem){
                                break;
                            }
                            /////////////////////////////////////////////
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
                                competitorBasicPriceU = getIntegerFromString(currentBasicPriceString) * 100;

                                //получение цены currentPriceUString
                                try {
                                    String currentPriceUString = elementsFor_b5v4.get(1).asText();
                                    competitorPriceU = getIntegerFromString(currentPriceUString) * 100;
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
                                        competitorPremiumPriceForOzon = getIntegerFromString(premiumPriceString) * 100;
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
                                competitorBasicPriceU = getIntegerFromString(currentBasicPriceString) * 100;

                                //получение цены currentPriceUString
                                try {
                                    String currentPriceUString = spanFor_a0t0.get(1).asText();
                                    competitorPriceU = getIntegerFromString(currentPriceUString) * 100;
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
                                        competitorPremiumPriceForOzon = getIntegerFromString(premiumPriceString) * 100;
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
                    if (isDiscountedItem){
                        continue;
                    }

                    productList.add(new Product(
                            "-",
                            "-",
                            "-",
                            "-",
                            "-",

                            querySearchAndCount,
                            refUrlForResult,

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

//*TO-DO. из запроса получаем те элементы, которые хотим проконтроллировать в описании(модель, тип кабеля)//////////////////////////////////////////////////

            //получаем кол-во элементов javascript
            List<HtmlElement> itemsCountSearchJavascript = page.getByXPath("//div[@class='a1j1']");
            if (itemsCountSearchJavascript == null) {
                System.out.println("не нашёл html-элемент javascript - div[@class='a1j1']");
            } else {
                try {
                    int countJavascript = itemsCountSearchJavascript.size();
                    int countForAnalise = 0;
                    int notValid = 0;
                    if (countJavascript > 24) {
                        countForAnalise = 24;
                    } else {
                        countForAnalise = countJavascript;
                    }
                    System.out.println("Получение " + countForAnalise + " товаров-аналогов для \"" + model + "\" через загрузку и обработку их ссылок");
                    for (int i = 0; i < countForAnalise; i++) {
                        String description = itemsCountSearchJavascript.get(i).asText().toLowerCase();
                        //проходимся только по тем товарам, в названии которых есть наша модель и наши дополнительные параметры
                        System.out.println(Constants.getBlueString(String.valueOf(i + 1)) + " - " + description);
                        if (description.toLowerCase().contains(model + ",") || description.toLowerCase().contains(model + " ") || description.toLowerCase().contains("(" + model + ")") || description.toLowerCase().contains(", " + model + "")) {

//в зависимости от типа продукта определяем по параметрам подходит ли он нам для дальнейшего анализа////////////////////
                            switch (productType) {
                                case Constants.PRODUCT_TYPE_1C_10:
                                case Constants.PRODUCT_TYPE_1C_39:
                                case Constants.PRODUCT_TYPE_1C_40:
                                case Constants.PRODUCT_TYPE_1C_132:
                                    System.out.println("Тип продукта - " + productType);
                                    String param = "-";
                                    try {
                                        if (arrayParams.size() == 0) {
                                            int check = 0;
                                            for (String s : Constants.listForCharging) {
                                                if (description.contains(s.toLowerCase())) {
                                                    param = s;
                                                    check++;
                                                    break;
                                                }
                                            }
                                            if (check != 0) {
                                                System.out.println("не прошёл, так как есть доп. параметр поиска = " + Constants.getRedString(param));
                                                continue;
                                            }
                                        }
                                        //если в запросе бренд, модель и кабель то нам нужен первый product, в описании которого наш param
                                        else {
                                            //определяем коллекцию с разными названиями нашего param
                                            List<String> listWithCable = Constants.getCollectionsParam(arrayParams, brand + model);
                                            int check = 0;
                                            for (String s : listWithCable) {
                                                param = s;
                                                if (description.contains(s)) {
                                                    check++;
                                                    break;
                                                }
                                            }
                                            if (check == 0) {
                                                System.out.println("не прошёл, так как отсутствует доп. параметр поиска = " + Constants.getRedString(param));
                                                continue;
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
                                    System.out.println("Тип продукта - " + productType);
                                    String param1 = "-";
                                    String param2 = "-";
                                    try {
                                        //определяем коллекцию с разными названиями нашего param
                                        List<String> listWithCableParam_1;
                                        List<String> listWithCableParam_2 = null;
                                        if (arrayParams.size() == 1) {
                                            listWithCableParam_1 = Constants.getCollectionsParamCable(arrayParams.get(0), brand + model);
                                        } else {
                                            listWithCableParam_1 = Constants.getCollectionsParamCable(arrayParams.get(0), brand + model);
                                            listWithCableParam_2 = Constants.getCollectionsParamCable(arrayParams.get(1), brand + model);
                                        }
                                        int check = 0;
                                        for (String s1 : listWithCableParam_1) {
                                            param1 = s1;
                                            if (description.contains(s1)) {
                                                if (listWithCableParam_2 != null) {
                                                    check = 10;
                                                    for (String s2 : listWithCableParam_2) {
                                                        param2 = s2;
                                                        if (description.contains(s2)) {
                                                            check++;
                                                            break;
                                                        }
                                                    }
                                                    if (check != 0) {
                                                        break;
                                                    }
                                                } else {
                                                    check++;
                                                    break;
                                                }
                                            }
                                        }
                                        if (check == 0) {
                                            System.out.println("не прошёл, тук как нет доп. параметра поиска = " + Constants.getRedString(param1));
                                            continue;
                                        }
                                        if (check == 10) {
                                            System.out.println("не прошёл, тук как нет доп. параметра поиска = " + Constants.getRedString(param2));
                                            continue;
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Ошибка при обработке списка аналогов на поиск по параметру");
                                    }
                                    break;
                                case Constants.PRODUCT_TYPE_1C_139:
                                    System.out.println("Тип продукта - " + productType);
                                    try {
                                        if (arrayParams.size() == 1) {
                                            if (!description.contains(arrayParams.get(0))) {
                                                System.out.println("не прошёл, тук как нет доп. параметра поиска = " + Constants.getRedString(arrayParams.get(0)));
                                                continue;
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Ошибка при обработке списка аналогов на поиск по параметру");
                                    }
                                    break;
                                default:
                                    System.out.println("Тип продукта - " + productType);
                            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            String refForImage = "-";
                            String vendorCode = "-";
                            String seller = "-";
                            String discountedItem = "Уценённый товар";
                            //получение ссылки на продукт
                            String hRefProduct = "https://www.ozon.ru" + itemsCountSearchJavascript.get(i).getFirstChild().getAttributes().getNamedItem("href").getNodeValue();
                            page = getHtmlPage(hRefProduct);

                            String sHRef = page.asXml();
                            if (page == null) {
                                System.out.println("Запрашиваемая страница = null");
                                return null;
                            }

/////////////////////////////определяем не уценённый ли товар, если уценённый то переходим к следующему/////////////////
                            try {
                                final HtmlDivision div_class_b2h1_b2h3 = (HtmlDivision) page.getByXPath("//div[@class='b2h1 b2h3']").get(0);
                                DomNodeList<HtmlElement> spans = div_class_b2h1_b2h3.getElementsByTagName("span");
                                if (spans.size() == 2){
                                    String text = spans.get(1).asText();
                                    if (text.contains(discountedItem)) {
                                        System.out.println(discountedItem);
                                        continue;
                                    }
                                }
                            } catch (Exception ignored) {
                            }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            //ссылка на картинку товара
                            final HtmlDivision div_class_a8n3 = (HtmlDivision) page.getByXPath("//div[@class='a8n3']").get(0);
                            refForImage = div_class_a8n3.getFirstChild().getFirstChild().getAttributes().getNamedItem("src").getNodeValue();

                            //получаем продовца
                            final HtmlDivision div_class_b1c6 = (HtmlDivision) page.getByXPath("//div[@class='b1c6']").get(0);
                            seller = div_class_b1c6.asText();

                            HtmlDivision divClassForPrices = null;
                            DomNodeList<HtmlElement> spansForPrices = null;
                            int competitorPriceU = 0;
                            int competitorBasicPriceU = 0;

                            HtmlDivision divClassForPremiumPrice = null;
                            DomNodeList<HtmlElement> spansForPremiumPrice = null;
                            int competitorPremiumPrice = 0;

                            int tries = 5;
                            boolean isNotValid1 = false;
                            boolean isNotValid2 = false;
                            boolean isNotValid3 = false;
                            boolean isNotValid4 = false;

                            while (tries > 0) {
                                //попытки получения данных об аналогах
                                try {
                                    System.out.println("Попытка № " + tries + " получить элемент \"c8q5 c8r0 b1k2\"");//1 - c2h3 c2h9 c2e7, 2 - c8q5 c8r0 b1k2, 3 - c2h3 c2i c2e7, 4 - c8q5 c8r1 b1k2
                                    divClassForPrices = (HtmlDivision) page.getByXPath("//div[@class='c8q5 c8r0 b1k2']").get(0);
                                    break;
                                } catch (Exception ignored){
                                    isNotValid1 = true;
                                }

                                try {
                                    System.out.println("Попытка № " + tries + " получить элемент \"c2h3 c2h9 c2e7\"");
                                    divClassForPrices = (HtmlDivision) page.getByXPath("//div[@class='c2h3 c2h9 c2e7']").get(0);
                                    break;
                                } catch (Exception ignored){
                                    isNotValid2 = true;
                                }

                                try {
                                    System.out.println("Попытка № " + tries + " получить элемент \"c2h3 c2i c2e7\"");
                                    divClassForPrices = (HtmlDivision) page.getByXPath("//div[@class='c2h3 c2i c2e7']").get(0);
                                    break;
                                } catch (Exception ignored){
                                    isNotValid3 = true;
                                }

                                try {
                                    System.out.println("Попытка № " + tries + " получить элемент \"c8q5 c8r1 b1k2\"");
                                    divClassForPrices = (HtmlDivision) page.getByXPath("//div[@class='c8q5 c8r1 b1k2']").get(0);
                                    break;
                                } catch (Exception ignored){
                                    isNotValid4 = true;
                                }
                                tries--;
                            }

                            if (isNotValid1 && isNotValid2 && isNotValid3 && isNotValid4){
                                System.out.println(Constants.getRedString("Необходим анализ кода страницы!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"));
                                System.out.println(page.asXml());
                                continue;
                            }

                            System.out.println("Попытка № " + tries + " получить элементы \"span\", которые содержат текущую цену и цену до скидки");
                                spansForPrices = divClassForPrices.getElementsByTagName("span");
                                System.out.println(spansForPrices.size());
                                if (spansForPrices.size() == 2) {
                                    String competitorPriceUString = spansForPrices.get(1).asText();
                                    if (competitorPriceUString.contains("₽")) {
                                        competitorPriceU = getIntegerFromString(competitorPriceUString) * 100;
                                    }
                                } else {
                                    String competitorBasicPriceUString = spansForPrices.get(1).asText();
                                    if (competitorBasicPriceUString.contains("₽")) {
                                        competitorPriceU = getIntegerFromString(competitorBasicPriceUString) * 100;
                                    }
                                    competitorBasicPriceU = getIntegerFromString(competitorBasicPriceUString) * 100;
                                    String competitorPriceUString = spansForPrices.get(2).asText();
                                    competitorPriceU = getIntegerFromString(competitorPriceUString) * 100;
                                }

                            //элемент с ценой Premium - b9w7
                            try {
                                System.out.println("Попытка получить элемент \"b9w7\" с премиум-ценой");
                                divClassForPremiumPrice = (HtmlDivision) page.getByXPath("//div[@class='b9w7']").get(0);
                                System.out.println("Попытка № " + tries + " получить элементы \"span\", которые содержат премиум-цену");
                                spansForPremiumPrice = divClassForPremiumPrice.getElementsByTagName("span");
                                String competitorPremiumPriceString = spansForPremiumPrice.get(1).asText();
                                competitorPremiumPrice = getIntegerFromString(competitorPremiumPriceString);
                            } catch (@SuppressWarnings("CatchMayIgnoreException") Exception ignored) {
//                                System.out.println("Премиум-цена отсутствует. ignored = " + ignored.getMessage());
//                                System.out.println(page.asXml());
                            }

                            String[] arrayBuff1 = hRefProduct.split("/");
                            for (int j = 0; j < arrayBuff1.length; j++) {
                                if (arrayBuff1[j].equals("id")) {
                                    vendorCode = arrayBuff1[j + 1];
                                    break;
                                }
                            }
                            if (vendorCode.equals("-")) {
                                String[] arrayBuff2 = hRefProduct.split("-");
                                String vendorCodeBuff = arrayBuff2[arrayBuff2.length - 1];
                                String[] arrayBuff3 = vendorCodeBuff.split("/");
                                vendorCode = arrayBuff3[0];
                            }

                            productList.add(new Product(
                                    "-",
                                    "-",
                                    "-",
                                    "-",
                                    "-",

                                    querySearchAndCount,
                                    refUrlForResult,

                                    "-",
                                    vendorCode,
                                    description,
                                    hRefProduct,
                                    refForImage,
                                    "-",
                                    0,
                                    competitorPriceU,
                                    0,
                                    competitorBasicPriceU,
                                    0,
                                    0,
                                    competitorPremiumPrice,

                                    seller
                            ));
                            page = null;
                        } else {
                            System.out.println("Не прошёл по названию модели = " + model);
                        }
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                    System.out.println(page.asXml());
                }
            }
//*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        return productList;
    }

    private static HtmlPage getHtmlPage(String url) {
        System.out.println("проверка - lock свободен: " + lockOzon.toString());
        lockOzon.lock();
        HtmlPage page = null;
        boolean isBloking = true;
        String blocking = "Блокировка сервером";
        while (isBloking) {
            int count = 10;//количество попыток получения валидной станицы ozon
            //webClientForOzon.getOptions().setTimeout(15000);
            int countPageNull = 0;
            while (count > 0) {
                System.out.println("Непосредственно получение страницы. Время таймаута = " + webClientForOzon.getOptions().getTimeout() / 1000 + " c");
                try {
                    if (countPageNull == 2){
                        System.out.println("Кол-во полученных страниц NULL = 2. Меняем IP");
                        switchIpForProxy();
                    }
                    if (countUseIP == 5){
                        System.out.println("Кол-во использования IP № " + countSwitchIP + " = " + countUseIP + ". Меняем IP");
                        switchIpForProxy();
                        countUseIP = 0;
                        countSwitchIP++;
                    }

                    page = webClientForOzon.getPage(url);

                    countUseIP++;

                } catch (Exception ignored) {
                    System.out.println("Ошибка при получении страницы для запроса \"" + myQuery + "\": " + ignored.getMessage());
                    if (count == 0) {
                        System.out.println("Попытки получения страницы поискового запроса закончились");
                        webClientForOzon.close();
                        lockOzon.unlock();
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
        System.out.println(Constants.getGreenString("IP №" + countSwitchIP) + ". Страница ozon для запроса \"" + myQuery + "\" получена");
        lockOzon.unlock();
        System.out.println("проверка - lock свободен: " + lockOzon.toString());
        webClientForOzon.close();
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
                page = webClientForOzon.getPage(uri);
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
//        webClientForOzon.close();
    }

    private static Iterable<DomElement> getDomElements(HtmlElement itemProduct) throws Exception {

        while (itemProduct.getChildElementCount() != 3) {
            itemProduct = (HtmlElement) itemProduct.getFirstChild();
        }
        return itemProduct.getChildElements();
    }

    private static int getIntegerFromString(String price) {
        StringBuilder resultPrice = new StringBuilder();

        Pattern p = Pattern.compile("-?\\d+");

        Matcher m = p.matcher(price);
        while (m.find()) {
            resultPrice.append(m.group());
        }
        return Integer.parseInt(resultPrice.toString());
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
//                                    if (nomenclature.contains(productModel)) {
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
                                            if (p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase()) || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
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
//                                    if (nomenclature.contains(productModel)) {
                                    if (nomenclature.toLowerCase().contains(productModel + ",")
                                            || nomenclature.toLowerCase().contains(productModel + " ")
                                            || nomenclature.toLowerCase().contains("(" + productModel + ")")
                                            || nomenclature.toLowerCase().contains(", " + productModel + "")) {
                                        int check = 0;
                                        for (String s : listWithCable) {
                                            if (nomenclature.contains(s)) {
                                                if (p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase()) || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
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
//                                if (nomenclature.contains(productModel)) {
                                if (nomenclature.toLowerCase().contains(productModel + ",") || nomenclature.toLowerCase().contains(productModel + " ") || nomenclature.toLowerCase().contains("(" + productModel + ")") || nomenclature.toLowerCase().contains(", " + productModel + "")) {
                                    int check = 0;
                                    for (String s1 : listWithCableParam_1) {
                                        if (nomenclature.contains(s1)) {
                                            if (listWithCableParam_2 != null){
                                                for (String s2 : listWithCableParam_2) {
                                                    if (nomenclature.contains(s2)) {
                                                        if (myVendorCodes.contains(p.getCompetitorVendorCode())) {
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
                                                if (myVendorCodes.contains(p.getCompetitorVendorCode())) {
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
                                        if (p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase()) || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
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
                        if (p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER.toLowerCase()) || p.getCompetitorName().toLowerCase().equals(Constants.MY_SELLER_2.toLowerCase())) {
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
