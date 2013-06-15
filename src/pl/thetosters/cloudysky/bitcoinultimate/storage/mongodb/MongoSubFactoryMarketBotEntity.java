/*
 * MongoSubFactoryMarketBotEntity.java, version: 1.0.0
 * Date: 01-06-2013 12:52:26
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.storage.mongodb;

import java.util.ArrayList;
import java.util.List;

import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketBotEntity;
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
public class MongoSubFactoryMarketBotEntity extends AbstractMongoSubFactory
                implements MongoObjectSerializer {

    private final static String COLL_NAME = "btc_marketBot_entity";

    private MongoStorage repository;
    
    /**
     * @see pl.thetosters.cloudysky.server.storage.SubFactory#requestEntities(java.lang.String,
     *      java.lang.Object)
     */
    private MarketBotEntity unpackFromDB(DBObject rs) {
        MarketBotEntity record = new MarketBotEntity();
        record.setAccountId((String) rs.get("accountId"));
        record.setEnabled((Boolean) rs.get("enabled"));
        record.setId((String) rs.get("id"));
        record.setOpertationsCount((Integer)rs.get("opCnt"));
        record.setStartPLN((Double)rs.get("stPLN"));
        record.setTotalBuyBTC((Double)rs.get("tbBTC"));
        record.setTotalBuyPLN((Double)rs.get("tbPLN"));
        record.setTotalSellBTC((Double)rs.get("tsBTC"));
        record.setTotalSellPLN((Double)rs.get("tsPLN"));
        record.setWorkPlanId((String)rs.get("logicItemId"));
        record.setCurrentBTC((Double)rs.get("curBTC"));
        record.setCurrentPLN((Double)rs.get("curPLN"));
        record.setBasePricePLN((Double)rs.get("basePrice"));
        return record;
    }

    @Override
    public List<LogicEntity> requestEntities(String key, Object value) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);

        BasicDBObject query = new BasicDBObject();
        query.put(key, value);
        DBCursor cur = coll.find(query);
        List<LogicEntity> result = new ArrayList<LogicEntity>();
        while (cur.hasNext() == true) {
            DBObject rs = cur.next();
            MarketBotEntity record = unpackFromDB(rs);
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

        MarketBotEntity record = null;
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
        this.repository = (MongoStorage) repository;
        this.repository.setSerializerFor(MarketBotEntity.class, this);
    }

    /**
     * @see pl.thetosters.cloudysky.server.storage.mongodb.MongoObjectSerializer#remove(java.lang.Object)
     */
    @Override
    public void remove(Object o) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);

        BasicDBObject query = new BasicDBObject();
        query.put("id", ((MarketBotEntity) o).getId());
        query.put("accountId", ((MarketBotEntity) o).getAccountId());
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
        MarketBotEntity ent = (MarketBotEntity) o;
        
        doc.put("accountId", ent.getAccountId());
        doc.put("enabled", ent.isEnabled());
        doc.put("id", ent.getId());
        doc.put("opCnt", ent.getOpertationsCount());
        doc.put("stPLN", ent.getStartPLN());
        doc.put("tbBTC", ent.getTotalBuyBTC());
        doc.put("tbPLN", ent.getTotalBuyPLN());
        doc.put("tsBTC", ent.getTotalSellBTC());
        doc.put("tsPLN", ent.getTotalSellPLN());
        doc.put("logicItemId", ent.getWorkPlanId() == null ? "" : ent.getWorkPlanId() );
        doc.put("curBTC", ent.getCurrentBTC());
        doc.put("curPLN", ent.getCurrentPLN());
        doc.put("basePrice", ent.getBasePricePLN());
                
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
            MarketBotEntity record = unpackFromDB(rs);
            result.add(record);
        }
        cur.close();
        return result;
    }
}
