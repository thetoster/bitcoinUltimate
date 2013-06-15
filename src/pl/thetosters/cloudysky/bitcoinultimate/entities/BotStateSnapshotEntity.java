package pl.thetosters.cloudysky.bitcoinultimate.entities;

import java.io.Serializable;
import java.util.Date;

import pl.thetosters.cloudysky.server.entities.LogicEntity;

public class BotStateSnapshotEntity  implements Serializable, LogicEntity{
    private static final long serialVersionUID = 1890484524955808065L;
    private double currentPLN;  //ile mamy gotówki do dyspozycji
    private double currentBTC;   //ile mamy BC do dyspozycji
    private double basePricePLN;    //bazowa cena dla operacji (ostatnia cena kupna lub sprzedały)
    private Date time;
    private String botId;
    
    /**
     * @return the currentPLN
     */
    public double getCurrentPLN() {
        return currentPLN;
    }
    /**
     * @param currentPLN the currentPLN to set
     */
    public void setCurrentPLN(double currentPLN) {
        this.currentPLN = currentPLN;
    }
    /**
     * @return the currentBTC
     */
    public double getCurrentBTC() {
        return currentBTC;
    }
    /**
     * @param currentBTC the currentBTC to set
     */
    public void setCurrentBTC(double currentBTC) {
        this.currentBTC = currentBTC;
    }
    /**
     * @return the basePricePLN
     */
    public double getBasePricePLN() {
        return basePricePLN;
    }
    /**
     * @param basePricePLN the basePricePLN to set
     */
    public void setBasePricePLN(double basePricePLN) {
        this.basePricePLN = basePricePLN;
    }
    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }
    /**
     * @return the botId
     */
    public String getBotId() {
        return botId;
    }
    /**
     * @param botId the botId to set
     */
    public void setBotId(String botId) {
        this.botId = botId;
    }
	
}
