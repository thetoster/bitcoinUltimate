package pl.thetosters.cloudysky.bitcoinultimate;

import pl.thetosters.cloudysky.bitcoinultimate.markets.MtGoxApi;


public class BotTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	    
//      BitCurexApi bca = new BitCurexApi("3546da1ed84ec83ec2e81f25e9719561215af6ccfe6cbcb86fa3d8a7f750a18f",
//      "G8a2HhQN/t5Gz8l7TZEDmNrX6+Loob7bp/RiulQDFHr/LSFio+QlCbRklZLrAwgnIxM56Zveer+/IJm79G1UQQ==");
////bca.getFounds();
//List<?> l = bca.getOrderBook();
//System.out.println(l);
MtGoxApi gs = new MtGoxApi("1dfe9532-3014-4409-b1d6-7e272c1040ce", 
      "GmLjIhn9B1dEklurM+usyZjJKXrN0abK1Z6kl+Vmo4gODw3ymCBDhvMgK39fmSvjQ3sUIofrgWBKdbkNAwXJfA==");
System.out.println(gs.getFounds());
//gs.executeQuery();
	}

}
