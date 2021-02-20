import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultProduct extends Product{
    private String brand;
    private String category;
    private String code_1C;
    private String myVendorCodeForWildberies;
    private String vendorCode_1C;
    private int specPrice;
    private int myPriceU;
    private int myBasicSale;
    private int myBasicPriceU;
    private int myPromoSale;
    private int myPromoPriceU;

    private int recommendedPriceU;
    private int recommendedSale;
    private int recommendedPromoSale;

    public ResultProduct(String vendorCodeFromRequest,
                         String myRefForPage,
                         String myRefForImage,
                         String myProductName,
                         String mySpecAction,
                         String brand,
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
                         String queryForSearch) {
        super(vendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction, brand, vendorCode, productName, refForPage, refForImage, priceU, basicSale, basicPriceU, promoSale, promoPriceU, specAction, rating, queryForSearch);
    }


    public ResultProduct(
            String brand,
            String category,
            String code_1C,
            String myVendorCodeForWildberies,
            String vendorCode_1C,
            int specPrice,
            int myPriceU,
            int myBasicSale,
            int myBasicPriceU,
            int myPromoSale,
            int myPromoPriceU,
            int recommendedPriceU,
            int recommendedSale,
            int recommendedPromoSale,

            String vendorCodeFromRequest,
            String myRefForPage,
            String myRefForImage,
            String myProductName,
            String mySpecAction,

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
            String refForRequest
            ) {
        super(vendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction, brand, vendorCode, productName, refForPage, refForImage, priceU, basicSale, basicPriceU, promoSale, promoPriceU, specAction, rating, refForRequest);
        this.brand = brand;
        this.category = category;
        this.code_1C = code_1C;
        this.myVendorCodeForWildberies = myVendorCodeForWildberies;
        this.vendorCode_1C = vendorCode_1C;
        this.specPrice = specPrice;
        this.myPriceU = myPriceU;
        this.myBasicSale = myBasicSale;
        this.myBasicPriceU = myBasicPriceU;
        this.myPromoSale = myPromoSale;
        this.myPromoPriceU = myPromoPriceU;
        this.recommendedPriceU = recommendedPriceU;
        this.recommendedSale = recommendedSale;
        this.recommendedPromoSale = recommendedPromoSale;
    }

    public ResultProduct(String brand, String category, String code_1C, String myVendorCodeWildberies, String vendorCode_1C, int i, int myPriceU, int myBasicSale, int myBasicPriceU, int myPromoSale, int myPromoPriceU, int i1, int i2, int i3) {
        super();
        this.brand = brand;
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
