package models.modelForDescriptionAndParamsForOzon;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import models.modelCatologForWB.Data;

public class RequestOzon {
    @Getter
    @SerializedName("widgetStates")
    private WidgetStates widgetStates;

}
