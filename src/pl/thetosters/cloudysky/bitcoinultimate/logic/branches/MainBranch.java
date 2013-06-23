/*
 * MainBranch.java
 * Date: 2011-11-08 16:44:01
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.branches;

import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestAccountState;
import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestAddAccount;
import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestAddBot;
import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestAddLogicItem;
import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestBotState;
import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestDelBot;
import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestDelLogicItem;
import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestGetLogicItems;
import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.ClientRequestSetBot;
import pl.thetosters.cloudysky.bitcoinultimate.markets.MarketEngine;
import pl.thetosters.cloudysky.server.MasterHub;
import pl.thetosters.cloudysky.server.logic.ClientRequest;
import pl.thetosters.cloudysky.server.logic.LogicBranch;
import pl.thetosters.cloudysky.server.logic.branches.BranchBaseImpl;
import pl.thetosters.cloudysky.server.misc.AutoConvertor;

/**
 * Logic branch which introduces friends handling commands.
 * @author Bartłomiej Żarnowski
 * @version 1.0.0
 */
public class MainBranch extends BranchBaseImpl {
    public final static String STORAGE_ID = "BitcoinUltimateBranch";
    private final static String CMD_ADD_ACCOUNT = "addAccount";
    private final static String CMD_ADD_BOT = "addBot";
    private final static String CMD_SET_BOT = "setBot";
    private final static String CMD_DEL_BOT = "delBot";
    private final static String CMD_BOT_STATE = "botState";
    private final static String CMD_ACCOUNT_STATE = "accountState";
    private final static String CMD_ADD_LOGIC_ITEM = "addLogicItem";
    private final static String CMD_GET_LOGIC_ITEMS = "getLogicItems";
    private final static String CMD_DEL_LOGIC_ITEM = "delLogicItem";
    
    private MarketEngine marketEngine;
    
	public MainBranch(MasterHub aMasterHub, MarketEngine engine){
		masterHub = aMasterHub;
		marketEngine = engine;
	}
	
	@Override
	public ClientRequest parse(Map<?, ?> o) {
        Map<?, ?> values = (Map<?, ?>) o.get(CMD_ADD_ACCOUNT);
        if (values != null) {
            return buildAddAccountRequest(values);
        }
        values = (Map<?, ?>) o.get(CMD_ADD_BOT);
        if (values != null) {
            return buildAddBotRequest(values);
        }
        values = (Map<?, ?>) o.get(CMD_SET_BOT);
        if (values != null) {
            return buildSetBotRequest(values);
        }
        values = (Map<?, ?>) o.get(CMD_DEL_BOT);
        if (values != null) {
            return buildDelBotRequest(values);
        }
        values = (Map<?, ?>) o.get(CMD_ACCOUNT_STATE);
        if (values != null) {
            return buildAccountStateRequest(values);
        }
        values = (Map<?, ?>) o.get(CMD_BOT_STATE);
        if (values != null) {
            return buildBotStateRequest(values);
        }
        values = (Map<?, ?>) o.get(CMD_ADD_LOGIC_ITEM);
        if (values != null) {
            return buildAddLogicItemRequest(values);
        }
        values = (Map<?, ?>) o.get(CMD_GET_LOGIC_ITEMS);
        if (values != null) {
            return buildGetLogicItemsRequest(values);
        }
        values = (Map<?, ?>) o.get(CMD_DEL_LOGIC_ITEM);
        if (values != null) {
            return buildDelLogicItemRequest(values);
        }
		return null;
	}

	/**
     * @param values
     * @return
     */
    private ClientRequest buildDelLogicItemRequest(Map<?, ?> values) {
        String accountId = (String)values.get("accountId");
        String id = (String)values.get("id");
        if ((id == null) || (accountId == null)){
            masterHub.getLogicLogger().error("Missing required param id/accountId");
            parent.sendMissingOrBadParam("type", "id and accountId are required");
            return null;
        }
        ClientRequestDelLogicItem cr = new ClientRequestDelLogicItem(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setId(id);
        cr.setAccountId(accountId);
        return cr;
    }

    /**
     * @param values
     * @return
     */
    private ClientRequest buildGetLogicItemsRequest(Map<?, ?> values) {
        String accountId = (String)values.get("accountId");
        if (accountId == null){
            masterHub.getLogicLogger().error("Missing required param accountId");
            parent.sendMissingOrBadParam("type", "accountId is required");
            return null;
        }
        
        ClientRequestGetLogicItems cr = new ClientRequestGetLogicItems(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setAccountId(accountId);
        
        return cr;
    }

    /**
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    private ClientRequest buildAddLogicItemRequest(Map<?, ?> values) {
        String accountId = (String)values.get("accountId");
        String type = (String)values.get("type");
        if ((accountId == null) || (type == null)){
            masterHub.getLogicLogger().error("Missing required accountId/type");
            parent.sendMissingOrBadParam("type", "accountId/type is required");
            return null;
        }
        Object data = values.get("data");
        if ((data instanceof Map) == false){
            masterHub.getLogicLogger().error("addLogicItem: data is not a Map");
            parent.sendMissingOrBadParam("type", "data must be key-value");
            return null;            
        }
        ClientRequestAddLogicItem cr = new ClientRequestAddLogicItem(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setAccountId(accountId);
        cr.setData((Map<String,Object>)data);
        cr.setType(type);
        return cr;
    }

    /**
     * @param values
     * @return
     */
    private ClientRequest buildBotStateRequest(Map<?, ?> values) {
        String accountId = (String)values.get("accountId");
        String botId = (String)values.get("botId");
        if ((botId == null) || (accountId == null)){
            masterHub.getLogicLogger().error("Missing required param botId/accountId");
            parent.sendMissingOrBadParam("type", "botId and accountId are required");
            return null;
        }
        ClientRequestBotState cr = new ClientRequestBotState(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setBotId(botId);
        cr.setAccountId(accountId);
        
        return cr;
    }

    /**
     * @param values
     * @return
     */
    private ClientRequest buildAccountStateRequest(Map<?, ?> values) {
        String accountId = (String)values.get("accountId");
        if (accountId == null){
            masterHub.getLogicLogger().error("Missing required param accountId");
            parent.sendMissingOrBadParam("type", "accountId is required");
            return null;
        }
        
        ClientRequestAccountState cr = new ClientRequestAccountState(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setAccountId(accountId);
        
        return cr;
    }

    /**
     * @param values
     * @return
     */
    private ClientRequest buildDelBotRequest(Map<?, ?> values) {
        String accountId = (String)values.get("accountId");
        String botId = (String)values.get("botId");
        if ((botId == null) || (accountId == null)){
            masterHub.getLogicLogger().error("Missing required param botId/accountId");
            parent.sendMissingOrBadParam("type", "botId and accountId are required");
            return null;
        }
        ClientRequestDelBot cr = new ClientRequestDelBot(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setBotId(botId);
        cr.setAccountId(accountId);
        
        return cr;
    }

    /**
     * @param values
     * @return
     */
    private ClientRequest buildSetBotRequest(Map<?, ?> values) {
        String accountId = (String)values.get("accountId");
        String botId = (String)values.get("botId");
        if ((botId == null) || (accountId == null)){
            masterHub.getLogicLogger().error("Missing required param botId/accountId");
            parent.sendMissingOrBadParam("type", "botId and accountId are required");
            return null;
        }
        ClientRequestSetBot cr = new ClientRequestSetBot(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setBotId(botId);
        cr.setAccountId(accountId);
        cr.setPLNChange(AutoConvertor.getAsDouble("PLNChange", values));
        cr.setBTCChange(AutoConvertor.getAsDouble("BTCChange", values));
        cr.setEnabled(AutoConvertor.getAsInt("enabled", values, -1));
        cr.setWorkplan((String)values.get("workPlan"));
        
        return cr;
    }

    /**
     * @param values
     * @return
     */
    private ClientRequest buildAddBotRequest(Map<?, ?> values) {
        String accountId = (String)values.get("accountId");
        if (accountId == null){
            masterHub.getLogicLogger().error("Missing required param accountId");
            parent.sendMissingOrBadParam("type", "accountId is required");
            return null;
        }
        
        ClientRequestAddBot cr = new ClientRequestAddBot(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setAccountId(accountId);
        
        return cr;
    }

    /**
     * @param values
     * @return
     */
    @SuppressWarnings("unchecked")
    private ClientRequest buildAddAccountRequest(Map<?, ?> values) {
        String apiKey = (String)values.get("apiKey");
        String apiSecret = (String)values.get("apiSecret");
        String type = (String)values.get("type");
        if ((apiKey == null) || (apiSecret == null) || (type == null)){
            masterHub.getLogicLogger().error("Missing required params:" + values);
            return null;
        }

        if (("MTGOX".equals(type) == false)&&("BITCUREX".equals(type) == false)){
            masterHub.getLogicLogger().error("Type must be MTGOX or BITCUREX");
            parent.sendMissingOrBadParam("type", "Type must be MTGOX or BITCUREX");
            return null;            
        }
        
        ClientRequestAddAccount cr = new ClientRequestAddAccount(parent, 
                        masterHub, marketEngine);
        cr.setLid(values.get("lid"));
        cr.setApiKey(apiKey);
        cr.setApiSecret(apiSecret);
        cr.setType(type);
        cr.setParams((Map<String, Object>) values.get("config"));
        return cr;
    }

    /**
     * @param values
     * @return
     */

    @Override
    public void addChildBranch(LogicBranch child) {
	    //nothing here
    }

    @Override
    public void removeChildBranch(LogicBranch child) {
    	//nothing here
    }

}
