package com.emg.poiwebeditor.dao.pepro.qctask;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.emg.poiwebeditor.pojo.qctask.ItemInfoModel;
import com.emg.poiwebeditor.pojo.qctask.ItemInfoModelExample;

public interface ItemInfoModelDao {
    long countByExample(ItemInfoModelExample example);

    int deleteByExample(ItemInfoModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ItemInfoModel record);

    int insertSelective(ItemInfoModel record);

    List<ItemInfoModel> selectByExample(ItemInfoModelExample example);

    ItemInfoModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ItemInfoModel record, @Param("example") ItemInfoModelExample example);

    int updateByExample(@Param("record") ItemInfoModel record, @Param("example") ItemInfoModelExample example);

    int updateByPrimaryKeySelective(ItemInfoModel record);

    int updateByPrimaryKey(ItemInfoModel record);
}