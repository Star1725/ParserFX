import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultProduct extends Product {
    private String brand;
    private String category;
    private String code_1C;
    private String myVendorCodeForMarketPlace;
    private String vendorCode_1C;
    private int specPrice;
    private int myPriceU;
    private int myBasicSale;
    private int myBasicPriceU;
    private int myPromoSale;
    private int myPromoPriceU;
    private int myPremiumPriceU;

    private int recommendedPriceU;
    private int recommendedSale;
    private int recommendedPromoSale;

    public ResultProduct(String vendorCodeFromRequest,
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
                         int competitorRating,

                         int competitorPriceU,
                         int competitorBasicSale,
                         int competitorBasicPriceU,
                         int competitorPromoSale,
                         int competitorPromoPriceU,
                         int competitorPremiumPriceU,
                         String competitorName) {
        super(vendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction, queryForSearch, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, competitorRating, competitorPriceU, competitorBasicSale, competitorBasicPriceU, competitorPromoSale, competitorPromoPriceU, competitorPremiumPriceU, competitorName);
    }

    public ResultProduct(
                        String brand,
                        String category,
                        String code_1C,
                        String myVendorCodeForMarketPlace,
                        String vendorCode_1C,
                        int specPrice,
                        int myPriceU,
                        int myBasicSale,
                        int myBasicPriceU,
                        int myPromoSale,
                        int myPromoPriceU,
                        int myPremiumPriceU,
                        int recommendedPriceU,
                        int recommendedSale,
                        int recommendedPromoSale,

                        String vendorCodeFromRequest,
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
                        int competitorRating,
                        int competitorPriceU,
                        int competitorBasicSale,
                        int competitorBasicPriceU,
                        int competitorPromoSale,
                        int competitorPromoPriceU,
                        int competitorPremiumPriceU,
                        String competitorName) {
        super(vendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction, queryForSearch, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, competitorRating, competitorPriceU, competitorBasicSale, competitorBasicPriceU, competitorPromoSale, competitorPromoPriceU, competitorPremiumPriceU, competitorName);
        this.brand = brand;
        this.category = category;
        this.code_1C = code_1C;
        this.myVendorCodeForMarketPlace = myVendorCodeForMarketPlace;
        this.vendorCode_1C = vendorCode_1C;
        this.specPrice = specPrice;
        this.myPriceU = myPriceU;
        this.myBasicSale = myBasicSale;
        this.myBasicPriceU = myBasicPriceU;
        this.myPromoSale = myPromoSale;
        this.myPromoPriceU = myPromoPriceU;
        this.recommendedPriceU = recommendedPriceU;
        this.myPremiumPriceU = myPremiumPriceU;
        this.recommendedSale = recommendedSale;
        this.recommendedPromoSale = recommendedPromoSale;
    }

    public ResultProduct(
            String brand,
            String category,
            String code_1C,
            String myVendorCodeForMarketPlace,
            String vendorCode_1C,
            int specPrice,
            int myPriceU,
            int myBasicSale,
            int myBasicPriceU,
            int myPromoSale,
            int myPromoPriceU,
            int myPremiumPriceU,
            int recommendedPriceU,
            int recommendedSale,
            int recommendedPromoSale,

            String queryForSearch

            ) {
        super(queryForSearch);
        this.brand = brand;
        this.category = category;
        this.code_1C = code_1C;
        this.myVendorCodeForMarketPlace = myVendorCodeForMarketPlace;
        this.vendorCode_1C = vendorCode_1C;
        this.specPrice = specPrice;
        this.myPriceU = myPriceU;
        this.myBasicSale = myBasicSale;
        this.myBasicPriceU = myBasicPriceU;
        this.myPromoSale = myPromoSale;
        this.myPromoPriceU = myPromoPriceU;
        this.myPremiumPriceU = myPremiumPriceU;
        this.recommendedPriceU = recommendedPriceU;
        this.recommendedSale = recommendedSale;
        this.recommendedPromoSale = recommendedPromoSale;
    }

    public int getMyLowerPriceU() {
        if (this.myPromoPriceU != 0) {
            return myPromoPriceU;
        } else if (this.myBasicPriceU != 0) {
            return myBasicPriceU;
        } else {
            return myPriceU;
        }
    }

    public int getMyLowerSale() {
        if (this.myPromoSale != 0) {
            return myPromoSale;
        } else {
            return myBasicSale;
        }
    }
}
