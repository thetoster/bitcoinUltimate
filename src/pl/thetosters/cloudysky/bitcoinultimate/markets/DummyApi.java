/*
 * DummyApi.java, version: 1.0.0
 * Date: 22-06-2013 18:47:40
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.markets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import pl.thetosters.cloudysky.bitcoinultimate.entities.AccountStateEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketOrderEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;
import pl.thetosters.cloudysky.server.misc.AutoConvertor;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class DummyApi implements MarketApi{
    private Map<String, Object[]> orders = new HashMap<>();
    private Map<String, Object[]> closedOrders = new HashMap<>();
    private double ticker, avrPrice = 365, tickerAmpl = 70, tickerStdDev = 5;
    private double drift = avrPrice, driftMean=365, driftStdDev=30;
    private int driftTicker, driftCount=40;
    private Random rand = new Random();
    private double availPLN, availBTC;
    
    @Override
    public AccountStateEntity getFounds() throws Exception {
        AccountStateEntity ase = new AccountStateEntity ();
        ase.setBtc(availBTC);
        ase.setPln(availPLN);
        ase.setTime(new Date());
        return ase;
    }

    @Override
    public MarketStateEntity getTicker() throws Exception {
        //calc drift
        driftTicker++;
        if (driftTicker >= driftCount){
            driftTicker = 0;
            drift = driftMean + rand.nextGaussian() * driftStdDev;
        }
        if (drift != avrPrice){
            if (Math.abs(drift-avrPrice) < 15){
                avrPrice = drift;
            } else {
                avrPrice += (drift - avrPrice)/3;
            }
        }
        
        //calc current price
        ticker += Math.PI/18;
        double curPrice = avrPrice + Math.sin( ticker ) * tickerAmpl;
        
        MarketStateEntity mse = new MarketStateEntity();
        mse.setAvrPrice(curPrice);
        double max = 0;
        while(max < curPrice){
            max = curPrice + rand.nextGaussian() * tickerStdDev;
        }
        double min = curPrice;
        while(min >= curPrice){
            min = curPrice + rand.nextGaussian() * tickerStdDev;
        }
        
        mse.setMaxPrice(max);
        mse.setMinPrice(min);
        mse.setMarket("Dummy");
        mse.setTime(new Date());
        mse.setVolume(140);
        mse.setWeightedAvrPrice(curPrice);
        
        //update orders
        Set<Entry<String, Object[]>> oS = orders.entrySet();
        for(Entry<String, Object[]> e : oS){
            double amount = ((Double)(e.getValue()[1])).doubleValue();
            double price = ((Double)(e.getValue()[2])).doubleValue();
            String type = (String)(e.getValue()[0]);
            if (type.equals("sell") == true){
                if (price <= curPrice){
                    closedOrders.put(e.getKey(), e.getValue());
                    oS.remove(e);
                    double tmp = Math.min(availBTC, amount);
                    availBTC -= tmp;
                    availPLN += tmp * price;
                }
            } else {
                if (price >= curPrice){
                    closedOrders.put(e.getKey(), e.getValue());
                    oS.remove(e);
                    double tmp = amount * price;
                    tmp = Math.min(availPLN, tmp);
                    availBTC += amount * (tmp/(amount * price));
                    availPLN -= tmp;
                }
                
            }
        }
        System.out.println("ORDERS:" + orders.keySet());
        if (orders.size() > 1){
            System.out.println("??");
        }
        return mse;
    }

    @Override
    public List<MarketOrderEntity> getOrderBook() throws Exception {
        return new ArrayList<>();
    }

    @Override
    public List<MarketOrderEntity> getOrders() throws Exception {
        List<MarketOrderEntity> result = new ArrayList<>();
        for(Entry<String, Object[]> e : orders.entrySet()){
            double amount = ((Double)(e.getValue()[1])).doubleValue();
            double price = ((Double)(e.getValue()[2])).doubleValue();
            String type = (String)(e.getValue()[0]);
            
            MarketOrderEntity moe = new MarketOrderEntity();
            moe.setAmount(amount);
            moe.setOid(e.getKey());
            moe.setPrice(price);
            moe.setSellBTC(type.equals("sell"));
            moe.setState("open");
            moe.setTime(new Date());
            moe.setMarket("DUMMY");
            result.add(moe);
        }
        return result;
    }
int cntB = 0, cntS = 0;
    @Override
    public String buyBTC(double amount, double price) throws Exception {
        //String oid = "B_" + UUID.randomUUID().toString();
        String oid = "B_" + cntB;
        cntB++;
        orders.put(oid, new Object[]{"buy", amount, price});
        return oid;
    }

    @Override
    public String sellBTC(double amount, double price) throws Exception {
        //String oid = "S_" + UUID.randomUUID().toString();
        String oid = "S_" + cntS;
        cntS++;
        orders.put(oid, new Object[]{"sell", amount, price});
        return oid;
    }

    @Override
    public boolean cancelOrder(String oid, String type) throws Exception {
        orders.remove(oid);
        return true;
    }
    
    @Override
    public void configure(Map<String, Object> params){
        if (params == null){
            return;
        }
        ticker = AutoConvertor.getAsDouble("ticker", params, ticker);
        avrPrice  = AutoConvertor.getAsDouble("avrPrice", params, avrPrice);
        tickerAmpl  = AutoConvertor.getAsDouble("tickerAmpl", params, tickerAmpl);
        tickerStdDev = AutoConvertor.getAsDouble("tickerStdDev", params, tickerStdDev);
        drift = AutoConvertor.getAsDouble("drift", params, drift);
        driftMean = AutoConvertor.getAsDouble("driftMean", params, driftMean);
        driftStdDev = AutoConvertor.getAsDouble("driftStdDev", params, driftStdDev);
        availPLN = AutoConvertor.getAsDouble("availPLN", params, availPLN);
        availBTC = AutoConvertor.getAsDouble("availBTC", params, availBTC);
        driftTicker = AutoConvertor.getAsInt("driftTicker", params, driftTicker);
        driftCount = AutoConvertor.getAsInt("driftCount", params, driftCount);
    }
    
    @Override
    public Map<String, Object> getConfig(){
        Map<String, Object> result = new HashMap<>();
        result.put("ticker", ticker);
        result.put("avrPrice", avrPrice);
        result.put("tickerAmpl", tickerAmpl);
        result.put("tickerStdDev", tickerStdDev);
        result.put("drift", drift);
        result.put("driftMean", driftMean);
        result.put("driftStdDev", driftStdDev);
        result.put("availPLN", availPLN);
        result.put("availBTC", availBTC);
        result.put("driftTicker", driftTicker);
        result.put("driftCount", driftCount);
        
        return result;
    }
}
