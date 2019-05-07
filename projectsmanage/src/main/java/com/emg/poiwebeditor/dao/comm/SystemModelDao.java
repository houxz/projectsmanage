package com.emg.poiwebeditor.dao.comm;

import java.util.List;
import java.util.Map;

import com.emg.poiwebeditor.pojo.SystemModel;

public interface SystemModelDao {
    List<SystemModel> getSystemsByNames(Map<String, List<String>> map);
    public List<SystemModel> getAllSystems();
}