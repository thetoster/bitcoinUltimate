/*
 * LogicItemFactory.java, version: 1.0.0
 * Date: 30-05-2013 23:17:06
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import pl.thetosters.cloudysky.bitcoinultimate.entities.LogicItemEntity;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemFactory {
    private static Logger logger;
    private final static Map<String, Class<? extends LogicItem>> typeMap = 
        new HashMap<>();
        
    static{
        typeMap.put("cond", LogicItemCondition.class);
        typeMap.put("calc", LogicItemCalc.class);
        typeMap.put("pred", LogicItemPredict.class);
        typeMap.put("order", LogicItemExecOrder.class);
        typeMap.put("store", LogicItemStore.class);
        typeMap.put("set", LogicItemSetter.class);
        typeMap.put("exec", LogicItemExecute.class);
    }
    
    public static LogicItem buildItem(LogicItemEntity ent){
        
        Class<? extends LogicItem> buildClass = typeMap.get(ent.getType());
        if (buildClass == null){
            logger.error("Error no type defined:" + ent.getType());
            return null;
        }
        LogicItem it = null;
        try {
            it = buildClass.newInstance();
        } catch (Exception e) {
            logger.error("Can't build LogicItem instance", e);
            return null;
        }
        it.setId(ent.getId());
        for(Map.Entry<?, ?> m : ent.getData().entrySet() ){
            it.setData((String)m.getKey(), m.getValue());
        }
        
        if (it.getId() == null){
            logger.error("No id in logic item definition:" + ent);
            return null;
        }
        return it;
    }
    
    /**
     * @param type
     * @param data
     * @return
     */
    public static LogicItem buildItem(String type, Map<String, Object> data) {
        LogicItemEntity lie = new LogicItemEntity();
        lie.setType(type);
        lie.setData(data);
        return buildItem(lie);
    }
    
    public static LogicItemEntity convertToStorageEntity(LogicItem it){
        LogicItemEntity lie = new LogicItemEntity();
        for(Entry<String, Class<? extends LogicItem>> e : typeMap.entrySet() ){
            Class<? extends LogicItem> cl = e.getValue();
            if (cl.isInstance(it) == true){
                lie.setType(e.getKey());
                break;
            }
        }
        lie.setId(it.getId());
        Map<String, Object> data = new HashMap<>();
        it.getData(data);
        lie.setData(data);
        return lie;
    }
    /**
     * @return the logger
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public static void setLogger(Logger logger) {
        LogicItemFactory.logger = logger;
    }

}
