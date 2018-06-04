package com.emg.projectsmanage.dao.process;

import java.util.List;
import java.util.Map;

import com.emg.projectsmanage.pojo.ProcessConfigModel;;

public interface ProcessConfigModelDao {
	
	List<Map<String, Object>> selectAllConfigDBModels();
	
	List<ProcessConfigModel> selectAllProcessConfigModels();
	
	int updateDefaultValueSelective(ProcessConfigModel record);
	
	ProcessConfigModel selectByPrimaryKey(Integer id);

}