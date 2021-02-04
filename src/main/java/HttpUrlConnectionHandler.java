import com.google.gson.Gson;
import model.Request;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class HttpUrlConnectionHandler {

    public static void getCatalog(List<Product> productList){
        Request request;
        int count = productList.size();
        if (count > 1){
            String ids = "";
            for (Product product : productList) {
                if (count != 1){
                    ids = ids + product.getVendorCode() + ";";
                    count--;
                } else {
                    ids = ids + product.getVendorCode();
                }
            }
            try {
                String url = "https://wbxcatalog-ru.wildberries.ru/nm-2-card/catalog?spp=0&regions=69,58,64,40,48,70,1,38,4,30,22,66&" +
                        "stores=119261,121631,1193,119400,116433,117501,507,3158,120762,1733,117986,686,117413,119781&" +
                        "couponsGeo=2,12,6,9&pricemarginCoeff=1.0&reg=0&appType=1&offlineBonus=0&onlineBonus=0&emp=0&locale=ru&lang=ru&nm=" +
                        ids;
                URL uri1 = new URL(url);
                HttpsURLConnection httpsURLConnection = null;

                httpsURLConnection = (HttpsURLConnection) uri1.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.setReadTimeout(30000);

//                httpsURLConnection.setRequestProperty("Host", "www.wildberries.ru");
//                httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
//                httpsURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
//                httpsURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//                httpsURLConnection.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
//                httpsURLConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
//                httpsURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
//                httpsURLConnection.setRequestProperty("Referer", "https://www.wildberries.ru/");
//                httpsURLConnection.setRequestProperty("Connection", "keep-alive");
//                httpsURLConnection.setRequestProperty("Pragma", "no-cache");
//                httpsURLConnection.setRequestProperty("Cache-Control", "no-cache");
//                httpsURLConnection.setRequestProperty("Content-Length", "0");


                BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                String result = getLines(in);
                in.close();
                Gson gson = new Gson();
                request = gson.fromJson(result, Request.class);

                for (Product product : productList) {
                    for (int i = 0; i < request.getData().getProducts().length; i++) {
                        if (product.getVendorCode().equals(request.getData().getProducts()[i])){

                        }
                    }
                }

                productList = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static String getLines(BufferedReader in){
        return in.lines().collect(Collectors.joining("\n"));
    }
}
