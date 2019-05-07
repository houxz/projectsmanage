package com.emg.poiwebeditor.dao.process;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.emg.poiwebeditor.pojo.ConfigDefaultModel;
import com.emg.poiwebeditor.pojo.ConfigDefaultModelExample;

public interface ConfigDefaultModelDao {
    int countByExample(ConfigDefaultModelExample example);

    int deleteByExample(ConfigDefaultModelExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ConfigDefaultModel record);

    int insertSelective(ConfigDefaultModel record);

    List<ConfigDefaultModel> selectByExample(ConfigDefaultModelExample example);

    ConfigDefaultModel selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ConfigDefaultModel record, @Param("example") ConfigDefaultModelExample example);

    int updateByExample(@Param("record") ConfigDefaultModel record, @Param("example") ConfigDefaultModelExample example);

    int updateByPrimaryKeySelective(ConfigDefaultModel record);

    int updateByPrimaryKey(ConfigDefaultModel record);
}