package work.jianhang.sorm.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 回调类
 */
public interface CallBack {

    /**
     * 回调事件
     * @param conn Connection连接对象
     * @param ps PreparedStatemnet对象
     * @param rs ResultSet对象
     * @return Object
     */
    public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs);

}
