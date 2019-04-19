package work.jianhang.sorm.core;

import work.jianhang.po.Emp;
import work.jianhang.sorm.bean.ColumnInfo;
import work.jianhang.sorm.bean.TableInfo;
import work.jianhang.sorm.utils.JDBCUtils;
import work.jianhang.sorm.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
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

    @Override
    public void delete(Class clazz, Object id) {
        // Emp.class,2-->delete from emp where id = 2
        // 通过Class对象找TableInfo
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
        // 获得主键
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();

        String sql = "delete from " + tableInfo.getTname() + " where " + onlyPriKey.getName() + " = ? ";
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

    @Override
    public List queryRows(String sql, Class clazz, Object[] params) {
        Connection conn = DBManager.getConn();
        List list = null; // 存储查询结果的容器
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            // 给sql设值
            JDBCUtils.handleParams(ps, params);
            rs = ps.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            DBManager.close(ps, conn);
        }
        return list;
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


    public static void main(String[] args) {

    }

    public static void testDML() {
        Emp emp = new Emp();
        emp.setId(2);
        emp.setEmpname("Tony");
        emp.setBirthday(new java.sql.Date(System.currentTimeMillis()));
        emp.setAge(12);
        emp.setSalary(1111.0);
        // new MySqlQuery().delete(emp);
        // new MySqlQuery().insert(emp);
        new MySqlQuery().update(emp, new String[]{"empname", "age", "salary"});
    }
}
