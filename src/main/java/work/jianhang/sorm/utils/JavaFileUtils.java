package work.jianhang.sorm.utils;

import work.jianhang.sorm.bean.ColumnInfo;
import work.jianhang.sorm.bean.JavaFieldGetSet;
import work.jianhang.sorm.core.TypeConvertor;

/**
 * 封装了生成Java文件(源代码)常用的操作
 */
public class JavaFileUtils {

    /**
     *  根据字段信息生成java属性信息。如：varchar username->private String username;以及对应的get、set方法源码
     * @param column 字段信息
     * @param convertor 类型转换器
     * @return java属性和get、set方法源码
     */
    public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor) {
        JavaFieldGetSet jfgs = new JavaFieldGetSet();
        String javaFieldType = convertor.databaseType2JavaType(column.getDataType());
        jfgs.setFieldInfo("\tprivate " + javaFieldType + " " + column.getName() + ";\n");

        // public String getUsername() {return username;}
        // 生成get方法源码
        StringBuilder getSrc = new StringBuilder();
        getSrc.append("\tpublic " + javaFieldType + " get" + StringUtils.firstChar2UpperCase(column.getName()) + "(){\n");
        getSrc.append("\t\treturn " + column.getName() + ";\n");
        getSrc.append("\t}\n");
        jfgs.setGetInfo(getSrc.toString());

        // public void setUsername(String username) {this.username = username;}
        // 生成set方法源码
        StringBuilder setSrc = new StringBuilder();
        setSrc.append("\tpublic void set" + StringUtils.firstChar2UpperCase(column.getName()) + "("+ javaFieldType + " " + column.getName() + "){\n");
        setSrc.append("\t\tthis." + column.getName() + " = " + column.getName() + ";\n");
        setSrc.append("\t}\n");
        jfgs.setSetInfo(setSrc.toString());
        return jfgs;
    }

}
