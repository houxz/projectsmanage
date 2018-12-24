package com.emg.projectsmanage.dao.process;

import java.util.List;
import java.util.Map;

import com.emg.projectsmanage.pojo.ProjectsProcessModel;

public interface ProjectsProcessModelDao {
    List<ProjectsProcessModel> getProjectsProcess(Map<String, Object> map);
    
    int countProjectsProcess(Map<String, Object> map);
    
    int newProjectsProcess(ProjectsProcessModel record);
}