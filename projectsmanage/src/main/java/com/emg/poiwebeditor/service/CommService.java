package com.emg.poiwebeditor.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.emg.poiwebeditor.dao.comm.MetadataDao;
import com.emg.poiwebeditor.dao.comm.SystemModelDao;
import com.emg.poiwebeditor.pojo.MetadataModel;
import com.emg.poiwebeditor.pojo.SystemModel;

@Service
public class CommService {
	final static private String CACHEVALUE = "CommCache";
	final static private String CACHEKEYGENERATOR = "baseCacheKeyGenerator";

	@Autowired
	private MetadataDao metadataDao;

	@Autowired
	private SystemModelDao systemModelDao;

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public List<MetadataModel> getDifficuties() {
		Map<String, String> moduleAndKey = new HashMap<String, String>();
		String module = "projectmanager.projects";
		String key = "projectmanager.tb_projects.pdifficulty";
		moduleAndKey.put("module", module);
		moduleAndKey.put("key", key);
		return metadataDao.selectByModuleAndKey(moduleAndKey);
	}

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public List<MetadataModel> getSkillLevels() {
		Map<String, String> moduleAndKey = new HashMap<String, String>();
		String module = "projectmanager.skill";
		String key = "projectmanager.tb_employee_skill.skilllevel";
		moduleAndKey.put("module", module);
		moduleAndKey.put("key", key);
		return metadataDao.selectByModuleAndKey(moduleAndKey);
	}

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public List<MetadataModel> getSkillModules() {
		Map<String, String> moduleAndKey = new HashMap<String, String>();
		String module = "projectmanager.skill";
		String key = "projectmanager.tb_employee_skill.skillmodule";
		moduleAndKey.put("module", module);
		moduleAndKey.put("key", key);
		return metadataDao.selectByModuleAndKey(moduleAndKey);
	}

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public List<MetadataModel> queryAllErrorStates() {
		Map<String, String> moduleAndKey = new HashMap<String, String>();
		String module = "error.errorstate";
		String key = "tb_error.errorstate";
		moduleAndKey.put("module", module);
		moduleAndKey.put("key", key);
		return metadataDao.selectByModuleAndKey(moduleAndKey);
	}

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public List<MetadataModel> queryAllErrorModifyStates() {
		Map<String, String> moduleAndKey = new HashMap<String, String>();
		String module = "error.modifystate";
		String key = "tb_error.modifystate";
		moduleAndKey.put("module", module);
		moduleAndKey.put("key", key);
		return metadataDao.selectByModuleAndKey(moduleAndKey);
	}

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public List<MetadataModel> queryAllOprationType() {
		Map<String, String> moduleAndKey = new HashMap<String, String>();
		String module = "evidence.action";
		String key = "evidence.tb_action.type";
		moduleAndKey.put("module", module);
		moduleAndKey.put("key", key);
		return metadataDao.selectByModuleAndKey(moduleAndKey);
	}

	@Cacheable(value = CACHEVALUE, keyGenerator = CACHEKEYGENERATOR)
	public List<SystemModel> getAllSystems() {
		return systemModelDao.getAllSystems();
	}
}
