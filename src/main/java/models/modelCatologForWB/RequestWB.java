package models.modelCatologForWB;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class RequestWB {
@Getter
    @SerializedName("data")
    private Data data;
}
