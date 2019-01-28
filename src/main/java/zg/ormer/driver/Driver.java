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

    public abstract void query(Class<XBean> xBeanClass, Object condition, Callback cb);

    public interface ICondition<D> {
        D eq(Class<XBean> xBeanClass, int index, Object value);
        D ne(Class<XBean> xBeanClass, int index, Object value);
        D gt(Class<XBean> xBeanClass, int index, Object value);
        D lt(Class<XBean> xBeanClass, int index, Object value);
        D gte(Class<XBean> xBeanClass, int index, Object value);
        D lte(Class<XBean> xBeanClass, int index, Object value);
        D in(Class<XBean> xBeanClass, int index, Object... values);
        D nin(Class<XBean> xBeanClass, int index, Object... values);
        D and(D... datas);
        D or(D... datas);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void call(T result, Throwable t);
    }
}
