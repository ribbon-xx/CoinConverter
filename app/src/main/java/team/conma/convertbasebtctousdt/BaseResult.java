package team.conma.convertbasebtctousdt;

import com.google.gson.annotations.SerializedName;

/**
 * Created by longdinh Dinh on 9/4/17.
 */

public class BaseResult {

	@SerializedName("MarketName")
	private String marketName;
	@SerializedName("Last")
	private double last;
	@SerializedName("TimeStamp")
	private String timeStamp;

	public String getMarketName() {
		return marketName;
	}

	public double getLast() {
		return last;
	}

	public String getTimeStamp() {
		return timeStamp;
	}
}
