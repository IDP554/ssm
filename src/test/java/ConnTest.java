import dao.BaseDao;
import org.junit.Test;

import java.sql.Connection;

public class ConnTest {
    @Test
    public void test1(){
        Connection conn = BaseDao.getConnection();
        System.out.println("成功！");
    }}
