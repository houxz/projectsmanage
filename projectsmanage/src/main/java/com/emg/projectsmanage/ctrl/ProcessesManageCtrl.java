package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.ItemSetEnable;
import com.emg.projectsmanage.common.ItemSetSysType;
import com.emg.projectsmanage.common.ItemSetType;
import com.emg.projectsmanage.common.ItemSetUnit;
import com.emg.projectsmanage.common.ModelEnum;
import com.emg.projectsmanage.common.OwnerStatus;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.PriorityLevel;
import com.emg.projectsmanage.common.ProcessConfigEnum;
import com.emg.projectsmanage.common.ProcessConfigModuleEnum;
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.common.ItemAreaType;
import com.emg.projectsmanage.common.ProjectState;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.common.SystemType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.dao.task.DatasetModelDao;
import com.emg.projectsmanage.dao.task.ItemSetModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ConfigValueModel;
import com.emg.projectsmanage.pojo.DatasetModel;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.ItemAreaModel;
import com.emg.projectsmanage.pojo.ItemSetModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;
import com.emg.projectsmanage.pojo.ProcessConfigValueModel;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;
import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import com.emg.projectsmanage.pojo.ProjectsUserModel;
import com.emg.projectsmanage.pojo.UserRoleModel;
import com.emg.projectsmanage.pojo.ProcessModelExample.Criteria;
import com.emg.projectsmanage.service.EmapgoAccountService;
import com.emg.projectsmanage.service.ProcessConfigModelService;

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

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesConfigCtrl-openLader start.");

		model.addAttribute("processStates", ProcessState.undoneToJsonStr());
		model.addAttribute("processTypes", ProcessType.toJsonStr());
		model.addAttribute("itemAreaTypes", ItemAreaType.toJsonStr());
		model.addAttribute("priorityLevels", PriorityLevel.toJsonStr());
		model.addAttribute("itemsetEnables", ItemSetEnable.toJsonStr());
		model.addAttribute("itemsetSysTypes", ItemSetSysType.toJsonStr());
		model.addAttribute("itemsetTypes", ItemSetType.toJsonStr());
		model.addAttribute("itemsetUnits", ItemSetUnit.toJsonStr());
		//add by lianhr begin 2019/02/19
		model.addAttribute("itemmodels", ModelEnum.toJsonStr());
		//add by lianhr end

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
			criteria.andStateNotEqualTo(ProcessState.COMPLETE.getValue());
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						criteria.andIdEqualTo(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "type":
						criteria.andTypeEqualTo(Integer.valueOf(filterPara.get(key).toString()));
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
			Integer type = ParamUtils.getIntParameter(request, "type", 0);
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
			
			String suffix = new String();
			Integer systemid = -1;
			
			if(type.equals(ProcessType.ERROR.getValue())){
				suffix = "_改错";
				systemid = SystemType.MapDbEdit.getValue();
			} else if(type.equals(ProcessType.NRFC.getValue())) {
				suffix = "_NR/FC";
				systemid = SystemType.MapDbEdit_NRFC.getValue();
			} else if(type.equals(ProcessType.ATTACH.getValue())) {
				suffix = "_关系附属表";
				systemid = SystemType.MapDbEdit_Attach.getValue();
			} else if(type.equals(ProcessType.COUNTRY.getValue())) {
				suffix = "_全国质检";
				systemid = SystemType.MapDbEdit_Country.getValue();
			} else if(type.equals(ProcessType.POIEDIT.getValue())) {
				suffix = "";
				systemid = SystemType.poivideoedit.getValue();
			} else if(type.equals(ProcessType.GEN.getValue())) {
				suffix = "";
				systemid = SystemType.poi_GEN.getValue();
			}
			//新建/更新流程
			if (isNewProcess) {
				ProcessModel newProcess = new ProcessModel();
				newProcess.setName(newProcessName);
				newProcess.setType(type);
				newProcess.setPriority(priority);
				newProcess.setState(0);
				newProcess.setUserid(uid);
				newProcess.setUsername(username);
				if(type.equals(ProcessType.COUNTRY.getValue())) {
					newProcess.setProgress("0,0,0,0");
				}
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

			//创建/更新质检项目
			if (isNewProcess) {
				if(!type.equals(ProcessType.NRFC.getValue()) && 
					!type.equals(ProcessType.ATTACH.getValue()) &&
					!type.equals(ProcessType.POIEDIT.getValue())) {
					String config_1_4 = newProcessName + "_质检";

					ProjectModel newpro = new ProjectModel();
					newpro.setProcessid(newProcessID);
					newpro.setName(config_1_4);
					newpro.setSystemid(SystemType.DBMapChecker.getValue());
					newpro.setProtype(type);
					newpro.setPdifficulty(0);
					newpro.setTasknum(-1);
					newpro.setOverstate(0);
					newpro.setCreateby(uid);
					newpro.setPriority(priority);
					newpro.setOwner(OwnerStatus.PUBLIC.getValue());

					if (projectModelDao.insert(newpro) > 0) {
						projectid332 = newpro.getId();
					}
					if (projectid332 > 0) {
						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANXIANGMUID.getValue(), projectid332.toString()));
						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANXIANGMUMINGCHENG.getValue(), config_1_4));
						//add by lianhr begin 2019/02/14
						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BANGDINGZILIAO.getValue(), strBatch));
						configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANMOSHI.getValue(), strModel));
						//add by lianhr end
					}
				}
			} else {
				if(!type.equals(ProcessType.NRFC.getValue()) && 
					!type.equals(ProcessType.ATTACH.getValue()) &&
					!type.equals(ProcessType.POIEDIT.getValue()) && projectid332.compareTo(0L) > 0) {
					String config_1_4 = newProcessName + "_质检";
					configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANXIANGMUMINGCHENG.getValue(), config_1_4));
					
					//add by lianhr begin 2019/02/14
					configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BANGDINGZILIAO.getValue(), strBatch));
					configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue(), ProcessConfigEnum.ZHIJIANMOSHI.getValue(), strModel));
					//add by lianhr end
					
					ProjectModel pro = new ProjectModel();
					pro.setId(projectid332);
					pro.setName(config_1_4);
					pro.setPriority(priority);
					projectModelDao.updateByPrimaryKeySelective(pro);
				}
			}

			//创建/更新改错项目
			if (isNewProcess && !type.equals(ProcessType.COUNTRY.getValue())) {
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
			} else if(!isNewProcess) {
				configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue(), newProcessName + suffix));

				ProjectModel pro = new ProjectModel();
				pro.setId(projectid349);
				pro.setName(newProcessName + suffix);
				pro.setPriority(priority);
				pro.setOwner(owner);
				projectModelDao.updateByPrimaryKeySelective(pro);
			}

			//更新编辑人员
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
			
			// 处理资料绑定
			if (strDatasets != null && !strDatasets.isEmpty() && !isNewProcess) {
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

			List<ProcessConfigModel> processConfigs = processConfigModelService.selectAllProcessConfigModels(type);
			for (ProcessConfigModel processConfig : processConfigs) {
				Integer moduleid = processConfig.getModuleid();
				Integer configid = processConfig.getId();
				String defaultValue = processConfig.getDefaultValue() == null ? new String() : processConfig.getDefaultValue().toString();

				// 这是前边代码特殊处理的部分配置
				if ((moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANXIANGMUID.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANXIANGMUMINGCHENG.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIXIANGMUID.getValue())) ||
						(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue())))
					continue;
				
				//add by lianhr begin 2019/02/20
				if(type.equals(ProcessType.COUNTRY.getValue()) && (moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BANGDINGZILIAO.getValue()))) {
					continue;
				}
				//add by lianhr end
				
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
				com.emg.projectsmanage.pojo.ProjectModelExample.Criteria criteria = example.or();
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
	@RequestMapping(params = "atn=getitemareas")
	public ModelAndView getItemAreas(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getItemAreas start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ItemAreaModel> itemAreas = new ArrayList<ItemAreaModel>();
		try {
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ItemAreaModel itemAreaModel = new ItemAreaModel();

			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						itemAreaModel.setId(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "areatype":
						itemAreaModel.setType(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "province":
						itemAreaModel.setProvince(filterPara.get(key).toString());
						break;
					case "city":
						itemAreaModel.setCity(filterPara.get(key).toString());
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			itemAreas = itemSetModelDao.getItemAreas(configDBModel, type, itemAreaModel);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", itemAreas);
		json.addObject("count", itemAreas.size());
		json.addObject("result", 1);

		logger.debug("ProcessesManageCtrl-getItemAreas end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getitemsets")
	public ModelAndView getItemsets(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getItemsets start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ItemSetModel> itemsets = new ArrayList<ItemSetModel>();
		try {
			String filter = ParamUtils.getParameter(request, "filter", "");
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));

			Map<String, Object> filterPara = null;
			ItemSetModel itemSetModel = new ItemSetModel();

			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						itemSetModel.setId(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						itemSetModel.setName(filterPara.get(key).toString());
						break;
					case "layername":
						itemSetModel.setLayername(filterPara.get(key).toString());
						break;
					case "type":
						itemSetModel.setType(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "systype":
						itemSetModel.setSystype(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "referdata":
						itemSetModel.setReferdata(filterPara.get(key).toString());
						break;
					case "unit":
						itemSetModel.setUnit(Byte.valueOf(filterPara.get(key).toString()));
						break;
					case "desc":
						itemSetModel.setDesc(filterPara.get(key).toString());
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			itemsets = itemSetModelDao.selectItemSets(configDBModel, itemSetModel, -1, -1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", itemsets);
		json.addObject("count", itemsets.size());
		json.addObject("result", 1);

		logger.debug("ProcessesManageCtrl-getItemsets end.");
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
			
			ProcessType processType = ProcessType.POIEDIT;

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
						record.setBatchid(Long.valueOf(value));
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

			datasetModels = datasetModelDao.selectDatasets(configDBModel, record, limit, offset);
			count = datasetModelDao.countErrorSets(configDBModel, record, limit, offset);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", datasetModels);
		json.addObject("total", count);
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}

}
