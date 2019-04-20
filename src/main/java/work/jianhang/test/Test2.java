package work.jianhang.test;

/**
 * 测试连接池的调用效率
 */
public class Test2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i=0; i<3000; i++) {
            Test.test01();
        }
        long end = System.currentTimeMillis();
        System.out.println("final cost time:" + (end - start)); // 不加连接池的耗时：313947, 增加连接池后耗时：33170
    }

}
