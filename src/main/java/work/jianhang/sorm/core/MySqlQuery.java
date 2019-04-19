package work.jianhang.sorm.core;

import work.jianhang.po.Emp;
import work.jianhang.vo.EmpVO;

import java.util.List;

/**
 * 负责针对Mysql数据库的查询
 */
public class MySqlQuery extends Query {

    public static void main(String[] args) {
        Object obj = new MySqlQuery().queryValue("select empname from emp where salary > ?", new Object[]{10});
        System.out.println(obj);

        Number obj2 = new MySqlQuery().queryNumber("select count(*) from emp where salary > ?", new Object[]{10});
        System.out.println(obj2);
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

    public static void testQueryRows() {
        List<Emp> emps = new MySqlQuery().queryRows("select id, empname, age from emp where age > ?", Emp.class, new Object[]{10});
        for (Emp e : emps) {
            System.out.println(e.getId() + ":" + e.getEmpname() + ":" + e.getAge());
        }

        String sql2 = "select e.id, e.empname, 2 * salary 'xinshui', age, d.dname 'deptName', d.address 'deptAddr' from emp e join dept d on e.deptId = d.id";
        List<EmpVO> empVOs = new MySqlQuery().queryRows(sql2, EmpVO.class, null);
        for (EmpVO vo : empVOs) {
            System.out.println(vo.getId() + ":" + vo.getEmpname() + ":" + vo.getXinshui() + ":" + vo.getAge() + ":" + vo.getDeptName() + ":" + vo.getDeptAddr());
        }
    }
}
