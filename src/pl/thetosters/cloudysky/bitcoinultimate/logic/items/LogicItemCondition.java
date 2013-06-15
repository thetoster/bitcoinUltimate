package pl.thetosters.cloudysky.bitcoinultimate.logic.items;

import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.thetosters.cloudysky.server.misc.AutoConvertor;

public class LogicItemCondition extends LogicItem{
    private enum Compare{
        UNDEFINED("?"),
        EQUAL("=="), NOT_EQUAL("!="), 
        LESS("<"), LESS_EQUAL("<="), 
        GREAT(">"), GREAT_EQUAL(">=");

        private final String name;
                
        Compare(String n){
            name = n;
        }
        
        String getName(){
            return name;
        }
    }
    
    private String var;
    private String var2;
    private Object value;
    private Compare cmp = Compare.UNDEFINED;
    private LogicItem onFailItem;
    private String onFailId;
    
	@Override
	public void setData(String key, Object aValue){
	    super.setData(key, aValue);
	    if (key.equals("var1") == true){
            var = (String)aValue;
            
        } else if (key.equals("var2") == true){
            var2 = (String)aValue;
            
        } else if (key.equals("value") == true){
            value = aValue;
            
        } else if (key.equals("onFailId") == true){
            onFailId = (String)aValue;
            
        } else if (key.equals("type") == true){
            for(Compare c : Compare.values()){
                if (c.getName().equals(aValue)){
                    cmp = c;
                    return;
                }
            }
        }
	}

	public void buildItemsChain(LogicItemsProvider prv){
	    super.buildItemsChain(prv);
	    onFailItem = prv.getItemById(onFailId);
    }
	
    @Override
    public void getData(Map<String, Object> data){
        super.getData(data);
        data.put("var1", var);
        data.put("var2", var2);
        data.put("value", value);
        data.put("onFailId", onFailId);
        data.put("type", cmp.getName());
    }
    
    @Override
    public LogicItem execute(Map<String, Object> params) {
        Object value1 = params.get(var);
        Object value2 = null;
        if (var2 != null){
            value2 = params.get(var2);
        } else {
            value2 = value;
        }

        boolean result = false;
        if ("NULL".equals(value2) == true){
            if (cmp == Compare.EQUAL){
                result = value1 == null;
            } else {
                result = value1 != null;
            }
        } else {
            // simple compare
            if (value1 instanceof Boolean) {
                result = compareAsBoolean(value1, value2);
            } else if (value1 instanceof Integer) {
                result = compareAsInteger(value1, value2);
            } else {
                result = compareAsDouble(value1, value2);
            }
        }
        
        if (result == true){
            return onNextItem;
        } else {
            return onFailItem;
        }
    }

    /**
     * @param value1
     * @param value2
     * @return
     */
    private boolean compareAsDouble(Object value1, Object value2) {
        double b1 = AutoConvertor.asDouble(value1);
        double b2 = AutoConvertor.asDouble(value2);
        switch(cmp){
            case GREAT_EQUAL:
                return b1 >= b2;
                
            case LESS_EQUAL:
                return b1 <= b2;
                
            case EQUAL:
                return b1 == b2;
                
            case NOT_EQUAL:
                return b1 != b2;
                
            case GREAT:
                return b1 > b2;
                
            case LESS:
                return b1 < b2;
                
            default:
                return false;
        }
    }

    /**
     * @param value1
     * @param value2
     * @return
     */
    private boolean compareAsInteger(Object value1, Object value2) {
        int b1 = AutoConvertor.asInt(value1);
        int b2 = AutoConvertor.asInt(value2);
        switch(cmp){
            case GREAT_EQUAL:
                return b1 >= b2;
                
            case LESS_EQUAL:
                return b1 <= b2;
                
            case EQUAL:
                return b1 == b2;
                
            case NOT_EQUAL:
                return b1 != b2;
                
            case GREAT:
                return b1 > b2;
                
            case LESS:
                return b1 < b2;
                
            default:
                return false;
        }
    }

    /**
     * @param value1
     * @param value2
     * @return
     */
    private boolean compareAsBoolean(Object value1, Object value2) {
        boolean b1 = AutoConvertor.asBoolean(value1);
        boolean b2 = AutoConvertor.asBoolean(value2);
        switch(cmp){
            case GREAT_EQUAL:
            case LESS_EQUAL:
            case EQUAL:
                return b1 == b2;
                
            case NOT_EQUAL:
                return b1 != b2;
                
            default:
                return false;
        }
    }
    
    public boolean validate(Set<LogicItem> traversed, List<String> messages, 
                    boolean lastState){
        //prevent loop
        if (traversed.contains(this) == true){
            return lastState;
        }
        
        traversed.add(this);
        if (AutoConvertor.isEmpty(var) == true){
            lastState = false;
            messages.add(id + " Error: There is no primary varible for condition set");
        }
        if ((AutoConvertor.isEmpty(var2) == true) && (value == null)){
            lastState = false;
            messages.add(id + " Error: Neither second varible or constat value is set");            
        }
        if (cmp == Compare.UNDEFINED){
            lastState = false;
            messages.add(id + " Error: There is comparison type set");
        }

        if (AutoConvertor.isEmpty(onFailId) == true){
            //this may be correct, if we want to stop processing in case of failure
            messages.add(id + " Warning: There is item which should be executed in case of condition failure");
        }
        
        if (onNextItem == null){
            //this may be correct, if we want to stop processing in case of success
            messages.add(id + " Warning: There is item which should be executed in case of condition success");
        }
        
        if ((onNextItem == null) && (AutoConvertor.isEmpty(onFailId) == true)){
            lastState = false;
            messages.add(id + " Error: There is no logic item to proceed neither in case of success or failure");            
        }
        
        if (onFailItem != null){
            lastState &= onFailItem.validate(traversed, messages, lastState);
        }
        if (onNextItem != null){
            lastState &= onNextItem.validate(traversed, messages, lastState);
        }
        return lastState;
    }

}
