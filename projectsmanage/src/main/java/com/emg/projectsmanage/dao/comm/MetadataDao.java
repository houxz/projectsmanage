package com.emg.projectsmanage.dao.comm;

import java.util.List;
import java.util.Map;

import com.emg.projectsmanage.pojo.MetadataModel;

public interface MetadataDao {
    List<MetadataModel> selectByModuleAndKey(Map<String, String> moduleAndKey);
}