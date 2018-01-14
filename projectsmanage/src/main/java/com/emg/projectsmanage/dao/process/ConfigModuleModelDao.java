package com.emg.projectsmanage.dao.process;

import com.emg.projectsmanage.pojo.ConfigModuleModel;

public interface ConfigModuleModelDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ConfigModuleModel record);

    int insertSelective(ConfigModuleModel record);

    ConfigModuleModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConfigModuleModel record);

    int updateByPrimaryKey(ConfigModuleModel record);
}