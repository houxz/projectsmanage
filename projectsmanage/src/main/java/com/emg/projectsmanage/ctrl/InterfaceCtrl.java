package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;
import com.emg.projectsmanage.pojo.ProcessConfigValueModel;
import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.InterfaceResultModel;
import com.emg.projectsmanage.common.ProcessConfigEnum;
import com.emg.projectsmanage.common.ProcessConfigModuleEnum;
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.SystemType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ConfigValueModel;
import com.emg.projectsmanage.pojo.DepartmentModel;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import com.emg.projectsmanage.pojo.ProjectModelExample.Criteria;
import com.emg.projectsmanage.pojo.ProjectsUserModel;
import com.emg.projectsmanage.pojo.UserRoleModel;
import com.emg.projectsmanage.service.EmapgoAccountService;
import com.emg.projectsmanage.service.ProcessConfigModelService;

@Controller
@RequestMapping("/interface.web")
public class InterfaceCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(InterfaceCtrl.class);

	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;
	@Autowired
	private UserRoleModelDao userRoleModelDao;
	@Autowired
	private EmapgoAccountService emapgoAccountService;
	@Autowired
	private ProcessModelDao processModelDao;
	@Autowired
	private ProcessConfigModelService processConfigModelService;
	@Autowired
	private ProcessConfigValueModelDao processConfigValueModelDao;
	@Autowired
	private ConfigValueModelDao configValueModelDao;
	@Autowired
	private ConfigDBModelDao configDBModelDao;
	
	@RequestMapping(params = "action=insertNewProject", method = RequestMethod.POST)
	private ModelAndView insertNewProject(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("processid") Long processid,
			@RequestParam("protype") Integer protype,
			@RequestParam("pdifficulty") Integer pdifficulty,
			@RequestParam("priority") Integer priority,
			@RequestParam("tasknum") Integer tasknum,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("description") String description,
			@RequestParam("createby") Integer createby,
			@RequestParam("area") String area,
			@RequestParam("name") String name,
			@RequestParam("owner") Integer owner) {
		logger.debug("insertNewProject start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		Long projectid = -1L;
		try {
			ProjectModel project = new ProjectModel();
			project.setProcessid(processid);
			project.setProtype(protype);
			project.setPdifficulty(pdifficulty);
			project.setPriority(0);
			project.setTasknum(tasknum);
			project.setSystemid(systemid);
			project.setDescription(description);
			project.setCreateby(createby);
			project.setOverstate(0);
			project.setArea(area);
			project.setName(name);
			project.setOwner(owner);

			if (projectModelDao.insert(project) > 0) {
				projectid = project.getId();
				status = true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		json.addObject("option", projectid);
		logger.debug("insertNewProject end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectByID", method = RequestMethod.POST)
	private ModelAndView selectProjectByID(Model model, HttpSession session, HttpServletRequest request, @RequestParam("pid") String projectID) {
		logger.debug("selectProjectByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModel projects = projectModelDao.selectByPrimaryKey(Long.valueOf(projectID));
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectByID end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectByProtype", method = RequestMethod.POST)
	private ModelAndView selectProjectByProtype(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("protype") Integer protype) {
		logger.debug("selectProjectByProtype start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid).andProtypeEqualTo(protype);
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectByProtype end!");
		return json;
	}

	@RequestMapping(params = "action=selectTopProjects", method = RequestMethod.POST)
	private ModelAndView selectTopProjects(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("limit") Integer limit) {
		logger.debug("selectTopProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			Criteria criteria = example.or();
			criteria.andSystemidEqualTo(systemid);
			example.setOrderByClause("id desc");
			if (limit != null && limit > 0) {
				example.setLimit(limit);
			}
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectTopProjects end!");
		return json;
	}

	@RequestMapping(params = "action=selectUndoProjects", method = RequestMethod.POST)
	private ModelAndView selectUndoProjects(Model model, HttpSession session, HttpServletRequest request, @RequestParam("systemid") Integer systemid) {
		logger.debug("selectUndoProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid).andOverstateLessThan(4);
			example.setOrderByClause("id desc");
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectUndoProjects end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectsByCreatetime", method = RequestMethod.POST)
	private ModelAndView selectProjectsByCreatetime(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("starttime") String starttime,
			@RequestParam("endtime") String endtime) {
		logger.debug("selectProjectsByCreatetime start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			Criteria criteria = example.or();
			criteria.andSystemidEqualTo(systemid);
			if (!starttime.isEmpty())
				criteria.andCreatetimeGreaterThanOrEqualTo(starttime);
			if (!endtime.isEmpty())
				criteria.andCreatetimeLessThanOrEqualTo(endtime);
			example.setOrderByClause("id");
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectsByCreatetime end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectsByEndtime", method = RequestMethod.POST)
	private ModelAndView selectProjectsByEndtime(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("endtime") String endtime) {
		logger.debug("selectProjectsByEndtime start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			Criteria criteria = example.or();
			criteria.andSystemidEqualTo(systemid);
			if (!endtime.isEmpty())
				criteria.andCreatetimeLessThanOrEqualTo(endtime);
			example.setOrderByClause("id");
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectsByEndtime end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectsByStarttime", method = RequestMethod.POST)
	private ModelAndView selectProjectsByStarttime(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("starttime") String starttime) {
		logger.debug("selectProjectsByStarttime start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			Criteria criteria = example.or();
			criteria.andSystemidEqualTo(systemid);
			if (!starttime.isEmpty())
				criteria.andCreatetimeGreaterThanOrEqualTo(starttime);
			example.setOrderByClause("id");
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectsByStarttime end!");
		return json;
	}

	@RequestMapping(params = "action=selectAllProjects", method = RequestMethod.POST)
	private ModelAndView selectAllProjects(Model model, HttpSession session, HttpServletRequest request, @RequestParam("systemid") Integer systemid) {
		logger.debug("selectAllProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid);
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectAllProjects end!");
		return json;
	}

	@RequestMapping(params = "action=selectCountOfProjects", method = RequestMethod.POST)
	private ModelAndView selectCountOfProjects(Model model, HttpSession session, HttpServletRequest request, @RequestParam("systemid") Integer systemid) {
		logger.debug("selectCountOfProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid);
			int count = projectModelDao.countByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", count);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectCountOfProjects end!");
		return json;
	}

	@RequestMapping(params = "action=updateProjectTasknumByID", method = RequestMethod.POST)
	private ModelAndView updateProjectTasknumByID(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("pid") String projectID,
			@RequestParam("tasknum") Integer tasknum) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {
			ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectID));
			
			if(project == null) {
				logger.error("Can not find Project :" + projectID);
				json.addObject("option", "Can not find Project :" + projectID);
				json.addObject("status", false);
				return json;
			}

			Integer curTasknum = project.getTasknum();
			if (tasknum > curTasknum) {
				project.setTasknum(tasknum);
				project.setOverstate(1);
				if (projectModelDao.updateByPrimaryKey(project) > 0)
					status = true;
			} else {
				status = false;
				json.addObject("option", "任务数未增加");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "action=updateProjectOverstateByID", method = RequestMethod.POST)
	private ModelAndView updateProjectOverstateByID(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("pid") String projectID,
			@RequestParam("overstate") Integer overstate) {
		logger.debug("updateProjectOverstateByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {
			ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectID));
			
			if(project == null) {
				logger.error("Can not find Project :" + projectID);
				json.addObject("option", "Can not find Project :" + projectID);
				json.addObject("status", false);
				return json;
			}
			
			project.setOverstate(overstate);
			if (projectModelDao.updateByPrimaryKey(project) > 0)
				status = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("updateProjectOverstateByID end!");
		return json;
	}

	@RequestMapping(params = "action=submitTaskByID", method = RequestMethod.POST)
	private ModelAndView submitTaskByID(Model model, HttpSession session, HttpServletRequest request, @RequestParam("pid") String projectID, @RequestParam("type") Integer type) {
		logger.debug("submitTaskByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {
			status = false;
			json.addObject("option", "未知的操作类型");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("submitTaskByID end!");
		return json;
	}

	/**
	 * 废弃不用了
	 * @param model
	 * @param session
	 * @param request
	 * @param systemid
	 * @param projectid
	 * @param taskid
	 * @param userid
	 * @param statebefore
	 * @param processbefore
	 * @param stateafter
	 * @param processafter
	 * @return
	 */
	@RequestMapping(params = "action=submitTaskStatus", method = RequestMethod.POST)
	private ModelAndView submitTaskStatus(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("projectid") String projectid,
			@RequestParam("taskid") String taskid,
			@RequestParam("userid") Integer userid,
			@RequestParam("statebefore") Integer statebefore,
			@RequestParam("processbefore") Integer processbefore,
			@RequestParam("stateafter") Integer stateafter,
			@RequestParam("processafter") Integer processafter) {
		logger.debug("submitTaskStatus start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {} catch (Exception e) {
			logger.error(e.getMessage(), e);
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("submitTaskStatus end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserInSystem", method = RequestMethod.POST)
	private ModelAndView checkUserInSystem(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("userid") Integer userid,
			@RequestParam("roleid") Integer roleid) {
		logger.debug("checkUserInSystem start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			UserRoleModel relation = new UserRoleModel();
			relation.setUserid(userid);
			relation.setRoleid(roleid);
			List<UserRoleModel> projectUsers = userRoleModelDao.query(relation);
			if (projectUsers != null && projectUsers.size() > 0) {
				json.addObject("status", true);
				json.addObject("option", true);
			} else {
				json.addObject("status", true);
				json.addObject("option", false);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserInSystem end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserRoleByID", method = RequestMethod.POST)
	private ModelAndView selectUserRoleByID(Model model, HttpSession session, HttpServletRequest request, @RequestParam("id") Integer id) {
		logger.debug("selectUserRoleByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			UserRoleModel ru = new UserRoleModel();
			ru.setId(id);
			List<UserRoleModel> roleusers = userRoleModelDao.query(ru);
			json.addObject("status", true);
			json.addObject("option", roleusers);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectUserRoleByID end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserRoleByUserID", method = RequestMethod.POST)
	private ModelAndView selectUserRoleByUserID(Model model, HttpSession session, HttpServletRequest request, @RequestParam("userid") Integer userid) {
		logger.debug("selectUserRoleByUserID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			UserRoleModel ru = new UserRoleModel();
			ru.setUserid(userid);
			List<UserRoleModel> roleusers = userRoleModelDao.query(ru);
			json.addObject("status", true);
			json.addObject("option", roleusers);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserRoleByUserID end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserRoleByRoleID", method = RequestMethod.POST)
	private ModelAndView selectUserRoleByRoleID(Model model, HttpSession session, HttpServletRequest request, @RequestParam("roleid") Integer roleid) {
		logger.debug("selectUserRoleByRoleID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			UserRoleModel ru = new UserRoleModel();
			ru.setRoleid(roleid);
			List<UserRoleModel> roleusers = userRoleModelDao.query(ru);
			json.addObject("status", true);
			json.addObject("option", roleusers);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserRoleByRoleID end!");
		return json;
	}

	@RequestMapping(params = "action=selectAllUserRoles", method = RequestMethod.POST)
	private ModelAndView selectAllUserRoles(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("selectAllUserRoles start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<UserRoleModel> roleusers = userRoleModelDao.queryAll();
			json.addObject("status", true);
			json.addObject("option", roleusers);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectAllUserRoles end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserRoleCount", method = RequestMethod.POST)
	private ModelAndView selectUserRoleCount(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("selectUserRoleCount start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			int count = userRoleModelDao.queryCount();
			json.addObject("status", true);
			json.addObject("option", count);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserRoleCount end!");
		return json;
	}
	
	@RequestMapping(params = "action=selectDoingProjects", method = RequestMethod.POST)
	private ModelAndView selectDoingProjects(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid) {
		logger.debug("selectDoingProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<ProjectModel> projects = new ArrayList<ProjectModel>();
			
			ProjectModelExample example = new ProjectModelExample();
			example.or()
				.andSystemidEqualTo(systemid)
				.andOverstateEqualTo(1);
			example.setOrderByClause("owner DESC, priority DESC, id");
			projects = projectModelDao.selectByExample(example);
			
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectDoingProjects end!");
		return json;
	}
	
	@RequestMapping(params = "action=selectMyProjects", method = RequestMethod.POST)
	private ModelAndView selectMyProjects(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("userid") Integer userid,
			@RequestParam(value = "roleid", required = false, defaultValue = "-1") Integer roleid) {
		logger.debug("selectMyProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<ProjectModel> myProjects = new ArrayList<ProjectModel>();
			
			ProjectsUserModel record = new ProjectsUserModel();
			record.setUserid(userid);
			if (roleid.compareTo(0) >= 0) {
				record.setRoleid(roleid);
			}
			List<ProjectsUserModel> projectsUserModels = projectsUserModelDao.queryProjectUsers(record);
			List<Long> myProjectIDs = new ArrayList<Long>();
			myProjectIDs.add(-1L);
			for (ProjectsUserModel projectsUserModel : projectsUserModels) {
				myProjectIDs.add(Long.valueOf(projectsUserModel.getPid()));
			}

			ProjectModelExample example = new ProjectModelExample();
			example.or()
				.andSystemidEqualTo(systemid)
				.andOverstateEqualTo(1)
				.andOwnerEqualTo(1)
				.andIdIn(myProjectIDs);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));
			
			example.clear();
			example.or()
				.andSystemidEqualTo(systemid)
				.andOverstateEqualTo(1)
				.andOwnerEqualTo(0);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));

			model.addAttribute("status", true);
			model.addAttribute("option", myProjects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectMyProjects end!");
		return json;
	}

	@RequestMapping(params = "action=selectNextProject", method = RequestMethod.POST)
	private ModelAndView selectNextProject(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("userid") Integer userid,
			@RequestParam(value = "roleid", required = false, defaultValue = "-1") Integer roleid,
			@RequestParam("pid") String pid) {
		logger.debug("selectNextProject start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			String projectid = "-1";
			String name = new String();
			Integer tasknum = -1;
			Long processid = -1L;

			ProjectsUserModel record = new ProjectsUserModel();
			record.setUserid(userid);
			if (roleid.compareTo(0) >= 0) {
				record.setRoleid(roleid);
			}
			List<ProjectsUserModel> projectsUserModels = projectsUserModelDao.queryProjectUsers(record);
			List<Long> myProjectIDs = new ArrayList<Long>();
			myProjectIDs.add(-1L);
			for (ProjectsUserModel projectsUserModel : projectsUserModels) {
				myProjectIDs.add(Long.valueOf(projectsUserModel.getPid()));
			}

			Integer priority = 9999;
			Integer owner = 1;
			if (!pid.equals("-1")) {
				ProjectModel curProject = projectModelDao.selectByPrimaryKey(Long.valueOf(pid));
				if (curProject != null) {
					priority = curProject.getPriority();
					owner = curProject.getOwner();
				} else {
					json.addObject("status", false);
					json.addObject("option", "基于 " + pid + " 未找到项目！");
					logger.debug("selectNextProject end!");
					return json;
				}
			}

			ProjectModelExample example = new ProjectModelExample();
			Criteria criteria1 = example.or();
			criteria1.andSystemidEqualTo(systemid).andOverstateEqualTo(1).andOwnerEqualTo(owner).andPriorityEqualTo(priority).andIdGreaterThan(Long.valueOf(pid));
			Criteria criteria2 = example.or();
			criteria2.andSystemidEqualTo(systemid).andOverstateEqualTo(1).andOwnerEqualTo(owner).andPriorityLessThan(priority);
			if (owner.equals(1) && myProjectIDs.size() > 0) {
				criteria1.andIdIn(myProjectIDs);
				criteria2.andIdIn(myProjectIDs);
			}
			example.setOrderByClause("priority DESC, id");
			example.setLimit(1);
			List<ProjectModel> project = projectModelDao.selectByExample(example);
			if ((project == null || project.size() <= 0) && owner.equals(1)) {
				example.clear();
				example.or().andSystemidEqualTo(systemid).andOwnerEqualTo(0).andOverstateEqualTo(1);
				example.setOrderByClause("priority DESC, id");
				example.setLimit(1);
				project = projectModelDao.selectByExample(example);
			}
			if (project != null && project.size() > 0) {
				projectid = project.get(0).getId().toString();
				processid = project.get(0).getProcessid();
				tasknum = project.get(0).getTasknum();
				name = project.get(0).getName();
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("projectid", projectid);
			map.put("processid", processid);
			map.put("tasknum", tasknum);
			map.put("name", name);
			model.addAttribute("status", true);
			model.addAttribute("option", map);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectNextProject end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserByUsername", method = RequestMethod.POST)
	private ModelAndView checkUserByUsername(Model model, HttpSession session, HttpServletRequest request, @RequestParam("username") String username) {
		logger.debug("checkUserByUsername start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			EmployeeModel record = new EmployeeModel();
			record.setUsername(username);
			EmployeeModel employee = emapgoAccountService.getOneEmployee(record);

			if (employee != null) {
				json.addObject("status", true);
				json.addObject("option", true);
			} else {
				json.addObject("status", true);
				json.addObject("option", false);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserByUsername end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserByUsernameAndPassword", method = RequestMethod.POST)
	private ModelAndView checkUserByUsernameAndPassword(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("username") String username,
			@RequestParam("password") String password) {
		logger.debug("checkUserByUsernameAndPassword start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			EmployeeModel record = new EmployeeModel();
			record.setUsername(username);
			record.setPassword(password);
			EmployeeModel employee = emapgoAccountService.getOneEmployee(record);
			if (employee != null) {
				json.addObject("status", true);
				json.addObject("option", true);
			} else {
				json.addObject("status", true);
				json.addObject("option", false);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserByUsernameAndPassword end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserByUsernameAndDepartment", method = RequestMethod.POST)
	private ModelAndView checkUserByUsernameAndDepartment(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("username") String username,
			@RequestParam("department") Integer department) {
		logger.debug("checkUserByUsernameAndDepartment start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			EmployeeModel record = new EmployeeModel();
			record.setUsername(username);
			record.setDepartment(department);
			EmployeeModel employee = emapgoAccountService.getOneEmployee(record);
			if (employee != null) {
				json.addObject("status", true);
				json.addObject("option", true);
			} else {
				json.addObject("status", true);
				json.addObject("option", false);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserByUsernameAndDepartment end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserByUsernameAndPasswordAndDepartment", method = RequestMethod.POST)
	private ModelAndView checkUserByUsernameAndPasswordAndDepartment(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("department") Integer department) {
		logger.debug("checkUserByUsernameAndPasswordAndDepartment start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			EmployeeModel record = new EmployeeModel();
			record.setUsername(username);
			record.setPassword(password);
			record.setDepartment(department);
			EmployeeModel employee = emapgoAccountService.getOneEmployee(record);
			if (employee != null) {
				json.addObject("status", true);
				json.addObject("option", true);
			} else {
				json.addObject("status", true);
				json.addObject("option", false);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserByUsernameAndPasswordAndDepartment end!");
		return json;
	}

	@RequestMapping(params = "action=selectAllUserInfos", method = RequestMethod.POST)
	private ModelAndView selectAllUserInfos(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("selectAllUserInfos start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<EmployeeModel> employees = emapgoAccountService.getAllEmployees();
			json.addObject("status", true);
			json.addObject("option", employees);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectAllUserInfos end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserInfoByUsername", method = RequestMethod.POST)
	private ModelAndView selectUserInfoByUsername(Model model, HttpSession session, HttpServletRequest request, @RequestParam("username") String username) {
		logger.debug("selectUserIDByUsername start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			EmployeeModel record = new EmployeeModel();
			record.setUsername(username);
			EmployeeModel employee = emapgoAccountService.getOneEmployee(record);
			if (employee != null) {
				json.addObject("status", true);
				json.addObject("option", employee);
			} else {
				json.addObject("status", true);
				json.addObject("option", null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserInfoByUsername end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserInfoByUserID", method = RequestMethod.POST)
	private ModelAndView selectUserInfoByUserID(Model model, HttpSession session, HttpServletRequest request, @RequestParam("userid") Integer userid) {
		logger.debug("selectUserIDByUsername start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			EmployeeModel record = new EmployeeModel();
			record.setId(userid);
			EmployeeModel employee = emapgoAccountService.getOneEmployee(record);
			if (employee != null) {
				json.addObject("status", true);
				json.addObject("option", employee);
			} else {
				json.addObject("status", true);
				json.addObject("option", null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserInfoByUsername end!");
		return json;
	}

	@RequestMapping(params = "action=selectAllDepartment", method = RequestMethod.POST)
	private ModelAndView selectAllDepartment(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("selectAllDepartment start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<DepartmentModel> departments = emapgoAccountService.getAllDepartment();
			if (departments != null && departments.size() > 0) {
				json.addObject("status", true);
				json.addObject("departments", departments);
			} else {
				json.addObject("status", false);
				json.addObject("option", null);
				logger.error("ZSEN -> null");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectAllDepartment end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectByName", method = RequestMethod.POST)
	private ModelAndView selectProjectByName(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam("name") String name,
			@RequestParam("proType") Integer proType,
			@RequestParam("systemid") Integer systemid) {
		logger.debug("selectProjectByName start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andNameEqualTo(name)
						.andProtypeEqualTo(proType)
						.andSystemidEqualTo(systemid);
			
			List<ProjectModel> ret = projectModelDao.selectByExample(example);
			json.addObject("status", true);
			json.addObject("option", ret);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectProjectByName end!");
		return json;
	}
	
	@RequestMapping(params = "action=selectProcessIDByCityAndBatch", method = RequestMethod.POST)
	private ModelAndView selectProcessIDByCityAndBatch(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam("city") String city,
			@RequestParam("batchid") String batchid) {
		logger.debug("selectProjectByName start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<Long> list = processConfigValueModelDao.selectProcessIDsByCityAndBatch(city, batchid);
			if (list != null && list.size() > 0) {
				json.addObject("status", true);
				json.addObject("option", list);
			} else {
				json.addObject("status", true);
				json.addObject("option", null);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectProjectByName end!");
		return json;
	}

	@RequestMapping(params = "action=QCUndoProjects", method = RequestMethod.POST)
	private ModelAndView QCUndoProjects(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("limit") Integer limit) {
		logger.debug("QCUndoProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid).andOverstateEqualTo(1);
			example.or().andSystemidEqualTo(0).andOverstateEqualTo(1);
			example.setOrderByClause("priority desc, id");
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			json.addObject("status", true);
			json.addObject("option", projects);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("QCUndoProjects end!");
		return json;
	}

	@RequestMapping(params = "action=QCUndoProjectIDs", method = RequestMethod.POST)
	private ModelAndView QCUndoProjectIDs(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("QCUndoProjectIDs start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			String ids = new String();
			ProjectModelExample example = new ProjectModelExample();
			example.or().andOverstateEqualTo(1);
			example.setOrderByClause("priority desc, id");
			List<ProjectModel> projects = projectModelDao.selectByExample(example);
			if (projects.size() > 0) {
				for (ProjectModel project : projects) {
					ids += project.getId() + ",";
				}
				ids = ids.substring(0, ids.length() - 1);
			}
			json.addObject("status", true);
			json.addObject("option", ids);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("QCUndoProjectIDs end!");
		return json;
	}
	
	@RequestMapping(params = "action=insertNewProcess", method = RequestMethod.POST)
	private ModelAndView insertNewProcess(Model model, HttpSession session, HttpServletRequest request, 
			@RequestParam("type") Integer newProcessType,
			@RequestParam("name") String newProcessName,
			@RequestParam(value = "priority", required = false, defaultValue = "0") Integer newProcessPriority,
			@RequestParam(value = "state", required = false, defaultValue = "0") Integer newProcessState,
			@RequestParam(value = "owner", required = false, defaultValue = "0") Integer newProcessOwner,
			@RequestParam(value = "fielddata", required = false, defaultValue = "") String newProcessFielddata,
			@RequestParam(value = "area", required = false, defaultValue = "") String newProcessArea) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			String suffix = new String();
			Integer systemid = -1;
			
			if(newProcessType.equals(ProcessType.ERROR.getValue())){
				suffix = "_改错";
				systemid = SystemType.MapDbEdit.getValue();
			} else if(newProcessType.equals(ProcessType.NRFC.getValue())) {
				suffix = "_NR/FC";
				systemid = SystemType.MapDbEdit_NRFC.getValue();
			} else if(newProcessType.equals(ProcessType.ATTACH.getValue())) {
				suffix = "_关系附属表";
				systemid = SystemType.MapDbEdit_Attach.getValue();
			} else if(newProcessType.equals(ProcessType.COUNTRY.getValue())) {
				suffix = "_全国质检";
				systemid = SystemType.MapDbEdit_Country.getValue();
			} else if(newProcessType.equals(ProcessType.POIEDIT.getValue())) {
				suffix = "";
				systemid = SystemType.poivideoedit.getValue();
			} else if (newProcessType.equals(ProcessType.ADJUSTMAP.getValue())) {
				suffix = "";
				systemid = SystemType.AdjustMap.getValue();
			} else if (newProcessType.equals(ProcessType.GEN.getValue())) {
				suffix = "";
				systemid = SystemType.poi_GEN.getValue();
			} else {
				json.addObject("result", false);
				json.addObject("resultMsg", "未知的项目类型");
				return json;
			}
			
			ProcessModel newProcess = new ProcessModel();
			newProcess.setName(newProcessName);
			newProcess.setType(newProcessType);
			newProcess.setPriority(newProcessPriority);
			newProcess.setState(newProcessState);
			newProcess.setUserid(0);
			newProcess.setUsername("系统工具");
			if(newProcessType.equals(ProcessType.POIEDIT.getValue()) ||
				newProcessType.equals(ProcessType.GEN.getValue())) {
				newProcess.setProgress("0,0");
			} else if(newProcessType.equals(ProcessType.ADJUSTMAP.getValue())) {
				newProcess.setProgress("0");
			} else {
				newProcess.setProgress("0,0,0,0");
			}
			if (processModelDao.insertSelective(newProcess) <= 0) {
				json.addObject("result", false);
				json.addObject("resultMsg", "新建流程失败");
				return json;
			}
			Long newProcessID = newProcess.getId();
			
			String projectName = newProcessName + suffix;
			ProjectModel newpro = new ProjectModel();
			newpro.setProcessid(newProcessID);
			newpro.setName(projectName);
			newpro.setSystemid(systemid);
			newpro.setProtype(newProcessType);
			newpro.setPdifficulty(0);
			newpro.setTasknum(-1);
			newpro.setOverstate(newProcessState);
			newpro.setCreateby(0);
			newpro.setPriority(newProcessPriority);
			newpro.setOwner(newProcessOwner);
			
			if (projectModelDao.insert(newpro) <= 0) {
				json.addObject("result", false);
				json.addObject("resultMsg", "新建项目失败");
				return json;
			}
			Long projectid = newpro.getId();
			
			List<ProcessConfigValueModel> configValues = new ArrayList<ProcessConfigValueModel>();

			configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJIXIANGMUID.getValue(), projectid.toString()));
			configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue(), projectName));
			configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BANGDINGZILIAO.getValue(), newProcessFielddata));
			configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.GONGYOUSIYOU.getValue(), newProcessOwner.toString()));
			configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANQUYU.getValue(), newProcessArea.toString()));
			
			List<ProcessConfigModel> processConfigs = processConfigModelService.selectAllProcessConfigModels(newProcessType);
			for (ProcessConfigModel processConfig : processConfigs) {
				Integer moduleid = processConfig.getModuleid();
				Integer configid = processConfig.getId();
				String defaultValue = processConfig.getDefaultValue() == null ? new String() : processConfig.getDefaultValue().toString();

				if ((moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIXIANGMUID.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BANGDINGZILIAO.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.GONGYOUSIYOU.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANQUYU.getValue())))
					continue;

				configValues.add(new ProcessConfigValueModel(newProcessID, moduleid, configid, defaultValue));
			}

			processConfigValueModelDao.insert(configValues);
			
			json.addObject("status", true);
			json.addObject("option", newProcessID);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("result", false);
			json.addObject("resultMsg", e.getMessage());
			return json;
		}
		logger.debug("END");
		return json;
	}

	// by xiao
	@RequestMapping(params = "action=selectProcessByID", method = RequestMethod.POST)
	private ModelAndView selectProcessByID(Model model, HttpSession session, HttpServletRequest request, @RequestParam("processid") String processID) {
		logger.debug("selectProcessByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProcessModel process = processModelDao.selectByPrimaryKey(Long.valueOf(processID));
			model.addAttribute("status", true);
			model.addAttribute("option", process);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProcessByID end!");
		return json;
	}
	
	@RequestMapping(params = "action=selectProcessByName", method = RequestMethod.POST)
	private ModelAndView selectProcessByName(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam("name") String name,
			@RequestParam("type") Integer type) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProcessModelExample example = new ProcessModelExample();
			example.or()
				.andNameEqualTo(name)
				.andTypeEqualTo(type);
			List<ProcessModel> processes = processModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", processes);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "action=selectProcessByState", method = RequestMethod.POST)
	private ModelAndView selectProcessByState(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam("type") Integer type,
			@RequestParam("state") Integer state) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProcessModelExample example = new ProcessModelExample();
			example.or()
				.andStateEqualTo(state)
				.andTypeEqualTo(type);
			example.setOrderByClause("priority DESC, id");
			List<ProcessModel> processes = processModelDao.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", processes);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "action=selectProcessIDByProjectID", method = RequestMethod.POST)
	private ModelAndView selectProcessIDByProjectID(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("moduleid") Integer moduleid,
			@RequestParam("projectid") Long projectid) {
		logger.debug("selectProcessIDByProjectID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
			if (project != null) {
				model.addAttribute("status", true);
				model.addAttribute("option", project.getProcessid());
				return json;
			} else {
				model.addAttribute("status", false);
				model.addAttribute("option", "未找到项目");
				logger.error("未找到流程:  " + projectid);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProcessIDByProjectID end!");
		return json;
	}
	
	@RequestMapping(params = "action=updateProcessByID", method = RequestMethod.POST)
	private ModelAndView updateProcessByID(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("processid") Long processID,
			@RequestParam(value = "state", required = false, defaultValue = "-1") Integer state,
			@RequestParam(value = "stage", required = false, defaultValue = "-1") Integer stage,
			@RequestParam(value = "stagestate", required = false, defaultValue = "-1") Integer stagestate,
			@RequestParam(value = "progress", required = false, defaultValue = "") String progress) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {
			ProcessModel process = new ProcessModel();
			process.setId(processID);
			if (state >= 0)
				process.setState(state);
			if (stage >= 0)
				process.setStage(stage);
			if (stagestate >= 0)
				process.setStagestate(stagestate);
			if (progress != null && !progress.isEmpty())
				process.setProgress(progress);
			if (processModelDao.updateByPrimaryKeySelective(process) > 0) {
				status = true;
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("processID: " + processID);
			logger.error("state: " + state);
			logger.error("stage: " + stage);
			logger.error("stagestate: " + stagestate);
			logger.error("progress: " + progress);
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("END");
		return json;
	}

	// by xiao
	@RequestMapping(params = "action=updateProcessProgressByID", method = RequestMethod.POST)
	private ModelAndView updateProcessProgressByID(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("processid") Long processID,
			@RequestParam("stage") Integer stage,
			@RequestParam("progress") Float progress) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {
			if (stage <= 0 || stage > 4) {
				json.addObject("status", false);
				json.addObject("option", "错误的数据范围<stage>:" + stage);
				logger.error("错误的数据范围<stage>:" + stage);
				return json;
			}
			if (processID.compareTo(0L) <= 0) {
				json.addObject("status", false);
				json.addObject("option", "错误的数据范围<processID>:" + processID);
				logger.error("错误的数据范围<processID>:" + processID);
				return json;
			}
			if (progress.compareTo((float) 0) <= 0) {
				json.addObject("status", false);
				json.addObject("option", "错误的数据范围<progress>:" + progress);
				logger.error("错误的数据范围<progress>:" + progress);
				return json;
			}
			ProcessModel process = processModelDao.selectByPrimaryKey(processID);
			if (process == null || process.getId() == null || process.getId().compareTo(0L) <= 0) {
				json.addObject("status", false);
				json.addObject("option", "未找到流程<ID>:" + processID);
				logger.error("未找到流程<ID>:" + processID);
				return json;
			}

			Integer proType = process.getType();

			if (proType.equals(ProcessType.ERROR.getValue())) {
				String sProgress = process.getProgress();
				if (sProgress.length() > 0) {
					String[] arProgress = sProgress.split(",");
					ArrayList<String> alProgress = new ArrayList<String>(Arrays.asList(arProgress));
					Integer length = alProgress.size();
					while (length < CommonConstants.PROCESSCOUNT_ERROR) {
						alProgress.add("0");
						length++;
					}
					alProgress.set(stage - 1, progress.toString());

					StringBuilder sbProgress = new StringBuilder();
					for (String p : alProgress) {
						sbProgress.append(p);
						sbProgress.append(",");
					}
					sbProgress.deleteCharAt(sbProgress.length() - 1);

					process.setProgress(sbProgress.toString());
					Integer stageStart = 0;
					Integer projectid = 0;

					if (progress.compareTo(Float.valueOf(100)) == 0 && stage == process.getStage()) {
						process.setStagestate(3);

						if (stage < CommonConstants.PROCESSCOUNT_ERROR) {
							ProcessConfigValueModel configValueModel = processConfigValueModelDao.selectByProcessIDAndConfigID(processID, stage == 1 ? ProcessConfigEnum.ZHIJIANQIDONGLEIXING.getValue() : ProcessConfigEnum.BIANJIQIDONGLEIXING.getValue());
							if (Integer.valueOf(configValueModel.getValue()) == 2) {
								process.setStage(stage + 1);
								process.setStagestate(1);
								stageStart = process.getStage();
							}
							configValueModel = processConfigValueModelDao.selectByProcessIDAndConfigID(processID, stage == 1 ? ProcessConfigEnum.ZHIJIANXIANGMUID.getValue() : ProcessConfigEnum.BIANJIXIANGMUID.getValue());
							projectid = Integer.valueOf(configValueModel.getValue());
						} else if (stage == CommonConstants.PROCESSCOUNT_ERROR) {
							process.setState(ProcessState.COMPLETE.getValue());
						}
					}
					if (processModelDao.updateByPrimaryKey(process) > 0) {
						status = true;

						if (stageStart == 2 || stageStart == 4) {
							try {
								ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
								project.setOverstate(1);
								projectModelDao.updateByPrimaryKey(project);
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								json.addObject("option", e.getMessage());
							}
						}

					}
				} else {
					status = false;
					json.addObject("option", "进度未更新");
				}
			} else if (proType.equals(ProcessType.NRFC.getValue()) || proType.equals(ProcessType.ATTACH.getValue())) {
				String sProgress = process.getProgress();
				if (sProgress.length() > 0) {
					String[] arProgress = sProgress.split(",");
					ArrayList<String> alProgress = new ArrayList<String>(Arrays.asList(arProgress));
					Integer length = alProgress.size();
					while (length < CommonConstants.PROCESSCOUNT_NRFC) {
						alProgress.add("0");
						length++;
					}
					// X,X,X
					alProgress.set(stage - 1, progress.toString());

					StringBuilder sbProgress = new StringBuilder();
					for (String p : alProgress) {
						sbProgress.append(p);
						sbProgress.append(",");
					}
					sbProgress.deleteCharAt(sbProgress.length() - 1);

					process.setProgress(sbProgress.toString());
					Integer stageStart = 0;
					Integer projectid = 0;

					if (progress.compareTo(Float.valueOf(100)) == 0 && stage == process.getStage()) {
						process.setStagestate(3);

						if (stage < CommonConstants.PROCESSCOUNT_NRFC) {
							ProcessConfigValueModel configValueModel = processConfigValueModelDao.selectByProcessIDAndConfigID(processID, ProcessConfigEnum.BIANJIQIDONGLEIXING.getValue());
							if (Integer.valueOf(configValueModel.getValue()) == 2) {
								process.setStage(stage + 1);
								process.setStagestate(1);
								stageStart = process.getStage();
							}
							configValueModel = processConfigValueModelDao.selectByProcessIDAndConfigID(processID, ProcessConfigEnum.BIANJIXIANGMUID.getValue());
							projectid = Integer.valueOf(configValueModel.getValue());
						} else if (stage == CommonConstants.PROCESSCOUNT_NRFC) {
							process.setState(ProcessState.COMPLETE.getValue());
						}
					}
					if (processModelDao.updateByPrimaryKey(process) > 0) {
						status = true;

						if (stageStart == 2) {
							try {
								ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
								project.setOverstate(1);
								projectModelDao.updateByPrimaryKey(project);
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								json.addObject("option", e.getMessage());
							}
						}

					}
				}
			} else if (proType.equals(ProcessType.COUNTRY.getValue())) {
				String sProgress = process.getProgress();
				if (sProgress.length() > 0) {
					String[] arProgress = sProgress.split(",");
					ArrayList<String> alProgress = new ArrayList<String>(Arrays.asList(arProgress));
					Integer length = alProgress.size();
					while (length < CommonConstants.PROCESSCOUNT_COUNTRY) {
						alProgress.add("0");
						length++;
					}
					// X,X,X,X
					alProgress.set(stage - 1, progress.toString());

					StringBuilder sbProgress = new StringBuilder();
					for (String p : alProgress) {
						sbProgress.append(p);
						sbProgress.append(",");
					}
					sbProgress.deleteCharAt(sbProgress.length() - 1);

					process.setProgress(sbProgress.toString());
					Integer stageStart = 0;
					Integer projectid = 0;

					if (progress.compareTo(Float.valueOf(100)) == 0 && stage == process.getStage()) {
						process.setStagestate(3);

						if (stage < CommonConstants.PROCESSCOUNT_COUNTRY) {
							ProcessConfigValueModel configValueModel = processConfigValueModelDao.selectByProcessIDAndConfigID(processID, stage == 1 ? ProcessConfigEnum.ZHIJIANQIDONGLEIXING.getValue() : ProcessConfigEnum.BIANJIQIDONGLEIXING.getValue());
							if (Integer.valueOf(configValueModel.getValue()) == 2) {
								process.setStage(stage + 1);
								process.setStagestate(1);
								stageStart = process.getStage();
							}
							configValueModel = processConfigValueModelDao.selectByProcessIDAndConfigID(processID, stage == 1 ? ProcessConfigEnum.ZHIJIANXIANGMUID.getValue() : ProcessConfigEnum.BIANJIXIANGMUID.getValue());
							projectid = Integer.valueOf(configValueModel.getValue());
						} else if (stage == CommonConstants.PROCESSCOUNT_COUNTRY) {
							process.setState(ProcessState.COMPLETE.getValue());
						}
					}
					if (processModelDao.updateByPrimaryKey(process) > 0) {
						status = true;

						if (stageStart == 2) {
							try {
								ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
								project.setOverstate(1);
								projectModelDao.updateByPrimaryKey(project);
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								json.addObject("option", e.getMessage());
							}
						}

					}
				} else {
					status = false;
					json.addObject("option", "进度未更新");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("processID: " + processID);
			logger.error("stage: " + stage);
			logger.error("progress: " + progress);
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("END");
		return json;
	}

	// by xiao
	@RequestMapping(params = "action=selectNextProcess", method = RequestMethod.POST)
	private ModelAndView selectNextProcess(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("stageid") Integer stageid,
			@RequestParam("pid") Long pid,
			@RequestParam("type") Integer type) {
		logger.debug("selectNextProcess start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			String projectid = "-1";
			String name = "";

			ProcessModel curProcess = processModelDao.selectByPrimaryKey(pid);
			Integer priority = curProcess == null ? 2 : curProcess.getPriority();

			ProcessModelExample example = new ProcessModelExample();
			com.emg.projectsmanage.pojo.ProcessModelExample.Criteria _criteria1 = example.or();
			_criteria1.andTypeEqualTo(type).andPriorityEqualTo(priority).andStateEqualTo(1) // 流程开始
					.andStageEqualTo(stageid).andStagestateEqualTo(1) // 阶段开始
					.andIdGreaterThan(pid);
			com.emg.projectsmanage.pojo.ProcessModelExample.Criteria _criteria2 = example.or();
			_criteria2.andTypeEqualTo(type).andPriorityEqualTo(priority).andStateEqualTo(1) // 流程开始
					.andStageEqualTo(stageid - 1).andStagestateEqualTo(3) // 上一个阶段完成
					.andIdGreaterThan(pid);
			com.emg.projectsmanage.pojo.ProcessModelExample.Criteria _criteria3 = example.or();
			_criteria3.andTypeEqualTo(type).andPriorityEqualTo(priority).andStateEqualTo(1) // 流程开始
					.andStageEqualTo(stageid).andStagestateEqualTo(2) // 阶段暂停
					.andIdGreaterThan(pid);
			com.emg.projectsmanage.pojo.ProcessModelExample.Criteria _criteria4 = example.or();
			_criteria4.andTypeEqualTo(type).andPriorityEqualTo(priority).andStateEqualTo(1) // 流程开始
					.andStageEqualTo(stageid).andStagestateEqualTo(0) // 阶段初始
					.andIdGreaterThan(pid);
			if (stageid == 1) {
				com.emg.projectsmanage.pojo.ProcessModelExample.Criteria _criteria5 = example.or();
				_criteria5.andTypeEqualTo(type).andPriorityEqualTo(priority).andStateEqualTo(1) // 流程开始
						.andStageEqualTo(0) // 流程初始
						.andStagestateEqualTo(0) // 阶段初始
						.andIdGreaterThan(pid);
			}

			example.setOrderByClause("priority DESC, id");
			example.setLimit(1);
			List<ProcessModel> project = processModelDao.selectByExample(example);
			if (project == null || project.size() <= 0) {
				example.clear();
				com.emg.projectsmanage.pojo.ProcessModelExample.Criteria criteria1 = example.or();
				criteria1.andTypeEqualTo(type).andPriorityLessThan(priority).andStateEqualTo(1) // 流程开始
						.andStageEqualTo(stageid).andStagestateEqualTo(1); // 阶段开始
				com.emg.projectsmanage.pojo.ProcessModelExample.Criteria criteria2 = example.or();
				criteria2.andTypeEqualTo(type).andPriorityLessThan(priority).andStateEqualTo(1) // 流程开始
						.andStageEqualTo(stageid - 1).andStagestateEqualTo(3); // 上一个阶段完成
				com.emg.projectsmanage.pojo.ProcessModelExample.Criteria criteria3 = example.or();
				criteria3.andTypeEqualTo(type).andPriorityLessThan(priority).andStateEqualTo(1) // 流程开始
						.andStageEqualTo(stageid).andStagestateEqualTo(2); // 阶段暂停
				com.emg.projectsmanage.pojo.ProcessModelExample.Criteria criteria4 = example.or();
				criteria4.andTypeEqualTo(type).andPriorityLessThan(priority).andStateEqualTo(1) // 流程开始
						.andStageEqualTo(stageid).andStagestateEqualTo(0); // 阶段初始
				if (stageid == 1) {
					com.emg.projectsmanage.pojo.ProcessModelExample.Criteria criteria5 = example.or();
					criteria5.andTypeEqualTo(type).andPriorityLessThan(priority).andStateEqualTo(1) // 流程开始
							.andStageEqualTo(0) // 流程初始
							.andStagestateEqualTo(0); // 阶段初始
				}

				example.setOrderByClause("priority DESC, id");
				example.setLimit(1);
				project = processModelDao.selectByExample(example);
			}

			Boolean status = true;
			if (project != null && project.size() > 0) {
				ProcessModel modelP = project.get(0);
				projectid = modelP.getId().toString();
				name = modelP.getName();
				modelP.setStage(stageid);
				modelP.setStagestate(1);
				if (processModelDao.updateByPrimaryKey(modelP) > 0) {
					status = true;
				} else {
					status = false;
					json.addObject("option", "阶段和阶段状态未更新……");
				}
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", projectid);
			map.put("name", name);
			model.addAttribute("status", status);
			model.addAttribute("option", map);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("stageid: " + stageid);
			logger.error("pid: " + pid);
			logger.error("type: " + type);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectNextProcess end!");
		return json;
	}

	// by xiao
	@RequestMapping(params = "action=selectProcessConfigs", method = RequestMethod.POST)
	private ModelAndView selectProcessConfigs(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("processid") Long processid,
			@RequestParam(value = "moduleid", required = false, defaultValue = "-1") Integer moduleid,
			@RequestParam(value = "configid", required = false, defaultValue = "-1") Integer configid) {
		logger.debug("selectProcessConfigs start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ConfigValueModel value = new ConfigValueModel();
			value.setProcessId(processid);
			value.setModuleid(moduleid);
			value.setConfigId(configid);
			List<ConfigValueModel> configList = configValueModelDao.selectConfigsById(value);

			// TODO 需要修改获取config的方式 2018年9月21日 下午5:39:51
			// 数据库配置信息
			List<ConfigDBModel> dbList = configDBModelDao.selectDbInfos();
			// 将数据库id换成详细信息
			for (ConfigValueModel modelValue : configList) {
				if (modelValue.getName().endsWith("库")) {
					for (ConfigDBModel modelDb : dbList) {
						if (modelValue.getValue() != null && !modelValue.getValue().isEmpty() && (modelDb.getId() == Integer.valueOf(modelValue.getValue()))) {
							Integer dbtype = modelDb.getDbtype();
							String sdbtype = "";
							if (dbtype == 1) {
								sdbtype = "mysql";
							} else if (dbtype == 2) {
								sdbtype = "postgre";
							}

							String sDbInfo = String.format("DBTYPE=%s;DBSCHEMA=%s;DBNAME=%s;SERVER=%s;USER=%s;PWD=%s;PORT=%s;", sdbtype, modelDb.getDbschema(),
									modelDb.getDbname(), modelDb.getIp(), modelDb.getUser(), modelDb.getPassword(), modelDb.getPort());

							modelValue.setValue(sDbInfo);
							break;
						}
					}
				}
			}

			model.addAttribute("status", true);
			model.addAttribute("option", configList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProcessConfigs end!");
		return json;
	}

	@RequestMapping(params = "action=test", method = RequestMethod.POST)
	private ModelAndView test(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam("type") String type,
			@RequestParam("taskid") Long taskid,
			@RequestParam("time") String strTime) {
		logger.debug("START");
		InterfaceResultModel result = new InterfaceResultModel();
		try {} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setStatus(false);
			result.setOption(e.getMessage());
		}
		logger.debug("END");
		return new ModelAndView(new MappingJackson2JsonView()).addAllObjects(result);
	}
}