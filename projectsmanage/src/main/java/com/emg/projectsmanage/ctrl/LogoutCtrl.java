package com.emg.projectsmanage.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.dao.projectsmanager.LogModelDao;
import com.emg.projectsmanage.pojo.LogModel;

@Controller
@RequestMapping("/logout.web")
public class LogoutCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(LogoutCtrl.class);
	
	@Autowired
	private LogModelDao logModelDao;

	@RequestMapping()
	public String logout(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("LOGOUT");
		try {
			String account = getLoginAccount(session);
			Integer userid = session.getAttribute(CommonConstants.SESSION_USER_ID) == null ? 0 : Integer.valueOf(session.getAttribute(CommonConstants.SESSION_USER_ID).toString());
			
			LogModel log = new LogModel();
			log.setType("LOGOUT");
			log.setKey(userid.toString());
			log.setValue(account);
			log.setSessionid(session.getId());
			log.setIp(getRemortIP(request));
			logModelDao.log(log);
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(request, response, auth);
			}
			return "redirect:login.jsp";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:login.jsp";
		}
	}
	
	private String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

}
