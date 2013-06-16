/*
 * LogicItemCalc.java, version: 1.0.0
 * Date: 02-06-2013 12:51:43
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.items;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.softwaremonkey.MathEval;

import pl.thetosters.cloudysky.server.misc.AutoConvertor;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemCalc extends LogicItem{

    private String equation, resultVar;
    
    @Override
    public void setData(String key, Object aValue){
        super.setData(key, aValue);
        if (key.equals("equation") == true){
            equation = (String)aValue;
        }
        if (key.equals("resultVar") == true){
            resultVar = (String)aValue;
        }
    }
    
    @Override
    public LogicItem execute(Map<String, Object> params) {
        if (resultVar == null){
            return null;
        }
        MathEval eval = new MathEval();
        
        for(Entry<String, Object> ent: params.entrySet()){
            if (ent.getValue() instanceof Number){
                eval.setConstant(ent.getKey(), 
                                AutoConvertor.asDouble(ent.getValue()));
            }
        }
        double result = eval.evaluate(equation);
        params.put(resultVar, result);
        addLog("[CALC] "+resultVar+" = " + result, params);
        return onNextItem;
    }
    
    @Override
    public void getData(Map<String, Object> data){
        super.getData(data);
        data.put("resultVar", resultVar);
        data.put("equation", equation);
    }
    
    public boolean validate(Set<LogicItem> traversed, List<String> messages, 
                    boolean lastState){
        //prevent loop
        if (traversed.contains(this) == true){
            return lastState;
        }
        traversed.add(this);
        if (AutoConvertor.isEmpty(equation) == true){
            lastState = false;
            messages.add(id + " Error: No equation is set.");
        }
        if (AutoConvertor.isEmpty(resultVar) == true){
            lastState = false;
            messages.add(id + " Error: No varible to store result is set");            
        }
        if (onNextItem != null){
            return lastState & onNextItem.validate(traversed, messages, lastState);
        } else {
            return lastState;
        }
    }
}
