package com.emg.projectsmanage.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/selfcheck.web")
public class SelfCheckCtrl {
	
	private static final Logger logger = LoggerFactory.getLogger(SelfCheckCtrl.class);

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String openLader() {
		logger.debug("START");
		
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		logger.debug("END");
		return Boolean.TRUE.toString();
	}
}
