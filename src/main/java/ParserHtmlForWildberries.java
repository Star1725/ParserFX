import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHtmlForWildberries {

    private static final Logger loggerParserHtmlForWildberries = Logger.getLogger(ParserHtmlForWildberries.class.getName());
    static {
        loggerParserHtmlForWildberries.addHandler(Main.fileHandler);
    }

    //css элемент для заголовка продукта на его странице
    static final String JSOUP_ELEMENT_WITH_TITLE_PRODUCT = "div[class=brand-and-name j-product-title]";
    static final String PLAYWRIGHT_ELEMENT_WITH_TITLE_PRODUCT = "css=div[class=first-horizontal]";
    static final String HTMLUNIT_ELEMENT_WITH_TITLE_PRODUCT = "//div[@class='brand-and-name j-product-title']";

    //css элемент для описания и характеристик продукта на его странице
    static final String JSOUP_ELEMENT_WITH_DESCRIPTION_AND_PARAM_PRODUCT = "div[class=card-add-info]";
    static final String PLAYWRIGHT_ELEMENT_WITH_DESCRIPTION_AND_PARAM_PRODUCT = "css=div[class=card-add-info]";
    static final String HTMLUNIT_ELEMENT_WITH_DESCRIPTION_AND_PARAM_PRODUCT = "//div[@class='card-add-info']";

    static Document getDocumentPageForVendorCode(String myVendorCodeFromRequest) {
        String url = getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP");
        return SupplierHtmlPage.getWBPageFromJsoup(url);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static String getTitleFromPageProduct(Object page) {
        String titleFromPageProduct = null;
        if (page instanceof Document){
            Element elementBrandAndNameTitle = ((Document) page).select(JSOUP_ELEMENT_WITH_TITLE_PRODUCT).first();
            if (elementBrandAndNameTitle != null){
                return elementBrandAndNameTitle.text();
            } else {
                return Constants.NOT_FOUND_HTML_ITEM;
            }
        } else if (page instanceof Page){
            ElementHandle contentElement_titleFromPageProduct;
            boolean resultQueryIsNotValid = true;
            while (resultQueryIsNotValid) {
                try {
                    contentElement_titleFromPageProduct = ((Page) page).querySelector(PLAYWRIGHT_ELEMENT_WITH_TITLE_PRODUCT);
                    titleFromPageProduct = contentElement_titleFromPageProduct.innerText();
                    resultQueryIsNotValid = false;
                } catch (Exception ignored) {
                }
            }
        } else if (page instanceof HtmlPage){
            final HtmlDivision elementTitle = (HtmlDivision) ((HtmlPage)page).getByXPath(HTMLUNIT_ELEMENT_WITH_TITLE_PRODUCT).get(0);
            if (elementTitle != null){
                return elementTitle.asText();
            } else {
                return Constants.NOT_FOUND_HTML_ITEM;
            }
        }
        return titleFromPageProduct;
    }

    static String getDescriptionAndParam(Object page) {
        String descriptionAndParams = null;
        if (page instanceof Document){
            Element elementBrandAndNameTitle = ((Document) page).select(JSOUP_ELEMENT_WITH_DESCRIPTION_AND_PARAM_PRODUCT).first();
            if (elementBrandAndNameTitle != null){
                return elementBrandAndNameTitle.text();
            } else {
                return Constants.NOT_FOUND_HTML_ITEM;
            }
        } else if (page instanceof Page){
            ElementHandle contentElement_descriptionAndParams;
            boolean resultQueryIsNotValid = true;
            while (resultQueryIsNotValid) {
                try {
                    contentElement_descriptionAndParams = ((Page)page).querySelector(PLAYWRIGHT_ELEMENT_WITH_DESCRIPTION_AND_PARAM_PRODUCT);
                    descriptionAndParams =  contentElement_descriptionAndParams.innerText();
                    resultQueryIsNotValid = false;
                } catch (Exception ignored) {
                    continue;
                }
            }
        } else if (page instanceof HtmlPage){
            final HtmlDivision elementDescriptionAndParams = (HtmlDivision) ((HtmlPage)page).getByXPath(HTMLUNIT_ELEMENT_WITH_DESCRIPTION_AND_PARAM_PRODUCT).get(0);
            if (elementDescriptionAndParams != null){
                return elementDescriptionAndParams.asText();
            } else {
                return Constants.NOT_FOUND_HTML_ITEM;
            }
        }
        return descriptionAndParams;
    }

//получение с помощью Playwright ///////////////////////////////////////////////////////////////////////////////////////
    static Page getPageForCatalogProductsFromPlaywright(String url){

        Page page = null;
        ElementHandle contentElement_searching_results = null;
        String results = "-";
        ElementHandle contentElement_searching_results_count = null;
        String countResults = "-";
        ElementHandle contentElement_catalog_main_table;
        List<ElementHandle> contentElementsForRefProduct = null;

        boolean resultQueryIsNotValid = true;

        int tried1 = 0;
        int tried2 = 0;
        while (resultQueryIsNotValid) {

            if (tried1 == 0){
                page = SupplierHtmlPage.getWBPageFromPlaywright(url);
                tried1 = 4;
            }

            //результат запроса
            try {
                contentElement_searching_results = page.querySelector("css=div[class=searching-results-inner]");
                results = contentElement_searching_results.innerText();
                contentElement_searching_results_count = contentElement_searching_results.querySelector("css=span[class=goods-count]");
                countResults = contentElement_searching_results_count.innerText();
            } catch (Exception ignored) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            if (results.contains("По Вашему запросу ничего не найдено.")){
                return null;
            } else {
                contentElement_catalog_main_table = page.querySelector("css=div[id=catalog-content]");//работает только с div
                contentElementsForRefProduct = contentElement_catalog_main_table.querySelectorAll("css=div[id=c]");//работает только с div
                if (contentElementsForRefProduct.size() == 0) {
                    tried1--;

                    if (tried2 == 1){
                        return null;
                    }

                    loggerParserHtmlForWildberries.info("Страница получена не полностью - " + tried1);
                    if (tried1 == 0){
                        loggerParserHtmlForWildberries.info("меняем Ip и делаем перезапрос страницы - " + url);
                        try {
                            SupplierHtmlPage.switchIpForProxyFromPlaywright();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        page.close();
                        tried2++;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    resultQueryIsNotValid = false;
                }
            }
        }

        loggerParserHtmlForWildberries.info("Результат запроса для url = " + url + " - " + results + "( " + contentElementsForRefProduct.size() + " )");

        LowerProductFinder.resultSearch = results + "( " + contentElementsForRefProduct.size() + " )";

        return page;
    }

    static List<Product> getListProductsForPageCatalogPlaywright(Page pageForCatalog){
        List<Product> productList = new ArrayList<>();
        ElementHandle contentElement_catalog_main_table;
        List<ElementHandle> contentElementsForRefProduct;

        contentElement_catalog_main_table = pageForCatalog.querySelector("css=div[id=catalog-content]");//работает только с div
        contentElementsForRefProduct = contentElement_catalog_main_table.querySelectorAll("css=div[id=c]");//работает только с div

        String brandName = "-";
        String vendorCode = "-";
        String productDescription = "-";
        String refForPage = "-";
        String refForImg = "-";
        String specAction = "-";
        int competitorPriceU = 0;
        int competitorBasicPrice = 0;
        int rating = 0;

        int position = 1;

        //обработка аналогов
        loggerParserHtmlForWildberries.info(Constants.getYellowString("<<<<<<<<<<<<<<< Аналоги >>>>>>>>>>>>>>>>"));

        for (ElementHandle elementHandle : contentElementsForRefProduct) {

            loggerParserHtmlForWildberries.info(Constants.getYellowString("<<<<<<<<<<<<<<< " + position + " >>>>>>>>>>>>>>>>"));

            ElementHandle elementHandle1 = elementHandle.querySelector("css=a");
            refForPage = Constants.MARKETPLACE_WILDBERRIES_RU + elementHandle1.getAttribute("href");

            loggerParserHtmlForWildberries.info("Ссылка на " + position + " аналог - " + refForPage);

            ElementHandle elementHandle2 = elementHandle1.querySelector("css=div[class=l_class]");
            vendorCode = elementHandle2.getAttribute("id").substring(1);

            loggerParserHtmlForWildberries.info("Артикул Wildberries = " + vendorCode);

            ElementHandle elementHandle3 = elementHandle2.querySelector("css=img");
            refForImg = "https:" + elementHandle3.getAttribute("src");

            loggerParserHtmlForWildberries.info("Ссылка на картинку товара - " + refForImg);

            ElementHandle elementHandle4 = elementHandle1.querySelector("css=div[class=dtlist-inner-brand]");
            ElementHandle elementHandle5 = elementHandle4.querySelector("css=div[class=j-cataloger-price]");
            String prices = elementHandle5.innerText();
            loggerParserHtmlForWildberries.info("Цены : " + Constants.getYellowString(prices));
            String[] arrayPrices = prices.split("₽");
            if (arrayPrices.length == 2) {
                competitorPriceU = getIntegerFromString(arrayPrices[0].replace(" ", "").trim()) * 100;

                loggerParserHtmlForWildberries.info(Constants.getYellowString("competitorPriceU = " + competitorPriceU));
            } else if (arrayPrices.length == 3) {
                competitorPriceU = getIntegerFromString(arrayPrices[0].replace(" ", "").trim()) * 100;
                competitorBasicPrice = getIntegerFromString(arrayPrices[1].replace(" ", "").trim()) * 100;

                loggerParserHtmlForWildberries.info(Constants.getYellowString("competitorPriceU = " + competitorPriceU + ", " + "competitorBasicPrice = " + competitorBasicPrice));
            }

            ElementHandle elementHandle6 = elementHandle4.querySelector("css=div[class=dtlist-inner-brand-name]");
            productDescription = elementHandle6.innerText();

            loggerParserHtmlForWildberries.info("Описание продукта - " + productDescription);
            try {
                ElementHandle elementHandle7 = elementHandle1.querySelector("css=div[class=spec-action-wrap]");
                specAction = elementHandle7.innerText();

                loggerParserHtmlForWildberries.info("СпецАкция - " + specAction);
            } catch (Exception ignored) {
            }

            productList.add(new Product(
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",

                    LowerProductFinder.resultSearch,
                    LowerProductFinder.refUrlForResult,

                    brandName,
                    vendorCode,
                    productDescription,
                    refForPage,
                    refForImg,
                    specAction,
                    rating,

                    competitorPriceU,
                    0,
                    competitorBasicPrice,
                    0,
                    0,
                    0,

                    "-"));

            position++;
        }
        pageForCatalog.close();
        return productList;
    }

    public static String getSellerNameFromPlaywright(Page page){
        String sellerName = null;
        ElementHandle contentElement_sellerName;
        boolean resultQueryIsNotValid = true;
        while (resultQueryIsNotValid) {
            try {
                contentElement_sellerName = page.querySelector("css=div[class=seller]");
                sellerName =  contentElement_sellerName.innerText();
                resultQueryIsNotValid = false;
            } catch (Exception ignored) {
                continue;
            }
        }
        return sellerName;
    }

//получение с помощью HtmlUnit /////////////////////////////////////////////////////////////////////////////////////////
    public static String getMySpecActionFromHtmlUnit(HtmlPage pageForMyProduct) {
        String specAction = "-";
        HtmlDivision elementSpecAction = null;
        try {
            elementSpecAction = (HtmlDivision) pageForMyProduct.getByXPath("//div[@class='i-spec-action-v1 ']").get(0);
            specAction = elementSpecAction.asText();
        } catch (Exception ignored) {
        }

        return specAction;
    }

    static String getMyProductImageFromHtmlUnit(HtmlPage page) {
        String refMyProductImage = "-";
        //получение по id
        HtmlDivision elementImage = (HtmlDivision) page.getElementById("imageContainer");
//        System.out.println("1 дочерний элемент - " + elementImage.getChildElements().iterator().next().getAttribute("src"));

        //получение по xPath
        final HtmlImage image;
        final HtmlDivision divisionImage;

        try {
            image = (HtmlImage) page.getByXPath("//img[@class='preview-photo j-zoom-preview']").get(0);
            refMyProductImage = "https:" + image.getAttributes().getNamedItem("src").getNodeValue();
        } catch (Exception ignored) {
            divisionImage = (HtmlDivision) page.getByXPath("//div[@id='imageContainer']").get(0);
            refMyProductImage = "https:" + divisionImage.getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
        }

        return refMyProductImage;
    }

//получение с помощью Jsoup ////////////////////////////////////////////////////////////////////////////////////////////
    static String getMySpecActionFromJsoup(Document page) {
        Element specAction = page.select(Constants.ELEMENT_WITH_SPEC_ACTION_MY_PRODUCT).first();
        if (specAction != null){
            return specAction.text();
        } else {
            return Constants.NOT_FOUND_HTML_ITEM;
        }
    }

    static String getMyProductsImageFromJsoup(Document page) {
        Element elementImage1 = page.select("img[class=j-zoom-photo preview-photo]").first();
        Element elementImage2 = page.select(Constants.ELEMENT_WITH_PHOTO_MY_PRODUCT).first();
        if (elementImage2 != null){
            return "https:" + elementImage2.attr(Constants.ATTRIBUTE_WITH_REF_FOR_IMAGE_1);
        } else return Constants.NOT_FOUND_HTML_ITEM;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //вспомогательные методы
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

    private static int getIntegerFromString(String price) {
        StringBuilder resultPrice = new StringBuilder();

        Pattern p = Pattern.compile("-?\\d+");

        Matcher m = p.matcher(price);
        while (m.find()) {
            resultPrice.append(m.group());
        }
        return Integer.parseInt(resultPrice.toString());
    }

    private static String getString(String s, String queryUTF8, String s2) {
        return s + queryUTF8 + s2;
    }
}
