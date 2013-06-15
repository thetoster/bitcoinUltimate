/*
 * MarketState.java, version: 1.0.0
 * Date: 25-05-2013 17:48:30
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.entities;
import java.io.Serializable;
import java.util.Date;

import pl.thetosters.cloudysky.server.entities.LogicEntity;

/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class MarketStateEntity implements LogicEntity,Serializable {
    
    private static final long serialVersionUID = 2498014156623527743L;
    private Date time;
    private double minPrice, maxPrice, avrPrice, weightedAvrPrice;
    private double volume;
    private String market;
    
    
    /**
     * @return the market
     */
    public String getMarket() {
        return market;
    }
    /**
     * @param market the market to set
     */
    public void setMarket(String market) {
        this.market = market;
    }
    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }
    /**
     * @return the minPrice
     */
    public double getMinPrice() {
        return minPrice;
    }
    /**
     * @param minPrice the minPrice to set
     */
    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }
    /**
     * @return the maxPrice
     */
    public double getMaxPrice() {
        return maxPrice;
    }
    /**
     * @param maxPrice the maxPrice to set
     */
    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }
    /**
     * @return the avrPrice
     */
    public double getAvrPrice() {
        return avrPrice;
    }
    /**
     * @param avrPrice the avrPrice to set
     */
    public void setAvrPrice(double avrPrice) {
        this.avrPrice = avrPrice;
    }
    /**
     * @return the weightedAvrPrice
     */
    public double getWeightedAvrPrice() {
        return weightedAvrPrice;
    }
    /**
     * @param weightedAvrPrice the weightedAvrPrice to set
     */
    public void setWeightedAvrPrice(double weightedAvrPrice) {
        this.weightedAvrPrice = weightedAvrPrice;
    }
    /**
     * @return the volume
     */
    public double getVolume() {
        return volume;
    }
    /**
     * @param volume the volume to set
     */
    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "MarketState [market=" + market + ", time=" + time 
                        + ", minPrice=" + minPrice
                        + ", maxPrice=" + maxPrice + ", avrPrice=" + avrPrice
                        + ", weightedAvrPrice=" + weightedAvrPrice
                        + ", volume=" + volume + "]";
    }
}
