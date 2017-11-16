package com.emg.projectsmanage.dao.projectsmanager;

import java.util.List;

import com.emg.projectsmanage.pojo.RoleModel;

public interface RoleModelDao {
    List<RoleModel> queryRoles(RoleModel record);
    
    int addRole(RoleModel record);
}