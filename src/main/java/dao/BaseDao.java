package dao;

import tools.ProperManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 操作数据库的基类--静态类
 * @author Administrator
 *
 */
public class BaseDao {
	
//	static{//静态代码块,在类加载的时候执行
//		init();
//	}
	
	private static String driver = ProperManager.getInstance().getValueByKey("jdbc.driver");
	private static String url = ProperManager.getInstance().getValueByKey("jdbc.url");
	private static String user = ProperManager.getInstance().getValueByKey("jdbc.username");
	private static String password = ProperManager.getInstance().getValueByKey("jdbc.password");

//	private static String driver;
//	private static String url;
//	private static String user;
//	private static String password;
//
//	//初始化连接参数,从配置文件里获得
//	public static void init(){
//		Properties params=new Properties();
//		String configFile = "db.properties";
//		InputStream is=BaseDao.class.getClassLoader().getResourceAsStream(configFile);
//		try {
//			params.load(is);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		driver=params.getProperty("jdbc.driver");
//		url=params.getProperty("jdbc.url");
//		user=params.getProperty("jdbc.username");
//		password=params.getProperty("jdbc.password");
//
//	}
	
	
	/**
	 * 获取数据库连接
	 * @return
	 */
	public static Connection getConnection(){
		Connection connection = null;
		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connection;
	}
	/**
	 * 查询操作
	 * @param connection
	 * @param pstm
	 * @param rs
	 * @param sql
	 * @param params
	 * @return
	 */
	public static ResultSet execute(Connection connection,PreparedStatement pstm,ResultSet rs,
			String sql,Object[] params) throws Exception{
		pstm = connection.prepareStatement(sql);
		for(int i = 0; i < params.length; i++){
			pstm.setObject(i+1, params[i]);
		}
		rs = pstm.executeQuery();
		return rs;
	}
	/**
	 * 更新操作
	 * @param connection
	 * @param pstm
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static int execute(Connection connection,PreparedStatement pstm,
			String sql,Object[] params) throws Exception{
		int updateRows = 0;
		pstm = connection.prepareStatement(sql);
		for(int i = 0; i < params.length; i++){
			pstm.setObject(i+1, params[i]);
		}
		updateRows = pstm.executeUpdate();
		return updateRows;
	}
	
	/**
	 * 释放资源
	 * @param connection
	 * @param pstm
	 * @param rs
	 * @return
	 */
	public static boolean closeResource(Connection connection,PreparedStatement pstm,ResultSet rs){
		boolean flag = true;
		if(rs != null){
			try {
				rs.close();
				rs = null;//GC回收
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flag = false;
			}
		}
		if(pstm != null){
			try {
				pstm.close();
				pstm = null;//GC回收
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flag = false;
			}
		}
		if(connection != null){
			try {
				connection.close();
				connection = null;//GC回收
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				flag = false;
			}
		}
		
		return flag;
	}

}
