import com.google.gson.Gson;
import model.Request;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final String PARAM_1_1 = "Количество предметов в упаковке";
    private static final String PARAM_1_2 = "Модель";
    private static final String PARAM_1_3 = "Гарантийный срок";
    private static final String PRICE_AND_SALE = "priceAndSale";

    public static Product getResultProduct (String vendorCode, String category, String brand){
        List<Product> productList = null;
        Product product = null;
        List<String> paramsForRequest;

        Document page = getPageForVendorCode(vendorCode);

        if (page == null){
            return null;
        }

        String query = null;
        switch (category){

            case CATEGORY_10:
                //для поиска по маскам надо в запросе передать "Маски одноразовые" и кол-во штук в упаковке
                paramsForRequest = getDataForRequestFromCategory(page, category);
                String count = paramsForRequest.get(0);
                query = category + " " + count;
                productList = getProduct(query);
                product = productList.stream().filter(p -> p.getProductName().contains(count)).findAny().orElse(null);
                break;

            case CATEGORY_4:
                paramsForRequest = getDataForRequestFromCategory(page, category);
                query = paramsForRequest.get(0);
                productList = getProduct(query);
                product = productList.stream().findFirst().orElse(null);
                break;

            case CATEGORY_7:
                paramsForRequest = getDataForRequestFromCategory(page, category);
                try {
                    query = paramsForRequest.get(0) + " " + paramsForRequest.get(1);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Для артикула: " + vendorCode + " ошибка формирования запроса на поиск конкурентов");
                }
                productList = getProduct(query);

                product = productList.get(0);
                for (Product p : productList) {
                    if (p.getLowerPrice() < product.getLowerPrice()){
                        product = p;
                    }
                }
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

                paramsForRequest = getDataForRequestFromCategory(page, category);
                try {
                    query = brand + " " + paramsForRequest.get(0);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    System.out.println("Для артикула: " + vendorCode + " ошибка формирования запроса на поиск конкурентов");
                }
                productList = getProduct(query);
                product = productList.stream().findFirst().orElse(null);
                break;
        }
        product.setRefFromRequest(getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&xsearch=true&sort=priceup"));
        return product;
    }

    private static Document getPageForVendorCode(String vendorCode){
        String url = getString("https://www.wildberries.ru/catalog/", vendorCode, "/detail.aspx?targetUrl=SP");
        Document page = null;
        try {
            page = Jsoup.parse(new URL(url), 30000);
        } catch (IOException e) {
            System.out.println("превышено аремя ожидания ответа сервера");
        }
        return page;
    }

    //метод читающий на странице продукта характеристики, по которым будет осуществляться запрос на поиск аналогов!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static List<String> getDataForRequestFromCategory(Document page, String category){
        List<String> paramsForRequest = new ArrayList<>();
        Element params;

        if (category.equals(PRICE_AND_SALE)){
//            params = page.select("div[class=inner-priceU]").first();
//            String text = params.text();
            Element finalCost = page.select("span[class=final-cost]").first();
            String finalPriceBuff1 = finalCost.text().replaceAll(" ", "");
            finalPriceBuff1 = finalPriceBuff1.substring(0, finalPriceBuff1.length() - 1);
            paramsForRequest.add(finalPriceBuff1);
            return paramsForRequest;
        }

        switch (category){
            case CATEGORY_4:
                try {
                    params = page.select("div[class=brand-and-name j-product-title]").first();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                String tittle4 = params.text();
                paramsForRequest.add(tittle4);
                return paramsForRequest;

            case CATEGORY_7:
                try {
                    params = page.select("div[class=brand-and-name j-product-title]").first();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                String tittle7 = params.text();
                String[] strBuf = tittle7.split(", ");
                paramsForRequest.add(strBuf[0]);

                for (int i = 1; i < strBuf.length; i++) {
                    if (strBuf[i].contains("D") || strBuf[i].contains("EYE PROTECTION")){
                        paramsForRequest.add(strBuf[i]);
                    }
                }
                return paramsForRequest;
        }

        try {
            params = page.select("div[class=params]").first();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

        if (paramsForRequest.size() == 0){
            try {
                params = page.select("div[class=brand-and-name j-product-title]").first();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            String tittle7 = params.text();
            String[] strBuf = tittle7.split(", ");

            String[] strBuf2 = strBuf[0].split(" / ");

            String[] strBuf3 = strBuf2[1].split(" ");

            String model = "";

//            for (String s : strBuf3) {
//                if (s.equalsIgnoreCase(strBuf2[0])){
//                    model = model + s + " ";
//                }
//            }

            for (int i = 0; i < strBuf3.length; i++) {
                if (strBuf3[i].equalsIgnoreCase(strBuf2[0])){
                    for (int j = i + 1; j < strBuf3.length; j++) {
                        model = model + strBuf3[j] + " ";
                    };
                    break;
                }
            }

            paramsForRequest.add(model);
        }


        return paramsForRequest;
    }
   // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    private static List<Product> getProduct(String query) {
        List<Product> productList;
        Document page = null;
        page = getPageForSearch(query);
        productList = getLowerProductForCatalog(page);


        //пробую через json
        HttpUrlConnectionHandler.getCatalog(productList);



        return productList;
    }

    private static Document getPageForSearch(String query) {
        String url = getString("https://www.wildberries.ru/catalog/0/search.aspx?search=", getQueryUTF8(query), "&xsearch=true&sort=priceup");

        Document page = null;
        try {
            page = Jsoup.parse(new URL(url), 30000);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("превышено аремя ожидания ответа сервера");
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
            e.printStackTrace();
            System.out.println("Ошибка декодирования в UTF8");
        }
        return queryUTF8;
    }

    private static List<Product> getLowerProductForCatalog(Document page){
        List<Product> productList = new ArrayList<>();
        Product product = null;

        Element catalog = page.select("div[class=catalog_main_table j-products-container]").first();
        Elements goods = catalog.select("div[class=dtList i-dtList j-card-item]");
        int i = 0;
        for (Element good : goods) {
            //артикул
            String id = good.attr("data-popup-nm-id");

            //актуальная цена
            List<String> finalPrices = getDataForRequestFromCategory(getPageForVendorCode(id), PRICE_AND_SALE);
            int lowerPrice = 0;
            try {
                lowerPrice = Integer.parseInt(finalPrices.get(0));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                e.printStackTrace();
                System.out.println("Ошибка при запросе цены на артикул: " + id);
            }

            Element fullProductCard = good.select("a[class=ref_goods_n_p j-open-full-product-card]").first();

            //имя товара
            Element nameGoods = fullProductCard.select("span[class=goods-name c-text-sm]").first();
            String name = nameGoods.text();

            //ссылка на товар
            String href = "https://www.wildberries.ru" + fullProductCard.attr("href");

            //ссылка на картинку товара
            Element img = fullProductCard.select("img[class=thumbnail]").first();
            String refForImg = "-";
            String refForImgTemp1 = img.attr("src");
            String refForImgTemp2 = img.attr("data-original");
            if (refForImgTemp2.equals("")){
                refForImg = "https:" + refForImgTemp1;
            } else {
                refForImg = "https:" + refForImgTemp2;
            }

            //актуальная цена
//            Element priceGoods = fullProductCard.select("ins[class=lower-priceU]").first();
//            int lowerPrice;
//            try {
//                String p = priceGoods.text();
//                String s = p.replaceAll(" ", "");
//                String r = p.replaceAll(" ", "").substring(0, s.length() - 1);
//                lowerPrice = Integer.parseInt(r);
//            } catch (Exception e) {
//                priceGoods = fullProductCard.select("span[class=lower-priceU]").first();
//                String[] p = priceGoods.text().split(" ");
//                lowerPrice = Integer.parseInt(p[0]);
//            }
            //старая цена и скидка
            Element priceGoods = fullProductCard.select("span[class=priceU-old-block]").first();
            int oldPrice = 0;
            //int sale = 0;
            try {
                String oldPriceAndSale = priceGoods.text();
                System.out.println(oldPriceAndSale);
                String[] arrayWithOldPriceAndSale = oldPriceAndSale.split(" ₽ -");
                oldPrice = Integer.parseInt(arrayWithOldPriceAndSale[0].replaceAll(" ", ""));
                //sale = Integer.parseInt(arrayWithOldPriceAndSale[1].replaceFirst(".$",""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //спец-акция
            priceGoods = fullProductCard.select("span[class=spec-actions-catalog i-spec-action]").first();
            String specAction = "-";
            try {
                specAction = priceGoods.text();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //рейтинг
            Element star = fullProductCard.getElementsByAttributeValueStarting("class", "c-stars").first();
            int rating = 0;
            try {
                String nameClass = star.className();
                rating = Integer.parseInt(String.valueOf(nameClass.charAt(nameClass.length() - 1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Brand
            Element brand = fullProductCard.select("strong[class=brand-name c-text-sm]").first();
            String string = brand.text();
            String brandName = string.substring(0, string.length() - 2);
            productList.add(new Product(id, brandName, name, href, refForImg, lowerPrice, oldPrice, specAction, rating, "-"));
        }
        return productList;
    }
}
