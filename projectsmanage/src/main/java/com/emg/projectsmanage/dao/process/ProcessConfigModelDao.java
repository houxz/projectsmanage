package com.emg.projectsmanage.dao.process;

import java.util.List;
import java.util.Map;

import com.emg.projectsmanage.pojo.ProcessConfigModel;;

public interface ProcessConfigModelDao {
	
	List<ProcessConfigModel> selectAllProcessConfigModels(Integer processType);
	
	ProcessConfigModel selectByPrimaryKey(Map<String, Integer> map);

}