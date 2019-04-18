package work.jianhang.sorm.bean;

/**
 * 封装了java属性和get、set方法的源代码
 */
public class JavaFieldGetSet {

    /**
     * 属性的源码信息，如：private int userId;
     */
    private String fieldInfo;

    /**
     * get方法的源码信息，如：public int getUserId() {return userId;}
     */
    private String getInfo;

    /**
     * set方法的源码信息，如：public void setUserId() {this.userId = userId;}
     */
    private String setInfo;

    public JavaFieldGetSet() {
    }

    public JavaFieldGetSet(String fieldInfo, String getInfo, String setInfo) {
        this.fieldInfo = fieldInfo;
        this.getInfo = getInfo;
        this.setInfo = setInfo;
    }

    public String getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(String fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getGetInfo() {
        return getInfo;
    }

    public void setGetInfo(String getInfo) {
        this.getInfo = getInfo;
    }

    public String getSetInfo() {
        return setInfo;
    }

    public void setSetInfo(String setInfo) {
        this.setInfo = setInfo;
    }
}
