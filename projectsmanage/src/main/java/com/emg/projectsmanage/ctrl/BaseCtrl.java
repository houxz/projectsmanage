package com.emg.projectsmanage.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

public class BaseCtrl {

	protected String getLoginAccount(HttpSession session) {
		SecurityContext sc = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if (sc != null) {
			Authentication au = sc.getAuthentication();
			String account = au.getName();
			return account;
		} else {
			return "";
		}
	}

	protected boolean hasRole(HttpServletRequest request, String role) {
		SecurityContextHolderAwareRequestWrapper sch = new SecurityContextHolderAwareRequestWrapper(request, "");
		return sch.isUserInRole(role);
	}
}
