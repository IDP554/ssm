package dao.role;

import java.sql.Connection;
import java.util.List;
import pojo.Role;

public interface RoleDao {
	
	public List<Role> getRoleList()throws Exception;

}
