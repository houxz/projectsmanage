package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
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
import com.emg.projectsmanage.common.PriorityLevel;
import com.emg.projectsmanage.common.ProcessConfigEnum;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.TaskTypeEnum;
import com.emg.projectsmanage.common.StateMap;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.task.TaskModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;
import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import com.emg.projectsmanage.pojo.TaskModel;
import com.emg.projectsmanage.service.EmapgoAccountService;
import com.emg.projectsmanage.service.ProcessConfigModelService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/tasksmanage.web")
public class TasksManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(TasksManageCtrl.class);

	@Autowired
	private TaskModelDao taskModelDao;

	@Autowired
	private ProcessConfigModelService processConfigModelService;

	@Autowired
	private ConfigDBModelDao configDBModelDao;

	@Autowired
	private ProjectModelDao projectModelDao;

	@Autowired
	private ProcessModelDao processModelDao;
	
	@Autowired
	private EmapgoAccountService emapgoAccountService;
	
	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		model.addAttribute("processTypes", ProcessType.toJsonStr());
		model.addAttribute("taskTypes", TaskTypeEnum.toJsonStr());	
		model.addAttribute("priorityLevels", PriorityLevel.toJsonStr());
		return "tasksmanage";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			TaskModel record = new TaskModel();
			ProcessType processType = ProcessType.ATTACH;

			Map<String, Object> filterPara = null;
			List<Long> projectids = null;
			List<ProjectModel> projects = null;
			List<Integer> editUserids = new ArrayList<Integer>();
			List<Integer> checkUserids = new ArrayList<Integer>();
			ProjectModelExample example = new ProjectModelExample();
			List<StateMap> stateMaps = new ArrayList<StateMap>();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				processType = ProcessType.valueOf(Integer.valueOf(filterPara.get("processtype").toString()));
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "processid":
						Long processid = Long.valueOf(filterPara.get(key).toString());
						example.clear();
						example.or().andProcessidEqualTo(processid);
						projects = new ArrayList<ProjectModel>();
						projects.addAll(projectModelDao.selectByExample(example));
						break;
					case "processname":
						String processname = filterPara.get(key).toString();
						ProcessModelExample _example = new ProcessModelExample();
						_example.or().andNameLike("%" + processname + "%");
						List<ProcessModel> processes = processModelDao.selectByExample(_example);
						if (processes != null && processes.size() > 0) {
							List<Long> processids = new ArrayList<Long>();
							for (ProcessModel processModel : processes) {
								processids.add(processModel.getId());
							}
							example.clear();
							example.or().andProcessidIn(processids);
							projects = new ArrayList<ProjectModel>();
							projects.addAll(projectModelDao.selectByExample(example));
						}
						break;
					case "id":
						record.setId(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						record.setName(filterPara.get(key).toString());
						break;
					case "tasktype":
						record.setTasktype(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "priority":
						record.setPriority(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "statedes":
						stateMaps = getStateMap(processType, filterPara.get(key).toString());
						if (stateMaps == null || stateMaps.size() <= 0) {
							json.addObject("rows", new ArrayList<TaskModel>());
							json.addObject("total", 0);
							json.addObject("result", 1);
							return json;
						}
						break;
					case "editname":
						List<EmployeeModel> editusers = emapgoAccountService.getEmployeesByIDSAndRealname(null, filterPara.get(key).toString());
						if (editusers != null && editusers.size() > 0) {
							for (EmployeeModel edituser : editusers) {
								editUserids.add(edituser.getId());
							}
						} else {
							json.addObject("rows", new ArrayList<TaskModel>());
							json.addObject("total", 0);
							json.addObject("result", 1);
							return json;
						}
						break;
					case "checkname":
						List<EmployeeModel> checkusers = emapgoAccountService.getEmployeesByIDSAndRealname(null, filterPara.get(key).toString());
						if (checkusers != null && checkusers.size() > 0) {
							for (EmployeeModel checkuser : checkusers) {
								checkUserids.add(checkuser.getId());
							}
						} else {
							json.addObject("rows", new ArrayList<TaskModel>());
							json.addObject("total", 0);
							json.addObject("result", 1);
							return json;
						}
						break;
					case "processtype":
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			if (projects != null) {
				projectids = new ArrayList<Long>();
				for (ProjectModel project : projects) {
					projectids.add(project.getId());
				}
			}

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
				ConfigDBModel configDBModel = configDBModelDao
						.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
				record.setProcesstype(processType.getValue());
				List<TaskModel> rows = taskModelDao.selectTaskModels(configDBModel, record, projectids, editUserids, checkUserids, stateMaps, limit, offset);
				for (TaskModel row : rows) {
					if (row == null)
						continue;

					if (row.getProjectid() != null && row.getProjectid().compareTo(0L) > 0) {
						ProjectModel project = projectModelDao.selectByPrimaryKey(row.getProjectid());
						if (project != null) {
							ProcessModel processModel = processModelDao.selectByPrimaryKey(project.getProcessid());
							if (processModel != null) {
								row.setProcessid(processModel.getId());
								row.setProcessname(processModel.getName());
								row.setStatedes(getStateDes(row));
							}
						}
					}
					
					if (row.getEditid() != null && row.getEditid().compareTo(0) > 0) {
						EmployeeModel erecord = new EmployeeModel();
						erecord.setId(row.getEditid().compareTo(500000) >= 0 ? (row.getEditid() - 500000) : row.getEditid());
						EmployeeModel edit = emapgoAccountService.getOneEmployeeWithCache(erecord);
						if (edit != null) {
							row.setEditname(edit.getRealname());
						}
					}
					
					if (row.getCheckid() != null && row.getCheckid().compareTo(0) > 0) {
						EmployeeModel erecord = new EmployeeModel();
						erecord.setId(row.getCheckid().compareTo(600000) >= 0 ? (row.getCheckid() - 600000) : row.getCheckid());
						EmployeeModel check = emapgoAccountService.getOneEmployeeWithCache(erecord);
						if (check != null) {
							row.setCheckname(check.getRealname());
						}
					}
				}
				Integer count = taskModelDao.countTaskModels(configDBModel, record, projectids, editUserids, checkUserids, stateMaps);
				json.addObject("rows", rows);
				json.addObject("total", count);
				json.addObject("result", 1);
			} else {
				json.addObject("rows", new ArrayList<TaskModel>());
				json.addObject("total", 0);
				json.addObject("result", 1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=changePriority")
	public ModelAndView changePriority(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer ret = -1;
		try {
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			Integer priority = ParamUtils.getIntParameter(request, "priority", -1);
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);
			
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, ProcessType.valueOf(processType));
			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

				TaskModel record = new TaskModel();
				record.setId(taskid);
				record.setPriority(priority);
				ret = taskModelDao.updateTaskByIDSelective(configDBModel, record );
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1;
		}
		json.addObject("ret", ret);

		logger.debug("END");
		return json;
	}

	private List<StateMap> getStateMap(ProcessType processType, String stateDes) {
		List<StateMap> stateMaps = new ArrayList<StateMap>();
		try {
			if (processType.equals(ProcessType.ERROR) ||
				processType.equals(ProcessType.NRFC) ||
				processType.equals(ProcessType.ATTACH)) {
				switch (stateDes) {
				case "编辑中":
					stateMaps.add(new StateMap(0, 5, null, -1));
					stateMaps.add(new StateMap(1, 5, null, -1));
					stateMaps.add(new StateMap(2, 6, null, null));
					stateMaps.add(new StateMap(2, 11, null, null));
					stateMaps.add(new StateMap(2, 12, null, null));
					stateMaps.add(new StateMap(2, 13, null, null));
					stateMaps.add(new StateMap(2, 14, null, null));
					stateMaps.add(new StateMap(2, 15, null, null));
					break;
				case "完成":
					stateMaps.add(new StateMap(3, 5, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(3, 6, null, null));
					break;
				case "未制作":
					stateMaps.add(new StateMap(0, 0, null, null));
					break;
				case "校正错误修改中":
					stateMaps.add(new StateMap(0, 5, null, 1));
					stateMaps.add(new StateMap(1, 5, null, 1));
					break;
				case "待校正":
					stateMaps.add(new StateMap(3, 5, TaskTypeEnum.ATTACH.getValue(), null));
					break;
				case "校正中":
					stateMaps.add(new StateMap(0, 6, null, null));
					stateMaps.add(new StateMap(1, 6, null, null));
					break;
				case "悬挂点创建中":
					stateMaps.add(new StateMap(0, 11, null, null));
					stateMaps.add(new StateMap(0, 12, null, null));
					stateMaps.add(new StateMap(0, 13, null, null));
					stateMaps.add(new StateMap(0, 14, null, null));
					stateMaps.add(new StateMap(0, 15, null, null));
					stateMaps.add(new StateMap(1, 11, null, null));
					stateMaps.add(new StateMap(1, 12, null, null));
					stateMaps.add(new StateMap(1, 13, null, null));
					stateMaps.add(new StateMap(1, 14, null, null));
					stateMaps.add(new StateMap(1, 15, null, null));
					break;
				case "预发布完成":
					stateMaps.add(new StateMap(3, 20, null, null));
					break;
				case "质检完成":
					stateMaps.add(new StateMap(2, 52, null, null));
					break;
				case "质检中":
					stateMaps.add(new StateMap(1, 52, null, null));
					stateMaps.add(new StateMap(2, 5, null, null));
					break;
				}
			} else if(processType.equals(ProcessType.POIEDIT)) {
				switch (stateDes) {
				case "未制作":
					for (TaskTypeEnum type : TaskTypeEnum.getPoiEditTaskTypes()) {
						stateMaps.add(new StateMap(0, 0, type.getValue(), null));
					}
					break;
				case "制作中":
					for (TaskTypeEnum type : TaskTypeEnum.getPoiEditTaskTypes()) {
						stateMaps.add(new StateMap(1, 5, type.getValue(), null));
					}
					break;
				case "制作完成":
					for (TaskTypeEnum type : TaskTypeEnum.getPoiEditTaskTypes()) {
						stateMaps.add(new StateMap(2, 5, type.getValue(), null));
					}
					break;
				case "未校正":
					for (TaskTypeEnum type : TaskTypeEnum.getPoiCheckTaskTypes()) {
						stateMaps.add(new StateMap(0, 0, type.getValue(), null));
					}
					break;
				case "校正中":
					for (TaskTypeEnum type : TaskTypeEnum.getPoiCheckTaskTypes()) {
						stateMaps.add(new StateMap(1, 6, type.getValue(), null));
					}
					break;
				case "校正完成":
					for (TaskTypeEnum type : TaskTypeEnum.getPoiCheckTaskTypes()) {
						stateMaps.add(new StateMap(2, 6, type.getValue(), null));
					}
					break;
				}
			}
		} catch (Exception e) {
		}
		return stateMaps;
	}

	private String getStateDes(TaskModel task) {
		Integer state = task.getState();
		Integer process = task.getProcess();
		TaskTypeEnum tasktype = TaskTypeEnum.valueOf(task.getTasktype());
		Integer checkid = task.getCheckid();
		
		if (tasktype.equals(TaskTypeEnum.ERROR) ||
			tasktype.equals(TaskTypeEnum.NRFC) ||
			tasktype.equals(TaskTypeEnum.ATTACH)) {
			switch (state) {
			case 0:
				switch (process) {
				case 0:
					return "未制作";
				case 5:
					if (checkid != null && checkid > 0)
						return "校正错误修改中";
					else
						return "制作中";
				case 6:
					return "校正中";
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
					return "悬挂点创建中";
				}
			case 1:
				switch (process) {
				case 5:
					if (checkid != null && checkid > 0)
						return "校正错误修改中";
					else
						return "制作中";
				case 6:
					return "校正中";
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
					return "悬挂点创建中";
				case 52:
					return "质检中";
				}
				break;
			case 2:
				switch (process) {
				case 5:
					return "质检中";
				case 6:
					return "制作中";
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
					return "制作中";
				case 52:
					return "质检完成";
				}
				break;
			case 3:
				switch (process) {
				case 5:
					if (tasktype.equals(TaskTypeEnum.NRFC))
						return "完成";
					else if(tasktype.equals(TaskTypeEnum.ATTACH))
						return "未校正";
					else
						return "校正中";
				case 6:
					return "完成";
				case 20:
					return "预发布完成";
				}
				break;
			}
		} else if (TaskTypeEnum.getPoiEditTaskTypes().contains(tasktype)) {
			switch (state) {
			case 0:
				return "未制作";
			case 1:
				return "制作中";
			case 2:
				return "制作完成";
			}
		} else if (TaskTypeEnum.getPoiCheckTaskTypes().contains(tasktype)) {
			switch (state) {
			case 0:
				return "未校正";
			case 1:
				return "校正中";
			case 2:
				return "校正完成";
			}
		}
		
		return tasktype + "-" + state + ":" + process;
	}
}
