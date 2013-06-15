/*
 * AccountStateEntity.java, version: 1.0.0
 * Date: 30-05-2013 21:29:06
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
public class AccountStateEntity implements LogicEntity, Serializable{
 
    private static final long serialVersionUID = 5366182874709030200L;
    private double pln;
    private double btc;
    private Date time;
    private String accountId;
    
    /**
     * @return the pln
     */
    public double getPln() {
        return pln;
    }
    /**
     * @param pln the pln to set
     */
    public void setPln(double pln) {
        this.pln = pln;
    }
    /**
     * @return the btc
     */
    public double getBtc() {
        return btc;
    }
    /**
     * @param btc the btc to set
     */
    public void setBtc(double btc) {
        this.btc = btc;
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
    
    
}
