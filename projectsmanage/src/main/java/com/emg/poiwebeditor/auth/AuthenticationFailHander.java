package com.emg.poiwebeditor.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailHander extends SimpleUrlAuthenticationFailureHandler {
	
	private static final Logger logger = Logger.getLogger(AuthenticationFailHander.class);

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) {
		String strUrl = request.getContextPath() + "/login.jsp?login_error=%d";
		Integer errorCode = -1;
		try {
			if (exception instanceof UsernameNotFoundException) {
				errorCode = 1;
			} else if (exception instanceof BadCredentialsException) {
				errorCode = 2;
			} else if (exception instanceof SessionAuthenticationException) {
				errorCode = 3;
			} else if (exception instanceof DisabledException) {
				errorCode = 4;
			} else {
				logger.error(exception.getMessage(), exception);
			}
			response.sendRedirect(String.format(strUrl, errorCode));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
