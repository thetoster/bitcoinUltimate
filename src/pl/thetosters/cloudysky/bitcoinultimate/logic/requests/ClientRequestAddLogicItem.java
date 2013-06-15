/*
 * ClientRequestAddLogicItem.java, version: 1.0.0
 * Date: 08-06-2013 13:09:51
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.thetosters.cloudysky.bitcoinultimate.entities.LogicItemEntity;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account;
import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItem;
import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItemFactory;
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
public class ClientRequestAddLogicItem  extends ClientRequestBase {

    private String accountId;
    private MarketEngine engine;
    private Map<String, Object> data;
    private String type;
    
    /**
     * @param parent
     * @param masterHub
     */
    public ClientRequestAddLogicItem(ClientSession parent, MasterHub masterHub,
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
            sendResponse(Helpers.embedMap("AddLogicItem",m));
            return null;                
        }
        if (acc.getOwner().equals(parent.getUsername()) == false){
            masterHub.getLogicLogger().error("Security: AddLogicItem requested from user which is not account owner");
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "You are not owner of this account");
            sendResponse(Helpers.embedMap("AddLogicItem",m));
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
            String id = (String)data.get("id");
            if (acc.getItemById(id) != null){
                masterHub.getLogicLogger().error("AddLogicItem item with id:" 
                                + id + " already exists!");
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("error", "item with id:" + id + " already exists!");
                sendResponse(Helpers.embedMap("AddLogicItem",m));
                return;
            }
            LogicItem li = LogicItemFactory.buildItem(type, data);

            Map<String, Object> m = new HashMap<String, Object>();
            if (li == null){
                m.put("error", "Can't build logic item.");
            } else {
                acc.getItems().put(li.getId(), li);
                Set<LogicItem> s = new HashSet<>();
                List<String> msg = new ArrayList<>();
                boolean v = li.validate(s, msg, true);
                m.put("itemId", li.getId());
                m.put("result", "ok");
                m.put("isValid", v);
                m.put("validationMsg", msg);
            }
            LogicItemEntity lie = LogicItemFactory.convertToStorageEntity(li);
            lie.setAccountId(accountId);
            masterHub.getEntityFactory().storeEntity(lie, false);
            sendResponse(Helpers.embedMap("AddLogicItem",m));
        } catch (Exception e){
            masterHub.getLogicLogger().error("Error in AddLogicItem", e);
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("error", "unknown problem");
            sendResponse(Helpers.embedMap("AddLogicItem",m));
        }
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * @param data
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }
}
