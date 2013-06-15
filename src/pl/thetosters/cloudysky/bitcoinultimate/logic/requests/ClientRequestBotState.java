/*
 * ClientRequestBotState.java, version: 1.0.0
 * Date: 08-06-2013 12:06:53
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.requests;

import java.util.HashMap;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.logic.Account;
import pl.thetosters.cloudysky.bitcoinultimate.logic.MarketBot;
import pl.thetosters.cloudysky.bitcoinultimate.markets.MarketEngine;
import pl.thetosters.cloudysky.server.MasterHub;
import pl.thetosters.cloudysky.server.logic.ClientSession;
import pl.thetosters.cloudysky.server.logic.requests.ClientRequestBase;
import pl.thetosters.cloudysky.server.misc.Helpers;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class ClientRequestBotState  extends ClientRequestBase {

    private String accountId;
    private MarketEngine engine;
    private String botId;
    /**
     * @param parent
     * @param masterHub
     */
    public ClientRequestBotState(ClientSession parent, MasterHub masterHub,
                    MarketEngine marketEngine) {
        super(parent, masterHub);
        engine = marketEngine;
    }
    
    private Account obtainAccount(){
        Account acc = engine.getAccount(accountId);
        if (acc == null){
            masterHub.getLogicLogger().error("Account not found");
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "Account not found");
            sendResponse(Helpers.embedMap("botState",m));
            return null;                
        }
        if (acc.getOwner().equals(parent.getUsername()) == false){
            masterHub.getLogicLogger().error("Security: botState requested from user which is not account owner");
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "You are not owner of this account");
            sendResponse(Helpers.embedMap("botState",m));
            return null;
        }
        return acc;
    }
    
    @Override
    public void run() {
        try{
            //masterHub.getEntityFactory().storeEntity(autEnt, true);
            Account acc = obtainAccount();
            if (acc == null){
                return;
            }
            MarketBot bot = null;
            for(MarketBot b : acc.getBots()){
                if (b.getId().equals(botId) == true){
                    bot = b;
                    break;
                }
            }
            if (bot == null){
                masterHub.getLogicLogger().error("Error bot with " + botId + 
                                " not found");
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("error", "No bot with given id: " + botId);
                sendResponse(Helpers.embedMap("botState",m));
                return;
            }
            Map<String, Object> m = new HashMap<String, Object>();
            bot.getStatus(m);
            m.put("result", "ok");
            m.put("accountId", acc.getId());
            m.put("type", acc.getType().name());
            sendResponse(Helpers.embedMap("botState",m));
        } catch (Exception e){
            masterHub.getLogicLogger().error("Error in botState", e);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "unknown problem");
            sendResponse(Helpers.embedMap("botState",m));
        }
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * @param botId
     */
    public void setBotId(String botId) {
        this.botId = botId;
    }


}
