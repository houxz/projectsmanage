package com.emg.poiwebeditor.dao.projectsmanager;

import java.util.List;

import com.emg.poiwebeditor.pojo.RoleModel;

public interface RoleModelDao {
    List<RoleModel> queryRoles(RoleModel record);
    
    int addRole(RoleModel record);
}