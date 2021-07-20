import com.gargoylesoftware.htmlunit.html.*;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHTMLForOzon {

    private static final Logger loggerParserHTMLForOzon = Logger.getLogger(ParserHtmlForWildberries.class.getName());
    static {
        loggerParserHTMLForOzon.addHandler(Main.fileHandler);
    }

//получение с помощью Playwright ///////////////////////////////////////////////////////////////////////////////////////
    static Page getPageForCatalogProductsFromPlaywright(String url){
        Page page = null;
        String selector = null;
        ElementHandle contentElement_searching_results = null;
        String results = "-";

        List<ElementHandle> contentElementsForRefProduct = null;

        int versionPage = 1;

        loggerParserHTMLForOzon.info(Constants.getYellowString("IP №" + LowerProductFinder.countSwitchIP + ".Получение страницы ozon для запроса - " + LowerProductFinder.myQuery + ". Артикул Ozon - " + LowerProductFinder.myVendorCodeFromRequest));
        boolean isNotGetValidPage = true;
        while (isNotGetValidPage) {
            page = SupplierHtmlPage.getOzonPageFromPlaywrightForJavaScript(url);

            //получаем кол-во найденных аналогов
            try {
                selector = "css=div[class=b6r7]";
                contentElement_searching_results = page.waitForSelector(selector);
            } catch (Exception exception) {
                loggerParserHTMLForOzon.info("Ошибка при парсинге колличества найденных аналогов моего товара (элемента " + selector + ") на странице \"OZON\" - " + url);
                exception.printStackTrace();
            }

            try {
                results = contentElement_searching_results.innerText();
                isNotGetValidPage = false;

                if (results.contains("товаров сейчас нет")){
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                loggerParserHTMLForOzon.info("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                ElementHandle htmlBody = page.querySelector("css=body");
                String hrefForNewCatalog = htmlBody.innerHTML();
                System.out.println(hrefForNewCatalog);
                hrefForNewCatalog = htmlBody.textContent();
                String[] strBuff = hrefForNewCatalog.split("=", 2);
                String temp = strBuff[1].substring(strBuff[1].indexOf("\"") + 1, strBuff[1].lastIndexOf("\""));
                String newUrlForNewCatalog = "https://www.ozon.ru" + temp;
                url = newUrlForNewCatalog;
                versionPage = 2;
                continue;
            }
        }

        loggerParserHTMLForOzon.info(Constants.getBlueString("Результат поиска аналогов - " + results));
        LowerProductFinder.resultSearch = results;
        return page;
    }

    public static List<Product> getListProductsForPageCatalogPlaywright(Page pageForCatalog) {
        List<Product> productList = new ArrayList<>();
        String brandName = "-";
        String vendorCode = "-";
        String productDescription = "-";
        String refForPage = "-";
        String refForImg = "-";
        String specAction = "-";
        String sellerName = "-";
        int competitorPriceU = 0;
        int competitorBasicPrice = 0;
        int rating = 0;

        int position = 1;

        int versionPage = 1;

        String selector = null;
        if (pageForCatalog == null) {
            return productList;
        }

        ElementHandle contentElementWithCatalog_ao4 = null;
        List<ElementHandle> contentElementsForAnalogs = new ArrayList<>();

        //обработка аналогов
        loggerParserHTMLForOzon.info(Constants.getYellowString("<<<<<<<<<<<<<<< Аналоги >>>>>>>>>>>>>>>>"));

        int numberPageCatalog = 0;
        int maxAnalisePage = 1;
        boolean check;
        int fullCountProducts = 0;
        if (LowerProductFinder.resultSearch.contains("товаров")
                || LowerProductFinder.resultSearch.contains("товар")
                || LowerProductFinder.resultSearch.contains("товара")) {
            String[] buff = LowerProductFinder.resultSearch.split(" ");
            fullCountProducts = Integer.parseInt(buff[buff.length - 2]);
        }

        List<String> listWithRefNextPageCatalog = null;
        try {
            selector = "css=div[class=b7t2]";
            List<ElementHandle> listElementsWithRefNextPageCatalog_div = pageForCatalog.querySelector(selector).querySelectorAll("css=div");
            selector = "a[href]";
            List<ElementHandle> listElementsWithRefNextPageCatalog_a = listElementsWithRefNextPageCatalog_div.get(0).querySelectorAll(selector);

            listWithRefNextPageCatalog = new ArrayList<>();
            if (listElementsWithRefNextPageCatalog_a != null){
                for (ElementHandle elementHandle : listElementsWithRefNextPageCatalog_a) {
                    listWithRefNextPageCatalog.add(Constants.MARKETPLACE_OZON_RU + elementHandle.getAttribute("href"));
                }
                maxAnalisePage = listWithRefNextPageCatalog.size();
                if (listWithRefNextPageCatalog.size() > 3){
                    maxAnalisePage = 3;
                }
            }
        } catch (Exception ignored) {
            if (fullCountProducts >= 36) {
                loggerParserHTMLForOzon.info("Ошибка при парсинге элемента с ссылками на страницы каталого (элемента " + selector + ") на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
            }
        }

        do {
            try {
                selector = "css=div[class=ao4]";
                contentElementWithCatalog_ao4 = pageForCatalog.waitForSelector(selector);
            } catch (Exception exception) {
                loggerParserHTMLForOzon.info("Ошибка при парсинге элемента с аналогами моего товара (элемента " + selector + ") на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
                exception.printStackTrace();
            }

            int countProductsForPage = 0;
            int countTried = 10;
            do {

                if (numberPageCatalog == 0){
                    if (fullCountProducts < 36){
                        countProductsForPage = fullCountProducts;
                    } else {
                        countProductsForPage = Math.abs(fullCountProducts * numberPageCatalog - 36);
                    }
                } else if (numberPageCatalog == 1){
                    countProductsForPage = Math.abs(fullCountProducts * numberPageCatalog - 36);
                    if (countProductsForPage > 36){
                        countProductsForPage = 36;
                    }
                } else if (numberPageCatalog == 2){
                    countProductsForPage = fullCountProducts - 72;
                    if (countProductsForPage > 36){
                        countProductsForPage = 36;
                    }
                }
                try {
                    Thread.sleep(1000);
                    selector = "css=div[class=a0c4]";
                    assert contentElementWithCatalog_ao4 != null;
                    contentElementsForAnalogs = contentElementWithCatalog_ao4.querySelectorAll(selector);
                } catch (Exception exception) {
                    loggerParserHTMLForOzon.info("Ошибка при парсинге элементов с карточками аналогов моего товара (элемента " + selector + ")" +
                            " на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
                    exception.printStackTrace();
                }
                loggerParserHTMLForOzon.info("contentElementsForAnalogs.size() = " + contentElementsForAnalogs.size() + ", countProductsForPage = " + countProductsForPage + ". Tried = " + countTried);
                countTried--;
            } while ((contentElementsForAnalogs.size() != countProductsForPage) && countTried != 0);

            for (ElementHandle contentElementsForItemAnalog : contentElementsForAnalogs) {

                List<ElementHandle> elementHandleListForVersionPage_2 = null;
                ElementHandle contentWithDescriptionForVersionPage_2 = null;
                ElementHandle contentWithPricesAndSellerForVersionPage_2 = null;

                loggerParserHTMLForOzon.info(Constants.getYellowString("----------------- " + position + " -----------------"));

                if (checkDiscountedItemProduct(position, contentElementsForItemAnalog)) {
                    position++;
                    continue;
                }
                try {
                    selector = "css=a";
                    ElementHandle contentWithRefForPage = contentElementsForItemAnalog.waitForSelector(selector);
                    refForPage = Constants.MARKETPLACE_OZON_RU + contentWithRefForPage.getAttribute("href");
                } catch (Exception e) {
                    loggerParserHTMLForOzon.info("Ошибка при парсинге элемента с ссылкой на страницу " + position + "аналога моего товара (элемента \"" + selector + "\")" +
                            " на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
                    e.printStackTrace();
                }
                loggerParserHTMLForOzon.info("Ссылка на " + position + " аналог - " + refForPage);

                /////////////////////получение артикула ozon для продукта
                vendorCode = getVendorCodeForItemProduct_Ozon(refForPage, vendorCode);
                loggerParserHTMLForOzon.info("Артикул Ozon = " + vendorCode);

                try {
                    selector = "css=img";
                    ElementHandle contentElementWithRefForImg = contentElementsForItemAnalog.querySelector(selector);
                    refForImg = contentElementWithRefForImg.getAttribute("src");
                } catch (Exception e) {
                    loggerParserHTMLForOzon.info("Ошибка при парсинге элемента с ссылкой на изображение " + position + "аналога моего товара (элемента \"" + selector + "\")" +
                            " на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
                    e.printStackTrace();
                }
                loggerParserHTMLForOzon.info("Ссылка на картинку товара - " + refForImg);

                ElementHandle contentWithPricesDescriptionAndSeller_div = null;
                List<ElementHandle> contentsWithPricesAndDescription_a = null;
                try {
                    selector = "css=div[class=a0s9]";
                    contentWithPricesDescriptionAndSeller_div = contentElementsForItemAnalog.querySelector(selector);
                    if (contentWithPricesDescriptionAndSeller_div == null) {
                        selector = "css=div[class=a0t0]";
                        elementHandleListForVersionPage_2 = contentElementsForItemAnalog.querySelectorAll(selector);
                        if (elementHandleListForVersionPage_2 != null) {
                            versionPage = 2;
                            contentWithDescriptionForVersionPage_2 = elementHandleListForVersionPage_2.get(1);
                            contentWithPricesAndSellerForVersionPage_2 = elementHandleListForVersionPage_2.get(2);
                        }
                    } else {
                        selector = "css=a";
                        contentsWithPricesAndDescription_a = contentWithPricesDescriptionAndSeller_div.querySelectorAll(selector);
                    }
                } catch (Exception e) {
                    loggerParserHTMLForOzon.info("Ошибка при парсинге элемента с ценами, описанием и продавцом " + position + "аналога моего товара (элемента \"" + selector + "\")" +
                            " на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
                    e.printStackTrace();
                }

                String prices = null;
                try {
                    if (versionPage == 1) {
                        selector = "css=div";
                        ElementHandle contentElementsWithPrices = contentsWithPricesAndDescription_a.get(0).querySelector(selector);
                        prices = contentElementsWithPrices.innerText();
                    } else if (versionPage == 2) {
                        selector = "css=a";
                        ElementHandle contentElementsWithPrices = contentWithPricesAndSellerForVersionPage_2.querySelector(selector);
                        selector = "css=div";
                        List<ElementHandle> elementHandleList = contentElementsWithPrices.querySelectorAll(selector);
                        if (elementHandleList.size() == 1) {
                            prices = elementHandleList.get(0).innerText();
                        } else if (elementHandleList.size() == 2){
                            prices = elementHandleList.get(1).innerText();
                        }
                    }
                    loggerParserHTMLForOzon.info("Цены : " + Constants.getYellowString(prices));
                    String[] arrayPrices = prices.split("\n");
                    if (arrayPrices.length == 1) {
                        competitorPriceU = getIntegerFromString(arrayPrices[0].replace(" ", "").trim()) * 100;
                        loggerParserHTMLForOzon.info(Constants.getYellowString("competitorPriceU = " + competitorPriceU));
                    } else if (arrayPrices.length == 2) {
                        competitorPriceU = getIntegerFromString(arrayPrices[0].replace(" ", "").trim()) * 100;
                        competitorBasicPrice = getIntegerFromString(arrayPrices[1].replace(" ", "").trim()) * 100;
                        loggerParserHTMLForOzon.info(Constants.getYellowString("competitorPriceU = " + competitorPriceU + ", " + "competitorBasicPrice = " + competitorBasicPrice));
                    }
                } catch (Exception e) {
                    loggerParserHTMLForOzon.info("Ошибка при парсинге элемента с ценами " + position + "аналога моего товара (элемента \"" + selector + "\")" +
                            " на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
                    e.printStackTrace();
                }

                try {
                    if (versionPage == 1) {
                        productDescription = contentsWithPricesAndDescription_a.get(1).innerText();
                    } else if (versionPage == 2) {
                        productDescription = contentWithDescriptionForVersionPage_2.innerText();
                    }
                } catch (Exception e) {
                    loggerParserHTMLForOzon.info("Ошибка при парсинге элемента с ценами " + position + "аналога моего товара (элемента \"css=span[class=j4 as3 az a0f2 f-tsBodyL item b3u9]\")" +
                            " на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
                    e.printStackTrace();
                }
                loggerParserHTMLForOzon.info(Constants.getYellowString("Описание аналога - " + productDescription));

                try {
                    selector = "text=продавец";
                    ElementHandle contentWithSeller = null;
                    if (versionPage == 1) {
                        contentWithSeller = contentWithPricesDescriptionAndSeller_div.querySelector(selector);
                    } else if (versionPage == 2) {
                        contentWithSeller = contentWithPricesAndSellerForVersionPage_2.querySelector(selector);
                    }
                    String[] buff = contentWithSeller.innerText().split("продавец");
                    sellerName = buff[1];
                } catch (Exception e) {
                    loggerParserHTMLForOzon.info("Ошибка при парсинге элемента с именем продавца " + position + "аналога моего товара (элемента \"" + selector + "\")" +
                            " на странице \"OZON\" - " + LowerProductFinder.urlForMyQuery);
                    e.printStackTrace();
                }
                loggerParserHTMLForOzon.info(Constants.getYellowString("Продавец - " + sellerName));

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

                        sellerName));

                position++;

                brandName = "-";
                vendorCode = "-";
                productDescription = "-";
                refForPage = "-";
                refForImg = "-";
                specAction = "-";
                sellerName = "-";
                competitorPriceU = 0;
                competitorBasicPrice = 0;
                rating = 0;
            }
            numberPageCatalog++;
            pageForCatalog.close();
            if (numberPageCatalog < maxAnalisePage) {
                String urlForNextPage = listWithRefNextPageCatalog.get(numberPageCatalog);
                loggerParserHTMLForOzon.info(Constants.getYellowString("Получение " + (numberPageCatalog + 1) + " страницы каталога"));
                pageForCatalog = SupplierHtmlPage.getOzonPageFromPlaywrightForJavaScript(urlForNextPage);
            }
            check = numberPageCatalog < maxAnalisePage;
        }
        while (check);


        return productList;
    }

    private static boolean checkDiscountedItemProduct(int position, ElementHandle itemProduct) {
        boolean isDiscountedItem;
        String discountedItem = "Уцененный товар";
        String fullInformationItemProduct = itemProduct.innerText();
        if (fullInformationItemProduct.contains(discountedItem)) {
            loggerParserHTMLForOzon.info(Constants.getYellowString(Constants.getRedString("продукт № " + position + " - уценённый")));
            isDiscountedItem = true;
            //если продукт уценённый, пропускаем его
            return true;
        }
        return false;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static String getString_pricesItemProduct(int versionPage, int item, HtmlElement itemProduct) {
        String xpathExpr = null;
        String pricesItemProduct = "";
        if (versionPage == 1){
            xpathExpr = "//a[@class='a0y9 tile-hover-target']";
        } else if (versionPage == 2){
            xpathExpr = "//a[@class='a0y9 a0z0 tile-hover-target']";
        }
        HtmlAnchor a_pricesItemProduct = (HtmlAnchor) itemProduct.getByXPath(xpathExpr).get(item);
        Iterable<DomElement> elements = a_pricesItemProduct.getChildElements();
        for (DomElement element: elements){
            if (element.getTextContent().contains("мес")){
                continue;
            }
            pricesItemProduct = pricesItemProduct + element.getTextContent();
        }

        if (pricesItemProduct.contains("%")){
            String[] tempSttring = pricesItemProduct.split("%");
            pricesItemProduct = tempSttring[1];
        } else {

        }
        System.out.println("цены для продукта № " + (item + 1) + " = " + pricesItemProduct + ":");
        pricesItemProduct = pricesItemProduct + "/";
        return pricesItemProduct;
    }

    private static String getString_sellerItemProduct(int versionPage, int item, HtmlElement itemProduct) {
        String sellerItemProduct;
        String xpathExpr = null;
        if (versionPage == 1){
            xpathExpr = "//div[@class='a0t6']";
        } else if (versionPage == 2){
            xpathExpr = "//div[@class='a0s9 a0t']";
        }
        HtmlDivision division_SellerItemProduct = (HtmlDivision) itemProduct.getByXPath(xpathExpr).get(item);
        String[] tempSellerItemProduct = division_SellerItemProduct.getTextContent().split("Продавец");
        sellerItemProduct = tempSellerItemProduct[1];
        System.out.println("имя продовца продукта № " + (item + 1) + " = " + sellerItemProduct);
        return sellerItemProduct;
    }

    private static String getString_href_forItemProduct(int versionPage, int item, HtmlElement itemProduct) {
        String refForItemProduct;
        String xpathExpr = null;
        if (versionPage == 1){
            xpathExpr = "//a[@class='a0v2 tile-hover-target']";
        } else if (versionPage == 2){
            xpathExpr = "//a[@class='a0v2 a0v4 tile-hover-target']";
        }
        HtmlAnchor a_href_forItemProduct = (HtmlAnchor) itemProduct.getByXPath(xpathExpr).get(item);
        refForItemProduct = "https://www.ozon.ru" + a_href_forItemProduct.getAttributes().getNamedItem("href").getNodeValue();
        System.out.println("ссылка на продукт № " + (item + 1) + " = " + refForItemProduct);
        return refForItemProduct;
    }


    private static String getVendorCodeForItemProduct_Ozon(String refForProduct, String vendorCode) {
        String[] arrayBuff1 = refForProduct.split("/");
        for (int i = 0; i < arrayBuff1.length; i++) {
            if (arrayBuff1[i].equals("id")) {
                vendorCode = arrayBuff1[i + 1];
                break;
            }
        }

        if (vendorCode.equals("-")) {
            String[] arrayBuff2 = refForProduct.split("-");

            for (int i =1; i <arrayBuff2.length; i++){
                if (arrayBuff2[i].contains("/")){
                    String[] arrayBuff3 = arrayBuff2[i].split("/");
                    vendorCode = arrayBuff3[0];
                }
            }
        }
        return vendorCode;
    }

    private static String getStringLengthForCable(String brand, String model, List<String> arrayParams, String refForProduct, String productDescription) {
        //для кабелей определение наличия длинны в описании
        if (arrayParams.size() == 2){
            boolean check1 = false;
            List<String> listWithCableParam_1 = Constants.getCollectionsParamCable(arrayParams.get(0), brand + model);
            for (String type: listWithCableParam_1){
                if (productDescription.toLowerCase().contains(type)){
                    check1 = true;
                    break;
                }
            }
            if (check1){
                boolean check2 = false;
                for (String length: Constants.listForCableAllLength){
                    if (productDescription.contains(length)) {
                        check2 = true;
                        break;
                    }
                }
                //если длины нет, то идем на страницу товара и исчем длину там
                if (!check2){
                    System.out.println("Поиск длинны кабеля на его странице = " + refForProduct);
                    HtmlPage pageProduct = (HtmlPage) SupplierHtmlPage.getOzonPageFromHtmlUnit(refForProduct);
                    try {
                        List<HtmlElement> dListElement = pageProduct.getByXPath("//dl[@class='db8']");
                        for (HtmlElement db: dListElement){
                            if (db.asText().contains("Длина")){
                                String length = db.getElementsByTagName("dd").get(0).asText();
                                productDescription = productDescription + " " + length + "м";
                            }
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }

        }
        return productDescription;
    }

    private static Iterable<DomElement> getDomElements(HtmlElement itemProduct) throws Exception {

        while (itemProduct.getChildElementCount() != 3) {
            itemProduct = (HtmlElement) itemProduct.getFirstChild();
        }
        return itemProduct.getChildElements();
    }

    static String getRefMyProductsImageFromPlaywright(Page page, String url) {
        String refForMyImage = "-";
        ElementHandle elementHandle;
        String selector = "css=div[class=a0i7]";
        try {
            elementHandle = page.querySelector(selector);
            selector = "css=img[class=_3Ugp]";
            elementHandle = elementHandle.waitForSelector(selector);
            System.out.println(elementHandle.innerHTML());
            loggerParserHTMLForOzon.info(Constants.getWhiteString(Constants.getYellowString("Получение ссылки на картинку моего товара")));
            refForMyImage = elementHandle.getAttribute("src");
            loggerParserHTMLForOzon.info(Constants.getWhiteString("Ссылка на картинку моего товара получена!"));
        } catch (Exception ignored) {
            loggerParserHTMLForOzon.info(Constants.getYellowString("Ошибка при парсинге ссылки на картинку моего товара (элемента " + selector + ") на странице \"OZON\" - " + url));
        }

        return refForMyImage;
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

    public static String checkAndGetLengthInDescriptionAndParam(Object pageProduct, String productDescription) {
        String selector = null;
        if (pageProduct instanceof HtmlPage){
            try {
                selector = "//dl[@class='db8']";
                List<HtmlElement> dListElement = ((HtmlPage) pageProduct).getByXPath(selector);
                for (HtmlElement db: dListElement){
                    if (db.asText().contains("Длина")){
                        selector = "dd";
                        String length = db.getElementsByTagName(selector).get(0).asText();
                        productDescription = productDescription + " " + length + "м";
                    }
                }
            } catch (Exception e) {
                loggerParserHTMLForOzon.info("Ошибка при поиске элемента (\"" + selector + "\") с характеристикой длинна аналога моего товара на его странице");
                e.printStackTrace();
            }
        }
        return productDescription;
    }

    public static String getParams(Object pageProduct) {
        String selector = null;
        String params = null;
        if (pageProduct instanceof HtmlPage){
            try {
                selector = "//div[@class='da4']";
                HtmlDivision contentParams = (HtmlDivision) ((HtmlPage) pageProduct).getByXPath(selector).get(0);
                params = contentParams.asText();

            } catch (Exception e) {
                loggerParserHTMLForOzon.info("Ошибка при парсинге элемента (\"" + selector + "\") с характеристиками  аналога моего товара на его странице");
                e.printStackTrace();
            }
        }
        return params;
    }
}
