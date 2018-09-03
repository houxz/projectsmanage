package com.emg.projectsmanage.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.pojo.AuthorityModel;
import com.emg.projectsmanage.service.EmapgoAccountService;
import com.emg.projectsmanage.service.SessionService;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@Autowired
	private UserRoleModelDao userRoleModelDao;

	@Autowired
	private SessionService sessionService;

	@Override
	public CustomUserDetails loadUserByUsername(String username) {
		AuthorityModel authority = emapgoAccountService.getAuthorityByUsername(username);
		if (authority == null) {
			throw new UsernameNotFoundException(new String());
		}
		if(sessionService.isDuplicateLogin(username)) {
			throw new SessionAuthenticationException(new String());
		}

		CustomUserDetails userDetails = new CustomUserDetails();
		userDetails.setUsername(username);
		userDetails.setPassword(authority.getPassword());
		userDetails.setEnabled(authority.getEnabled().equals(1));

		List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
		auths.add(new SimpleGrantedAuthority(authority.getRolename()));
		Integer userid = authority.getId();
		List<Map<String, Object>> authlist = userRoleModelDao.getEpleRoles(userid);
		for (Map<String, Object> auth : authlist) {
			auths.add(new SimpleGrantedAuthority(MapUtils.getString(auth, "rolename")));
		}
		userDetails.setAuthorities(auths);
		return userDetails;
	}

}