package com.emg.projectsmanage.dao.projectsmanager;

import com.emg.projectsmanage.pojo.CapacityModel;
import com.emg.projectsmanage.pojo.CapacityModelExample;
import com.emg.projectsmanage.pojo.QualityCapcityModel;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CapacityModelDao {
    int countByExample(CapacityModelExample example);

    int deleteByExample(CapacityModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CapacityModel record);
    
	// add by lianhr begin 2018/12/17
	
	int insertSpecial(QualityCapcityModel record);
	// add by lianhr end

    int insertSelective(CapacityModel record);

    List<CapacityModel> selectByExample(CapacityModelExample example);

    CapacityModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CapacityModel record, @Param("example") CapacityModelExample example);

    int updateByExample(@Param("record") CapacityModel record, @Param("example") CapacityModelExample example);

    int updateByPrimaryKeySelective(CapacityModel record);

    int updateByPrimaryKey(CapacityModel record);
}