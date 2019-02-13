package zg.ormer.pool;

import zg.ormer.core.XBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimplePool<T extends XBean> implements IPool<T> {
    private Map<Class<? extends T>, List<T>> classObjects = new HashMap<>();

    @Override
    public T malloc(Class<? extends T> clz) throws Exception {
        List<T> objects = this.classObjects.get(clz);
        if (objects == null) {
            objects = new ArrayList<>();
            this.classObjects.put(clz, objects);
            return clz.newInstance();
        }

        if (objects.size() == 0) {
            return clz.newInstance();
        }

        return objects.remove(0);
    }

    @Override
    public void free(T t) {
        t.clear();
        this.classObjects.get(t.getClass()).add(t);
    }

    @Override
    public boolean free(List<T> ts) {
        if (ts.size() > 0) {
            List<T> objects = this.classObjects.get(ts.get(0).getClass());
            for (T t : ts) {
                t.clear();
                objects.add(t);
            }
        }
        return true;
    }
}
