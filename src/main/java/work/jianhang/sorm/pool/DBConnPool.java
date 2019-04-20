package work.jianhang.sorm.pool;

import work.jianhang.sorm.core.DBManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接池的类
 */
public class DBConnPool {
    /**
     * 连接池对象
     */
    private List<Connection> pool;

    /**
     * 最大连接数
     */
    private static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize();
    /**
     * 最小连接数
     */
    private static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();

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

    /**
     * 从连接池中取出一个连接
     * @return 连接对象
     */
    public synchronized Connection getConnection() {
        int lastIndex = pool.size() - 1;
        Connection conn =  pool.get(lastIndex);
        pool.remove(lastIndex);
        return conn;
    }

    /**
     * 将连接放回池中
     * @param conn 连接对象
     */
    public synchronized void close(Connection conn) {
        if (pool.size() >= DBConnPool.POOL_MAX_SIZE) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            pool.add(conn);
        }
    }
}
