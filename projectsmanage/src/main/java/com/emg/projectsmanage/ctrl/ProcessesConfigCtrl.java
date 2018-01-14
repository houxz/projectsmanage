package com.emg.projectsmanage.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsTaskCountModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;

@Controller
@RequestMapping("/processesconfig.web")
public class ProcessesConfigCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProcessesConfigCtrl.class);

	@Autowired
	private ProjectsTaskCountModelDao projectsTaskCountDao;

	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;

	@Autowired
	private ProjectModelDao projectModelDao;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesConfigCtrl-openLader start.");
		return "processesconfig";
	}
}
