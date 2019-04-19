package work.jianhang.sorm.core;

import work.jianhang.sorm.bean.ColumnInfo;
import work.jianhang.sorm.bean.TableInfo;
import work.jianhang.sorm.utils.JDBCUtils;
import work.jianhang.sorm.utils.ReflectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 负责针对Mysql数据库的查询
 */
public class MySqlQuery implements Query {

    @Override
    public int executeDML(String sql, Object[] params) {
        Connection conn = DBManager.getConn();
        int count = 0;
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql);

            // 给sql设值
            JDBCUtils.handleParams(ps, params);

            count = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(ps, conn);
        }
        return count;
    }

    @Override
    public void insert(Object obj) {

    }

    @Override
    public void delete(Class clazz, Object id) {
        // Emp.class,2-->delete from emp where id = 2
        // 通过Class对象找TableInfo
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
        // 获得主键
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();

        String sql = "delete from" + tableInfo.getTname() + " where " + onlyPriKey + " = ? ";
        executeDML(sql, new Object[]{id});
    }

    @Override
    public void delete(Object obj) {
        Class c = obj.getClass();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();

        // 通过反射机制，调用属性对应的get方法或set方法
        Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
        delete(c, priKeyValue);
    }

    @Override
    public int update(Object obj, String[] fieldNames) {
        return 0;
    }

    @Override
    public List queryRows(String sql, Class clazz, Object[] params) {
        return null;
    }

    @Override
    public Object queryUniqueRows(String sql, Class clazz, Object[] params) {
        return null;
    }

    @Override
    public Object queryValue(String sql, Object[] params) {
        return null;
    }

    @Override
    public Number queryNumber(String sql, Object[] params) {
        return null;
    }
}
