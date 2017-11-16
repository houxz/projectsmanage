package com.emg.projectsmanage.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.emg.projectsmanage.dao.projectsmanager.MessageModelDao;
import com.emg.projectsmanage.pojo.MessageModel;

@Service
public class MessageModelService {
	final static private String CACHEVALUE = "MessageCache";

	@Autowired
	private MessageModelDao messageModelDao;

	@Cacheable(value = CACHEVALUE, key = "('UNCHECKCOUNT-').concat(#userID)")
	public Integer countUncheckMessages(Integer userID) {
		return messageModelDao.countUncheckMessages(userID);
	}

	@Cacheable(value = CACHEVALUE, key = "('CONTACTS-').concat(#userID)")
	public List<Map<String, Object>> getContacts(Integer userID) {
		List<Map<String, Object>> senders = new ArrayList<Map<String, Object>>();
		senders = messageModelDao.getContacts(userID);
		return senders;
	}

	@Cacheable(value = CACHEVALUE, key = "('MESSAGES-').concat(#userID).concat('-').concat(#contactID)")
	public List<MessageModel> getMessagesByContact(Integer userID, Integer contactID) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userID", userID);
		map.put("contactID", contactID);
		return messageModelDao.getMessagesByContact(map);
	}

	@Caching(evict = { 
			@CacheEvict(value = CACHEVALUE, key = "('UNCHECKCOUNT-').concat(#userID)"), 
			@CacheEvict(value = CACHEVALUE, key = "('CONTACTS-').concat(#userID)"),
			@CacheEvict(value = CACHEVALUE, key = "('MESSAGES-').concat(#userID).concat('-').concat(#contactID)")})
	public Boolean checkMessage(Integer userID, Integer contactID, String messageIDs) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userID", userID);
		map.put("messageIDs", messageIDs);
		return messageModelDao.checkMessage(map);
	}

	@Caching(evict = { 
			@CacheEvict(value = CACHEVALUE, key = "('UNCHECKCOUNT-').concat(#message.receiver)"), 
			@CacheEvict(value = CACHEVALUE, key = "('CONTACTS-').concat(#message.receiver)"),
			@CacheEvict(value = CACHEVALUE, key = "('CONTACTS-').concat(#message.sender)"),
			@CacheEvict(value = CACHEVALUE, key = "('MESSAGES-').concat(#message.receiver).concat('-').concat(#message.sender)"),
			@CacheEvict(value = CACHEVALUE, key = "('MESSAGES-').concat(#message.sender).concat('-').concat(#message.receiver)")})
	public int newMessage(MessageModel message) {
		return messageModelDao.newMessage(message);
	}
}
