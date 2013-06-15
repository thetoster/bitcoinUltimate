/*
 * ClientRequestDelLogicItem.java, version: 1.0.0
 * Date: 08-06-2013 16:12:28
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.requests;

import java.util.HashMap;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.entities.LogicItemEntity;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account;
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
public class ClientRequestDelLogicItem extends ClientRequestBase {

    private String accountId;
    private MarketEngine engine;
    private String id;
    /**
     * @param parent
     * @param masterHub
     */
    public ClientRequestDelLogicItem(ClientSession parent, MasterHub masterHub,
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
            sendResponse(Helpers.embedMap("delBot",m));
            return null;                
        }
        if (acc.getOwner().equals(parent.getUsername()) == false){
            masterHub.getLogicLogger().error("Security: delBot requested from user which is not account owner");
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "You are not owner of this account");
            sendResponse(Helpers.embedMap("delBot",m));
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

            if (acc.getItemById(id) == null){
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("error", "No logic item with given id");
                sendResponse(Helpers.embedMap("delBot",m));
            } else {
                acc.getItems().remove(id);
                LogicItemEntity ent = new LogicItemEntity();
                ent.setAccountId(accountId);
                ent.setId(id);
                masterHub.getEntityFactory().removeEntity(ent);
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("result", "ok");
                sendResponse(Helpers.embedMap("delBot", m));
            }
        } catch (Exception e){
            masterHub.getLogicLogger().error("Error in delBot", e);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "unknown problem");
            sendResponse(Helpers.embedMap("delBot",m));
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
    public void setId(String id) {
        this.id = id;
    }
}
