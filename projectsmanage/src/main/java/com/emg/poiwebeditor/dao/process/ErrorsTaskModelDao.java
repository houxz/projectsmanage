package com.emg.poiwebeditor.dao.process;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.emg.poiwebeditor.pojo.ErrorsTaskModel;
import com.emg.poiwebeditor.pojo.ErrorsTaskModelExample;

public interface ErrorsTaskModelDao {
    int countByExample(ErrorsTaskModelExample example);

    int deleteByExample(ErrorsTaskModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ErrorsTaskModel record);

    int insertSelective(ErrorsTaskModel record);

    List<ErrorsTaskModel> selectByExample(ErrorsTaskModelExample example);

    ErrorsTaskModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ErrorsTaskModel record, @Param("example") ErrorsTaskModelExample example);

    int updateByExample(@Param("record") ErrorsTaskModel record, @Param("example") ErrorsTaskModelExample example);

    int updateByPrimaryKeySelective(ErrorsTaskModel record);

    int updateByPrimaryKey(ErrorsTaskModel record);
}