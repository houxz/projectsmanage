package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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
import com.emg.projectsmanage.common.OwnerStatus;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.common.SystemType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsTaskCountModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsTaskLogModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ConfigValueModel;
import com.emg.projectsmanage.pojo.DepartmentModel;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import com.emg.projectsmanage.pojo.ProjectModelExample.Criteria;
import com.emg.projectsmanage.pojo.ProjectsTaskCountModel;
import com.emg.projectsmanage.pojo.ProjectsTaskLogModel;
import com.emg.projectsmanage.pojo.ProjectsUserModel;
import com.emg.projectsmanage.pojo.UserRoleModel;
import com.emg.projectsmanage.service.EmapgoAccountService;

import com.emg.projectsmanage.library.JNATest;
import com.sun.jna.WString;

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
	private ProjectsTaskLogModelDao projectsTaskLogModelDao;
	@Autowired
	private ProjectsTaskCountModelDao projectsTaskCountDao;
	@Autowired
	private ProcessModelDao processModelDao;
	@Autowired
	private ProcessConfigModelDao processConfigModelDao;
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
		try {
			String username = new String();
			RoleType roleType = RoleType.UNKNOW;
			if (systemid.compareTo(SystemType.DBMapChecker.getValue()) == 0) {// 批处理工具平台
				username = "系统工具";
			} else {
				EmployeeModel record = new EmployeeModel();
				record.setId(userid);
				EmployeeModel employee = emapgoAccountService.getOneEmployee(record);
				username = employee.getRealname();

				if (processafter.equals(52)) {
					roleType = RoleType.ROLE_WORKER;
				} else {
					roleType = RoleType.valueOf(processafter);
				}
			}

			String rolename = roleType.getDes();
			ProjectsTaskLogModel taskLog = new ProjectsTaskLogModel();
			taskLog.setSystemid(systemid);
			taskLog.setProjectid(projectid);
			taskLog.setTaskid(taskid);
			taskLog.setUserid(userid);
			taskLog.setRoleid(roleType.getValue());
			taskLog.setStatebefore(statebefore == null ? 0 : statebefore);
			taskLog.setProcessbefore(processbefore == null ? 0 : processbefore);
			taskLog.setStateafter(stateafter == null ? 0 : stateafter);
			taskLog.setProcessafter(processafter == null ? 0 : processafter);

			// 判断是否第一次写入
			int count = projectsTaskLogModelDao.count(taskLog);
			// 写入tb_projects_task_log
			if (projectsTaskLogModelDao.insert(taskLog) > 0) {

				ProjectsTaskCountModel taskCount = new ProjectsTaskCountModel();
				taskCount.setSystemid(systemid);
				taskCount.setUserid(userid);
				taskCount.setUsername(username);
				taskCount.setRoleid(roleType.getValue());
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
					try {
						projectsTaskCountDao.newProjectsProgress(taskCount);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}

				if (count > 0) {// 非第一次进入下一阶段
					status = true;
				} else {// 第一次进入下一阶段
					if (systemid.compareTo(0) == 0) {// POI视频编辑平台
						if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 0 && processbefore == 0 && stateafter == 1 && processafter == 5) {// (0,0)
																															// ->
																															// (1,5)
							if (projectsTaskCountDao.newTask2Edit(taskCount) > 0)
								status = true;
						} else if (roleType.equals(RoleType.ROLE_CHECKER) && statebefore == 3 && processbefore == 5 && stateafter == 1 && processafter == 6) {// (3,5)
																																	// ->
																																	// (1,6)
							if (projectsTaskCountDao.newTask2Check(taskCount) > 0)
								status = true;
						} else if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 1 && processbefore == 5 && stateafter == 2 && processafter == 5) {// (1,5)
																																	// ->
																																	// (2,5)
							if (projectsTaskCountDao.Edit2QC(taskCount) > 0)
								status = true;
						} else if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 1 && processbefore == 5 && stateafter == 3 && processafter == 5) {// (1,5)
																																	// ->
																																	// (3,5)
							if (projectsTaskCountDao.QC2Check(taskCount) > 0)
								status = true;
						} else if (roleType.equals(RoleType.ROLE_CHECKER) && statebefore == 1 && processbefore == 6 && stateafter == 3 && processafter == 6) {// (1,6)
																																	// ->
																																	// (3,6)
							if (projectsTaskCountDao.taskDone(taskCount) > 0) {
								ProjectsTaskLogModel projectsTaskLog = new ProjectsTaskLogModel();
								projectsTaskLog.setSystemid(systemid);
								projectsTaskLog.setProjectid(projectid);
								projectsTaskLog.setTaskid(taskid);
								projectsTaskLog.setRoleid(RoleType.ROLE_WORKER.getValue());
								projectsTaskLog.setStatebefore(1);
								projectsTaskLog.setProcessbefore(5);
								projectsTaskLog.setStateafter(3);
								projectsTaskLog.setProcessafter(5);
								List<ProjectsTaskLogModel> projectsTaskLogs = projectsTaskLogModelDao.select(projectsTaskLog);
								if (projectsTaskLogs != null && projectsTaskLogs.size() > 0) {
									Integer editid = projectsTaskLogs.get(0).getUserid();
									taskCount.setUserid(editid);
									taskCount.setRoleid(RoleType.ROLE_WORKER.getValue());
									if (projectsTaskCountDao.taskDone(taskCount) > 0) {
										Map<String, Object> map = new HashMap<String, Object>();
										map.put("systemid", systemid);
										map.put("projectid", projectid);
										List<ProjectsTaskCountModel> list = projectsTaskCountDao.getProjectsProgress(map);
										if (list != null && list.size() > 0) {
											ProjectsTaskCountModel projectsCount = list.get(0);
											if (projectsCount.getTotaltask().equals(projectsCount.getCompletetask())) {
												ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
												project.setOverstate(4);
												projectModelDao.updateByPrimaryKey(project);
											}
											status = true;
										}
									}
								}
							}
						} else {
							status = true;
						}
					} else if (systemid.compareTo(SystemType.MapDbEdit.getValue()) == 0) {// 综合编辑平台
						if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 0 && processbefore == 0 && stateafter == 1 && processafter == 5) {// (0,0)
																															// ->
																															// (1,5)
							if (projectsTaskCountDao.newTask2Edit(taskCount) > 0)
								status = true;
						} else if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 1 && processbefore == 5 && stateafter == 3 && processafter == 5) {// (1,5)
																																	// ->
																																	// (3,5)
							if (projectsTaskCountDao.comTaskDone(taskCount) > 0) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("systemid", systemid);
								map.put("projectid", projectid);
								List<ProjectsTaskCountModel> list = projectsTaskCountDao.getProjectsProgress(map);
								if (list != null && list.size() > 0) {
									ProjectsTaskCountModel projectsCount = list.get(0);

									// by xiao 计算任务完成进度
									Integer totalTask = projectsCount.getTotaltask();
									Integer completeTask = projectsCount.getCompletetask();
									Double progress = ((double) completeTask / (double) totalTask) * 100;

									// by xiao 更新流程进度
									// 项目完成时，修改其关联的流程任务的阶段、阶段状态、流程状态
									// 先找到该项目关联的所有流程任务
									ProcessModel process;
									ConfigValueModel valuemodel = new ConfigValueModel();
									valuemodel.setName("%项目id");
									valuemodel.setValue(projectid.toString());
									valuemodel.setModuleid(2);
									List<ConfigValueModel> valueList = configValueModelDao.selectProcessIdByConfig(valuemodel);
									if (valueList.size() > 0) {
										for (ConfigValueModel value : valueList) {
											Long idProcess = value.getProcessId();
											process = processModelDao.selectByPrimaryKey(idProcess);
											String sProgress = process.getProgress();
											if (sProgress.length() > 0) {
												String[] arProgress = sProgress.split(",");
												ArrayList<String> alProgress = new ArrayList<String>(Arrays.asList(arProgress));
												Integer length = alProgress.size();
												while (length < CommonConstants.PROCESSCOUNT_ERROR) {
													alProgress.add("0");
													length++;
												}
												// X,X,X,X
												// 质检进度在第四个X
												alProgress.set(3, String.format("%.3f", progress));

												StringBuilder sbProgress = new StringBuilder();
												for (String p : alProgress) {
													sbProgress.append(p);
													sbProgress.append(",");
												}
												sbProgress.deleteCharAt(sbProgress.length() - 1);

												process.setProgress(sbProgress.toString());
												if (totalTask.compareTo(completeTask) == 0) {
													// 更新流程状态和阶段状态
													process.setState(3); // 流程完成
													process.setStagestate(3); // 阶段完成
												}
												processModelDao.updateByPrimaryKey(process);
											}
										}
									}
									// by xiao 计算任务完成进度 end

									if (totalTask.compareTo(completeTask) == 0) {
										ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
										project.setOverstate(4); // 项目完成
										projectModelDao.updateByPrimaryKey(project);
									}
									status = Boolean.valueOf(true);
								}
							}
						} else {
							status = true;
						}
					} else if (systemid.compareTo(SystemType.MapDbEdit_NRFC.getValue()) == 0) {
						if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 0 && processbefore == 0 && stateafter == 1 && processafter == 5) {// (0,0)->(1,5)
							if (projectsTaskCountDao.newTask2Edit(taskCount) > 0)
								status = true;
						} else if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 1 && processbefore == 5 && stateafter == 3 && processafter == 5) {// (1,5)->(3,5)
							if (projectsTaskCountDao.comTaskDone(taskCount) > 0) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("systemid", systemid);
								map.put("projectid", projectid);
								List<ProjectsTaskCountModel> list = projectsTaskCountDao.getProjectsProgress(map);
								if (list != null && list.size() > 0) {
									ProjectsTaskCountModel projectsCount = list.get(0);

									// by xiao 计算任务完成进度
									Integer totalTask = projectsCount.getTotaltask();
									if (totalTask <= 0) {
										json.addObject("status", false);
										json.addObject("option", "任务总数未更新");
										return json;
									}
									Integer completeTask = projectsCount.getCompletetask();
									Double progress = ((double) completeTask / (double) totalTask) * 100;

									// by xiao 更新流程进度
									// 项目完成时，修改其关联的流程任务的阶段、阶段状态、流程状态
									// 先找到该项目关联的所有流程任务
									ProcessModel process;
									ConfigValueModel valuemodel = new ConfigValueModel();
									valuemodel.setName("%项目id");
									valuemodel.setValue(projectid.toString());
									valuemodel.setModuleid(2);
									List<ConfigValueModel> valueList = configValueModelDao.selectProcessIdByConfig(valuemodel);
									if (valueList.size() > 0) {
										for (ConfigValueModel value : valueList) {
											Long idProcess = value.getProcessId();
											process = processModelDao.selectByPrimaryKey(idProcess);
											String sProgress = process.getProgress();
											if (sProgress.length() > 0) {
												String[] arProgress = sProgress.split(",");
												ArrayList<String> alProgress = new ArrayList<String>(Arrays.asList(arProgress));
												Integer length = alProgress.size();
												while (length < CommonConstants.PROCESSCOUNT_NRFC) {
													alProgress.add("0");
													length++;
												}
												// X,X,X,X
												// 质检进度在第二个X
												alProgress.set(1, String.format("%.3f", progress));

												StringBuilder sbProgress = new StringBuilder();
												for (String p : alProgress) {
													sbProgress.append(p);
													sbProgress.append(",");
												}
												sbProgress.deleteCharAt(sbProgress.length() - 1);

												process.setProgress(sbProgress.toString());
												if (totalTask.compareTo(completeTask) == 0) {
													// 更新流程状态和阶段状态
													process.setStage(3);
													process.setStagestate(1); // 阶段完成
												}
												processModelDao.updateByPrimaryKey(process);
											}
										}
									}
									// by xiao 计算任务完成进度 end

									if (totalTask.compareTo(projectsCount.getCompletetask()) == 0) {
										ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
										project.setOverstate(4);
										projectModelDao.updateByPrimaryKey(project);
									}
									status = Boolean.valueOf(true);
								}
							}
						}
					} else if (systemid.compareTo(SystemType.MapDbEdit_Country.getValue()) == 0) {// 全国质检
						if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 0 && processbefore == 0 && stateafter == 1 && processafter == 5) {// (0,0)
							// ->
							// (1,5)
							if (projectsTaskCountDao.newTask2Edit(taskCount) > 0)
								status = true;
						} else if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 1 && processbefore == 5 && stateafter == 3 && processafter == 5) {// (1,5)
							// ->
							// (3,5)
							if (projectsTaskCountDao.comTaskDone(taskCount) > 0) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("systemid", systemid);
								map.put("projectid", projectid);
								List<ProjectsTaskCountModel> list = projectsTaskCountDao.getProjectsProgress(map);
								if (list != null && list.size() > 0) {
									ProjectsTaskCountModel projectsCount = list.get(0);

									// by xiao 计算任务完成进度
									Integer totalTask = projectsCount.getTotaltask();
									Integer completeTask = projectsCount.getCompletetask();
									// Double progress = ((double) completeTask
									// / (double) totalTask) * 100;

									// by xiao 更新流程进度
									// 项目完成时，修改其关联的流程任务的阶段、阶段状态、流程状态
									// 先找到该项目关联的所有流程任务
									ProcessModel process;
									ConfigValueModel valuemodel = new ConfigValueModel();
									valuemodel.setName("%项目id");
									valuemodel.setValue(projectid.toString());
									valuemodel.setModuleid(2);
									List<ConfigValueModel> valueList = configValueModelDao.selectProcessIdByConfig(valuemodel);
									if (valueList.size() > 0) {
										for (ConfigValueModel value : valueList) {
											Long idProcess = value.getProcessId();
											process = processModelDao.selectByPrimaryKey(idProcess);
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
												// 质检进度在第四个X
												// alProgress.set(3,
												// String.format("%.3f",
												// progress));

												StringBuilder sbProgress = new StringBuilder();
												for (String p : alProgress) {
													sbProgress.append(p);
													sbProgress.append(",");
												}
												sbProgress.deleteCharAt(sbProgress.length() - 1);

												process.setProgress(sbProgress.toString());
												if (totalTask.compareTo(completeTask) == 0) {
													// 更新流程状态和阶段状态
													process.setState(2); // 流程完成
													process.setStagestate(3); // 阶段完成
												}
												processModelDao.updateByPrimaryKey(process);
											}
										}
									}
									// by xiao 计算任务完成进度 end

									if (totalTask.compareTo(completeTask) == 0) {
										ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
										project.setOverstate(4); // 项目完成
										projectModelDao.updateByPrimaryKey(project);
									}
									status = Boolean.valueOf(true);
								}
							}
						} else {
							status = true;
						}
					} else if (systemid.compareTo(SystemType.MapDbEdit_Attach.getValue()) == 0) {
						if (roleType.equals(RoleType.ROLE_WORKER) && statebefore == 0 && processbefore == 0 && stateafter == 1 && processafter == 5) {// (0,0)->(1,5)
							if (projectsTaskCountDao.newTask2Edit(taskCount) > 0)
								status = true;
						} else if (roleType.equals(RoleType.ROLE_CHECKER) && statebefore == 1 && processbefore == 6 && stateafter == 3 && processafter == 6) {// (1,6)->(3,6)

							logger.debug("submitTaskStatus begin!");
							if (projectsTaskCountDao.comTaskDone(taskCount) > 0) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("systemid", systemid);
								map.put("projectid", projectid);
								List<ProjectsTaskCountModel> list = projectsTaskCountDao.getProjectsProgressAttach(map);
								if (list != null && list.size() > 0) {
									ProjectsTaskCountModel projectsCount = list.get(0);

									Integer totalTask = projectsCount.getTotaltask();
									if (totalTask <= 0) {
										json.addObject("status", false);
										json.addObject("option", "任务总数未更新");
										return json;
									}
									Integer completeTask = projectsCount.getCompletetask();
									Double progress = ((double) completeTask / (double) totalTask) * 100;

									// 项目完成时，修改其关联的流程任务的阶段、阶段状态、流程状态
									// 先找到该项目关联的所有流程任务
									ProcessModel process;
									ConfigValueModel valuemodel = new ConfigValueModel();
									valuemodel.setName("%项目id");
									valuemodel.setValue(projectid.toString());
									valuemodel.setModuleid(2);
									List<ConfigValueModel> valueList = configValueModelDao.selectProcessIdByConfig(valuemodel);
									if (valueList.size() > 0) {
										for (ConfigValueModel value : valueList) {
											Long idProcess = value.getProcessId();
											process = processModelDao.selectByPrimaryKey(idProcess);
											String sProgress = process.getProgress();
											if (sProgress.length() > 0) {
												String[] arProgress = sProgress.split(",");
												ArrayList<String> alProgress = new ArrayList<String>(Arrays.asList(arProgress));
												Integer length = alProgress.size();
												while (length < CommonConstants.PROCESSCOUNT_NRFC) {
													alProgress.add("0");
													length++;
												}
												// X,X,X,X
												// 质检进度在第二个X
												alProgress.set(1, String.format("%.3f", progress));

												StringBuilder sbProgress = new StringBuilder();
												for (String p : alProgress) {
													sbProgress.append(p);
													sbProgress.append(",");
												}
												sbProgress.deleteCharAt(sbProgress.length() - 1);

												process.setProgress(sbProgress.toString());
												if (totalTask.compareTo(completeTask) == 0) {
													// 更新流程状态和阶段状态
													process.setStage(3);
													process.setStagestate(1); // 阶段完成
												}
												processModelDao.updateByPrimaryKey(process);
											}
										}
									}
									// by xiao 计算任务完成进度 end

									if (totalTask.compareTo(projectsCount.getCompletetask()) == 0) {
										ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
										project.setOverstate(4);
										projectModelDao.updateByPrimaryKey(project);
									}
									status = Boolean.valueOf(true);
								}
							}
						}
					} else if (systemid.compareTo(SystemType.DBMapChecker.getValue()) == 0) {// 批处理工具平台，质检平台
						if (statebefore == 12 && processbefore == 51 && stateafter == 11 && processafter == 52) {// (12,51)
																													// ->
																													// (11,52)
							if (projectsTaskCountDao.newTask2QC(taskCount) > 0)
								status = true;
						} else if (statebefore == 11 && processbefore == 52 && stateafter == 2 && processafter == 52) {// (11,52)
																														// ->
																														// (2,52)
							if (projectsTaskCountDao.QCTaskDone(taskCount) > 0) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("systemid", systemid);
								map.put("projectid", projectid);
								List<ProjectsTaskCountModel> list = projectsTaskCountDao.getProjectsProgress(map);
								if (list != null && list.size() > 0) {
									ProjectsTaskCountModel projectsCount = list.get(0);

									// by xiao 计算任务完成进度
									Integer totalTask = projectsCount.getTotaltask();
									if (totalTask <= 0) {
										json.addObject("status", false);
										json.addObject("option", "任务总数未更新");
										return json;
									}
									Integer completeTask = projectsCount.getCompletetask();
									Double progress = ((double) completeTask / (double) totalTask) * 100;

									// by xiao 更新流程进度
									// 项目完成时，修改其关联的流程任务的阶段、阶段状态、流程状态
									// 先找到该项目关联的所有流程任务
									ProcessModel process;
									ConfigValueModel valuemodel = new ConfigValueModel();
									valuemodel.setName("%项目id");
									valuemodel.setValue(projectid.toString());
									valuemodel.setModuleid(1);
									List<ConfigValueModel> valueList = configValueModelDao.selectProcessIdByConfig(valuemodel);
									if (valueList.size() > 0) {
										for (ConfigValueModel value : valueList) {
											Long idProcess = value.getProcessId();
											process = processModelDao.selectByPrimaryKey(idProcess);
											String sProgress = process.getProgress();
											if (sProgress.length() > 0) {
												String[] arProgress = sProgress.split(",");
												ArrayList<String> alProgress = new ArrayList<String>(Arrays.asList(arProgress));
												Integer length = alProgress.size();
												while (length < CommonConstants.PROCESSCOUNT_ERROR) {
													alProgress.add("0");
													length++;
												}
												// X,X,X,X
												// 质检进度在第二个X
												alProgress.set(1, String.format("%.3f", progress));

												StringBuilder sbProgress = new StringBuilder();
												for (String p : alProgress) {
													sbProgress.append(p);
													sbProgress.append(",");
												}
												sbProgress.deleteCharAt(sbProgress.length() - 1);

												process.setProgress(sbProgress.toString());
												if (totalTask.compareTo(completeTask) == 0) {
													// 更新流程状态和阶段状态
													process.setStagestate(3); // 阶段完成
												}
												processModelDao.updateByPrimaryKey(process);
											}
										}
									}
									// by xiao 计算任务完成进度 end

									if (totalTask.compareTo(projectsCount.getCompletetask()) == 0) {
										ProjectModel project = projectModelDao.selectByPrimaryKey(Long.valueOf(projectid));
										project.setOverstate(4);
										projectModelDao.updateByPrimaryKey(project);
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

	@RequestMapping(params = "action=selectNextProject", method = RequestMethod.POST)
	private ModelAndView selectNextProject(Model model,
			HttpSession session,
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
			Long processid = -1L;

			ProjectsUserModel record = new ProjectsUserModel();
			record.setUserid(userid);
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
				logger.warn("ZSEN -> " + departments.size());
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
			@RequestParam(value = "state", required = false, defaultValue = "0") Integer newProcessState) {
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
			}
			
			ProcessModel newProcess = new ProcessModel();
			newProcess.setName(newProcessName);
			newProcess.setType(newProcessType);
			newProcess.setPriority(newProcessPriority);
			newProcess.setState(newProcessState);
			newProcess.setUserid(0);
			newProcess.setUsername("系统工具");
			if(newProcessType.equals(ProcessType.POIEDIT.getValue())) {
				newProcess.setProgress("0,0");
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
			newpro.setProtype(0);
			newpro.setPdifficulty(0);
			newpro.setTasknum(-1);
			newpro.setOverstate(newProcessState);
			newpro.setCreateby(0);
			newpro.setPriority(newProcessPriority);
			newpro.setOwner(OwnerStatus.PUBLIC.getValue());
			
			if (projectModelDao.insert(newpro) <= 0) {
				json.addObject("result", false);
				json.addObject("resultMsg", "新建流程失败");
				return json;
			}
			Long projectid = newpro.getId();
			
			List<ProcessConfigValueModel> configValues = new ArrayList<ProcessConfigValueModel>();

			configValues.add(new ProcessConfigValueModel(newProcessID, 2, 11, projectid.toString()));
			configValues.add(new ProcessConfigValueModel(newProcessID, 2, 12, projectName));
			
			List<ProcessConfigModel> processConfigs = processConfigModelDao.selectAllProcessConfigModels(newProcessType);
			for (ProcessConfigModel processConfig : processConfigs) {
				Integer moduleid = processConfig.getModuleid();
				Integer configid = processConfig.getId();
				String defaultValue = processConfig.getDefaultValue() == null ? new String() : processConfig.getDefaultValue().toString();

				if ((moduleid.equals(1) && configid.equals(3)) || (moduleid.equals(1) && configid.equals(4)) || (moduleid.equals(2) && configid.equals(11))
						|| (moduleid.equals(2) && configid.equals(12)))
					continue;

				Enumeration<String> paramNames = request.getParameterNames();
				while (paramNames.hasMoreElements()) {
					String paramName = paramNames.nextElement();
					if (!paramName.equals("config_" + moduleid + "_" + configid))
						continue;
					defaultValue = ParamUtils.getParameter(request, paramName);
				}

				configValues.add(new ProcessConfigValueModel(newProcessID, moduleid, configid, defaultValue));
			}

			if (processConfigValueModelDao.deleteByProcessID(newProcessID) >= 0) {
				processConfigValueModelDao.insert(configValues);
			}
			
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

	@RequestMapping(params = "action=selectProcessIDByProjectID", method = RequestMethod.POST)
	private ModelAndView selectProcessIDByProjectID(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("moduleid") Integer moduleid,
			@RequestParam("projectid") Long projectid) {
		logger.debug("selectProcessIDByProjectID start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ConfigValueModel config = new ConfigValueModel();
			config.setModuleid(moduleid);
			config.setValue(projectid.toString());
			List<ConfigValueModel> configs = configValueModelDao.selectConfigs(config);
			if (configs != null && configs.size() > 0) {
				model.addAttribute("status", true);
				model.addAttribute("option", configs.get(0).getProcessId());
				return json;
			}
			model.addAttribute("status", false);
			model.addAttribute("option", new String());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("selectProcessIDByProjectID end!");
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
						process.setStagestate(3); // 阶段进度为100时，自动将该阶段的状态设置为完成

						// 若下一阶段为自动开启状态，则直接设置为下一阶段开启
						if (stage < CommonConstants.PROCESSCOUNT_ERROR) {
							ConfigValueModel modelConfig = new ConfigValueModel();
							Integer module = 0;
							if (stage == 1) { // 当前为第一阶段时，若完成，自动开始的第二阶段也属于模块1
								module = 1;
							} else if (stage == 2 || stage == 3) { // 当前为第二阶段时，若完成，自动开始的第三杰顿属于模块2
								module = 2;
							}

							modelConfig.setModuleid(module);
							modelConfig.setName("%启动类型");
							modelConfig.setProcessId(processID);
							ConfigValueModel valueModel = configValueModelDao.selectValueByConfig(modelConfig);
							if (Integer.valueOf(valueModel.getValue()) == 2) { // 自动
								process.setStage(stage + 1); // 自动设置为下一阶段开始
								process.setStagestate(1);
								stageStart = process.getStage();
							}
							modelConfig.setName("%项目id");
							valueModel = configValueModelDao.selectValueByConfig(modelConfig);
							projectid = Integer.valueOf(valueModel.getValue());
						} else if (stage == CommonConstants.PROCESSCOUNT_ERROR) {
							process.setState(ProcessState.COMPLETE.getValue());
						}
					}
					if (processModelDao.updateByPrimaryKey(process) > 0) {
						status = true;

						// 质检准备完成时，自动开启质检项目；
						// 改错准备完成时，自动开启改错项目；
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
						process.setStagestate(3); // 阶段进度为100时，自动将该阶段的状态设置为完成

						// 若下一阶段为自动开启状态，则直接设置为下一阶段开启
						if (stage < CommonConstants.PROCESSCOUNT_NRFC) {
							ConfigValueModel modelConfig = new ConfigValueModel();
							// NR/FC项目默认只有编辑阶段，启动类型只考虑编辑阶段
							Integer module = 2;
							modelConfig.setModuleid(module);
							modelConfig.setName("%启动类型");
							modelConfig.setProcessId(processID);
							ConfigValueModel valueModel = configValueModelDao.selectValueByConfig(modelConfig);
							if (Integer.valueOf(valueModel.getValue()) == 2) { // 自动
								process.setStage(stage + 1); // 自动设置为下一阶段开始
								process.setStagestate(1);
								stageStart = process.getStage();
							}
							modelConfig.setName("%项目id");
							valueModel = configValueModelDao.selectValueByConfig(modelConfig);
							projectid = Integer.valueOf(valueModel.getValue());
						} else if (stage == CommonConstants.PROCESSCOUNT_NRFC) {
							process.setState(ProcessState.COMPLETE.getValue());
						}
					}
					if (processModelDao.updateByPrimaryKey(process) > 0) {
						status = true;

						// 改错准备完成时，自动开启改错项目；
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
						process.setStagestate(3); // 阶段进度为100时，自动将该阶段的状态设置为完成

						// 若下一阶段为自动开启状态，则直接设置为下一阶段开启
						if (stage < CommonConstants.PROCESSCOUNT_COUNTRY) {
							ConfigValueModel modelConfig = new ConfigValueModel();
							Integer module = 0;
							if (stage == 1) { // 当前为第一阶段时，若完成，自动开始的第二阶段也属于模块1
								module = 1;
							} else if (stage == 2) { // 当前为第二阶段时，若完成，自动开始的第三杰顿属于模块2
								module = 2;
							}

							modelConfig.setModuleid(module);
							modelConfig.setName("%启动类型");
							modelConfig.setProcessId(processID);
							ConfigValueModel valueModel = configValueModelDao.selectValueByConfig(modelConfig);
							if (Integer.valueOf(valueModel.getValue()) == 2) { // 自动
								process.setStage(stage + 1); // 自动设置为下一阶段开始
								process.setStagestate(1);
								stageStart = process.getStage();
							}
							modelConfig.setName("%项目id");
							valueModel = configValueModelDao.selectValueByConfig(modelConfig);
							projectid = Integer.valueOf(valueModel.getValue());
						} else if (stage == CommonConstants.PROCESSCOUNT_COUNTRY) {
							process.setState(ProcessState.COMPLETE.getValue());
						}
					}
					if (processModelDao.updateByPrimaryKey(process) > 0) {
						status = true;

						// 质检准备完成时，自动开启质检项目；
						// 改错准备完成时，自动开启改错项目；
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
			@RequestParam("moduleid") Integer moduleid) {
		logger.debug("selectProcessConfigs start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ConfigValueModel value = new ConfigValueModel();
			value.setProcessId(processid);
			if (moduleid != null && moduleid.compareTo(0) > 0) {
				value.setModuleid(moduleid);
			}
			List<ConfigValueModel> configList = configValueModelDao.selectConfigsById(value);

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
	private ModelAndView test(Model model, HttpSession session, HttpServletRequest request, @RequestParam("key") String key, @RequestParam("value") String value) {
		logger.debug("test start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			JNATest.Instance.say(new WString("Hello World!"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("status", false);
			json.addObject("option", e.getMessage());
		}
		logger.debug("test end!");
		return json;
	}
}
