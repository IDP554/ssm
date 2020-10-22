package service.role;

import java.sql.Connection;
import java.util.List;

import dao.BaseDao;
import dao.role.RoleDao;
import org.springframework.stereotype.Service;
import pojo.Role;

import javax.annotation.Resource;

@Service
public class RoleServiceImpl implements RoleService{

	@Resource
	private RoleDao roleDao;

	@Override
	public List<Role> getRoleList() {
		List<Role> roleList = null;
		try {
			roleList = roleDao.getRoleList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleList;
	}
	
}
