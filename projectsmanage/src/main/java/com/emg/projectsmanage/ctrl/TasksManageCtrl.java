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
import com.emg.projectsmanage.common.ProcessConfigEnum;
import com.emg.projectsmanage.common.ProcessType;
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
import com.emg.projectsmanage.service.ProcessConfigModelService;

import net.sf.json.JSONArray;
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
	
	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
//		if (!hasRole(request, RoleType.ROLE_POIVIDEOEDIT.toString())) {
//			Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
//		}
		model.addAttribute("processTypes", ProcessType.toJsonStr());

		List<EmployeeModel> editers = new ArrayList<EmployeeModel>();
		List<EmployeeModel> checkers = new ArrayList<EmployeeModel>();
		for (ProcessType pType : ProcessType.values()) {
			if (pType.equals(ProcessType.UNKNOWN))
				continue;
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, pType);
			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao
//						.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
				ConfigDBModel configDBModel = configDBModelDao
						.selectByPrimaryKey(1001);
				editers.addAll(taskModelDao.groupEditers(configDBModel, pType.getValue()));
				checkers.addAll(taskModelDao.groupCheckers(configDBModel, pType.getValue()));
			}
		}
		if (editers.size() > 0) {
			JSONArray jsonArrayworker = JSONArray.fromObject(editers);
			model.addAttribute("editers", jsonArrayworker.toString());
		} else {
			model.addAttribute("editers", "({})");
		}
		if (checkers.size() > 0) {
			JSONArray jsonArraychecker = JSONArray.fromObject(checkers);
			model.addAttribute("checkers", jsonArraychecker.toString());
		} else {
			model.addAttribute("checkers", "({})");
		}

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
			ProjectModelExample example = new ProjectModelExample();
			List<StateMap> stateMaps = new ArrayList<StateMap>();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
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
						List<Long> processids = new ArrayList<Long>();
						for (ProcessModel processModel : processes) {
							processids.add(processModel.getId());
						}
						example.clear();
						example.or().andProcessidIn(processids);
						projects = new ArrayList<ProjectModel>();
						projects.addAll(projectModelDao.selectByExample(example));
						break;
					case "processtype":
						processType = ProcessType.valueOf(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "id":
						record.setId(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						record.setName(filterPara.get(key).toString());
						break;
					case "statedes":
						stateMaps = getStateMap(filterPara.get(key).toString());
						break;
					case "editid":
						record.setEditid(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "checkid":
						record.setCheckid(Integer.valueOf(filterPara.get(key).toString()));
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
//				ConfigDBModel configDBModel = configDBModelDao
//						.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
				ConfigDBModel configDBModel = configDBModelDao
						.selectByPrimaryKey(1001);
				record.setProcesstype(processType.getValue());
				List<TaskModel> rows = taskModelDao.selectTaskModels(configDBModel, record, projectids, stateMaps, limit, offset);
				for (TaskModel row : rows) {
					if (row == null)
						continue;

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
				Integer count = taskModelDao.countTaskModels(configDBModel, record, projectids, stateMaps);
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

	private List<StateMap> getStateMap(String stateDes) {
		List<StateMap> stateMaps = new ArrayList<StateMap>();
		try {
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
				stateMaps.add(new StateMap(3, 5, 12, null));
				stateMaps.add(new StateMap(3, 6, null, null));
				break;
			case "未制作":
				stateMaps.add(new StateMap(0, 0, null, null));
				break;
			case "校正错误修改中":
				stateMaps.add(new StateMap(0, 5, null, 1));
				stateMaps.add(new StateMap(1, 5, null, 1));
				break;
			case "校正中":
				stateMaps.add(new StateMap(3, 5, 13, null));
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
		} catch (Exception e) {
		}
		return stateMaps;
	}

	private String getStateDes(TaskModel task) {
		Integer state = task.getState();
		Integer process = task.getProcess();
		Integer tasktype = task.getTasktype();
		Integer checkid = task.getCheckid();

		switch (state) {
		case 0:
			switch (process) {
			case 0:
				return "未制作";
			case 5:
				if (checkid != null && checkid > 0)
					return "校正错误修改中";
				else
					return "编辑中";
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
					return "编辑中";
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
				return "编辑中";
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				return "编辑中";
			case 52:
				return "质检完成";
			}
			break;
		case 3:
			switch (process) {
			case 5:
				if (tasktype == 12)
					return "完成";
				else
					return "校正中";
			case 6:
				return "完成";
			case 20:
				return "预发布完成";
			}
			break;
		}
		return state + ":" + process;
	}
}
