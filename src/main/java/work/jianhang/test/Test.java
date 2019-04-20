package work.jianhang.test;

import work.jianhang.sorm.core.Query;
import work.jianhang.sorm.core.QueryFactory;
import work.jianhang.vo.EmpVO;

import java.util.List;

/**
 * 客户端调用的测试类
 */
public class Test {

    public static void main(String[] args) {
        test01();
    }

    public static void test01() {
        Query q = QueryFactory.createQuery();
        String sql2 = "select e.id, e.empname, 2 * salary 'xinshui', age, d.dname 'deptName', d.address 'deptAddr' from emp e join dept d on e.deptId = d.id";
        List<EmpVO> empVOs = q.queryRows(sql2, EmpVO.class, null);
        for (EmpVO vo : empVOs) {
            System.out.println(vo.getId() + ":" + vo.getEmpname() + ":" + vo.getXinshui() + ":" + vo.getAge() + ":" + vo.getDeptName() + ":" + vo.getDeptAddr());
        }
    }
}
