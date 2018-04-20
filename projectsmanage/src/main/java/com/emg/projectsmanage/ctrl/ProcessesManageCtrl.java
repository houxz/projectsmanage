package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
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
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.PriorityLevel;
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.common.ItemAreaType;
import com.emg.projectsmanage.common.ProjectState;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.common.SystemType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.dao.task.ItemSetModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
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

@Controller
@RequestMapping("/processesmanage.web")
public class ProcessesManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProcessesManageCtrl.class);

	@Autowired
	private ProcessModelDao processModelDao;

	@Autowired
	private ConfigDBModelDao configDBModelDao;

	@Autowired
	private ProcessConfigModelDao processConfigModelDao;

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

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesConfigCtrl-openLader start.");

		model.addAttribute("processStates", ProcessState.toJsonStr());
		model.addAttribute("processTypes", ProcessType.toJsonStr());
		model.addAttribute("itemAreaTypes", ItemAreaType.toJsonStr());
		model.addAttribute("priorityLevels", PriorityLevel.toJsonStr());
		model.addAttribute("itemsetEnables", ItemSetEnable.toJsonStr());
		model.addAttribute("itemsetSysTypes", ItemSetSysType.toJsonStr());
		model.addAttribute("itemsetTypes", ItemSetType.toJsonStr());
		model.addAttribute("itemsetUnits", ItemSetUnit.toJsonStr());

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
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Map<String, Object> filterPara = null;
			ProcessModelExample example = new ProcessModelExample();
			Criteria criteria = example.or();
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
						logger.debug("未处理的筛选项：" + key);
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
			e.printStackTrace();
			logger.debug(e.getMessage());
		}

		logger.debug("ProcessesManageCtrl-pages end.");
		return json;
	}

	@RequestMapping(params = "atn=newprocess")
	public ModelAndView createNewProcess(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-createNewProcess start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Integer ret = -1;
		try {
			Long newProcessID = ParamUtils.getLongParameter(request, "processid", -1L);
			String newProcessName = ParamUtils.getParameter(request, "newProcessName");
			Integer type = ParamUtils.getIntParameter(request, "type", 0);
			Integer priority = ParamUtils.getIntParameter(request, "priority", 0);
			Integer uid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			String username = (String) session.getAttribute(CommonConstants.SESSION_USER_NAME);
			Integer owner = ParamUtils.getIntParameter(request, "config_2_19", 0) == 1 ? 1 : 0;
			Long projectid332 = ParamUtils.getLongParameter(request, "config_1_3", -1L);
			Long projectid349 = ParamUtils.getLongParameter(request, "config_2_11", -1L);
			String strWorkers = ParamUtils.getParameter(request, "config_2_18");

			Boolean isNewProcess = newProcessID.equals(0L);

			if (newProcessID.compareTo(0L) < 0) {
				ret = -1;
				json.addObject("result", ret);
				json.addObject("resultMsg", "保存失败，错误的参数值：processid");
				return json;
			}
			if (newProcessName == null || newProcessName.isEmpty()) {
				ret = -1;
				json.addObject("result", ret);
				json.addObject("resultMsg", "保存失败，项目名称不能为空");
				return json;
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

				if (processModelDao.insertSelective(newProcess) <= 0) {
					ret = -1;
					json.addObject("result", ret);
					json.addObject("resultMsg", "新建项目失败");
					return json;
				}
				newProcessID = newProcess.getId();
				System.out.println("---------------->createNewProcess:  new " + newProcessID);
			} else {
				ProcessModel process = new ProcessModel();
				process.setId(newProcessID);
				process.setType(type);
				process.setName(newProcessName);
				process.setPriority(priority);
				processModelDao.updateByPrimaryKeySelective(process);
				System.out.println("---------------->createNewProcess:  update " + newProcessID);
			}

			List<ProcessConfigValueModel> configValues = new ArrayList<ProcessConfigValueModel>();

			//创建/更新质检项目
			if (isNewProcess) {
				if(!type.equals(ProcessType.NRFC.getValue()) && !type.equals(ProcessType.ATTACH.getValue())) {
					String config_1_4 = newProcessName + "_质检";

					ProjectModel newpro = new ProjectModel();
					newpro.setProcessid(newProcessID);
					newpro.setName(config_1_4);
					newpro.setSystemid(SystemType.DBMapChecker.getValue());
					newpro.setProtype(0);
					newpro.setPdifficulty(0);
					newpro.setTasknum(-1);
					newpro.setOverstate(0);
					newpro.setCreateby(uid);
					newpro.setPriority(priority);
					newpro.setOwner(owner);

					if (projectModelDao.insert(newpro) > 0) {
						projectid332 = newpro.getId();
					}
					if (projectid332 > 0) {
						ProcessConfigValueModel configValue = new ProcessConfigValueModel();
						configValue.setProcessid(newProcessID);
						configValue.setModuleid(1);
						configValue.setConfigid(3);
						configValue.setValue(projectid332.toString());

						configValues.add(configValue);

						ProcessConfigValueModel _configValue = new ProcessConfigValueModel();
						_configValue.setProcessid(newProcessID);
						_configValue.setModuleid(1);
						_configValue.setConfigid(4);
						_configValue.setValue(config_1_4);

						configValues.add(_configValue);
					}
				}
			} else {
				if(!type.equals(ProcessType.NRFC.getValue()) && !type.equals(ProcessType.ATTACH.getValue())) {
					String config_1_4 = newProcessName + "_质检";

					ProcessConfigValueModel _configValue = new ProcessConfigValueModel();
					_configValue.setProcessid(newProcessID);
					_configValue.setModuleid(1);
					_configValue.setConfigid(4);
					_configValue.setValue(config_1_4);

					configValues.add(_configValue);

					ProjectModel pro = new ProjectModel();
					pro.setId(projectid332);
					pro.setName(config_1_4);
					pro.setPriority(priority);
					projectModelDao.updateByPrimaryKeySelective(pro);
				}
			}

			//创建/更新改错项目
			if (isNewProcess) {
				ProjectModel newpro = new ProjectModel();
				newpro.setProcessid(newProcessID);
				newpro.setName(newProcessName + suffix);
				newpro.setSystemid(systemid);
				newpro.setProtype(0);
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
					ProcessConfigValueModel configValue = new ProcessConfigValueModel();
					configValue.setProcessid(newProcessID);
					configValue.setModuleid(2);
					configValue.setConfigid(11);
					configValue.setValue(projectid349.toString());

					configValues.add(configValue);

					ProcessConfigValueModel _configValue = new ProcessConfigValueModel();
					_configValue.setProcessid(newProcessID);
					_configValue.setModuleid(2);
					_configValue.setConfigid(12);
					_configValue.setValue(newProcessName + suffix);

					configValues.add(_configValue);
				}
			} else {

				ProcessConfigValueModel _configValue = new ProcessConfigValueModel();
				_configValue.setProcessid(newProcessID);
				_configValue.setModuleid(2);
				_configValue.setConfigid(12);
				_configValue.setValue(newProcessName + suffix);

				configValues.add(_configValue);

				ProjectModel pro = new ProjectModel();
				pro.setId(projectid349);
				pro.setName(newProcessName + suffix);
				pro.setPriority(priority);
				pro.setOwner(owner);
				projectModelDao.updateByPrimaryKeySelective(pro);
			}

			//更新作业人员
			if (strWorkers != null && !strWorkers.isEmpty()) {
				List<EmployeeModel> workers = new ArrayList<EmployeeModel>();
				for (String strWorker : strWorkers.split(",")) {
					EmployeeModel worker = new EmployeeModel();
					worker.setId(Integer.valueOf(strWorker));
					workers.add(worker);
				}
				
				//setWorkers(configDBModel349, projectid349, workers, uid, SystemType.MapDbEdit.getValue());
				ProjectsUserModel record = new ProjectsUserModel();
				record.setPid(projectid349.toString());
				if (projectsUserModelDao.delete(record ) >= 0) {
					for (EmployeeModel worker : workers) {
						ProjectsUserModel ur = new ProjectsUserModel();
						ur.setPid(projectid349.toString());
						ur.setUsername(worker.getRealname());
						ur.setUserid(worker.getId());
						ur.setRoleid(RoleType.ROLE_WORKER.getValue());
						ur.setRolename(RoleType.ROLE_WORKER.getDes());
						ur.setOpuid(uid);
						ur.setSystemid(SystemType.MapDbEdit.getValue());
						projectsUserModelDao.insert(ur);
					}
				}
			}

			List<ProcessConfigModel> processConfigs = processConfigModelDao.selectAllProcessConfigModels();
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

				ProcessConfigValueModel configValue = new ProcessConfigValueModel();
				configValue.setProcessid(newProcessID);
				configValue.setModuleid(moduleid);
				configValue.setConfigid(configid);
				configValue.setValue(defaultValue);

				configValues.add(configValue);
			}

			if (processConfigValueModelDao.deleteByProcessID(newProcessID) >= 0) {
				ret = processConfigValueModelDao.insert(configValues);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			json.addObject("result", -1);
			json.addObject("option", e.getMessage());
			return json;
		}
		json.addObject("result", ret);

		logger.debug("ProcessesManageCtrl-createNewProcess end.");
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
				project.setOverstate(state);
				ProjectModelExample example = new ProjectModelExample();
				com.emg.projectsmanage.pojo.ProjectModelExample.Criteria criteria = example.or();
				criteria.andProcessidEqualTo(processid);
				criteria.andOverstateNotEqualTo(ProjectState.COMPLETE.getValue());
				ret = projectModelDao.updateByExampleSelective(project, example);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
			logger.debug(e.getMessage());
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
			e.printStackTrace();
			ret = -1;
			logger.debug(e.getMessage());
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
			e.printStackTrace();
			configValues = new ArrayList<ProcessConfigValueModel>();
			logger.debug(e.getMessage());
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
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

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
						logger.debug("未处理的筛选项：" + key);
						break;
					}
				}
			}

			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			itemAreas = itemSetModelDao.getItemAreas(configDBModel, type, itemAreaModel);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
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
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

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
						logger.debug("未处理的筛选项：" + key);
						break;
					}
				}
			}

			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			itemsets = itemSetModelDao.selectItemSets(configDBModel, itemSetModel, -1, -1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", itemsets);
		json.addObject("count", itemsets.size());
		json.addObject("result", 1);

		logger.debug("ProcessesManageCtrl-getItemsets end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getworkers")
	public ModelAndView getWorkers(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getWorkers start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<EmployeeModel> workers = new ArrayList<EmployeeModel>();
		try {
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

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
						logger.debug("未处理的筛选项：" + key);
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
					json.addObject("rows", workers);
					json.addObject("count", workers.size());
					json.addObject("result", 1);
					return json;
				}
			}

			if (ids.size() > 0)
				workers = emapgoAccountService.getEmployeesByIDSAndRealname(ids, employeeModel.getRealname());

		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", workers);
		json.addObject("count", workers.size());
		json.addObject("result", 1);

		logger.debug("ProcessesManageCtrl-getWorkers end.");
		return json;
	}

}
