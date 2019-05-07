package com.emg.poiwebeditor.dao.projectsmanager;

import java.util.List;
import java.util.Map;

import com.emg.poiwebeditor.pojo.MessageModel;

public interface MessageModelDao {
    Integer countUncheckMessages(Integer userID);

    List<Map<String, Object>> getContacts(Integer userID);
    
    List<MessageModel> getMessagesByContact(Map<String, Object> map);
    
    Boolean checkMessage(Map<String, Object> map);
    
    Integer newMessage(MessageModel message);
}