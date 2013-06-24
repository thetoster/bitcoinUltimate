package pl.thetosters.cloudysky.bitcoinultimate.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItem;
import pl.thetosters.cloudysky.bitcoinultimate.logic.stats.MarketPredictor;
import pl.thetosters.cloudysky.server.MasterHub;

public class MarketBot implements RequestExecutor{
	
	private String id;
	private LogicItem workPlan;
	private int operationsCount;   //ile zlecen sumarycznie wykonal bot
    private double totalBuyBTC;
    private double totalSellBTC;
	private double totalBuyPLN;
	private double totalSellPLN;
	private double startPLN;
	private boolean enabled;
    private double currentPLN;  //ile mamy gotówki do dyspozycji
    private double currentBTC;   //ile mamy BC do dyspozycji
    private double basePricePLN;    //bazowa cena dla operacji (ostatnia cena kupna lub sprzedały)
    
    private RequestExecutor reqExecutor;

    private transient boolean wasValidated, isValid; 
    
    public MarketBot() {
    }
    
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LogicItem getWorkPlan() {
		return workPlan;
	}
	public void setWorkPlan(LogicItem workPlan) {
		this.workPlan = workPlan;
	}
	
	public void setTotalBuyPLN(double number) {
		totalBuyPLN = number;
	}

	public void setTotalSellPLN(double number) {
		totalSellPLN = number;
	}
	
	public void setStartPLN(double number) {
		startPLN = number;
	}
	/**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the operationsCount
     */
    public int getOperationsCount() {
        return operationsCount;
    }

    /**
     * @param operationsCount the operationsCount to set
     */
    public void setOperationsCount(int operationsCount) {
        this.operationsCount = operationsCount;
    }

    /**
     * @return the totalBuyBTC
     */
    public double getTotalBuyBTC() {
        return totalBuyBTC;
    }

    /**
     * @param totalBuyBTC the totalBuyBTC to set
     */
    public void setTotalBuyBTC(double totalBuyBTC) {
        this.totalBuyBTC = totalBuyBTC;
    }

    /**
     * @return the totalSellBTC
     */
    public double getTotalSellBTC() {
        return totalSellBTC;
    }

    /**
     * @param totalSellBTC the totalSellBTC to set
     */
    public void setTotalSellBTC(double totalSellBTC) {
        this.totalSellBTC = totalSellBTC;
    }

    /**
     * @return the currentPLN
     */
    public double getCurrentPLN() {
        return currentPLN;
    }

    /**
     * @param currentPLN the currentPLN to set
     */
    public void setCurrentPLN(double currentPLN) {
        this.currentPLN = currentPLN;
    }

    /**
     * @return the currentBTC
     */
    public double getCurrentBTC() {
        return currentBTC;
    }

    /**
     * @param currentBTC the currentBTC to set
     */
    public void setCurrentBTC(double currentBTC) {
        this.currentBTC = currentBTC;
    }

    /**
     * @return the basePricePLN
     */
    public double getBasePricePLN() {
        return basePricePLN;
    }

    /**
     * @param basePricePLN the basePricePLN to set
     */
    public void setBasePricePLN(double basePricePLN) {
        this.basePricePLN = basePricePLN;
    }

    /**
     * @return the totalBuyPLN
     */
    public double getTotalBuyPLN() {
        return totalBuyPLN;
    }

    /**
     * @return the totalSellPLN
     */
    public double getTotalSellPLN() {
        return totalSellPLN;
    }

    /**
     * @return the startPLN
     */
    public double getStartPLN() {
        return startPLN;
    }
    
    /**
     * @return the reqExecutor
     */
    public RequestExecutor getReqExecutor() {
        return reqExecutor;
    }

    /**
     * @param reqExecutor the reqExecutor to set
     */
    public void setReqExecutor(RequestExecutor reqExecutor) {
        this.reqExecutor = reqExecutor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MarketBot other = (MarketBot) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void execute(Map<String, Object> globals) {
        if (enabled == false){
            return;
        }
	    if (workPlan == null){
	        //no workplan defined, probably this bot is under construction
	        return;
	    }
	    if (wasValidated == false){
	        List<String> l = new ArrayList<>();
	        Set<LogicItem> s = new HashSet<>();
	        isValid = workPlan.validate(s, l, true);
	        wasValidated = true;
	    }
        if (isValid == false){
            return;
        }
        MarketPredictor pred = (MarketPredictor)globals.get("predictor");
	    Map<String, Object> params = new HashMap<>();
	    params.putAll(globals);
	    params.put("curPLN", currentPLN);
	    params.put("curBTC", currentBTC);
	    params.put("marketAvg", pred.getLastMarketState().getAvrPrice());
	    params.put("marketAvgW", pred.getLastMarketState().getWeightedAvrPrice());
	    params.put("marketLow", pred.getLastMarketState().getMinPrice());
	    params.put("marketHigh", pred.getLastMarketState().getMaxPrice());
	    params.put("executor", this);
	    params.put("botId", id);
	    try{
	        LogicItem li = workPlan;
	        int cnt = 0;
	        while (li != null){
	            li = li.execute(params);
	            cnt++;
	            if (cnt > 1500){
	                MasterHub hub = (MasterHub)params.get("MasterHub");
	                hub.getLogicLogger().warn("Bot " + id + " to many iterations.");
	                break;
	            }
	        }
	    } catch(Exception e){
	        //we don't care. Since it may be exception due to user wrong definition
	    }
	}

    @Override
    public String addBuyTransaction(String callerId, double pricePLN, 
                    double amountBC) {
        operationsCount++;
        return reqExecutor.addBuyTransaction(id, pricePLN, amountBC);
    }

    @Override
    public String addSellTransaction(String callerId, double pricePLN, 
                    double amountBC) {
        operationsCount++; 
        return reqExecutor.addSellTransaction(id, pricePLN, amountBC);
    }

    @Override
    public void cancelOrder(String callerId, String order) {
        operationsCount++;
        reqExecutor.cancelOrder(id, order);
    }

    /**
     * @return
     */
    public boolean getEnabled() {
        return enabled;
    }
    
    public void getStatus(Map<String, Object> m) {
        m.put("botId", id);
        m.put("totalSoldPLN", totalSellPLN);
        m.put("totalSoldBTC", totalSellBTC);
        m.put("totalBougthPLN", totalBuyPLN);
        m.put("totalBougthBTC", totalBuyBTC);
        m.put("operationsCount", operationsCount);
        m.put("currentPLN", currentPLN);
        m.put("currentBTC", currentBTC);
        m.put("enabled", enabled);
        m.put("workPlan", workPlan == null ? "" : workPlan.getId());
    }

    /**
     * @param amount
     * @param d
     */
    public void changeBTCAmountDueSell(double amount, double pricePerAmount) {
        totalSellBTC += amount;
        currentBTC -= amount;
        
        totalSellPLN += pricePerAmount;
        currentPLN += pricePerAmount;
    }

    /**
     * @param amount
     * @param d
     */
    public void changeBTCAmountDueBuy(double amount, double pricePerAmount) {
        totalBuyBTC += amount;
        currentBTC += amount;
        
        currentPLN -= pricePerAmount;
        totalBuyPLN += pricePerAmount;
    }
}
