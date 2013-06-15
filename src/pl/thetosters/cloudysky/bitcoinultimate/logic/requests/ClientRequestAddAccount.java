/*
 * ClientRequestAddBook.java, version: 1.0.0
 * Date: 21-08-2012 20:41:55
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
public class ClientRequestAddAccount extends ClientRequestBase {

    private String type, apiKey, apiSecret;
    private MarketEngine engine;
    /**
     * @param parent
     * @param masterHub
     */
    public ClientRequestAddAccount(ClientSession parent, MasterHub masterHub,
                    MarketEngine marketEngine) {
        super(parent, masterHub);
        engine = marketEngine;
    }
    
    @Override
    public void run() {
        try{
            //masterHub.getEntityFactory().storeEntity(autEnt, true);
            Account.Type acType = Account.Type.valueOf(type);
            String id = engine.createAccount(acType, parent.getUsername(), 
                            apiKey, apiSecret);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("result", "ok");
            m.put("accountId", id);
            sendResponse(Helpers.embedMap("addAccount",m));
        } catch (IllegalArgumentException e){ 
            masterHub.getLogicLogger().error("Error in addAccount", e);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "wrong account type:" + type);
            sendResponse(Helpers.embedMap("addAccount",m));
        } catch (Exception e){
            masterHub.getLogicLogger().error("Error in addAccount", e);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "unknown problem");
            sendResponse(Helpers.embedMap("addAccount",m));
        }
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @param apiSecret the apiSecret to set
     */
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}
