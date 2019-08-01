package com.emg.poiwebeditor.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.dao.projectsmanager.UserRoleModelDao;
import com.emg.poiwebeditor.pojo.AuthorityModel;
import com.emg.poiwebeditor.service.EmapgoAccountService;
import com.emg.poiwebeditor.service.SessionService;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	private final static String SUPERADMIN_USERNAME = "superadmin";
	@Value("${superadmin.password}")
	private String SUPERADMIN_PASSWORD = "emapgo1qaz@WSX";
	private final static List<RoleType> SUPERADMIN_ROLETYPE = new ArrayList<RoleType>(Arrays.asList(RoleType.ROLE_SUPERADMIN));

	private final static String YANFAADMIN_USERNAME = "yanfaadmin";
	@Value("${yanfaadmin.password}")
	private String YANFAADMIN_PASSWORD = "5921034";
	private final static List<RoleType> YANFAADMIN_ROLETYPE = new ArrayList<RoleType>(Arrays.asList(RoleType.ROLE_YANFAADMIN));

	private final static String ADMIN_USERNAME = "admin";
	@Value("${admin.password}")
	private String ADMIN_PASSWORD = "emapgo123!@#";
	private final static List<RoleType> ADMIN_ROLETYPE = new ArrayList<RoleType>(Arrays.asList(RoleType.ROLE_ADMIN));

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@Autowired
	private UserRoleModelDao userRoleModelDao;

	@Autowired
	private SessionService sessionService;

	@Override
	public CustomUserDetails loadUserByUsername(String username) {

		//判断是否重复登陆
		if (sessionService.isDuplicateLogin(username)) {
			throw new SessionAuthenticationException(new String());
		}

		if (username.equals(SUPERADMIN_USERNAME)) {
			CustomUserDetails userDetails = new CustomUserDetails();
			userDetails.setUsername(username);
			userDetails.setPassword(DigestUtils.md5DigestAsHex(SUPERADMIN_PASSWORD.getBytes()));
			userDetails.setEnabled(Boolean.valueOf(true));
			List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
			for (RoleType role : SUPERADMIN_ROLETYPE) {
				auths.add(new SimpleGrantedAuthority(role.toString()));
			}
			userDetails.setAuthorities(auths);
			return userDetails;
		}
		if (username.equals(YANFAADMIN_USERNAME)) {
			CustomUserDetails userDetails = new CustomUserDetails();
			userDetails.setUsername(username);
			userDetails.setPassword(DigestUtils.md5DigestAsHex(YANFAADMIN_PASSWORD.getBytes()));
			userDetails.setEnabled(Boolean.valueOf(true));
			List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
			for (RoleType role : YANFAADMIN_ROLETYPE) {
				auths.add(new SimpleGrantedAuthority(role.toString()));
			}
			userDetails.setAuthorities(auths);
			return userDetails;
		}
		if (username.equals(ADMIN_USERNAME)) {
			CustomUserDetails userDetails = new CustomUserDetails();
			userDetails.setUsername(username);
			userDetails.setPassword(DigestUtils.md5DigestAsHex(ADMIN_PASSWORD.getBytes()));
			userDetails.setEnabled(Boolean.valueOf(true));
			List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
			for (RoleType role : ADMIN_ROLETYPE) {
				auths.add(new SimpleGrantedAuthority(role.toString()));
			}
			userDetails.setAuthorities(auths);
			return userDetails;
		}

		AuthorityModel authority = emapgoAccountService.getAuthorityByUsername(username);
		if (authority == null) {
			throw new UsernameNotFoundException(new String());
		}

		CustomUserDetails userDetails = new CustomUserDetails();
		userDetails.setUsername(username);
		userDetails.setPassword(authority.getPassword());
		userDetails.setEnabled(authority.getEnabled().equals(1));

		List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
//		auths.add(new SimpleGrantedAuthority(authority.getRolename()));不再继承人员库的权限，只采用项目管理系统的权限
		Integer userid = authority.getId();
		List<Map<String, Object>> authlist = userRoleModelDao.getEpleRoles(userid);
		for (Map<String, Object> auth : authlist) {
			auths.add(new SimpleGrantedAuthority(MapUtils.getString(auth, "rolename")));
		}
		userDetails.setAuthorities(auths);
		return userDetails;
	}

}