package com.emg.poiwebeditor.dao.process;

import java.util.List;

import com.emg.poiwebeditor.pojo.ConfigValueModel;

public interface ConfigValueModelDao {

    List<ConfigValueModel> selectConfigsById(ConfigValueModel model);
    
}