package com.emg.projectsmanage.dao.process;

import com.emg.projectsmanage.pojo.ConfigValueModel;
import java.util.List;

public interface ConfigValueModelDao {

    List<ConfigValueModel> selectConfigsById(ConfigValueModel model);
    
    List<ConfigValueModel> selectProcessIdByConfig(ConfigValueModel model);
    
    ConfigValueModel selectValueByConfig(ConfigValueModel model);
    
    List<ConfigValueModel> selectConfigs(ConfigValueModel model);
    
}