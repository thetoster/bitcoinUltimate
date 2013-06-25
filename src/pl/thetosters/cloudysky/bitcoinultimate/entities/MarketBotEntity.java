/*
 * MarketBotEntity.java, version: 1.0.0
 * Date: 30-05-2013 23:24:33
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.entities;

import java.io.Serializable;

import pl.thetosters.cloudysky.server.entities.LogicEntity;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class MarketBotEntity implements Serializable, LogicEntity{
    private static final long serialVersionUID = 4205440395366986101L;

    public static final String ACCOUNT = "accountId";

    private String id;
    private String accountId;
    private String workPlanId;
    private int opertationsCount;
    private double totalBuyBTC;
    private double totalSellBTC;
    private double totalBuyPLN;
    private double totalSellPLN;
    private double startPLN;
    private boolean enabled;
    private double currentPLN;  //ile mamy gotówki do dyspozycji
    private double currentBTC;   //ile mamy BC do dyspozycji
    private double basePricePLN;    //bazowa cena dla operacji (ostatnia cena kupna lub sprzedały)
    private int iteration;
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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
     * @return the workPlanId
     */
    public String getWorkPlanId() {
        return workPlanId;
    }
    /**
     * @param workPlanId the workPlanId to set
     */
    public void setWorkPlanId(String workPlanId) {
        this.workPlanId = workPlanId;
    }
    /**
     * @return the totalBuyPLN
     */
    public double getTotalBuyPLN() {
        return totalBuyPLN;
    }
    /**
     * @param totalBuyPLN the totalBuyPLN to set
     */
    public void setTotalBuyPLN(double totalBuyPLN) {
        this.totalBuyPLN = totalBuyPLN;
    }
    /**
     * @return the totalSellPLN
     */
    public double getTotalSellPLN() {
        return totalSellPLN;
    }
    /**
     * @param totalSellPLN the totalSellPLN to set
     */
    public void setTotalSellPLN(double totalSellPLN) {
        this.totalSellPLN = totalSellPLN;
    }
    /**
     * @return the startPLN
     */
    public double getStartPLN() {
        return startPLN;
    }
    /**
     * @param startPLN the startPLN to set
     */
    public void setStartPLN(double startPLN) {
        this.startPLN = startPLN;
    }
    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    /**
     * @return the opertationsCount
     */
    public int getOpertationsCount() {
        return opertationsCount;
    }
    /**
     * @param opertationsCount the opertationsCount to set
     */
    public void setOpertationsCount(int opertationsCount) {
        this.opertationsCount = opertationsCount;
    }
    /**
     * @return the totalBuyBTC
     */
    public double getTotalBuyBTC() {
        return totalBuyBTC;
    }
    /**
     * @param totalBuyBTC the totalBuyBTC to set
     */
    public void setTotalBuyBTC(double totalBuyBTC) {
        this.totalBuyBTC = totalBuyBTC;
    }
    /**
     * @return the totalSellBTC
     */
    public double getTotalSellBTC() {
        return totalSellBTC;
    }
    /**
     * @param totalSellBTC the totalSellBTC to set
     */
    public void setTotalSellBTC(double totalSellBTC) {
        this.totalSellBTC = totalSellBTC;
    }
    /**
     * @return the currentPLN
     */
    public double getCurrentPLN() {
        return currentPLN;
    }
    /**
     * @param currentPLN the currentPLN to set
     */
    public void setCurrentPLN(double currentPLN) {
        this.currentPLN = currentPLN;
    }
    /**
     * @return the currentBC
     */
    public double getCurrentBTC() {
        return currentBTC;
    }
    /**
     * @param currentBC the currentBC to set
     */
    public void setCurrentBTC(double currentBC) {
        this.currentBTC = currentBC;
    }
    /**
     * @return the basePricePLN
     */
    public double getBasePricePLN() {
        return basePricePLN;
    }
    /**
     * @param basePricePLN the basePricePLN to set
     */
    public void setBasePricePLN(double basePricePLN) {
        this.basePricePLN = basePricePLN;
    }
    /**
     * @return the iteration
     */
    public int getIteration() {
        return iteration;
    }
    /**
     * @param iteration the iteration to set
     */
    public void setIteration(int iteration) {
        this.iteration = iteration;
    }  
    
}
