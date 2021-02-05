import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Product {
    private String brand;
    private String vendorCode;
    private String productName;
    private String refForPage;
    private String refForImage;
    private int priceU;
    private int basicSale;
    private int basicPriceU;
    private int promoSale;
    private int promoPriceU;
    private String specAction;
    private int rating;
    private String refFromRequest;

    public int getLowerPriceU(){
        if (this.promoPriceU != 0){
            return promoPriceU;
        } else if (this.basicPriceU != 0){
            return basicPriceU;
        } else {
            return priceU;
        }
    }
}

