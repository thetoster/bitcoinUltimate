package pl.thetosters.cloudysky.bitcoinultimate.logic.items;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class LogicItem {

	//identyfikator tego logicItemu
	protected String id;
	
	private String onNextId;
	protected LogicItem onNextItem;
		
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void buildItemsChain(LogicItemsProvider prv){
		onNextItem = prv.getItemById(onNextId);
	}
	
	public void setData(String key, Object value){
		if (key.equals("id") == true){
			id = (String)value;
			
		} else if (key.equals("nextId") == true){
		    onNextId = (String)value;
			
		}
	}
	
	public void getData(Map<String, Object> data){ 
	    data.put("nextId", onNextId);
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
		LogicItem other = (LogicItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
    protected void addLog(String s, Map<String, Object> params){
	    List<String> log = (List<String>)params.get("log");
	    if (log != null){
	        log.add(id + ": " + s);
	    }
	}
    /**
     * @param params
     */
    abstract public LogicItem execute(Map<String, Object> params); 
    
    abstract public boolean validate(Set<LogicItem> traversed, 
                    List<String> messages, boolean lastState);
}
