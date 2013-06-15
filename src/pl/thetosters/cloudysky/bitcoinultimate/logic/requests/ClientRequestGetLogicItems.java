/*
 * ClientRequestGetLogicItems.java, version: 1.0.0
 * Date: 08-06-2013 16:02:23
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.logic.Account;
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
public class ClientRequestGetLogicItems  extends ClientRequestBase {

    private String accountId;
    private MarketEngine engine;
    /**
     * @param parent
     * @param masterHub
     */
    public ClientRequestGetLogicItems(ClientSession parent, MasterHub masterHub,
                    MarketEngine marketEngine) {
        super(parent, masterHub);
        engine = marketEngine;
    }
    
    @Override
    public void run() {
        try{
            //masterHub.getEntityFactory().storeEntity(autEnt, true);
            Account acc = engine.getAccount(accountId);
            if (acc == null){
                masterHub.getLogicLogger().error("Account not found");
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("error", "Account not found");
                sendResponse(Helpers.embedMap("accountStatus",m));
                return;                
            }
            if (acc.getOwner().equals(parent.getUsername()) == false){
                masterHub.getLogicLogger().error("Security: accountStatus requested from user which is not account owner");
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("error", "You are not owner of this account");
                sendResponse(Helpers.embedMap("accountStatus",m));
                return;
            }
            
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("result", "ok");
            m.put("accountId", accountId);
            Map<String, LogicItem> map = acc.getItems();
            List<Map<String, Object>> res = new ArrayList<>();
            for(LogicItem item : map.values()){
                Map<String, Object> data = new HashMap<>();
                item.getData(data);
                data.put("id", item.getId());
                res.add(data);
            }
            m.put("item", res);
            sendResponse(Helpers.embedMap("accountStatus",m));
        } catch (Exception e){
            masterHub.getLogicLogger().error("Error in accountStatus", e);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "unknown problem");
            sendResponse(Helpers.embedMap("accountStatus",m));
        }
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
