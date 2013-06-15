/*
 * OrderAnalizer.java, version: 1.0.0
 * Date: 09-06-2013 12:56:50
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.markets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketOrderEntity;
import pl.thetosters.cloudysky.server.MasterHub;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class OrderAnalizer {
    private Map<String, MarketOrderEntity> map;
    private MasterHub masterHub;
    
    public OrderAnalizer(MasterHub hub){
        masterHub = hub;
    }
    
    public void checkState(MarketApi api){
        List<MarketOrderEntity> list = api.getOrders();
        map = new HashMap<String, MarketOrderEntity>();
        for(MarketOrderEntity o : list){
            map.put(o.getOid(), o);
        }
    }

    public void addOrder(MarketOrderEntity order){
        map.put(order.getOid(), order);
    }
    
    public boolean isOrderOpen(String orderId){
        return map.get(orderId) != null;
    }
    
    public void storeOrder(String orderId, String ownerBotId){
        MarketOrderEntity ent = map.get(orderId);
        if (ent == null){
            masterHub.getLogicLogger().error("Requested storage of Market order with id " 
                            + orderId + " but this order is unknown, requested by bot:" 
                            + ownerBotId);
        }
        ent.setBotId(ownerBotId);
        masterHub.getEntityFactory().storeEntity(ent, true);
    }
    
    public int isOrderTypeBuy(String orderId){
        MarketOrderEntity ent = map.get(orderId);
        if (ent == null){
            ent = masterHub.getEntityFactory().requestEntity(
                            MarketOrderEntity.class, MarketOrderEntity.ID, 
                            orderId);
            if (ent != null){
                map.put(ent.getOid(), ent);
            }
        }
        
        if (ent == null){
            //unknown
            return -1;
        }
        return ent.isSellBTC() == false ? 1 : 0;
    }
}
