/*
 * LogicItemEntity.java, version: 1.0.0
 * Date: 30-05-2013 23:12:03
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.entities;

import java.io.Serializable;
import java.util.Map;

import pl.thetosters.cloudysky.server.entities.LogicEntity;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemEntity implements Serializable, LogicEntity{
    
    private static final long serialVersionUID = 4205440395366986101L;

    public static final String ACCOUNT = "accountId";
    
    private String type;
    private String accountId;
    private String id;
    private Map<String,?> data;
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the data
     */
    public Map<String, ?> getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(Map<String, ?> data) {
        this.data = data;
    }
    
    
}
