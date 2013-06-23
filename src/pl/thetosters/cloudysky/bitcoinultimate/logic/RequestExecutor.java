package pl.thetosters.cloudysky.bitcoinultimate.logic;

public interface RequestExecutor {
	/**
	 * Wysyłamy do sklepu rządanie kupna BC. 
	 * @param pricePLN po jakiej cenie chcemy kupić BC
	 * @param amountBC ile BC chcemy kupić
	 * @return zwrocone przez server transaction id
	 */
	public String addBuyTransaction(String callerId, double pricePLN, 
	                double amountBC);
	
	/**
	 * Wysyłamy do sklepu rządanie sprzedaży BC.
	 * @param pricePLN po jakiej cenie chcemy sprzedaż
	 * @param amountBC ile BC chcemy sprzedaż
	 * @return zwrocone przez server transaction id
	 */
	public String addSellTransaction(String callerId, double pricePLN, 
	                double amountBC);

    /**
     * Cancel previous order
     * @param order
     */
    public void cancelOrder(String callerId, String order);
}
