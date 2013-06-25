/*
 * LogLogicEntity.java, version: 1.0.0
 * Date: 16-06-2013 20:49:18
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import pl.thetosters.cloudysky.server.entities.LogicEntity;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogLogicEntity implements Serializable, LogicEntity{

    private static final long serialVersionUID = 2694227074588049945L;
    private Date time;
    private int iteration;
    private String botId;
    private String accountId;
    private List<String> log;
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
     * @return the log
     */
    public List<String> getLog() {
        return log;
    }
    /**
     * @param log the log to set
     */
    public void setLog(List<String> log) {
        this.log = log;
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
