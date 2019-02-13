package zg.ormer.driver;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import zg.ormer.core.XBean;
import zg.ormer.pool.IPool;
import zg.ormer.pool.SimplePool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;


public class MongoDriver extends Driver {
    public static final Logger logger = LogManager.getLogger(MongoDriver.class);

    private MongoClient client;
    private MongoDatabase database;
    private IPool<XBean> pool;

    public MongoDriver(String ip, Integer port, String database) {
        this(ip, port, database, 1);
    }

    public MongoDriver(String ip, Integer port, String database, int connectNum) {
        super(new MongoCondition());
        try {
            this.client = MongoClients.create(new ConnectionString("mongodb://" + ip + ":" + port));
            this.database = this.client.getDatabase(database);
            this.pool = new SimplePool<>();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void insert(XBean xBean, Callback cb) {
        MongoCollection<Document> collection = this.database.getCollection(XBean.tblname(xBean.getClass()));
        Document document = new Document();
        xBean.each((key, value) -> document.append(key, value));
        collection.insertOne(document, (result, throwable) -> cb.call(result, throwable));
    }

    public void insert(List<XBean> xBeans, Callback cb) {
        MongoCollection<Document> collection = this.database.getCollection(XBean.tblname(xBeans.get(0).getClass()));
        List<Document> documents = new ArrayList<>();
        for (XBean xBean : xBeans) {
            Document document = new Document();
            xBean.each((key, value) -> document.append(key, value));
            documents.add(document);
        }
        collection.insertMany(documents, (result, throwable) -> cb.call(result, throwable));
    }

    public void delete(XBean xBean, Callback cb) {
        Class clz = xBean.getClass();
        MongoCollection<Document> collection = this.database.getCollection(XBean.tblname(clz));
        List<Bson> conds = new ArrayList<>();
        xBean.each((key, value) -> conds.add(eq(key, value)));
        collection.deleteOne(and(conds), (result, throwable) -> cb.call(result, throwable));
    }

    public void delete(XBean xBean, Integer pkey, Callback cb) {
        this.delete(xBean, Arrays.asList(pkey), cb);
    }

    public void delete(XBean xBean, List<Integer> pkeys, Callback cb) {
        Class clz = xBean.getClass();
        MongoCollection<Document> collection = this.database.getCollection(XBean.tblname(clz));
        List<Bson> conds = new ArrayList<>();
        for (int index : pkeys) {
            conds.add(eq(XBean.property(clz, index), xBean.get(index)));
        }
        collection.deleteMany(and(conds), (result, throwable) -> cb.call(result, throwable));
    }

    public void update(XBean xBean, Integer pkey, Callback cb) {
        this.update(xBean, Arrays.asList(pkey), cb);
    }

    public void update(XBean xBean, List<Integer> pkeys, Callback cb) {
        Class clz = xBean.getClass();
        MongoCollection<Document> collection = this.database.getCollection(XBean.tblname(clz));
        List<Bson> conds = new ArrayList<>();
        for (int index : pkeys) {
            conds.add(eq(XBean.property(clz, index), xBean.get(index)));
        }
        List<Bson> sets = new ArrayList<>();
        xBean.each((key, value) -> sets.add(set(key, value)));
        collection.updateMany(and(conds), combine(sets), (result, throwable) -> cb.call(result, throwable));
    }

    public void update(XBean xBean, Integer pkey, List<Integer> properties, Callback cb) {
        this.update(xBean, Arrays.asList(pkey), properties, cb);
    }

    public void update(XBean xBean, List<Integer> pkeys, List<Integer> properties, Callback cb) {
        Class clz = xBean.getClass();
        MongoCollection<Document> collection = this.database.getCollection(XBean.tblname(clz));
        List<Bson> conds = new ArrayList<>();
        for (int index : pkeys) {
            conds.add(eq(XBean.property(clz, index), xBean.get(index)));
        }
        List<Bson> sets = new ArrayList<>();
        for (int index : properties) {
            sets.add(set(XBean.property(clz, index), xBean.get(index)));
        }
        collection.updateMany(and(conds), combine(sets), (result, throwable) -> cb.call(result, throwable));
    }

    public void query(Class<? extends XBean> xBeanClass, Object condition, Callback cb) {
        MongoCollection<Document> collection = this.database.getCollection(XBean.tblname(xBeanClass));
        List<XBean> xBeans = new ArrayList<>();
        collection.find((Bson)condition).forEach((document) -> {
            try {
                XBean xBean = this.pool.malloc(xBeanClass);
                for (Map.Entry<String, Object> entry: document.entrySet()) {
                    xBean.set(XBean.index(xBeanClass, entry.getKey()), entry.getValue());
                }
                xBeans.add(xBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, (result, throwable) -> {
            cb.call(xBeans, throwable);
            this.pool.free(xBeans);
        });
    }

    static class MongoCondition implements ICondition {
        @Override
        public Object eq(Class<? extends XBean> xBeanClass, int index, Object value) {
            return Filters.eq(XBean.property(xBeanClass, index), value);
        }
        @Override
        public Object ne(Class<? extends XBean> xBeanClass, int index, Object value) {
            return Filters.ne(XBean.property(xBeanClass, index), value);
        }
        @Override
        public Object gt(Class<? extends XBean> xBeanClass, int index, Object value) {
            return Filters.gt(XBean.property(xBeanClass, index), value);
        }
        @Override
        public Object lt(Class<? extends XBean> xBeanClass, int index, Object value) {
            return Filters.lt(XBean.property(xBeanClass, index), value);
        }
        @Override
        public Object gte(Class<? extends XBean> xBeanClass, int index, Object value) {
            return Filters.gte(XBean.property(xBeanClass, index), value);
        }
        @Override
        public Object lte(Class<? extends XBean> xBeanClass, int index, Object value) {
            return Filters.lte(XBean.property(xBeanClass, index), value);
        }
        @Override
        public Object in(Class<? extends XBean> xBeanClass, int index, Object... values) {
            return Filters.in(XBean.property(xBeanClass, index), values);
        }
        @Override
        public Object nin(Class<? extends XBean> xBeanClass, int index, Object... values) {
            return Filters.nin(XBean.property(xBeanClass, index), values);
        }
        @Override
        public Object and(Object... datas) {
            Bson[] bsons = new Bson[datas.length];
            for (int i = 0; i < datas.length; i++) {
                bsons[i] = (Bson) datas[i];
            }
            return Filters.and(bsons);
        }
        @Override
        public Object or(Object... datas) {
            Bson[] bsons = new Bson[datas.length];
            for (int i = 0; i < datas.length; i++) {
                bsons[i] = (Bson) datas[i];
            }
            return Filters.or(bsons);
        }
    }
}
