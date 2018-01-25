package com.emg.projectsmanage.ctrl;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

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
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.pojo.ProcessConfigModel;

@Controller
@RequestMapping("/systemsets.web")
public class SystemSetsCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(SystemSetsCtrl.class);

	@Autowired
	private ProcessConfigModelDao processConfigModelDao;

	/**
	 * 系统配置页面
	 * 
	 * @param model
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("SystemSetsCtrl-openLader start.");
		try {
			List<Map<String, Object>> configDBModels = processConfigModelDao.selectAllConfigDBModels();
			model.addAttribute("configDBModels", configDBModels);

			return "systemsets";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:login.jsp";
		}
	}

	@RequestMapping(params = "atn=setdefaultvalues")
	public ModelAndView setDefaultValues(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("SystemSetsCtrl-setDefaultValues start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = paramNames.nextElement();
				if (!paramName.startsWith("config_"))
					continue;

				String[] a = paramName.split("_");
				Integer configid = Integer.valueOf(a[2]);
				String defaultValue = ParamUtils.getParameter(request, paramName);

				ProcessConfigModel config = new ProcessConfigModel();
				config.setId(configid);
				config.setDefaultValue(defaultValue);
				
				processConfigModelDao.updateDefaultValueSelective(config);
			}
			json.addObject("ret", 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}

		logger.debug("SystemSetsCtrl-setDefaultValues end.");
		return json;
	}

	@RequestMapping(params = "atn=getdefaultvalues")
	public ModelAndView getDefaultValues(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("SystemSetsCtrl-setDefaultValues start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<Map<String, Object>> processConfigs = processConfigModelDao.selectAllProcessConfigModels();
			json.addObject("ret", 1);
			json.addObject("configs", processConfigs);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}

		logger.debug("SystemSetsCtrl-setDefaultValues end.");
		return json;
	}
}
