package team.conma.convertbasebtctousdt;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by longdinh Dinh on 9/4/17.
 */

public class BaseResponse {

	@SerializedName("success")
	private boolean success;
	@SerializedName("message")
	private String message;
	@SerializedName("result")
	private List<BaseResult> result;

	public List<BaseResult> getResult() {
		return result;
	}
}
