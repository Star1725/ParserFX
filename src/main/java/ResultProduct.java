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

    private int recommendedMyLowerPrice;
    private int recommendedSale;
    private int recommendedPromoSale;
    private int recommendedPriceU;

    public ResultProduct(String myVendorCodeFromRequest,
                         String myRefForPage,
                         String myRefForImage,
                         String myProductName,
                         String mySpecAction,

                         String queryForSearch,
                         int countSearch,

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
        super(myVendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction,  queryForSearch, countSearch, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, rating, priceU, basicSale, basicPriceU, promoSale, promoPriceU, competitorName);
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
            int recommendedMyLowerPrice,
            int recommendedSale,
            int recommendedPromoSale,
            int recommendedPriceU,

            String myVendorCodeFromRequest,
            String myRefForPage,
            String myRefForImage,
            String myProductName,
            String mySpecAction,

            String queryForSearch,
            int countSearch,

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
        super(myVendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction,  queryForSearch, countSearch, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, rating, priceU, basicSale, basicPriceU, promoSale, promoPriceU, competitorName);
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
        this.recommendedMyLowerPrice = recommendedMyLowerPrice;
        this.recommendedSale = recommendedSale;
        this.recommendedPromoSale = recommendedPromoSale;
        this.recommendedPriceU = recommendedPriceU;
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
            int recommendedMyLowerPrice,
            int recommendedSale,
            int recommendedPromoSale,
            int recommendedPriceU
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
        this.recommendedMyLowerPrice = recommendedMyLowerPrice;
        this.recommendedSale = recommendedSale;
        this.recommendedPromoSale = recommendedPromoSale;
        this.recommendedPriceU = recommendedPriceU;
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
}
