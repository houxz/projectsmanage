package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.projectsmanager.LogModelDao;
import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.LogModel;
import com.emg.projectsmanage.service.CommService;
import com.emg.projectsmanage.service.EmapgoAccountService;

@Controller
@RequestMapping("/login.web")
public class LoginCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(LoginCtrl.class);

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@Autowired
	private LogModelDao logModelDao;

	@Autowired
	private UserRoleModelDao userRoleModelDao;

	@Autowired
	private CommService commService;

	@RequestMapping()
	public String login(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("LoginCtrl-login start.");
		try {
			String account = getLoginAccount(session);
			EmployeeModel record = new EmployeeModel();
			record.setUsername(account);
			EmployeeModel user = emapgoAccountService.getOneEmployee(record);
			if (user == null) {
				if (session != null) {
					session.invalidate();
				}
				SecurityContext context = SecurityContextHolder.getContext();
				context.setAuthentication(null);
				SecurityContextHolder.clearContext();
				logger.debug("user : " + account + " deny to login.");
				return "redirect:login.jsp";
			}

			Integer userid = user.getId();

			session.setAttribute(CommonConstants.SESSION_USER_ACC, account);

			session.setAttribute(CommonConstants.SESSION_USER_ID, userid);
			session.setAttribute(CommonConstants.SESSION_USER_NAME, user.getRealname());

			LogModel log = new LogModel();
			log.setType("LOGIN");
			log.setKey(userid.toString());
			log.setValue(account);
			log.setSessionid(session.getId());
			log.setIp(getRemortIP(request));
			logModelDao.log(log);

			List<GrantedAuthority> newAuths = new ArrayList<GrantedAuthority>();
			for (GrantedAuthority ga : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
				newAuths.add(ga);
			}
			List<Map<String, Object>> authlist = userRoleModelDao.getEpleRoles(user.getId());
			for (Map<String, Object> auth : authlist) {
				newAuths.add(new SimpleGrantedAuthority(MapUtils.getString(auth, "rolename")));
			}

			SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), SecurityContextHolder.getContext()
							.getAuthentication().getCredentials(), newAuths));

			session.setAttribute(CommonConstants.SESSION_CURRENTSYSTEMID, CommonConstants.SYSTEM_POIVIDEOEDIT_ID);

			if (hasRole(request, RoleType.ROLE_ADMIN.toString())) {
				logger.debug("LoginCtrl-login end to admin page.");
				return "redirect:usersmanage.web";
			} else if (hasRole(request, RoleType.ROLE_POIVIDEOEDIT.toString())) {
				logger.debug("LoginCtrl-login end to leader page.");
				return "redirect:processesmanage.web";
			} else if (hasRole(request, RoleType.ROLE_WORKER.toString()) || hasRole(request, RoleType.ROLE_CHECKER.toString())) {
				logger.debug("LoginCtrl-login end to worker page.");
				return "redirect:worktasks.web";
			} else {
				logger.debug("LoginCtrl-login end to login page.");
				return "redirect:login.jsp";
			}
		} catch (Exception e) {
			e.printStackTrace();
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
