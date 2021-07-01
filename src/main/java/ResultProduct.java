import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResultProduct extends Product{
    private int isFind;
    private String myBrand;
    private String category;
    private String productType;
    private String myProductModel;
    private int countMyProductModel;
    private ArrayList<List<String>> arrayListParams;
    private String code_1C;
    private String myNomenclature_1C;
    private String myVendorCodeForWildberiesOrOzon;

    private double myCommissionForOzonOrWildberries;
    private double myOrderAssemblyForOzon;
    private double myTrunkForOzon;
    private double myLastMileForOzonOrWildberries;

    private String specQuerySearchForWildberiesOrOzon;
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
                         String refUrlForResult,

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
        super(myVendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction,  queryForSearch, refUrlForResult, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, rating, priceU, basicSale, basicPriceU, promoSale, promoPriceU, competitorPremiumPriceForOzon, competitorName);
    }


    public ResultProduct(
            int isFind,
            String myBrand,
            String category,
            String productType,
            String myProductModel,
            int countMyProductModel,
            ArrayList<List<String>> arrayListParams,
            String code_1C,
            String myNomenclature_1C,
            String myVendorCodeForWildberiesOrOzon,

            double myCommissionForOzonOrWildberries,
            double myOrderAssemblyForOzon,
            double myTrunkForOzon,
            double myLastMileForOzonOrWildberries,

            String specQuerySearchForWildberiesOrOzon,
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
            String refUrlForResult,

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
        super(myVendorCodeFromRequest, myRefForPage, myRefForImage, myProductName, mySpecAction,  queryForSearch, refUrlForResult, competitorBrand, competitorVendorCode, competitorProductName, competitorRefForPage, competitorRefForImage, competitorSpecAction, rating, priceU, basicSale, basicPriceU, promoSale, promoPriceU, competitorPremiumPriceForOzon, competitorName);
        this.isFind = isFind;
        this.myBrand = myBrand;
        this.category = category;
        this.productType = productType;
        this.myProductModel = myProductModel;
        this.countMyProductModel = countMyProductModel;
        this.arrayListParams = arrayListParams;
        this.code_1C = code_1C;
        this.myNomenclature_1C = myNomenclature_1C;
        this.myVendorCodeForWildberiesOrOzon = myVendorCodeForWildberiesOrOzon;

        this.myCommissionForOzonOrWildberries = myCommissionForOzonOrWildberries;
        this.myOrderAssemblyForOzon = myOrderAssemblyForOzon;
        this.myTrunkForOzon = myTrunkForOzon;
        this.myLastMileForOzonOrWildberries = myLastMileForOzonOrWildberries;

        this.specQuerySearchForWildberiesOrOzon = specQuerySearchForWildberiesOrOzon;
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
            int isFind,
            String myBrand,
            String category,
            String productType,
            String myProductModel,
            int countMyProductModel,
            ArrayList<List<String>> arrayListParams,
            String code_1C,
            String myNomenclature_1C,
            String myVendorCodeForWildberiesOrOzon,

            double myCommissionForOzonOrWildberries,
            double myOrderAssemblyForOzon,
            double myTrunkForOzon,
            double myLastMileForOzonOrWildberries,

            String specQuerySearchForWildberiesOrOzon,
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
        this.isFind = isFind;
        this.myBrand = myBrand;
        this.category = category;
        this.productType = productType;
        this.myProductModel = myProductModel;
        this.countMyProductModel = countMyProductModel;
        this.arrayListParams = arrayListParams;
        this.code_1C = code_1C;
        this.myNomenclature_1C = myNomenclature_1C;
        this.myVendorCodeForWildberiesOrOzon = myVendorCodeForWildberiesOrOzon;

        this.myCommissionForOzonOrWildberries = myCommissionForOzonOrWildberries;
        this.myOrderAssemblyForOzon = myOrderAssemblyForOzon;
        this.myTrunkForOzon = myTrunkForOzon;
        this.myLastMileForOzonOrWildberries = myLastMileForOzonOrWildberries;

        this.specQuerySearchForWildberiesOrOzon = specQuerySearchForWildberiesOrOzon;
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
