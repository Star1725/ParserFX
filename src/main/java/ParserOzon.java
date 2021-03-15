import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;

public class ParserOzon {

    private Object mon = new Object();
    private static WebClient webClientForOzon;

    public Product getProduct(String myVendorCodeFromRequest, String category, String brand,String productType, Set myVendorCodes, String querySearchForOzon, WebClient webClient, int marketPlaceFlag){
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
        webClientForOzon = webClient;

        StringBuilder query = new StringBuilder(querySearchForOzon);
        //в заввисимости от категории ozon определяем параметры запроса для поиска конкурентов
        switch (productType) {
            case Constants.PRODUCT_TYPE_1C_23:
                productList = getCatalogProducts(query.toString().toLowerCase(), brand);

                product = getProductWithLowerPrice(productList, myVendorCodes, myVendorCodeFromRequest);

                break;
        }

//        //в заввисимости от категории определяем параметры запроса для поиска конкурентов
//        switch (category){
//            case Constants.CATEGORY_WILD_10:
//                String count = paramsForRequest.get(0);
//                //для поиска по маскам надо в запросе передать "Маски одноразовые" и кол-во штук в упаковке
//                query = new StringBuilder(category + " " + count);
//                productList = getCatalogProducts(query.toString().toLowerCase(), brand);
//                if (productList.size() != 0){
//                    product = productList.stream().filter(p -> p.getCompetitorProductName().contains(count)).findAny().orElse(null);
//                }
//                break;
//
//            case Constants.CATEGORY_WILD_4:
//                query = new StringBuilder(paramsForRequest.get(0));
//                productList = getCatalogProducts(query.toString().toLowerCase(), brand);
//                if (productList.size() != 0){
//                    product = productList.stream().findFirst().orElse(null);
//                }
//                break;
//
//                //для данных категорий запрос формирунтся из бренда и модели
//            case Constants.CATEGORY_WILD_1:
//            case Constants.CATEGORY_WILD_2:
//            case Constants.CATEGORY_WILD_5:
//            case Constants.CATEGORY_WILD_6:
//            case Constants.CATEGORY_WILD_7:
//            case Constants.CATEGORY_WILD_8:
//            case Constants.CATEGORY_WILD_15:
//            case Constants.CATEGORY_WILD_18:
//            case Constants.CATEGORY_WILD_20:
//            case Constants.CATEGORY_WILD_21:
//            case Constants.CATEGORY_WILD_22:
//            case Constants.CATEGORY_WILD_34:
//            case Constants.CATEGORY_WILD_35:
//            case Constants.CATEGORY_WILD_37:
//            case Constants.CATEGORY_WILD_40:
//                query = new StringBuilder(brand);
//                for (String s : paramsForRequest) {
//                    query.append(" ").append(s);
//                }
//                query = new StringBuilder(query.toString().toLowerCase());
//                productList = getCatalogProducts(query.toString().toLowerCase(), brand);
//                if (productList.size() != 0){
//                    //проходимся по всему списку и находим продукт с наименьшей ценой
//                    product = getProductWithLowerPrice(productList, myVendorCodes);
//                }
//                break;
//
//            //для данных категорий запрос формирунтся из бренда, категории и модели
//            case Constants.CATEGORY_WILD_3:
//            case Constants.CATEGORY_WILD_9:
//            case Constants.CATEGORY_WILD_11:
//            case Constants.CATEGORY_WILD_12:
//            case Constants.CATEGORY_WILD_13:
//            case Constants.CATEGORY_WILD_14:
//
//            case Constants.CATEGORY_WILD_17:
//            case Constants.CATEGORY_WILD_23:
//            case Constants.CATEGORY_WILD_24:
//            case Constants.CATEGORY_WILD_25:
//            case Constants.CATEGORY_WILD_26:
//            case Constants.CATEGORY_WILD_27:
//            case Constants.CATEGORY_WILD_28:
//            case Constants.CATEGORY_WILD_29:
//            case Constants.CATEGORY_WILD_30:
//            case Constants.CATEGORY_WILD_31:
//            case Constants.CATEGORY_WILD_32:
//            case Constants.CATEGORY_WILD_33:
//            case Constants.CATEGORY_WILD_36:
//            case Constants.CATEGORY_WILD_38:
//            case Constants.CATEGORY_WILD_39:
//            case Constants.CATEGORY_WILD_41:
//            case Constants.CATEGORY_WILD_42:
//            case Constants.CATEGORY_WILD_43:
//            case Constants.CATEGORY_WILD_44:
//            case Constants.CATEGORY_WILD_45:
//            case Constants.CATEGORY_WILD_46:
//            case Constants.CATEGORY_WILD_47:
//            case Constants.CATEGORY_WILD_48:
//
//                query = new StringBuilder(brand + " " + category);
//                for (String s : paramsForRequest) {
//                    query.append(" ").append(s);
//                }
//                query = new StringBuilder(query.toString().toLowerCase());
//                productList = getCatalogProducts(query.toString().toLowerCase(), brand);
//                if (productList.size() != 0){
//                    //проходимся по всему списку и находим продукт с наименьшей ценой
//                    product = getProductWithLowerPrice(productList, myVendorCodes);
//                }
//                break;
//
//            case Constants.CATEGORY_WILD_16:
//
//                String[] buffArray1 = paramsForRequest.get(0).split("/");
//                String[] buffArray2 = buffArray1[1].split(",");
//                query = new StringBuilder(brand + " " + buffArray2[0].trim());
//
//                query = new StringBuilder(query.toString().toLowerCase());
//                productList = getCatalogProducts(query.toString().toLowerCase(), brand);
//                if (productList.size() != 0){
//                    //проходимся по всему списку и находим продукт с наименьшей ценой
//                    product = getProductWithLowerPrice(productList, myVendorCodes);
//                }
//                break;
//        }

        assert product != null;
//        //устанавливаем имя продовца
//        String sellerName = getSellerName(product.getCompetitorVendorCode(), webClient);
//        product.setCompetitorName(sellerName);
//        product.setCompetitorName("-");
//
//        //устанавливаем мою спецакцию, если она есть
//        product.setMySpecAction(getMySpecAction(page));
//
//        //устанавливаем ссылку на картинку моего товара
//        product.setMyRefForImage(getMyProductsPhoto(page));
//
//        //устанавливаем поисковый запрос аналогов
//        product.setQueryForSearch(query.toString());
//
//        //устанавливаем наименование моего товара
//        product.setMyProductName(getMyProductsTitle(page));
//
//        //устанавливаем ссылку на артикул моего товара
//        product.setMyRefForPage(getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP"));
//
//        //устанавливаем мой vendorCode
//        product.setMyVendorCodeFromRequest(myVendorCodeFromRequest);

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

        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        try {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            page = webClient.getPage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        webClient.close();

        assert page != null;
        String pageString = page.asXml();

        //получаем кол-во найденных аналогов
        List<HtmlElement> itemsCountSearch = page.getByXPath("//div[@class='b6r7']");
        if (itemsCountSearch.isEmpty()) {
            System.out.println("No items found !");
        } else {
            querySearchAndCount = itemsCountSearch.get(0).asText();
        }

        //получаем список продуктов, полученный по поисковому запросу аналогов
        List<HtmlElement> itemsForListProducts = page.getByXPath("//div[@class='a0c4']");

        if (itemsForListProducts.isEmpty()) {
            System.out.println("No items found !");
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

                //получение ссылки на продукт
                refForProduct = "https://www.ozon.ru" + itemProduct.getFirstChild().getAttributes().getNamedItem("href").getNodeValue();
                String[] arrayBuff1 = refForProduct.split("/");
                for (int i = 0; i < arrayBuff1.length; i++) {
                    if (arrayBuff1[i].equals("id")){
                        vendorCode = arrayBuff1[i + 1];
                        break;
                    }
                }
                //элементы, в которых вся нужная информация
                Iterable<DomElement> elementsFor_a0c4 = itemProduct.getChildElements();
                int childFor_a0c4 = 1;
                for (DomElement elementFor_a0c4 : elementsFor_a0c4) {
                    //получение ссылки на картинку
                    if (childFor_a0c4 == 1) {
                        refImage = elementFor_a0c4.getFirstChild().getFirstChild().getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
                    }
                    //получение
                    if (childFor_a0c4 == 2) {

                        DomNodeList<HtmlElement> asFor_a0s9 = elementFor_a0c4.getElementsByTagName("a");

                        //получение цен: currentBasicPriceString, competitorPriceU
                        DomNodeList<HtmlElement> divsFor_a0y9 = asFor_a0s9.get(0).getElementsByTagName("div");
                        System.out.println(divsFor_a0y9.size());
                        DomNodeList<HtmlElement> spanFor_b5v4 = divsFor_a0y9.get(0).getElementsByTagName("span");
                        System.out.println(spanFor_b5v4.size());
                        String currentBasicPriceString = spanFor_b5v4.get(0).asText();
                        competitorBasicPriceU = getPriceFromStringPrice(currentBasicPriceString);
                        String currentPriceUString = spanFor_b5v4.get(1).asText();
                        competitorPriceU = getPriceFromStringPrice(currentPriceUString);

                        //получение цены premiumPriceString
                        try {
                            //пробуем получить премиум цену, если есть
                            String premiumPriceString = divsFor_a0y9.get(2).asText();
                            competitorPremiumPriceForOzon = getPriceFromStringPrice(premiumPriceString);
                        } catch (Exception ignored) {
                        }

                        //получение описания продукта
                        productDescription = asFor_a0s9.get(1).asText();
                        //определяем какой бренд
                        competitorBrand = "-";
                        for (String s: Constants.listForBrands){
                            if (productDescription.contains(s)){
                                competitorBrand = s;
                                break;
                            }
                        }

                        //получение имени продавца
                        DomNodeList<HtmlElement> divsFor_a0s9 = elementFor_a0c4.getElementsByTagName("div");
                        DomNodeList<HtmlElement> spansFor_a0t6 = divsFor_a0s9.get(1).getElementsByTagName("span");
                        seller = spansFor_a0t6.get(0).asText();

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

    private static int getPriceFromStringPrice(String price) {
        String resultPrice = "";

        Pattern p = Pattern.compile("-?\\d+");

        Matcher m = p.matcher(price);
        while (m.find()) {
            System.out.println(m.group());
            resultPrice = resultPrice + m.group();
        }
        return Integer.parseInt(resultPrice);
    }













//    private Document getDocumentPageForVendorCode(String myVendorCodeFromRequest) {
//        String url = getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP");
//        Document page = null;
//        try {
//            page = Jsoup.connect(url)
//                    .userAgent("Mozilla")
//                    .timeout(20000)
//                    .referrer("https://google.com")
//                    .get();
//        } catch (IOException e) {
//            System.out.println(Constants.NOT_FOUND_PAGE);
//        }
//        return page;
//    }
//
    private static Product getProductWithLowerPrice(List<Product> productList, Set myVendorCodes, String myVendorCodeFromRequest) {
        if (productList.size() == 1){
            return productList.get(0);
        } else {
            Product product = null;
            productList.sort(comparing(Product::getCompetitorLowerPriceU));
            for (Product p : productList) {
                if (!myVendorCodes.contains(p.getCompetitorVendorCode())) {
                    product = p;
                    break;
                }
            }
            if (product == null){
                product = productList.get(0);
            }
            return product;
        }
    }
//
//    private String getSellerName(String vendorCode, WebClient webClient){
////        Document page = getDocumentPageForVendorCode(vendorCode);
//        String url = getString("https://www.wildberries.ru/catalog/", vendorCode, "/detail.aspx?targetUrl=SP");
//        HtmlPage page = null;
//        HtmlDivision spanSellerName;
//        synchronized (mon) {
//            try {
//                webClient.getOptions().setCssEnabled(false);
//                webClient.getOptions().setJavaScriptEnabled(true);
//
//                page = webClient.getPage(url);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            //Element elementSellerName = page.select("span[class=seller__text]").first();
//            webClient.waitForBackgroundJavaScript(7000);
//            String pageString = page.asXml();
//            spanSellerName = (HtmlDivision) page.getByXPath("//div[@class='seller']").get(0);
//        }
//        return spanSellerName.asText();
//    }
//
//    //метод читающий на странице продукта характеристики, по которым будет осуществляться запрос на поиск аналогов!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//    private static List<String> getDataForRequestFromCategory(Document page, String category, String brand){
//        List<String> paramsForRequest = new ArrayList<>();
//        String title = getMyProductsTitle(page);
//        String description = getProductDescription(page);
//
//        //пытаемся получить название модели из характеристик
//        String modelName = getModelName(page);
//        if (modelName.equals("-")) {
//            //пытаемся получить название модели из title
//            modelName = getProductModelFromTitle(title, brand);
//            if (modelName.equals("-")){
//                //пытаемся получить название модели из описания
//                modelName = getProductModelFromDescription(description, brand);
//            }
//        }
//        //если название модели не найдено, то возвращаем всю title
//        if (modelName.equals("-")){
//            paramsForRequest.add(title);
//            return paramsForRequest;
//        }
//
//        //--- 1 --- первым параметром бедет название модели либо кол-во элементов в упаковке
//        paramsForRequest.add(modelName);
//
//        //определяем дополнительные параметры запроса в зависимости от категории товара
//        switch (category) {
//            case Constants.CATEGORY_WILD_1:
//            case Constants.CATEGORY_WILD_6:
//                getParamsFromTitleForCharging(paramsForRequest, title);
//                break;
//
//            case Constants.CATEGORY_WILD_8:
//                String myType = title.toLowerCase();
//                try {
//                    String[] arrayTitle1 = title.split("/");
//                    brand = brand.toLowerCase();
//                    if (arrayTitle1[1].toLowerCase().contains(brand)){
//                        String[] types = arrayTitle1[1].toLowerCase().split(brand);
//                        String[] arrayTitle2 = arrayTitle1[1].split(",", 2);
//                        String[] arrayTitle3 = arrayTitle2[0].toLowerCase().split(brand);
//
//                        paramsForRequest.set(0, arrayTitle3[1].trim());
//                    }
//                } catch (Exception e) {
//                }
//                for (String type : Constants.listForCabel) {
//                    if (myType.replaceAll(",", "").contains(type)) {
//                        paramsForRequest.add(type);
//                    }
//                }
//                break;
//
//            case Constants.CATEGORY_WILD_3:
//            case Constants.CATEGORY_WILD_12:
//                String typeConnect = getTypeConnectForHeadset(page);
//                if (typeConnect == ""){
//                    for (String type : Constants.listForHeadset) {
//                        if (title.toLowerCase().contains(type)) {
//                            paramsForRequest.add(type);
//                        }
//                    }
//                } else {
//                    paramsForRequest.add(typeConnect);
//                }
//                break;
//
//            case Constants.CATEGORY_WILD_7:
//                String[] arrayForTitle = title.split(",", 2);
//                String[] arrayForParams = arrayForTitle[1].replace(",", "").split(" ");
//                for (String s: arrayForParams){
//                    for (String type : Constants.listForTypeGlass) {
//                        if (s.equalsIgnoreCase(type)) {
//                            paramsForRequest.add(type);
//                        }
//                    }
//                }
//
//                break;
//
//            case Constants.CATEGORY_WILD_16:
//                try {
//                    String[] strBuf1 = title.split(", ");
//                    String[] strBuf2 = strBuf1[0].split(" ");
//                    paramsForRequest.add(strBuf2[strBuf2.length - 1]);
//                    return paramsForRequest;
//                } catch (Exception e) {
//                }
//                break;
//
//            case Constants.CATEGORY_WILD_40:
//            case Constants.CATEGORY_WILD_34:
//                String[] arrayStr = title.split(",", 2);
//                String[] arrayStr2 = arrayStr[0].split("/");
//                brand = brand.toLowerCase();
//                if (category.equals(Constants.CATEGORY_WILD_40)){
//                    paramsForRequest.set(0, arrayStr2[1]);
//                } else {
//                    String str = arrayStr2[1].toLowerCase().replace(brand, "");
//                    paramsForRequest.set(0, str);
//                }
//
//                for (String s: Constants.listForBugs){
//                    if (title.contains(s)){
//                        paramsForRequest.add(s);
//                    }
//                }
//                break;
//        }
//        return paramsForRequest;
//    }
//
//    private static String getTypeConnectForHeadset(Document page) {
//        Elements elementsParams = page.select("div[class=pp]");
//        String typeConnect = "";
//        for (Element elementParam : elementsParams) {
//            if (elementParam.text().contains("Разъем подключения наушников")){
//                for (String type : Constants.listForHeadset) {
//                    if (elementParam.text().contains(type)){
//                        typeConnect = type;
//                    }
//                }
//            }
//        }
//        return typeConnect;
//    }
//
//    private static String getProductModelFromDescription(String description, String brand) {
//        brand = brand.toLowerCase();
//        String model = "модель:";
//        description = description.toLowerCase();
//        if (description.contains(brand)){
//            String[] arrayDescription1 = description.split(brand);
//            String[] arrayDescription2 = arrayDescription1[1].trim().split(" ");
//            return arrayDescription2[0];
//        } else if (description.contains(model)){
//            String[] arrayDescription1 = description.split(model);
//            String[] arrayDescription2 = arrayDescription1[1].trim().split(" ");
//            return arrayDescription2[0];
//        }
//        return "-";
//    }
//
//    private static String getProductDescription(Document page) {
//        Element elementDescription = page.select(Constants.ELEMENT_WITH_DESCRIPTION_MY_PRODUCT).first();
//        if (elementDescription != null){
//            return elementDescription.text();
//        } else {
//            return Constants.NOT_FOUND_HTML_ITEM;
//        }
//    }
//
//    private static void getParamsFromTitleForCharging(List<String> paramsForRequest, String title) {
//        if (title.contains("кабелем") || title.contains("кабель") || title.contains("держателем") || title.contains("держатель")){
//            String[] arrayForTitle = title.split(",", 2);
//            String[] arrayForParams = arrayForTitle[1].replace(",", "").split(" ");
//            for (String s: arrayForParams){
//                for (String param : Constants.listForСharging) {
//                    if (s.equalsIgnoreCase(param)) {
//                        paramsForRequest.add(param);
//                    }
//                }
//            }
//        }
//    }
//
//    //ищем модель в характеристиках
//    private static String getModelName(Document page) {
//        Element params = page.select(Constants.ELEMENT_WITH_PARAMS_MY_PRODUCT).first();
//        if (params != null){
//            Elements elements = params.getAllElements();
//            String param4 = elements.get(4).text();
//            if (param4.equals(Constants.PARAM_1_1)) {
//                String countItemsFromPackage = elements.get(5).text();
//                String[] array = countItemsFromPackage.split(" ");
//                //возвращаем кол-во элементов в упаковке
//                return array[0];
//            } else if (param4.equals(Constants.PARAM_1_2)) {
//                //возвращаем название модели
//                return elements.get(5).text();
//            }
//        } else {
//            return "-";
//        }
//        return "-";
//    }
//
//    private static String getMyProductsPhoto(Document page) {
//        //элемент переписан 16.02.21
//        //Element elementImage1 = page.select("img[class=j-zoom-photo preview-photo]").first();
//        Element elementImage2 = page.select(Constants.ELEMENT_WITH_PHOTO_MY_PRODUCT).first();
//        if (elementImage2 != null){
//            return "https:" + elementImage2.attr(Constants.ATTRIBUTE_WITH_REF_FOR_IMAGE_1);
//        } else return Constants.NOT_FOUND_HTML_ITEM;
//    }
//
//    //по наличию этого параметра определяем есть ли акция
//    private static String getMySpecAction(Document page) {
//        Element specAction = page.select(Constants.ELEMENT_WITH_SPEC_ACTION_MY_PRODUCT).first();
//        if (specAction != null){
//            return specAction.text();
//        } else {
//            return Constants.NOT_FOUND_HTML_ITEM;
//        }
//    }
//
//    //для формирования запроса на основании названия модели товара, которое находится в заголовке как аправило сразу после бренда
//    private static String getMyProductsTitle(Document page) {
//        Element elementBrandAndNameTitle = page.select(Constants.ELEMENT_WITH_TITLE_MY_PRODUCT).first();
//        if (elementBrandAndNameTitle != null){
//            return elementBrandAndNameTitle.text();
//        } else {
//            return Constants.NOT_FOUND_HTML_ITEM;
//        }
//    }
//
//    private static String getProductModelFromTitle(String title, String brand) {
//
//        String[] strBuf1 = title.split("/", 2);
//        brand = brand.toLowerCase().trim();
//
//        String[] strBuf2 = strBuf1[1].trim().split(",");
//        String model = "";
//        for (String s : strBuf2) {
//            if ((s.toLowerCase().contains(brand)) || s.toLowerCase().contains(brand.substring(0, 2))) {
//
//                String[] strBuf3 = s.trim().split(" ");
//
//                for (int z = 0; z < strBuf3.length; z++) {
//                    if (strBuf3[z].equalsIgnoreCase(brand)) {
//                        for (int j = z + 1; j < strBuf3.length; j++) {
//                            model = model + strBuf3[j] + " ";
//                        }
//                        break;
//                    } else if (strBuf3[z].toLowerCase().startsWith(brand.substring(0, 2))) {
//                        for (int j = z; j < strBuf3.length; j++) {
//                            model = model + strBuf3[j] + " ";
//                        }
//                        break;
//                    }
//                }
//            }
//        }
//        if (model.equals("")){
//            for (int i = 0; i < strBuf2.length; i++){
//                if (strBuf2[i].toLowerCase().contains(brand)){
//                    model = strBuf2[i + 1].trim();
//                }
//            }
//        }
//        if (model.equals("")){
//            char[] strBrandToArray = brand.toCharArray();
//            for (String s2: strBuf2){
//                String[] strBuf3 = s2.toLowerCase().trim().split(" ");
//                for (String s3: strBuf3){
//                    if (s3.startsWith(String.valueOf(strBrandToArray[0])) && s3.contains("-")){
//                        model = s3;
//                    }
//                }
//            }
//        }
//        if (model.equals("")){
//            String[] arrayTitle = title.replace(",", "").split(" ");
//            Character character1 = brand.toUpperCase().charAt(0);
//            for (String str: arrayTitle){
//                if (str.length() == 3){
//                    Character character2 = str.toUpperCase().charAt(0);
//                    if (character1.equals(character2)){
//                        model = str;
//                    }
//                }
//            }
//        }
//        if (model.equals("")){
//            model = "-";
//        }
//        return model.replaceAll("()", "").trim();
//    }
//    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//    private static List<Product> getCatalogProducts(String query, String brand) {
//        List<Product> productList;
//        Document page;
//        page = getPageForSearchQuery(query);
//
//        //получение бренда, артикула, имени товара, ссылки на страницу товара, ссылки на картинкау товара, спец-акции, рейтинга
//        productList = getCatalogProductsForRequestPage(page, brand);
//        if (productList.size() == 0){
//            return productList;
//        }
//        //получение цены и скидок через json
//        HttpUrlConnectionHandler.getCatalog(productList, query);
//
//        return productList;
//    }
//
//    private static Document getPageForSearchQuery(String query) {
//        //String url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&xsearch=true&sort=priceup"); запрос изменился 05.03.21
//        String url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&&sort=priceup");
//
//        Document page = null;
//        try {
//            page = Jsoup.parse(new URL(url), 30000);
//        } catch (IOException e) {
//            return page;
//        }
//        return page;
//    }
//
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
//
//    private static List<Product> getCatalogProductsForRequestPage(Document page, String myBrand){
//        List<Product> productList = new ArrayList<>();
//        if (page != null){
//            Element catalog = page.select(Constants.ELEMENT_WITH_CATALOG).first();
//            if (catalog == null){
//                return productList;
//            }
//            Elements goods = catalog.select(Constants.ELEMENT_WITH_PRODUCT);
//            int countSearch = goods.size();
//            for (Element good : goods) {
//                //артикул
//                String vendorCode = good.attr(Constants.ATTRIBUTE_WITH_VENDOR_CODE);
//
//                Element fullProductCard = good.select(Constants.ELEMENT_WITH_CARD_PRODUCT).first();
//
//                //имя товара
//                Element nameGoods = fullProductCard.select(Constants.ELEMENT_WITH_NAME_PRODUCT).first();
//                String productName = nameGoods.text();
//
//                //ссылка на товар
//                String refForPage = Constants.MARKETPLACE + fullProductCard.attr(Constants.ATTRIBUTE_WITH_REF_FOR_PAGE_PRODUCT);
//
//                //ссылка на картинку товара
//                Element img = fullProductCard.select(Constants.ELEMENT_WITH_REF_FOR_IMAGE).first();
//                String refForImg = "-";
//                String refForImgTemp1 = img.attr(Constants.ATTRIBUTE_WITH_REF_FOR_IMAGE_1);
//                String refForImgTemp2 = img.attr(Constants.ATTRIBUTE_WITH_REF_FOR_IMAGE_2);
//                if (refForImgTemp2.equals("")){
//                    refForImg = "https:" + refForImgTemp1;
//                } else {
//                    refForImg = "https:" + refForImgTemp2;
//                }
//
//                //спец-акция
//                Element priceGoods = fullProductCard.select(Constants.ELEMENT_WITH_SPEC_ACTION).first();
//                String specAction = "-";
//                if (priceGoods != null){
//                    specAction = priceGoods.text();
//                }
//
//                //рейтинг
//                Element star = fullProductCard.getElementsByAttributeValueStarting("class", "c-stars").first();
//                int rating = 0;
//                if (star != null){
//                    String nameClass = star.className();
//                    rating = Integer.parseInt(String.valueOf(nameClass.charAt(nameClass.length() - 1)));
//                }
//
//                //Brand
//                Element brand = fullProductCard.select(Constants.ELEMENT_WITH_BRAND_NAME).first();
//                String string = brand.text();
//                String brandName = string.substring(0, string.length() - 2).toLowerCase();
//                if (!myBrand.equals("Aiqura")){
//                    if (!brandName.contains(myBrand.toLowerCase())) continue;
//                }
//
//                productList.add(new Product("-",
//                        "-",
//                        "-",
//                        "-",
//                        "-",
//
//                        "-",
//                        countSearch,
//
//                        brandName,
//                        vendorCode,
//                        productName,
//                        refForPage,
//                        refForImg,
//                        specAction,
//                        rating,
//
//                        0,
//                        0,
//                        0,
//                        0,
//                        0,
//
//                        "-"));
//            }
//        }
//        return productList;
//    }
}
