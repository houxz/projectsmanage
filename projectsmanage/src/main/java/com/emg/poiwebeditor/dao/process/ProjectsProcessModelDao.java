package com.emg.poiwebeditor.dao.process;

import java.util.List;
import java.util.Map;

import com.emg.poiwebeditor.pojo.ProjectsProcessModel;

public interface ProjectsProcessModelDao {
    List<ProjectsProcessModel> getProjectsProcess(Map<String, Object> map);
    
    int countProjectsProcess(Map<String, Object> map);
    
    int newProjectsProcess(ProjectsProcessModel record);
}