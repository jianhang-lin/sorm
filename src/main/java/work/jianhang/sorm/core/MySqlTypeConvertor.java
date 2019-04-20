package work.jianhang.sorm.core;

/**
 * mysql数据库类型和java数据类型的转换
 */
public class MySqlTypeConvertor implements TypeConvertor {

    /**
     * mysql数据库类型转换成java数据类型
     * @param columnType 数据库字段的数据类型
     * @return java数据类型
     */
    public String databaseType2JavaType(String columnType) {
        if ("varchar".equalsIgnoreCase(columnType) || "char".equalsIgnoreCase(columnType)) {
            return "String";
        } else if ("int".equalsIgnoreCase(columnType) || "tinyint".equalsIgnoreCase(columnType)
                    || "smallint".equalsIgnoreCase(columnType) || "integer".equalsIgnoreCase(columnType)) {
            return "Integer";
        } else if ("bigint".equalsIgnoreCase(columnType)) {
            return "Long";
        } else if ("double".equalsIgnoreCase(columnType) || "float".equalsIgnoreCase(columnType)) {
            return "Double";
        } else if ("clob".equalsIgnoreCase(columnType)) {
            return "java.sql.Clob";
        } else if ("blob".equalsIgnoreCase(columnType)) {
            return "java.sql.Blob";
        } else if ("date".equalsIgnoreCase(columnType)) {
            return "java.sql.Date";
        } else if ("time".equalsIgnoreCase(columnType)) {
            return "java.sql.Time";
        } else if ("timestamp".equalsIgnoreCase(columnType)) {
            return "java.sql.Timestamp";
        }
        return null;
    }

    /**
     * java数据类型转换成mysql数据库类型
     * @param javaDataType java数据类型
     * @return mysql数据库类型
     */
    public String javaType2DatabaseType(String javaDataType) {
        // TODO
        return null;
    }
}
