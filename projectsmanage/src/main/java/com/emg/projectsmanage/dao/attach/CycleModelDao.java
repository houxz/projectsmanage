package com.emg.projectsmanage.dao.attach;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.emg.projectsmanage.pojo.CycleModel;
import com.emg.projectsmanage.pojo.CycleModelExample;

public interface CycleModelDao {
    long countByExample(CycleModelExample example);

    int deleteByExample(CycleModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CycleModel record);

    int insertSelective(CycleModel record);

    List<CycleModel> selectByExample(CycleModelExample example);

    CycleModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CycleModel record, @Param("example") CycleModelExample example);

    int updateByExample(@Param("record") CycleModel record, @Param("example") CycleModelExample example);

    int updateByPrimaryKeySelective(CycleModel record);

    int updateByPrimaryKey(CycleModel record);
    
    List<CycleModel> selectExistRecord(@Param("record") CycleModel record);
    
    /**
     * 根据指定用户ID， 登陆日期以及结束来标识更新结束标识，当最后一次更新退出时间比当前系统时间大于指定心跳包间隔，则自动更新结束标识为结束，
     * 因为这种可能存在系统崩溃后没能准确发送退出包
     * @param cycle
     * @param differminute
     * @return
     */
    int updateEnd(@Param("cycle") CycleModel cycle, @Param("differminute") int differminute);
    
    /**
     * 根据指定USERID的退出时间
     * @param cycle
     * @return
     */
    int updateLogouttime(@Param("cycle") CycleModel cycle);
    
    /**
     * 退出
     * @param cycle
     * @return
     */
    int updateExit(@Param("cycle") CycleModel cycle);
    
}