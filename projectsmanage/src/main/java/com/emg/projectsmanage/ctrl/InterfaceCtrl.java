package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
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
import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsTaskCountModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsTaskLogModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.pojo.DepartmentModel;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import com.emg.projectsmanage.pojo.ProjectModelExample.Criteria;
import com.emg.projectsmanage.pojo.MetadataModel;
import com.emg.projectsmanage.pojo.ProjectsTaskCountModel;
import com.emg.projectsmanage.pojo.ProjectsTaskLogModel;
import com.emg.projectsmanage.pojo.ProjectsUserModel;
import com.emg.projectsmanage.pojo.UserRoleModel;
import com.emg.projectsmanage.service.CommService;
import com.emg.projectsmanage.service.EmapgoAccountService;

@Controller
@RequestMapping("/interface.web")
public class InterfaceCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory
			.getLogger(InterfaceCtrl.class);

	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;
	@Autowired
	private CommService commService;
	@Autowired
	private UserRoleModelDao userRoleModelDao;
	@Autowired
	private EmapgoAccountService emapgoAccountService;
	@Autowired
	private ProjectsTaskLogModelDao projectsTaskLogModelDao;
	@Autowired
	private ProjectsTaskCountModelDao projectsTaskCountDao;

	@RequestMapping(params = "action=insertNewProject", method = RequestMethod.POST)
	private ModelAndView insertNewProject(Model model, HttpSession session,
			HttpServletRequest request,
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
			e.printStackTrace();
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		json.addObject("option", projectid);
		logger.debug("insertNewProject end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectByID", method = RequestMethod.POST)
	private ModelAndView selectProjectByID(Model model, HttpSession session,
			HttpServletRequest request, @RequestParam("pid") String projectID) {
		logger.debug("selectProjectByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModel projects = projectModelDao.selectByPrimaryKey(Long
					.valueOf(projectID));
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectByID end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectByProtype", method = RequestMethod.POST)
	private ModelAndView selectProjectByProtype(Model model,
			HttpSession session, HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("protype") Integer protype) {
		logger.debug("selectProjectByProtype start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid)
					.andProtypeEqualTo(protype);
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectByProtype end!");
		return json;
	}

	@RequestMapping(params = "action=selectTopProjects", method = RequestMethod.POST)
	private ModelAndView selectTopProjects(Model model, HttpSession session,
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
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectTopProjects end!");
		return json;
	}

	@RequestMapping(params = "action=selectUndoProjects", method = RequestMethod.POST)
	private ModelAndView selectUndoProjects(Model model, HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid) {
		logger.debug("selectUndoProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid).andOverstateLessThan(4);
			example.setOrderByClause("id desc");
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectUndoProjects end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectsByCreatetime", method = RequestMethod.POST)
	private ModelAndView selectProjectsByCreatetime(Model model,
			HttpSession session, HttpServletRequest request,
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
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectsByCreatetime end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectsByEndtime", method = RequestMethod.POST)
	private ModelAndView selectProjectsByEndtime(Model model,
			HttpSession session, HttpServletRequest request,
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
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectsByEndtime end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectsByStarttime", method = RequestMethod.POST)
	private ModelAndView selectProjectsByStarttime(Model model,
			HttpSession session, HttpServletRequest request,
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
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProjectsByStarttime end!");
		return json;
	}

	@RequestMapping(params = "action=selectAllProjects", method = RequestMethod.POST)
	private ModelAndView selectAllProjects(Model model, HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid) {
		logger.debug("selectAllProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid);
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectAllProjects end!");
		return json;
	}

	@RequestMapping(params = "action=selectCountOfProjects", method = RequestMethod.POST)
	private ModelAndView selectCountOfProjects(Model model,
			HttpSession session, HttpServletRequest request,
			@RequestParam("systemid") Integer systemid) {
		logger.debug("selectCountOfProjects start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andSystemidEqualTo(systemid);
			int count = projectModelDao.countByExample(example);
			model.addAttribute("status", true);
			model.addAttribute("option", count);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectCountOfProjects end!");
		return json;
	}

	@RequestMapping(params = "action=updateProjectTasknumByID", method = RequestMethod.POST)
	private ModelAndView updateProjectTasknumByID(Model model,
			HttpSession session, HttpServletRequest request,
			@RequestParam("pid") String projectID,
			@RequestParam("tasknum") Integer tasknum) {
		logger.debug("updateProjectTasknumByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {
			ProjectModel project = projectModelDao.selectByPrimaryKey(Long
					.valueOf(projectID));

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
			e.printStackTrace();
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("updateProjectTasknumByID end!");
		return json;
	}

	@RequestMapping(params = "action=updateProjectOverstateByID", method = RequestMethod.POST)
	private ModelAndView updateProjectOverstateByID(Model model,
			HttpSession session, HttpServletRequest request,
			@RequestParam("pid") String projectID,
			@RequestParam("overstate") Integer overstate) {
		logger.debug("updateProjectOverstateByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {
			ProjectModel project = projectModelDao.selectByPrimaryKey(Long
					.valueOf(projectID));
			project.setOverstate(overstate);
			if (projectModelDao.updateByPrimaryKey(project) > 0)
				status = true;
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("updateProjectOverstateByID end!");
		return json;
	}

	@RequestMapping(params = "action=submitTaskByID", method = RequestMethod.POST)
	private ModelAndView submitTaskByID(Model model, HttpSession session,
			HttpServletRequest request, @RequestParam("pid") String projectID,
			@RequestParam("type") Integer type) {
		logger.debug("submitTaskByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean status = false;
		try {
			status = false;
			json.addObject("option", "未知的操作类型");
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("submitTaskByID end!");
		return json;
	}

	@RequestMapping(params = "action=submitTaskStatus", method = RequestMethod.POST)
	private ModelAndView submitTaskStatus(Model model, HttpSession session,
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
		try {
			String username = new String();
			Integer roleid = RoleType.ROLE_WORKER.getValue();
			if (systemid.compareTo(332) == 0) {// 批处理工具平台
				username = "系统工具";
			} else {
				EmployeeModel record = new EmployeeModel();
				record.setId(userid);
				EmployeeModel employee = emapgoAccountService.getOneEmployee(record);
				username = employee.getRealname();

				roleid = processafter;
			}

			String rolename = roleid == RoleType.ROLE_WORKER.getValue() ? RoleType.ROLE_WORKER
					.getDes() : RoleType.ROLE_CHECKER.getDes();
			ProjectsTaskLogModel taskLog = new ProjectsTaskLogModel();
			taskLog.setSystemid(systemid);
			taskLog.setProjectid(projectid);
			taskLog.setTaskid(taskid);
			taskLog.setUserid(userid);
			taskLog.setRoleid(roleid);
			taskLog.setStatebefore(statebefore);
			taskLog.setProcessbefore(processbefore);
			taskLog.setStateafter(stateafter);
			taskLog.setProcessafter(processafter);

			// 判断是否第一次写入
			int count = projectsTaskLogModelDao.count(taskLog);
			// 写入tb_projects_task_log
			if (projectsTaskLogModelDao.insert(taskLog) > 0) {

				ProjectsTaskCountModel taskCount = new ProjectsTaskCountModel();
				taskCount.setSystemid(systemid);
				taskCount.setUserid(userid);
				taskCount.setUsername(username);
				taskCount.setRoleid(roleid);
				taskCount.setRolename(rolename);
				taskCount.setProjectid(projectid);
				// tb_projects_task_count查看是不是已经写入统计项了
				int _count = projectsTaskCountDao.count(taskCount);
				if (_count == 0) {
					taskCount.setTotaltask(0);
					taskCount.setEdittask(0);
					taskCount.setQctask(0);
					taskCount.setChecktask(0);
					taskCount.setCompletetask(0);
					projectsTaskCountDao.newProjectsProgress(taskCount);
				}

				if (count > 0) {// 非第一次进入下一阶段
					status = true;
				} else {// 第一次进入下一阶段
					if (systemid.compareTo(0) == 0) {// POI视频编辑平台
						if (roleid == 5 && statebefore == 0
								&& processbefore == 0 && stateafter == 1
								&& processafter == 5) {// (0,0) -> (1,5)
							if (projectsTaskCountDao.newTask2Edit(taskCount) > 0)
								status = true;
						} else if (roleid == 6 && statebefore == 3
								&& processbefore == 5 && stateafter == 1
								&& processafter == 6) {// (3,5) -> (1,6)
							if (projectsTaskCountDao.newTask2Check(taskCount) > 0)
								status = true;
						} else if (roleid == 5 && statebefore == 1
								&& processbefore == 5 && stateafter == 2
								&& processafter == 5) {// (1,5) -> (2,5)
							if (projectsTaskCountDao.Edit2QC(taskCount) > 0)
								status = true;
						} else if (roleid == 5 && statebefore == 1
								&& processbefore == 5 && stateafter == 3
								&& processafter == 5) {// (1,5) -> (3,5)
							if (projectsTaskCountDao.QC2Check(taskCount) > 0)
								status = true;
						} else if (roleid == 6 && statebefore == 1
								&& processbefore == 6 && stateafter == 3
								&& processafter == 6) {// (1,6) -> (3,6)
							if (projectsTaskCountDao.taskDone(taskCount) > 0) {
								ProjectsTaskLogModel projectsTaskLog = new ProjectsTaskLogModel();
								projectsTaskLog.setSystemid(systemid);
								projectsTaskLog.setProjectid(projectid);
								projectsTaskLog.setTaskid(taskid);
								projectsTaskLog.setRoleid(RoleType.ROLE_WORKER
										.getValue());
								projectsTaskLog.setStatebefore(1);
								projectsTaskLog.setProcessbefore(5);
								projectsTaskLog.setStateafter(3);
								projectsTaskLog.setProcessafter(5);
								List<ProjectsTaskLogModel> projectsTaskLogs = projectsTaskLogModelDao
										.select(projectsTaskLog);
								if (projectsTaskLogs != null
										&& projectsTaskLogs.size() > 0) {
									Integer editid = projectsTaskLogs.get(0)
											.getUserid();
									taskCount.setUserid(editid);
									taskCount.setRoleid(RoleType.ROLE_WORKER
											.getValue());
									if (projectsTaskCountDao
											.taskDone(taskCount) > 0) {
										Map<String, Object> map = new HashMap<String, Object>();
										map.put("systemid", systemid);
										map.put("projectid", projectid);
										List<ProjectsTaskCountModel> list = projectsTaskCountDao
												.getProjectsProgress(map);
										if (list != null && list.size() > 0) {
											ProjectsTaskCountModel projectsCount = list
													.get(0);
											if (projectsCount.getTotaltask()
													.equals(projectsCount
															.getCompletetask())) {
												ProjectModel project = projectModelDao
														.selectByPrimaryKey(Long
																.valueOf(projectid));
												project.setOverstate(4);
												projectModelDao
														.updateByPrimaryKey(project);
											}
											status = true;
										}
									}
								}
							}
						} else {
							status = true;
						}
					} else if (systemid.compareTo(349) == 0) {// 综合编辑平台
						if (roleid == 5 && statebefore == 0
								&& processbefore == 0 && stateafter == 1
								&& processafter == 5) {// (0,0) -> (1,5)
							if (projectsTaskCountDao.newTask2Edit(taskCount) > 0)
								status = true;
						} else if (roleid == 5 && statebefore == 1
								&& processbefore == 5 && stateafter == 3
								&& processafter == 5) {// (1,5) -> (3,5)
							if (projectsTaskCountDao.comTaskDone(taskCount) > 0) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("systemid", systemid);
								map.put("projectid", projectid);
								List<ProjectsTaskCountModel> list = projectsTaskCountDao
										.getProjectsProgress(map);
								if (list != null && list.size() > 0) {
									ProjectsTaskCountModel projectsCount = list
											.get(0);
									if (projectsCount.getTotaltask().compareTo(
											projectsCount.getCompletetask()) == 0) {
										ProjectModel project = projectModelDao
												.selectByPrimaryKey(Long
														.valueOf(projectid));
										project.setOverstate(4);
										projectModelDao
												.updateByPrimaryKey(project);
									}
									status = Boolean.valueOf(true);
								}
							}
						} else {
							status = true;
						}
					} else if (systemid.compareTo(332) == 0) {// 批处理工具平台
						if (statebefore == 12 && processbefore == 51
								&& stateafter == 11 && processafter == 52) {// (12,51)
																			// ->
																			// (11,52)
							if (projectsTaskCountDao.newTask2QC(taskCount) > 0)
								status = true;
						} else if (statebefore == 11 && processbefore == 52
								&& (stateafter == 2 || stateafter == 22)
								&& processafter == 52) {// (11,52) -> (2,52) 或者
														// (11,52) -> (22,52)
							if (projectsTaskCountDao.QCTaskDone(taskCount) > 0) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("systemid", systemid);
								map.put("projectid", projectid);
								List<ProjectsTaskCountModel> list = projectsTaskCountDao
										.getProjectsProgress(map);
								if (list != null && list.size() > 0) {
									ProjectsTaskCountModel projectsCount = list
											.get(0);
									if (projectsCount.getTotaltask().compareTo(
											projectsCount.getCompletetask()) == 0) {
										ProjectModel project = projectModelDao
												.selectByPrimaryKey(Long
														.valueOf(projectid));
										project.setOverstate(4);
										projectModelDao
												.updateByPrimaryKey(project);
									}
									status = Boolean.valueOf(true);
								}
							}
						} else {
							status = true;
						}
					} else {

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
			json.addObject("option", e.getMessage());
		}
		json.addObject("status", status);
		logger.debug("submitTaskStatus end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserInSystem", method = RequestMethod.POST)
	private ModelAndView checkUserInSystem(Model model, HttpSession session,
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
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserInSystem end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserRoleByID", method = RequestMethod.POST)
	private ModelAndView selectUserRoleByID(Model model, HttpSession session,
			HttpServletRequest request, @RequestParam("id") Integer id) {
		logger.debug("selectUserRoleByID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			UserRoleModel ru = new UserRoleModel();
			ru.setId(id);
			List<UserRoleModel> roleusers = userRoleModelDao.query(ru);
			json.addObject("status", true);
			json.addObject("option", roleusers);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectUserRoleByID end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserRoleByUserID", method = RequestMethod.POST)
	private ModelAndView selectUserRoleByUserID(Model model,
			HttpSession session, HttpServletRequest request,
			@RequestParam("userid") Integer userid) {
		logger.debug("selectUserRoleByUserID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			UserRoleModel ru = new UserRoleModel();
			ru.setUserid(userid);
			List<UserRoleModel> roleusers = userRoleModelDao.query(ru);
			json.addObject("status", true);
			json.addObject("option", roleusers);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserRoleByUserID end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserRoleByRoleID", method = RequestMethod.POST)
	private ModelAndView selectUserRoleByRoleID(Model model,
			HttpSession session, HttpServletRequest request,
			@RequestParam("roleid") Integer roleid) {
		logger.debug("selectUserRoleByRoleID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			UserRoleModel ru = new UserRoleModel();
			ru.setRoleid(roleid);
			List<UserRoleModel> roleusers = userRoleModelDao.query(ru);
			json.addObject("status", true);
			json.addObject("option", roleusers);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserRoleByRoleID end!");
		return json;
	}

	@RequestMapping(params = "action=selectAllUserRoles", method = RequestMethod.POST)
	private ModelAndView selectAllUserRoles(Model model, HttpSession session,
			HttpServletRequest request) {
		logger.debug("selectAllUserRoles start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<UserRoleModel> roleusers = userRoleModelDao.queryAll();
			json.addObject("status", true);
			json.addObject("option", roleusers);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectAllUserRoles end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserRoleCount", method = RequestMethod.POST)
	private ModelAndView selectUserRoleCount(Model model, HttpSession session,
			HttpServletRequest request) {
		logger.debug("selectUserRoleCount start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			int count = userRoleModelDao.queryCount();
			json.addObject("status", true);
			json.addObject("option", count);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserRoleCount end!");
		return json;
	}

	@RequestMapping(params = "action=selectNextProject", method = RequestMethod.POST)
	private ModelAndView selectNextProject(Model model, HttpSession session,
			HttpServletRequest request,
			@RequestParam("systemid") Integer systemid,
			@RequestParam("userid") Integer userid,
			@RequestParam("pid") String pid) {
		logger.debug("selectNextProject start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			String projectid = "-1";
			String name = new String();
			Integer tasknum = -1;
			
			ProjectsUserModel record = new ProjectsUserModel();
			record.setUserid(userid);
			List<ProjectsUserModel> projectsUserModels = projectsUserModelDao
					.queryProjectUsers(record);
			List<Long> myProjectIDs = new ArrayList<Long>();
			myProjectIDs.add(-1L);
			for (ProjectsUserModel projectsUserModel : projectsUserModels) {
				myProjectIDs.add(Long.valueOf(projectsUserModel.getPid()));
			}
			
			Integer priority = 9999;
			Integer owner = 1;
			if(!pid.equals("-1")){
				ProjectModel curProject = projectModelDao.selectByPrimaryKey(Long
						.valueOf(pid));
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
			criteria1.andSystemidEqualTo(systemid)
					.andOverstateEqualTo(1)
					.andOwnerEqualTo(owner)
					.andPriorityEqualTo(priority)
					.andIdGreaterThan(Long.valueOf(pid));
			Criteria criteria2 = example.or();
			criteria2.andSystemidEqualTo(systemid)
					.andOverstateEqualTo(1)
					.andOwnerEqualTo(owner)
					.andPriorityLessThan(priority);
			if(owner.equals(1) && myProjectIDs.size() > 0){
				criteria1.andIdIn(myProjectIDs);
				criteria2.andIdIn(myProjectIDs);
			}
			example.setOrderByClause("priority DESC, id");
			example.setLimit(1);
			List<ProjectModel> project = projectModelDao.selectByExample(example);
			if ((project == null || project.size() <= 0) && owner.equals(1) ) {
				example.clear();
				example.or().andSystemidEqualTo(systemid)
							.andOwnerEqualTo(0)
							.andOverstateEqualTo(1);
				example.setOrderByClause("priority DESC, id");
				example.setLimit(1);
				project = projectModelDao.selectByExample(example);
			}
			if(project != null && project.size() > 0){
				projectid = project.get(0).getId().toString();
				tasknum = project.get(0).getTasknum();
				name = project.get(0).getName();
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("projectid", projectid);
			map.put("tasknum", tasknum);
			map.put("name", name);
			model.addAttribute("status", true);
			model.addAttribute("option", map);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectNextProject end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserByUsername", method = RequestMethod.POST)
	private ModelAndView checkUserByUsername(Model model, HttpSession session,
			HttpServletRequest request,
			@RequestParam("username") String username) {
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
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserByUsername end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserByUsernameAndPassword", method = RequestMethod.POST)
	private ModelAndView checkUserByUsernameAndPassword(Model model,
			HttpSession session, HttpServletRequest request,
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
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserByUsernameAndPassword end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserByUsernameAndDepartment", method = RequestMethod.POST)
	private ModelAndView checkUserByUsernameAndDepartment(Model model,
			HttpSession session, HttpServletRequest request,
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
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserByUsernameAndDepartment end!");
		return json;
	}

	@RequestMapping(params = "action=checkUserByUsernameAndPasswordAndDepartment", method = RequestMethod.POST)
	private ModelAndView checkUserByUsernameAndPasswordAndDepartment(
			Model model, HttpSession session, HttpServletRequest request,
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
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("checkUserByUsernameAndPasswordAndDepartment end!");
		return json;
	}

	@RequestMapping(params = "action=selectAllUserInfos", method = RequestMethod.POST)
	private ModelAndView selectAllUserInfos(Model model, HttpSession session,
			HttpServletRequest request) {
		logger.debug("selectAllUserInfos start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<EmployeeModel> employees = emapgoAccountService.getAllEmployees();
			json.addObject("status", true);
			json.addObject("option", employees);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectAllUserInfos end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserInfoByUsername", method = RequestMethod.POST)
	private ModelAndView selectUserInfoByUsername(Model model,
			HttpSession session, HttpServletRequest request,
			@RequestParam("username") String username) {
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
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserInfoByUsername end!");
		return json;
	}

	@RequestMapping(params = "action=selectUserInfoByUserID", method = RequestMethod.POST)
	private ModelAndView selectUserInfoByUserID(Model model,
			HttpSession session, HttpServletRequest request,
			@RequestParam("userid") Integer userid) {
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
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectUserInfoByUsername end!");
		return json;
	}

	@RequestMapping(params = "action=selectAllDepartment", method = RequestMethod.POST)
	private ModelAndView selectAllDepartment(Model model, HttpSession session,
			HttpServletRequest request) {
		logger.debug("selectAllDepartment start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<DepartmentModel> departments = emapgoAccountService
					.getAllDepartment();
			if (departments != null && departments.size() > 0) {
				json.addObject("status", true);
				json.addObject("departments", departments);
			} else {
				json.addObject("status", true);
				json.addObject("option", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectAllDepartment end!");
		return json;
	}

	@RequestMapping(params = "action=selectProjectByName", method = RequestMethod.POST)
	private ModelAndView selectProjectByName(Model model, HttpSession session,
			HttpServletRequest request, @RequestParam("name") String name) {
		logger.debug("selectProjectByName start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.or().andNameEqualTo(name);
			int ret = projectModelDao.countByExample(example);
			if (ret > 0) {
				json.addObject("status", true);
				json.addObject("option", true);
			} else {
				json.addObject("status", true);
				json.addObject("option", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}

		logger.debug("selectProjectByName end!");
		return json;
	}

	@RequestMapping(params = "action=QCUndoProjects", method = RequestMethod.POST)
	private ModelAndView QCUndoProjects(Model model, HttpSession session,
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
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			json.addObject("status", true);
			json.addObject("option", projects);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("QCUndoProjects end!");
		return json;
	}

	@RequestMapping(params = "action=QCUndoProjectIDs", method = RequestMethod.POST)
	private ModelAndView QCUndoProjectIDs(Model model, HttpSession session,
			HttpServletRequest request) {
		logger.debug("QCUndoProjectIDs start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			String ids = new String();
			ProjectModelExample example = new ProjectModelExample();
			example.or().andOverstateEqualTo(1);
			example.setOrderByClause("priority desc, id");
			List<ProjectModel> projects = projectModelDao
					.selectByExample(example);
			if (projects.size() > 0) {
				for (ProjectModel project : projects) {
					ids += project.getId() + ",";
				}
				ids = ids.substring(0, ids.length() - 1);
			}
			json.addObject("status", true);
			json.addObject("option", ids);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("QCUndoProjectIDs end!");
		return json;
	}
	
	@RequestMapping(params = "action=test", method = RequestMethod.POST)
	private ModelAndView test(Model model, HttpSession session,
			HttpServletRequest request,
			@RequestParam("key") String key,
			@RequestParam("value") String value) {
		logger.debug("test start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<MetadataModel> list = commService.getDifficuties();
			json.addObject("status", true);
			json.addObject("option", list);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("test end!");
		return json;
	}
}
