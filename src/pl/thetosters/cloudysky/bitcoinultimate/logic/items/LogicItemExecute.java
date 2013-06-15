/*
 * LogicItemExecute.java, version: 1.0.0
 * Date: 10-06-2013 20:01:26
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.items;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.thetosters.cloudysky.server.misc.AutoConvertor;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemExecute extends LogicItem{

    private List<Object> params;
    private String objectKey;
    private String methodName;
    private String resultVar;
    
    @SuppressWarnings("unchecked")
    @Override
    public void setData(String key, Object value){
        super.setData(key, value);
        if (key.equals("object") == true) {
            this.objectKey = (String)value;
            
        } else if (key.equals("method") == true) {
            this.methodName = (String)value;
            
        } else if (key.equals("resultVar") == true) {
            this.resultVar = (String)value;
            
        } else if (key.equals("params") == true) {
            this.params = (List<Object>)value;
        }
    }
    
    @Override
    public void getData(Map<String, Object> data){
        super.getData(data);
        data.put("object", objectKey);
        data.put("method", methodName);
        data.put("resultVar", resultVar);
        data.put("params", params);
    }
    
    @Override
    public LogicItem execute(Map<String, Object> params) {
        Object obj = params.get(objectKey);
        if (obj == null){
            params.put("lastError", id + ": No object named " + objectKey + 
                            ", can't continue");
        }
        Method m = findMethodToExecute(obj);
        if (m == null){
            params.put("lastError", id + ": No method named " + methodName + 
                            ", can't continue");
        }
        Object p[] = prepareParams(m);
        Object result;
        try {
            result = m.invoke(obj, p);
        } catch (Exception e) {
            params.put("lastError","" + e);
            return null;
        }
        if (resultVar != null){
            params.put(resultVar, result);
        }
        
        return onNextItem;
    }

    private Object convert(String type, int index){
        Object result = null; 
        switch(type){
            case "int":
                result = new Integer(AutoConvertor.asInt(params.get(index)));
                break;
            case "boolean":
                result = new Boolean(AutoConvertor.asBoolean(params.get(index)));
                break;
            case "float":
                result = new Float(AutoConvertor.asDouble(params.get(index)));
                break;
            case "double":
                result = new Double(AutoConvertor.asDouble(params.get(index)));
                break;
            case "java.lang.String":
                result = "" + params.get(index); 
                break;
        }
        return result;
    }
    
    private Object[] prepareParams(Method m) {
        Class<?>[] s = m.getParameterTypes();
        if (s.length == 0){
            return null;
        }
        Object o[] = new Object[s.length];
        for(int t = 0; t < s.length; t++){
            o[t] = convert(s[t].getName(), t);
        }
        return o;
    }

    /**
     * @param obj
     * @return
     */
    private Method findMethodToExecute(Object obj) {
        Method[] mList = obj.getClass().getMethods();
        for(Method meth : mList){
            if (meth.getName().equals(methodName) == false){
                continue;
            }
            int pCount = params != null ? params.size() : 0;
            if (meth.getGenericParameterTypes().length != pCount){
                continue;
            }
            return meth;
        }
        return null;
    }

    public boolean validate(Set<LogicItem> traversed, List<String> messages, 
                    boolean lastState){
        //prevent loop
        if (traversed.contains(this) == true){
            return lastState;
        }
        traversed.add(this);
        if (AutoConvertor.isEmpty(objectKey) == true){
            lastState = false;
            messages.add(id + " Error: There is no object to execute selected");
        }
        if (AutoConvertor.isEmpty(methodName) == true){
            lastState = false;
            messages.add(id + " Error: There is no method to execute selected");
        }
        if (AutoConvertor.isEmpty(resultVar) == true){
            messages.add(id + " Warning: There is no resultVar, this may be an error");
        }
        if (AutoConvertor.isEmpty(params) == true){
            messages.add(id + " Warning: There is no params to method, this may be an error");
        }
        
        if (onNextItem != null){
            return lastState & onNextItem.validate(traversed, messages, lastState);
        } else {
            return lastState;
        }
    }
}