package com.emg.poiwebeditor.dao.emapgoaccount;

import java.util.List;
import java.util.Map;

import com.emg.poiwebeditor.pojo.EmployeeModel;

public interface EmployeeModelDao {
	List<EmployeeModel> getAllEmployees();
	
	EmployeeModel getOneEmployee(EmployeeModel record);
	
	List<EmployeeModel> getEmployeeByIDS(Map<String, List<Integer>> map);
	
	List<EmployeeModel> getEmployeesByIDSAndRealname(Map<String, Object> map);
	
	List<Map<String, Object>> getEmployeeListForZTree();
}