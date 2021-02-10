import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ParserWildBer {

    private static final String CATEGORY_1 = "Автомобильные зарядные устройства";
    private static final String CATEGORY_2 = "Внешние аккумуляторы";
    private static final String CATEGORY_3 = "Гарнитуры";
    private static final String CATEGORY_4 = "Гироскутеры";
    private static final String CATEGORY_5 = "Держатели в авто";
    private static final String CATEGORY_6 = "Зарядные устройства";
    private static final String CATEGORY_7 = "Защитные стекла";
    private static final String CATEGORY_8 = "Кабели";
    private static final String CATEGORY_9 = "Колонки";
    private static final String CATEGORY_10 = "Маски одноразовые";
    private static final String CATEGORY_11 = "Моноподы";
    private static final String CATEGORY_12 = "Наушники";
    private static final String CATEGORY_13 = "Пылесосы автомобильные";
    private static final String CATEGORY_14 = "Увлажнители";
    private static final String CATEGORY_15 = "Переходники";
    private static final String CATEGORY_16 = "Термометры медицинские";
    private static final String CATEGORY_17 = "Адаптеры";
    private static final String CATEGORY_18 = "Подставки для мобильных устройств";

    private static final String PARAM_1_1 = "Количество предметов в упаковке";
    private static final String PARAM_1_2 = "Модель";
    private static final String PARAM_1_3 = "Гарантийный срок";

    public Product getProduct (String vendorCodeFromRequest, String category, String brand){
        List<Product> productList = null;
        Product product = new Product(vendorCodeFromRequest, "-", "-","-", "-", "-", "-", "-", 0, 0, 0, 0, 0, "-", 0, "-");
        List<String> paramsForRequest = null;

        String url = getString("https://www.wildberries.ru/catalog/", vendorCodeFromRequest, "/detail.aspx?targetUrl=SP");
        Document page = null;
        try {
            page = Jsoup.parse(new URL(url), 30000);
        } catch (IOException e) {
            System.out.println(Constants.NOT_FOUND_PAGE);
        }

        if (page == null){
            return product;
        }

        String query = null;


        switch (category){
            case CATEGORY_10:
                paramsForRequest = getDataForRequestFromCategory(page, category);
                String count = paramsForRequest.get(1);
                //для поиска по маскам надо в запросе передать "Маски одноразовые" и кол-во штук в упаковке
                query = category + " " + count;
                productList = getCatalogProducts(query, vendorCodeFromRequest, brand);
                product = productList.stream().filter(p -> p.getProductName().contains(count)).findAny().orElse(null);
                break;

            case CATEGORY_4:
                paramsForRequest = getDataForRequestFromCategory(page, category);
                query = paramsForRequest.get(1);
                productList = getCatalogProducts(query, vendorCodeFromRequest, brand);
                product = productList.stream().findFirst().orElse(null);
                break;

            case CATEGORY_7:
                paramsForRequest = getDataForRequestFromCategory(page, category);
                try {
                    query = paramsForRequest.get(1) + " " + paramsForRequest.get(2);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Для артикула: " + vendorCodeFromRequest + " - ошибка формирования запроса на поиск конкурентов");
                }
                productList = getCatalogProducts(query, vendorCodeFromRequest, brand);

                //проходимся по всему списку и находим продукт с наименьшей ценой
                product = getProductWithLowerPrice(productList);
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

                paramsForRequest = getDataForRequestFromCategory(page, category);
                try {
                    if (paramsForRequest.get(1).equals("")){
                        product.setRefFromRequest("Мало данных для формирования поискового запроса");
                        break;
                    }
                    query = brand + " " + paramsForRequest.get(1);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    System.out.println("Для артикула: " + vendorCodeFromRequest + " ошибка формирования запроса на поиск конкурентов");
                }
                productList = getCatalogProducts(query, vendorCodeFromRequest, brand);
                //проходимся по всему списку и находим продукт с наименьшей ценой
                product = getProductWithLowerPrice(productList);
                break;
        }
        //устанавливаем спецакцию, если она есть
        product.setMySpecAction(paramsForRequest.get(0));

        if (!paramsForRequest.get(1).equals("")){
            product.setRefFromRequest(getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&xsearch=true&sort=priceup"));
        }
        product.setMyRefForPage(getString("https://www.wildberries.ru/catalog/", vendorCodeFromRequest, "/detail.aspx?targetUrl=SP"));
        return product;
    }

    private static Product getProductWithLowerPrice(List<Product> productList) {
        Product product = productList.get(0);

        for (Product p : productList) {
            if (p.getLowerPriceU() <= product.getLowerPriceU()) {
                product = p;
            }
        }
        return product;
    }

    private static Document getPageForVendorCode(String vendorCode){
        String url = getString("https://www.wildberries.ru/catalog/", vendorCode, "/detail.aspx?targetUrl=SP");
        Document page = null;
        try {
            page = Jsoup.parse(new URL(url), 30000);
        } catch (IOException e) {
            System.out.println(Constants.NOT_FOUND_PAGE);
        }
        return page;
    }

    //метод читающий на странице продукта характеристики, по которым будет осуществляться запрос на поиск аналогов!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static List<String> getDataForRequestFromCategory(Document page, String category){
        List<String> paramsForRequest = new ArrayList<>();

        //формирование запроса на основании названия модели товара, которое находится в заголовке как аправило сразу после бренда
        Element elementBrandAndNameTitle = page.select("div[class=brand-and-name j-product-title]").first();

        //по наличию этого параметра определяем есть ли акция
        Element specAction = page.select("div[class=j-big-sale-icon-card-wrapper i-spec-action-v1]").first();
        try {
            String mySpecAction = specAction.text();
            paramsForRequest.add(mySpecAction);
        } catch (Exception e) {
            paramsForRequest.add("-");
        }

        switch (category){
            case CATEGORY_4:
                try {
                    String tittle = elementBrandAndNameTitle.text();
                    paramsForRequest.add(tittle);
                    return paramsForRequest;
                } catch (Exception e) {
                }

            case CATEGORY_7:
                try {
                    String tittle = elementBrandAndNameTitle.text();
                    String[] strBuf = tittle.split(", ");
                    paramsForRequest.add(strBuf[0]);

                    for (int i = 1; i < strBuf.length; i++) {
                        if (strBuf[i].contains("D") || strBuf[i].contains("EYE PROTECTION")){
                            paramsForRequest.add(strBuf[i]);
                        }
                    }
                    return paramsForRequest;
                } catch (Exception e) {
                }

            case CATEGORY_16:
                try {
                    String tittle = elementBrandAndNameTitle.text();
                    String[] strBuf1 = tittle.split(", ");
                    String[] strBuf2 = strBuf1[0].split(" ");
                    paramsForRequest.add(strBuf2[strBuf2.length - 1]);

                    return paramsForRequest;
                } catch (Exception e) {
                }

            case CATEGORY_8:
                try {
                    //определение параметров запроса
                    String tittle = elementBrandAndNameTitle.text();
                    String[] strBuf1 = tittle.split(", ");
                    String[] strBuf2 = strBuf1[0].split(" / ");
                    paramsForRequest.add(strBuf2[1]);
                    return paramsForRequest;
                } catch (Exception e) {
                }

        }




        Element params = page.select("div[class=params]").first();

        try {
            Elements elements = params.getAllElements();
            String param4 = elements.get(4).text();
            if (param4.equals(PARAM_1_1)){
                String countItemsFromPackage = elements.get(5).text();
                String[] array = countItemsFromPackage.split(" ");
                String count = array[0];
                paramsForRequest.add(count);
            }
            if (param4.equals(PARAM_1_2)){
                String model = elements.get(5).text();
                paramsForRequest.add(model);
            }
            if (param4.equals(PARAM_1_3)){
                try {
                    params = page.select("div[class=brand-and-name j-product-title]").first();
                    String[] strs1 = params.text().split(",");
                    String model = strs1[1].trim();

                    Element description = page.select("div[class=j-description description-text collapsable-content]").first();
                    List<String> strs2 = Arrays.asList(description.text().replaceAll(",", "").split(" "));
                    if (strs2.contains(model)){
                        paramsForRequest.add(model);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }

        if (paramsForRequest.size() == 1){

            String tittle7 = elementBrandAndNameTitle.text();
            String[] strBuf = tittle7.split(", ");

            String[] strBuf2 = strBuf[0].split(" / ");

            String[] strBuf3 = strBuf2[1].split(" ");

            String model = "";

            for (int i = 0; i < strBuf3.length; i++) {
                if (strBuf3[i].equalsIgnoreCase(strBuf2[0]) || strBuf3[i].startsWith(strBuf2[0].substring(0, 2))){
                    for (int j = i + 1; j < strBuf3.length; j++) {
                        model = model + strBuf3[j] + " ";
                    };
                    break;
                }
            }

            if (model.equals("")){
                try {
                    model = strBuf[1];
                } catch (Exception e) {
                }
            }

            paramsForRequest.add(model);
        }

        return paramsForRequest;
    }
   // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private static List<Product> getCatalogProducts(String query, String vendorCodeFromRequest, String brand) {
        List<Product> productList;
        Document page = null;
        page = getPageForSearchQuery(query);
        productList = getCatalogProductsForPage(page, vendorCodeFromRequest, brand);

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

    private static List<Product> getCatalogProductsForPage(Document page, String vendorCodeFromRequest, String myBrand){
        List<Product> productList = new ArrayList<>();
        Product product = null;
        if (page != null){
            Element catalog = page.select(Constants.ELEMENT_WITH_CATALOG).first();
            Elements goods = catalog.select(Constants.ELEMENT_WITH_PRODUCT);
            int i = 0;
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
                try {
                    specAction = priceGoods.text();
                } catch (Exception e) {
                }

                //рейтинг
                Element star = fullProductCard.getElementsByAttributeValueStarting("class", "c-stars").first();
                int rating = 0;
                try {
                    String nameClass = star.className();
                    rating = Integer.parseInt(String.valueOf(nameClass.charAt(nameClass.length() - 1)));
                } catch (Exception e) {
                }

                //Brand
                Element brand = fullProductCard.select("strong[class=brand-name c-text-sm]").first();
                String string = brand.text();
                String brandName = string.substring(0, string.length() - 2);
                if (!brandName.equals(myBrand)) continue;

                productList.add(new Product(vendorCodeFromRequest, "-", "-", brandName, vendorCode, productName, refForPage, refForImg, 0, 0, 0, 0, 0, specAction, rating, "-"));
            }
        }
        return productList;
    }
}
