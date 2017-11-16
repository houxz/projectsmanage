package com.emg.projectsmanage.dao.projectsmanager;

import java.util.List;
import com.emg.projectsmanage.pojo.EmployeeSkillModel;

public interface EmployeeSkillModelDao {
    List<EmployeeSkillModel> queryEmployeeSkills(EmployeeSkillModel record);
    
    void addEmployeDetail(EmployeeSkillModel record);
    
    void delEmployDetail(EmployeeSkillModel record);
}