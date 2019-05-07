package com.emg.poiwebeditor.dao.process;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProcessModelExample;

public interface ProcessModelDao {
    int countByExample(ProcessModelExample example);

    int deleteByExample(ProcessModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ProcessModel record);

    int insertSelective(ProcessModel record);

    List<ProcessModel> selectByExample(ProcessModelExample example);

    ProcessModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ProcessModel record, @Param("example") ProcessModelExample example);

    int updateByExample(@Param("record") ProcessModel record, @Param("example") ProcessModelExample example);

    int updateByPrimaryKeySelective(ProcessModel record);

    int updateByPrimaryKey(ProcessModel record);
    
    int getRowNumByByPrimaryKey(Long id);
}