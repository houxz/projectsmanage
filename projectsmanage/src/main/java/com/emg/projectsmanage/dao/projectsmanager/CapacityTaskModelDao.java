package com.emg.projectsmanage.dao.projectsmanager;

import com.emg.projectsmanage.pojo.CapacityTaskModel;
import com.emg.projectsmanage.pojo.CapacityTaskModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CapacityTaskModelDao {
    int countByExample(CapacityTaskModelExample example);

    int deleteByExample(CapacityTaskModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CapacityTaskModel record);

    int insertSelective(CapacityTaskModel record);

    List<CapacityTaskModel> selectByExample(CapacityTaskModelExample example);

    CapacityTaskModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CapacityTaskModel record, @Param("example") CapacityTaskModelExample example);

    int updateByExample(@Param("record") CapacityTaskModel record, @Param("example") CapacityTaskModelExample example);

    int updateByPrimaryKeySelective(CapacityTaskModel record);

    int updateByPrimaryKey(CapacityTaskModel record);
}