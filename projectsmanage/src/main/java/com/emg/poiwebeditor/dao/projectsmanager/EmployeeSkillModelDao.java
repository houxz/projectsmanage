package com.emg.poiwebeditor.dao.projectsmanager;

import java.util.List;

import com.emg.poiwebeditor.pojo.EmployeeSkillModel;

public interface EmployeeSkillModelDao {
    List<EmployeeSkillModel> queryEmployeeSkills(EmployeeSkillModel record);
    
    void addEmployeDetail(EmployeeSkillModel record);
    
    void delEmployDetail(EmployeeSkillModel record);
}