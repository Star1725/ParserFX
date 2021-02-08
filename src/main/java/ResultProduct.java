import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultProduct extends Product{
    private String category;
    private String myVendorCode;
    private int myPriceU;
    private int myBasicSale;
    private int myBasicPriceU;
    private int myPromoSale;
    private int myPromoPriceU;
    private int recommendedPriceU;
    private int recommendedSale;

    public ResultProduct(
            String vendorCodeFromRequest,
            String category,
            String brand,
            String myVendorCode,
            int myPriceU,
            int myBasicSale,
            int myBasicPriceU,
            int myPromoSale,
            int myPromoPriceU,
            String vendorCode,
            String productName,
            String refForPage,
            String refForImage,
            int priceU,
            int basicSale,
            int basicPriceU,
            int promoSale,
            int promoPriceU,
            String specAction,
            int rating,
            String refForRequest,
            int recommendedPriceU,
            int recommendedSale) {
        super(vendorCodeFromRequest, brand, vendorCode, productName, refForPage, refForImage, priceU, basicSale, basicPriceU, promoSale, promoPriceU, specAction, rating, refForRequest);
        this.category = category;
        this.myVendorCode = myVendorCode;
        this.myPriceU = myPriceU;
        this.myBasicSale = myBasicSale;
        this.myBasicPriceU = myBasicPriceU;
        this.myPromoSale = myPromoSale;
        this.myPromoPriceU = myPromoPriceU;
        this.recommendedPriceU = recommendedPriceU;
        this.recommendedSale = recommendedSale;
    }

    public int getMyLowerPriceU(){
        if (this.myPromoPriceU != 0){
            return myPromoPriceU;
        } else if (this.myBasicPriceU != 0){
            return myBasicPriceU;
        } else {
            return myPriceU;
        }
    }

    public int getMyLowerSale(){
        if (this.myPromoSale != 0){
            return myPromoSale;
        } else {
            return myBasicSale;
        }
    }
}
