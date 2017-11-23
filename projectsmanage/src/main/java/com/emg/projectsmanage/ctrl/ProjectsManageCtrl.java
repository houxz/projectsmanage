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

import com.emg.projectsmanage.pojo.MetadataModel;
import com.emg.projectsmanage.pojo.ProjectsUserModel;
import com.emg.projectsmanage.pojo.RoleModel;
import com.emg.projectsmanage.pojo.UserRoleModel;
import com.emg.projectsmanage.service.CommService;
import com.emg.projectsmanage.service.EmapgoAccountService;
import com.emg.projectsmanage.service.ProjectManagerRoleService;
import com.emg.projectsmanage.service.UserAndRoleService;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.EmployeeSkillModel;
import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.common.JobStatus;
import com.emg.projectsmanage.common.OwnerStatus;
import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.PriorityLevel;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.ctrl.BaseCtrl;
import com.emg.projectsmanage.dao.projectsmanager.EmployeeSkillModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;

@Controller
@RequestMapping("/projectsmanage.web")
public class ProjectsManageCtrl extends BaseCtrl {

	@Autowired
	private EmapgoAccountService emapgoAccountService;
	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;
	@Autowired
	private UserRoleModelDao userRoleModelDao;
	@Autowired
	private ProjectManagerRoleService projectManagerRoleService;
	@Autowired
	private EmployeeSkillModelDao employeeSkillModelDao;
	@Autowired
	private UserAndRoleService userAndRoleService;
	@Autowired
	private CommService commService;

	private static final Logger logger = LoggerFactory.getLogger(ProjectsManageCtrl.class);

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProjectsManageCtrl-openLader start.");
		
		Integer systemid = CommonConstants.SYSTEM_POIVIDEOEDIT_ID;
		try {
			if (session.getAttribute(CommonConstants.SESSION_CURRENTSYSTEMID) != null)
				systemid = (Integer) session.getAttribute(CommonConstants.SESSION_CURRENTSYSTEMID);
		} catch (Exception e) {
			systemid = CommonConstants.SYSTEM_POIVIDEOEDIT_ID;
		}

		try {
			if (!hasRole(request, RoleType.ROLE_POIVIDEOEDIT.toString())) {
				return "redirect:login.jsp";
			}
			Integer uid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			if (!userAndRoleService.isUserHasRole(uid, RoleType.ROLE_POIVIDEOEDIT.toString())) {
				return "redirect:login.jsp";
			}
			
			List<MetadataModel> skillLevels = commService.getSkillLevels();
			List<MetadataModel> difficuties = commService.getDifficuties();

			JSONArray jsonArraySkill = new JSONArray();
			for(MetadataModel skillLevel : skillLevels) {
				jsonArraySkill.add(skillLevel.getValue().intValue(), skillLevel.getDesc());
			}
			model.addAttribute("skillLevels", jsonArraySkill.toString());
			
			JSONArray jsonArrayDifficuties = new JSONArray();
			for(MetadataModel difficuty : difficuties) {
				jsonArrayDifficuties.add(difficuty.getValue().intValue(), difficuty.getDesc());
			}
			model.addAttribute("difficuties", jsonArrayDifficuties.toString());
			
			model.addAttribute("jobstatus", JobStatus.toJsonStr());
			model.addAttribute("priorityLevels", PriorityLevel.toJsonStr());
			model.addAttribute("ownerLevels", OwnerStatus.toJsonStr());

			List<UserRoleModel> allWorkersList = getEmployAndRole(RoleType.ROLE_WORKER.toString(), systemid);
			List<UserRoleModel> allCheckersList = getEmployAndRole(RoleType.ROLE_CHECKER.toString(), systemid);
			model.addAttribute("allWorkersList", allWorkersList);
			model.addAttribute("allCheckersList", allCheckersList);

			ProjectsUserModel para = new ProjectsUserModel();
			para.setSystemid(systemid);
			List<ProjectsUserModel> users = projectsUserModelDao.getDistinctWorkers(para);

			List<ProjectsUserModel> workers = new ArrayList<ProjectsUserModel>();
			List<ProjectsUserModel> checkers = new ArrayList<ProjectsUserModel>();

			RoleModel workRole = projectManagerRoleService.getWorkerRole();
			RoleModel checkRole = projectManagerRoleService.getCheckerRole();
			for (ProjectsUserModel u : users) {
				if (u.getRoleid() == workRole.getId().intValue()) {
					workers.add(u);
				} else if (u.getRoleid() == checkRole.getId().intValue()) {
					checkers.add(u);
				}
			}
			if (workers.size() > 0) {
				JSONArray jsonArrayworker = JSONArray.fromObject(workers);
				model.addAttribute("workers", jsonArrayworker.toString());
			} else {
				model.addAttribute("workers", "({})");
			}
			if (checkers.size() > 0) {
				JSONArray jsonArraychecker = JSONArray.fromObject(checkers);
				model.addAttribute("checkers", jsonArraychecker.toString());
			} else {
				model.addAttribute("checkers", "({})");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		logger.debug("ProjectsManageCtrl-openLader end.");
		return "projectsmanage";
	}

	/**
	 * 根据用户角色获取用户的所有信息，包括用户名，用户技能
	 * 
	 * @param rolename
	 * @param systemid
	 * @return
	 */
	private List<UserRoleModel> getEmployAndRole(String rolename, Integer systemid) {
		RoleModel wrole = new RoleModel();
		if (rolename.equals(RoleType.ROLE_WORKER.toString())) {
			wrole = projectManagerRoleService.getWorkerRole();
		} else if (rolename.equals(RoleType.ROLE_CHECKER.toString())) {
			wrole = projectManagerRoleService.getCheckerRole();
		}
		List<MetadataModel> skillLevels = commService.getSkillLevels();

		UserRoleModel wrparam = new UserRoleModel();
		wrparam.setRoleid(wrole.getId());
		List<UserRoleModel> workers = userRoleModelDao.query(wrparam);
		List<Integer> ids = new ArrayList<Integer>();
		for (UserRoleModel worker : workers) {
			Integer id = worker.getUserid();
			ids.add(id);
		}
		List<EmployeeModel> es = emapgoAccountService.getEmployeeByIDS(ids);
		EmployeeSkillModel emparam = new EmployeeSkillModel();
		emparam.setRoleid(wrole.getId());
		emparam.setSystemid(systemid);
		List<EmployeeSkillModel> eds = employeeSkillModelDao.queryEmployeeSkills(emparam);
		for (UserRoleModel r : workers) {
			r.setRolename(wrole.getName());
			for (int i = 0; i < es.size(); i++) {
				EmployeeModel e = es.get(i);
				if (e.getId().compareTo(r.getUserid()) == 0) {
					r.setUsername(e.getRealname());
					break;
				}
			}
			for (int i = 0; i < eds.size(); i++) {
				EmployeeSkillModel e = eds.get(i);
				if (e.getUserid().compareTo(r.getUserid()) == 0) {
					r.setSkilllevel(e.getSkilllevel());
					for (int j = 0; j < skillLevels.size(); j++) {
						if (e.getSkilllevel() == skillLevels.get(j).getValue().intValue()) {
							r.setSkilldes(skillLevels.get(j).getDesc());
							break;
						}
					}
					break;
				}
			}
		}
		return workers;
	}

	/**
	 * 添加制作人
	 * 
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(params = "atn=add")
	public ModelAndView addMaker(HttpServletRequest request, HttpSession session) {
		logger.debug("ProjectsManageCtrl-addMaker start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer opuid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			int worktype = ParamUtils.getIntParameter(request, "worktype", 0);
			String proid = ParamUtils.getParameter(request, "proid");
			String workerStr = ParamUtils.getParameter(request, "addworkers");
			JSONArray addarray = JSONArray.fromObject(workerStr);
			Integer sysid = (Integer) session.getAttribute(CommonConstants.SESSION_CURRENTSYSTEMID);

			RoleModel workRole = projectManagerRoleService.getWorkerRole();
			RoleModel checkRole = projectManagerRoleService.getCheckerRole();

			ProjectsUserModel ur = new ProjectsUserModel();
			ur.setOpuid(opuid);
			ur.setPid(proid);
			ur.setSystemid(sysid);
			if (worktype == 0) {
				ur.setRoleid(workRole.getId());
			} else {
				ur.setRoleid(checkRole.getId());
			}
			for (int i = 0; i < addarray.size(); i++) {
				JSONObject js = addarray.getJSONObject(i);
				Integer workuid = js.getInt("uid");
				String name = js.getString("username");
				ur.setUsername(name);
				ur.setUserid(workuid);
				projectsUserModelDao.insert(ur);
			}
			String delworkerStr = ParamUtils.getParameter(request, "delworkers");
			JSONArray delarray = JSONArray.fromObject(delworkerStr);

			ur = new ProjectsUserModel();
			ur.setPid(proid);
			ur.setSystemid(sysid);
			if (worktype == 0) {
				ur.setRoleid(workRole.getId());
			} else {
				ur.setRoleid(checkRole.getId());
			}
			for (int i = 0; i < delarray.size(); i++) {
				JSONObject js = delarray.getJSONObject(i);
				Integer workuid = js.getInt("uid");
				ur.setUserid(workuid);
				projectsUserModelDao.delete(ur);
			}
			json.addObject("result", 0);
		} catch (Exception e) {
			json.addObject("result", 1);
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		logger.debug("ProjectsManageCtrl-addMaker end.");
		return json;
	}

	@RequestMapping(params = "atn=resetdiff")
	public ModelAndView resetDiff(HttpServletRequest request, HttpSession session, Model mode) {
		logger.debug("ProjectsManageCtrl-resetDiff start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String proid = ParamUtils.getParameter(request, "proid");
		int diff = ParamUtils.getIntParameter(request, "diff", 0);
		try {
			ProjectModel record = new ProjectModel();
			record.setId(Long.valueOf(proid));
			record.setPdifficulty(diff);
			if (projectModelDao.updateByPrimaryKeySelective(record) > 0) {
				json.addObject("result", 1);
			} else {
				json.addObject("result", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			json.addObject("result", 0);
		}
		logger.debug("ProjectsManageCtrl-resetDiff end.");
		return json;
	}

	@RequestMapping(params = "atn=resetpriority")
	public ModelAndView resetPriority(HttpServletRequest request, HttpSession session, Model mode) {
		logger.debug("ProjectsManageCtrl-resetPriority start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String proid = ParamUtils.getParameter(request, "proid");
		int priority = ParamUtils.getIntParameter(request, "priority", 0);
		try {
			ProjectModel record = new ProjectModel();
			record.setId(Long.valueOf(proid));
			record.setPriority(priority);
			if (projectModelDao.updateByPrimaryKeySelective(record) > 0) {
				json.addObject("result", 1);
			} else {
				json.addObject("result", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			json.addObject("result", 0);
		}
		logger.debug("ProjectsManageCtrl-resetPriority end.");
		return json;
	}

	@RequestMapping(params = "atn=resetpaccess")
	public ModelAndView resetPaccess(HttpServletRequest request, HttpSession session, Model mode) {
		logger.debug("ProjectsManageCtrl-resetPaccess start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String proid = ParamUtils.getParameter(request, "proid");
		int access = ParamUtils.getIntParameter(request, "paccess", 0);
		try {
			ProjectModel record = new ProjectModel();
			record.setId(Long.valueOf(proid));
			record.setOwner(access);
			if (projectModelDao.updateByPrimaryKeySelective(record) > 0) {
				json.addObject("result", 1);
			} else {
				json.addObject("result", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			json.addObject("result", 0);
		}
		logger.debug("ProjectsManageCtrl-resetPaccess end.");
		return json;
	}

	@RequestMapping(params = "atn=skillAndDiffAndPri")
	public ModelAndView skillAndDiffAndPri(HttpServletRequest request, HttpSession session) {
		logger.debug("ProjectsManageCtrl-skillAndDiffAndPri start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<MetadataModel> skillLevels = commService.getSkillLevels();
			List<MetadataModel> difficuties = commService.getDifficuties();
			Map<Integer, String> priorityLevels = new HashMap<Integer, String>();
			for (PriorityLevel priorityLevel : PriorityLevel.values()) {
				priorityLevels.put(priorityLevel.getValue(), priorityLevel.getDes());
			}
			json.addObject("skillLevels", skillLevels);
			json.addObject("difficuties", difficuties);
			json.addObject("priorityLevels", priorityLevels);
			json.addObject("result", 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			json.addObject("result", 0);
		}
		logger.debug("ProjectsManageCtrl-skillAndDiffAndPri end.");
		return json;
	}

	@RequestMapping(params = "atn=changestate")
	public ModelAndView changeState(HttpServletRequest request, HttpSession session) {
		logger.debug("-changeState start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String proid = ParamUtils.getParameter(request, "proid");
		int tostate = ParamUtils.getIntParameter(request, "tostate", 0);
		try {
			ProjectModel record = new ProjectModel();
			record.setId(Long.valueOf(proid));
			record.setOverstate(tostate);
			if (projectModelDao.updateByPrimaryKeySelective(record) > 0) {
				json.addObject("result", 1);
			} else {
				json.addObject("result", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			json.addObject("result", 0);
		}
		logger.debug("-changeState end.");
		return json;
	}
}
