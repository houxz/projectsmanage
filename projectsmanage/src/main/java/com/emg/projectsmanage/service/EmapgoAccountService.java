package com.emg.projectsmanage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.emg.projectsmanage.dao.emapgoaccount.DepartmentModelDao;
import com.emg.projectsmanage.dao.emapgoaccount.EmployeeModelDao;
import com.emg.projectsmanage.pojo.DepartmentModel;
import com.emg.projectsmanage.pojo.EmployeeModel;

@Service
public class EmapgoAccountService {
	final static private String CACHEVALUE = "EmapAccountCache";

	@Autowired
	private DepartmentModelDao departmentModelDao;

	@Autowired
	private EmployeeModelDao employeeModelDao;

	@Cacheable(value = CACHEVALUE, key = "#root.methodName")
	public List<DepartmentModel> getAllDepartment() {
		return departmentModelDao.getAllDepartment();
	}

	@Cacheable(value = CACHEVALUE, key = "#root.methodName")
	public List<EmployeeModel> getAllEmployees() {
		return employeeModelDao.getAllEmployees();
	}

	@Cacheable(value = CACHEVALUE, key = "#root.methodName + #record.id + #record.username + #record.password + #record.department")
	public EmployeeModel getOneEmployee(EmployeeModel record) {
		return employeeModelDao.getOneEmployee(record);
	}

	@Cacheable(value = CACHEVALUE, key = "#root.methodName + #ids")
	public List<EmployeeModel> getEmployeeByIDS(List<Integer> ids) {
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		map.put("ids", ids);
		return employeeModelDao.getEmployeeByIDS(map);
	}
	
	@Cacheable(value = CACHEVALUE, key = "#root.methodName + #ids + #realname")
	public List<EmployeeModel> getEmployeesByIDSAndRealname(List<Integer> ids, String realname) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		if(realname != null && realname.length() > 0)
			map.put("realname", "%"+realname+"%");
		return employeeModelDao.getEmployeesByIDSAndRealname(map);
	}

	@Cacheable(value = CACHEVALUE, key = "#root.methodName")
	public List<Map<String, Object>> getEmployeeListForZTree() {
		return employeeModelDao.getEmployeeListForZTree();
	}
}
