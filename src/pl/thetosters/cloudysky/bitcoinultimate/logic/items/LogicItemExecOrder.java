/*
 * LogicItemExecOrder.java, version: 1.0.0
 * Date: 02-06-2013 17:29:32
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.items;

import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.thetosters.cloudysky.bitcoinultimate.logic.RequestExecutor;
import pl.thetosters.cloudysky.server.misc.AutoConvertor;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemExecOrder extends LogicItem {

    public enum Type{
        UNDEFINED, 
        SELL_BTC, //try to sell BTC 
        BUY_BTC,  //try to buy BTC
        CANCEL    //cancel previous order
    }
    
    private String amountVar, priceVar, orderVar;
    private Type type = Type.UNDEFINED;
    
    @Override
    public void setData(String key, Object value){
        super.setData(key, value);
        
        if (key.equals("amountVar") == true){
            amountVar = (String)value;

        } else if (key.equals("priceVar") == true){
            priceVar = (String)value;
            
        } else if (key.equals("orderVar") == true){
            orderVar = (String)value;
            
        } else if (key.equals("type") == true){
            type = Type.valueOf((String)value);
        }
    }
    
    @Override
    public void getData(Map<String, Object> data){
        super.getData(data);
        
        data.put("amountVar", amountVar);
        data.put("priceVar", priceVar);
        data.put("orderVar", orderVar);
        data.put("type", type.toString());
    }
    
    @Override
    public LogicItem execute(Map<String, Object> params) {
        if (type == Type.UNDEFINED){
            return null;
        }
        
        RequestExecutor reqExec = (RequestExecutor)params.get("executor");
        double price = AutoConvertor.asDouble(params.get(priceVar));
        double amount = AutoConvertor.asDouble(params.get(amountVar));
        String order = (String)params.get(orderVar);
        
        switch(type){
            case BUY_BTC:
                order = reqExec.addBuyTransaction((String)params.get("botId"), 
                                price, amount);
                params.put(orderVar, order);
                addLog("[ORDER] buy BTC amount:" + amount + " by price:" + price 
                                + " orderId(" + orderVar + ")=" + order, params);
                break;
            case SELL_BTC:
                order = reqExec.addSellTransaction((String)params.get("botId"),
                                price, amount);
                params.put(orderVar, order);
                addLog("[ORDER] sell BTC amount:" + amount + " by price:" + price 
                                + " orderId(" + orderVar + ")=" + order, params);
                break;
            case CANCEL:
                reqExec.cancelOrder((String)params.get("botId"), order);
                addLog("[ORDER] cancel orderId(" + orderVar + ")=" + order, params);
                break;
            default:
                break;
        }
        return onNextItem;
    }
    public boolean validate(Set<LogicItem> traversed, List<String> messages, 
                    boolean lastState){
        //prevent loop
        if (traversed.contains(this) == true){
            return lastState;
        }
        traversed.add(this);

        switch(type){
            case UNDEFINED:
                lastState = false;
                messages.add(id + " Error: No order type is set"); 
                break;
            case CANCEL:
                if (AutoConvertor.isEmpty(orderVar) == true){
                    lastState = false;
                    messages.add(id + " Error: No varible name for CANCEL order is set");                     
                }
                break;
            case BUY_BTC:
            case SELL_BTC:
                if (AutoConvertor.isEmpty(priceVar) == true){
                    lastState = false;
                    messages.add(id + " Error: No varible name for obtain price is set");                     
                }
                if (AutoConvertor.isEmpty(amountVar) == true){
                    lastState = false;
                    messages.add(id + " Error: No varible name for obtain amount is set");                     
                }
                if (AutoConvertor.isEmpty(orderVar) == true){
                    messages.add(id + " Warning: No varible name for storing order id is set");                     
                }
                break;
                
        }
        
        if (onNextItem != null){
            return lastState & onNextItem.validate(traversed, messages, lastState);
        } else {
            return lastState;
        }

    }
}