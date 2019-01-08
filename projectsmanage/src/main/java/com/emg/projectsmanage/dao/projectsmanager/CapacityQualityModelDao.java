package com.emg.projectsmanage.dao.projectsmanager;

import com.emg.projectsmanage.pojo.CapacityQualityModel;
import com.emg.projectsmanage.pojo.CapacityQualityModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CapacityQualityModelDao {
    int countByExample(CapacityQualityModelExample example);

    int deleteByExample(CapacityQualityModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CapacityQualityModel record);

    int insertSelective(CapacityQualityModel record);

    List<CapacityQualityModel> selectByExample(CapacityQualityModelExample example);

    CapacityQualityModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CapacityQualityModel record, @Param("example") CapacityQualityModelExample example);

    int updateByExample(@Param("record") CapacityQualityModel record, @Param("example") CapacityQualityModelExample example);

    int updateByPrimaryKeySelective(CapacityQualityModel record);

    int updateByPrimaryKey(CapacityQualityModel record);
}