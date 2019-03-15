package com.emg.projectsmanage.dao.projectsmanager;

import java.util.List;

import com.emg.projectsmanage.pojo.FeatureFinishedModel;

public interface FeatureFinishedModelDao {
	int insert(FeatureFinishedModel record);
	int queryCount(FeatureFinishedModel record);
	List<FeatureFinishedModel> query(FeatureFinishedModel record);

}
