package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
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

import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import com.emg.projectsmanage.pojo.ProjectModelExample.Criteria;
import com.emg.projectsmanage.pojo.ProjectsUserModel;
import com.emg.projectsmanage.pojo.RoleModel;
import com.emg.projectsmanage.service.ProjectManagerRoleService;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;

@Controller
@RequestMapping("/videoproject.web")
public class PoiVideoProjectsCtrl {

	private static final Logger logger = LoggerFactory.getLogger(PoiVideoProjectsCtrl.class);

	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;

	@Autowired
	private ProjectModelDao projectModelDao;

	@Autowired
	private ProjectManagerRoleService projectManagerRoleService;

	@SuppressWarnings("unchecked")
	@RequestMapping
	public ModelAndView getProjects(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("-getProjects start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			// 分页
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;

			RoleModel workRole = projectManagerRoleService.getWorkerRole();
			RoleModel checkRole = projectManagerRoleService.getCheckerRole();

			ProjectModelExample example = new ProjectModelExample();
			Criteria criteria = example.or();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				ProjectsUserModel pu = new ProjectsUserModel();
				List<ProjectsUserModel> rtemp = new ArrayList<ProjectsUserModel>();
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						criteria.andIdEqualTo(Long.valueOf(filterPara.get("id").toString()));
						break;
					case "name":
						criteria.andNameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "workers":
						List<Long> workPids = new ArrayList<Long>();
						pu.setRoleid(workRole.getId());
						pu.setUserid(Integer.valueOf(filterPara.get("workers").toString()));
						rtemp = projectsUserModelDao.queryProjectUsers(pu);
						for (ProjectsUserModel _rtemp : rtemp) {
							workPids.add(Long.valueOf(_rtemp.getPid()));
						}
						if (workPids.size() > 0) {
							criteria.andIdIn(workPids);
						}
						break;
					case "checkers":
						List<Long> checkPids = new ArrayList<Long>();
						pu.setRoleid(checkRole.getId());
						pu.setUserid(Integer.valueOf(filterPara.get("checkers").toString()));
						rtemp = projectsUserModelDao.queryProjectUsers(pu);
						for (ProjectsUserModel _rtemp : rtemp) {
							checkPids.add(Long.valueOf(_rtemp.getPid()));
						}
						if (checkPids.size() > 0) {
							criteria.andIdIn(checkPids);
						}
						break;
					case "overstate":
						criteria.andOverstateEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "priority":
						criteria.andPriorityEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "pdifficulty":
						criteria.andPdifficultyEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "owner":
						criteria.andOwnerEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					default:
						break;
					}
				}
			}
			example.setOrderByClause("`priority` desc, `id`");
			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);
			List<ProjectModel> list = projectModelDao.selectByExample(example);
			if (list != null && list.size() > 0)
				getPUsersByPro(list, workRole, checkRole);

			Integer c = projectModelDao.countByExample(example);
			json.addObject("rows", list);
			json.addObject("total", c);
			json.addObject("result", 1);
		} catch (Exception e) {
			json.addObject("result", 0);
			logger.error(e.getMessage());
		}
		logger.debug("-getProjects end.");
		return json;
	}

	private void getPUsersByPro(List<ProjectModel> projectModels, RoleModel workerRole, RoleModel checkerRole) {
		List<String> projectsIDs = new ArrayList<String>();
		for (ProjectModel p : projectModels) {
			projectsIDs.add(p.getId().toString());
		}
		List<ProjectsUserModel> users = projectsUserModelDao.queryProjectUsersByPids(projectsIDs);

		for (ProjectModel projectModel : projectModels) {
			List<ProjectsUserModel> wusers = new ArrayList<ProjectsUserModel>();
			List<ProjectsUserModel> cusers = new ArrayList<ProjectsUserModel>();
			StringBuilder workers = new StringBuilder("");
			StringBuilder checkers = new StringBuilder("");
			for (ProjectsUserModel user : users) {
				if (user.getPid().equals(projectModel.getId().toString())) {
					Integer rid = user.getRoleid();
					if (rid == workerRole.getId()) {
						workers.append(user.getUserid()).append(",");
						wusers.add(user);
					} else if (rid == checkerRole.getId()) {
						checkers.append(user.getUserid()).append(",");
						cusers.add(user);
					}
				}
			}
			if (workers.length() > 0) {
				projectModel.setWorkers(workers.delete(workers.length() - 1, workers.length()).toString());
			}
			if (checkers.length() > 0) {
				projectModel.setCheckers(checkers.delete(checkers.length() - 1, checkers.length()).toString());
			}
			projectModel.setCheckusers(cusers);
			projectModel.setWorkusers(wusers);
		}
	}
}
