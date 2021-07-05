import com.gargoylesoftware.htmlunit.html.*;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import net.sourceforge.htmlunit.cyberneko.HTMLElements;
import org.w3c.dom.html.HTMLDListElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHTMLForOzon {

    static List<Product> getCatalogFromFPageForHtmlUnit(String url, String productType, String brand, String model, List<List<String>> arrayParams) {
        List<Product> productList = new ArrayList<>();
        HtmlPage page = null;
        String stringPage = null;
        String querySearchAndCount = "-";
        String category = "-";
        int versionPage = 1;

        System.out.println("IP №" + LowerProductFinder.countSwitchIP + ".Получение страницы ozon для запроса - " + LowerProductFinder.myQuery + ". Артикул Ozon - " + LowerProductFinder.myVendorCodeFromRequest);
        boolean isNotGetValidPage = true;
        while (isNotGetValidPage){
            //получение страницы поискового запроса с аналогами
            page = SupplierHtmlPage.getHtmlPage(url);

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
                    LowerProductFinder.resultSearch = querySearchAndCount;
                    System.out.println(Constants.getYellowString(url));
                    System.out.println(Constants.getRedString(querySearchAndCount));
                    //System.out.println(page.asXml());
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
                    System.out.println(page.asXml());
                    HtmlBody htmlBody = (HtmlBody) page.getBody();
                    String hrefForNewCatalog = htmlBody.getTextContent();
                    System.out.println(hrefForNewCatalog);
                    String[] strBuff = hrefForNewCatalog.split("=", 2);
                    String temp = strBuff[1].substring(strBuff[1].indexOf("\"") + 1, strBuff[1].lastIndexOf("\""));
                    String newUrlForNewCatalog = "https://www.ozon.ru" + temp;
                    url = newUrlForNewCatalog;
                    versionPage = 2;
                    continue;
                }
            }

            if (querySearchAndCount.contains("товаров сейчас нет")){
                productList.add(new Product("Запрос - " + LowerProductFinder.myQuery + ". " + querySearchAndCount, "-1"));
                return productList;
            }

            //получаем первые 4 продукта, полученные по поисковому запросу аналогов
//            List<HtmlElement> itemsForListProducts1 = page.getByXPath("//div[@class='a0c6 a0c9']");
            List<HtmlElement> itemsForListProducts = page.getByXPath("//div[@class='a0c4']");
//            boolean isException1 = false;
//            boolean isException2 = false;
//            boolean isException3 = false;
//            boolean isException4 = false;
//            boolean isException5 = false;
//            boolean isException6 = false;
            if (itemsForListProducts.isEmpty()) {

            } else {

                int item = 0;

                for (HtmlElement itemProduct: itemsForListProducts) {
                    String competitorBrand = "-";
                    String refForItemProduct = "-";
                    String refImageForItemProduct = "-";
                    String vendorCode = "-";
                    String productDescription = "-";
                    String sellerItemProduct = "-";
                    int intSale = 0;
                    int competitorBasicPriceU = 0;
                    int competitorPriceU = 0;
                    int competitorPremiumPriceForOzon = 0;

                    System.out.println("анализ " + (item + 1) + " продукта из первых " + itemsForListProducts.size());

//                    System.out.println("его код в html: ");
//                    System.out.println(itemProduct.asXml());

                    if (versionPage == 1){

                    }

/////////////////////определяем не уценённый ли продукт
                    boolean isDiscountedItem = false;
                    if (checkDiscountedItemProduct(item, itemProduct)) continue;

/////////////////////получение ссылки на продукт
//                    System.out.println("Ссылки на продукты:");
//                    List<HtmlElement> as_href_forItemProduct = page.getByXPath("//a[@class='a0v2 tile-hover-target']");
//                    for (HtmlElement htmlElement: as_href_forItemProduct){
//                        System.out.println("- " + "https://www.ozon.ru" + htmlElement.getAttributes().getNamedItem("href").getNodeValue());
//                    }
                    refForItemProduct = getString_href_forItemProduct(versionPage, item, itemProduct);

/////////////////////получение ссылки на картинку продукта
                    String xpathExpr = null;
                    if (versionPage == 1){
                        xpathExpr = "//div[@class='a0i7']";
                    } else if (versionPage == 2){
                        xpathExpr = "//div[@class='a0i7 a0i8']";
                    }
                    HtmlDivision div_src_imageForItemProduct = (HtmlDivision) itemProduct.getByXPath(xpathExpr).get(item);
                    refImageForItemProduct = div_src_imageForItemProduct.getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
                    System.out.println("ссылка на картинку продукта № " + (item + 1) + " = " + refImageForItemProduct);

/////////////////////получение артикула ozon для продукта
                    vendorCode = getVendorCodeForItemProduct_Ozon(refForItemProduct, vendorCode);
                    System.out.println("артикул по ozon на продукт № " + (item + 1) + " = " + vendorCode);

/////////////////////получение описания продукта (одинаковое название класса для обеих версий страниц)
                    HtmlAnchor a_descriptionItemProduct = (HtmlAnchor) itemProduct.getByXPath("//a[@class='tile-hover-target b3u9']").get(item);
                    String descriptionItemProduct = a_descriptionItemProduct.getTextContent();
                    System.out.println("описание продукта № " + (item + 1) + " = " + descriptionItemProduct);

/////////////////////получение цен на продукт
                    String pricesItemProduct = getString_pricesItemProduct(versionPage, item, itemProduct);
                    String[] tempPricesItemProduct = pricesItemProduct.split("₽");
                    switch (tempPricesItemProduct.length){
                        case 2:
                            competitorPriceU = getIntegerFromString(tempPricesItemProduct[0]) * 100;
                            System.out.println("цена продукта одна - " + competitorPriceU);
                            break;
                        case 3:
                            competitorPriceU = getIntegerFromString(tempPricesItemProduct[0]) * 100;
                            System.out.println("цена после скидки - " + competitorPriceU);
                            competitorBasicPriceU = getIntegerFromString(tempPricesItemProduct[1]) * 100;
                            System.out.println("цена до скидки - " + competitorBasicPriceU);
                            break;
                        case 4:
                            competitorPriceU = getIntegerFromString(tempPricesItemProduct[0]) * 100;
                            System.out.println("цена после скидки - " + competitorPriceU);
                            competitorBasicPriceU = getIntegerFromString(tempPricesItemProduct[1]) * 100;
                            System.out.println("цена до скидки - " + competitorBasicPriceU);
                            competitorPremiumPriceForOzon = getIntegerFromString(tempPricesItemProduct[2]) * 100;
                            System.out.println("премиумная цена - " + competitorPremiumPriceForOzon);
                            break;
                    }

/////////////////////получение имени продовца продукта
                    sellerItemProduct = getString_sellerItemProduct(versionPage, item, itemProduct);

                    item++;

                    productList.add(new Product(
                            "-",
                            "-",
                            "-",
                            "-",
                            "-",

                            LowerProductFinder.resultSearch,
                            LowerProductFinder.refUrlForResult,

                            competitorBrand,
                            vendorCode,
                            descriptionItemProduct,
                            refForItemProduct,
                            refImageForItemProduct,
                            "-",
                            0,
                            competitorPriceU,
                            intSale,
                            competitorBasicPriceU,
                            0,
                            0,
                            competitorPremiumPriceForOzon,

                            sellerItemProduct
                    ));






//                    //получение ссылки на продукт
//                    try {
//                        refForItemProduct = "https://www.ozon.ru" + itemProduct.getFirstChild().getAttributes().getNamedItem("href").getNodeValue();
//                        versionPage = 1;
//                    } catch (NullPointerException e) {
//                        refForItemProduct = "https://www.ozon.ru" + itemProduct.getFirstChild().getFirstChild().getFirstChild().getAttributes().getNamedItem("href").getNodeValue();
//                        versionPage = 2;
//                    }
//
//                    vendorCode = getVendorCodeForItemProduct_Ozon(refForItemProduct, vendorCode);
//
//                    //исчем 3 элемента,
//                    // если versionPage = 1, то 1 элемент - ссылка на картинку, 2 элемент - цены, описание, продавец
//                    // если versionPage = 2, то 1 элемент - ссылка на картинку, 2 элемент - описание, продавец, 3 элемент - цены
//                    Iterable<DomElement> elementsFor_a0c4 = null;
//                    try {
//                        elementsFor_a0c4 = getDomElements(itemProduct);
//                    } catch (Exception ignored) {
//                        //e.printStackTrace();
//                        System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
//                        isException6 = true;
//                        System.out.println("isException6 = " + isException6);
//                    }
//
//                    int childFor_a0c4 = 1;
//
//                    for (DomElement elementFor_a0t0 : elementsFor_a0c4) {
//                        //1 элемент - получение ссылки на картинку
//                        if (childFor_a0c4 == 1) {
//                            //refImageForItemProduct = elementFor_a0t0.getFirstChild().getFirstChild().getFirstChild().getAttributes().getNamedItem("src").getNodeValue();
//                            refImageForItemProduct = elementFor_a0t0.getElementsByTagName("img").get(0).getAttributes().getNamedItem("src").getNodeValue();
//                        }
//
//                        //2 элемент
//                        if (childFor_a0c4 == 2) {
//                            DomNodeList<HtmlElement> asFor_a0s9 = elementFor_a0t0.getElementsByTagName("a");
//                            //определяем не уценённый ли товар
//                            DomNodeList<HtmlElement> divsFor_a0s9 = elementFor_a0t0.getElementsByTagName("div");
//                            for (HtmlElement element: divsFor_a0s9){
//                                if (element.asText().contains(discountedItem)) {
//                                    System.out.println(discountedItem);
//                                    isDiscountedItem = true;
//                                    break;
//                                }
//                            }
//                            if (isDiscountedItem){
//                                break;
//                            }
//                            /////////////////////////////////////////////
//                            if (versionPage == 1) {//цены, описание, продавец
//                                //получение цен: currentBasicPriceString, competitorPriceU
//                                DomNodeList<HtmlElement> divsFor_a0y9 = null;
//                                DomNodeList<HtmlElement> elementsFor_b5v4 = null;
//                                String currentBasicPriceString = "0";
//                                try {
//                                    divsFor_a0y9 = asFor_a0s9.get(0).getElementsByTagName("div");
//                                    elementsFor_b5v4 = divsFor_a0y9.get(0).getElementsByTagName("span");
//                                    currentBasicPriceString = elementsFor_b5v4.get(0).asText();
//                                } catch (Exception ignored) {
////                                    if (divsFor_a0y9.size() == 5){
////                                        elementsFor_b5v4 = divsFor_a0y9.get(2).getElementsByTagName("span");
////                                        currentBasicPriceString = elementsFor_b5v4.get(0).asText();
////                                    }
////                                    if (divsFor_a0y9.size() == 6){
////                                        elementsFor_b5v4 = divsFor_a0y9.get(3).getElementsByTagName("span");
////                                        currentBasicPriceString = elementsFor_b5v4.get(0).asText();
////                                    }
//                                }
//                                competitorBasicPriceU = getIntegerFromString(currentBasicPriceString) * 100;
//
////получение цены currentPriceUString
//                                try {
//                                    String currentPriceUString = elementsFor_b5v4.get(1).asText();
//                                    competitorPriceU = getIntegerFromString(currentPriceUString) * 100;
//                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
////                                    isException2 = true;
////                                    System.out.println("isException2 = " + isException2);
//                                }
//
////получение цены premiumPriceString
//                                try {
//                                    //пробуем получить премиум цену, если есть
//                                    String premiumPriceString = divsFor_a0y9.get(divsFor_a0y9.size() - 1).asText();
//                                    if (premiumPriceString.contains("Premium")) {
//                                        competitorPremiumPriceForOzon = getIntegerFromString(premiumPriceString) * 100;
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
//                                    isException3 = true;
//                                    System.out.println("isException3 = " + isException3);
//                                }
//
////получение описания продукта
////                                productDescription = asFor_a0s9.get(1).asText();
////                                productDescription = getStringLengthForCable(brand, model, arrayParams, refForItemProduct, productDescription);
//
////определяем какой бренд
////                                competitorBrand = "-";
////                                for (String s : Constants.listForBrands) {
////                                    if (productDescription.contains(s)) {
////                                        competitorBrand = s;
////                                        break;
////                                    }
////                                }
//
//                                //получение имени продавца
//                                DomNodeList<HtmlElement> spanFor_a0s9 = elementFor_a0t0.getElementsByTagName("span");
//                                //DomNodeList<HtmlElement> spansFor_a0t6 = divsFor_a0s9.get(1).getElementsByTagName("span");
//                                sellerItemProduct = spanFor_a0s9.get(spanFor_a0s9.size() - 1).asText();
//                            }
//                            if (versionPage == 2) {//описание, продавец
//                                //получение описания продукта
//                                productDescription = asFor_a0s9.get(0).asText();
//                                productDescription = getStringLengthForCable(brand, model, arrayParams, refForItemProduct, productDescription);
//                                //получение имени продавца
//                                DomNodeList<HtmlElement> spanFor_a0s9 = elementFor_a0t0.getElementsByTagName("span");
//                                //DomNodeList<HtmlElement> spansFor_a0t6 = divsFor_a0s9.get(1).getElementsByTagName("span");
//                                sellerItemProduct = spanFor_a0s9.get(spanFor_a0s9.size() - 1).asText();
//                            }
//                        }
//                        //3 элемент
//                        if (childFor_a0c4 == 3){
//                            DomNodeList<HtmlElement> asFor_a0s9 = elementFor_a0t0.getElementsByTagName("a");
//                            if (versionPage == 2){// - цены
//                                //получение цен: currentBasicPriceString, competitorPriceU
//                                DomNodeList<HtmlElement> spanFor_a0t0 = asFor_a0s9.get(0).getElementsByTagName("span");
//                                String currentBasicPriceString = spanFor_a0t0.get(0).asText();
//                                competitorBasicPriceU = getIntegerFromString(currentBasicPriceString) * 100;
//
//                                //получение цены currentPriceUString
//                                try {
//                                    String currentPriceUString = spanFor_a0t0.get(1).asText();
//                                    competitorPriceU = getIntegerFromString(currentPriceUString) * 100;
//                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
////                                    isException4 = true;
////                                    System.out.println("isException4 = " + isException4);
//                                }
//
//                                //получение цены premiumPriceString
//                                try {
//                                    DomNodeList<HtmlElement> divsFor_a0y9 = asFor_a0s9.get(0).getElementsByTagName("div");
//                                    //пробуем получить премиум цену, если есть
//                                    String premiumPriceString = divsFor_a0y9.get(divsFor_a0y9.size() - 1).asText();
//                                    if (premiumPriceString.contains("Premium")) {
//                                        competitorPremiumPriceForOzon = getIntegerFromString(premiumPriceString) * 100;
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    System.out.println("////////////////////////////////////////Невалидная страница///////////////////////////////////////////");
//                                    isException5 = true;
//                                    System.out.println("isException5 = " + isException5);
//                                }
//                            }
//                        }
//                        childFor_a0c4++;
//                    }
//                    if (isDiscountedItem){
//                        continue;
//                    }
//
//                    productList.add(new Product(
//                            "-",
//                            "-",
//                            "-",
//                            "-",
//                            "-",
//
//                            LowerProductFinder.resultSearch,
//                            LowerProductFinder.refUrlForResult,
//
//                            competitorBrand,
//                            vendorCode,
//                            productDescription,
//                            refForItemProduct,
//                            refImageForItemProduct,
//                            "-",
//                            0,
//                            competitorPriceU,
//                            intSale,
//                            competitorBasicPriceU,
//                            0,
//                            0,
//                            competitorPremiumPriceForOzon,
//
//                            sellerItemProduct
//                    ));






                }
            }
            isNotGetValidPage = false;
//            isNotGetValidPage = isException1 || isException2 || isException3 || isException4 || isException5 || isException6;
//            if (isNotGetValidPage){
//                System.out.println("//////////////////////////////////////Попытка получения новой валидной страницы//////////////////////////////////////");
//                System.out.println("isNotGetValidPage = " + isNotGetValidPage);
//            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//*TO-DO. Получаем остальные 35 продукта, полученные по поисковому запросу аналогов//////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //получаем кол-во элементов javascript
            List<HtmlElement> itemsCountSearchJavascript = page.getByXPath("//div[@class='a1j1']");
            if (itemsCountSearchJavascript == null) {
                System.out.println("не нашёл html-элемент javascript - div[@class='a1j1']");
            } else {
                try {
                    int countJavascript = itemsCountSearchJavascript.size();
                    int countForAnalise = 0;
                    int notValid = 0;
                    if (countJavascript > 35 && (arrayParams.size() == 2 || arrayParams.size() == 1)) {
                        countForAnalise = 35;
                    } else if (arrayParams.size() == 0){
                        countForAnalise = Math.min(countJavascript, 35);
                    } else {
                        countForAnalise = countJavascript;
                    }
                    System.out.println("Получение " + countForAnalise + " товаров-аналогов для \"" + model + "\" через загрузку и обработку их ссылок");
                    for (int i = 0; i < countForAnalise; i++) {
                        String description = itemsCountSearchJavascript.get(i).asText().toLowerCase();
                        //проходимся только по тем товарам, в названии которых есть наша модель и наши дополнительные параметры
                        System.out.println(Constants.getBlueString(String.valueOf(i + 1)) + " - " + description);
//                        if (description.toLowerCase().contains(model + ",")
//                                || description.toLowerCase().contains(model + " ")
//                                || description.toLowerCase().contains("(" + model + ")")
//                                || description.toLowerCase().contains(", " + model + "")
//                                || description.toLowerCase().contains(" " + model + "")) {

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
                                case Constants.PRODUCT_TYPE_1C_62:
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
                                            listWithCableParam_1 = Constants.getCollectionsParamCable(arrayParams.get(0).get(0), brand + model);
                                        } else {
                                            listWithCableParam_1 = Constants.getCollectionsParamCable(arrayParams.get(0).get(0), brand + model);
                                            listWithCableParam_2 = Constants.getCollectionsParamCable(arrayParams.get(1).get(0), brand + model);
                                        }
                                        int check1 = 0;
                                        int check2 = 10;
                                        for (String s1 : listWithCableParam_1) {
                                            param1 = s1;
                                            if (description.contains(s1)) {
                                                if (listWithCableParam_2 != null) {
                                                    for (String s2 : listWithCableParam_2) {
                                                        param2 = s2;
                                                        if (description.contains(s2)) {
                                                            check2++;
                                                            break;
                                                        } else {
                                                            for (String length: Constants.listForCableAllLength){
                                                                if (!description.contains(length)){
                                                                    check2++;
                                                                    check1++;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (check2 != 0) {
                                                        break;
                                                    }
                                                } else {
                                                    check1++;
                                                    break;
                                                }
                                            }
                                        }
                                        if (check1 == 0) {
                                            System.out.println("не прошёл, тук как нет доп. параметра поиска = " + Constants.getRedString(param1));
                                            continue;
                                        }
                                        if (arrayParams.size() == 2){
                                            if (check2 == 10) {
                                                System.out.println("не прошёл, тук как нет доп. параметра поиска = " + Constants.getRedString(param2));
                                                continue;
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Ошибка при обработке списка аналогов на поиск по параметру");
                                    }
                                    break;
                                case Constants.PRODUCT_TYPE_1C_139:
                                case Constants.PRODUCT_TYPE_1C_93:
                                case Constants.PRODUCT_TYPE_1C_136:
                                case Constants.PRODUCT_TYPE_1C_167:
                                case Constants.PRODUCT_TYPE_1C_168:
                                case Constants.PRODUCT_TYPE_1C_92:
                                    System.out.println("Тип продукта - " + productType);
                                    try {
                                        if (arrayParams.size() == 1) {
                                            if (!description.contains(arrayParams.get(0).get(0))) {
                                                System.out.println("не прошёл, тук как нет доп. параметра поиска = " + Constants.getRedString(arrayParams.get(0).get(0)));
                                                continue;
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Ошибка при обработке списка аналогов на поиск по параметру");
                                    }
                                    break;
                                default:
                                    System.out.println("Тип продукта - " + productType);
                                    if (arrayParams.size() == 0){
                                        if (!description.contains(model)) {
                                            System.out.println("не прошёл, так как не соответствует тип модели = " + Constants.getRedString(model));
                                            continue;
                                        }
                                    }
                            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            String refForImage = "-";
                            String vendorCode = "-";
                            String seller = "-";
                            String discountedItem = "Уценённый товар";
                            //получение ссылки на продукт
                            String hRefProduct = "https://www.ozon.ru" + itemsCountSearchJavascript.get(i).getFirstChild().getAttributes().getNamedItem("href").getNodeValue();
                            page = SupplierHtmlPage.getHtmlPage(hRefProduct);

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
//для кабелей ищем длинну
                            switch (productType){
                                case Constants.PRODUCT_TYPE_1C_48:
                                case Constants.PRODUCT_TYPE_1C_49:
                                case Constants.PRODUCT_TYPE_1C_50:
                                case Constants.PRODUCT_TYPE_1C_61:
                                case Constants.PRODUCT_TYPE_1C_62:
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
                                        List<HtmlElement> dListElement = page.getByXPath("//dl[@class='db8']");
                                        for (HtmlElement db: dListElement){
                                            if (db.asText().contains("Длина")){
                                                String length = db.getElementsByTagName("dd").get(0).asText();
                                                description = description + " " + length;
                                            }
                                        }
                                    } catch (Exception ignored) {
                                        ignored.printStackTrace();
                                    }
                            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

                        vendorCode = getVendorCodeForItemProduct_Ozon(hRefProduct, vendorCode);

                        productList.add(new Product(
                                    "-",
                                    "-",
                                    "-",
                                    "-",
                                    "-",

                                    LowerProductFinder.resultSearch,
                                    LowerProductFinder.refUrlForResult,

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

                            if (!seller.equals(Constants.MY_SELLER) || !seller.equals(Constants.MY_SELLER_2)){
                                System.out.println(Constants.getRedString("Найден аналог конкурента!!!!!!!!!!!!!!!!!!!!!!!!!"));
                                break;
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

    private static boolean checkDiscountedItemProduct(int item, HtmlElement itemProduct) {
        boolean isDiscountedItem;
        String discountedItem = "Уцененный товар";
        HtmlDivision division_fullInformationItemProduct = (HtmlDivision) itemProduct.getByXPath("//div[@class='a0c4']").get(item);
        String fullInformationItemProduct = division_fullInformationItemProduct.getTextContent();
        if (fullInformationItemProduct.contains(discountedItem)) {
            System.out.println(Constants.getRedString("продукт № " + (item + 1) + " - уценённый"));
            isDiscountedItem = true;
            //если продукт уценённый, пропускаем его
            return true;
        }
        return false;
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
            String vendorCodeBuff = arrayBuff2[arrayBuff2.length - 1];
            String[] arrayBuff3 = vendorCodeBuff.split("/");
            vendorCode = arrayBuff3[0];
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
                    HtmlPage pageProduct = SupplierHtmlPage.getHtmlPage(refForProduct);
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
