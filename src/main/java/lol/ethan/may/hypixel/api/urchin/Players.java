package lol.ethan.may.hypixel.api.urchin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Players {
    @SerializedName(value = "additionalProperty")
    private List<AdditionalPropertyItem> additionalProperty;

    public List<AdditionalPropertyItem> getAdditionalProperty() {
        return this.additionalProperty;
    }
}

