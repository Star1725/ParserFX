import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultProduct extends Product{
    private String myBrand;
    private String category;
    private String productType;
    private String code_1C;
    private String myNomenclature_1C;
    private String myVendorCodeForWildberiesOrOzon;

    private double myCommissionForOzon;
    private double myOrderAssemblyForOzon;
    private double myTrunkForOzon;
    private double myLastMileForOzon;

    private String querySearchForWildberiesOrOzon;
    private int specPrice;
    private int myPriceU;
    private int myBasicSale;
    private int myBasicPriceU;
    private int myPromoSale;
    private int myPromoPriceU;

    private int recommendedMyLowerPrice;
    private int recommendedBasicSale;
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
                         int competitorPremiumPriceForOzon,

                         String competitorName
                         ) {
        super(myVendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction,  queryForSearch, countSearch, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, rating, priceU, basicSale, basicPriceU, promoSale, promoPriceU, competitorPremiumPriceForOzon, competitorName);
    }


    public ResultProduct(
            String myBrand,
            String category,
            String productType,
            String code_1C,
            String myNomenclature_1C,
            String myVendorCodeForWildberiesOrOzon,

            double myCommissionForOzon,
            double myOrderAssemblyForOzon,
            double myTrunkForOzon,
            double myLastMileForOzon,

            String querySearchForWildberiesOrOzon,
            int specPrice,
            int myPriceU,
            int myBasicSale,
            int myBasicPriceU,
            int myPromoSale,
            int myPromoPriceU,
            int recommendedMyLowerPrice,
            int recommendedBasicSale,
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
            int competitorPremiumPriceForOzon,

            String competitorName
            ) {
        super(myVendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction,  queryForSearch, countSearch, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, rating, priceU, basicSale, basicPriceU, promoSale, promoPriceU, competitorPremiumPriceForOzon, competitorName);
        this.myBrand = myBrand;
        this.category = category;
        this.productType = productType;
        this.code_1C = code_1C;
        this.myNomenclature_1C = myNomenclature_1C;
        this.myVendorCodeForWildberiesOrOzon = myVendorCodeForWildberiesOrOzon;

        this.myCommissionForOzon = myCommissionForOzon;
        this.myOrderAssemblyForOzon = myOrderAssemblyForOzon;
        this.myTrunkForOzon = myTrunkForOzon;
        this.myLastMileForOzon = myLastMileForOzon;


        this.querySearchForWildberiesOrOzon = querySearchForWildberiesOrOzon;
        this.specPrice = specPrice;
        this.myPriceU = myPriceU;
        this.myBasicSale = myBasicSale;
        this.myBasicPriceU = myBasicPriceU;
        this.myPromoSale = myPromoSale;
        this.myPromoPriceU = myPromoPriceU;
        this.recommendedMyLowerPrice = recommendedMyLowerPrice;
        this.recommendedBasicSale = recommendedBasicSale;
        this.recommendedPromoSale = recommendedPromoSale;
        this.recommendedPriceU = recommendedPriceU;
    }

    public ResultProduct(
            String myBrand,
            String category,
            String productType,
            String code_1C,
            String myNomenclature_1C,
            String myVendorCodeForWildberiesOrOzon,

            double myCommissionForOzon,
            double myOrderAssemblyForOzon,
            double myTrunkForOzon,
            double myLastMileForOzon,

            String querySearchForWildberiesOrOzon,
            int specPrice,
            int myPriceU,
            int myBasicSale,
            int myBasicPriceU,
            int myPromoSale,
            int myPromoPriceU,
            int recommendedMyLowerPrice,
            int recommendedBasicSale,
            int recommendedPromoSale,
            int recommendedPriceU
    ) {
        this.myBrand = myBrand;
        this.category = category;
        this.productType = productType;
        this.code_1C = code_1C;
        this.myNomenclature_1C = myNomenclature_1C;
        this.myVendorCodeForWildberiesOrOzon = myVendorCodeForWildberiesOrOzon;

        this.myCommissionForOzon = myCommissionForOzon;
        this.myOrderAssemblyForOzon = myOrderAssemblyForOzon;
        this.myTrunkForOzon = myTrunkForOzon;
        this.myLastMileForOzon = myLastMileForOzon;

        this.querySearchForWildberiesOrOzon = querySearchForWildberiesOrOzon;
        this.specPrice = specPrice;
        this.myPriceU = myPriceU;
        this.myBasicSale = myBasicSale;
        this.myBasicPriceU = myBasicPriceU;
        this.myPromoSale = myPromoSale;
        this.myPromoPriceU = myPromoPriceU;
        this.recommendedMyLowerPrice = recommendedMyLowerPrice;
        this.recommendedBasicSale = recommendedBasicSale;
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
