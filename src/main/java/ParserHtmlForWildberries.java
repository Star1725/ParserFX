import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlBody;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHtmlForWildberries {

    private static final Logger loggerParserHtmlForWildberries = Logger.getLogger(ParserHtmlForWildberries.class.getName());

    static {
        loggerParserHtmlForWildberries.addHandler(Main.fileHandler);
    }

    static Document getDocumentPageForVendorCode(String myVendorCodeFromRequest) {
        String url = getString("https://www.wildberries.ru/catalog/", myVendorCodeFromRequest, "/detail.aspx?targetUrl=SP");
        return SupplierHtmlPage.getWildberriesPageFromJsoup(url);
    }

    private static String getString(String s, String queryUTF8, String s2) {
        return s + queryUTF8 + s2;
    }

    public static String getSellerName(Page page){
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

    static String getMyProductsPhoto(Document page) {
        Element elementImage1 = page.select("img[class=j-zoom-photo preview-photo]").first();
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

    static String getTitleFromPageProduct(Page page) {
        String titleFromPageProduct = null;
        ElementHandle contentElement_titleFromPageProduct;
        boolean resultQueryIsNotValid = true;
        while (resultQueryIsNotValid) {
            try {
                contentElement_titleFromPageProduct = page.querySelector("css=div[class=first-horizontal]");
                titleFromPageProduct = contentElement_titleFromPageProduct.innerText();
                resultQueryIsNotValid = false;
            } catch (Exception ignored) {
                continue;
            }
        }
        return titleFromPageProduct;
    }

    static String getDescriptionAndParam(Page page) {
        String descriptionAndParams = null;
        ElementHandle contentElement_descriptionAndParams;
        boolean resultQueryIsNotValid = true;
        while (resultQueryIsNotValid) {
            try {
                contentElement_descriptionAndParams = page.querySelector("css=div[class=card-add-info]");
                descriptionAndParams =  contentElement_descriptionAndParams.innerText();
                resultQueryIsNotValid = false;
            } catch (Exception ignored) {
                continue;
            }
        }
        return descriptionAndParams;
    }

    static List<Product> getCatalogProductsForRequestPage(String myBrand, String model, List<List<String>> arrayParams, String url){

        Page page = null;
        List<Product> productList = null;
        ElementHandle contentElement_searching_results = null;
        List<ElementHandle> contentElementsForRefProduct = null;

        boolean resultQueryIsNotValid = true;

        while (resultQueryIsNotValid) {
            page = SupplierHtmlPage.getWildberriesPageFromPlaywright(url);
            productList = new ArrayList<>();

            //результат запроса
            try {
                contentElement_searching_results = page.querySelector("css=div[class=searching-results-inner]");
            } catch (Exception ignored) {
                continue;
            }
            loggerParserHtmlForWildberries.info("Результат запроса для url = " + url + " - " + contentElement_searching_results.innerText());
            System.out.println("Результат запроса для url = " + url + " - " + contentElement_searching_results.innerText());
            if (contentElement_searching_results.innerText().contains("По Вашему запросу ничего не найдено.")){
                return productList;
            } else {
                contentElementsForRefProduct = page.querySelectorAll("css=div[class=dtList-inner]");//работает только с div
                if (contentElementsForRefProduct.size() == 0) {
                    loggerParserHtmlForWildberries.info("Страница получена не полностью");
                    System.out.println("Страница получена не полностью");
                } else {
                    resultQueryIsNotValid = false;
                }
            }
        }

        LowerProductFinder.resultSearch = contentElement_searching_results.innerText();

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
        System.out.println("<<<<<<<<<<<<<<< Аналоги >>>>>>>>>>>>>>>>");
        loggerParserHtmlForWildberries.info(("<<<<<<<<<<<<<<< Аналоги >>>>>>>>>>>>>>>>"));
        for (ElementHandle elementHandle : contentElementsForRefProduct) {
            System.out.println("< " + position + " >");
            loggerParserHtmlForWildberries.info("< " + position + " >");

            ElementHandle elementHandle1 = elementHandle.querySelector("css=a");
            refForPage = Constants.MARKETPLACE_WILDBERRIES_RU + elementHandle1.getAttribute("href");
            System.out.println("Ссылка на " + position + " аналог - " + refForPage);
            loggerParserHtmlForWildberries.info("Ссылка на " + position + " аналог - " + refForPage);

            ElementHandle elementHandle2 = elementHandle1.querySelector("css=div[class=l_class]");
            vendorCode = elementHandle2.getAttribute("id").substring(1);
            System.out.println("Артикул Wildberries = " + vendorCode);
            loggerParserHtmlForWildberries.info("Артикул Wildberries = " + vendorCode);

            ElementHandle elementHandle3 = elementHandle2.querySelector("css=img");
            refForImg = "https:" + elementHandle3.getAttribute("src");
            System.out.println("Ссылка на картинку товара - " + refForImg);
            loggerParserHtmlForWildberries.info("Ссылка на картинку товара - " + refForImg);

            ElementHandle elementHandle4 = elementHandle1.querySelector("css=div[class=dtlist-inner-brand]");
            ElementHandle elementHandle5 = elementHandle4.querySelector("css=div[class=j-cataloger-price]");
            String prices = elementHandle5.innerText();
            System.out.println("Цены : " + prices);
            loggerParserHtmlForWildberries.info("Цены : " + prices);
            String[] arrayPrices = prices.split("₽");
            if (arrayPrices.length == 2) {
                competitorPriceU = getIntegerFromString(arrayPrices[0].replace(" ", "").trim()) * 100;
                System.out.println("competitorPriceU = " + competitorPriceU);
                loggerParserHtmlForWildberries.info("competitorPriceU = " + competitorPriceU);
            } else if (arrayPrices.length == 3) {
                competitorPriceU = getIntegerFromString(arrayPrices[0].replace(" ", "").trim()) * 100;
                competitorBasicPrice = getIntegerFromString(arrayPrices[1].replace(" ", "").trim()) * 100;
                System.out.println("competitorPriceU = " + competitorPriceU + ", " + "competitorBasicPrice = " + competitorBasicPrice);
                loggerParserHtmlForWildberries.info("competitorPriceU = " + competitorPriceU + ", " + "competitorBasicPrice = " + competitorBasicPrice);
            }

            ElementHandle elementHandle6 = elementHandle4.querySelector("css=div[class=dtlist-inner-brand-name]");
            productDescription = elementHandle6.innerText();
            System.out.println("Описание продукта - " + productDescription);
            loggerParserHtmlForWildberries.info("Описание продукта - " + productDescription);
            try {
                ElementHandle elementHandle7 = elementHandle1.querySelector("css=div[class=spec-action-wrap]");
                specAction = elementHandle7.innerText();
                System.out.println("СпецАкция - " + specAction);
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
        page.close();
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

    private static int getIntegerFromString(String price) {
        StringBuilder resultPrice = new StringBuilder();

        Pattern p = Pattern.compile("-?\\d+");

        Matcher m = p.matcher(price);
        while (m.find()) {
            resultPrice.append(m.group());
        }
        return Integer.parseInt(resultPrice.toString());
    }
}
