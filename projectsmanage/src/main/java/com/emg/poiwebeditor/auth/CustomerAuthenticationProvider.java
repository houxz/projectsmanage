package com.emg.poiwebeditor.auth;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.emg.poiwebeditor.dao.projectsmanager.LogModelDao;
import com.emg.poiwebeditor.pojo.LogModel;

@Component
public class CustomerAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = Logger.getLogger(CustomerAuthenticationProvider.class);

	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private LogModelDao logModelDao;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String msg = new String();
		String username = new String();
		String password = new String();
		try {
			username = authentication.getName();
			password = authentication.getCredentials().toString();

			logger.debug(String.format("User %s try login with password : %s", username, password));

			CustomUserDetails userDetails = this.customUserDetailsService.loadUserByUsername(authentication.getName());
			
			if (userDetails.getAuthorities().size() <= 0) {
				throw new DisabledException(new String());
			}

			if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(userDetails.getPassword())) {
				throw new BadCredentialsException(new String());
			}
			
			return new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(),
					userDetails.getAuthorities());
		} catch (UsernameNotFoundException e) {
			msg = String.format("User not exist: %s", username);
			logger.error(msg);
			LogModel log = new LogModel();
			log.setType("TRYLOGIN");
			log.setKey(username);
			log.setValue(password);
			log.setSessionid(msg);
			logModelDao.log(log);
			throw e;
		} catch (BadCredentialsException e) {
			msg = String.format("User %s Wrong Password: %s", username, password);
			logger.error(msg);
			LogModel log = new LogModel();
			log.setType("TRYLOGIN");
			log.setKey(username);
			log.setValue(password);
			log.setSessionid(msg);
			logModelDao.log(log);
			throw e;
		} catch (SessionAuthenticationException e) {
			msg = String.format("User duplicate login: %s", username);
			logger.error(msg);
			LogModel log = new LogModel();
			log.setType("TRYLOGIN");
			log.setKey(username);
			log.setValue(password);
			log.setSessionid(msg);
			logModelDao.log(log);
			throw e;
		} catch (DisabledException e) {
			msg = String.format("User has no power getting in: %s", username);
			logger.error(msg);
			LogModel log = new LogModel();
			log.setType("TRYLOGIN");
			log.setKey(username);
			log.setValue(password);
			log.setSessionid(msg);
			logModelDao.log(log);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
