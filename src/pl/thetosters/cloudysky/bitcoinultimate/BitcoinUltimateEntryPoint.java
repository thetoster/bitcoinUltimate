/*
 * BitcoinUltimateEntryPoint.java
 * Date: 2011-11-08 16:30:30
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate;

import java.util.Map;
import java.util.TimerTask;

import pl.thetosters.cloudysky.bitcoinultimate.logic.PeriodicTimerTask;
import pl.thetosters.cloudysky.bitcoinultimate.logic.branches.MainBranch;
import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItemFactory;
import pl.thetosters.cloudysky.bitcoinultimate.markets.MarketEngine;
import pl.thetosters.cloudysky.server.MasterHub;
import pl.thetosters.cloudysky.server.entities.LogicEntity;
import pl.thetosters.cloudysky.server.logic.ClientSession;
import pl.thetosters.cloudysky.server.logic.LogicBranch;
import pl.thetosters.cloudysky.server.realms.RealmPlugin;
import pl.thetosters.cloudysky.server.storage.StorageBinder;
import pl.thetosters.cloudysky.server.storage.SubFactory;
import pl.thetosters.cloudysky.server.storage.mongodb.MongoStorage;


/**
 * Entry point for this plugin. It creates binding to storage and logic
 *  structures.
 * @author Bartłomiej Żarnowski
 * @version 1.0.0
 */
public class BitcoinUltimateEntryPoint implements RealmPlugin {

	private MasterHub masterHub;
	private Map<Class<LogicEntity>, SubFactory> regStorageSubFact;
	private TimerTask periodicTask;
	private MarketEngine engine;
	private final static String pluginName = "Bitcoin Ultimate";
	
	@Override
	public void doStorageRegister() throws Exception{
		masterHub.getMainLogger().info("Plugin " + pluginName + " storage registration");
		StorageBinder sb = masterHub.getEntityFactory().getStorageBinder(); 
		
		if (sb.getStorageLink() instanceof MongoStorage){
			regStorageSubFact = sb.buildFactoriesMap(this,
							"pl.thetosters.cloudysky.bitcoinultimate.entities",
							"pl.thetosters.cloudysky.bitcoinultimate.storage.mongodb");
		} else if (sb.getStorageLink() instanceof MongoStorage){
			regStorageSubFact = sb.buildFactoriesMap(this,
							"pl.thetosters.cloudysky.bitcoinultimate.entities",
							"pl.thetosters.cloudysky.bitcoinultimate.storage.simpledb");
		} else {
			throw new IllegalStateException("This plugin doesent support selected storage engine");
		}
		masterHub.getEntityFactory().addSubFactories(regStorageSubFact);
	}

	@Override
	public void doLogicRegister(){
        LogicItemFactory.setLogger(masterHub.getLogicLogger());
	    engine = new MarketEngine(masterHub);
	    periodicTask = new PeriodicTimerTask(masterHub);
	    long rep = 1000 * Integer.getInteger("bitcoinultimate.check.interval", 
	                    600);
	    
	    masterHub.getMaintenanceTimer().schedule(periodicTask, 1000,rep);
	}
	
	@Override
	public void onPluginInit(MasterHub hub){
		masterHub = hub;
		hub.getMainLogger().info("Plugin " + pluginName + " init");
	}

	/** 
     * @see pl.thetosters.cloudysky.server.realms.RealmPlugin#getRealmName()
     */
    @Override
    public String getRealmName() {
	    return PluginConstants.REALM_NAME;
    }

	/**
     * @see pl.thetosters.cloudysky.server.realms.RealmPlugin#unregisterLogic()
     */
    @Override
    public void unregisterLogic() {
        if (periodicTask != null){
            periodicTask.cancel();
        }
        periodicTask = null;
        engine = null;
    }

	/**
     * @see pl.thetosters.cloudysky.server.realms.RealmPlugin#unregisterStorage()
     */
    @Override
    public void unregisterStorage() {
    	masterHub.getMainLogger().info("Plugin " + pluginName + " unregistration");
    	masterHub.getEntityFactory().removeSubFactories(regStorageSubFact);
    }

	/**
     * @see pl.thetosters.cloudysky.server.realms.RealmPlugin#onUserEnter(pl.thetosters.cloudysky.server.logic.ClientSession, java.lang.String)
     */
    @Override
    public boolean onUserEnter(ClientSession cs, String password) {
	    return true;
    }

	/**
     * @see pl.thetosters.cloudysky.server.realms.RealmPlugin#onUserLeave(pl.thetosters.cloudysky.server.logic.ClientSession)
     */
    @Override
    public void onUserLeave(ClientSession cs) {
	    //cleanup our stuff
        cs.getRealmStorage().remove(MainBranch.STORAGE_ID);
    }

	/**
     * @see pl.thetosters.cloudysky.server.realms.RealmPlugin#getCurrentLogicBranch(pl.thetosters.cloudysky.server.logic.ClientSession)
     */
    @Override
    public LogicBranch getCurrentLogicBranch(ClientSession cs) {
        Map<String,Object> storage = cs.getRealmStorage();
        return (LogicBranch)storage.get(MainBranch.STORAGE_ID);
    }

	/**
     * @see pl.thetosters.cloudysky.server.realms.RealmPlugin#getDefaultLogicBranch(pl.thetosters.cloudysky.server.logic.ClientSession)
     */
    @Override
    public LogicBranch getDefaultLogicBranch(ClientSession cs) {
        Map<String,Object> storage = cs.getRealmStorage();
        LogicBranch lb = null;
        lb = (LogicBranch) storage.get(MainBranch.STORAGE_ID);
        if (lb == null) {
            lb = new MainBranch(masterHub, engine);
            lb.setParent(cs);
            storage.put(MainBranch.STORAGE_ID, lb);
        }
	    return lb;
    }

	/**
     * @see pl.thetosters.cloudysky.server.realms.RealmPlugin#getMasterObject(java.lang.String)
     */
    @Override
    public Object getMasterObject(Object key) {
        if (key.equals(MarketEngine.class) == true){
            return engine;
        } else {
            return null;
        }
    }
}
