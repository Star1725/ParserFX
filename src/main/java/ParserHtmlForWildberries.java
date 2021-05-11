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
import java.util.ArrayList;
import java.util.List;

public class ParserHtmlForWildberries {

    static Document getDocumentPageForVendorCode(String myVendorCodeFromRequest) {
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

    private static String getString(String s, String queryUTF8, String s2) {
        return s + queryUTF8 + s2;
    }

    private String getSellerName(String vendorCode, WebClient webClient){
//        Document page = getDocumentPageForVendorCode(vendorCode);
        String url = getString("https://www.wildberries.ru/catalog/", vendorCode, "/detail.aspx?targetUrl=SP");
        HtmlPage page = null;
        HtmlDivision spanSellerName;
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

        return spanSellerName.asText();
    }

    static String getMyProductsPhoto(Document page) {
        //элемент переписан 16.02.21
        //Element elementImage1 = page.select("img[class=j-zoom-photo preview-photo]").first();
        Element elementImage2 = page.select(Constants.ELEMENT_WITH_PHOTO_MY_PRODUCT).first();
        if (elementImage2 != null){
            return "https:" + elementImage2.attr(Constants.ATTRIBUTE_WITH_REF_FOR_IMAGE_1);
        } else return Constants.NOT_FOUND_HTML_ITEM;
    }

    //по наличию этого параметра определяем есть ли акция
    static String getMySpecAction(Document page) {
        Element specAction = page.select(Constants.ELEMENT_WITH_SPEC_ACTION_MY_PRODUCT).first();
        if (specAction != null){
            return specAction.text();
        } else {
            return Constants.NOT_FOUND_HTML_ITEM;
        }
    }

    //для формирования запроса на основании названия модели товара, которое находится в заголовке как аправило сразу после бренда
    static String getMyProductsTitle(Document page) {
        Element elementBrandAndNameTitle = page.select(Constants.ELEMENT_WITH_TITLE_MY_PRODUCT).first();
        if (elementBrandAndNameTitle != null){
            return elementBrandAndNameTitle.text();
        } else {
            return Constants.NOT_FOUND_HTML_ITEM;
        }
    }

//    static Document getPageForUrl(String url) {
//
//        Document page = null;
//        while (page == null){
//            try {
//                System.out.println("Получение страницы для url = " + url);
//                page = Jsoup.parse(new URL(url), 30000);
//            } catch (IOException ignored) {
//                try {
//                    System.out.println("Получаем пустую страницу. Видимо проблемы с соединением");
//                    Thread.sleep(1000);
//                } catch (InterruptedException interruptedException) {
//                    interruptedException.printStackTrace();
//                }
//            }
//        }
//        return page;
//    }

    static List<Product> getCatalogProductsForRequestPage(Document page, String myBrand, String model, List<String> arrayParams){
        List<Product> productList = new ArrayList<>();
        if (page != null){

            //результат запроса
            try {
                Element result = page.select(Constants.ELEMENT_WITH_RESULT_SEARCH).first();
                LowerProductFinder.resultSearch = result.text();
                System.out.println(LowerProductFinder.resultSearch);
            } catch (Exception e) {
                Element result1 = page.select(Constants.ELEMENT_WITH_EMPTY_RESULT_SEARCH_1).first();
                Element result2 = page.select(Constants.ELEMENT_WITH_EMPTY_RESULT_SEARCH_2).first();
                LowerProductFinder.resultSearch = result1.text() + "\n\r" + result2.text();
                System.out.println(LowerProductFinder.resultSearch);
                return productList;
            }

            Element catalog = page.select(Constants.ELEMENT_WITH_CATALOG).first();
            if (catalog == null){
                return productList;
            }
            Elements goods = catalog.select(Constants.ELEMENT_WITH_PRODUCT);
            //int countSearch = goods.size();
            for (Element good : goods) {
//                //артикул
//                String vendorCode = good.attr(Constants.ATTRIBUTE_WITH_VENDOR_CODE); артикул исчем по новому с 14.04.21

                Element fullProductCard = good.select(Constants.ELEMENT_WITH_CARD_PRODUCT).first();

                //имя товара
                Element nameGoods = fullProductCard.select(Constants.ELEMENT_WITH_NAME_PRODUCT).first();
                String productName = nameGoods.text().toLowerCase();

                //ссылка на товар
                String refForPage = Constants.MARKETPLACE + fullProductCard.attr(Constants.ATTRIBUTE_WITH_REF_FOR_PAGE_PRODUCT);

/////////////////определяем есть ли в названии искомые параметры arrayParams////////////////////////////////////////////
                if (arrayParams.size() > 0){
                    List<String> listWithCableParam_1 = null;
                    List<String> listWithCableParam_2 = null;
                    if (arrayParams.size() == 1){
                        listWithCableParam_1 = Constants.getCollectionsParamCableForSearchInTitle(arrayParams.get(0), refForPage);
                    } else if (arrayParams.size() == 2){
                        listWithCableParam_1 = Constants.getCollectionsParamCableForSearchInTitle(arrayParams.get(0), refForPage);
                        listWithCableParam_2 = Constants.getCollectionsParamCableForSearchInTitle(arrayParams.get(1), refForPage);
                    }

                    boolean check1 = false;
                    boolean check2 = false;
                    boolean resultCheck = false;

                    for (String param1 : listWithCableParam_1) {
                        if (productName.contains(param1)) {
                            check1 = true;
                            break;
                        }
                    }
                    if (listWithCableParam_2 != null){
                        for (String param2 : listWithCableParam_2) {
                            if (productName.contains(param2)) {
                                check2 = true;
                                break;
                            }
                        }
                        resultCheck = check1 && check2;
                    } else {
                        resultCheck = check1;
                    }

                    if (!resultCheck) {
                        Document pageProduct = SupplierHtmlPage.getPageForUrl(refForPage);
                        Elements pps = pageProduct.select(Constants.ELEMENT_WITH_PP);

                        for (Element pp : pps) {
                            Elements spans = pp.select(Constants.ELEMENT_SPAN_IN_PP);
                            Element span1 = spans.get(0);
                            String spanText1 = span1.text();
                            Element span2 = spans.get(1);
                            String spanText2 = span2.text();

                            if (spanText1.contains("Модель")) {
                                productName = productName + " " + spanText2.toLowerCase();
                            }
                            if (spanText1.contains("Вид разъема")) {
                                productName = productName + " " + spanText2.toLowerCase();
                            }
                            if (spanText1.contains("Длина кабеля")) {
                                productName = productName + " " + spanText2 + " м";
                            }
                        }
                    }
                }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                    try {
                        rating = Integer.parseInt(String.valueOf(nameClass.charAt(nameClass.length() - 1)));
                    } catch (NumberFormatException ignored) {
                    }
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

                        LowerProductFinder.resultSearch,
                        "-",

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
