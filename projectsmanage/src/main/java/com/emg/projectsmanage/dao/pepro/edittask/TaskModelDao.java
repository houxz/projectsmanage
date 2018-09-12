package com.emg.projectsmanage.dao.pepro.edittask;

import com.emg.projectsmanage.pojo.edittask.TaskModel;
import com.emg.projectsmanage.pojo.edittask.TaskModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TaskModelDao {
    int countByExample(TaskModelExample example);

    int deleteByExample(TaskModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TaskModel record);

    int insertSelective(TaskModel record);

    List<TaskModel> selectByExample(TaskModelExample example);

    TaskModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TaskModel record, @Param("example") TaskModelExample example);

    int updateByExample(@Param("record") TaskModel record, @Param("example") TaskModelExample example);

    int updateByPrimaryKeySelective(TaskModel record);

    int updateByPrimaryKey(TaskModel record);
}