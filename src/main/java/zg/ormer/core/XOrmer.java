package zg.ormer.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.ormer.driver.Driver;
import zg.ormer.utils.Assert;
import zg.ormer.utils.ClzParse;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class XOrmer {
    public static final Logger logger = LogManager.getLogger(XBean.class);
    public static Driver driver;
    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("ormer.properties"));

            Assert.verify(properties.getProperty("common.package") != null);
            Assert.verify(properties.getProperty("common.drvier") != null);
            Assert.verify(properties.getProperty("common.drvier.ip") != null);
            Assert.verify(properties.getProperty("common.drvier.port") != null);
            Assert.verify(properties.getProperty("common.drvier.database") != null);

            String[] clznames = ClzParse.findClassesByPackage(properties.getProperty("common.package"),
                    new ArrayList<String>(Arrays.asList(XBean.class.getName())), ClzParse.EMPTY_LIST);
            for (String clzname : clznames) {
                Class<?> clz = Class.forName(clzname);
                List<String> propertyList = new ArrayList<>();
                Map<String, Integer> propertyMap = new HashMap<String, Integer>();
                for (Field field : clz.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        XProperty xProperty = clz.getAnnotation(XProperty.class);
                        if (xProperty == null) continue;
                        propertyList.set(field.getInt(clz), field.getName());
                        propertyMap.put(field.getName(), field.getInt(clz));
                    }
                }
                XBean.className.put(clz, clzname);
                XBean.classProperties.put(clz, propertyList);
                XBean.classPropertyIndexs.put(clz, propertyMap);
            }
            Class<?> clz = Class.forName(properties.getProperty("common.drvier"));
            Constructor ct = clz.getDeclaredConstructor(String.class, int.class, String.class);
            XOrmer.driver = (Driver) ct.newInstance(properties.getProperty("common.drvier.ip"),
                    properties.getProperty("common.drvier.port"), properties.getProperty("common.drvier.database"));
        } catch (Throwable e) {
            logger.error("uncatch exception:", e);
        }
    }

    /**
     * 序列化成json
     */
    public static String XBeanToJSON(XBean xBean) {
        StringBuilder builder = new StringBuilder("{");
        xBean.each((key, value) -> {
            builder.append("\"").append(key).append("\":");
            if (value instanceof String) {
                builder.append("\"").append(value).append("\",");
            } else {
                builder.append(value).append(",");
            }
        });
        builder.deleteCharAt(builder.length()).append("}");
        return builder.toString();
    }
}
