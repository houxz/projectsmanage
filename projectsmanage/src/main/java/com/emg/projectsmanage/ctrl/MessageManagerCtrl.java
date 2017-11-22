package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.pojo.MessageModel;
import com.emg.projectsmanage.service.MessageModelService;

@Controller
@RequestMapping("/message.web")
public class MessageManagerCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(MessageManagerCtrl.class);
	
	@Autowired
	private MessageModelService messageModelService;

	@RequestMapping(params = "action=getmycontacts")
	public ModelAndView getMyContacts(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("MessageManagerCtrl-getMyContacts start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<Map<String, Object>> contacts = new ArrayList<Map<String,Object>>();
		try {
			Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			if (userid != null && userid > 0) {
				contacts = messageModelService.getMyContacts(userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.addObject("contacts", contacts);
		return json;
	}
	
	@RequestMapping(params = "action=getmessages")
	public ModelAndView getMessages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("MessageManagerCtrl-getContacts start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<MessageModel> messageModels = new ArrayList<MessageModel>();
		try {
			Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			Integer contactID = ParamUtils.getIntParameter(request, "contactid", -1);
			if (userid != null && userid > 0 && contactID != null && contactID > 0) {
				messageModels = messageModelService.getMessagesByContact(userid, contactID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		json.addObject("messageModels", messageModels);
		return json;
	}
	
	@RequestMapping(params = "atn=checkmessage")
	public ModelAndView checkMessage(HttpServletRequest request, HttpSession session) {
		logger.debug("MessageManagerCtrl-checkMessage start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
		Integer contactID = ParamUtils.getIntParameter(request, "contactID", -1);
		String messageIDs = ParamUtils.getParameter(request, "messageids");
		messageModelService.checkMessage(userid, contactID, messageIDs);
		logger.debug("MessageManagerCtrl-checkMessage end.");
		return json;
	}
}
