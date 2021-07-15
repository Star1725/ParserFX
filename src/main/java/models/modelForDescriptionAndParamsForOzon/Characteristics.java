package models.modelForDescriptionAndParamsForOzon;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class Characteristics {

    @Getter
    @SerializedName("characteristics")
    InnerCharacteristics[] innerCharacteristics;
}
