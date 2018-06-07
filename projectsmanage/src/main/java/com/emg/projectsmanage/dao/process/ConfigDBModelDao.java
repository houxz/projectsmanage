package com.emg.projectsmanage.dao.process;

import java.util.List;

import com.emg.projectsmanage.pojo.ConfigDBModel;

public interface ConfigDBModelDao {
	
	List<ConfigDBModel> selectAllConfigDBModels();
	
    int deleteByPrimaryKey(Integer id);

    int insert(ConfigDBModel record);

    int insertSelective(ConfigDBModel record);

    ConfigDBModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConfigDBModel record);

    int updateByPrimaryKey(ConfigDBModel record);
    
    ConfigDBModel selectDbInfoByDbid(Integer id);
    
    List<ConfigDBModel> selectDbInfos();
}