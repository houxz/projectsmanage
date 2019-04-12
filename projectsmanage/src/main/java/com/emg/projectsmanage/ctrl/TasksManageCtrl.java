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
import com.emg.projectsmanage.common.ProcessState;
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
		model.addAttribute("processStates", ProcessState.toJsonStr());
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
			List<ProjectModel> projects = new ArrayList<ProjectModel>();
			List<ProjectModel> _projects = new ArrayList<ProjectModel>();
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
						_projects = projectModelDao.selectByExample(example);
						if (projects.isEmpty()) {
							projects.addAll(_projects);
						} else {
							projects.retainAll(_projects);
							if (projects.isEmpty()) {
								json.addObject("rows", new ArrayList<TaskModel>());
								json.addObject("total", 0);
								json.addObject("result", 1);
								return json;
							}
						}
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
							_projects = projectModelDao.selectByExample(example);
							if (projects.isEmpty()) {
								projects.addAll(_projects);
							} else {
								projects.retainAll(_projects);
								if (projects.isEmpty()) {
									json.addObject("rows", new ArrayList<TaskModel>());
									json.addObject("total", 0);
									json.addObject("result", 1);
									return json;
								}
							}
						}
						break;
					case "processstate":
						Integer processstate = Integer.valueOf(filterPara.get(key).toString());
						ProcessModelExample __example = new ProcessModelExample();
						__example.or().andStateEqualTo(processstate);
						List<ProcessModel> _processes = processModelDao.selectByExample(__example);
						if (_processes != null && _processes.size() > 0) {
							List<Long> processids = new ArrayList<Long>();
							for (ProcessModel processModel : _processes) {
								processids.add(processModel.getId());
							}
							example.clear();
							example.or().andProcessidIn(processids);
							_projects = projectModelDao.selectByExample(example);
							if (projects.isEmpty()) {
								projects.addAll(_projects);
							} else {
								projects.retainAll(_projects);
								if (projects.isEmpty()) {
									json.addObject("rows", new ArrayList<TaskModel>());
									json.addObject("total", 0);
									json.addObject("result", 1);
									return json;
								}
							}
						}
						break;
					case "id":
						record.setId(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						record.setName(filterPara.get(key).toString());
						break;
					case "batchid":
						record.setBatchid(Long.valueOf(filterPara.get(key).toString()));
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

			if (projects != null && !projects.isEmpty()) {
				projectids = new ArrayList<Long>();
				for (ProjectModel project : projects) {
					projectids.add(project.getId());
				}
			}

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
			//add by lianhr begin 2019/02/21
			if(processType.getValue().equals(ProcessType.COUNTRY.getValue())) {
				config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			}
			//add by lianhr end
			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
				ConfigDBModel configDBModel = configDBModelDao
						.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
				record.setProcesstype(processType.getValue());
				Integer count = taskModelDao.countTaskModels(configDBModel, record, projectids, editUserids, checkUserids, stateMaps);
				if (count != null && count.compareTo(0) > 0) {
					List<TaskModel> rows = taskModelDao.selectTaskModels(configDBModel, record, projectids, editUserids, checkUserids, stateMaps, limit, offset);
					List<Long> projectIDInRows = new ArrayList<Long>();
					List<Long> taskIDInRows = new ArrayList<Long>();
					List<Integer> userIDInRows = new ArrayList<Integer>();
					for (TaskModel row : rows) {
						if (row == null)
							continue;
						
						taskIDInRows.add(row.getId());
						
						if (row.getProjectid() != null && row.getProjectid().compareTo(0L) > 0) {
							projectIDInRows.add(row.getProjectid());
						}
						
						if (row.getEditid() != null && row.getEditid().compareTo(0) > 0) {
							Integer editid = row.getEditid();
							if (editid.compareTo(500000) >= 0) {
								editid = editid - 500000;
								row.setEditid(editid);
							}
							userIDInRows.add(editid);
						}
						
						if (row.getCheckid() != null && row.getCheckid().compareTo(0) > 0) {
							Integer checkid = row.getCheckid();
							if (checkid.compareTo(600000) >= 0) {
								checkid = checkid - 600000;
								row.setCheckid(checkid);
							}
							userIDInRows.add(checkid);
						}
					}
					
					List<Map<String, Object>> errorList = new ArrayList<Map<String, Object>>();
					List<Map<String, Object>> fielddataList = new ArrayList<Map<String, Object>>();
					if (processType.equals(ProcessType.POIEDIT)) {
						errorList = taskModelDao.countErrorsByTaskid(configDBModel, taskIDInRows);
						fielddataList = taskModelDao.countFielddatasByTaskid(configDBModel, taskIDInRows);
					}
					
					List<ProjectModel> projectsInRows = new ArrayList<ProjectModel>();
					List<ProcessModel> processesInRows = new ArrayList<ProcessModel>();
					if (!projectIDInRows.isEmpty()) {
						ProjectModelExample _example = new ProjectModelExample();
						_example.or().andIdIn(projectIDInRows);
						projectsInRows = projectModelDao.selectByExample(_example );
						if (projectsInRows != null && !projectsInRows.isEmpty()) {
							List<Long> processesIDInRows = new ArrayList<Long>();
							for (ProjectModel projectsInRow : projectsInRows) {
								processesIDInRows.add(projectsInRow.getProcessid());
							}
							ProcessModelExample __example = new ProcessModelExample();
							__example.or().andIdIn(processesIDInRows);
							processesInRows = processModelDao.selectByExample(__example );
						}
					}
					
					List<EmployeeModel> userInRows = new ArrayList<EmployeeModel>();
					if (userIDInRows != null && !userIDInRows.isEmpty()) {
						userInRows = emapgoAccountService.getEmployeeByIDS(userIDInRows);
					}
					
					for (TaskModel row : rows) {
						Long taskid = row.getId();
						Long projectid = row.getProjectid();
						Integer editid = row.getEditid();
						Integer checkid = row.getCheckid();
						
						row.setStatedes(getStateDes(row));
						
						for (ProjectModel projectsInRow : projectsInRows) {
							if (projectid == null)
								continue;
							
							if (projectid.equals(projectsInRow.getId())) {
								for (ProcessModel processesInRow : processesInRows) {
									if (processesInRow.getId().equals(projectsInRow.getProcessid())) {
										row.setProcessid(processesInRow.getId());
										row.setProcessstate(processesInRow.getState());
										row.setProcessname(processesInRow.getName());
										break;
									}
								}
								break;
							}
						}
						
						for (Map<String, Object> error : errorList) {
							if (taskid.equals(Long.valueOf(error.get("taskid").toString()))) {
								row.setErrortotal(Integer.valueOf(error.get("total").toString()));
								row.setErrorrest(Integer.valueOf(error.get("rest").toString()));
								break;
							}
						}
						
						for (Map<String, Object> fielddata : fielddataList) {
							if (taskid.equals(Long.valueOf(fielddata.get("taskid").toString()))) {
								row.setFielddatatotal(Integer.valueOf(fielddata.get("total").toString()));
								row.setFielddatarest(Integer.valueOf(fielddata.get("rest").toString()));
								break;
							}
						}
						
						for (EmployeeModel userInRow : userInRows) {
							if (editid != null && editid.equals(userInRow.getId())) {
								row.setEditname(userInRow.getRealname());
							}
							if (checkid != null && checkid.equals(userInRow.getId())) {
								row.setCheckname(userInRow.getRealname());
							}
						}
					}
					
					json.addObject("rows", rows);
					json.addObject("total", count);
					json.addObject("result", 1);
				} else {
					json.addObject("rows", new ArrayList<TaskModel>());
					json.addObject("total", 0);
					json.addObject("result", 1);
				}
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
			switch (processType) {
			case ERROR:
				// TODO: 综检改错任务列表基于任务状态筛选还需要补充
				break;
			case NRFC:
				switch (stateDes) {
				case "编辑中":
				case "制作中":
					stateMaps.add(new StateMap(0, 5, TaskTypeEnum.NRFC.getValue(), -1));
					stateMaps.add(new StateMap(1, 5, TaskTypeEnum.NRFC.getValue(), -1));
					stateMaps.add(new StateMap(2, 11, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(2, 12, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(2, 13, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(2, 14, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(2, 15, TaskTypeEnum.NRFC.getValue(), null));
					break;
				case "完成":
					stateMaps.add(new StateMap(3, 5, TaskTypeEnum.NRFC.getValue(), null));
					break;
				case "未制作":
					stateMaps.add(new StateMap(0, 0, TaskTypeEnum.NRFC.getValue(), null));
					break;
				case "悬挂点创建中":
					stateMaps.add(new StateMap(0, 11, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(0, 12, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(0, 13, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(0, 14, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(0, 15, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(1, 11, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(1, 12, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(1, 13, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(1, 14, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(1, 15, TaskTypeEnum.NRFC.getValue(), null));
					break;
				case "预发布完成":
					stateMaps.add(new StateMap(3, 20, TaskTypeEnum.NRFC.getValue(), null));
					break;
				case "质检完成":
					stateMaps.add(new StateMap(2, 52, TaskTypeEnum.NRFC.getValue(), null));
					break;
				case "质检中":
					stateMaps.add(new StateMap(1, 52, TaskTypeEnum.NRFC.getValue(), null));
					stateMaps.add(new StateMap(2, 5, TaskTypeEnum.NRFC.getValue(), null));
					break;
				}
				break;
			case ATTACH:
			case AREA:
			case ATTACHWITHDATA:
				switch (stateDes) {
				case "编辑中":
				case "制作中":
					stateMaps.add(new StateMap(0, 5, null, -1));
					stateMaps.add(new StateMap(1, 5, null, -1));
					stateMaps.add(new StateMap(2, 6, null, null));
					break;
				case "完成":
					stateMaps.add(new StateMap(3, 6, null, null));
					break;
				case "未制作":
					stateMaps.add(new StateMap(0, 0, null, null));
					break;
				case "校正错误修改中":
					stateMaps.add(new StateMap(0, 5, null, 1));
					stateMaps.add(new StateMap(1, 5, null, 1));
					break;
				case "未校正":
				case "待校正":
					stateMaps.add(new StateMap(3, 5, null, null));
					break;
				case "校正中":
					stateMaps.add(new StateMap(0, 6, null, null));
					stateMaps.add(new StateMap(1, 6, null, null));
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
				break;
			case COUNTRY:
				//add by lianhr begin 2019/02/22
				switch (stateDes) {
				case "质检异常":
					stateMaps.add(new StateMap(13, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(14, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(15, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(16, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(17, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(18, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(19, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(22, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(23, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(100, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(13, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(14, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(15, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(16, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(17, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(18, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(19, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(22, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(23, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					stateMaps.add(new StateMap(100, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					break;
				case "待质检":
					stateMaps.add(new StateMap(12, 51, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(12, 51, TaskTypeEnum.QC_QUANYU.getValue(), null));
					break;
				case "质检中":
					stateMaps.add(new StateMap(11, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(11, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					break;
				case "质检完成":
					stateMaps.add(new StateMap(2, 52, TaskTypeEnum.QC_JIUGONGGE.getValue(), null));
					stateMaps.add(new StateMap(2, 52, TaskTypeEnum.QC_QUANYU.getValue(), null));
					break;
				}
				break;
				//add by lianhr end
			case POIEDIT:
				switch (stateDes) {
				case "未制作":
					for (TaskTypeEnum type : TaskTypeEnum.getPoiEditTaskTypes()) {
						stateMaps.add(new StateMap(0, 0, type.getValue(), null));
						stateMaps.add(new StateMap(0, 5, type.getValue(), null));
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
						stateMaps.add(new StateMap(0, 6, type.getValue(), null));
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
				break;
			case ADJUSTMAP:
				switch (stateDes) {
				case "制作中":
					stateMaps.add(new StateMap(1, 5, TaskTypeEnum.ADJUSTMAP.getValue(), -1));
					stateMaps.add(new StateMap(2, 6, TaskTypeEnum.ADJUSTMAP.getValue(), null));
					break;
				case "完成":
					stateMaps.add(new StateMap(3, 6, TaskTypeEnum.ADJUSTMAP.getValue(), null));
					break;
				case "未制作":
					stateMaps.add(new StateMap(0, 0, TaskTypeEnum.ADJUSTMAP.getValue(), null));
					break;
				case "校正错误修改中":
					stateMaps.add(new StateMap(1, 5, TaskTypeEnum.ADJUSTMAP.getValue(), 1));
					break;
				case "未校正":
					stateMaps.add(new StateMap(3, 5, TaskTypeEnum.ADJUSTMAP.getValue(), null));
					break;
				case "校正中":
					stateMaps.add(new StateMap(1, 6, TaskTypeEnum.ADJUSTMAP.getValue(), null));
					break;
				}
				break;
			case GEN:
				switch (stateDes) {
				case "未制作":
					stateMaps.add(new StateMap(0, null, TaskTypeEnum.GEN_WEB.getValue(), null));
					break;
				case "制作中":
					stateMaps.add(new StateMap(1, 5, TaskTypeEnum.GEN_WEB.getValue(), null));
					break;
				case "校正中":
					stateMaps.add(new StateMap(1, 6, TaskTypeEnum.GEN_WEB.getValue(), null));
					break;
				case "校正错误修改中":
					stateMaps.add(new StateMap(1, 7, TaskTypeEnum.GEN_WEB.getValue(), null));
					stateMaps.add(new StateMap(2, 7, TaskTypeEnum.GEN_WEB.getValue(), null));
					break;
				case "制作完成":
					stateMaps.add(new StateMap(2, 5, TaskTypeEnum.GEN_WEB.getValue(), null));
					break;
				case "校正完成":
					stateMaps.add(new StateMap(2, 6, TaskTypeEnum.GEN_WEB.getValue(), null));
					break;
				case "完成":
					stateMaps.add(new StateMap(3, null, TaskTypeEnum.GEN_WEB.getValue(), null));
					break;
				}
				break;
			default:
				logger.error("Unknow process type: " + processType.toString());
				break;
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
		
		if (tasktype.equals(TaskTypeEnum.QC_JIUGONGGE) || tasktype.equals(TaskTypeEnum.QC_QUANYU)) {
			//add by lianhr begin 2019/02/22
			switch (state) {
			case 12:
				switch (process) {
			    case 51:
			        return "待质检";
			    }
				break;
			case 11:
				switch (process) {
			    case 52:
			        return "质检中";
			    }
				break;
			case 2:
				switch (process) {
			    case 52:
			        return "质检完成";
			    }
				break;
			default:
				return "质检异常";
			}
			//add by lianhr end
		} else if (tasktype.equals(TaskTypeEnum.NRFC)) {
			switch (state) {
			case 0:
				switch (process) {
				case 0:
					return "未制作";
				case 5:
					if (checkid == null || checkid <= 0)
						return "制作中";
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
					if (checkid == null || checkid <= 0)
						return "制作中";
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
					return "完成";
				case 20:
					return "预发布完成";
				}
				break;
			}
		} else if (tasktype.equals(TaskTypeEnum.ATTACH) ||
				tasktype.equals(TaskTypeEnum.AREA_QUHUAN) ||
				tasktype.equals(TaskTypeEnum.AREA_JIANCHENGQU) ||
				tasktype.equals(TaskTypeEnum.ATTACHWITHDATA)) {
			switch (state) {
			case 0:
				switch (process) {
				case 0:
					return "未制作";
				case 5:
					if (checkid == null || checkid <= 0)
						return "制作中";
					else
						return "校正错误修改中";
				case 6:
					return "校正中";
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
				case 52:
					return "质检完成";
				}
				break;
			case 3:
				switch (process) {
				case 5:
					return "未校正";
				case 6:
					return "完成";
				case 20:
					return "预发布完成";
				}
				break;
			}
		} else if (tasktype.equals(TaskTypeEnum.ADJUSTMAP)) {
			switch (state) {
			case 0:
				switch (process) {
				case 0:
					return "未制作";
				}
				break;
			case 1:
				switch (process) {
				case 5:
					if (checkid != null && checkid > 0)
						return "校正错误修改中";
					else
						return "制作中";
				case 6:
					return "校正中";
				}
				break;
			case 2:
				switch (process) {
				case 6:
					return "制作中";
				}
				break;
			case 3:
				switch (process) {
				case 5:
					return "未校正";
				case 6:
					return "完成";
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
		} else if (tasktype.equals(TaskTypeEnum.GEN_WEB)) {
			switch (state) {
			case 0:
				return "未制作";
			case 1:
				switch (process) {
				case 5:
					return "制作中";
				case 6:
					return "校正中";
				case 7:
					return "校正错误修改中";
				}
				break;
			case 2:
				switch (process) {
				case 5:
					return "制作完成";
				case 6:
					return "校正完成";
				case 7:
					return "校正错误修改中";
				}
				break;
			case 3:
				return "完成";
			}
		}
		
		return tasktype + "-" + state + ":" + process;
	}
}
