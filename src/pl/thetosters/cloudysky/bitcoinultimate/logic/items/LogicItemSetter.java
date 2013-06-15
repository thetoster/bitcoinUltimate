/*
 * LogicItemSetter.java, version: 1.0.0
 * Date: 03-06-2013 22:22:28
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
import java.util.Set;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemSetter extends LogicItem{

    Map<String, Object> toSet = new HashMap<>();
    
    @Override
    public void setData(String key, Object value){
        super.setData(key, value);
        if ((key.equals("nextId") == false) && (key.equals("id") == false)){
            toSet.put(key, value);
        }
    }
    
    @Override
    public void getData(Map<String, Object> data){
        super.getData(data);
        data.putAll(toSet);
    }
    
    @Override
    public LogicItem execute(Map<String, Object> params) {
        params.putAll(toSet);
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
