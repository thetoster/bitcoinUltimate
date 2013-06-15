/*
 * BitCurexApi.java, version: 1.0.0
 * Date: 23-05-2013 19:57:08
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */

package pl.thetosters.cloudysky.bitcoinultimate.markets;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import pl.thetosters.cloudysky.bitcoinultimate.entities.AccountStateEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketOrderEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author Toster
 * @version 1.0.0
 * 
 */
public class BitCurexApi implements MarketApi{

    private String apiKey, secretKey;
    private static final JSONParser parser = new JSONParser();
    
    private static final String clientIdent = "Mozilla/4.0 (compatible; BitCurex Ultimate Bot)";

    private final static String baseUrl = "https://data.mtgox.com/api/2/";
    private static final String tickerUrl = "https://pln.bitcurex.com/data/ticker.json";
    private static final String tradesUrl = "https://pln.bitcurex.com/data/trades.json";
    private static final String orderBookUrl = "https://pln.bitcurex.com/data/orderbook.json";
    private static final String marketApi = "BitCurex";
    
    public BitCurexApi(String apiKey, String secret){
        this.apiKey = apiKey;
        this.secretKey = secret;
    }
    
    private String query(String path, HashMap<String, String> args) {
        String result = null;
        try {
            // add nonce and build arg list
            args.put("nonce", String.valueOf(System.currentTimeMillis()) + "000");
            String post_data = this.buildQueryString(args);
 
            // args signature
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec((new BASE64Decoder()).decodeBuffer(this.secretKey), "HmacSHA512");
            mac.init(secret_spec);
            String signature = (new BASE64Encoder()).encode(mac.doFinal(post_data.getBytes()));

            // build URL
            URL queryUrl = new URL(baseUrl + path);
            System.out.println(queryUrl);
            
            // create connection
            HttpURLConnection connection = (HttpURLConnection)queryUrl.openConnection();
            connection.setDoOutput(true);
            
            // set signature
            signature = signature.replaceAll("\r\n", "");
            signature = signature.replaceAll("\n", "");
            connection.setRequestProperty("User-Agent", clientIdent);
            connection.setRequestProperty("Rest-Key", this.apiKey);
            connection.setRequestProperty("Rest-Sign", signature);
            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");
            
            // write post
            connection.getOutputStream().write(post_data.getBytes());
 
            // read info
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[500];
            InputStream is = connection.getInputStream();
            while( true ){
                int len = is.read(buffer, 0, 500);
                if (len > 0){
                    baos.write(buffer, 0, len);
                } else {
                    break;
                }
            }
            result = new String(baos.toByteArray(), 0, baos.size(), "UTF-8");
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }
 
    protected String buildQueryString(HashMap<String, String> args) {
        String result = new String();
        for (String hashkey : args.keySet()) {
            if (result.length() > 0) result += '&';
            try {
                result += URLEncoder.encode(hashkey, "UTF-8") + "="
                        + URLEncoder.encode(args.get(hashkey), "UTF-8");
            } catch (Exception ex) {
               System.out.println(ex);
            }
        }
        return result;
    }
    
    public AccountStateEntity getFounds(){
        HashMap<String, String> query_args = new HashMap<>();        
        String s = query("getFunds", query_args);
        if (s == null){
            return null;
        }
        AccountStateEntity as = null;
        try {
            @SuppressWarnings("unchecked")
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            as = new AccountStateEntity();
            as.setBtc(((Number)r.get("btcs")).doubleValue());
            as.setPln(((Number)r.get("plns")).doubleValue());
            as.setTime( new Date() );
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return as;
    }
    
    public List<MarketOrderEntity> getOrders(){
        HashMap<String, String> query_args = new HashMap<>();        
        String s = query("getOrders", query_args);
        System.out.println(s);
        return null;
    }
    
    public String buyBTC(double amount, double price){
        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("amount", "" + amount);
        query_args.put("price", "" + price);
        String s = query("buyBTC", query_args);
        System.out.println(s);
        return null;
    }
    
    public String sellBTC(double amount, double price){
        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("amount", "" + amount);
        query_args.put("price", "" + price);
        String s = query("sellBTC", query_args);
        System.out.println(s);
        return null;
    }
    
    public boolean cancelOrder(String oid, String type){
        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("oid", oid);
        query_args.put("type", type);
        String s = query("cancelOrder", query_args);
        System.out.println(s);
        System.out.println("Sprawdzic wynik i dokonczyc implementacje!");
        return true;
    }
    
    public void getTransactions(String oid, String type){
        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("oid", oid);
        query_args.put("type", type);
        query("getTransactions", query_args);        
    }
 
    private String publicQuery(String url){
        String result = null;
        try {
            // build URL
            URL queryUrl = new URL(url);
            System.out.println(queryUrl);
            // create connection
            HttpURLConnection connection = (HttpURLConnection)queryUrl.openConnection();
            connection.setRequestProperty("User-Agent", clientIdent);
            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");
            
            // read info
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte buffer[] = new byte[500];
            InputStream is = connection.getInputStream();
            while( true ){
                int len = is.read(buffer, 0, 500);
                if (len > 0){
                    baos.write(buffer, 0, len);
                } else {
                    break;
                }
            }
            result = new String(baos.toByteArray(), 0, baos.size(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }
    
    public MarketStateEntity getTicker(){
        String s = publicQuery(tickerUrl);
        if (s == null){
            return null;
        }
        MarketStateEntity ms = null;
        try {
            @SuppressWarnings("unchecked")
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            ms = new MarketStateEntity();
            ms.setMarket(marketApi);
            ms.setAvrPrice(((Number)r.get("avg")).doubleValue());
            ms.setMaxPrice(((Number)r.get("high")).doubleValue());
            ms.setMinPrice(((Number)r.get("low")).doubleValue());
            ms.setTime( new Date( ((Number)r.get("time")).intValue() ) );
            ms.setVolume(((Number)r.get("vol")).doubleValue());
            ms.setWeightedAvrPrice(((Number)r.get("vwap")).doubleValue());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return ms;
    }
    
    public void getTrades(){
        publicQuery(tradesUrl);
    }
    
    @SuppressWarnings("unchecked")
    public List<MarketOrderEntity> getOrderBook(){
        String s = publicQuery(orderBookUrl);
        if (s == null){
            return new ArrayList<>();
        }
        ArrayList<MarketOrderEntity> list = new ArrayList<>();
        try {
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            List<List<?>> l = (List<List<?>>)r.get("bids");
            list.addAll( unparseOrders(l, false) );
            l = (List<List<?>>)r.get("asks");
            list.addAll( unparseOrders(l, true) );
        } catch (ParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return list;
    }
    
    /**
     * @param l
     * @return
     */
    private List<MarketOrderEntity> unparseOrders(List<List<?>> l, boolean sellBtc) {
        List<MarketOrderEntity> list = new ArrayList<>();
        Date d = new Date();
        try{
            for (List<?> m : l) {
                MarketOrderEntity mo = new MarketOrderEntity();
                mo.setMarket(marketApi);
                mo.setPrice(Double.parseDouble((String) m.get(0)));
                mo.setAmount(Double.parseDouble((String) m.get(1)));
                mo.setTime(d);
                mo.setSellBTC(sellBtc);
                list.add(mo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}