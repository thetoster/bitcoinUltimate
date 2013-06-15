/*
 * PeriodicTimerTask.java, version: 1.0.0
 * Date: 10-10-2012 19:53:26
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic;

import java.util.TimerTask;

import pl.thetosters.cloudysky.bitcoinultimate.logic.requests.InnerRequestMarketCheck;
import pl.thetosters.cloudysky.server.MasterHub;
import pl.thetosters.cloudysky.server.logic.InnerRequest;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class PeriodicTimerTask extends TimerTask {

    private MasterHub hub;
    
    public PeriodicTimerTask(MasterHub h){
        hub = h;
    }
    
    @Override
    public void run() {
        InnerRequest r = new InnerRequestMarketCheck(hub);
        hub.getWorkLine().add(r);
    }
}
