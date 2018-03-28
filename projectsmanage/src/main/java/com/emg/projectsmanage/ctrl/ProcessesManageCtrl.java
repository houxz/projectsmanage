package com.emg.projectsmanage.ctrl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.Common;
import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.common.ItemSetEnable;
import com.emg.projectsmanage.common.ItemSetSysType;
import com.emg.projectsmanage.common.ItemSetType;
import com.emg.projectsmanage.common.ItemSetUnit;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.PriorityLevel;
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.common.ItemAreaType;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.common.SystemType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.ItemAreaModel;
import com.emg.projectsmanage.pojo.ItemSetModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;
import com.emg.projectsmanage.pojo.ProcessConfigValueModel;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;
import com.emg.projectsmanage.pojo.ProjectModel;
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

			ProcessConfigModel config332 = processConfigModelDao.selectByPrimaryKey(1);
			ConfigDBModel configDBModel332 = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config332.getDefaultValue()));
			if (isNewProcess) {
				if(!type.equals(ProcessType.NRFC.getValue())) {
					String config_1_4 = newProcessName + "_质检";

					ProjectModel newpro = new ProjectModel();
					newpro.setName(config_1_4);
					newpro.setSystemid(SystemType.DBMapChecker.getValue());
					newpro.setCreateby(uid);
					newpro.setPriority(priority);

					projectid332 = newProject(configDBModel332, newpro);
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
				if(!type.equals(ProcessType.NRFC.getValue())) {
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
					updateProject(configDBModel332, pro);
				}
			}

			ProcessConfigModel config349 = processConfigModelDao.selectByPrimaryKey(9);
			ConfigDBModel configDBModel349 = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config349.getDefaultValue()));
			if (isNewProcess) {
				String config_2_12 = type.equals(ProcessType.NRFC.getValue()) ? (newProcessName + "_NR/FC") : (newProcessName + "_改错");
				Integer systemid = type.equals(ProcessType.NRFC.getValue()) ? SystemType.MapDbEdit_NRFC.getValue() : SystemType.MapDbEdit.getValue();
				
				ProjectModel newpro = new ProjectModel();
				newpro.setName(config_2_12);
				newpro.setSystemid(systemid);
				newpro.setCreateby(uid);
				newpro.setPriority(priority);
				newpro.setOwner(owner);

				projectid349 = newProject(configDBModel349, newpro);
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
					_configValue.setValue(config_2_12);

					configValues.add(_configValue);
				}
			} else {
				String config_2_12 = type.equals(ProcessType.NRFC.getValue()) ? (newProcessName + "_NR/FC") : (newProcessName + "_改错");

				ProcessConfigValueModel _configValue = new ProcessConfigValueModel();
				_configValue.setProcessid(newProcessID);
				_configValue.setModuleid(2);
				_configValue.setConfigid(12);
				_configValue.setValue(config_2_12);

				configValues.add(_configValue);

				ProjectModel pro = new ProjectModel();
				pro.setId(projectid349);
				pro.setName(config_2_12);
				pro.setPriority(priority);
				pro.setOwner(owner);
				updateProject(configDBModel349, pro);
			}

			if (strWorkers != null && !strWorkers.isEmpty()) {
				List<EmployeeModel> workers = new ArrayList<EmployeeModel>();
				for (String strWorker : strWorkers.split(",")) {
					EmployeeModel worker = new EmployeeModel();
					worker.setId(Integer.valueOf(strWorker));
					workers.add(worker);
				}
				setWorkers(configDBModel349, projectid349, workers, uid, SystemType.MapDbEdit.getValue());
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
				List<ProcessConfigValueModel> configValues = processConfigValueModelDao.selectByProcessID(processid);
				Long projectid332 = -1L;
				Long projectid349 = -1L;
				Integer configDBid332 = -1;
				Integer configDBid349 = -1;
				for (ProcessConfigValueModel configValue : configValues) {
					if (configValue.getModuleid().equals(1) && configValue.getConfigid().equals(1)) {
						configDBid332 = Integer.valueOf(configValue.getValue());
					} else if (configValue.getModuleid().equals(1) && configValue.getConfigid().equals(3)) {
						projectid332 = Long.valueOf(configValue.getValue());
					} else if (configValue.getModuleid().equals(2) && configValue.getConfigid().equals(9)) {
						configDBid349 = Integer.valueOf(configValue.getValue());
					} else if (configValue.getModuleid().equals(2) && configValue.getConfigid().equals(11)) {
						projectid349 = Long.valueOf(configValue.getValue());
					}
				}
				ConfigDBModel configDBModel332 = configDBModelDao.selectByPrimaryKey(configDBid332);
				ConfigDBModel configDBModel349 = configDBModelDao.selectByPrimaryKey(configDBid349);
				ProjectModel pro332 = new ProjectModel();
				pro332.setId(projectid332);
				pro332.setOverstate(state);
				updateProject(configDBModel332, pro332);
				ProjectModel pro349 = new ProjectModel();
				pro349.setId(projectid349);
				pro349.setOverstate(state);
				updateProject(configDBModel349, pro349);
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
				List<ProcessConfigValueModel> configValues = processConfigValueModelDao.selectByProcessID(processid);
				Long projectid332 = -1L;
				Long projectid349 = -1L;
				Integer configDBid332 = -1;
				Integer configDBid349 = -1;
				for (ProcessConfigValueModel configValue : configValues) {
					if (configValue.getModuleid().equals(1) && configValue.getConfigid().equals(1)) {
						configDBid332 = Integer.valueOf(configValue.getValue());
					} else if (configValue.getModuleid().equals(1) && configValue.getConfigid().equals(3)) {
						projectid332 = Long.valueOf(configValue.getValue());
					} else if (configValue.getModuleid().equals(2) && configValue.getConfigid().equals(9)) {
						configDBid349 = Integer.valueOf(configValue.getValue());
					} else if (configValue.getModuleid().equals(2) && configValue.getConfigid().equals(11)) {
						projectid349 = Long.valueOf(configValue.getValue());
					}
				}
				ConfigDBModel configDBModel332 = configDBModelDao.selectByPrimaryKey(configDBid332);
				ConfigDBModel configDBModel349 = configDBModelDao.selectByPrimaryKey(configDBid349);
				ProjectModel pro332 = new ProjectModel();
				pro332.setId(projectid332);
				pro332.setPriority(priority);
				updateProject(configDBModel332, pro332);
				ProjectModel pro349 = new ProjectModel();
				pro349.setId(projectid349);
				pro349.setPriority(priority);
				updateProject(configDBModel349, pro349);
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

			itemAreas = getItemAreas(configDBModel, type, itemAreaModel);
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
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
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

			itemsets = getItemsets(configDBModel, type, itemSetModel);
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

			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(1);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			workers = getWorkers(configDBModel, employeeModel);
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

	private BasicDataSource getDataSource(ConfigDBModel configDBModel) {
		BasicDataSource dataSource = new BasicDataSource();
		Integer dbtype = configDBModel.getDbtype();
		if (dbtype.equals(DatabaseType.MYSQL.getValue())) {
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		} else if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
			dataSource.setDriverClassName("org.postgresql.Driver");
		} else {
			return null;
		}
		dataSource.setUrl(getUrl(configDBModel));
		dataSource.setUsername(configDBModel.getUser());
		dataSource.setPassword(configDBModel.getPassword());
		return dataSource;
	}

	private String getUrl(ConfigDBModel configDBModel) {
		StringBuffer url = new StringBuffer();
		try {
			Integer dbtype = configDBModel.getDbtype();
			if (dbtype.equals(DatabaseType.MYSQL.getValue())) {
				url.append("jdbc:mysql://");
			} else if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				url.append("jdbc:postgresql://");
			} else {
				return null;
			}
			url.append(configDBModel.getIp());
			url.append(":");
			url.append(configDBModel.getPort());
			url.append("/");
			url.append(configDBModel.getDbname());
			url.append("?characterEncoding=UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return new String();
		}
		return url.toString();
	}

	private List<ItemAreaModel> getItemAreas(ConfigDBModel configDBModel, Integer type, ItemAreaModel itemArea) {
		List<ItemAreaModel> list = new ArrayList<ItemAreaModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			
			String separator = Common.getDatabaseSeparator(dbtype);
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DISTINCT ON (" + separator + "province" + separator + "," + separator + "city" + separator + "," + separator + "type" + separator + ") * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_city ");
			sql.append(" WHERE " + separator + "type" + separator + " != 3 ");
			if (itemArea.getId() != null) {
				sql.append(" AND " + separator + "id" + separator + " = " + itemArea.getId());
			}
			if (itemArea.getType() != null) {
				sql.append(" AND " + separator + "type" + separator + " = " + itemArea.getType());
			}
			if (itemArea.getProvince() != null) {
				sql.append(" AND " + separator + "province" + separator + " like '%" + itemArea.getProvince() + "%'");
			}
			if (itemArea.getCity() != null) {
				sql.append(" AND " + separator + "city" + separator + " like '%" + itemArea.getCity() + "%'");
			}
			if (type.equals(1)) {

			} else if (type.equals(2)) {

			} else if (type.equals(3)) {
				sql.append(" AND " + separator + "type" + separator + " = 2 ");
			} else {
				return list;
			}
			sql.append(" ORDER BY " + separator + "type" + separator + "," + separator + "province" + separator + "," + separator + "city" + separator + "");

			dataSource = getDataSource(configDBModel);
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemAreaModel>(ItemAreaModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<ItemAreaModel>();
		} finally {
			try {
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private List<ItemSetModel> getItemsets(ConfigDBModel configDBModel, Integer type, ItemSetModel itemset) {
		List<ItemSetModel> list = new ArrayList<ItemSetModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			
			String separator = Common.getDatabaseSeparator(dbtype);
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemset ");
			sql.append(" WHERE 1=1 ");
			if (itemset.getId() != null) {
				sql.append(" AND " + separator + "id" + separator + " = " + itemset.getId());
			}
			if (itemset.getName() != null) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + itemset.getName() + "%'");
			}
			if (itemset.getLayername() != null) {
				sql.append(" AND " + separator + "layername" + separator + " like '%" + itemset.getLayername() + "%'");
			}
			if (itemset.getType() != null) {
				sql.append(" AND " + separator + "type" + separator + " = " + itemset.getType());
			}
			if (itemset.getSystype() != null) {
				sql.append(" AND " + separator + "systype" + separator + " = " + itemset.getSystype());
			}
			if (itemset.getReferdata() != null) {
				sql.append(" AND " + separator + "referdata" + separator + " like '%" + itemset.getReferdata() + "%'");
			}
			if (itemset.getUnit() != null) {
				sql.append(" AND " + separator + "unit" + separator + " = " + itemset.getUnit());
			}
			if (itemset.getDesc() != null) {
				sql.append(" AND " + separator + "desc" + separator + " like '%" + itemset.getDesc() + "%'");
			}

			dataSource = getDataSource(configDBModel);
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemSetModel>(ItemSetModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<ItemSetModel>();
		} finally {
			try {
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private Long newProject(ConfigDBModel configDBModel, final ProjectModel newProject) {
		Long newProjectID = -1L;
		BasicDataSource dataSource = null;
		try {
			final StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO tb_projects (protype, pdifficulty, priority, tasknum, systemid, description, createby, area, name, owner, overprogress, overstate) ");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ");

			KeyHolder keyHolder = new GeneratedKeyHolder();
			dataSource = getDataSource(configDBModel);
			new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					ps.setInt(1, 0);
					ps.setInt(2, 0);
					ps.setInt(3, newProject.getPriority() == null ? 0 : newProject.getPriority());
					ps.setInt(4, 0);
					ps.setInt(5, newProject.getSystemid() == null ? 0 : newProject.getSystemid());
					ps.setString(6, new String());
					ps.setInt(7, newProject.getCreateby() == null ? 0 : newProject.getCreateby());
					ps.setString(8, new String());
					ps.setString(9, newProject.getName() == null ? new String() : newProject.getName());
					ps.setInt(10, newProject.getOwner() == null ? 0 : newProject.getOwner());
					ps.setString(11, new String());
					ps.setInt(12, 0);
					return ps;
				}
			}, keyHolder);
			newProjectID = keyHolder.getKey().longValue();
		} catch (Exception e) {
			e.printStackTrace();
			newProjectID = -1L;
		} finally {
			try {
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return newProjectID;
	}

	private Integer updateProject(ConfigDBModel configDBModel, ProjectModel project) {
		Integer ret = -1;
		BasicDataSource dataSource = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE tb_projects ");
			sql.append(" SET id = id");
			if (project.getName() != null) {
				sql.append(", name = '" + project.getName() + "'");
			}
			if (project.getPriority() != null) {
				sql.append(", priority = " + project.getPriority());
			}
			if (project.getOverstate() != null) {
				sql.append(", overstate = " + project.getOverstate());
			}
			if (project.getOwner() != null) {
				sql.append(", owner = " + project.getOwner());
			}
			sql.append(" WHERE id = " + project.getId());

			dataSource = getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			try {
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	private List<EmployeeModel> getWorkers(ConfigDBModel configDBModel, EmployeeModel employeeModel) {
		List<EmployeeModel> workers = new ArrayList<EmployeeModel>();
		BasicDataSource dataSource = null;
		try {
			List<Integer> ids = new ArrayList<Integer>();
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_user_roles ");
			sql.append(" WHERE roleid in ( " + RoleType.ROLE_WORKER.getValue() + " , " + RoleType.ROLE_CHECKER.getValue() + " )");

			dataSource = getDataSource(configDBModel);
			List<UserRoleModel> userRoleModels = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<UserRoleModel>(UserRoleModel.class));
			for (UserRoleModel userRoleModel : userRoleModels) {
				Integer id = userRoleModel.getUserid();
				ids.add(id);
			}

			if (employeeModel.getId() != null) {
				if (ids.contains(employeeModel.getId())) {
					ids.clear();
					ids.add(employeeModel.getId());
				} else {
					return new ArrayList<EmployeeModel>();
				}
			}

			if (ids.size() > 0)
				workers = emapgoAccountService.getEmployeesByIDSAndRealname(ids, employeeModel.getRealname());
		} catch (Exception e) {
			e.printStackTrace();
			workers = new ArrayList<EmployeeModel>();
		} finally {
			try {
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return workers;
	}

	private Integer setWorkers(ConfigDBModel configDBModel, Long pid, List<EmployeeModel> workers, Integer opuid, Integer systemid) {
		Integer ret = -1;
		if (workers.size() <= 0)
			return ret;
		BasicDataSource dataSource = null;
		try {
			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE ");
			sql_del.append(" FROM tb_projects_user ");
			sql_del.append(" WHERE pid = " + pid);

			dataSource = getDataSource(configDBModel);
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			Integer ret_del = jdbc.update(sql_del.toString());
			if (ret_del >= 0) {
				StringBuffer sql = new StringBuffer();
				sql.append(" INSERT INTO tb_projects_user");
				sql.append(" (pid, userid, username, roleid, rolename, opuid, systemid) ");
				sql.append(" VALUES ");
				for (EmployeeModel worker : workers) {
					sql.append("(");
					sql.append(pid + ", ");
					sql.append(worker.getId() + ", ");
					sql.append("'" + worker.getRealname() + "', ");
					sql.append(RoleType.ROLE_WORKER.getValue() + ", ");
					sql.append("'" + RoleType.ROLE_WORKER.getDes() + "', ");
					sql.append(opuid + ", ");
					sql.append(systemid);
					sql.append(" ),");
				}
				sql.deleteCharAt(sql.length() - 1);
				ret = jdbc.update(sql.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			try {
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
