/*
 * MarketApi.java, version: 1.0.0
 * Date: 26-05-2013 20:29:42
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.markets;
import java.util.List;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.entities.AccountStateEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketOrderEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;

/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public interface MarketApi {

    public abstract AccountStateEntity getFounds() throws Exception;

    public abstract MarketStateEntity getTicker() throws Exception;

    public abstract List<MarketOrderEntity> getOrderBook() throws Exception;

    public abstract List<MarketOrderEntity> getOrders() throws Exception;

    public abstract String buyBTC(double amount, double price) throws Exception;

    public abstract String sellBTC(double amount, double price) throws Exception;

    public abstract boolean cancelOrder(String oid, String type) throws Exception;

    public abstract void configure(Map<String, Object> params);

    public abstract Map<String, Object> getConfig();

}