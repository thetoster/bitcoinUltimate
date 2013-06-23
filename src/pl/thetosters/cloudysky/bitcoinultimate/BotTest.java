package pl.thetosters.cloudysky.bitcoinultimate;

import java.util.HashMap;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;
import pl.thetosters.cloudysky.bitcoinultimate.markets.DummyApi;


public class BotTest {
    static DummyApi api = new DummyApi();
    
	public static void main(String[] args) throws Exception {
	    
	    Map<String, Object> result = new HashMap<>();
        result.put("ticker", 0);
        result.put("avrPrice", 330);
        result.put("tickerAmpl", 20);
        result.put("tickerStdDev", 5);
        result.put("drift", 330);
        result.put("driftMean", 330);
        result.put("driftStdDev", 60);
        result.put("availPLN", 100);
        result.put("availBTC", 0);
        result.put("driftTicker", 0);
        result.put("driftCount", 45);
        api.configure(result);
        for(int t = 0; t < 100; t++){
            MarketStateEntity mse = api.getTicker();
            System.out.println(mse.getAvrPrice() + "\u0009" + mse.getMinPrice()+ "\u0009" + mse.getMaxPrice());
        }
	}

}
