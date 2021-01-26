import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Product {
    private int vendorCode;
    private String brand;
    private String productName;
    private String refForPage;
    private String refForImage;
    private double lowerPrice;
    private double price;
    private int sale;
    private String specAction;
    private int rating;

    @Override
    public String toString() {
        return "Product{" + "\n" +
                "vendorCode = " + vendorCode + "\n" +
                ", brand = '" + brand + '\'' + "\n" +
                ", productName = '" + productName + '\'' + "\n" +
                ", refForPage = '" + refForPage + '\'' + "\n" +
                ", refForImage = '" + refForImage + '\'' + "\n" +
                ", lowerPrice = " + lowerPrice + "\n" +
                ", price = " + price + "\n" +
                ", sale = " + sale + "\n" +
                ", specAction = '" + specAction + '\'' + "\n" +
                ", rating = " + rating + "\n" +
                '}' + "\n";
    }
}
