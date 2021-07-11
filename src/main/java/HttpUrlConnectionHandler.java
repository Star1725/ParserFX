import com.google.gson.Gson;
import model.Request;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class HttpUrlConnectionHandler {

    public static List<Product> getCatalog(List<Product> productList, String query){
        Request request;

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
//               httpsURLConnection.setRequestProperty("Host", "www.wildberries.ru");
//               httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
//               httpsURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
//               httpsURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//               httpsURLConnection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
//               httpsURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
//               httpsURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
//               httpsURLConnection.setRequestProperty("Referer", "https://www.wildberries.ru/");
//               httpsURLConnection.setRequestProperty("Connection", "keep-alive");
//               httpsURLConnection.setRequestProperty("Pragma", "no-cache");
//               httpsURLConnection.setRequestProperty("Cache-Control", "no-cache");
//               httpsURLConnection.setRequestProperty("Content-Length", "0");
            BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
            String result = getLines(in);
            in.close();
            Gson gson = new Gson();
            request = gson.fromJson(result, Request.class);
            for (Product product : productList) {
                for (int i = 0; i < request.getData().getProducts().length; i++) {
                    if (Integer.parseInt(product.getCompetitorVendorCode()) == (request.getData().getProducts()[i].getId())){
                        product.setCompetitorPriceU(request.getData().getProducts()[i].getPriceU());

                        if (request.getData().getProducts()[i].getExtended() != null){
                            product.setCompetitorBasicSale(request.getData().getProducts()[i].getExtended().getBasicSale());
                            product.setCompetitorBasicPriceU(request.getData().getProducts()[i].getExtended().getBasicPriceU());
                            product.setCompetitorPromoSale(request.getData().getProducts()[i].getExtended().getPromoSale());
                            product.setCompetitorPromoPriceU(request.getData().getProducts()[i].getExtended().getPromoPriceU());
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

    private static String getLines(BufferedReader in){
        return in.lines().collect(Collectors.joining("\n"));
    }
}
