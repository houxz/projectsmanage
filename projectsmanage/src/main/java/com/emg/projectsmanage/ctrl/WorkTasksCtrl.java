package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsTaskCountModelDao;
import com.emg.projectsmanage.pojo.ProjectsTaskCountModel;
import com.emg.projectsmanage.pojo.ProjectsUserModel;
import com.emg.projectsmanage.service.EmapgoAccountService;

@Controller
@RequestMapping("/worktasks.web")
public class WorkTasksCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(WorkTasksCtrl.class);

	@Autowired
	private ProjectsTaskCountModelDao projectsTaskCountDao;

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("WorkTasksCtrl-openLader start.");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer systemid = (Integer) session.getAttribute(CommonConstants.SESSION_CURRENTSYSTEMID);
		map.put("systemid", systemid);
		if (!hasRole(request, RoleType.ROLE_POIVIDEOEDIT.toString())) {
			Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			map.put("userid", userid);
		}
		List<ProjectsTaskCountModel> projectsTaskCountModels = projectsTaskCountDao.groupProjectsProgressByUseridAndRoleid(map);
		List<ProjectsUserModel> users = new ArrayList<ProjectsUserModel>();
		List<ProjectsUserModel> roles = new ArrayList<ProjectsUserModel>();

		for (ProjectsTaskCountModel projectsTaskCountModel : projectsTaskCountModels) {
			Integer userid = projectsTaskCountModel.getUserid();
			String username = projectsTaskCountModel.getUsername();
			ProjectsUserModel user = new ProjectsUserModel();
			user.setUserid(userid);
			user.setUsername(username);
			Integer roleid = projectsTaskCountModel.getRoleid();
			String rolename = projectsTaskCountModel.getRolename();
			ProjectsUserModel role = new ProjectsUserModel();
			role.setRoleid(roleid);
			role.setRolename(rolename);
			if (!users.contains(user))
				users.add(user);
			if (!roles.contains(role))
				roles.add(role);
		}
		model.addAttribute("users", users.size() > 0 ? JSONArray.fromObject(users).toString() : "({})");
		model.addAttribute("roles", roles.size() > 0 ? JSONArray.fromObject(roles).toString() : "({})");
		return "worktasks";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("WorkTasksCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer systemid = (Integer) session.getAttribute(CommonConstants.SESSION_CURRENTSYSTEMID);
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String sort = ParamUtils.getParameter(request, "sort", "");
			String order = ParamUtils.getParameter(request, "order", "");
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("systemid", systemid);
			if (!hasRole(request, RoleType.ROLE_POIVIDEOEDIT.toString())) {
				Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
				map.put("userid", userid);
			}
			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "projectid":
						map.put("projectid", filterPara.get(key).toString());
						break;
					case "userid":
						map.put("userid", filterPara.get(key).toString());
						break;
					case "roleid":
						map.put("roleid", filterPara.get(key).toString());
						break;
					case "projectname":
						map.put("projectname", "%" + filterPara.get(key).toString() + "%");
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

			List<ProjectsTaskCountModel> projectsTaskCountModels = projectsTaskCountDao.getProjectsProgressByUserid(map);
			int count = projectsTaskCountDao.countProjectsProgressByUserid(map);

			json.addObject("rows", projectsTaskCountModels);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}

		logger.debug("WorkTasksCtrl-pages end.");
		return json;
	}
}
