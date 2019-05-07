package com.emg.poiwebeditor.dao.projectsmanager;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;

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