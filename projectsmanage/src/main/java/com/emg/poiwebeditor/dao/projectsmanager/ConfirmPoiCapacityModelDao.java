package com.emg.poiwebeditor.dao.projectsmanager;

import java.util.List;


import com.emg.poiwebeditor.pojo.CapacityModelExample;
import com.emg.poiwebeditor.pojo.ConfirmPoiCapacityModel;

public interface ConfirmPoiCapacityModelDao {
	
	int countByExample(CapacityModelExample example);
	
	List<ConfirmPoiCapacityModel> selectByExample(CapacityModelExample example);
	
	int insert(ConfirmPoiCapacityModel record);
}
