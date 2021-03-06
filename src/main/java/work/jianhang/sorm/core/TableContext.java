package work.jianhang.sorm.core;

import work.jianhang.sorm.bean.ColumnInfo;
import work.jianhang.sorm.bean.TableInfo;
import work.jianhang.sorm.utils.JavaFileUtils;
import work.jianhang.sorm.utils.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构
 */
public class TableContext {
    /**
     * 表名为kye，表信息为value
     */
    public static Map<String, TableInfo> tables = new HashMap<String, TableInfo>();

    /**
     * 将po的class对象和表信息对象关联起来，便于重用
     */
    public static Map<Class, TableInfo> poClassTableMap = new HashMap<Class, TableInfo>();

    public TableContext() {
    }

    static {
        try {
            // 初始化获取表的信息
            Connection conn = DBManager.getConn();
            DatabaseMetaData dbmd = conn.getMetaData();

            ResultSet tableSet = dbmd.getTables(null, "%", "%", new String[]{"TABLE"});

            while (tableSet.next()) {
                String tableName = tableSet.getString("TABLE_NAME");
                TableInfo ti = new TableInfo(tableName, new ArrayList<ColumnInfo>(), new HashMap<String, ColumnInfo>());
                tables.put(tableName, ti);

                // 查询表中的所有字段
                ResultSet set = dbmd.getColumns(null, "%", tableName, "%");
                while (set.next()) {
                    ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME"), 0);
                    ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
                }

                // 查询表中的主键
                ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);
                while (set2.next()) {
                    ColumnInfo ci2 = ti.getColumns().get(set2.getObject("COLUMN_NAME"));
                    ci2.setKeyType(1);// 设置主键类型
                    ti.getPriKeys().add(ci2);
                }
                if (ti.getPriKeys().size() > 0) {
                    ti.setOnlyPriKey(ti.getPriKeys().get(0));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 更新类结构
        updateJavaPOFile();

        // 加载po包下面所有的类，便于重用，提交效率
        loadPOTables();
    }

    public static Map<String, TableInfo> getTableInfos() {
        return tables;
    }

    /**
     * 根据表结构，更新配置的po包下面的java类
     * 实现了从表结构转化到类结构
     */
    public static void updateJavaPOFile() {
        Map<String, TableInfo> map = TableContext.getTableInfos();
        for (TableInfo t : map.values()) {
            JavaFileUtils.createJavaPOFile(t, new MySqlTypeConvertor());
        }
    }

    /**
     * 加载po包下面的类
     */
    public static void loadPOTables() {

        for (TableInfo tableInfo : tables.values()) {
            try {
                Class c = Class.forName(DBManager.getConf().getPoPackage() + "." + StringUtils.firstChar2UpperCase(tableInfo.getTname()));
                poClassTableMap.put(c, tableInfo);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
