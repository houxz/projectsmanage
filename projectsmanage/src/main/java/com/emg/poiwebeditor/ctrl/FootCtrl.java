package com.emg.poiwebeditor.ctrl;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/foot.web")
public class FootCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(FootCtrl.class);

	@Value("${version}")
	private String version;

	@RequestMapping()
	public String foot(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("FootCtrl-foot start.");

		Calendar date = Calendar.getInstance();
		String thisYear = String.valueOf(date.get(Calendar.YEAR));

		model.addAttribute("thisYear", thisYear);
		model.addAttribute("version", version);

		logger.debug("FootCtrl-foot end.");
		return "foot";
	}
}
