package com.emg.projectsmanage.dao.process;

import java.util.List;

import com.emg.projectsmanage.pojo.ProcessConfigValueModel;

public interface ProcessConfigValueModelDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ProcessConfigValueModel record);

    int insertSelective(ProcessConfigValueModel record);
    
    int insert(List<ProcessConfigValueModel> records);

    ProcessConfigValueModel selectByPrimaryKey(Integer id);
    
    int deleteByProcessID(Long processID);
    
    List<ProcessConfigValueModel> selectByProcessID(Long processID);

    int updateByPrimaryKeySelective(ProcessConfigValueModel record);

    int updateByPrimaryKey(ProcessConfigValueModel record);
}