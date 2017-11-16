package com.emg.projectsmanage.dao.projectsmanager;

import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProjectModelDao {
	int countByExample(ProjectModelExample example);

	int deleteByExample(ProjectModelExample example);

	int deleteByPrimaryKey(Long id);

	int insert(ProjectModel record);

	int insertSelective(ProjectModel record);

	List<ProjectModel> selectByExample(ProjectModelExample example);

	ProjectModel selectByPrimaryKey(Long id);

	int updateByExampleSelective(@Param("record") ProjectModel record, @Param("example") ProjectModelExample example);

	int updateByExample(@Param("record") ProjectModel record, @Param("example") ProjectModelExample example);

	int updateByPrimaryKeySelective(ProjectModel record);

	int updateByPrimaryKey(ProjectModel record);
}