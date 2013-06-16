/*
 * MtGoxApi.java, version: 1.0.0
 * Date: 26-05-2013 17:41:20
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
import javax.net.ssl.HttpsURLConnection;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import pl.thetosters.cloudysky.bitcoinultimate.entities.AccountStateEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketOrderEntity;
import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * TODO: Opis
 * @author Toster
 * @version 1.0.0
 * 
 */
public class MtGoxApi implements MarketApi {
    private String apiKey, secretKey;
    private static final JSONParser parser = new JSONParser();
    
    private static final String clientIdent = "Mozilla/4.0 (compatible; MtGox Ultimate Bot)";

    private final static String baseUrl = "https://data.mtgox.com/api/2/";
    private static final String tickerUrl = "https://data.mtgox.com/api/2/BTCPLN/money/ticker";

    private static final String orderBookUrl = "https://data.mtgox.com/api/2/BTCPLN/money/depth/fetch";
    private static final String marketApi = "mtGox";
    
    public MtGoxApi(String apiKey, String secret){
        this.apiKey = apiKey;
        this.secretKey = secret;
    }

    private String query(String path, HashMap<String, String> args) 
                    throws Exception{
        String result = null;

        // add nonce and build arg list
        args.put("nonce", String.valueOf(System.currentTimeMillis()) + "000");
        String post_data = this.buildQueryString(args);

        // args signature
        String hash_data = path + "\0" + post_data;
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secret_spec = new SecretKeySpec(
                        (new BASE64Decoder()).decodeBuffer(this.secretKey),
                        "HmacSHA512");
        mac.init(secret_spec);
        String signature = (new BASE64Encoder()).encode(mac.doFinal(hash_data
                        .getBytes()));

        // build URL
        URL queryUrl = new URL(baseUrl + path);
        System.out.println(queryUrl);

        // create connection
        HttpURLConnection connection = (HttpURLConnection) queryUrl
                        .openConnection();
        connection.setDoOutput(true);

        // set signature
        signature = signature.replaceAll("\r\n", "");
        signature = signature.replaceAll("\n", "");
        connection.setRequestProperty("User-Agent", clientIdent);
        connection.setRequestProperty("Rest-Key", this.apiKey);
        connection.setRequestProperty("Rest-Sign", signature);
        connection.setRequestProperty("Content-type",
                        "application/x-www-form-urlencoded");

        // write post
        connection.getOutputStream().write(post_data.getBytes());

        // read info
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[500];
        InputStream is = connection.getInputStream();
        while (true) {
            int len = is.read(buffer, 0, 500);
            if (len > 0) {
                baos.write(buffer, 0, len);
            } else {
                break;
            }
        }
        result = new String(baos.toByteArray(), 0, baos.size(), "UTF-8");
        
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
    
    @Override
    public AccountStateEntity getFounds() throws Exception{
        HashMap<String, String> query_args = new HashMap<>();        
        String s = query("MONEY/INFO", query_args);
        if (s == null){
            return null;
        }
        AccountStateEntity as = null;
        try {
            @SuppressWarnings("unchecked")
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            as = new AccountStateEntity();
            as.setBtc(getDouble(r, "data/Wallets/BTC/Balance/value"));
            as.setPln(getDouble(r, "data/Wallets/PLN/Balance/value"));
            as.setTime( new Date() );
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return as;
    }
    
    private String publicQuery(String url){
        String result = null;
        try {
            // build URL
            URL queryUrl = new URL(url);
            System.out.println(queryUrl);
            // create connection
//            SSLSocketFactory sslFactory = null;
//            try {
//                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());  
//                KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
//                FileInputStream keystoreStream = new FileInputStream("keystore.jks");
//                keystore.load(keystoreStream, "h4rdc0r_".toCharArray());  
//                trustManagerFactory.init(keystore);  
//                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();  
//                SSLContext ctx = SSLContext.getInstance("TLS");  
//                ctx.init(null, trustManagers, null); 
//                sslFactory = ctx.getSocketFactory();
//            } catch (Exception e) {
//                System.out.println(e);
//            }
            //HttpURLConnection connection = (HttpURLConnection)queryUrl.openConnection();
            HttpsURLConnection connection = (HttpsURLConnection)queryUrl.openConnection();
            //connection.setSSLSocketFactory(sslFactory);
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
        return result;
    }
    
    @Override
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
            ms.setAvrPrice(getDouble(r, "data/avg/value"));
            ms.setMaxPrice(getDouble(r, "data/high/value"));
            ms.setMinPrice(getDouble(r, "data/low/value"));
            ms.setTime( new Date( (int)getDouble(r, "data/now") ) );
            ms.setVolume(getDouble(r, "data/vol/value"));
            ms.setWeightedAvrPrice(getDouble(r, "data/vwap/value"));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return ms;
    }

    /**
     * @param r
     * @param string
     * @return
     */
    @SuppressWarnings("unchecked")
    private double getDouble(Map<String, ?> r, String path) {
        String[] sub = path.split("/");
        Object o = null;
        for(String ss : sub){
            o = r.get(ss);
            if (o instanceof Map){
                r = (Map<String, ?>)o;
            }
        }
        if (o instanceof Number){
            return ((Number)o).doubleValue();
        }
        return Double.parseDouble((String)o);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<MarketOrderEntity> getOrderBook(){
        String s = publicQuery(orderBookUrl);
        if (s == null){
            return new ArrayList<>();
        }
        ArrayList<MarketOrderEntity> list = new ArrayList<>();
        try {
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            r = (Map<String,?>)r.get("data"); 
            List<Map<?,?>> l = (List<Map<?,?>>)r.get("bids");
            list.addAll( unparseOrders(l, false) );
            l = (List<Map<?,?>>)r.get("asks");
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
    private List<MarketOrderEntity> unparseOrders(List<Map<?,?>> l, boolean sellBtc) {
        List<MarketOrderEntity> list = new ArrayList<>();
        Date d = null;
        try{
            for (Map<?,?> m : l) {
                System.out.println(m);
                MarketOrderEntity mo = new MarketOrderEntity();
                mo.setMarket(marketApi);
                mo.setPrice( ((Number)m.get("price")).doubleValue() );
                mo.setAmount( ((Number)m.get("amount")).doubleValue());
                d = new Date(Long.parseLong((String) m.get("stamp")));
                mo.setTime(d);
                mo.setSellBTC(sellBtc);
                list.add(mo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MarketOrderEntity> getOrders() throws Exception{
        HashMap<String, String> query_args = new HashMap<>();        
        String s = query("BTCPLN/money/orders", query_args);
        if (s == null){
            return new ArrayList<>();
        }
        ArrayList<MarketOrderEntity> list = new ArrayList<>();
        try {
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            List<Map<String, ?>> tmp = (List<Map<String, ?>>)r.get("data");
            for(Map<String, ?> m : tmp){
                MarketOrderEntity mo = new MarketOrderEntity();
                mo.setMarket(marketApi);
                mo.setOid((String) m.get("oid"));
                mo.setAmount(getDouble(m, "amount/value"));
                mo.setPrice(getDouble(m, "price/value"));
                boolean ask = "ask".equals( m.get("type") );
                mo.setSellBTC(ask);
                Date d = new Date(Long.parseLong((String) m.get("priority")));
                mo.setTime(d);
                mo.setState((String) m.get("status"));
                list.add(mo);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return list;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public String buyBTC(double amount, double price) throws Exception{
        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("amount_int", "" + (long)(amount * 100000));
        query_args.put("price_int", "" + (long)(price * 100000));
        query_args.put("type", "bid");
        String s = query("BTCPLN/money/order/add", query_args);
        if (s == null){
            return null;
        }
        try {
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            s = (String) r.get("data");
        } catch( Exception e){
            e.printStackTrace();
        }
        return s;
    }
    
    @Override
    public String sellBTC(double amount, double price) throws Exception{
        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("amount_int", "" + (long)(amount * 100000));
        query_args.put("price_int", "" + (long)(price * 100000));
        query_args.put("type", "ask");
        String s = query("BTCPLN/money/order/add", query_args);
        if (s == null){
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            s = (String) r.get("data");
        } catch( Exception e){
            e.printStackTrace();
        }
        return s;    
    }
    
    @Override
    public boolean cancelOrder(String oid, String type) throws Exception{
        HashMap<String, String> query_args = new HashMap<>();
        query_args.put("oid", oid);
        String s = query("BTCPLN/money/order/cancel", query_args);        
        if (s == null){
            return false;
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String,?> r = (Map<String,?>)parser.parse(s);
            s = (String) r.get("result");
        } catch( Exception e){
            e.printStackTrace();
        }
        return "success".equals(s);
    }    
}
