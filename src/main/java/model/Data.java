package model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Data {

    @SerializedName("products")
    private Products[] products;
}
