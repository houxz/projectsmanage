package com.emg.projectsmanage.dao.projectsmanager;

import com.emg.projectsmanage.pojo.MessageModel;
import java.util.List;
import java.util.Map;

public interface MessageModelDao {
    Integer countUncheckMessages(Integer userID);

    List<Map<String, Object>> getContacts(Integer userID);
    
    List<MessageModel> getMessagesByContact(Map<String, Object> map);
    
    Boolean checkMessage(Map<String, Object> map);
    
    Integer newMessage(MessageModel message);
}