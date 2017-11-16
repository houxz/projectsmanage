package com.emg.projectsmanage.dao.projectsmanager;

import java.util.List;
import com.emg.projectsmanage.pojo.ProjectsWorkCountModel;

public interface ProjectsWorkCountModelDao {
    List<ProjectsWorkCountModel> queryModifyCount(ProjectsWorkCountModel record);
    
    List<ProjectsWorkCountModel> queryDistinctRolesIdList(ProjectsWorkCountModel record);
    
    String queryModifyTime(ProjectsWorkCountModel record);
}