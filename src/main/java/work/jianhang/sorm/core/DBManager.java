package work.jianhang.sorm.core;

import work.jianhang.sorm.bean.Configuration;
import work.jianhang.sorm.pool.DBConnPool;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * 根据配置信息，维护连接对象的管理(增加连接池功能)
 */
public class DBManager {

    /**
     * 配置信息
     */
    private static Configuration conf;
    /**
     * 连接池对象
     */
    private static DBConnPool pool;

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
        conf.setQueryClass(pros.getProperty("queryClass"));
        conf.setPoolMinSize(Integer.valueOf(pros.getProperty("poolMinSize")));
        conf.setPoolMaxSize(Integer.valueOf(pros.getProperty("poolMaxSize")));

        // 加载TableContest
        System.out.println(TableContext.class);
    }

    /**
     * 创建新的Connection对象
     * @return 连接对象
     */
    public static Connection createConn() {
        try {
            Class.forName(conf.getDriver());
            // TODO 直接建立连接，后期增加连接池处理，提高效率
            return DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得Connection对象
     * @return 连接对象
     */
    public static Connection getConn() {
        /*try {
            Class.forName(conf.getDriver());
            return DriverManager.getConnection(conf.getUrl(), conf.getUser(), conf.getPwd());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
        if (pool == null) {
            pool = new DBConnPool();
        }
        return pool.getConnection();
    }

    /**
     * 关闭传入的ResultSet、PreparedStatement、Connection对象
     * @param rs ResultSet对象
     * @param ps PreparedStatement对象
     * @param conn Connection对象
     */
    public static void close(ResultSet rs, Statement ps, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pool.close(conn);
    }

    /**
     * 关闭传入的PreparedStatement、Connection对象
     * @param ps PreparedStatement对象
     * @param conn Connection对象
     */
    public static void close(Statement ps, Connection conn) {
        close(null, ps, conn);
    }

    /**
     * 关闭传入的Connection对象
     * @param conn Connection对象
     */
    public static void close(Connection conn) {
        close(null, null, conn);
    }

    /**
     * 返回Configuration对象
     * @return 连接对象
     */
    public static Configuration getConf() {
        return conf;
    }
}
