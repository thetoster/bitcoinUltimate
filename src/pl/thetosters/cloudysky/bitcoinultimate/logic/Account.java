package pl.thetosters.cloudysky.bitcoinultimate.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.entities.AccountEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.LogLogicEntity;
import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItem;
import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItemsProvider;
import pl.thetosters.cloudysky.bitcoinultimate.markets.MarketApi;
import pl.thetosters.cloudysky.bitcoinultimate.markets.OrderAnalizer;
import pl.thetosters.cloudysky.server.MasterHub;

public class Account implements LogicItemsProvider, RequestExecutor{
    public enum Type{
        MTGOX,
        BITCUREX,
        DUMMY
    }
    
    private Type type;
	private String id;
	private Map<String, LogicItem> items = new HashMap<>();
	private List<MarketBot> bots = new ArrayList<>();
    private String apiKey;
    private String apiSecret;
    private MarketApi marketApi;
    private String owner;
    private OrderAnalizer orderAnalizer;
        
    public Account(Type aType, String anId, String aOwner, String anApiKey, 
                    String secret) {
        type = aType;
        id = anId;
        apiKey = anApiKey;
        apiSecret = secret;
        owner = aOwner;
    }

    /**
     * @param le
     */
    public Account(AccountEntity le) {
        type = le.getType();
        id = le.getId();
        apiKey = le.getApiKey();
        apiSecret = le.getApiSecret();
        owner = le.getOwnerLogin();
    }

	@Override
	public LogicItem getItemById(String itemId) {
		return items.get(itemId);
	}

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the items
     */
    public Map<String, LogicItem> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(Map<String, LogicItem> items) {
        this.items = items;
    }

    /**
     * @return the bots
     */
    public List<MarketBot> getBots() {
        return bots;
    }

    /**
     * @param bots the bots to set
     */
    public void setBots(List<MarketBot> bots) {
        this.bots = bots;
    }

    /**
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @return the apiSecret
     */
    public String getApiSecret() {
        return apiSecret;
    }

    /**
     * @param apiSecret the apiSecret to set
     */
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    /**
     * @param bitCurexApi
     */
    public void setMarketApi(MarketApi mApi) {
        marketApi = mApi;
    }

    /**
     * @return the marketApi
     */
    public MarketApi getMarketApi() {
        return marketApi;
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
        Account other = (Account) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    /**
     * @return
     */
    public AccountEntity requestStorageEntity() {
        AccountEntity ae = new AccountEntity();
        ae.setApiKey(apiKey);
        ae.setApiSecret(apiSecret);
        ae.setId(id);
        ae.setType(type);
        ae.setOwnerLogin(owner); 
        ae.setConfig(marketApi.getConfig());
        return ae;
    } 
    
    public MarketBot createNewMarketBot(){
        MarketBot bot = new MarketBot();
        long time = System.currentTimeMillis() - 1369683324000L;
        bot.setId(id + "." + Long.toString(time, Character.MAX_RADIX));
        bot.setReqExecutor(this);
        bots.add(bot);
        return bot;
    }

    /**
     * @param mse
     */
    @SuppressWarnings("unchecked")
    public void processState(Map<String, Object> globals) {
        if (orderAnalizer == null){
            orderAnalizer = new OrderAnalizer((MasterHub)globals.get("masterHub"));
        }
        orderAnalizer.checkState(marketApi);
        
        globals.put("orderAnalizer", orderAnalizer);
        for(MarketBot bot : bots){
            globals.put("log", new ArrayList<String>());
            
            bot.execute(globals);
            
            if (Boolean.getBoolean("bitcoinultimate.bot.logLogic") == true){
                LogLogicEntity ent = new LogLogicEntity();
                ent.setAccountId(id);
                ent.setBotId(bot.getId());
                ent.setTime(new Date());
                ent.setLog((List<String>)globals.get("log"));
                MasterHub hub = (MasterHub)globals.get("masterHub");
                hub.getEntityFactory().storeEntity(ent, false);
            }
        }
    }

    @Override
    public String addBuyTransaction(String callerId, double pricePLN, 
                    double amountBC) {
        
        try {
            return marketApi.buyBTC(amountBC, pricePLN);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String addSellTransaction(String callerId, double pricePLN, 
                    double amountBC) {
        
        try {
            return marketApi.sellBTC(amountBC, pricePLN);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void cancelOrder(String callerId, String order) {
        try {
            marketApi.cancelOrder(order, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getOwner(){
        return owner;
    }

    /**
     * @param bot
     */
    public void deleteBot(MarketBot bot) {
        bots.remove(bot);
    }

    /**
     * @param m
     */
    public void getStatus(Map<String, Object> m) {
        m.put("accountId", id);
        m.put("type", type.name());
        List<String> botsId = new ArrayList<>();
        List<String> enabledBotsId = new ArrayList<>();
        double currentPLN = 0, currentBTC = 0;
        double soldPLN = 0, soldBTC = 0;
        double boughtPLN = 0, boughtBTC = 0;
        int totalOpCount = 0;
        for(MarketBot bot: bots){
            botsId.add(bot.getId());
            if (bot.getEnabled() == true){
                enabledBotsId.add(bot.getId());
            }
            currentPLN += bot.getCurrentPLN();
            currentBTC += bot.getCurrentBTC();
            totalOpCount += bot.getOperationsCount();
            soldPLN += bot.getTotalSellPLN();
            soldBTC += bot.getTotalSellBTC();
            boughtBTC += bot.getTotalBuyBTC();
            boughtPLN += bot.getTotalBuyPLN();
        }
        m.put("totalSoldPLN", soldPLN);
        m.put("totalSoldBTC", soldBTC);
        m.put("totalBougthPLN", boughtPLN);
        m.put("totalBougthBTC", boughtBTC);
        m.put("operationsCount", totalOpCount);
        m.put("currentPLN", currentPLN);
        m.put("currentBTC", currentBTC);
        m.put("bots", botsId);
        m.put("enabledBots", enabledBotsId);
        m.put("config", marketApi.getConfig());
    }
}
