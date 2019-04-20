package work.jianhang.sorm.pool;

import work.jianhang.sorm.core.DBManager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接池的类
 */
public class DBConnPool {
    /**
     * 连接池对象
     */
    private static List<Connection> pool;

    /**
     * 最大连接数
     */
    private static final int POOL_MAX_SIZE = 100;
    /**
     * 最小连接数
     */
    private static final int POOL_MIN_SIZE = 10;

    /**
     * 初如化连接池，使池中的连接数达到最小值
     */
    public void initPool() {
        if (pool == null) {
            pool = new ArrayList();
        }

        while (pool.size() < DBConnPool.POOL_MIN_SIZE) {
            pool.add(DBManager.createConn());
            System.out.println("init pool, size = " + pool.size());
        }
    }

    public DBConnPool() {
        initPool();
    }
}
