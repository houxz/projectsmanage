package com.emg.projectsmanage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emg.projectsmanage.dao.emapgoaccount.AuthorityModelDao;
import com.emg.projectsmanage.dao.emapgoaccount.DepartmentModelDao;
import com.emg.projectsmanage.dao.emapgoaccount.EmployeeModelDao;
import com.emg.projectsmanage.pojo.AuthorityModel;
import com.emg.projectsmanage.pojo.DepartmentModel;
import com.emg.projectsmanage.pojo.EmployeeModel;

@Service
public class EmapgoAccountService {

	@Autowired
	private DepartmentModelDao departmentModelDao;

	@Autowired
	private EmployeeModelDao employeeModelDao;
	
	@Autowired
	private AuthorityModelDao authorityModelDao;

	public List<DepartmentModel> getAllDepartment() {
		return departmentModelDao.getAllDepartment();
	}

	public List<EmployeeModel> getAllEmployees() {
		return employeeModelDao.getAllEmployees();
	}

	public EmployeeModel getOneEmployee(EmployeeModel record) {
		return employeeModelDao.getOneEmployee(record);
	}

	public List<EmployeeModel> getEmployeeByIDS(List<Integer> ids) {
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		map.put("ids", ids);
		return employeeModelDao.getEmployeeByIDS(map);
	}
	
	public List<EmployeeModel> getEmployeesByIDSAndRealname(List<Integer> ids, String realname) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		if(realname != null && realname.length() > 0)
			map.put("realname", "%"+realname+"%");
		return employeeModelDao.getEmployeesByIDSAndRealname(map);
	}

	public List<Map<String, Object>> getEmployeeListForZTree() {
		return employeeModelDao.getEmployeeListForZTree();
	}
	
	public AuthorityModel getAuthorityByUsername(String username) {
		return authorityModelDao.getAuthorityByUsername(username);
	}
}
