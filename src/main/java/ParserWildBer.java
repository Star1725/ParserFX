import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ParserWildBer {

    //private static List<Product> productList;

    public static List<Product> extracted(String query) throws IOException {
        Document page = getPage(query);
        Element catalog = page.select("div[class=catalog_main_table j-products-container]").first();
        return getProductListForCatalog(catalog);
    }

    private static Document getPage(String query) throws IOException {
        String queryUTF8 = URLEncoder.encode(query, "UTF-8");
        System.out.println(queryUTF8);

        //замена символа "+" на код "%20"
        queryUTF8 = queryUTF8.replace("+", "%20");
        System.out.println(queryUTF8);

        String url = "https://www.wildberries.ru/catalog/0/search.aspx?search=" + queryUTF8 + "&sort=priceup";

        Document page = Jsoup.parse(new URL(url), 5000);
        return page;
    }

    private static List<Product> getProductListForCatalog(Element catalog){
        Elements goods = catalog.select("div[class=dtList i-dtList j-card-item]");
        List<Product> productList = new ArrayList<>();
        int i = 0;
        for (Element good : goods) {
            String id = good.attr("data-popup-nm-id");

            Element fullProductCard = good.select("a[class=ref_goods_n_p j-open-full-product-card]").first();
            String href = "https://www.wildberries.ru" + fullProductCard.attr("href");

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
            Element priceGoods = fullProductCard.select("ins[class=lower-price]").first();
            int lowerPrice;
            try {
                String[] p = priceGoods.text().split(" ");
                lowerPrice = Integer.parseInt(p[0]);
            } catch (Exception e) {
                priceGoods = fullProductCard.select("span[class=lower-price]").first();
                String[] p = priceGoods.text().split(" ");
                lowerPrice = Integer.parseInt(p[0]);
            }

            //старая цена и скидка
            priceGoods = fullProductCard.select("span[class=price-old-block]").first();
            int oldPrice = 0;
            int sale = 0;
            try {
                String oldPriceAndSale = priceGoods.text();
                System.out.println(oldPriceAndSale);
                String[] arrayWithOldPriceAndSale = oldPriceAndSale.split(" ₽ -");
                for (String s : arrayWithOldPriceAndSale) {
                    System.out.println(s);
                }
                oldPrice = Integer.parseInt(arrayWithOldPriceAndSale[0]);
                sale = Integer.parseInt(arrayWithOldPriceAndSale[1].replaceFirst(".$",""));
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
            String brandName = brand.text();

            //имя товара
            Element nameGoods = fullProductCard.select("span[class=goods-name c-text-sm]").first();
            String name = nameGoods.text();

            productList.add(new Product(Integer.parseInt(id), brandName, name, href, refForImg, lowerPrice, oldPrice, sale, specAction, rating));

        }
        return productList;
    }


}
