package model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class Request {
@Getter
    @SerializedName("data")
    private Data data;
}
