package com.emg.projectsmanage.auth;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.emg.projectsmanage.dao.projectsmanager.LogModelDao;
import com.emg.projectsmanage.pojo.LogModel;

@Component
public class CustomerAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = Logger.getLogger(CustomerAuthenticationProvider.class);

	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private LogModelDao logModelDao;

	@Override
	public Authentication authenticate(Authentication authentication) {
		Boolean loginRet = false;
		String username = new String();
		String password = new String();
		try {
			username = authentication.getName();
			password = authentication.getCredentials().toString();

			logger.debug(String.format("User %s try login with password : %s", username, password));

			CustomUserDetails userDetails = this.customUserDetailsService.loadUserByUsername(authentication.getName());

			if (userDetails == null) {
				loginRet = false;
				logger.error("User not exist: " + username);
			} else {

				if (DigestUtils.md5DigestAsHex(password.getBytes()).equals(userDetails.getPassword())) {
					loginRet = true;
					return new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(),
							userDetails.getAuthorities());
				} else {
					loginRet = false;
					logger.error("Wrong Password: " + password);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			LogModel log = new LogModel();
			log.setType("TRYLOGIN");
			log.setKey(username);
			log.setValue(password);
			log.setSessionid(loginRet.toString());
			logModelDao.log(log);
		}

		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

	public void setCustomUserDetailsService(CustomUserDetailsService customUserDetailsService) {
		this.customUserDetailsService = customUserDetailsService;
	}

}
