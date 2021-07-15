import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import models.modelCatologForWB.RequestWB;
import models.modelForDescriptionAndParamsForOzon.Characteristics;
import models.modelForDescriptionAndParamsForOzon.RequestOzon;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpUrlConnectionWorkerJSON {

    public static List<Product> getCatalogForWB(List<Product> productList, String query){
        RequestWB requestWB;

        String ids = "";
        for (Product product : productList) {
            ids = ids + product.getCompetitorVendorCode() + ";";
        }

        ids = ids.substring(0, ids.length() - 1);

        try {
            URL uri1 = new URL(Constants.URL_FOR_JSON_FOR_WB + ids);
            HttpsURLConnection httpsURLConnection = null;
            httpsURLConnection = (HttpsURLConnection) uri1.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setReadTimeout(30000);
            BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String result = getLines(in);
            in.close();
            Gson gson = new Gson();
            requestWB = gson.fromJson(result, RequestWB.class);
            for (Product product : productList) {
                for (int i = 0; i < requestWB.getData().getProducts().length; i++) {
                    if (Integer.parseInt(product.getCompetitorVendorCode()) == (requestWB.getData().getProducts()[i].getId())){
                        product.setCompetitorPriceU(requestWB.getData().getProducts()[i].getPriceU());

                        if (requestWB.getData().getProducts()[i].getExtended() != null){
                            product.setCompetitorBasicSale(requestWB.getData().getProducts()[i].getExtended().getBasicSale());
                            product.setCompetitorBasicPriceU(requestWB.getData().getProducts()[i].getExtended().getBasicPriceU());
                            product.setCompetitorPromoSale(requestWB.getData().getProducts()[i].getExtended().getPromoSale());
                            product.setCompetitorPromoPriceU(requestWB.getData().getProducts()[i].getExtended().getPromoPriceU());
                        }
                    }
                }
            }
            return productList;
        } catch (IOException e) {
            System.out.println("Для запроса - " + query + " Json выдаёт ошибку \"" + e.getMessage() + "\"");
            return productList;
        }
    }

    public static String getDescriptionsForOzon(String refForPageProduct, List<String> typeOfCharacteristic){
        RequestOzon requestOzon;
        String urlForJsonWithDescription = "https://www.ozon.ru/api/composer-api.bx/page/json/v2?url=" + refForPageProduct + "&layout_container=pdpPage2column&layout_page_index=2";

        //            URL uri1 = new URL(urlForJsonWithDescription);
//            HttpsURLConnection httpsURLConnection = null;
//            httpsURLConnection = (HttpsURLConnection) uri1.openConnection();
//            httpsURLConnection.setRequestMethod("GET");
//            httpsURLConnection.setReadTimeout(10000);
//            BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));

        Page page = SupplierHtmlPage.getOzonPageFromPlaywrightWithoutJavaScript(urlForJsonWithDescription);
        HtmlPage htmlPage = SupplierHtmlPage.getOzonPageFromHtmlUnit(urlForJsonWithDescription);

        HtmlElement element = htmlPage.getBody();

        String bodyForElement = element.asText();


        ElementHandle elementHandle = page.querySelector("body");

        String bodyPage = elementHandle.innerText();
//        bodyPage = bodyPage.replace("\"", "").replaceAll("\"", ":{").replaceAll("}\"", "}");
        String bodyPageAfter = bodyPage.replace("\\\\\"", "").replace("\\", "").replace(":\"{", ":{").replace("}\"", "}");

//            String result1 = getLines(in);

//            in.close();
        Gson gson = new Gson();
        requestOzon = gson.fromJson(bodyPageAfter, RequestOzon.class);
        System.out.println("получили json от Ozon с описанием товара - " + requestOzon.getWidgetStates().getDescription().getRichAnnotation());
        String description = requestOzon.getWidgetStates().getDescription().getRichAnnotation();
                Characteristics characteristics = null;
        if (requestOzon.getWidgetStates().getCharacteristics_545936() != null){
            characteristics = requestOzon.getWidgetStates().getCharacteristics_545936();
        } else if (requestOzon.getWidgetStates().getCharacteristics_567867() != null){
            characteristics = requestOzon.getWidgetStates().getCharacteristics_567867();
        }
        String param = "Выходные интерфейсы";

        for (int i = 0; i < characteristics.getInnerCharacteristics().length; i++) {
            for (int j = 0; j < characteristics.getInnerCharacteristics()[i].getShortCharacteristics().length; j++) {

                for (String type : typeOfCharacteristic) {
                    if (characteristics.getInnerCharacteristics()[i].getShortCharacteristics()[j].getName().contains(type)) {
                        if (type.contains("Длина, м")) {
                            description = description + " " + characteristics.getInnerCharacteristics()[i].getShortCharacteristics()[j].getValues()[0].getText() + " м";
                        } else {
                            description = description + " " + characteristics.getInnerCharacteristics()[i].getShortCharacteristics()[j].getValues()[0].getText();
                        }
                    }
                }
            }
        }

        page.close();

        return description;
    }

    private static String getLines(BufferedReader in){
        return in.lines().collect(Collectors.joining("\n"));
    }
}
