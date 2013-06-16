/*
 * LogicItemPredict.java, version: 1.0.0
 * Date: 02-06-2013 16:33:48
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

import pl.thetosters.cloudysky.bitcoinultimate.logic.stats.MarketPredictor;
import pl.thetosters.cloudysky.bitcoinultimate.logic.stats.MarketPredictor.*;
import pl.thetosters.cloudysky.server.misc.AutoConvertor;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemPredict extends LogicItem{

    private PredictType type;
    //topLimiter - nazwa zmiennej okreslajacej maksymalna wartosc od gory
    //jesli wartosc jest przekroczona, zostaje przycieta do niej
    //bottomLimiter - analogicznie
    //depth - ile wartosci z przeszlosci jest branych do predykcji
    private String topLimiter, bottomLimiter, depth;
    private String resultVar;   //tutaj zostanie zapamietany wynik
    
    @Override
    public void setData(String key, Object value){
        super.setData(key, value);
        
        if (key.equals("topLimiter") == true){
            topLimiter = (String)value;

        } else if (key.equals("bottomLimiter") == true){
            bottomLimiter = (String)value;
            
        } else if (key.equals("depth") == true){
            depth = (String)value;
            
        } else if (key.equals("resultVar") == true){
            resultVar = (String)value;
            
        } else if (key.equals("type") == true){
            type = PredictType.valueOf((String)value);
            
        }
    }
    
    @Override
    public void getData(Map<String, Object> data){
        super.getData(data);
        
        data.put("topLimiter", topLimiter);
        data.put("bottomLimiter", bottomLimiter);
        data.put("depth", depth);
        data.put("resultVar", resultVar);
        data.put("type", type.toString());
    }
    
    @Override
    public LogicItem execute(Map<String, Object> params) {
        double min = AutoConvertor.asDouble(params.get(bottomLimiter));
        double max = AutoConvertor.asDouble(params.get(topLimiter));
        int predDepth = AutoConvertor.asInt(params.get(depth));
        predDepth = predDepth < 2 ? 2 : predDepth;
        if (max == 0) {
            max = 10000;
        }
        MarketPredictor mp = (MarketPredictor)params.get("predictor");
        double[] predResults = mp.predictNextPrice(type, predDepth);
        double result = predResults[0];
        for(int t = 0; t < predDepth; t++){
            result = predResults[t];
            if ( (result < min) || (result > max) ){
                continue;
            }
            //ok this prediction meets conditions
            params.put(resultVar, result);
            break;
        }
        //nothing suitable found, just clamp last value
        result = result < min ? min : result;
        result = result > max ? max : result;
        params.put(resultVar, result);
        addLog("[PRED] type=" + type +" depth=" + predDepth + " -> " + 
                        resultVar + "=" + result, params);
        return onNextItem;
    }
    
    public boolean validate(Set<LogicItem> traversed, List<String> messages, 
                    boolean lastState){
        //prevent loop
        if (traversed.contains(this) == true){
            return lastState;
        }
        traversed.add(this);

        if (type == PredictType.UNKNOWN){
            lastState = false;
            messages.add(id + " Error: No type of prediction is selected"); 
        }
        if (AutoConvertor.isEmpty(resultVar) == true){
            messages.add(id + " Error: Depth of prediction is not set, value 2 will be used"); 
        }
        if (AutoConvertor.isEmpty(resultVar) == true){
            lastState = false;
            messages.add(id + " Error: No variable for prediction result is set"); 
        }
        
        if (AutoConvertor.isEmpty(topLimiter) == true){
            messages.add(id + " Warning: Top limiter is not set"); 
        }
        
        if (AutoConvertor.isEmpty(bottomLimiter) == true){
            messages.add(id + " Warning: Bottom limiter is not set"); 
        }
        
        if (onNextItem != null){
            return lastState & onNextItem.validate(traversed, messages, lastState);
        } else {
            return lastState;
        }
    }
}