package work.jianhang.sorm.core;

import work.jianhang.sorm.bean.Configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * 根据配置信息，维护连接对象的管理(增加连接池功能)
 */
public class DBManager {

    private static Configuration conf;

    static {
        Properties pros = new Properties();
        try {
            pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        conf = new Configuration();
        conf.setDriver(pros.getProperty("driver"));
        conf.setUrl(pros.getProperty("url"));
        conf.setUser(pros.getProperty("user"));
        conf.setPwd(pros.getProperty("pwd"));
        conf.setUsingDB(pros.getProperty("usingDB"));
        conf.setSrcPath(pros.getProperty("srcPath"));
        conf.setPoPackage(pros.getProperty("poPackage"));
    }
}
