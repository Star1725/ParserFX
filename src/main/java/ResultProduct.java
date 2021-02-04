import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultProduct extends Product{
    private String myVendorCode;
    private String category;
    private double myLoverPrice;
    private double myPriceU;
    private int mySale;
    private int myPromoSale;
    private double recommendedPrice;
    private int recommendedSale;

    public ResultProduct(String myVendorCode,
                        String vendorCode,
                         String category,
                         String brand,
                         String productName,
                         String refForPage,
                         String refForImage,
                         double lowerPrice,
                         double price,
                         String specAction,
                         int rating,
                         String refForRequest,
                         double myLoverPrice,
                         double myPriceU,
                         int mySale,
                         int myPromoSale,
                         double recommendedPrice,
                         int recommendedSale) {
        super(vendorCode, brand, productName, refForPage, refForImage, lowerPrice, price, specAction, rating, refForRequest);
        this.myVendorCode = myVendorCode;
        this.category = category;
        this.myLoverPrice = myLoverPrice;
        this.myPriceU = myPriceU;
        this.mySale = mySale;
        this.myPromoSale = myPromoSale;
        this.recommendedPrice = recommendedPrice;
        this.recommendedSale = recommendedSale;
    }
}
