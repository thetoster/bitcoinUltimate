/*
 * LogicItemsTest.java, version: 1.0.0
 * Date: 03-06-2013 20:46:40
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.items;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account.Type;
import pl.thetosters.cloudysky.bitcoinultimate.logic.stats.MarketPredictor;
import pl.thetosters.cloudysky.bitcoinultimate.logic.MarketBot;


/**
 * @author Toster
 * @version 1.0.0
 * 
 */
public class LogicItemsTest {

    private MarketBot bot;
    private Account acc;
    private MarketPredictor pred;
    int predTime;
    
    public LogicItemsTest(){
        try {
            testLoop();
            testPredict();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    private void testPredict() throws ParseException {
        setup();
        addToPred(10);
        addToPred(20);
        addToPred(30);
        
        LogicItemPredict p = new LogicItemPredict();
        feed("{'id':'1', 'topLimiter':'tl', 'bottomLimiter':'bl','depth':'de','resultVar':'r','type':'LINEAR'}", p);
        acc.getItems().put(p.getId(), p);
        
        LogicItemSetter start = new LogicItemSetter();
        feed("{'id':'start', 'tl':'60', 'bl':'30','de':'3','nextId':'1'}", start);
        acc.getItems().put(start.getId(), start);
        
        bot.setWorkPlan(start);
        doCheck();
    }

    /**
     * @param i
     */
    private void addToPred(int i) {
        predTime++;
        MarketStateEntity mse = new MarketStateEntity();
        mse.setAvrPrice(i);
        long s = Integer.getInteger("bitcoinultimate.predictor.extrapolation.timeStep", 60);
        mse.setTime(new Date(System.currentTimeMillis() + predTime * s * 1000));
        pred.addMarketState(mse);
    }

    @SuppressWarnings({ "unchecked" })
    private void feed(String inData, LogicItem item) throws ParseException{
        inData = inData.replace("'", "\"");
        JSONParser parser = new JSONParser();
        Map<String, Object>data = (Map<String, Object>) parser.parse(inData);
        for(Entry<String, Object> e : data.entrySet()){
            item.setData(e.getKey(), e.getValue());
        }
    }
    private void setup(){
        acc = new Account(Type.MTGOX, "1", "1", "1", "1");
        bot = new MarketBot();
        pred = new MarketPredictor(Type.MTGOX);
        predTime = 0;
    }
    
    private void testLoop() throws ParseException {
        
        setup();
        
        LogicItemCondition cmp = new LogicItemCondition();
        feed("{'id':'1', 'var1':'a', 'value':'10','type':'>','onFailId':'2'}", cmp);
        acc.getItems().put(cmp.getId(), cmp);
        
        LogicItemCalc calc = new LogicItemCalc();
        feed("{'id':'2', 'equation':'a+1', 'resultVar':'a','nextId':'1'}", calc);
        acc.getItems().put(calc.getId(), calc);
        
        LogicItemSetter start = new LogicItemSetter();
        feed("{'id':'start', 'a':'0','nextId':'1'}", start);
        acc.getItems().put(start.getId(), start);
        bot.setWorkPlan(start);
        
        doCheck();
        
    }

    private void doCheck() {
        for(Entry<String, LogicItem> e : acc.getItems().entrySet()){
            e.getValue().buildItemsChain(acc);
        }
        bot.setEnabled(true);
        Map<String, Object> m = new HashMap<>();
        m.put("predictor", pred);
        bot.execute(m);
        
    }
}
