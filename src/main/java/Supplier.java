import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Supplier {
    private String code_1C;
    private String myBrand;
    private String productType;
    private String nomenclature;
    private String myProductModel;
    private List<String> arrayListParams;
    private String specQuerySearch;
    private int specPrice;
    private double commission;
    private int delivery;

    public Supplier(String code_1C, String myBrand, String productType, String nomenclature, String myProductModel, List<String> arrayListParams, String specQuerySearch, int specPrice) {
        this.code_1C = code_1C;
        this.myBrand = myBrand;
        this.productType = productType;
        this.myProductModel = myProductModel;
        this.arrayListParams = arrayListParams;
        this.nomenclature = nomenclature;
        this.specQuerySearch = specQuerySearch;
        this.specPrice = specPrice;
    }
}
