/*
 * MongoSubFactoryDinnerDayEntity.java, version: 1.0.0
 * Date: 28-04-2013 18:03:42
 * Author: Bartłomiej Żarnowski [Toster]
 *
 * This source was created by member of The Tosters group. 
 * All rights reserved.
 * Visit us at: http://thetosters.pl
 */
package pl.thetosters.cloudysky.bitcoinultimate.storage.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.thetosters.cloudysky.bitcoinultimate.entities.MarketStateEntity;
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
public class MongoSubFactoryMarketStateEntity extends AbstractMongoSubFactory
                implements MongoObjectSerializer {

    private final static String COLL_NAME = "btc_marketState_entity";

    private MongoStorage repository;

    /**
     * @see pl.thetosters.cloudysky.server.storage.SubFactory#requestEntities(java.lang.String,
     *      java.lang.Object)
     */
    @Override
    public List<LogicEntity> requestEntities(String key, Object value) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);

        BasicDBObject query = new BasicDBObject();
        query.put(key, value);
        DBCursor cur = coll.find(query);
        List<LogicEntity> result = new ArrayList<LogicEntity>();
        while (cur.hasNext() == true) {
            DBObject rs = cur.next();

            MarketStateEntity record = new MarketStateEntity();
            record.setAvrPrice((Double)rs.get("avr"));
            record.setMarket((String)rs.get("market"));
            record.setMaxPrice((Double)rs.get("max"));
            record.setMinPrice((Double)rs.get("min"));
            record.setTime(new Date((Long)rs.get("date")));
            record.setVolume((Double)rs.get("vol"));
            record.setWeightedAvrPrice((Double)rs.get("avr_weight"));
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

        MarketStateEntity record = null;
        if (rs != null) {
            record = new MarketStateEntity();
            record.setAvrPrice((Double)rs.get("avr"));
            record.setMarket((String)rs.get("market"));
            record.setMaxPrice((Double)rs.get("max"));
            record.setMinPrice((Double)rs.get("min"));
            record.setTime(new Date((Long)rs.get("date")));
            record.setVolume((Double)rs.get("vol"));
            record.setWeightedAvrPrice((Double)rs.get("avr_weight"));
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
        this.repository.setSerializerFor(MarketStateEntity.class, this);
    }

    /**
     * @see pl.thetosters.cloudysky.server.storage.mongodb.MongoObjectSerializer#remove(java.lang.Object)
     */
    @Override
    public void remove(Object o) {
        DBCollection coll = repository.getDB().getCollection(COLL_NAME);

        BasicDBObject query = new BasicDBObject();
        query.put("date", ((MarketStateEntity) o).getTime().getTime());
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
        MarketStateEntity ent = (MarketStateEntity) o;
        doc.put("avr", ent.getAvrPrice());
        doc.put("market", ent.getMarket());
        doc.put("max", ent.getMaxPrice());
        doc.put("min", ent.getMinPrice());
        doc.put("date", ent.getTime().getTime());
        doc.put("vol", ent.getVolume());
        doc.put("avr_weight", ent.getWeightedAvrPrice());
        
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

            MarketStateEntity record = new MarketStateEntity();
            record.setAvrPrice((Double)rs.get("avr"));
            record.setMarket((String)rs.get("market"));
            record.setMaxPrice((Double)rs.get("max"));
            record.setMinPrice((Double)rs.get("min"));
            record.setTime(new Date((Long)rs.get("date")));
            record.setVolume((Double)rs.get("vol"));
            record.setWeightedAvrPrice((Double)rs.get("avr_weight"));
            result.add(record);
        }
        cur.close();
        return result;
    }
}
