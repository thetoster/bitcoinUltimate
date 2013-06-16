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
            addLog("[EXEC] No object named " + objectKey + ", can't continue", 
                            params);
            return null;
        }
        Method m = findMethodToExecute(obj);
        if (m == null){
            addLog("[EXEC] No method named " + methodName + ", can't continue", 
                            params);
            return null;
        }
        Object p[] = prepareParams(m, params);
        Object result;
        try {
            addLog("[EXEC] executing " + objectKey + "." + methodName + 
                            " with params:" + listParams(p), params);
            result = m.invoke(obj, p);
        } catch (Exception e) {
            addLog("[EXEC] exception:" + e, params);
            return null;
        }
        if (resultVar != null){
            params.put(resultVar, result);
            addLog("[EXEC] result " + resultVar + "=" + result, params);
        }
        
        return onNextItem;
    }

    /**
     * @param p
     * @return
     */
    private String listParams(Object[] p) {
        StringBuilder sb = new StringBuilder();
        for(Object o : p){
            sb.append(o);
            sb.append(",");
        }
        return sb.toString();
    }

    private Object convert(String type, int index, Map<String, Object> data){
        Object result = null; 
        Object inObj = data.get(params.get(index));
        switch(type){
            case "int":
                result = new Integer(AutoConvertor.asInt(inObj));
                break;
            case "boolean":
                result = new Boolean(AutoConvertor.asBoolean(inObj));
                break;
            case "float":
                result = new Float(AutoConvertor.asDouble(inObj));
                break;
            case "double":
                result = new Double(AutoConvertor.asDouble(inObj));
                break;
            case "java.lang.String":
                result = "" + inObj; 
                break;
        }
        return result;
    }
    
    private Object[] prepareParams(Method m, Map<String, Object> data) {
        Class<?>[] s = m.getParameterTypes();
        if (s.length == 0){
            return null;
        }
        Object o[] = new Object[s.length];
        for(int t = 0; t < s.length; t++){
            o[t] = convert(s[t].getName(), t, data);
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