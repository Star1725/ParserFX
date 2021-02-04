import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Product {
    private String vendorCode;
    private String brand;
    private String productName;
    private String refForPage;
    private String refForImage;
    private double lowerPrice;
    private double priceU;
    private String specAction;
    private int rating;
    private String refFromRequest;

}
