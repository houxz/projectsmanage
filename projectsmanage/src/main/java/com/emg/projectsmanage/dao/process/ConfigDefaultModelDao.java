package com.emg.projectsmanage.dao.process;

import com.emg.projectsmanage.pojo.ConfigDefaultModel;
import com.emg.projectsmanage.pojo.ConfigDefaultModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

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