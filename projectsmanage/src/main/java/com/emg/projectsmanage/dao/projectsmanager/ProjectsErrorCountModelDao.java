package com.emg.projectsmanage.dao.projectsmanager;

import java.util.List;

import com.emg.projectsmanage.pojo.ProjectsErrorCountModel;

public interface ProjectsErrorCountModelDao {
    List<ProjectsErrorCountModel> queryProptyErrorCount(ProjectsErrorCountModel record);
    
    List<ProjectsErrorCountModel> queryDistinctProptyErrorRolesIdList(ProjectsErrorCountModel record);
    
    String queryProptyErrorTime(ProjectsErrorCountModel record);
    
    List<ProjectsErrorCountModel> queryModifyGeoCount(ProjectsErrorCountModel record);
    
    List<ProjectsErrorCountModel> queryDistinctGeoRolesIdList(ProjectsErrorCountModel record);
    
    String queryModifyGeoTime(ProjectsErrorCountModel record);
}