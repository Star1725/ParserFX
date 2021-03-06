import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Product {
    private String myVendorCodeFromRequest;
    private String myRefForPage;
    private String myRefForImage;
    private String myProductName;
    private String mySpecAction;

    private String queryForSearch;

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

    private String competitorName;

    public int getLowerPriceU(){
        if (this.competitorPromoPriceU != 0){
            return competitorPromoPriceU;
        } else if (this.competitorBasicPriceU != 0){
            return competitorBasicPriceU;
        } else {
            return competitorPriceU;
        }
    }
}

