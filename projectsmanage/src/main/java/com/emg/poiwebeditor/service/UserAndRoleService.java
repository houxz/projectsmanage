package com.emg.poiwebeditor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emg.poiwebeditor.dao.projectsmanager.RoleModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.UserRoleModelDao;
import com.emg.poiwebeditor.pojo.RoleModel;
import com.emg.poiwebeditor.pojo.UserRoleModel;

@Service
public class UserAndRoleService {

	@Autowired
	RoleModelDao roleDao;

	@Autowired
	UserRoleModelDao userRoleModelDao;

	public boolean isUserHasRole(Integer uid, String rolename) {
		RoleModel role = new RoleModel();
		role.setName(rolename);
		role.setEnabled(1);
		List<RoleModel> rs = roleDao.queryRoles(role);
		if (rs.size() == 0)
			return false;
		RoleModel r = rs.get(0);
		Integer rid = r.getId();
		UserRoleModel rr = new UserRoleModel();
		rr.setRoleid(rid);
		rr.setUserid(uid);
		List<UserRoleModel> rrs = userRoleModelDao.query(rr);
		if (rrs.size() == 0)
			return false;
		return true;
	}
}
