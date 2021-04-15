import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static java.util.Comparator.comparing;

public class ParserWildBer {

    private Object mon = new Object();
    private static Map<String, List<Product>> resultMapForQueries = new LinkedHashMap<>();

    public Product getProduct(String myVendorCodeFromRequest, String category, String brand, String productType, String productModel, List<String> arrayParams, Set myVendorCodes, String specQuerySearch, WebClient webClient){
        List<Product> productList;
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


        StringBuilder query = new StringBuilder("-");
        Document page = null;
        List<String> paramsForRequest = null;

        //получение html-страницы для моего артикула
        page = getDocumentPageForVendorCode(myVendorCodeFromRequest);
        //если ничего не вернулось(страница не существует) то возвращаем нулевой продукт
        if (page == null){
            return product;
        }

//////////////формирование поискового запроса на основании данных, полученных из анализа страницы моего продукта, для которого мы исчем аналоги
        //обработка html-страницы для формирования поискового запроса аналогов
        if (productModel.isEmpty() || productModel.equals("-")){
            paramsForRequest = getDataForRequestFromCategory(page, category, brand);
            if (paramsForRequest.size() == 0){
                product.setQueryForSearch("Мало данных для формирования поискового запроса");
                return product;
            }

            //в заввисимости от категории определяем параметры запроса для поиска конкурентов
            switch (category){
                case Constants.CATEGORY_WILD_10:
                    String count = paramsForRequest.get(0);
                    //для поиска по маскам надо в запросе передать "Маски одноразовые" и кол-во штук в упаковке
                    query = new StringBuilder(category + " " + count);
                    productList = getCatalogProducts(query.toString().toLowerCase(), brand);
                    if (productList.size() != 0){
                        product = productList.stream().filter(p -> p.getCompetitorProductName().contains(count)).findAny().orElse(null);
                    }
                    break;

                case Constants.CATEGORY_WILD_4:
                    query = new StringBuilder(paramsForRequest.get(0));
                    productList = getCatalogProducts(query.toString().toLowerCase(), brand);
                    if (productList.size() != 0){
                        product = productList.stream().findFirst().orElse(null);
                    }
                    break;

                //для данных категорий запрос формирунтся из бренда и модели
                case Constants.CATEGORY_WILD_1:
                case Constants.CATEGORY_WILD_2:
                case Constants.CATEGORY_WILD_5:
                case Constants.CATEGORY_WILD_6:
                case Constants.CATEGORY_WILD_7:
                case Constants.CATEGORY_WILD_8:
                case Constants.CATEGORY_WILD_15:
                case Constants.CATEGORY_WILD_18:
                case Constants.CATEGORY_WILD_20:
                case Constants.CATEGORY_WILD_21:
                case Constants.CATEGORY_WILD_22:
                case Constants.CATEGORY_WILD_34:
                case Constants.CATEGORY_WILD_35:
                case Constants.CATEGORY_WILD_37:
                case Constants.CATEGORY_WILD_40:
                    query = new StringBuilder(brand);
                    for (String s : paramsForRequest) {
                        query.append(" ").append(s);
                    }
                    query = new StringBuilder(query.toString().toLowerCase());
                    productList = getCatalogProducts(query.toString().toLowerCase(), brand);
                    if (productList.size() != 0){
                        //проходимся по всему списку и находим продукт с наименьшей ценой
                        product = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
                    }
                    break;

                //для данных категорий запрос формирунтся из бренда, категории и модели
                case Constants.CATEGORY_WILD_3:
                case Constants.CATEGORY_WILD_9:
                case Constants.CATEGORY_WILD_11:
                case Constants.CATEGORY_WILD_12:
                case Constants.CATEGORY_WILD_13:
                case Constants.CATEGORY_WILD_14:

                case Constants.CATEGORY_WILD_17:
                case Constants.CATEGORY_WILD_23:
                case Constants.CATEGORY_WILD_24:
                case Constants.CATEGORY_WILD_25:
                case Constants.CATEGORY_WILD_26:
                case Constants.CATEGORY_WILD_27:
                case Constants.CATEGORY_WILD_28:
                case Constants.CATEGORY_WILD_29:
                case Constants.CATEGORY_WILD_30:
                case Constants.CATEGORY_WILD_31:
                case Constants.CATEGORY_WILD_32:
                case Constants.CATEGORY_WILD_33:
                case Constants.CATEGORY_WILD_36:
                case Constants.CATEGORY_WILD_38:
                case Constants.CATEGORY_WILD_39:
                case Constants.CATEGORY_WILD_41:
                case Constants.CATEGORY_WILD_42:
                case Constants.CATEGORY_WILD_43:
                case Constants.CATEGORY_WILD_44:
                case Constants.CATEGORY_WILD_45:
                case Constants.CATEGORY_WILD_46:
                case Constants.CATEGORY_WILD_47:
                case Constants.CATEGORY_WILD_48:

                    query = new StringBuilder(brand + " " + category);
                    for (String s : paramsForRequest) {
                        query.append(" ").append(s);
                    }
                    query = new StringBuilder(query.toString().toLowerCase());
                    productList = getCatalogProducts(query.toString().toLowerCase(), brand);
                    if (productList.size() != 0){
                        //проходимся по всему списку и находим продукт с наименьшей ценой
                        product = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
                    }
                    break;

                case Constants.CATEGORY_WILD_16:

                    String[] buffArray1 = paramsForRequest.get(0).split("/");
                    String[] buffArray2 = buffArray1[1].split(",");
                    query = new StringBuilder(brand + " " + buffArray2[0].trim());

                    query = new StringBuilder(query.toString().toLowerCase());
                    productList = getCatalogProducts(query.toString().toLowerCase(), brand);
                    if (productList.size() != 0){
                        //проходимся по всему списку и находим продукт с наименьшей ценой
                        product = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
                    }
                    break;
            }
            //пользуемся поисковым запросом, полученным на основании анализа номенклатуры из базы 1С
        } else {
////////////поисковый запрос мы получаем из аргументов метода: brand, productModel, specQuerySearch/////////////////////
            //если наш кеш пустой, то заносим туда первый каталог
            if (resultMapForQueries.size() == 0) {
                System.out.println("Получение каталога аналогов для \"" + brand.toLowerCase() + " " + productModel.toLowerCase() + "\"");
                System.out.println("дополнительный параметр поиска - " + arrayParams.toString());
                productList = getCatalogProducts(brand.toLowerCase() + " " + productModel.toLowerCase(), brand);

                resultMapForQueries.put(brand + " " + productModel, productList);

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
                //проверяем наш кеш на наличие каталога для запроса brand, productModel
            } else if (resultMapForQueries.containsKey(brand + " " + productModel)){
                System.out.println("Для запроса \"" + brand + " " + productModel + " + arrayParams = " + arrayParams.toString() + "\" в кеше найден каталог аналогов, поэтому запрос на ozon не осуществляем");
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
                System.out.println("Получение каталога аналогов для \"" + brand.toLowerCase() + " " + productModel.toLowerCase() + "\"");
                productList = getCatalogProducts(brand.toLowerCase() + " " + productModel.toLowerCase(), brand);

                resultMapForQueries.put(brand + " " + productModel, productList);

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
            }
//            System.out.println("Получение каталога аналогов для \"" + brand.toLowerCase() + " " + productModel.toLowerCase() + "\"");
//            productList = getCatalogProducts(brand.toLowerCase() + " " + productModel.toLowerCase(), brand);
//            if (productList.size() != 0) {
//                System.out.println("Размер каталога аналогов = " + productList.size());
//                //проходимся по всему списку и находим продукт с наименьшей ценой
//                System.out.println("Получение аналога с мин. ценой и подходящего по параметрам productModel и arrayParams");
//                Product buffProduct = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest, productType, brand, productModel, arrayParams);
//                if (buffProduct != null) {
//                    product = buffProduct;
//                }
//            } else {
//                System.out.println("Для данного запроса ничего не найдено");
//            }
        }

        assert product != null;
//        //устанавливаем имя продовца
//        String sellerName = getSellerName(product.getCompetitorVendorCode(), webClient);
//        product.setCompetitorName(sellerName);
        product.setCompetitorName("-");

        //устанавливаем мою спецакцию, если она есть
        product.setMySpecAction(getMySpecAction(page));

        //устанавливаем ссылку на картинку моего товара
        product.setMyRefForImage(getMyProductsPhoto(page));

        //устанавливаем поисковый запрос аналогов
        product.setQueryForSearch(specQuerySearch);

        //устанавливаем наименование моего товара
        product.setMyProductName(getMyProductsTitle(page));

        //устанавливаем ссылку на артикул моего товара
        product.setMyRefForPage(getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP"));

        //устанавливаем мой vendorCode
        product.setMyVendorCodeFromRequest(myVendorCodeFromRequest);

        return product;
    }

    private Document getDocumentPageForVendorCode(String myVendorCodeFromRequest) {
        String url = getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP");

        Document page = null;
        try {
            page = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .timeout(20000)
                    .referrer("https://google.com")
                    .get();
        } catch (IOException e) {
            System.out.println(Constants.NOT_FOUND_PAGE);
        }
        return page;
    }

    private static Product getProductWithLowerPrice(List<Product> productList, Set myVendorCodes, String myVendorCodeFromRequest, String productType, String brand, String productModel, List<String> arrayParams) {
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
                                    if (!myVendorCodes.contains(p.getCompetitorVendorCode())){
                                        String nomenclature = p.getCompetitorProductName().toLowerCase();
                                        if (nomenclature.contains(productModel)){
                                            int check = 0;
                                            for (String s: Constants.listForCharging){
                                                if (nomenclature.contains(s.toLowerCase())){
                                                    check++;
                                                    break;
                                                }
                                            }
                                            if (check == 0){
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
                                List<String> listWithCable = getCollectionsParam(arrayParams, brand + productModel);
                                for (Product p : productList) {
                                    if (!myVendorCodes.contains(p.getCompetitorVendorCode())) {
                                        String nomenclature = p.getCompetitorProductName().toLowerCase();
                                        if (nomenclature.contains(productModel)){
                                            int check = 0;
                                            for (String s : listWithCable) {
                                                if (nomenclature.contains(s)) {
                                                    product = p;
                                                    check++;
                                                    break;
                                                }
                                            }
                                            if (check != 0){
                                                break;
                                            }
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
                        if (productModel != null & arrayParams.size() == 2){
                            //определяем коллекцию с разными названиями нашего param
                            List<String> listWithCableParam_1 = getCollectionsParamCable(arrayParams.get(0), brand + productModel);
                            List<String> listWithCableParam_2 = getCollectionsParamCable(arrayParams.get(1), brand + productModel);
                            for (Product p : productList) {
                                if (!myVendorCodes.contains(p.getCompetitorVendorCode())) {
                                    String nomenclature = p.getCompetitorProductName().toLowerCase();
                                    if (nomenclature.contains(productModel)){
                                        int check = 0;
                                        for (String s1 : listWithCableParam_1) {
                                            if (nomenclature.contains(s1)) {
                                                for (String s2 : listWithCableParam_2) {
                                                    if(nomenclature.contains(s2)){
                                                        product = p;
                                                        check++;
                                                        break;
                                                    }
                                                }
                                                if (check != 0){
                                                    break;
                                                }
                                            }
                                        }
                                        if (check != 0){
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

                case Constants.PRODUCT_TYPE_1C_139:
                    try {
                        if (productModel != null){
                            if (arrayParams.size() == 1){
                                for (Product p : productList) {
                                    if (!myVendorCodes.contains(p.getCompetitorVendorCode())){
                                        String nomenclature = p.getCompetitorProductName().replaceAll(",", "").toLowerCase();
                                        if (nomenclature.contains(arrayParams.get(0))) {
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
                        if (!myVendorCodes.contains(p.getCompetitorVendorCode())) {
                            product = p;
                            break;
                        }
                    }
            }
            return product;
        }
    }

    private String getSellerName(String vendorCode, WebClient webClient){
//        Document page = getDocumentPageForVendorCode(vendorCode);
        String url = getString("https://www.wildberries.ru/catalog/", vendorCode, "/detail.aspx?targetUrl=SP");
        HtmlPage page = null;
        HtmlDivision spanSellerName;
        synchronized (mon) {
            try {
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setJavaScriptEnabled(true);

                page = webClient.getPage(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Element elementSellerName = page.select("span[class=seller__text]").first();
            webClient.waitForBackgroundJavaScript(7000);
            String pageString = page.asXml();
            spanSellerName = (HtmlDivision) page.getByXPath("//div[@class='seller']").get(0);
        }
        return spanSellerName.asText();
    }

    //метод читающий на странице продукта характеристики, по которым будет осуществляться запрос на поиск аналогов!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static List<String> getDataForRequestFromCategory(Document page, String category, String brand){
        List<String> paramsForRequest = new ArrayList<>();
        String title = getMyProductsTitle(page);
        String description = getProductDescription(page);

        //пытаемся получить название модели из характеристик
        String modelName = getModelName(page);
        if (modelName.equals("-")) {
            //пытаемся получить название модели из title
            modelName = getProductModelFromTitle(title, brand);
            if (modelName.equals("-")){
                //пытаемся получить название модели из описания
                modelName = getProductModelFromDescription(description, brand);
            }
        }
        //если название модели не найдено, то возвращаем всю title
        if (modelName.equals("-")){
            paramsForRequest.add(title);
            return paramsForRequest;
        }

        //--- 1 --- первым параметром бедет название модели либо кол-во элементов в упаковке
        paramsForRequest.add(modelName);

        //определяем дополнительные параметры запроса в зависимости от категории товара
        switch (category) {
            case Constants.CATEGORY_WILD_1:
            case Constants.CATEGORY_WILD_6:
                getParamsFromTitleForCharging(paramsForRequest, title);
                break;

            case Constants.CATEGORY_WILD_8:
                String myType = title.toLowerCase();
                try {
                    String[] arrayTitle1 = title.split("/");
                    brand = brand.toLowerCase();
                    if (arrayTitle1[1].toLowerCase().contains(brand)){
                        String[] types = arrayTitle1[1].toLowerCase().split(brand);
                        String[] arrayTitle2 = arrayTitle1[1].split(",", 2);
                        String[] arrayTitle3 = arrayTitle2[0].toLowerCase().split(brand);

                        paramsForRequest.set(0, arrayTitle3[1].trim());
                    }
                } catch (Exception e) {
                }
                for (String type : Constants.listForCable) {
                    if (myType.replaceAll(",", "").contains(type)) {
                        paramsForRequest.add(type);
                    }
                }
                break;

            case Constants.CATEGORY_WILD_3:
            case Constants.CATEGORY_WILD_12:
                String typeConnect = getTypeConnectForHeadset(page);
                if (typeConnect == ""){
                    for (String type : Constants.listForHeadset) {
                        if (title.toLowerCase().contains(type)) {
                            paramsForRequest.add(type);
                        }
                    }
                } else {
                    paramsForRequest.add(typeConnect);
                }
                break;

            case Constants.CATEGORY_WILD_7:
                String[] arrayForTitle = title.split(",", 2);
                String[] arrayForParams = arrayForTitle[1].replace(",", "").split(" ");
                for (String s: arrayForParams){
                    for (String type : Constants.listForTypeGlass) {
                        if (s.equalsIgnoreCase(type)) {
                            paramsForRequest.add(type);
                        }
                    }
                }

                break;

            case Constants.CATEGORY_WILD_16:
                try {
                    String[] strBuf1 = title.split(", ");
                    String[] strBuf2 = strBuf1[0].split(" ");
                    paramsForRequest.add(strBuf2[strBuf2.length - 1]);
                    return paramsForRequest;
                } catch (Exception e) {
                }
                break;

            case Constants.CATEGORY_WILD_40:
            case Constants.CATEGORY_WILD_34:
                String[] arrayStr = title.split(",", 2);
                String[] arrayStr2 = arrayStr[0].split("/");
                brand = brand.toLowerCase();
                if (category.equals(Constants.CATEGORY_WILD_40)){
                    paramsForRequest.set(0, arrayStr2[1]);
                } else {
                    String str = arrayStr2[1].toLowerCase().replace(brand, "");
                    paramsForRequest.set(0, str);
                }

                for (String s: Constants.listForBugs){
                    if (title.contains(s)){
                        paramsForRequest.add(s);
                    }
                }
                break;
        }
        return paramsForRequest;
    }

    private static String getTypeConnectForHeadset(Document page) {
        Elements elementsParams = page.select("div[class=pp]");
        String typeConnect = "";
        for (Element elementParam : elementsParams) {
            if (elementParam.text().contains("Разъем подключения наушников")){
                for (String type : Constants.listForHeadset) {
                    if (elementParam.text().contains(type)){
                        typeConnect = type;
                    }
                }
            }
        }
        return typeConnect;
    }

    private static String getProductModelFromDescription(String description, String brand) {
        brand = brand.toLowerCase();
        String model = "модель:";
        description = description.toLowerCase();
        if (description.contains(brand)){
            String[] arrayDescription1 = description.split(brand);
            String[] arrayDescription2 = arrayDescription1[1].trim().split(" ");
            return arrayDescription2[0];
        } else if (description.contains(model)){
            String[] arrayDescription1 = description.split(model);
            String[] arrayDescription2 = arrayDescription1[1].trim().split(" ");
            return arrayDescription2[0];
        }
        return "-";
    }

    private static String getProductDescription(Document page) {
        Element elementDescription = page.select(Constants.ELEMENT_WITH_DESCRIPTION_MY_PRODUCT).first();
        if (elementDescription != null){
            return elementDescription.text();
        } else {
            return Constants.NOT_FOUND_HTML_ITEM;
        }
    }

    private static void getParamsFromTitleForCharging(List<String> paramsForRequest, String title) {
        if (title.contains("кабелем") || title.contains("кабель") || title.contains("держателем") || title.contains("держатель")){
            String[] arrayForTitle = title.split(",", 2);
            String[] arrayForParams = arrayForTitle[1].replace(",", "").split(" ");
            for (String s: arrayForParams){
                for (String param : Constants.listForCharging) {
                    if (s.equalsIgnoreCase(param)) {
                        paramsForRequest.add(param);
                    }
                }
            }
        }
    }

    //ищем модель в характеристиках
    private static String getModelName(Document page) {
        Element params = page.select(Constants.ELEMENT_WITH_PARAMS_MY_PRODUCT).first();
        if (params != null){
            Elements elements = params.getAllElements();
            String param4 = elements.get(4).text();
            if (param4.equals(Constants.PARAM_1_1)) {
                String countItemsFromPackage = elements.get(5).text();
                String[] array = countItemsFromPackage.split(" ");
                //возвращаем кол-во элементов в упаковке
                return array[0];
            } else if (param4.equals(Constants.PARAM_1_2)) {
                //возвращаем название модели
                return elements.get(5).text();
            }
        } else {
            return "-";
        }
        return "-";
    }

    private static String getMyProductsPhoto(Document page) {
        //элемент переписан 16.02.21
        //Element elementImage1 = page.select("img[class=j-zoom-photo preview-photo]").first();
        Element elementImage2 = page.select(Constants.ELEMENT_WITH_PHOTO_MY_PRODUCT).first();
        if (elementImage2 != null){
            return "https:" + elementImage2.attr(Constants.ATTRIBUTE_WITH_REF_FOR_IMAGE_1);
        } else return Constants.NOT_FOUND_HTML_ITEM;
    }

    //по наличию этого параметра определяем есть ли акция
    private static String getMySpecAction(Document page) {
        Element specAction = page.select(Constants.ELEMENT_WITH_SPEC_ACTION_MY_PRODUCT).first();
        if (specAction != null){
            return specAction.text();
        } else {
            return Constants.NOT_FOUND_HTML_ITEM;
        }
    }

    //для формирования запроса на основании названия модели товара, которое находится в заголовке как аправило сразу после бренда
    private static String getMyProductsTitle(Document page) {
        Element elementBrandAndNameTitle = page.select(Constants.ELEMENT_WITH_TITLE_MY_PRODUCT).first();
        if (elementBrandAndNameTitle != null){
            return elementBrandAndNameTitle.text();
        } else {
            return Constants.NOT_FOUND_HTML_ITEM;
        }
    }

    private static String getProductModelFromTitle(String title, String brand) {

        String[] strBuf1 = title.split("/", 2);
        brand = brand.toLowerCase().trim();

        String[] strBuf2 = strBuf1[1].trim().split(",");
        String model = "";
        for (String s : strBuf2) {
            if ((s.toLowerCase().contains(brand)) || s.toLowerCase().contains(brand.substring(0, 2))) {

                String[] strBuf3 = s.trim().split(" ");

                for (int z = 0; z < strBuf3.length; z++) {
                    if (strBuf3[z].equalsIgnoreCase(brand)) {
                        for (int j = z + 1; j < strBuf3.length; j++) {
                            model = model + strBuf3[j] + " ";
                        }
                        break;
                    } else if (strBuf3[z].toLowerCase().startsWith(brand.substring(0, 2))) {
                        for (int j = z; j < strBuf3.length; j++) {
                            model = model + strBuf3[j] + " ";
                        }
                        break;
                    }
                }
            }
        }
        if (model.equals("")){
            for (int i = 0; i < strBuf2.length; i++){
                if (strBuf2[i].toLowerCase().contains(brand)){
                    model = strBuf2[i + 1].trim();
                }
            }
        }
        if (model.equals("")){
            char[] strBrandToArray = brand.toCharArray();
            for (String s2: strBuf2){
                String[] strBuf3 = s2.toLowerCase().trim().split(" ");
                for (String s3: strBuf3){
                    if (s3.startsWith(String.valueOf(strBrandToArray[0])) && s3.contains("-")){
                        model = s3;
                    }
                }
            }
        }
        if (model.equals("")){
            String[] arrayTitle = title.replace(",", "").split(" ");
            Character character1 = brand.toUpperCase().charAt(0);
            for (String str: arrayTitle){
                if (str.length() == 3){
                    Character character2 = str.toUpperCase().charAt(0);
                    if (character1.equals(character2)){
                        model = str;
                    }
                }
            }
        }
        if (model.equals("")){
            model = "-";
        }
        return model.replaceAll("()", "").trim();
    }
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private static List<Product> getCatalogProducts(String query, String brand) {
        List<Product> productList;
        Document page;
        page = getPageForSearchQuery(query);

        //получение бренда, артикула, имени товара, ссылки на страницу товара, ссылки на картинкау товара, спец-акции, рейтинга
        productList = getCatalogProductsForRequestPage(page, brand);
        if (productList.size() == 0){
            return productList;
        }
        //получение цены и скидок через json
        HttpUrlConnectionHandler.getCatalog(productList, query);

        return productList;
    }

    private static Document getPageForSearchQuery(String query) {
        String url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&xsearch=true&sort=priceup");// для вер. 2
        //String url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&&sort=priceup");// для вер. 1

        Document page = null;
        while (page == null){
            try {
                System.out.println("Получение страницы с аналогами");
                page = Jsoup.parse(new URL(url), 30000);
            } catch (IOException ignored) {
                try {
                    System.out.println("Получаем пустую страницу. Видимо проблемы с соединением");
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        return page;
    }

    private static String getString(String s, String queryUTF8, String s2) {
        return s + queryUTF8 + s2;
    }

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

    private static List<Product> getCatalogProductsForRequestPage(Document page, String myBrand){
        List<Product> productList = new ArrayList<>();
        if (page != null){
            Element catalog = page.select(Constants.ELEMENT_WITH_CATALOG).first();
            if (catalog == null){
                return productList;
            }
            Elements goods = catalog.select(Constants.ELEMENT_WITH_PRODUCT);
            int countSearch = goods.size();
            for (Element good : goods) {
//                //артикул
//                String vendorCode = good.attr(Constants.ATTRIBUTE_WITH_VENDOR_CODE); артикул исчем по новому с 14.04.21

                Element fullProductCard = good.select(Constants.ELEMENT_WITH_CARD_PRODUCT).first();

                //имя товара
                Element nameGoods = fullProductCard.select(Constants.ELEMENT_WITH_NAME_PRODUCT).first();
                String productName = nameGoods.text();

                //ссылка на товар
                String refForPage = Constants.MARKETPLACE + fullProductCard.attr(Constants.ATTRIBUTE_WITH_REF_FOR_PAGE_PRODUCT);

                //артикул
                String vendorCode = "-";
                String[] arrayStringsForRefForPage = refForPage.split("/");
                for (int i = 0; i < arrayStringsForRefForPage.length; i++){
                    if (arrayStringsForRefForPage[i].contains("catalog")){
                        vendorCode = arrayStringsForRefForPage[i +1];
                        break;
                    }
                }

                //ссылка на картинку товара
                Element img = fullProductCard.select(Constants.ELEMENT_WITH_REF_FOR_IMAGE).first();
                String refForImg = "-";
                String refForImgTemp1 = img.attr(Constants.ATTRIBUTE_WITH_REF_FOR_IMAGE_1);
                String refForImgTemp2 = img.attr(Constants.ATTRIBUTE_WITH_REF_FOR_IMAGE_2);
                if (refForImgTemp2.equals("")){
                    refForImg = "https:" + refForImgTemp1;
                } else {
                    refForImg = "https:" + refForImgTemp2;
                }

                //спец-акция
                Element priceGoods = fullProductCard.select(Constants.ELEMENT_WITH_SPEC_ACTION).first();
                String specAction = "-";
                if (priceGoods != null){
                    specAction = priceGoods.text();
                }

                //рейтинг
                Element star = fullProductCard.getElementsByAttributeValueStarting("class", "c-stars").first();
                int rating = 0;
                if (star != null){
                    String nameClass = star.className();
                    rating = Integer.parseInt(String.valueOf(nameClass.charAt(nameClass.length() - 1)));
                }

                //Brand
                Element brand = fullProductCard.select(Constants.ELEMENT_WITH_BRAND_NAME).first();
                String string = brand.text();
                String brandName = string.substring(0, string.length() - 2).toLowerCase();
                if (!myBrand.equals("Aiqura")){
                    if (!brandName.contains(myBrand.toLowerCase())) continue;
                }

                productList.add(new Product(
                        "-",
                        "-",
                        "-",
                        "-",
                        "-",

                        "-",
                        countSearch,

                        brandName,
                        vendorCode,
                        productName,
                        refForPage,
                        refForImg,
                        specAction,
                        rating,

                        0,
                        0,
                        0,
                        0,
                        0,
                        0,

                        "-"));
            }
        }
        return productList;
    }

    private static List<String> getCollectionsParam(List<String> arrayParams, String url) {
        //определяем коллекцию с названием одного и того же кабеля
        List<String> listWithCable = null;
        if (arrayParams.size() == 1){
            String param = arrayParams.get(0);
            boolean b = Constants.listForChargingMicro.contains(param);

            if (b){
                listWithCable = Constants.listForChargingMicro;
            } else if (Constants.listForChargingApple.contains(param)){
                listWithCable = Constants.listForChargingApple;
            } else if (Constants.listForChargingType.contains(param)){
                listWithCable = Constants.listForChargingType;
            } else {
                System.out.println("Уточнить параметр кабеля для ссылки \"" + url + "\"");
            }
            return listWithCable;
        } else {
            return  null;
        }
    }

    private static List<String> getCollectionsParamCable(String param, String url) {
        //определяем коллекцию с названием одного и того же кабеля
        List<String> listWithCable = null;
        boolean b = Constants.listForChargingMicro.contains(param);

        if (b) {
            listWithCable = Constants.listForChargingMicro;
        } else if (Constants.listForChargingApple.contains(param)) {
            listWithCable = Constants.listForChargingApple;
        } else if (Constants.listForChargingType.contains(param)) {
            listWithCable = Constants.listForChargingType;
        } else if (Constants.listForCharging_2in1.contains(param)) {
            listWithCable = Constants.listForCharging_2in1;
        } else if (Constants.listForCharging_3in1.contains(param)) {
            listWithCable = Constants.listForCharging_3in1;
        } else if (Constants.listForCharging_4in1.contains(param)) {
            listWithCable = Constants.listForCharging_4in1;
        } else if (Constants.listForCable_0_2m.contains(param)) {
            listWithCable = Constants.listForCable_0_2m;
        } else if (Constants.listForCable_0_25m.contains(param)) {
            listWithCable = Constants.listForCable_0_25m;
        } else if (Constants.listForCable_1m.contains(param)) {
            listWithCable = Constants.listForCable_1m;
        } else if (Constants.listForCable_1_2m.contains(param)) {
            listWithCable = Constants.listForCable_1_2m;
        } else if (Constants.listForCable_1_4m.contains(param)) {
            listWithCable = Constants.listForCable_1_4m;
        } else if (Constants.listForCable_1_6m.contains(param)) {
            listWithCable = Constants.listForCable_1_6m;
        } else if (Constants.listForCable_1_8m.contains(param)) {
            listWithCable = Constants.listForCable_1_8m;
        } else if (Constants.listForCable_2m.contains(param)) {
            listWithCable = Constants.listForCable_2m;
        } else if (Constants.listForCable_3m.contains(param)) {
            listWithCable = Constants.listForCable_3m;
        } else {
            System.out.println("Уточнить параметр кабеля для ссылки \"" + url + "\"");
        }
        return listWithCable;
    }
}
