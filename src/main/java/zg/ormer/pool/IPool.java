package zg.ormer.pool;

import java.util.List;

public interface IPool<T> {
    /**
     * 分配一个对象
     * @return
     */
    T malloc(Class<T> clz) throws Exception;

    /**
     * 释放一个对象
     * @param t
     */
    void free(T t);

    /**
     * 释放一组对象
     * @param ts
     * @return
     */
    boolean free(List<T> ts);
}
