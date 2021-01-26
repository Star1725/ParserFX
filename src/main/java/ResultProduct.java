import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResultProduct extends Product{

    private double myLoverPrice;
    private double myPrice;
    private int mySale;
    private int myPromoSale;
    private double recommendedPrice;
    private int recommendedSale;

    public ResultProduct(int vendorCode,
                         String brand,
                         String productName,
                         String refForPage,
                         String refForImage,
                         double lowerPrice,
                         double price,
                         int sale,
                         String specAction,
                         int rating) {
        super(vendorCode, brand, productName, refForPage, refForImage, lowerPrice, price, sale, specAction, rating);
    }

    public ResultProduct(int vendorCode, String brand, String productName, String refForPage, String refForImage, double lowerPrice, double price, int sale, String specAction, int rating,
                         double myLoverPrice,
                         double myPrice,
                         int mySale,
                         int myPromoSale,
                         double recommendedPrice,
                         int recommendedSale) {
        super(vendorCode, brand, productName, refForPage, refForImage, lowerPrice, price, sale, specAction, rating);
        this.myLoverPrice = myLoverPrice;
        this.myPrice = myPrice;
        this.mySale = mySale;
        this.myPromoSale = myPromoSale;
        this.recommendedPrice = recommendedPrice;
        this.recommendedSale = recommendedSale;
    }
}
