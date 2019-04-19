package work.jianhang.sorm.core;

/**
 * 创建Query对象的工厂类
 */
public class QueryFactory {

    private static QueryFactory factory = new QueryFactory();
    private static Query prototypeObj; // 原型对象

    static {
        try {
            Class c = Class.forName(DBManager.getConf().getQueryClass());
            prototypeObj = (Query) c.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private QueryFactory() {
    }

    public Query createQuery() {
        try {
            return (Query) prototypeObj.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
