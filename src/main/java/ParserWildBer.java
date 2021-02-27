import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class ParserWildBer {

    private static final String CATEGORY_1 = "Автомобильные зарядные устройства";//
    private static final String CATEGORY_2 = "Внешние аккумуляторы";//
    private static final String CATEGORY_3 = "Гарнитуры";//
    private static final String CATEGORY_4 = "Гироскутеры";
    private static final String CATEGORY_5 = "Держатели в авто";//
    private static final String CATEGORY_6 = "Зарядные устройства";//
    private static final String CATEGORY_7 = "Защитные стекла";//
    private static final String CATEGORY_8 = "Кабели";//
    private static final String CATEGORY_9 = "Колонки";//
    private static final String CATEGORY_10 = "Маски одноразовые";
    private static final String CATEGORY_11 = "Моноподы";//
    private static final String CATEGORY_12 = "Наушники";//
    private static final String CATEGORY_13 = "Пылесосы автомобильные";//
    private static final String CATEGORY_14 = "Увлажнители";//
    private static final String CATEGORY_15 = "Переходники";//
    private static final String CATEGORY_16 = "Термометры медицинские";
    private static final String CATEGORY_17 = "Адаптеры";//
    private static final String CATEGORY_18 = "Подставки для мобильных устройств";//
    private static final String CATEGORY_19 = "Чехлы для телефонов";
    private static final String CATEGORY_20 = "Дезинфицирующие средства";
    private static final String CATEGORY_21 = "Защитные кейсы";
    private static final String CATEGORY_22 = "Лампы автомобильные";
    private static final String CATEGORY_23 = "Массажеры электрические";
    private static final String CATEGORY_24 = "Мусорные ведра";
    private static final String CATEGORY_25 = "Мухобойки механические";
    private static final String CATEGORY_26 = "Мыльницы";
    private static final String CATEGORY_27 = "Подсветка для ноутбука";
    private static final String CATEGORY_28 = "Подстаканники электрические";
    private static final String CATEGORY_29 = "Подушки автомобильные";
    private static final String CATEGORY_30 = "Пульты дистанционной съемки";
    private static final String CATEGORY_31 = "Ремешки для умных часов";
    private static final String CATEGORY_32 = "Салфетки для авто";
    private static final String CATEGORY_33 = "Светильники";
    private static final String CATEGORY_34 = "Сумки для ноутбуков";
    private static final String CATEGORY_35 = "Таблички для авто";
    private static final String CATEGORY_36 = "Фильтры воздушные";
    private static final String CATEGORY_37 = "Автомобильные ароматизаторы";
    private static final String CATEGORY_38 = "FM-трансмиттеры";


    public Product getProduct (String myVendorCodeFromRequest, String category, String brand){
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
                "-",
                "-",
                "-",
                "-",
                0,

                0,
                0,
                0,
                0,
                0,

                "-");

        //получение html-страницы для моего артикула
        Document page = getDocumentPageForVendorCode(myVendorCodeFromRequest);
        //если ничего не вернулось(страница не существует) то возвращаем нулевой продукт
        if (page == null){
            return product;
        }
        //обработка html-страницы для формирования поискового запроса аналогов
        List<String> paramsForRequest = getDataForRequestFromCategory(page, category);
        String query = null;
        if (paramsForRequest.size() == 0){
            product.setQueryForSearch("Мало данных для формирования поискового запроса");
            return product;
        }

        //в заввисимости от категории определяем параметры запроса для поиска конкурентов
        switch (category){
            case CATEGORY_10:
                String count = paramsForRequest.get(0);
                //для поиска по маскам надо в запросе передать "Маски одноразовые" и кол-во штук в упаковке
                query = category + " " + count;
                productList = getCatalogProducts(query.toLowerCase(), brand);
                product = productList.stream().filter(p -> p.getCompetitorProductName().contains(count)).findAny().orElse(null);
                break;

            case CATEGORY_4:
                query = paramsForRequest.get(0);
                productList = getCatalogProducts(query.toLowerCase(), brand);
                product = productList.stream().findFirst().orElse(null);
                break;

            case CATEGORY_7:
                try {
                    query = brand + " " + paramsForRequest.get(0) + " " + paramsForRequest.get(1);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Для артикула: " + myVendorCodeFromRequest + " - ошибка формирования запроса на поиск конкурентов");
                }
                productList = getCatalogProducts(query.toLowerCase(), brand);

                //проходимся по всему списку и находим продукт с наименьшей ценой
                product = getProductWithLowerPrice(productList, myVendorCodeFromRequest);
                break;

                //для данных категорий запрос формирунтся из бренда и модели
            case CATEGORY_1:
            case CATEGORY_2:
            case CATEGORY_3:
            case CATEGORY_5:
            case CATEGORY_6:
            case CATEGORY_8:
            case CATEGORY_9:
            case CATEGORY_11:
            case CATEGORY_12:
            case CATEGORY_13:
            case CATEGORY_14:
            case CATEGORY_15:
            case CATEGORY_16:
            case CATEGORY_17:
            case CATEGORY_18:
            case CATEGORY_20:
            case CATEGORY_21:
            case CATEGORY_22:
            case CATEGORY_23:
            case CATEGORY_24:
            case CATEGORY_25:
            case CATEGORY_26:
            case CATEGORY_27:
            case CATEGORY_28:
            case CATEGORY_29:
            case CATEGORY_30:
            case CATEGORY_31:
            case CATEGORY_32:
            case CATEGORY_33:
            case CATEGORY_34:
            case CATEGORY_35:
            case CATEGORY_36:
            case CATEGORY_37:
            case CATEGORY_38:

                query = brand + " " + category;
                for (int i = 0; i < paramsForRequest.size(); i++) {
                    query = query + " " + paramsForRequest.get(i);
                }
                query = query.toLowerCase();
                productList = getCatalogProducts(query, brand);
                //проходимся по всему списку и находим продукт с наименьшей ценой
                product = getProductWithLowerPrice(productList, myVendorCodeFromRequest);

                break;
        }

        assert product != null;
        //устанавливаем имя продовца
//        String sellerName = getSellerName(product.getCompetitorVendorCode());
//        product.setCompetitorName(sellerName);

        //устанавливаем мою спецакцию, если она есть
        product.setMySpecAction(getMySpecAction(page));

        //устанавливаем ссылку на картинку моего товара
        product.setMyRefForImage(getMyProductsPhoto(page));

        //устанавливаем поисковый запрос аналогов
        product.setQueryForSearch(query);

        //устанавливаем наименование моего товара
        product.setMyProductName(getMyProductsTitle(page));

        //устанавливаем ссылку на артикул моего товара
        product.setMyRefForPage(getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP"));

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

    private static Product getProductWithLowerPrice(List<Product> productList, String myVendorCodeFromRequest) {
        if (productList.size() == 1){
            return productList.get(0);
        } else {
            Product product = null;
            productList.sort(comparing(Product::getLowerPriceU));
            for (Product p : productList) {
                if (!p.getCompetitorVendorCode().equals(myVendorCodeFromRequest)) {
                    product = p;
                }
            }
            return product;
        }
    }

    private String getSellerName(String vendorCode){
        Document page = getDocumentPageForVendorCode(vendorCode);
        Element elementSellerName = page.select("span[class=seller__text]").first();
        return elementSellerName.text();
    }

    //метод читающий на странице продукта характеристики, по которым будет осуществляться запрос на поиск аналогов!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static List<String> getDataForRequestFromCategory(Document page, String category){
        List<String> paramsForRequest = new ArrayList<>();
        String title = getMyProductsTitle(page);

        //пытаемся получить название модели из характеристик
        String modelName = getModelName(page);
        if (modelName == null) {
            //пытаемся получить название модели из title
            modelName = getProductModelFromTitle(title);
            //--- 1 --- первым параметром бедет название модели либо кол-во элементов в упаковке
            paramsForRequest.add(modelName);
        }

        //определяем дополнительные параметры запроса в зависимости от категории товара
        switch (category) {
            case CATEGORY_1:
            case CATEGORY_6:
                for (String param : Constants.listForCabel) {
                    if (title.contains(param)) {
                        paramsForRequest.add(param);
                    }
                }
                break;
            case CATEGORY_3:
            case CATEGORY_8:
            case CATEGORY_12:
                for (String type : Constants.listForTypeConnect) {
                    if (title.contains(type)) {
                        paramsForRequest.add(type);
                    }
                }
                break;
            case CATEGORY_7:
                for (String type: Constants.listForTypeGlass){
                    if(title.equals(type)){
                        paramsForRequest.add(type);
                        break;
                    }
                }
                break;
            case CATEGORY_16:
                try {
                    String[] strBuf1 = title.split(", ");
                    String[] strBuf2 = strBuf1[0].split(" ");
                    paramsForRequest.add(strBuf2[strBuf2.length - 1]);
                    return paramsForRequest;
                } catch (Exception e) {
                }
                break;
        }
        return paramsForRequest;
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
            return null;
        }
        return null;
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

    private static String getProductModelFromTitle(String title) {

        String[] strBuf1 = title.split("/");
        String brand = strBuf1[0].toLowerCase().trim();

        String[] strBuf2 = strBuf1[1].trim().split(",");
        String model = "";
        for (int i = 0; i < strBuf2.length; i++){
            if ((strBuf2[i].toLowerCase().contains(brand)) || strBuf2[i].toLowerCase().contains(brand.substring(0, 2))){

                String[] strBuf3 = strBuf2[i].trim().split(" ");

                for (int z = 0; z < strBuf3.length; z++) {
                    if (strBuf3[z].equalsIgnoreCase(brand)){
                        for (int j = z + 1; j < strBuf3.length; j++){
                            model = model + strBuf3[j] + " ";
                        }
                        break;
                    } else if(strBuf3[z].toLowerCase().startsWith(brand.substring(0, 2))){
                        for (int j = z; j < strBuf3.length; j++){
                            model = model + strBuf3[j] + " ";
                        }
                        break;
                    }
                }
            }
        }
        if (model.equals("")){
            if (strBuf2[0].toLowerCase().contains(brand)){
                model = strBuf2[1].trim();
            }
        }
        return model.replaceAll("()", "").trim();
    }
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private static List<Product> getCatalogProducts(String query, String brand) {
        List<Product> productList;
        Document page = null;
        page = getPageForSearchQuery(query);

        //получение бренда, артикула, имени товара, ссылки на страницу товара, ссылки на картинкау товара, спец-акции, рейтинга
        productList = getCatalogProductsForRequestPage(page, brand);

        //получение цены и скидок через json
        HttpUrlConnectionHandler.getCatalog(productList, query);

        return productList;
    }

    private static Document getPageForSearchQuery(String query) {
        String url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&xsearch=true&sort=priceup");

        Document page = null;
        try {
            page = Jsoup.parse(new URL(url), 30000);
        } catch (IOException e) {
            return page;
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
        Product product = null;
        if (page != null){
            Element catalog = page.select(Constants.ELEMENT_WITH_CATALOG).first();
            Elements goods = catalog.select(Constants.ELEMENT_WITH_PRODUCT);
            int countSearch = goods.size();
            for (Element good : goods) {
                //артикул
                String vendorCode = good.attr(Constants.ATTRIBUTE_WITH_VENDOR_CODE);

                Element fullProductCard = good.select(Constants.ELEMENT_WITH_CARD_PRODUCT).first();

                //имя товара
                Element nameGoods = fullProductCard.select(Constants.ELEMENT_WITH_NAME_PRODUCT).first();
                String productName = nameGoods.text();

                //ссылка на товар
                String refForPage = Constants.MARKETPLACE + fullProductCard.attr(Constants.ATTRIBUTE_WITH_REF_FOR_PAGE_PRODUCT);

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
                if (!brandName.contains(myBrand.toLowerCase())) continue;

                productList.add(new Product("-",
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

                        "-"));
            }
        }
        return productList;
    }
}
