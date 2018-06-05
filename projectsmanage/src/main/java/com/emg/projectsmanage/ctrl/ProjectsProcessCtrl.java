package com.emg.projectsmanage.ctrl;

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

import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsTaskCountModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import com.emg.projectsmanage.pojo.ProjectModelExample.Criteria;
import com.emg.projectsmanage.pojo.ProjectsTaskCountModel;

@Controller
@RequestMapping("/projectsprocess.web")
public class ProjectsProcessCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProjectsProcessCtrl.class);

	@Autowired
	private ProjectsTaskCountModelDao projectsTaskCountDao;

	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;

	@Autowired
	private ProjectModelDao projectModelDao;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProjectsProcessCtrl-openLader start.");
		return "projectsprocess";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProjectsProcessCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String sort = ParamUtils.getParameter(request, "sort", "");
			String order = ParamUtils.getParameter(request, "order", "");
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> map = new HashMap<String, Object>();
			ProjectModelExample example = new ProjectModelExample();
			Criteria criteria = example.or();

			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "projectid":
						map.put("projectid", filterPara.get(key).toString());
						criteria.andIdEqualTo(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "projectname":
						map.put("projectname", "%" + filterPara.get(key).toString() + "%");
						criteria.andNameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "processid":
						map.put("processid", filterPara.get(key).toString());
						criteria.andProcessidEqualTo(Long.valueOf(filterPara.get(key).toString()));
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

			List<ProjectsTaskCountModel> jobs = projectsTaskCountDao.getProjectsProgress(map);
			int count = projectModelDao.countByExample(example);

			json.addObject("rows", jobs);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("ProjectsProcessCtrl-pages end.");
		return json;

	}
}
