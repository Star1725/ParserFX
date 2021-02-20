import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SupplierSpecPrice {
    private String code_1C;
    private String vendorCode_1C;
    private int specPrice;
}
