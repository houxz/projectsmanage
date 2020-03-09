package com.emg.poiwebeditor.ctrl;

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
import org.springframework.web.bind.annotation.RequestMethod;

import com.emg.poiwebeditor.cache.ProductTask;
import com.emg.poiwebeditor.client.TaskClient;
import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.TypeEnum;
import com.emg.poiwebeditor.dao.projectsmanager.LogModelDao;
import com.emg.poiwebeditor.pojo.LogModel;

@Controller
@RequestMapping("/logout.web")
public class LogoutCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(LogoutCtrl.class);
	
	@Autowired
	private ProductTask productTask;
	
	@Autowired
	private LogModelDao logModelDao;
	
	@Autowired
	private TaskClient taskClient;

	@RequestMapping(method = RequestMethod.GET)
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
			if (auth != null ) {
				if (userid > 0) {
					productTask.removeUserTask(userid, ProductTask.TYPE_EDIT_QUENE, TypeEnum.edit_using);
					productTask.removeCurrentUserTask(userid, ProductTask.TYPE_EDIT_MAKING);
					
					productTask.removeUserTask(userid, ProductTask.TYPE_CHECK_QUENE, TypeEnum.check_using);
					productTask.removeCurrentUserTask(userid, ProductTask.TYPE_CHECK_MAKING);
					
					productTask.removeUserTask(userid, ProductTask.TYPE_POLYGONEDIT_QUENE, TypeEnum.polygon_edit_using);
					productTask.removeCurrentUserTask(userid, ProductTask.TYPE_POLYGONEDIT_MAKING);
					
					productTask.removeUserTask(userid, ProductTask.TYPE_POLYGONCHECK_QUENE, TypeEnum.polygon_check_using);
					productTask.removeCurrentUserTask(userid, ProductTask.TYPE_POLYGONCHECK_MAKING);
					Thread.sleep(500);
					// taskClient.updateTaskState(userid, 1, 5);
					//
					taskClient.initTaskState_logout(userid, TypeEnum.edit_using);
					taskClient.initTaskState_logout(userid, TypeEnum.check_using);
					taskClient.updateTaskState_logout(userid, TypeEnum.edit_using, TypeEnum.edit_used);
					taskClient.updateTaskState_logout(userid, TypeEnum.check_using, TypeEnum.check_used);
					
					taskClient.initTaskState_logout(userid, TypeEnum.polygon_edit_using);
					taskClient.initTaskState_logout(userid, TypeEnum.polygon_check_using);
					
					logger.warn("logout user is :" + userid);
				}
				
				new SecurityContextLogoutHandler().logout(request, response, auth);
				
			}
			
			return "redirect:login.jsp?logout=1";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:/login.jsp?logout=-1";
		}
	}
	
	@RequestMapping(params = "action=logoutIns", method = RequestMethod.POST)
	public String logoutIns(Model model, final HttpSession session, final HttpServletRequest request, final HttpServletResponse response) {
		logger.debug("LOGOUTINS");
		try {
			session.setAttribute("cancelflag", Boolean.FALSE);
			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {
					try {
						Thread.sleep(10000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					doLogout(session, request, response);
				}			
			});
			thread.start();
			
			return "redirect:login.jsp";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:login.jsp";
		}
	}
	
	@RequestMapping(params = "action=cancelLogout", method = RequestMethod.POST)
	public String cancelLogout(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("CANCELLOGOUT");
		try {
			session.setAttribute("cancelflag", Boolean.TRUE);
			return "redirect:login.jsp";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:login.jsp";
		}
	}
	
	private void doLogout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		try {
			Boolean cancelflag = session.getAttribute("cancelflag") == null ? false : Boolean.valueOf(session.getAttribute("cancelflag").toString());
			logger.debug("DOLOGOUT with cancelflag: " + cancelflag);
			if(!cancelflag) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (auth != null) {
					new SecurityContextLogoutHandler().logout(request, response, auth);
				}
				
				String account = getLoginAccount(session);
				Integer userid = session.getAttribute(CommonConstants.SESSION_USER_ID) == null ? 0 : Integer.valueOf(session.getAttribute(CommonConstants.SESSION_USER_ID).toString());
				
				LogModel log = new LogModel();
				log.setType("LOGOUTINS");
				log.setKey(userid.toString());
				log.setValue(account);
				log.setSessionid(session.getId());
				log.setIp(getRemortIP(request));
				logModelDao.log(log);
				
			}
		}catch(Exception e) {}
	}
	
	
	private String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

}
