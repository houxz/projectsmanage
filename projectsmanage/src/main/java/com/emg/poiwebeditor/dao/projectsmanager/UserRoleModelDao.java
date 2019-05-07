package com.emg.poiwebeditor.dao.projectsmanager;

import java.util.List;
import java.util.Map;

import com.emg.poiwebeditor.pojo.UserRoleModel;

public interface UserRoleModelDao {
    List<UserRoleModel> query(UserRoleModel record);
    
    List<UserRoleModel> queryAll();
    
    int queryCount();
    
    int delEpleRole(UserRoleModel record);
    
    int addEpleRole(UserRoleModel record);
    
    List<Map<String, Object>> getEpleRoles(int userid);
}