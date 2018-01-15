package com.emg.projectsmanage.ctrl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;

@Controller
@RequestMapping("/processesmanage.web")
public class ProcessesManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProcessesManageCtrl.class);
	
	@Autowired
	private ProcessModelDao processModelDao;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesConfigCtrl-openLader start.");
		model.addAttribute("processStates", ProcessState.toJsonStr());
		return "processesmanage";
	}
	
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");
			
			ProcessModelExample example = new ProcessModelExample();
			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);
			List<ProcessModel> projectsTaskCountModels = processModelDao.selectByExample(example );
			int count = processModelDao.countByExample(example);

			json.addObject("rows", projectsTaskCountModels);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}

		logger.debug("ProcessesManageCtrl-pages end.");
		return json;
	}
}
