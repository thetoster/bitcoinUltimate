/*
 * InnerRequestBuildListIfNeeded.java, version: 1.0.0
 * Date: 10-10-2012 20:13:22
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.requests;


import pl.thetosters.cloudysky.bitcoinultimate.PluginConstants;
import pl.thetosters.cloudysky.bitcoinultimate.logic.PeriodicTimerTask;
import pl.thetosters.cloudysky.bitcoinultimate.markets.MarketEngine;
import pl.thetosters.cloudysky.server.MasterHub;
import pl.thetosters.cloudysky.server.logic.InnerRequest;

/**
 * Called from {@link PeriodicTimerTask} to perform markets checks and bot 
 * operations.
 * @author Toster
 * @version 1.0.0
 * 
 */
public class InnerRequestMarketCheck extends InnerRequest {

    private MasterHub masterHub;
    
    /**
     * @param hub
     */
    public InnerRequestMarketCheck(MasterHub hub) {
        masterHub = hub;
    }

    @Override
    public void run() {
        MarketEngine me = (MarketEngine)masterHub.getRealmDependentObject(
                        PluginConstants.REALM_NAME, MarketEngine.class);
        me.checkMarkets();
    }
}
