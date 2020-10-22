package tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProperManager {
    private static ProperManager properManager;
    private Properties params;

    /**
     * 私有构造
     * 只允许自己实例化
     */
    private ProperManager() {
        params = new Properties();
        String db = "db.properties";
        InputStream is = ProperManager.class.getClassLoader().getResourceAsStream(db);
        try {
            params.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * getInstance 生成唯一实例 懒汉模式
     * @return
     */
//    public synchronized static ProperManager getInstance(){
//        if (properManager == null){
//            properManager = new ProperManager();
//        }
//        return properManager;
//    }

    /**
     * 饿汉模式 延迟加载
     */
    public static ProperManager getInstance(){
        return ProperManager_in.properManager();
    }

    /**
     * 通过 key 拿到值
     * @param key
     * @return
     */
    public String getValueByKey(String key){
        return params.getProperty(key);
    }

    /**
     * 静态内部类
     */
    public static class ProperManager_in{

        /**
         * 实例化 ProperManager
         */
        public static ProperManager properManager(){
            return new ProperManager();
        }

    }}
