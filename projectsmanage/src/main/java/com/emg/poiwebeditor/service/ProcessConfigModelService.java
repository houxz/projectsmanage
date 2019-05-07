package com.emg.poiwebeditor.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emg.poiwebeditor.common.ProcessConfigEnum;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.dao.process.ProcessConfigModelDao;
import com.emg.poiwebeditor.pojo.ProcessConfigModel;

@Service
public class ProcessConfigModelService {

	@Autowired
	private ProcessConfigModelDao processConfigModelDao;

	public List<ProcessConfigModel> selectAllProcessConfigModels(Integer processType){
		return processConfigModelDao.selectAllProcessConfigModels(processType);
	};
	
	public ProcessConfigModel selectByPrimaryKey(ProcessConfigEnum processConfigEnum, ProcessType processType) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("id", processConfigEnum.getValue());
		map.put("processType", processType.getValue());
		return processConfigModelDao.selectByPrimaryKey(map);
	};
}
