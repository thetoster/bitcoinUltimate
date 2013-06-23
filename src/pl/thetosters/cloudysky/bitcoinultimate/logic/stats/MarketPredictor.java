/*
 * MarketPredictor.java, version: 1.0.0
 * Date: 01-06-2013 17:49:12
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.logic.stats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account.Type;


/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class MarketPredictor {
    private int historyDepth;
    private int extrapolationTimeStep;
    
    public enum PredictType{
        UNKNOWN,
        LINEAR
    };
    
    private List<MarketStateEntity> history = new ArrayList<>();
    
    private Type type;
    
    /**
     * @param type
     */
    public MarketPredictor(Type aType) {
        type = aType;
        historyDepth = Integer.getInteger("bitcoinultimate.predictor.history.depth", 6);
        extrapolationTimeStep = Integer.getInteger("bitcoinultimate.predictor.extrapolation.timeStep", 60);
    }
    
    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    public MarketStateEntity getLastMarketState(){
        int size = history.size();
        return size > 0 ? history.get(size-1) : null;
    }
    
    /**
     * @param mse
     */
    public void addMarketState(MarketStateEntity mse) {
        history.add(mse);
        while(history.size() > historyDepth){
            history.remove(0);
        }
    }

    public double[] predictNextPrice(PredictType type, int steps){        
        switch(type){
            case LINEAR:
                return predictLinear(steps);
            default:
                break;
        }
        return new double[steps];
    }

    /**
     * @param steps 
     * @return
     */
    private double[] predictLinear(int steps) {
        if (history.size() < 2){
            double[] result = new double[steps];
            MarketStateEntity mse = history.get(0);
            for(int t = 0; t < steps; t++){
                result[t] = mse.getAvrPrice();
            }
            return result;
        }

        SimpleRegression sr = new SimpleRegression();
        for(MarketStateEntity mse : history){
            sr.addData( convertTime( mse.getTime() ), mse.getAvrPrice());
        }
        double[] result = new double[steps];
        MarketStateEntity mse = history.get(history.size()-1);
        double time = convertTime( mse.getTime() );
        for(int t = 0; t < steps; t++){
            time += extrapolationTimeStep;  //ma byc najpierw
            result[t] = sr.predict(time);
        }
        return result;
    }

    /**
     * @param time
     * @return
     */
    private double convertTime(Date time) {
        //nie chcemy takich wielkich wartosci, przeliczamy na cos mniejszego
        //i w minutach
        long l = time.getTime();
        l -= 1369683324000L;   //odrzucamy spory kawalek przeszlosci
        l /= 1000; //przeliczamy na sekundy
        return l;
    }    
}
