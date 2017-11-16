package com.emg.projectsmanage.dao.emapgoaccount;

import java.util.List;
import java.util.Map;

import com.emg.projectsmanage.pojo.EmployeeModel;

public interface EmployeeModelDao {
	List<EmployeeModel> getAllEmployees();
	
	EmployeeModel getOneEmployee(EmployeeModel record);
	
	List<EmployeeModel> getEmployeeByIDS(Map<String, List<Integer>> map);
	
	List<Map<String, Object>> getEmployeeListForZTree();
}