/*
 * AccountEntity.java, version: 1.0.0
 * Date: 29-05-2013 21:39:32
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.entities;

import java.io.Serializable;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.logic.Account.Type;
import pl.thetosters.cloudysky.server.entities.LogicEntity;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class AccountEntity implements Serializable, LogicEntity{
    private static final long serialVersionUID = 4205440395366986101L;
    private Type type;
    private String id;
    private String apiKey;
    private String apiSecret;
    private String ownerLogin;
    private Map<String, Object> config;
    
    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
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
     * @return the apiKey
     */
    public String getApiKey() {
        return apiKey;
    }
    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    /**
     * @return the apiSecret
     */
    public String getApiSecret() {
        return apiSecret;
    }
    /**
     * @param apiSecret the apiSecret to set
     */
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
    
  
    /**
     * @return the ownerLogin
     */
    public String getOwnerLogin() {
        return ownerLogin;
    }
    /**
     * @param ownerLogin the ownerLogin to set
     */
    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        AccountEntity other = (AccountEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    /**
     * @return the config
     */
    public Map<String, Object> getConfig() {
        return config;
    }
    /**
     * @param config the config to set
     */
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
