package com.emg.poiwebeditor.dao.comm;

import java.util.List;
import java.util.Map;

import com.emg.poiwebeditor.pojo.MetadataModel;

public interface MetadataDao {
    List<MetadataModel> selectByModuleAndKey(Map<String, String> moduleAndKey);
}