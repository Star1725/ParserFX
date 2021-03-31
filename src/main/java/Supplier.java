import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Supplier {
    private String code_1C;
    private String myBrand;
    private String productType;
    private String nomenclature;
    private String querySearch;
    private int specPrice;
    private double commission;
    private int delivery;

    public Supplier(String code_1C, String myBrand, String productType, String nomenclature, String querySearch, int specPrice) {
        this.code_1C = code_1C;
        this.myBrand = myBrand;
        this.productType = productType;
        this.nomenclature = nomenclature;
        this.querySearch = querySearch;
        this.specPrice = specPrice;
    }
}
