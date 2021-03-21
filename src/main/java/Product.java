import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String myVendorCodeFromRequest;
    private String myRefForPage;
    private String myRefForImage;
    private String myProductName;
    private String mySpecAction;

    private String queryForSearch;
    private int countSearch;

    private String competitorBrand;
    private String competitorVendorCode;
    private String competitorProductName;
    private String competitorRefForPage;
    private String competitorRefForImage;
    private String competitorSpecAction;
    private int competitorRating;

    private int competitorPriceU;
    private int competitorBasicSale;
    private int competitorBasicPriceU;
    private int competitorPromoSale;
    private int competitorPromoPriceU;
    private int competitorPremiumPriceForOzon;

    private String competitorName;

    public int getCompetitorLowerPriceU(){
        if (this.competitorPromoPriceU != 0){
            return competitorPromoPriceU;
        } else if (this.competitorBasicPriceU != 0){
            return competitorBasicPriceU;
        } else {
            return competitorPriceU;
        }
    }

    public Product(String queryForSearch, int countSearch) {
        this.queryForSearch = queryForSearch;
        this.countSearch = countSearch;
    }
}

