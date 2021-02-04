package model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Extended {

    @SerializedName("basicPriceU")
    private int basicPriceU;

    @SerializedName("basicSale")
    private int basicSale;

    @SerializedName("promoPriceU")
    private int promoPriceU;

    @SerializedName("promoSale")
    private int promoSale;

}
