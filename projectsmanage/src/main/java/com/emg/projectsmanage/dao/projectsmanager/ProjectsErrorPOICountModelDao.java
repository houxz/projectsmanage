package com.emg.projectsmanage.dao.projectsmanager;

import java.util.List;

import com.emg.projectsmanage.pojo.ProjectsErrorPOICountModel;

public interface ProjectsErrorPOICountModelDao {
    List<ProjectsErrorPOICountModel> queryModifyPOICount(ProjectsErrorPOICountModel record);
    
    List<ProjectsErrorPOICountModel> queryDistinctPOIRolesIdList(ProjectsErrorPOICountModel record);
    
    String queryModifyPOITime(ProjectsErrorPOICountModel record);
}