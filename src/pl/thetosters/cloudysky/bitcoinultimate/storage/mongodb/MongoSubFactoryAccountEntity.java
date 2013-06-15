/*
 * MongoSubFactoryAccountEntity.java, version: 1.0.0
 * Date: 29-05-2013 22:34:37
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.storage.mongodb;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import pl.thetosters.cloudysky.bitcoinultimate.entities.AccountEntity;
import pl.thetosters.cloudysky.bitcoinultimate.logic.Account.Type;
import pl.thetosters.cloudysky.server.entities.LogicEntity;
import pl.thetosters.cloudysky.server.storage.SpecializedQuery;
import pl.thetosters.cloudysky.server.storage.StorageLink;
import pl.thetosters.cloudysky.server.storage.mongodb.AbstractMongoSubFactory;
import pl.thetosters.cloudysky.server.storage.mongodb.MongoObjectSerializer;
import pl.thetosters.cloudysky.server.storage.mongodb.MongoStorage;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * TODO: Opis
 * 
 * @author Toster
 * @version 1.0.0
 * 
 */
public class MongoSubFactoryAccountEntity extends AbstractMongoSubFactory
                implements MongoObjectSerializer {

    private final static String COLL_NAME = "btc_account_entity";

    private MongoStorage repository;
    private SecretKeySpec secretKeySpec;
    private IvParameterSpec ivParameterSpec;
    
    private void initCrypto(){
        //Secret key
        String s = System.getProperty("bitcoinultimate.db.crypto.secret");
        if (s == null){
            throw new IllegalArgumentException("bitcoinultimate.db.crypto.secret must be set in properties!");
        }
        byte[] aesKey = DatatypeConverter.parseHexBinary(s);
        if (aesKey.length != 32){
            throw new IllegalArgumentException("bitcoinultimate.db.crypto.secret must be a 32 bytes long in hex, now it's " + aesKey.length );
        }
        secretKeySpec = new SecretKeySpec(aesKey, "AES");
        
        //IV param
        s = System.getProperty("bitcoinultimate.db.crypto.ivparam");
        if (s == null){
            throw new IllegalArgumentException("bitcoinultimate.db.crypto.ivparam must be set in properties!");
        }
        byte[] iv = DatatypeConverter.parseHexBinary(s);
        if (iv.length != 16){
            throw new IllegalArgumentException("bitcoinultimate.db.crypto.ivparam must be a 16 bytes long in hex, now it's " + iv.length);
        }
        ivParameterSpec = new IvParameterSpec(iv);
    }
    
    private byte[] encrypt(String inData){
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] ciphertext = cipher.doFinal(inData.getBytes("US-ASCII"));
            result = ciphertext;//new String(ciphertext, "US-ASCII");
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't encode:"+e);
        }
        return result;
    }
    
    private String decrypt(byte[] inData){
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] plaintext = cipher.doFinal(inData);//inData.getBytes("US-ASCII"));
            result = new String(plaintext, "US-ASCII");
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't decode:"+e);
        }
        return result;
    }
    
    /**
     * @see pl.thetosters.cloudysky.server.storage.SubFactory#requestEntities(java.lang.String,
     *      java.lang.Object)
     */
    private AccountEntity unpackFromDB(DBObject rs){
        AccountEntity record = new AccountEntity();
        record.setApiKey( decrypt((byte[]) rs.get("apiKey")) );//decrypt((String) rs.get("apiKey")) );
        record.setApiSecret( decrypt((byte[]) rs.get("apiSecret")) );//decrypt((String) rs.get("apiSecret")) );
        record.setId((String) rs.get("id"));
        record.setType(Type.valueOf((String)rs.get("type")));
        record.setOwnerLogin((String) rs.get("owner"));
        return record;
    }
    
    @Override
    public List<LogicEntity> requestEntities(String key, Object value) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);

        DBCursor cur;
        
        if ((value != null) && (key != null)){
            BasicDBObject query = new BasicDBObject();
            query.put(key, value);
            cur = coll.find(query);
        } else {
            cur = coll.find();
        }
        List<LogicEntity> result = new ArrayList<LogicEntity>();
        while (cur.hasNext() == true) {
            DBObject rs = cur.next();
            AccountEntity record = unpackFromDB(rs);
            result.add(record);
        }
        cur.close();
        return result;
    }

    /**
     * @see pl.thetosters.cloudysky.server.storage.SubFactory#requestEntity(java.lang.String,
     *      java.lang.Object)
     */
    @Override
    public LogicEntity requestEntity(String key, Object value) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);

        BasicDBObject query = new BasicDBObject();
        query.put(key, value);
        DBObject rs = coll.findOne(query);

        AccountEntity record = null;
        if (rs != null) {
            record = unpackFromDB(rs);
        }
        return record;
    }

    /**
     * @see pl.thetosters.cloudysky.server.storage.SubFactory#setRepository(pl.thetosters.cloudysky.server.storage.StorageLink)
     */
    @Override
    public void setRepository(StorageLink repository) {
        if ((repository instanceof MongoStorage) == false) {
            throw new IllegalArgumentException(
                            "Repository must be MongoStorage");
        }
        initCrypto();
        this.repository = (MongoStorage) repository;
        this.repository.setSerializerFor(AccountEntity.class, this);
    }

    /**
     * @see pl.thetosters.cloudysky.server.storage.mongodb.MongoObjectSerializer#remove(java.lang.Object)
     */
    @Override
    public void remove(Object o) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);

        BasicDBObject query = new BasicDBObject();
        query.put("id", ((AccountEntity) o).getId());
        DBCursor cur = coll.find(query);
        while (cur.hasNext() == true) {
            coll.remove(cur.next());
        }
        cur.close();
    }

    /**
     * @see pl.thetosters.cloudysky.server.storage.mongodb.MongoObjectSerializer#serialize(java.lang.Object,
     *      boolean)
     */
    @Override
    public void serialize(Object o, boolean removePrevious) {
        if (removePrevious == false) {
            doStore(o, true);
        } else {
            remove(o);
            doStore(o, false);
        }
    }

    /**
     * @param o
     */
    private void doStore(Object o, boolean overwrite) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);
        BasicDBObject doc = new BasicDBObject();
        AccountEntity ent = (AccountEntity) o;
        doc.put("apiKey", encrypt(ent.getApiKey()) );
        doc.put("apiSecret", encrypt(ent.getApiSecret()) );
        doc.put("id", ent.getId());
        doc.put("type", ent.getType().name());
        doc.put("owner", ent.getOwnerLogin());

        if (overwrite == true) {
            coll.save(doc);
        } else {
            coll.insert(doc);
        }
    }

    /**
     * @see pl.thetosters.cloudysky.server.storage.SubFactory#requestEntity(pl.thetosters.cloudysky.server.storage.SpecializedQuery)
     */
    @Override
    public List<LogicEntity> requestEntities(SpecializedQuery query) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);
        DBCursor cur = coll.find((BasicDBObject) query.getQuery(this, null));
        List<LogicEntity> result = new ArrayList<LogicEntity>();
        while (cur.hasNext() == true) {
            DBObject rs = cur.next();
            AccountEntity record = unpackFromDB(rs);
            result.add(record);
        }
        cur.close();
        return result;
    }

}
