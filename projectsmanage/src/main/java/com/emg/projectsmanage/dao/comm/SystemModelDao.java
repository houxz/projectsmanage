package com.emg.projectsmanage.dao.comm;

import java.util.List;
import java.util.Map;

import com.emg.projectsmanage.pojo.SystemModel;

public interface SystemModelDao {
    List<SystemModel> getSystemsByNames(Map<String, List<String>> map);
    public List<SystemModel> getAllSystems();
}