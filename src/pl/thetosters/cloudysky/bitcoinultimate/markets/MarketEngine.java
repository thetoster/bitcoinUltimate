/*
 * MarketEngine.java, version: 1.0.0
 * Date: 27-05-2013 21:16:23
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.markets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.thetosters.cloudysky.bitcoinultimate.entities.AccountEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.LogicItemEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketBotEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account.Type;
import pl.thetosters.cloudysky.bitcoinultimate.logic.MarketBot;
import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItem;
import pl.thetosters.cloudysky.bitcoinultimate.logic.items.LogicItemFactory;
import pl.thetosters.cloudysky.bitcoinultimate.logic.stats.MarketPredictor;
import pl.thetosters.cloudysky.server.MasterHub;
import pl.thetosters.cloudysky.server.entities.LogicEntity;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class MarketEngine {
    private Map<String, Account> accounts = new HashMap<String, Account>();
    private MasterHub masterHub;
    Map<Account.Type, MarketPredictor> predictors = new HashMap<>();
    
    public MarketEngine(MasterHub hub){
        masterHub = hub;
        List<LogicEntity> ent = (List<LogicEntity>)masterHub.getEntityFactory().
                        requestEntities(AccountEntity.class, null, null);
        for(LogicEntity le : ent){
            Account acc = new Account( (AccountEntity)le );
            loadAccounContent(acc);
            accounts.put(acc.getId(), acc);
        }
        masterHub.getLogicLogger().info("Restored " + ent.size() + " market accounts");
    }
    
    private void loadAccounContent(Account acc) {
        
        //deserialize logic items
        List<LogicEntity> ent = (List<LogicEntity>)masterHub.getEntityFactory().
                        requestEntities(LogicItemEntity.class, 
                                        LogicItemEntity.ACCOUNT, acc.getId());
        
        Map<String, LogicItem> m = acc.getItems();
        List<LogicItem> itemsList = new ArrayList<>();
        for(LogicEntity le : ent){
            LogicItem li = LogicItemFactory.buildItem((LogicItemEntity)le);
            if (m.get(li.getId()) != null){
                masterHub.getLogicLogger().warn("Overwriting LogicItem with id"
                                + li.getId());
            }
            m.put(li.getId(), li);
            itemsList.add(li);
        }
        //rebuild internal logic items structure
        for(LogicItem li : itemsList){
            li.buildItemsChain(acc);
        }
        
        //deserialize bots
        ent = (List<LogicEntity>)masterHub.getEntityFactory().
                        requestEntities(MarketBotEntity.class, 
                                        MarketBotEntity.ACCOUNT, acc.getId());
        List<MarketBot> mb = acc.getBots();
        for(LogicEntity le : ent){
            MarketBotEntity mbe = (MarketBotEntity)le;
            
            MarketBot bot = new MarketBot();
            bot.setStartPLN(mbe.getStartPLN());
            bot.setId(mbe.getId());
            bot.setTotalBuyPLN(mbe.getTotalBuyPLN());
            bot.setTotalSellPLN(mbe.getTotalSellPLN());
            bot.setTotalBuyBTC(mbe.getTotalBuyBTC());
            bot.setTotalSellBTC(mbe.getTotalSellBTC());
            bot.setOperationsCount(mbe.getOpertationsCount());
            bot.setWorkPlan(m.get(mbe.getWorkPlanId()));
            bot.setCurrentPLN(mbe.getCurrentPLN());
            bot.setCurrentBTC(mbe.getCurrentBTC());
            bot.setBasePricePLN(mbe.getBasePricePLN());
            bot.setEnabled(mbe.isEnabled());
            bot.setReqExecutor(acc);
            mb.add(bot);
        }
        
        switch(acc.getType()){
            case BITCUREX:
                acc.setMarketApi( new BitCurexApi(acc.getApiKey(), 
                                acc.getApiSecret()) );
                break;
                
            case MTGOX:
                acc.setMarketApi( new MtGoxApi(acc.getApiKey(), 
                                acc.getApiSecret()) );
                break;
        }
    }

    public void saveBot(Account acc, MarketBot bot){
        MarketBotEntity ent = new MarketBotEntity();
        
        ent.setAccountId(acc.getId());
        ent.setBasePricePLN(bot.getBasePricePLN());
        ent.setCurrentBTC(bot.getCurrentBTC());
        ent.setCurrentPLN(bot.getCurrentPLN());
        ent.setEnabled(bot.isEnabled());
        ent.setId(bot.getId());
        ent.setOpertationsCount(bot.getOperationsCount());
        ent.setStartPLN(bot.getStartPLN());
        ent.setTotalBuyBTC(bot.getTotalBuyBTC());
        ent.setTotalBuyPLN(bot.getTotalBuyPLN());
        ent.setTotalSellBTC(bot.getTotalSellBTC());
        ent.setTotalSellPLN(bot.getTotalSellPLN());
        if (bot.getWorkPlan() != null){
            ent.setWorkPlanId(bot.getWorkPlan().getId());
        }
        masterHub.getEntityFactory().storeEntity(ent, true);
    }
    
    public String createAccount(Account.Type type, String owner, String apiKey, 
                    String secret){
        
        long time = System.currentTimeMillis() - 1369683324000L;
        Account acc = new Account(type, Long.toString(time, Character.MAX_RADIX),
                        owner, apiKey, secret);

        switch(type){
            case BITCUREX:
                acc.setMarketApi( new BitCurexApi(apiKey, secret) );
                break;
                
            case MTGOX:
                acc.setMarketApi( new MtGoxApi(apiKey, secret) );
                break;
        }
        
        accounts.put(acc.getId(), acc);
        AccountEntity ae = acc.requestStorageEntity();
        masterHub.getEntityFactory().storeEntity(ae, false);
        return acc.getId();
    }
    
    public Account getAccount(String id){
        return accounts.get(id);
    }
    
    public void checkMarkets(){
        Map<Account.Type, MarketStateEntity> m = new HashMap<>();
        for(Account acc : accounts.values()){
            //check if we know market state
            MarketPredictor mpred = predictors.get( acc.getType() );
            if (mpred == null){
                mpred = buildPredictor(acc.getType());
            }

            MarketStateEntity mse = m.get( acc.getType() );
            if (mse == null) {
                //obtain new
                mse = acc.getMarketApi().getTicker();
                if (mse != null){
                    m.put(acc.getType(), mse);
                    masterHub.getEntityFactory().storeEntity(mse, false);
                    mpred.addMarketState(mse);
                } else {
                    //for some reasons state is not avail
                    continue;
                }
            }
            //pass to account for further processing
            Map<String, Object> globals = new HashMap<>();
            globals.put("predictor", mpred);
            globals.put("masterHub", masterHub);
            acc.processState(globals);
        }
    }

    /**
     * @param type
     * @return
     */
    private MarketPredictor buildPredictor(Type type) {
        MarketPredictor pred = new MarketPredictor(type);
        predictors.put(type, pred);
        return pred;
    }

    /**
     * @param acc
     * @param bot
     */
    public void deleteBot(Account acc, MarketBot bot) {
        acc.deleteBot(bot);
        MarketBotEntity ent = new MarketBotEntity();
        ent.setAccountId(acc.getId());
        ent.setId(bot.getId());
        masterHub.getEntityFactory().removeEntity(ent);
    }
}
