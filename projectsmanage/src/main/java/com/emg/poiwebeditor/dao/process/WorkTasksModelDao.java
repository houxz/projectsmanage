package com.emg.poiwebeditor.dao.process;

import java.util.List;
import java.util.Map;

import com.emg.poiwebeditor.pojo.WorkTasksModel;

public interface WorkTasksModelDao {
    List<WorkTasksModel> getWorkTasks(Map<String, Object> map);
    
    int countWorkTasks(Map<String, Object> map);
    
    int newWorkTask(WorkTasksModel record);
}