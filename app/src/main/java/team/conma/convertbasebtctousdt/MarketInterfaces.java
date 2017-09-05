package team.conma.convertbasebtctousdt;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by longdinh Dinh on 9/4/17.
 */

public interface MarketInterfaces {

	@GET("api/v1.1/public/getmarketsummary")
	Call<BaseResponse> getMarket(@Query("market") String market);
}
