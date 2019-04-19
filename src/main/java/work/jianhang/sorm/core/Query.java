package work.jianhang.sorm.core;

import work.jianhang.sorm.bean.ColumnInfo;
import work.jianhang.sorm.bean.TableInfo;
import work.jianhang.sorm.utils.JDBCUtils;
import work.jianhang.sorm.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责查询（对外提供服务的核心类）
 */
public abstract class Query implements Cloneable {

    /**
     * 直接执行一个DML语句
     * @param sql sql语句
     * @param params 参数
     * @return 执行sql语句后影响记录的行数
     */
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

    /**
     * 将一个对象存储到数据库中，把对象中不为null的属性往数据库存储，如果数字为null则为0。
     * @param obj 要存储的对象
     */
    public void insert(Object obj) {
        // obj-->表中。 insert into 表名(id, name, pwd) values (?, ?, ?)
        Class c = obj.getClass();
        List<Object> params = new ArrayList<>(); // 存储sql参数对象
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(tableInfo.getTname());
        sql.append(" (");
        int countNotNullField = 0; // 计算不为null的属性值
        Field[] fs = c.getDeclaredFields();
        for (Field f : fs) {
            String fieldName = f.getName();
            Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);

            if (fieldValue != null) {
                countNotNullField++;
                sql.append(fieldName);
                sql.append(",");
                params.add(fieldValue);
            }
        }

        sql.setCharAt(sql.length() - 1, ')');
        sql.append(" values (");
        for (int i=0; i<countNotNullField; i++) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length() - 1, ')');

        executeDML(sql.toString(), params.toArray());
    }

    /**
     * 删除clazz表示类对应的表中的记录(指定主键值id的记录)
     * @param clazz 跟表对应的类的Class对象
     * @param id 主键的值
     */
    public void delete(Class clazz, Object id) {
        // Emp.class,2-->delete from emp where id = 2
        // 通过Class对象找TableInfo
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
        // 获得主键
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();

        String sql = "delete from " + tableInfo.getTname() + " where " + onlyPriKey.getName() + " = ? ";
        executeDML(sql, new Object[]{id});
    }

    /**
     * 删除对象在数据库中对应的记录(对象所在的类对应列表，对象的主键的值对应到记录)
     * @param obj
     */
    public void delete(Object obj) {
        Class c = obj.getClass();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();

        // 通过反射机制，调用属性对应的get方法或set方法
        Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
        delete(c, priKeyValue);
    }

    /**
     * 更新对象对应的记录，并且只更新指定的字段的值
     * @param obj 所要更新的对象
     * @param fieldNames 更新的属性列表
     * @return 执行sql语句后影响记录的行数
     */
    public int update(Object obj, String[] fieldNames) {
        // obj{"username", "pwd"} --> update 表名 set username = ?, pwd = ? where id = ?
        Class c = obj.getClass();
        List<Object> params = new ArrayList<>(); // 存储sql参数对象
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        ColumnInfo priKey = tableInfo.getOnlyPriKey();
        StringBuilder sql = new StringBuilder("update ");
        sql.append(tableInfo.getTname()).append(" set ");

        for (String fname : fieldNames) {
            Object fvalue = ReflectUtils.invokeGet(fname, obj);
            params.add(fvalue);
            sql.append(fname).append(" = ?,");
        }
        sql.setCharAt(sql.length() - 1, ' ');
        sql.append(" where ").append(priKey.getName()).append(" = ?");

        params.add(ReflectUtils.invokeGet(priKey.getName(), obj));
        return executeDML(sql.toString(), params.toArray());
    }

    /**
     * 查询返回多行记录，并将每行记录封装到clazz指定的类的对象中
     * @param sql 查询语句
     * @param clazz 封装数据的javabean类的Class对象
     * @param params sql参数
     * @return 查询到的结果
     */
    public List queryRows(final String sql, final Class clazz, final Object[] params) {
        return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {
            @Override
            public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
                List list = null;
                try {
                    ResultSetMetaData metaData = rs.getMetaData();
                    // 多行
                    while (rs.next()) {
                        if (list == null) {
                            list = new ArrayList();
                        }
                        Object rowObject = clazz.newInstance(); // 调用javabean的无参构造器

                        // 多列 select empname, age from emp where id > ? and age > 18
                        for (int i=0; i<metaData.getColumnCount(); i++) {
                            String columnName = metaData.getColumnLabel(i+1);
                            Object columnValue = rs.getObject(i+1);

                            // 调用rowObj对象的setXXX(String xxx)方法，将columnValue的值设置进去
                            ReflectUtils.invokeSet(rowObject, columnName, columnValue);
                        }
                        list.add(rowObject);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return list;
            }
        });
    }

    /**
     * 采用模板方法模式将JDBC操作封装成模板，便于重用
     * @param sql sql语句
     * @param params sql的参数
     * @param clazz 记录要封装的java类
     * @param back CallBack的实现类，实现回调
     * @return
     */
    public Object executeQueryTemplate(String sql, Object[] params, Class clazz, CallBack back) {
        Connection conn = DBManager.getConn();
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            ps = conn.prepareStatement(sql);

            // 给sql设值
            JDBCUtils.handleParams(ps, params);
            rs = ps.executeQuery();
            return back.doExecute(conn, ps, rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBManager.close(ps, conn);
        }
    }

    /**
     * 查询返回一行记录，并将该记录封装到clazz指定的类的对象中
     * @param sql 查询语句
     * @param clazz 封装数据的javabean类的Class对象
     * @param params sql参数
     * @return 查询到的结果
     */
    public Object queryUniqueRows(String sql, Class clazz, Object[] params) {
        List list = queryRows(sql, clazz, params);
        return (list != null && list.size() > 0)?list.get(0) : null;
    }

    /**
     * 查询返回一个值(一行一列)，并将该值返回
     * @param sql 查询语句
     * @param params sql参数
     * @return 查询到的结果
     */
    public Object queryValue(String sql, Object[] params) {
        return executeQueryTemplate(sql, params, null, new CallBack() {
            @Override
            public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
                Object value = null;
                try {
                    while (rs.next()) {
                        // select count(*) from emp
                        value = rs.getObject(1);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return value;
            }
        });
    }

    /**
     * 查询返回一个数字(一行一列)，并将该值返回
     * @param sql 查询语句
     * @param params sql参数
     * @return 查询到的数字
     */
    public Number queryNumber(String sql, Object[] params) {
        return (Number) queryValue(sql, params);
    }

    /**
     * 分页查询
     * @param pageNum 第几页数据
     * @param size 每页显示多少记录
     * @return
     */
    public abstract Object queryPagenate(int pageNum, int size);
}
