package zg.ormer.driver;

import zg.ormer.core.XBean;

import java.util.List;

/**
 * 数据库无关适配器
 */
public abstract class Driver {

    public final ICondition condition;

    public Driver(ICondition condition) {
        this.condition = condition;
    }

    public abstract void insert(XBean xBean, Callback cb);

    public abstract void insert(List<XBean> xBeans, Callback cb);

    public abstract void delete(XBean xBean, Callback cb);

    public abstract void delete(XBean xBean, Integer pkey, Callback cb);

    public abstract void delete(XBean xBean, List<Integer> pkeys, Callback cb);

    public abstract void update(XBean xBean, Integer pkey, Callback cb);

    public abstract void update(XBean xBean, List<Integer> pkeys, Callback cb);

    public abstract void update(XBean xBean, Integer pkey, List<Integer> properties, Callback cb);

    public abstract void update(XBean xBean, List<Integer> pkeys, List<Integer> properties, Callback cb);

    public abstract void query(Class<? extends XBean> xBeanClass, Object condition, Callback cb);

    public interface ICondition {
        Object eq(Class<? extends XBean> xBeanClass, int index, Object value);
        Object ne(Class<? extends XBean> xBeanClass, int index, Object value);
        Object gt(Class<? extends XBean> xBeanClass, int index, Object value);
        Object lt(Class<? extends XBean> xBeanClass, int index, Object value);
        Object gte(Class<? extends XBean> xBeanClass, int index, Object value);
        Object lte(Class<? extends XBean> xBeanClass, int index, Object value);
        Object in(Class<? extends XBean> xBeanClass, int index, Object... values);
        Object nin(Class<? extends XBean> xBeanClass, int index, Object... values);
        Object and(Object... datas);
        Object or(Object... datas);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void call(T result, Throwable t);
    }
}
