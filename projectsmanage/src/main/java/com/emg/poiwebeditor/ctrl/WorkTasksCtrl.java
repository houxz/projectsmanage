package com.emg.poiwebeditor.ctrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.dao.process.WorkTasksModelDao;
import com.emg.poiwebeditor.pojo.WorkTasksModel;

@Controller
@RequestMapping("/worktasks.web")
public class WorkTasksCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(WorkTasksCtrl.class);

	@Autowired
	private WorkTasksModelDao workTasksModelDao;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("WorkTasksCtrl-openLader start.");
		
		model.addAttribute("processTypes", ProcessType.toJsonStr());
		
		return "worktasks";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("WorkTasksCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String sort = ParamUtils.getParameter(request, "sort", "");
			String order = ParamUtils.getParameter(request, "order", "");
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> map = new HashMap<String, Object>();
			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "projectid":
						map.put("projectid", filterPara.get(key).toString());
						break;
					case "username":
						map.put("username", filterPara.get(key).toString());
						break;
					case "roleid":
						map.put("roleid", filterPara.get(key).toString());
						break;
					case "processname":
						map.put("processname", "%" + filterPara.get(key).toString() + "%");
						break;
					case "processid":
						map.put("processid", filterPara.get(key).toString());
						break;
					case "processtype":
						map.put("processtype", filterPara.get(key).toString());
						break;
					case "time":
						map.put("time", filterPara.get(key).toString());
						break;
					default:
						break;
					}
				}
			}
			if (!sort.isEmpty()) {
				map.put("orderby", sort + " " + order);
			}
			if (limit.compareTo(0) > 0)
				map.put("limit", limit);
			if (offset.compareTo(0) > 0)
				map.put("offset", offset);

			List<WorkTasksModel> projectsTaskCountModels = workTasksModelDao.getWorkTasks(map);
			int count = workTasksModelDao.countWorkTasks(map);

			json.addObject("rows", projectsTaskCountModels);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("WorkTasksCtrl-pages end.");
		return json;
	}
}
