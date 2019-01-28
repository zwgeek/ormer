package zg.ormer.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POJO Bean的扩展, 采用枚举方式定义Bean属性
 */
public abstract class XBean implements Serializable {
    protected static Map<Class, String> className = new HashMap<>();
    protected static Map<Class, List<String>> classProperties = new HashMap<>();
    protected static Map<Class, Map<String, Integer>> classPropertyIndexs = new HashMap<Class, Map<String, Integer>>();

    private final List<Object> values = new ArrayList<>();

    /**
     * 获取表名
     * @return
     * @param xBeanClass
     */
    public static String tblname(Class<? extends XBean> xBeanClass) {
        return XBean.className.get(xBeanClass);
    }

    /**
     * 获取属性字符串
     * @param index
     * @return
     */
    public static String property(Class<? extends XBean> xBeanClass, int index) {
        return XBean.classProperties.get(xBeanClass).get(index);
    }

    /**
     * 获取属性索引
     * @param property
     * @return
     */
    public static int index(Class<? extends XBean> xBeanClass, String property) {
        return XBean.classPropertyIndexs.get(xBeanClass).get(property);
    }

    /**
     * 获取器
     * @param index
     * @return
     */
    public Object get(int index) {
        return this.values.get(index);
    }

    /**
     * 设置器
     * @param index
     * @param value
     */
    public void set(int index, Object value) {
        this.values.set(index, value);
    }

    /**
     * 迭代, 注意这是一个同步回调
     * @param cb
     */
    public void each(Callback cb) {
        for (Map.Entry<String, Integer> entry : XBean.classPropertyIndexs.get(this.getClass()).entrySet()) {
            Object value = this.values.get(entry.getValue());
            if (value == null) continue;
            cb.call(entry.getKey(), value);
        }
    }

    /**
     * 清理
     */
    public void clear() {
        this.values.clear();
    }

    @FunctionalInterface
    public interface Callback {
        void call(final String key, final Object value);
    }
}