package zg.ormer.driver;

import zg.ormer.core.XBean;

import java.util.List;

/**
 * 数据库无关适配器
 */
public abstract class Driver {

    public final ICondition condition;

    public final ISort sort;

    public final IProjection projection;

    public Driver(ICondition condition, ISort sort, IProjection projection) {
        this.condition = condition;
        this.sort = sort;
        this.projection = projection;
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

    public abstract void query(Class<? extends XBean> xBeanClass, Object condition, Object sort, Callback cb);

    public abstract void projection(Class<? extends XBean> xBeanClass, Object condition, Object fields, Callback cb);

    public abstract void projection(Class<? extends XBean> xBeanClass, Object condition, Object sort, Object fields, Callback cb);

    public interface ICondition {
        Object exists(Class<? extends XBean> xBeanClass, int index);
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

    public interface ISort {
        Object desc(Class<? extends XBean> xBeanClass, int... indexs);
        Object asc(Class<? extends XBean> xBeanClass, int... indexs);
    }

    public interface IProjection {
        Object include(Class<? extends XBean> xBeanClass, int... indexs);
        Object exclude(Class<? extends XBean> xBeanClass, int... indexs);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void call(T result, Throwable t);
    }
}
