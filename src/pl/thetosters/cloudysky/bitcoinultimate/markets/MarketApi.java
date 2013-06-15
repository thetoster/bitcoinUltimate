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

    public abstract AccountStateEntity getFounds();

    public abstract MarketStateEntity getTicker();

    public abstract List<MarketOrderEntity> getOrderBook();

    public abstract List<MarketOrderEntity> getOrders();

    public abstract String buyBTC(double amount, double price);

    public abstract String sellBTC(double amount, double price);

    public abstract boolean cancelOrder(String oid, String type);

}