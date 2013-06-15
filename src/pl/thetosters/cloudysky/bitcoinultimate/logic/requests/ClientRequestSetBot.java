/*
 * ClientRequestSetBot.java, version: 1.0.0
 * Date: 08-06-2013 11:09:03
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
import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItem;
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
public class ClientRequestSetBot extends ClientRequestBase {

    private String accountId;
    private MarketEngine engine;
    private String botId;
    private double plnChange;
    private double btcChange;
    private int enableChange;
    private String workPlanId;
    /**
     * @param parent
     * @param masterHub
     */
    public ClientRequestSetBot(ClientSession parent, MasterHub masterHub,
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
            sendResponse(Helpers.embedMap("setBot",m));
            return null;                
        }
        if (acc.getOwner().equals(parent.getUsername()) == false){
            masterHub.getLogicLogger().error("Security: setBot requested from user which is not account owner");
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "You are not owner of this account");
            sendResponse(Helpers.embedMap("setBot",m));
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
                sendResponse(Helpers.embedMap("setBot",m));
                return;
            }
            Map<String, Object> m = new HashMap<String, Object>();
            //--------- change enabled state
            if (enableChange == 0){
                bot.setEnabled(false);
            } else if (enableChange > 0){
                bot.setEnabled(true);
            }
            m.put("enabled", bot.getEnabled());
            
            //--------- change PLN
            if (plnChange != 0){
                double tmp = bot.getCurrentPLN();
                tmp += plnChange;
                bot.setCurrentPLN(tmp);
            }
            m.put("curentPLN", bot.getCurrentPLN());
            
            //--------- change BTC
            if (btcChange != 0){
                double tmp = bot.getCurrentBTC();
                tmp += btcChange;
                bot.setCurrentBTC(tmp);
            }
            m.put("curentBTC", bot.getCurrentBTC());
            
            //--------- change workplan
            if (workPlanId != null){
                LogicItem li = acc.getItemById(workPlanId);
                if (li == null){
                    m.put("error", "No logic item with id:" + workPlanId);
                } else {
                    bot.setWorkPlan(li);
                }
            }
            if (bot.getWorkPlan() == null){
                m.put("workPlanId", "");
            } else {
                m.put("workPlanId", bot.getWorkPlan().getId());
            }
            
            engine.saveBot(acc, bot);
            m.put("result", "ok");
            m.put("botId", bot.getId());
            sendResponse(Helpers.embedMap("setBot",m));
        } catch (Exception e){
            masterHub.getLogicLogger().error("Error in setBot", e);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "unknown problem");
            sendResponse(Helpers.embedMap("setBot",m));
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

    /**
     * @param asDouble
     */
    public void setPLNChange(double change) {
        this.plnChange = change;
    }

    public void setBTCChange(double change) {
        this.btcChange = change;
    }

    /**
     * @param asDouble
     */
    public void setEnabled(int enabled) {
        this.enableChange = enabled;
        
    }

    /**
     * @param string
     */
    public void setWorkplan(String id) {
        this.workPlanId = id;
    }
}
