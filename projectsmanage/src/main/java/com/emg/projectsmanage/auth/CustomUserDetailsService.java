package com.emg.projectsmanage.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.pojo.AuthorityModel;
import com.emg.projectsmanage.service.EmapgoAccountService;

@Component
public class CustomUserDetailsService implements UserDetailsService {
	
	private static final Logger logger = Logger.getLogger(CustomUserDetailsService.class);

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@Autowired
	private UserRoleModelDao userRoleModelDao;

	@Override
	public CustomUserDetails loadUserByUsername(String username) {
		CustomUserDetails userDetails = null;
		try {
			AuthorityModel authority = emapgoAccountService.getAuthorityByUsername(username);
			if(authority == null)	return userDetails;
			
			userDetails = new CustomUserDetails();
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
		} catch(UsernameNotFoundException e) {
			logger.error(e.getMessage(), e);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return userDetails;
	}

}