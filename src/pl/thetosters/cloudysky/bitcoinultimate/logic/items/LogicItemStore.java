/*
 * LogicItemStore.java, version: 1.0.0
 * Date: 02-06-2013 18:29:43
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pl.thetosters.cloudysky.bitcoinultimate.entities.LogicItemStorageEntity;
import pl.thetosters.cloudysky.server.MasterHub;
import pl.thetosters.cloudysky.server.misc.AutoConvertor;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemStore extends LogicItem{
    
    //jak true to zapisujemy wartosci do bazy, jak false to z niej odczytujemy
    private boolean storeValues;
    
    @Override
    public void setData(String key, Object value){
        super.setData(key, value);
        
        if (key.equals("storeValues") == true){
            storeValues = AutoConvertor.asBoolean(value);
        }
    }
    
    @Override
    public void getData(Map<String, Object> data){
        super.getData(data);
        data.put("storeValues", storeValues);
    }
    
    @Override
    public LogicItem execute(Map<String, Object> params) {
        if (storeValues == true){
            //serialize
            Map<String, Object> v = new HashMap<>();
            for(Entry<String, Object> e : params.entrySet()){
                Object val = e.getValue();
                if ((val instanceof Number) || (val instanceof Boolean) ||
                    (val instanceof String)){
                    v.put(e.getKey(), val);
                }
            }
            MasterHub hub = (MasterHub)params.get("masterHub");
            LogicItemStorageEntity ent = new LogicItemStorageEntity();
            ent.setId((String)params.get("botId"));
            ent.setValues(v);
            hub.getEntityFactory().storeEntity(ent, true);
            addLog("[STORE] save " + v, params);
        } else {
            //deserialize
            MasterHub hub = (MasterHub)params.get("masterHub");
            String id = (String)params.get("botId");
            LogicItemStorageEntity ent = (LogicItemStorageEntity)hub.
                            getEntityFactory().requestEntity(
                                            LogicItemStorageEntity.class, 
                                            LogicItemStorageEntity.ID, id);
            if (ent != null){
                params.putAll(ent.getValues());
            }
            addLog("[STORE] load " + (ent != null ? ent.getValues() : ""), params);
        }
        return onNextItem; 
    }
    
    public boolean validate(Set<LogicItem> traversed, List<String> messages, 
                    boolean lastState){
        //prevent loop
        if (traversed.contains(this) == true){
            return lastState;
        }
        traversed.add(this);
        
        if (onNextItem != null){
            return lastState & onNextItem.validate(traversed, messages, lastState);
        } else {
            return lastState;
        }
    }
}
