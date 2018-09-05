package com.emg.projectsmanage.auth;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.config.MenuConfig;

public class AuthMonitorInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private MenuConfig menuConfig;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Boolean ret = false;
		
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String url = requestUri.substring(contextPath.length());
		
		Set<RoleType> auths = new HashSet<RoleType>();
		for (GrantedAuthority ga : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			auths.add(RoleType.valueOf(ga.getAuthority()));
		}

		for (MenuAuthModel menuAuthModel : menuConfig.getMenus()) {
			if(menuAuthModel.getEnabled() && menuAuthModel.getUrl().equalsIgnoreCase(url)) {
				Set<RoleType> result = new HashSet<RoleType>();
				Set<RoleType> roleSet = menuAuthModel.getRoleSet();
				result.clear();
		        result.addAll(roleSet);
		        result.retainAll(auths);
		        if(result.size() > 0) {
		        	ret = true;
		        	break;
		        }
			}
		}

		if (!ret) {
			request.getRequestDispatcher("/exception/accessDenied.web").forward(request, response); 
			return false;
		} else
			return true;
	}
}
