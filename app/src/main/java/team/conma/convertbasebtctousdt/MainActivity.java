package team.conma.convertbasebtctousdt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Callback<BaseResponse> {

	private static final int TYPE_DEFAULT = 10;
	private static final int TYPE_COIN = 20;
	public static final String MARKET_DEFAULT = "usdt-btc";

	@BindView(R.id.tv_BTC_to_USDT)
	TextView tvBTCToUSDT;
	@BindView(R.id.et_coin)
	EditText etCoin;
	@BindView(R.id.sp_base_coin)
	Spinner spBaseCoin;
	@BindView(R.id.tv_base_coin_to_coin)
	TextView tvBaseCoinToCoin;
	@BindView(R.id.tv_base_coin_to_coin_value)
	TextView tvBaseCoinToCoinValue;
	@BindView(R.id.tv_new_base_coin_to_coin)
	TextView tvNewBaseCoinToCoin;
	@BindView(R.id.tv_new_coin_value)
	TextView tvNewCoinValue;
	@BindView(R.id.et_coin_price)
	EditText etCoinPrice;
	@BindView(R.id.pbLoading)
	ProgressBar pbLoading;
	@BindView(R.id.tvError)
	TextView tvError;
	@BindView(R.id.bt_convert_new_base_coin)
	Button btConvert;

	private DecimalFormat df;
	private Retrofit retrofit;

	private String targetCoinName;
	private BaseResult baseCoinValue, targetCoinValue;

	@Override
	protected void onStart() {
		super.onStart();
		OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(40, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS);

		retrofit = new Retrofit.Builder().baseUrl("https://bittrex.com").addConverterFactory(GsonConverterFactory.create()).client(builder.build()).build();
	}

	private synchronized void getMarketByName(String marketName, Callback<BaseResponse> callback) {
		synchronized(this) {
			pbLoading.setVisibility(View.VISIBLE);
			tvError.setVisibility(View.INVISIBLE);
			MarketInterfaces marketInterfaces = retrofit.create(MarketInterfaces.class);
			Call<BaseResponse> call = marketInterfaces.getMarket(marketName);
			call.enqueue(callback);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		spBaseCoin.setSelection(0);
		df = new DecimalFormat("#.############");
		df.setRoundingMode(RoundingMode.CEILING);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getMarketByName(MARKET_DEFAULT, this);
	}

	@OnClick(R.id.bt_refresh_coin_base)
	public void onCoinBaseRefreshClicked() {
		getMarketByName(MARKET_DEFAULT, this);
	}

	@OnClick(R.id.bt_get_exchange)
	public void onGetExchangeClicked() {
		String baseCoin = (String) spBaseCoin.getSelectedItem();
		String targetCoin = etCoin.getText().toString();
		if (targetCoin.trim().equalsIgnoreCase("")) {
			return;
		}
		getMarketByName(baseCoin + "-" + targetCoin, this);
	}

	@OnClick(R.id.bt_convert_new_base_coin)
	public void onConvertNewBaseCoinClicked() {
		if (null == baseCoinValue || null == targetCoinValue || etCoinPrice.getText().length() <= 0) {
			tvError.setVisibility(View.VISIBLE);
			return;
		}
		String baseCoin = (String) spBaseCoin.getSelectedItem();
		targetCoinName = etCoin.getText().toString().toUpperCase();
		if (baseCoin.equalsIgnoreCase("btc")) {
			convertWithBaseCoinIsBTC();
		} else {
			convertWithBaseCoinIsUSDT();
		}
	}

	private void convertWithBaseCoinIsBTC() {
		// We have 1 tCoin = xxx btc, need to know 1 btc = xxx tCoin
		double tCoinValue = Double.parseDouble(etCoinPrice.getText().toString());
		double oneBTC2TCoin = 1 / tCoinValue;
		// We have usdt equal with tcoin, convert now
		double finalPrice = baseCoinValue.getLast() / oneBTC2TCoin;
		tvNewBaseCoinToCoin.setText("USDT - " + targetCoinName);
		tvNewCoinValue.setText(df.format(finalPrice));
	}

	private void convertWithBaseCoinIsUSDT() {
		// We have 1 usdt = xxx btc, need to know 1 btc = xxx usdt
		double oneBTC2USDT = 1 / baseCoinValue.getLast();
		// We have 1 tCoin = xxx usdt, need to know 1 usdt = xxx tCoin
		double tCoinValue = Double.parseDouble(etCoinPrice.getText().toString());
		double oneUSDT2TCoin = 1 / tCoinValue;
		// We have btc equal with tcoin, convert now
		double finalPrice = oneBTC2USDT / oneUSDT2TCoin;
		tvNewBaseCoinToCoin.setText("BTC - " + targetCoinName);
		tvNewCoinValue.setText(df.format(finalPrice));
	}

	@Override
	public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
		pbLoading.setVisibility(View.GONE);
		BaseResponse baseResponse = response.body();
		if (null != baseResponse.getResult() && baseResponse.getResult().size() > 0) {
			BaseResult baseResult = baseResponse.getResult().get(0);
			String marketName = baseResult.getMarketName();
			String marketValue = df.format(baseResult.getLast());
			if (marketName.equalsIgnoreCase(MARKET_DEFAULT)) {
				baseCoinValue = baseResult;
				tvBTCToUSDT.setText(marketValue);
			} else {
				targetCoinValue = baseResult;
				tvBaseCoinToCoin.setText(marketName);
				tvBaseCoinToCoinValue.setText(marketValue);
				btConvert.setEnabled(true);
			}
		}
	}

	@Override
	public void onFailure(Call<BaseResponse> call, Throwable t) {
		pbLoading.setVisibility(View.GONE);
		tvError.setVisibility(View.VISIBLE);
	}
}
