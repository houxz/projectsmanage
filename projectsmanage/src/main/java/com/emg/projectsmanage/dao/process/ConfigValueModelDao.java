package com.emg.projectsmanage.dao.process;

import com.emg.projectsmanage.pojo.ConfigValueModel;
import java.util.List;

public interface ConfigValueModelDao {

    List<ConfigValueModel> selectConfigsById(ConfigValueModel model);
    
}