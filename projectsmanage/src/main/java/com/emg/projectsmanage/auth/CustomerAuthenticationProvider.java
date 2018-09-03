package com.emg.projectsmanage.auth;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
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
		String msg = new String();
		String username = new String();
		String password = new String();
		try {
			username = authentication.getName();
			password = authentication.getCredentials().toString();

			logger.debug(String.format("User %s try login with password : %s", username, password));

			CustomUserDetails userDetails = this.customUserDetailsService.loadUserByUsername(authentication.getName());

			if (DigestUtils.md5DigestAsHex(password.getBytes()).equals(userDetails.getPassword())) {
				loginRet = true;
				return new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(),
						userDetails.getAuthorities());
			} else {
				loginRet = false;
				msg = String.format("Wrong Password: %s", password);
				logger.error(msg);
			}
		} catch (UsernameNotFoundException e) {
			loginRet = false;
			msg = String.format("User not exist: %s", username);
			logger.error(msg);
		} catch (SessionAuthenticationException e) {
			loginRet = false;
			msg = String.format("User duplicate login: %s", username);
			logger.error(msg);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (!loginRet) {
				LogModel log = new LogModel();
				log.setType("TRYLOGIN");
				log.setKey(username);
				log.setValue(password);
				log.setSessionid(msg);
				logModelDao.log(log);
			}
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
