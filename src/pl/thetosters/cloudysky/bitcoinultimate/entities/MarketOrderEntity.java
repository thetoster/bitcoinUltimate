/*
 * MarketOrder.java, version: 1.0.0
 * Date: 25-05-2013 19:02:45
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
public class MarketOrderEntity implements LogicEntity,Serializable {
    
    private static final long serialVersionUID = -4230381612041713206L;
    public static final String ID = "oid";
    private String market;
    private String oid;
    private boolean sellBTC;    //true -> order of sell BTC (Ask)
    private double amount;
    private double price;
    private Date time;
    private String state;
    private String botId;
    private String accountId;
    private boolean tracked;    //true -> order analyzer should keep eye on it
    
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
     * @return the oid
     */
    public String getOid() {
        return oid;
    }
    /**
     * @param oid the oid to set
     */
    public void setOid(String oid) {
        this.oid = oid;
    }
    /**
     * @return the sellBTC
     */
    public boolean isSellBTC() {
        return sellBTC;
    }
    /**
     * @param sellBTC the sellBTC to set
     */
    public void setSellBTC(boolean sellBTC) {
        this.sellBTC = sellBTC;
    }
    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }
    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }
    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
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
     * @return the state
     */
    public String getState() {
        return state;
    }
    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * @return the botId
     */
    public String getBotId() {
        return botId;
    }
    /**
     * @param botId the botId to set
     */
    public void setBotId(String botId) {
        this.botId = botId;
    }
    // TODO: Napisać dokumentacje
    @Override
    public String toString() {
        return "MarketOrderEntity [market=" + market + ", oid=" + oid
                        + ", sellBTC=" + sellBTC + ", amount=" + amount
                        + ", price=" + price + ", time=" + time + ", state="
                        + state + ", botId=" + botId + ", accountId="
                        + accountId + ", tracked=" + tracked + "]";
    }
    /**
     * @return the accountId
     */
    public String getAccountId() {
        return accountId;
    }
    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    /**
     * @return the tracked
     */
    public boolean isTracked() {
        return tracked;
    }
    
    /**
     * @param tracked the tracked to set
     */
    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                        + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result + ((oid == null) ? 0 : oid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MarketOrderEntity other = (MarketOrderEntity) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        if (oid == null) {
            if (other.oid != null)
                return false;
        } else if (!oid.equals(other.oid))
            return false;
        return true;
    }
    
    

}
