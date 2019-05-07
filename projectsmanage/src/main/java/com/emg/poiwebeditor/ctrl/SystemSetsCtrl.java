package com.emg.poiwebeditor.ctrl;

import java.util.Enumeration;
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

import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.dao.process.ConfigDBModelDao;
import com.emg.poiwebeditor.dao.process.ConfigDefaultModelDao;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.ConfigDefaultModel;
import com.emg.poiwebeditor.pojo.ConfigDefaultModelExample;

@Controller
@RequestMapping("/systemsets.web")
public class SystemSetsCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(SystemSetsCtrl.class);

	@Autowired
	private ConfigDBModelDao configDBModelDao;
	@Autowired
	private ConfigDefaultModelDao configDefaultModelDao;

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
		logger.debug("start");
		try {
			List<ConfigDBModel> configDBModels = configDBModelDao.selectAllConfigDBModels();
			model.addAttribute("configDBModels", configDBModels);

			return "systemsets";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:login.jsp";
		}
	}

	@RequestMapping(params = "atn=setdefaultvalues")
	public ModelAndView setDefaultValues(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("start");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = paramNames.nextElement();
				if (!paramName.startsWith("config_"))
					continue;

				String[] a = paramName.split("_");
				Integer id = Integer.valueOf(a[1]);
				String defaultValue = ParamUtils.getParameter(request, paramName);

				ConfigDefaultModel record = new ConfigDefaultModel();
				record.setId(id);
				record.setDefaultvalue(defaultValue == null || defaultValue.isEmpty() ? "" : defaultValue);
				configDefaultModelDao.updateByPrimaryKeySelective(record );
			}
			json.addObject("ret", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("end");
		return json;
	}

	@RequestMapping(params = "atn=getdefaultvalues")
	public ModelAndView getDefaultValues(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("start");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ConfigDefaultModelExample example = new ConfigDefaultModelExample();
			example.or().andEditableEqualTo(Byte.valueOf("1"));
			List<ConfigDefaultModel> configDefaultModels = configDefaultModelDao.selectByExample(example);
			json.addObject("ret", 1);
			json.addObject("configDefaultModels", configDefaultModels);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("end");
		return json;
	}
}
