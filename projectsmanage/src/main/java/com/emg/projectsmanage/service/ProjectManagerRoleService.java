package com.emg.projectsmanage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.projectsmanager.RoleModelDao;
import com.emg.projectsmanage.pojo.RoleModel;

@Service
public class ProjectManagerRoleService {
	final static private String CACHEVALUE = "ProjectManagerRoleCache";
	final static private String CACHEKEYGENERATOR = "baseCacheKeyGenerator";

	@Autowired
	private RoleModelDao roleModelDao;

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public RoleModel getWorkerRole() {
		RoleModel roleparam = new RoleModel();
		roleparam.setName(RoleType.ROLE_WORKER.toString());
		roleparam.setEnabled(1);
		List<RoleModel> roles = roleModelDao.queryRoles(roleparam);
		if (roles != null && roles.size() > 0)
			return roles.get(0);
		else
			return null;
	}

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public RoleModel getCheckerRole() {
		RoleModel roleparam = new RoleModel();
		roleparam.setName(RoleType.ROLE_CHECKER.toString());
		roleparam.setEnabled(1);
		List<RoleModel> roles = roleModelDao.queryRoles(roleparam);
		if (roles != null && roles.size() > 0)
			return roles.get(0);
		else
			return null;
	}

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public List<RoleModel> getAllEnabledRoles() {
		RoleModel roleparam = new RoleModel();
		roleparam.setEnabled(1);
		return roleModelDao.queryRoles(roleparam);
	}

	@CacheEvict(value = CACHEVALUE, allEntries = true)
	public int addRole(RoleModel record) {
		return roleModelDao.addRole(record);
	}
}
