package models.modelForDescriptionAndParamsForOzon;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class ShortCharacteristics {

    @Getter
    @SerializedName("key")
    String key;

    @Getter
    @SerializedName("name")
    String name;

    @Getter
    @SerializedName("values")
    ValuesForCharacteristics[] values;
}
