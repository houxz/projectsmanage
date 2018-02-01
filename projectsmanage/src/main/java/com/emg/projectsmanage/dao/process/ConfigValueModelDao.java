package com.emg.projectsmanage.dao.process;

import com.emg.projectsmanage.pojo.ConfigValueModel;
import java.util.List;
//import org.apache.ibatis.annotations.Param;

public interface ConfigValueModelDao {
  //  int countByExample(ProcessModelExample example);

  //  int deleteByExample(ProcessModelExample example);

 //   int deleteByPrimaryKey(Long id);

  //  int insert(ProcessModel record);

 //   int insertSelective(ProcessModel record);

    List<ConfigValueModel> selectConfigsById(ConfigValueModel model);
    
    List<ConfigValueModel> selectProcessIdByConfig(ConfigValueModel model);
    
    ConfigValueModel selectValueByConfig(ConfigValueModel model);
    

 //   ProcessModel selectByPrimaryKey(Long id);

 //   int updateByExampleSelective(@Param("record") ProcessModel record, @Param("example") ProcessModelExample example);

  //  int updateByExample(@Param("record") ProcessModel record, @Param("example") ProcessModelExample example);

 //   int updateByPrimaryKeySelective(ProcessModel record);

  //  int updateByPrimaryKey(ProcessModel record);
}