package com.emg.projectsmanage.dao.process;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.emg.projectsmanage.pojo.ProcessConfigValueModel;

public interface ProcessConfigValueModelDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ProcessConfigValueModel record);

    int insertSelective(ProcessConfigValueModel record);
    
    int insert(List<ProcessConfigValueModel> records);

    ProcessConfigValueModel selectByPrimaryKey(Integer id);
    
    int deleteByProcessID(Long processID);
    
    List<ProcessConfigValueModel> selectByProcessID(Long processID);
    
    ProcessConfigValueModel selectByProcessIDAndConfigID(@Param("processid") Long processid, @Param("configid") Integer configid);

    int updateByPrimaryKeySelective(ProcessConfigValueModel record);

    int updateByPrimaryKey(ProcessConfigValueModel record);
}