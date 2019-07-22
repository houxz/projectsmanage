package com.emg.poiwebeditor.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emg.poiwebeditor.client.TaskModelClient;
import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.dao.projectsmanager.LogModelDao;
import com.emg.poiwebeditor.pojo.EmployeeModel;
import com.emg.poiwebeditor.pojo.LogModel;
import com.emg.poiwebeditor.service.EmapgoAccountService;

@Controller
@RequestMapping("/login.web")
public class LoginCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(LoginCtrl.class);

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@Autowired
	private LogModelDao logModelDao;
	
	@Autowired
	private TaskModelClient taskModelClient;

	@RequestMapping()
	public String login(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("START");
		try {
			String account = getLoginAccount(session);
			Integer userid = 0;
			String realname = new String();
			if(hasRole(request, RoleType.ROLE_SUPERADMIN.toString())) {
				userid = RoleType.ROLE_SUPERADMIN.getValue();
				realname = RoleType.ROLE_SUPERADMIN.getDes();
			} else if(hasRole(request, RoleType.ROLE_YANFAADMIN.toString())) {
				userid = RoleType.ROLE_YANFAADMIN.getValue();
				realname = RoleType.ROLE_YANFAADMIN.getDes();
			} else if(hasRole(request, RoleType.ROLE_ADMIN.toString())) {
				userid = RoleType.ROLE_ADMIN.getValue();
				realname = RoleType.ROLE_ADMIN.getDes();
			} else {
				EmployeeModel record = new EmployeeModel();
				record.setUsername(account);
				EmployeeModel user = emapgoAccountService.getOneEmployeeWithCache(record);
				if (user == null) {
					if (session != null) {
						session.invalidate();
					}
					SecurityContext context = SecurityContextHolder.getContext();
					context.setAuthentication(null);
					SecurityContextHolder.clearContext();
					logger.error("user : " + account + " deny to login.");
					return "redirect:login.jsp";
				}
			
				userid = user.getId();
				realname = user.getRealname();
				taskModelClient.updateTaskState(userid, 1, 5);
				taskModelClient.updateCheckTaskState(userid, 1, 7);
			}
			
			session.setAttribute(CommonConstants.SESSION_USER_ACC, account);
			session.setAttribute(CommonConstants.SESSION_USER_ID, userid);
			session.setAttribute(CommonConstants.SESSION_USER_NAME, realname);
			
			LogModel log = new LogModel();
			log.setType("LOGIN");
			log.setKey(userid.toString());
			log.setValue(account);
			log.setSessionid(session.getId());
			log.setIp(getRemortIP(request));
			logModelDao.log(log);

			if (hasRole(request, RoleType.ROLE_ADMIN.toString()) || hasRole(request, RoleType.ROLE_YANFAADMIN.toString()) || hasRole(request, RoleType.ROLE_SUPERADMIN.toString())) {
				logger.debug("LoginCtrl-login end to admin page.");
				return "redirect:usersmanage.web";
			} else if (hasRole(request, RoleType.ROLE_POIVIDEOEDIT.toString())) {
				logger.debug("LoginCtrl-login end to leader page.");
				return "redirect:processesmanage.web";
			} else if (hasRole(request, RoleType.ROLE_WORKER.toString())) {
				logger.debug("LoginCtrl-login end to worker page.");
				return "redirect:edit.web"; //制作任务跳转到制作
			} else if( hasRole(request, RoleType.ROLE_CHECKER.toString())) {
				logger.debug("LoginCtrl-login end to worker page.");
				return "redirect:edit.web";//校正任务跳转到校正页面 暂时没有校正临时跳转到制作
			}else {
				if (session != null) {
					session.invalidate();
				}
				SecurityContext context = SecurityContextHolder.getContext();
				context.setAuthentication(null);
				SecurityContextHolder.clearContext();
				logger.error("user has no power getting in : " + account);
				return "redirect:login.jsp?login_error=2";
			}
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
