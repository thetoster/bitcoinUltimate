package pl.thetosters.cloudysky.bitcoinultimate.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.entities.AccountEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.LogLogicEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketOrderEntity;
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
        Map<String, Object> m = new HashMap<>();
        getStatus(m);
        System.out.println("currentPLN:"+m.get("currentPLN")+" currentBTC:"+m.get("currentBTC"));
        
        
        if (orderAnalizer == null){
            orderAnalizer = new OrderAnalizer((MasterHub)globals.get("masterHub"),
                            id);
        }
        orderAnalizer.checkState(marketApi, this);
        
        
        System.out.println("--------------");
        m = new HashMap<>();
        getStatus(m);
        System.out.println("currentPLN:"+m.get("currentPLN")+"currentBTC:"+m.get("currentBTC"));
        System.out.println("--------------");
        
        
        globals.put("orderAnalizer", orderAnalizer);
        for(MarketBot bot : bots){
            System.out.println("Iteration:" + bot.getIteration());
            
            globals.put("log", new ArrayList<String>());
            
            bot.execute(globals);
            
            if (Boolean.getBoolean("bitcoinultimate.bot.logLogic") == true){
                LogLogicEntity ent = new LogLogicEntity();
                ent.setAccountId(id);
                ent.setBotId(bot.getId());
                ent.setTime(new Date());
                ent.setLog((List<String>)globals.get("log"));
                ent.setIteration(bot.getIteration());
                MasterHub hub = (MasterHub)globals.get("masterHub");
                hub.getEntityFactory().storeEntity(ent, false);
            }
        }
        System.out.println("+++++++++++++++++++++++++++++++++++++++++");
    }

    @Override
    public String addBuyTransaction(String callerId, double pricePLN, 
                    double amountBC) {
        
        try {
            String oid = marketApi.buyBTC(amountBC, pricePLN);
            MarketOrderEntity order = new MarketOrderEntity();
            order.setAmount(amountBC);
            order.setPrice(pricePLN);
            order.setMarket(type.name());
            order.setOid(oid);
            order.setSellBTC(false);
            order.setTime(new Date());
            order.setBotId(callerId);
            order.setAccountId(id);
            orderAnalizer.addOrder(order);
            orderAnalizer.storeOrder(oid);
            return oid;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String addSellTransaction(String callerId, double pricePLN, 
                    double amountBC) {
        
        try {
            String oid = marketApi.sellBTC(amountBC, pricePLN);
            MarketOrderEntity order = new MarketOrderEntity();
            order.setAmount(amountBC);
            order.setPrice(pricePLN);
            order.setMarket(type.name());
            order.setOid(oid);
            order.setSellBTC(true);
            order.setTime(new Date());
            order.setBotId(callerId);
            order.setAccountId(id);
            orderAnalizer.addOrder(order);
            orderAnalizer.storeOrder(oid);
            return oid;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void cancelOrder(String callerId, String order) {
        try {
            marketApi.cancelOrder(order, "");
            orderAnalizer.cancelOrder(order);
            orderAnalizer.storeOrder(order);
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

    /**
     * @param amount
     * @param price
     * @param botId
     */
    public void changeBTCAmountDueSell(double amount, double price, String botId) {
        MarketBot bot = null;
        for(MarketBot tmp : bots){
            if (tmp.getId().equals(botId) == true){
                bot = tmp;
                break;
            }
        }
        if (bot == null){
            return;
        }
        double oldAmount = amount; 
        switch(type){
            case BITCUREX:
                //0.06% BTC fee
                amount *= 0.994;
                break;
                
            case MTGOX:
                //0.06% BTC fee
                amount *= 0.994;
                break;
                
            case DUMMY:
                break;
        }
        bot.changeBTCAmountDueSell(amount, oldAmount * price);
    }

    /**
     * @param amount
     * @param price
     * @param botId
     */
    public void changeBTCAmountDueBuy(double amount, double price, String botId) {
        MarketBot bot = null;
        for(MarketBot tmp : bots){
            if (tmp.getId().equals(botId) == true){
                bot = tmp;
                break;
            }
        }
        if (bot == null){
            return;
        }
        double oldAmount = amount; 
        switch(type){
            case BITCUREX:
                //0.06% BTC fee
                amount *= 0.994;
                break;
                
            case MTGOX:
                //0.06% BTC fee
                amount *= 0.994;
                break;
                
            case DUMMY:
                break;
        }
        bot.changeBTCAmountDueBuy(amount, oldAmount * price);
    }
}
