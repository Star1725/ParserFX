import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultProduct extends Product{
    private String myBrand;
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

    public ResultProduct(String myVendorCodeFromRequest,
                         String myRefForPage,
                         String myRefForImage,
                         String myProductName,
                         String mySpecAction,

                         String queryForSearch,

                         String competitorBrand,
                         String competitorVendorCode,
                         String competitorProductName,
                         String competitorRefForPage,
                         String competitorRefForImage,
                         String competitorSpecAction,
                         int rating,

                         int priceU,
                         int basicSale,
                         int basicPriceU,
                         int promoSale,
                         int promoPriceU,

                         String competitorName
                         ) {
        super(myVendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction,  queryForSearch, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, rating, priceU, basicSale, basicPriceU, promoSale, promoPriceU, competitorName);
    }


    public ResultProduct(
            String myBrand,
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

            String myVendorCodeFromRequest,
            String myRefForPage,
            String myRefForImage,
            String myProductName,
            String mySpecAction,

            String queryForSearch,

            String competitorBrand,
            String competitorVendorCode,
            String competitorProductName,
            String competitorRefForPage,
            String competitorRefForImage,
            String competitorSpecAction,
            int rating,

            int priceU,
            int basicSale,
            int basicPriceU,
            int promoSale,
            int promoPriceU,

            String competitorName
            ) {
        super(myVendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction,  queryForSearch, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, rating, priceU, basicSale, basicPriceU, promoSale, promoPriceU, competitorName);
        this.myBrand = myBrand;
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

    public ResultProduct(
            String myBrand,
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
            int recommendedPromoSale
    ) {
        this.myBrand = myBrand;
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
