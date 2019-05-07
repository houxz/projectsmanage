package com.emg.poiwebeditor.dao.process;

import com.emg.poiwebeditor.pojo.ConfigModuleModel;

public interface ConfigModuleModelDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ConfigModuleModel record);

    int insertSelective(ConfigModuleModel record);

    ConfigModuleModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ConfigModuleModel record);

    int updateByPrimaryKey(ConfigModuleModel record);
}