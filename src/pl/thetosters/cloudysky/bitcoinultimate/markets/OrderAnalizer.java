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
import java.util.Map.Entry;
import java.util.Set;

import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketOrderEntity;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account;
import pl.thetosters.cloudysky.server.MasterHub;
import pl.thetosters.cloudysky.server.entities.LogicEntity;
import pl.thetosters.cloudysky.server.storage.SpecializedQuery;
import pl.thetosters.cloudysky.server.storage.StorageLink;
import pl.thetosters.cloudysky.server.storage.SubFactory;

import com.mongodb.BasicDBObject;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class OrderAnalizer {
       
    private Map<String, MarketOrderEntity> map = new HashMap<>();
    private MasterHub masterHub;
    
    public OrderAnalizer(MasterHub hub, String accountId){
        masterHub = hub;
        MarketOrdersQuery tmp = new MarketOrdersQuery(accountId);
        List<LogicEntity> list = hub.getEntityFactory().requestEntity(tmp);
        for(LogicEntity le : list){
            MarketOrderEntity moe = (MarketOrderEntity)le;
            map.put(moe.getOid(), moe);
        }
    }
    
    public void checkState(MarketApi api, Account acc){
        try{
            List<MarketOrderEntity> list = api.getOrders();
            for (MarketOrderEntity o : list) {
                o.setAccountId(acc.getId());
            }
            handleClosed(list, acc);
            for (MarketOrderEntity o : list) {
                if (map.get( o.getOid() ) == null ){
                    //somebody added order? But this is not a bot.
                    map.put(o.getOid(), o);
                }
            }
        } catch (Exception e){
            //not too much :/
        }
    }

    /**
     * @param list
     * @param acc
     */
    private void handleClosed(List<MarketOrderEntity> list, Account acc) {
        Set<Entry<String, MarketOrderEntity>> set = map.entrySet();
        System.out.println("IN ANAL:" + map.keySet());
        for(Entry<String, MarketOrderEntity> ent : set){
            MarketOrderEntity order = ent.getValue(); 
            if (list.contains(order) == true){
                //still exists
                continue;
            }
            //looks like it gone
            set.remove(ent);
            System.out.println("ORDER REMOVED:" + order);
            if (order.isTracked() == false){
                continue;
            }
            if (order.isSellBTC() == true){
                acc.changeBTCAmountDueSell(order.getAmount(), order.getPrice(),
                                order.getBotId());
            } else {
                acc.changeBTCAmountDueBuy(order.getAmount(), order.getPrice(),
                                order.getBotId());
            }
            if (order.getTime() == null){
                System.out.println("dkjf2");
            }
            order.setTracked(false);
            masterHub.getEntityFactory().storeEntity(order, true);
        }
    }

    public void addOrder(MarketOrderEntity order){
        order.setTracked(true);
        if (order.getTime() == null){
            System.out.println("dkjf");
        }
        map.put(order.getOid(), order);
    }
    
    public void cancelOrder(String oid) {
        MarketOrderEntity ent = map.get(oid);
        ent.setTracked(false);
        ent.setState("canceled");
    }
    
    public boolean isOrderOpen(String orderId){
        return map.get(orderId) != null;
    }
    
    public void storeOrder(String orderId){
        MarketOrderEntity ent = map.get(orderId);
        if (ent == null){
            masterHub.getLogicLogger().error("Requested storage of Market order with id " 
                            + orderId + " but this order is unknown");
        }
        masterHub.getEntityFactory().storeEntity(ent, true);
    }
    
    public int isOrderTypeBuy(String orderId){
        MarketOrderEntity ent = map.get(orderId);
        if (ent == null){
            ent = masterHub.getEntityFactory().requestEntity(
                            MarketOrderEntity.class, MarketOrderEntity.ID, 
                            orderId);
//            if (ent != null){
//                map.put(ent.getOid(), ent);
//            }
        }
        
        if (ent == null){
            //unknown
            return -1;
        }
        return ent.isSellBTC() == false ? 1 : 0;
    }
    
    private static class MarketOrdersQuery implements SpecializedQuery{
        private final String accId;
        
        public MarketOrdersQuery(String accountId){
            accId = accountId;
        }
        
        @Override
        public Class<? extends LogicEntity> getEntityClass() {
            return MarketOrderEntity.class;
        }

        @Override
        public boolean isStorageLinkSupported(StorageLink sl) {
            return true;
        }

        @Override
        public Object getQuery(SubFactory sf, Object o) {
            BasicDBObject query = new BasicDBObject();
            query.put("accountId", accId);
            query.put("tracked", true);
            return query;
        }
        
    }


}
