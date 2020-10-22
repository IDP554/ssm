package service.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dao.BaseDao;
import dao.user.UserDao;
import dao.user.UserDaoImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.User;

import javax.annotation.Resource;

/**
 * service层捕获异常，进行事务处理
 * 事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 * 事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService{
	@Resource
	private UserDao userDao;

	@Override
	public boolean add(User user) {
		boolean flag = false;
		int updateRows = 0;
		try {
			updateRows = userDao.add(user);
			if(updateRows > 0){
				flag = true;
				System.out.println("add success!");
			}else{
				System.out.println("add failed!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public User login(String userCode, String userPassword) {
		User user = null;
		try {
			user = userDao.getLoginUser(userCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//匹配密码
		if(null != user){
			if(!user.getUserPassword().equals(userPassword))
				user = null;
		}
		return user;
	}

	@Override
	public List<User> getUserList(String queryUserName,int queryUserRole,int currentPageNo, int pageSize) {
		List<User> userList = null;
		System.out.println("========Service层==========");
		System.out.print("姓名条件 ---- > " + queryUserName + "\t");
		System.out.println("角色条件 ---- > " + queryUserRole);
		System.out.print("当前页码 ---- > " + currentPageNo + "\t");
		System.out.println("单页最大显示 ---- > " + pageSize);
		try {
			userList = userDao.getUserList(queryUserName,queryUserRole,currentPageNo,pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userList;
	}

	@Override
	public User selectUserCodeExist(String userCode) {
		// TODO Auto-generated method stub
		Connection connection = null;
		User user = null;
		try {
			connection = BaseDao.getConnection();
			user = userDao.getLoginUser(userCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return user;
	}

	@Override
	public boolean deleteUserById(Integer delId) {
		// TODO Auto-generated method stub
		Connection connection = null;
		boolean flag = false;
		try {
			connection = BaseDao.getConnection();
			if(userDao.deleteUserById(delId) > 0)
				flag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}

	@Override
	public User getUserById(String id) {
		// TODO Auto-generated method stub
		User user = null;
		Connection connection = null;
		try{
			connection = BaseDao.getConnection();
			user = userDao.getUserById(id);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			user = null;
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return user;
	}

	@Override
	public boolean modify(User user) {
		// TODO Auto-generated method stub
		Connection connection = null;
		boolean flag = false;
		try {
			connection = BaseDao.getConnection();
			if(userDao.modify(user) > 0)
				flag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}

	@Override
	public boolean updatePwd(int id, String pwd) {
		// TODO Auto-generated method stub
		boolean flag = false;
		Connection connection = null;
		try{
			connection = BaseDao.getConnection();
			if(userDao.updatePwd(id,pwd) > 0)
				flag = true;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}

	@Override
	public int getUserCount(String queryUserName, int queryUserRole) {
		// TODO Auto-generated method stub
		Connection connection = null;
		int count = 0;
		System.out.print("条件查询的姓名--> " + queryUserName + "\t\t---------");
		System.out.println("条件查询的角色--> " + queryUserRole);
		try {
			connection = BaseDao.getConnection();
			count = userDao.getUserCount(queryUserName,queryUserRole);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return count;
	}
	
}
