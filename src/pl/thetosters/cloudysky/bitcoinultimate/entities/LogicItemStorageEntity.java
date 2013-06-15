/*
 * LogicItemStorageEntity.java, version: 1.0.0
 * Date: 02-06-2013 18:45:56
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
public class LogicItemStorageEntity implements LogicEntity, Serializable{

    private static final long serialVersionUID = -8330979728496236520L;

    public static final String ID = "id";

    private Map<String, Object> values;
    private String id;
    
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
     * @return the values
     */
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(Map<String, Object> values) {
        this.values = values;
    }
}
