package com.emg.projectsmanage.dao.process;

import com.emg.projectsmanage.pojo.CondigDBModel;

public interface CondigDBModelDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CondigDBModel record);

    int insertSelective(CondigDBModel record);

    CondigDBModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CondigDBModel record);

    int updateByPrimaryKey(CondigDBModel record);
}