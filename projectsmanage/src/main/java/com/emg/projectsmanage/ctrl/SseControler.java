package com.emg.projectsmanage.ctrl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.MessageReadType;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.MessageModel;
import com.emg.projectsmanage.service.EmapgoAccountService;
import com.emg.projectsmanage.service.MessageModelService;

@Controller
public class SseControler {
	private static final Logger logger = LoggerFactory.getLogger(SseControler.class);
	
	@Autowired
	private MessageModelService messageModelService;
	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@RequestMapping(value = "/chat.web", params = "action=msgcount", produces = "text/event-stream;charset=utf-8")
	public @ResponseBody
	String msgcount(Model model, HttpServletRequest request, HttpSession session) {
		int count = 0;
		try {
			Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			if (userid != null && userid > 0) {
				count = messageModelService.countUncheckMessages(userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			count = 0;
		}
		return "data:" + count + "\n\n";
	}
	
	@RequestMapping(value = "/chat.web", params = "action=getcontacts", produces = "text/event-stream;charset=utf-8")
	public @ResponseBody
	String getContacts(Model model, HttpServletRequest request, HttpSession session) {
		List<Map<String, Object>> contacts = null;
		try {
			Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			if (userid != null && userid > 0) {
				contacts = messageModelService.getMyContacts(userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			contacts = null;
		}
		String jsonStr = JSONArray.fromObject(contacts).toString();
		return "data:" + jsonStr + "\n\n";
	}
	
	@RequestMapping(value = "/chat.web", params = "action=getmessages", produces = "text/event-stream;charset=utf-8")
	public @ResponseBody
	String getMessages(Model model, HttpServletRequest request, HttpSession session) {
		List<MessageModel> contacts = null;
		try {
			Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			Integer contactID = ParamUtils.getIntParameter(request, "contactid", -1);
			if (userid != null && userid > 0 && contactID != null && contactID > 0) {
				contacts = messageModelService.getMessagesByContact(userid, contactID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			contacts = null;
		}
		String jsonStr = JSONArray.fromObject(contacts).toString();
		return "data:" + jsonStr + "\n\n";
	}
	
	@RequestMapping(value = "/chat.web", params = "atn=checkmessage")
	public ModelAndView checkMessage(HttpServletRequest request, HttpSession session) {
		logger.debug("SseControler-checkMessage start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
		Integer contactID = ParamUtils.getIntParameter(request, "contactID", -1);
		String messageIDs = ParamUtils.getParameter(request, "messageids");
		messageModelService.checkMessage(userid, contactID, messageIDs);
		logger.debug("SseControler-checkMessage end.");
		return json;
	}
	
	@RequestMapping(value = "/chat.web", params = "atn=sendmessage")
	public ModelAndView sendMessage(HttpServletRequest request, HttpSession session) {
		logger.debug("SseControler-checkMessage start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
		String username = (String) session.getAttribute(CommonConstants.SESSION_USER_NAME);
		String message = ParamUtils.getParameter(request, "message");
		Integer contactID = ParamUtils.getIntParameter(request, "contactID", 0);
		if(message == null || message.length() == 0 || contactID == null || contactID == 0){
			json.addObject("result", 0);
			return json;
		}
		EmployeeModel record = new EmployeeModel();
		record.setId(contactID);
		EmployeeModel contact = emapgoAccountService.getOneEmployee(record);
		MessageModel messageModel = new MessageModel();
		messageModel.setType(1);
		messageModel.setSender(userid);
		messageModel.setSendername(username);
		messageModel.setReceiver(contactID);
		messageModel.setReceivername(contact.getRealname());
		messageModel.setMessage(message);
		messageModel.setChecked(MessageReadType.UNREAD.getValue());
		try{
			int ret = messageModelService.newMessage(messageModel);
			if(ret > 0)
				json.addObject("result", 1);
			else
				json.addObject("result", 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		logger.debug("SseControler-checkMessage end.");
		return json;
	}
	
	@RequestMapping(value = "/chat.web", params = "atn=getallcontacts")
	public ModelAndView getAllContacts(HttpServletRequest request, HttpSession session) {
		logger.debug("SseControler-getAllContacts start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		
		List<EmployeeModel> contacts = emapgoAccountService.getAllEmployees();
		json.addObject("contacts", contacts);
		
		logger.debug("SseControler-getAllContacts end.");
		return json;
	}
}
