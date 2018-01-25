package com.emg.projectsmanage.dao.process;

import java.util.List;
import java.util.Map;

import com.emg.projectsmanage.pojo.ProcessConfigModel;;

public interface ProcessConfigModelDao {
	
	List<Map<String, Object>> selectAllConfigModuleModels();
	
	List<Map<String, Object>> selectAllConfigTypeModels();
	
	List<Map<String, Object>> selectAllConfigValueRangeModels();
	
	List<Map<String, Object>> selectAllConfigDBModels();
	
	List<Map<String, Object>> selectAllProcessConfigModels();
	
	int updateDefaultValueSelective(ProcessConfigModel record);

}