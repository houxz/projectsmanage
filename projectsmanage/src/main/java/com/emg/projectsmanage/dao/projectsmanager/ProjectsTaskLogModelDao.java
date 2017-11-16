package com.emg.projectsmanage.dao.projectsmanager;

import java.util.List;

import com.emg.projectsmanage.pojo.ProjectsTaskLogModel;

public interface ProjectsTaskLogModelDao {
    int insert(ProjectsTaskLogModel record);
    
    int count(ProjectsTaskLogModel record);
    
    List<ProjectsTaskLogModel> select(ProjectsTaskLogModel record);
}