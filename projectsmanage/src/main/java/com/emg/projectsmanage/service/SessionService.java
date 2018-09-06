package com.emg.projectsmanage.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.emg.projectsmanage.auth.CustomUserDetails;
import com.emg.projectsmanage.dao.projectsmanager.LogModelDao;
import com.emg.projectsmanage.pojo.LogModel;

@Service
public class SessionService {

	private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

	@Autowired
	private SessionRegistry sessionRegistry;
	
	@Autowired
	private LogModelDao logModelDao;

	public Boolean isDuplicateLogin(String username) {
		Boolean ret = false;
		try {
			List<Object> o = sessionRegistry.getAllPrincipals();
			for (Object principal : o) {
				if (principal instanceof CustomUserDetails
						&& (username.equals(((CustomUserDetails) principal).getUsername()))) {
					ret = true;
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}

	public Boolean KickOutUser(String username) {
		Boolean ret = false;
		try {
			List<Object> o = sessionRegistry.getAllPrincipals();
			logger.debug(String.format("%d user login :", o.size()));
			for (Object principal : o) {
				if (principal instanceof CustomUserDetails) {
					final CustomUserDetails loggedUser = (CustomUserDetails) principal;
					logger.debug(loggedUser.getUsername());
					if (loggedUser != null && username.equals(loggedUser.getUsername())) {
						List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false);
						if (null != sessionsInfo && sessionsInfo.size() > 0) {
							for (SessionInformation sessionInformation : sessionsInfo) {
								sessionInformation.expireNow();
							}
							logger.debug("Kick out user :" + username);
							LogModel log = new LogModel();
							log.setType("KICKOUT");
							log.setKey("by username");
							log.setValue(username);
							logModelDao.log(log);
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ret;
	}
	
}
