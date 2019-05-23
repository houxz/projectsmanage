package com.emg.poiwebeditor.ctrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.emg.poiwebeditor.client.TaskModelClient;
import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.ItemAreaType;
import com.emg.poiwebeditor.common.ItemSetEnable;
import com.emg.poiwebeditor.common.ItemSetSysType;
import com.emg.poiwebeditor.common.ItemSetType;
import com.emg.poiwebeditor.common.ItemSetUnit;
import com.emg.poiwebeditor.common.ModelEnum;
import com.emg.poiwebeditor.common.OwnerStatus;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.PriorityLevel;
import com.emg.poiwebeditor.common.ProcessConfigEnum;
import com.emg.poiwebeditor.common.ProcessConfigModuleEnum;
import com.emg.poiwebeditor.common.ProcessState;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.common.ProjectState;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.dao.process.ConfigDBModelDao;
import com.emg.poiwebeditor.dao.process.ProcessConfigValueModelDao;
import com.emg.poiwebeditor.dao.process.ProcessModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.UserRoleModelDao;
import com.emg.poiwebeditor.dao.task.DatasetModelDao;
import com.emg.poiwebeditor.dao.task.ItemSetModelDao;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.DatasetModel;
import com.emg.poiwebeditor.pojo.EmployeeModel;
import com.emg.poiwebeditor.pojo.ItemAreaModel;
import com.emg.poiwebeditor.pojo.ItemSetModel;
import com.emg.poiwebeditor.pojo.ProcessConfigModel;
import com.emg.poiwebeditor.pojo.ProcessConfigValueModel;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProcessModelExample;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsUserModel;
import com.emg.poiwebeditor.pojo.UserRoleModel;
import com.emg.poiwebeditor.pojo.keywordModelForTask;
import com.emg.poiwebeditor.pojo.ProcessModelExample.Criteria;
import com.emg.poiwebeditor.service.EmapgoAccountService;
import com.emg.poiwebeditor.service.ProcessConfigModelService;

@Controller
@RequestMapping("/processesmanage.web")
public class ProcessesManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProcessesManageCtrl.class);

	@Autowired
	private ProcessModelDao processModelDao;

	@Autowired
	private ConfigDBModelDao configDBModelDao;

	@Autowired
	private ProcessConfigModelService processConfigModelService;

	@Autowired
	private ProcessConfigValueModelDao processConfigValueModelDao;

	@Autowired
	private EmapgoAccountService emapgoAccountService;
	
	@Autowired
	private ProjectModelDao projectModelDao;
	
	@Autowired
	private UserRoleModelDao userRoleModelDao;
	
	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;
	
	private ItemSetModelDao itemSetModelDao = new ItemSetModelDao();
	
	private DatasetModelDao datasetModelDao = new DatasetModelDao();
	
	private TaskModelClient taskModelDao = new TaskModelClient();

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesConfigCtrl-openLader start.");

		model.addAttribute("processStates", ProcessState.undoneToJsonStr());
		model.addAttribute("processTypes", ProcessType.toJsonStr());
		model.addAttribute("priorityLevels", PriorityLevel.toJsonStr());
		
		return "processesmanage";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ProcessModelExample example = new ProcessModelExample();
			Criteria criteria = example.or();
			criteria.andTypeEqualTo(ProcessType.POIPOLYMERIZE.getValue());
			criteria.andStateNotEqualTo(ProcessState.COMPLETE.getValue());
			//hxz 根据id , 项目名称，用户，优先级，项目状态 过滤项目时触发以下代码
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						criteria.andIdEqualTo(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						criteria.andNameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "state":
						criteria.andStateEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "priority":
						criteria.andPriorityEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "username":
						criteria.andUsernameLike("%" + filterPara.get(key).toString() + "%");
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);
			example.setOrderByClause("priority desc, id");

			List<ProcessModel> rows = processModelDao.selectByExample(example);
			int count = processModelDao.countByExample(example);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("ProcessesManageCtrl-pages end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=newprocess")
	public ModelAndView createNewProcess(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-createNewProcess start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer ret = -1;
		Long newProcessID = -1L;
		try {
			newProcessID = ParamUtils.getLongParameter(request, "processid", -1L);
			String newProcessName = ParamUtils.getParameter(request, "newProcessName");
			//页面不再提供类型的输入了 byhxz20190517
			//Integer type = ParamUtils.getIntParameter(request, "type", 0);
			Integer type = ProcessType.POIPOLYMERIZE.getValue();
			Integer priority = ParamUtils.getIntParameter(request, "priority", 0);
			Integer uid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			String username = (String) session.getAttribute(CommonConstants.SESSION_USER_NAME);
			Integer owner = ParamUtils.getIntParameter(request, "config_2_19", 0) == 1 ? 1 : 0;
			String strWorkers = ParamUtils.getParameter(request, "config_2_18");
			String strCheckers = ParamUtils.getParameter(request, "config_2_21");
			String strDatasets = ParamUtils.getParameter(request, "config_2_25");
			//add by lianhr begin 2019/02/14
			String strBatch = ParamUtils.getParameter(request, "strbatch");
			String strModel = ParamUtils.getParameter(request, "config_1_29");
			//add by lianhr end
			Integer processEditType = ParamUtils.getIntParameter(request, "config_2_30", 1);

			Boolean isNewProcess = newProcessID.equals(0L);

			if (newProcessID.compareTo(0L) < 0) {
				json.addObject("result", -1);
				json.addObject("resultMsg", "保存失败，错误的参数值：processid");
				return json;
			}
			if (newProcessName == null || newProcessName.isEmpty()) {
				json.addObject("result", -1);
				json.addObject("resultMsg", "保存失败，项目名称不能为空");
				return json;
			}
			
			try {
				JSONObject.fromObject(strWorkers);
			} catch (Exception e) {
				logger.debug("strWorkers is not a json string: " + strWorkers);
				try {
					StringBuilder newStrWorkers = new StringBuilder();
					newStrWorkers.append("{");
					for (String strWorker : strWorkers.split(",")) {
						newStrWorkers.append("\"");
						newStrWorkers.append(strWorker);
						newStrWorkers.append("\":\"\",");
					}
					newStrWorkers = newStrWorkers.deleteCharAt(newStrWorkers.length() - 1);
					newStrWorkers.append("}");
					strWorkers = newStrWorkers.toString();
				} catch (Exception e2) {
					logger.error(e2.getMessage(), e2);
				}
				
			}
			
			Long projectid332 = -1L, projectid349 = -1L;
			if (!isNewProcess) {
				ProcessConfigValueModel processConfig332 = processConfigValueModelDao.selectByProcessIDAndConfigID(newProcessID, ProcessConfigEnum.ZHIJIANXIANGMUID.getValue());
				if (processConfig332 != null && processConfig332.getValue() != null && !processConfig332.getValue().isEmpty()) {
					projectid332 = Long.valueOf(processConfig332.getValue());
				}
				
				ProcessConfigValueModel processConfig349 = processConfigValueModelDao.selectByProcessIDAndConfigID(newProcessID, ProcessConfigEnum.BIANJIXIANGMUID.getValue());
				if (processConfig349 != null && processConfig349.getValue() != null && !processConfig349.getValue().isEmpty()) {
					projectid349 = Long.valueOf(processConfig349.getValue());
				}
			}
			
			// TODO: 新增项目类型需要指定systemid
			String suffix = new String();
			Integer systemid = -1;
// 这些不再需要byhxz20190517
//			if(type.equals(ProcessType.ERROR.getValue())){
//				suffix = "_改错";
//				systemid = SystemType.MapDbEdit.getValue();
//			} else if(type.equals(ProcessType.NRFC.getValue())) {
//				suffix = "_NR/FC";
//				systemid = SystemType.MapDbEdit_NRFC.getValue();
//			} else if(type.equals(ProcessType.ATTACH.getValue())) {
//				suffix = "_关系附属表";
//				systemid = SystemType.MapDbEdit_Attach.getValue();
//			} else if(type.equals(ProcessType.COUNTRY.getValue())) {
//				suffix = "_全国质检";
//				systemid = SystemType.MapDbEdit_Country.getValue();
//			} else if(type.equals(ProcessType.POIEDIT.getValue())) {
//				suffix = "";
//				systemid = SystemType.poivideoedit.getValue();
//			} else if(type.equals(ProcessType.ADJUSTMAP.getValue())) {
//				if (isNewProcess) {
//					logger.error("自动项目无需手动创建：" + type);
//					json.addObject("result", -1);
//					json.addObject("resultMsg", "自动项目无需手动创建");
//					return json;
//				}
//				suffix = "";
//				systemid = SystemType.AdjustMap.getValue();
//			} else if(type.equals(ProcessType.GEN.getValue())) {
//				if (isNewProcess) {
//					logger.error("自动项目无需手动创建：" + type);
//					json.addObject("result", -1);
//					json.addObject("resultMsg", "自动项目无需手动创建");
//					return json;
//				}
//				suffix = "";
//				systemid = SystemType.poi_GEN.getValue();
//			} else if(type.equals(ProcessType.AREA.getValue())) {
//				suffix = "_行政区划";
//				systemid = SystemType.MapDbEdit_Area.getValue();
//			} else if(type.equals(ProcessType.ATTACHWITHDATA.getValue())) {
//				suffix = "";
//				systemid = SystemType.MapDbEdit_AttachWithData.getValue();
//			} //新增人工确认项目byhxz
//			else if(type.equals(ProcessType.POIPOLYMERIZE.getValue())) {
//				suffix="";
//				systemid = SystemType.poi_polymerize.getValue();
//			} else {
//				logger.error("未知的项目类型：" + type);
//				json.addObject("result", -1);
//				json.addObject("resultMsg", "未知的项目类型：" + type);
//				return json;
//			}
			
//====================
		//新增人工确认项目byhxz
			if (type.equals(ProcessType.POIPOLYMERIZE.getValue())) {
				suffix = "";
				systemid = SystemType.poi_polymerize.getValue();
			} else {
				logger.error("未知的项目类型：" + type);
				json.addObject("result", -1);
				json.addObject("resultMsg", "未知的项目类型：" + type);
				return json;
			}
			
			//==========================
			
			//新建/更新流程
			if (isNewProcess) {
				ProcessModel newProcess = new ProcessModel();
				newProcess.setName(newProcessName);
				newProcess.setType(type);
				newProcess.setPriority(priority);
				newProcess.setState(0);
				newProcess.setUserid(uid);
				newProcess.setUsername(username);
				newProcess.setProgress("0,0,0,0");
				if (processModelDao.insertSelective(newProcess) <= 0) {
					ret = -1;
					json.addObject("result", ret);
					json.addObject("resultMsg", "新建项目失败");
					return json;
				}
				newProcessID = newProcess.getId();
			} else {
				ProcessModel process = new ProcessModel();
				process.setId(newProcessID);
				process.setType(type);
				process.setName(newProcessName);
				process.setPriority(priority);
				processModelDao.updateByPrimaryKeySelective(process);
			}

			List<ProcessConfigValueModel> configValues = new ArrayList<ProcessConfigValueModel>();

			configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJILEIXING.getValue(), processEditType.toString()));
			
			// TODO: 新增项目类型需要确定是否有质检过程
//			if (isNewProcess) {
//				if(type.equals(ProcessType.ERROR.getValue()) ||
//					type.equals(ProcessType.COUNTRY.getValue()) ||
//					type.equals(ProcessType.POIPOLYMERIZE.getValue()) //增加人工确认项目byhxz
//						) {
//					String config_1_4 = newProcessName + "_质检";
//
//					ProjectModel newpro = new ProjectModel();
//					newpro.setProcessid(newProcessID);
//					newpro.setName(config_1_4);
//					newpro.setSystemid(SystemType.DBMapChecker.getValue());
//					newpro.setProtype(type);
//					newpro.setPdifficulty(0);
//					newpro.setTasknum(-1);
//					newpro.setOverstate(0);
//					newpro.setCreateby(uid);
//					newpro.setPriority(priority);
//					newpro.setOwner(OwnerStatus.PUBLIC.getValue());
//
//					if (projectModelDao.insert(newpro) > 0) {
//						projectid332 = newpro.getId();
//					}
//					if (projectid332 > 0) {
//						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANXIANGMUID.getValue(), projectid332.toString()));
//						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANXIANGMUMINGCHENG.getValue(), config_1_4));
//						//add by lianhr begin 2019/02/14
//						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BANGDINGZILIAO.getValue(), strBatch));
//						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANMOSHI.getValue(), strModel));
//						//add by lianhr end
//					}
//				}
//			} else {
//				if((type.equals(ProcessType.ERROR.getValue()) ||
//					type.equals(ProcessType.COUNTRY.getValue())) && projectid332.compareTo(0L) > 0) {
//					String config_1_4 = newProcessName + "_质检";
//					configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANXIANGMUMINGCHENG.getValue(), config_1_4));
//					
//					//add by lianhr begin 2019/02/14
//					configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BANGDINGZILIAO.getValue(), strBatch));
//					configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANMOSHI.getValue(), strModel));
//					//add by lianhr end
//					
//					ProjectModel pro = new ProjectModel();
//					pro.setId(projectid332);
//					pro.setName(config_1_4);
//					pro.setPriority(priority);
//					projectModelDao.updateByPrimaryKeySelective(pro);
//				}
//			}

			// TODO: 新增项目类型需要确定是否有改错过程
			if (isNewProcess) {
				if (type.equals(ProcessType.ERROR.getValue()) ||
					type.equals(ProcessType.NRFC.getValue()) ||
					type.equals(ProcessType.ATTACH.getValue()) ||
					type.equals(ProcessType.POIEDIT.getValue()) ||
					type.equals(ProcessType.GEN.getValue()) ||
					type.equals(ProcessType.AREA.getValue()) ||
					type.equals(ProcessType.ATTACHWITHDATA.getValue())||
					type.equals(ProcessType.POIPOLYMERIZE.getValue())	//byhxz
					) {
					ProjectModel newpro = new ProjectModel();
					newpro.setProcessid(newProcessID);
					newpro.setName(newProcessName + suffix);
					newpro.setSystemid(systemid);
					newpro.setProtype(type);
					newpro.setPdifficulty(0);
					newpro.setTasknum(-1);
					newpro.setOverstate(0);
					newpro.setCreateby(uid);
					newpro.setPriority(priority);
					newpro.setOwner(owner);

					if (projectModelDao.insert(newpro) > 0) {
						projectid349 = newpro.getId();
					}
					if (projectid349 > 0) {
						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJIXIANGMUID.getValue(), projectid349.toString()));
						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue(), newProcessName + suffix));
					}
				}
			} else {
				if (type.equals(ProcessType.ERROR.getValue()) ||
					type.equals(ProcessType.NRFC.getValue()) ||
					type.equals(ProcessType.ATTACH.getValue()) ||
					type.equals(ProcessType.POIEDIT.getValue()) ||
					type.equals(ProcessType.GEN.getValue()) ||
					type.equals(ProcessType.AREA.getValue()) ||
					type.equals(ProcessType.ATTACHWITHDATA.getValue())||
					type.equals(ProcessType.POIPOLYMERIZE.getValue())
						) {
					configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue(), newProcessName + suffix));
	
					ProjectModel pro = new ProjectModel();
					pro.setId(projectid349);
					pro.setName(newProcessName + suffix);
					pro.setPriority(priority);
					pro.setOwner(owner);
					projectModelDao.updateByPrimaryKeySelective(pro);
				}
			}

			// TODO: 新增项目类型需要配置人员信息
			// 改错人员
			if (strWorkers != null && !strWorkers.isEmpty()) {
				List<EmployeeModel> workers = new ArrayList<EmployeeModel>();
				Map<String, Object> jsonWorkers = (Map<String, Object>)JSONObject.fromObject(strWorkers);
				for (String jsonWorker : jsonWorkers.keySet()) {
					EmployeeModel worker = new EmployeeModel();
					worker.setId(Integer.valueOf(jsonWorker));
					workers.add(worker);
				}
				
				if (projectid332.compareTo(0L) > 0) {
					ProjectsUserModel record = new ProjectsUserModel();
					record.setPid(projectid332.toString());
					record.setRoleid(RoleType.ROLE_WORKER.getValue());
					if (projectsUserModelDao.delete(record ) >= 0) {
						for (EmployeeModel worker : workers) {
							ProjectsUserModel ur = new ProjectsUserModel();
							ur.setPid(projectid332.toString());
							ur.setUsername(worker.getRealname());
							ur.setUserid(worker.getId());
							ur.setRoleid(RoleType.ROLE_WORKER.getValue());
							ur.setRolename(RoleType.ROLE_WORKER.getDes());
							ur.setOpuid(uid);
							ur.setSystemid(systemid);
							projectsUserModelDao.insert(ur);
						}
					}
				}
				
				if (projectid349.compareTo(0L) > 0) {
					ProjectsUserModel record = new ProjectsUserModel();
					record.setPid(projectid349.toString());
					record.setRoleid(RoleType.ROLE_WORKER.getValue());
					if (projectsUserModelDao.delete(record ) >= 0) {
						for (EmployeeModel worker : workers) {
							ProjectsUserModel ur = new ProjectsUserModel();
							ur.setPid(projectid349.toString());
							ur.setUsername(worker.getRealname());
							ur.setUserid(worker.getId());
							ur.setRoleid(RoleType.ROLE_WORKER.getValue());
							ur.setRolename(RoleType.ROLE_WORKER.getDes());
							ur.setOpuid(uid);
							ur.setSystemid(systemid);
							projectsUserModelDao.insert(ur);
						}
					}
				}
			}
			//更新校正人员
			if (strCheckers != null && !strCheckers.isEmpty()) {
				List<EmployeeModel> checkers = new ArrayList<EmployeeModel>();
				for (String strChecker : strCheckers.split(",")) {
					EmployeeModel checker = new EmployeeModel();
					checker.setId(Integer.valueOf(strChecker));
					checkers.add(checker);
				}
				
				if (projectid332.compareTo(0L) > 0) {
					ProjectsUserModel record = new ProjectsUserModel();
					record.setPid(projectid332.toString());
					record.setRoleid(RoleType.ROLE_CHECKER.getValue());
					if (projectsUserModelDao.delete(record ) >= 0) {
						for (EmployeeModel checker : checkers) {
							ProjectsUserModel ur = new ProjectsUserModel();
							ur.setPid(projectid332.toString());
							ur.setUsername(checker.getRealname());
							ur.setUserid(checker.getId());
							ur.setRoleid(RoleType.ROLE_CHECKER.getValue());
							ur.setRolename(RoleType.ROLE_CHECKER.getDes());
							ur.setOpuid(uid);
							ur.setSystemid(systemid);
							projectsUserModelDao.insert(ur);
						}
					}
				}
				
				if (projectid349.compareTo(0L) > 0) {
					ProjectsUserModel record = new ProjectsUserModel();
					record.setPid(projectid349.toString());
					record.setRoleid(RoleType.ROLE_CHECKER.getValue());
					if (projectsUserModelDao.delete(record ) >= 0) {
						for (EmployeeModel checker : checkers) {
							ProjectsUserModel ur = new ProjectsUserModel();
							ur.setPid(projectid349.toString());
							ur.setUsername(checker.getRealname());
							ur.setUserid(checker.getId());
							ur.setRoleid(RoleType.ROLE_CHECKER.getValue());
							ur.setRolename(RoleType.ROLE_CHECKER.getDes());
							ur.setOpuid(uid);
							ur.setSystemid(systemid);
							projectsUserModelDao.insert(ur);
						}
					}
				}
			}
			
			// TODO: 新增项目类型需要确定是否资料相关
			if (strDatasets != null && !strDatasets.isEmpty()) {
				configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BANGDINGZILIAO.getValue(), strDatasets));
//				if (!isNewProcess && type.equals(ProcessType.POIEDIT.getValue())) {
				if (!isNewProcess && type.equals(ProcessType.POIPOLYMERIZE.getValue())) {
					ProcessConfigValueModel processConfigValueModels = processConfigValueModelDao.selectByProcessIDAndConfigID(newProcessID, ProcessConfigEnum.BANGDINGZILIAO.getValue());
					if (processConfigValueModels != null && processConfigValueModels.getValue() != null && !processConfigValueModels.getValue().isEmpty()) {
						ArrayList<String> newDatasets = new ArrayList<String>(Arrays.asList(strDatasets.split(",")));
						ArrayList<String> oldDatasets = new ArrayList<String>(Arrays.asList(processConfigValueModels.getValue().split(",")));
						newDatasets.removeAll(oldDatasets);
						if (newDatasets != null && !newDatasets.isEmpty()) {
							ProcessModel process = new ProcessModel();
							process.setId(newProcessID);
							process.setState(ProcessState.NEW.getValue());
							processModelDao.updateByPrimaryKeySelective(process);
						}
					} else {
						ProcessModel process = new ProcessModel();
						process.setId(newProcessID);
						process.setState(ProcessState.NEW.getValue());
						processModelDao.updateByPrimaryKeySelective(process);
					}
				}
			}

			// TODO: 新增项目类型需要指定其他配置项
			List<ProcessConfigModel> processConfigs = processConfigModelService.selectAllProcessConfigModels(type);
			for (ProcessConfigModel processConfig : processConfigs) {
				Integer moduleid = processConfig.getModuleid();
				Integer configid = processConfig.getId();
				String defaultValue = processConfig.getDefaultValue() == null ? new String() : processConfig.getDefaultValue().toString();

				// 这是前边代码特殊处理的部分配置
				if ((moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANXIANGMUID.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANXIANGMUMINGCHENG.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIXIANGMUID.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BANGDINGZILIAO.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANMOSHI.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJILEIXING.getValue())))
					continue;
				
				// 这是不能修改的默认配置，这些配置项保留创建任务之初的时候的配置，不再根据系统配置的修改而变动了
				if (!isNewProcess &&
						((moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANRENWUKU.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANQIDONGLEIXING.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIRENWUKU.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.CUOWUGESHU.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.CUOWUJULI.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.CUOWUKU.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIQIDONGLEIXING.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZILIAOKU.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJISHUJUKU.getValue()))))
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
				ret = processConfigValueModelDao.insert(configValues);
			}
			
			if (isNewProcess) {
				System.out.println("########################################################");
				System.out.println("=============create task================================");
				ProcessType processType = ProcessType.POIPOLYMERIZE;
				ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZILIAOKU,
						processType);
				ConfigDBModel configDBModel = configDBModelDao
						.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

				List<keywordModelForTask> keyids = datasetModelDao.selectKeyidsbyDataset(configDBModel, 0, 0,  Long.valueOf(strDatasets));
				int shapeCount = keyids.size();
				int taskcount = 0;
				if (shapeCount > 0) {
					ConfigDBModel configDBModelForTask = configDBModelDao.selectByPrimaryKey(55);
					for (int indexShape = 0; indexShape < shapeCount; indexShape++) {
						keywordModelForTask task = keyids.get(indexShape);
						Long shapeid = task.getId();
						Boolean bSuccess = taskModelDao.InsertNewTask(configDBModelForTask, projectid349, shapeid);
						if (bSuccess)
							taskcount++;
					}
				}
				//创建的任务数等于资料数
				if (shapeCount == taskcount) {
					ProjectModel pro = new ProjectModel();
					pro.setId(projectid349);
					pro.setTasknum(taskcount);
					pro.setOverstate(1);
					projectModelDao.updateByPrimaryKeySelective(pro);
					
					//更新状态
					ProcessModel process = new ProcessModel();
					process.setId(newProcessID);
					process.setState(1);
					processModelDao.updateByPrimaryKeySelective(process);
					
					//更新资料状态 任务创建完成
					datasetModelDao.updateDataSetStatebyDataset(configDBModel, Long.valueOf(strDatasets),3,3);
					
				} else {
					//更新资料状态任务创建 异常
					datasetModelDao.updateDataSetStatebyDataset(configDBModel, Long.valueOf(strDatasets),2,3);
					System.out.println("有资料未创建任务");
				}
			}

			//=================================================================
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("result", -1);
			json.addObject("option", e.getMessage());
			return json;
		}
		
		json.addObject("result", ret);
		json.addObject("pid", newProcessID);

		logger.debug("ProcessesManageCtrl-createNewProcess end.");
		return json;
	}
	
	@RequestMapping(params = "atn=getRNByProcessID")
	public ModelAndView getRowNumByProcessID(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getRowNumByProcessID start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer ret = -1;
		try {
			Long processid = ParamUtils.getLongParameter(request, "processid", -1);
			ret = processModelDao.getRowNumByByPrimaryKey(processid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1;
		}
		json.addObject("ret", ret);

		logger.debug("ProcessesManageCtrl-getRowNumByProcessID end.");
		return json;
	}

	@RequestMapping(params = "atn=changeState")
	public ModelAndView changeState(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-changeState start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer ret = -1;
		//hxz项目管理操作中的 暂停按钮触发这里
		try {
			Long processid = ParamUtils.getLongParameter(request, "processid", -1);
			Integer state = ParamUtils.getIntParameter(request, "state", -1);

			ProcessModel record = new ProcessModel();
			record.setId(processid);
			record.setState(state);
			if (processModelDao.updateByPrimaryKeySelective(record) > 0) {
				ProjectModel project = new ProjectModel();
				//add by lianhr begin 2018/01/17
				if(state.equals(ProcessState.COMPLETE.getValue())) {
					state = ProjectState.COMPLETE.getValue();
				}
				//add by lianhr end
				project.setOverstate(state);
				ProjectModelExample example = new ProjectModelExample();
				com.emg.poiwebeditor.pojo.ProjectModelExample.Criteria criteria = example.or();
				criteria.andProcessidEqualTo(processid);
				criteria.andOverstateNotEqualTo(ProjectState.COMPLETE.getValue());
				ret = projectModelDao.updateByExampleSelective(project, example);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1;
		}
		json.addObject("ret", ret);

		logger.debug("ProcessesManageCtrl-changeState end.");
		return json;
	}

	@RequestMapping(params = "atn=changePriority")
	public ModelAndView changePriority(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-changePriority start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer ret = -1;
		try {
			Long processid = ParamUtils.getLongParameter(request, "processid", -1);
			Integer priority = ParamUtils.getIntParameter(request, "priority", -1);

			ProcessModel record = new ProcessModel();
			record.setId(processid);
			record.setPriority(priority);
			if (processModelDao.updateByPrimaryKeySelective(record) > 0) {
				ProjectModel project = new ProjectModel();
				project.setPriority(priority);
				ProjectModelExample example = new ProjectModelExample();
				example.or().andProcessidEqualTo(processid);
				ret = projectModelDao.updateByExampleSelective(project, example);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1;
		}
		json.addObject("ret", ret);

		logger.debug("ProcessesManageCtrl-changePriority end.");
		return json;
	}

	@RequestMapping(params = "atn=getconfigvalues")
	public ModelAndView getProcessConfigValues(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getProcessConfigValues start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ProcessConfigValueModel> configValues = new ArrayList<ProcessConfigValueModel>();
		try {
			Long processid = ParamUtils.getLongParameter(request, "processid", -1);
			configValues = processConfigValueModelDao.selectByProcessID(processid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			configValues = new ArrayList<ProcessConfigValueModel>();
		}
		json.addObject("configValues", configValues);

		logger.debug("ProcessesManageCtrl-getProcessConfigValues end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getworkersandcheckers")
	public ModelAndView getWorkersAndCheckers(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getWorkersAndCheckers start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<EmployeeModel> workersandcheckers = new ArrayList<EmployeeModel>();
		try {
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			EmployeeModel employeeModel = new EmployeeModel();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						employeeModel.setId(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "realname":
						employeeModel.setRealname(filterPara.get(key).toString());
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}
			
			UserRoleModel record = new UserRoleModel();
			record.setRoleid(RoleType.ROLE_WORKER.getValue());
			List<UserRoleModel> userRoleModels = userRoleModelDao.query(record );
			
			List<Integer> ids = new ArrayList<Integer>();
			for (UserRoleModel userRoleModel : userRoleModels) {
				Integer id = userRoleModel.getUserid();
				ids.add(id);
			}
			
			record.setRoleid(RoleType.ROLE_CHECKER.getValue());
			userRoleModels.clear();
			userRoleModels = userRoleModelDao.query(record );
			for (UserRoleModel userRoleModel : userRoleModels) {
				Integer id = userRoleModel.getUserid();
				ids.add(id);
			}
			
			if (employeeModel.getId() != null) {
				if (ids.contains(employeeModel.getId())) {
					ids.clear();
					ids.add(employeeModel.getId());
				} else {
					json.addObject("rows", workersandcheckers);
					json.addObject("count", workersandcheckers.size());
					json.addObject("result", 1);
					return json;
				}
			}

			if (ids.size() > 0)
				workersandcheckers = emapgoAccountService.getEmployeesByIDSAndRealname(ids, employeeModel.getRealname());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", workersandcheckers);
		json.addObject("count", workersandcheckers.size());
		json.addObject("result", 1);

		logger.debug("ProcessesManageCtrl-getWorkersAndCheckers end.");
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getworkers")
	public ModelAndView getWorkers(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<EmployeeModel> workers = new ArrayList<EmployeeModel>();
		try {
			String filter = ParamUtils.getParameter(request, "filter", new String());

			Map<String, Object> filterPara = null;
			EmployeeModel employeeModel = new EmployeeModel();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						employeeModel.setId(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "realname":
						employeeModel.setRealname(filterPara.get(key).toString());
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}
			
			UserRoleModel record = new UserRoleModel();
			record.setRoleid(RoleType.ROLE_WORKER.getValue());
			List<UserRoleModel> userRoleModels = userRoleModelDao.query(record );
			
			List<Integer> ids = new ArrayList<Integer>();
			for (UserRoleModel userRoleModel : userRoleModels) {
				Integer id = userRoleModel.getUserid();
				ids.add(id);
			}
			
			if (employeeModel.getId() != null) {
				if (ids.contains(employeeModel.getId())) {
					ids.clear();
					ids.add(employeeModel.getId());
				} else {
					json.addObject("rows", workers);
					json.addObject("count", workers.size());
					json.addObject("result", 1);
					return json;
				}
			}

			if (ids.size() > 0)
				workers = emapgoAccountService.getEmployeesByIDSAndRealname(ids, employeeModel.getRealname());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", workers);
		json.addObject("count", workers.size());
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getcheckers")
	public ModelAndView getCheckers(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<EmployeeModel> checkers = new ArrayList<EmployeeModel>();
		try {
			String filter = ParamUtils.getParameter(request, "filter", new String());

			Map<String, Object> filterPara = null;
			EmployeeModel employeeModel = new EmployeeModel();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						employeeModel.setId(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "realname":
						employeeModel.setRealname(filterPara.get(key).toString());
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}
			
			UserRoleModel record = new UserRoleModel();
			record.setRoleid(RoleType.ROLE_CHECKER.getValue());
			List<UserRoleModel> userRoleModels = userRoleModelDao.query(record );
			
			List<Integer> ids = new ArrayList<Integer>();
			for (UserRoleModel userRoleModel : userRoleModels) {
				Integer id = userRoleModel.getUserid();
				ids.add(id);
			}
			
			if (employeeModel.getId() != null) {
				if (ids.contains(employeeModel.getId())) {
					ids.clear();
					ids.add(employeeModel.getId());
				} else {
					json.addObject("rows", checkers);
					json.addObject("count", checkers.size());
					json.addObject("result", 1);
					return json;
				}
			}

			if (ids.size() > 0)
				checkers = emapgoAccountService.getEmployeesByIDSAndRealname(ids, employeeModel.getRealname());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", checkers);
		json.addObject("count", checkers.size());
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	//创建任务的时候绑定资料面板获取数据
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getdatasets")
	public ModelAndView getDatasets(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<DatasetModel> datasetModels = new ArrayList<DatasetModel>();
		Integer count = 0;
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", -1);
			Integer offset = ParamUtils.getIntParameter(request, "offset", -1);
			String filter = ParamUtils.getParameter(request, "filter", "");
			
			ProcessType processType = ProcessType.POIPOLYMERIZE;

			Map<String, Object> filterPara = null;
			DatasetModel record = new DatasetModel();
			
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					String value = filterPara.get(key).toString();
					switch (key) {
					case "id":
						record.setId(Long.valueOf(value));
						break;
					case "name":
						record.setName(value);
						break;
					case "datatype":
						record.setDatatype(Integer.valueOf(value));
						break;
					case "batchid":
						record.setBatchid(value);
						break;
					case "path":
						record.setPath(value);
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZILIAOKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//   不需要通用查询byhxz20190517
//			datasetModels = datasetModelDao.selectDatasets(configDBModel, new ArrayList<Integer>(getDataTypesByProcessType(processType)), record, limit, offset);
//			count = datasetModelDao.countErrorSets(configDBModel, new ArrayList<Integer>(getDataTypesByProcessType(processType)), record, limit, offset);
	
			//只需查询能创建任务的dataset
			datasetModels = datasetModelDao.selectOkDatasets(configDBModel, new ArrayList<Integer>(getDataTypesByProcessType(processType)), limit, offset);
			count = datasetModelDao.countOKDataSets(configDBModel, new ArrayList<Integer>(getDataTypesByProcessType(processType)));
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", datasetModels);
		json.addObject("total", count);
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}

	private Set<Integer> getDataTypesByProcessType(ProcessType processType) {
		Set<Integer> set = new HashSet<Integer>();
		switch (processType) {
		case POIPOLYMERIZE:
			set.add(36);
			break;
		case POIEDIT:
			set.add(14);
			set.add(31);
			set.add(32);
			set.add(35);
			break;
		case GEN:
			set.add(22);
			break;
		default:
			break;
		}
		return set;
	}
}
