package com.emg.projectsmanage.dao.process;

import java.util.List;
import java.util.Map;

import com.emg.projectsmanage.pojo.WorkTasksModel;

public interface WorkTasksModelDao {
    List<WorkTasksModel> getProjectsProgressByUserid(Map<String, Object> map);
    
    int countProjectsProgressByUserid(Map<String, Object> map);
    
    List<WorkTasksModel> getProjectsProgress(Map<String, Object> map);
    
    int newProjectsProgress(WorkTasksModel record);
}