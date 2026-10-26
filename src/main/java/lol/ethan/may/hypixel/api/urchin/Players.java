package lol.ethan.may.hypixel.api.urchin;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Players{

	@SerializedName("additionalProperty")
	private List<AdditionalPropertyItem> additionalProperty;

	public List<AdditionalPropertyItem> getAdditionalProperty(){
		return additionalProperty;
	}
}