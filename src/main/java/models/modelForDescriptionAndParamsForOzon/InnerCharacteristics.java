package models.modelForDescriptionAndParamsForOzon;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class InnerCharacteristics {

    @Getter
    @SerializedName("title")
    String title;

    @Getter
    @SerializedName("short")
    ShortCharacteristics[] shortCharacteristics;
}
