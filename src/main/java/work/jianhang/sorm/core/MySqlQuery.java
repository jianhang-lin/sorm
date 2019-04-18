package work.jianhang.sorm.core;

import java.util.List;

public class MySqlQuery implements Query {

    @Override
    public int executeDML(String sql, Object[] params) {
        return 0;
    }

    @Override
    public void insert(Object obj) {

    }

    @Override
    public void delete(Class clazz, int id) {

    }

    @Override
    public void delete(Object obj) {

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
