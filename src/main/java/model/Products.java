package model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Products {

    @SerializedName("id")
    private int id;

    @SerializedName("extended")
    private Extended extended;
}
