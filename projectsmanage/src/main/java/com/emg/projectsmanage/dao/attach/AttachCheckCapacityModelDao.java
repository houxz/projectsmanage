package com.emg.projectsmanage.dao.attach;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.emg.projectsmanage.pojo.AttachCapacityModelExample;
import com.emg.projectsmanage.pojo.AttachCheckCapacityModel;
import com.emg.projectsmanage.pojo.AttachMakeCapacityModel;

/**
 * 附属表产能统计类
 * @author Administrator
 *
 */
public interface AttachCheckCapacityModelDao {
	
	/**
	 * 查询满足条件的附属表产能统计
	 * @param searchDate
	 * @param userid
	 * @param id
	 * @return
	 */
    List<AttachCheckCapacityModel> selectcheckAttachCapacity(AttachCapacityModelExample example );
    
    /**
	 *统计满足条件的总条数
	 * @param searchDate
	 * @param userid
	 * @param id
	 * @return
	 */
    int countByExample(AttachCapacityModelExample example);
    
    /**
     * 执行附属表统计产能函数
     */
    String doAttachCheckCapacityTask(@Param(value="date") String date);
    
    /**
     * 更新每天制作错误统计
     * @param models
     */
    void updateMakeError(@Param(value="models") List<AttachCheckCapacityModel> models);

    /**
     * 更新每天校正错误统计
     * @param models
     */
    void updateCheckError(@Param(value="models") List<AttachCheckCapacityModel> models);

    void updateUserName(@Param(value="models") List<AttachCheckCapacityModel> models);
}