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

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.PriorityLevel;
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.common.ItemAreaType;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.process.CondigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.pojo.CondigDBModel;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.ItemAreaModel;
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
	private CondigDBModelDao condigDBModelDao;
	
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
		model.addAttribute("itemAreaTypes", ItemAreaType.toJsonStr());
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
					case "name":
						criteria.andNameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "state":
						criteria.andStateEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					default:
						break;
					}
				}
			}

			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);
			List<ProcessModel> projectsTaskCountModels = processModelDao.selectByExample(example);
			int count = processModelDao.countByExample(example);

			json.addObject("rows", projectsTaskCountModels);
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
			Integer priority = ParamUtils.getIntParameter(request, "priority", 0);
			Integer uid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			String username = (String) session.getAttribute(CommonConstants.SESSION_USER_NAME);
			Integer owner = ParamUtils.getIntParameter(request, "config_2_19", 0) == 1 ? 1:0;
			
			Boolean isNewProcess = newProcessID.equals(0L);

			if(isNewProcess) {
				ProcessModel newProcess = new ProcessModel();
				newProcess.setName(newProcessName);
				newProcess.setPriority(priority);
				newProcess.setState(0);
				newProcess.setUserid(uid);
				newProcess.setUsername(username);

				if(processModelDao.insertSelective(newProcess) <= 0)	return json;
				newProcessID = newProcess.getId();
				System.out.println("---------------->createNewProcess:  new " + newProcessID);
			} else {
				ProcessModel process = new ProcessModel();
				process.setId(newProcessID);
				process.setName(newProcessName);
				process.setPriority(priority);
				processModelDao.updateByPrimaryKeySelective(process);
				System.out.println("---------------->createNewProcess:  update " + newProcessID);
			}
			
			List<ProcessConfigValueModel> configValues = new ArrayList<ProcessConfigValueModel>();

			if (isNewProcess) {
				ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(1);
				CondigDBModel condigDBModel332 = condigDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
				String config_1_4 = newProcessName + "_质检";
				
				ProjectModel newpro = new ProjectModel();
				newpro.setName(config_1_4);
				newpro.setSystemid(332);
				newpro.setCreateby(uid);
				newpro.setPriority(priority);
				
				Integer projectid332 = newProject(condigDBModel332, newpro);
				if (projectid332 > 0) {
					ProcessConfigValueModel configValue = new ProcessConfigValueModel();
					configValue.setProcessid(newProcessID);
					configValue.setModuleid(1);
					configValue.setConfigid(4);
					configValue.setValue(config_1_4);

					configValues.add(configValue);
					
					ProcessConfigValueModel _configValue = new ProcessConfigValueModel();
					_configValue.setProcessid(newProcessID);
					_configValue.setModuleid(1);
					_configValue.setConfigid(3);
					_configValue.setValue(projectid332.toString());

					configValues.add(_configValue);
				}
			}

			if (isNewProcess) {
				ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(9);
				CondigDBModel condigDBModel349 = condigDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
				String config_2_12 = newProcessName + "_改错";
				
				ProjectModel newpro = new ProjectModel();
				newpro.setName(config_2_12);
				newpro.setSystemid(349);
				newpro.setCreateby(uid);
				newpro.setPriority(priority);
				newpro.setOwner(owner);
				
				Integer projectid349 = newProject(condigDBModel349, newpro);
				if (projectid349 > 0) {
					ProcessConfigValueModel configValue = new ProcessConfigValueModel();
					configValue.setProcessid(newProcessID);
					configValue.setModuleid(2);
					configValue.setConfigid(12);
					configValue.setValue(config_2_12);

					configValues.add(configValue);
					
					ProcessConfigValueModel _configValue = new ProcessConfigValueModel();
					_configValue.setProcessid(newProcessID);
					_configValue.setModuleid(2);
					_configValue.setConfigid(11);
					_configValue.setValue(projectid349.toString());

					configValues.add(_configValue);
				}
			}

			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = paramNames.nextElement();
				if (!paramName.startsWith("config_"))
					continue;

				String[] a = paramName.split("_");
				Integer moduleid = Integer.valueOf(a[1]);
				Integer configid = Integer.valueOf(a[2]);
				String value = ParamUtils.getParameter(request, paramName);

				ProcessConfigValueModel configValue = new ProcessConfigValueModel();
				configValue.setProcessid(newProcessID);
				configValue.setModuleid(moduleid);
				configValue.setConfigid(configid);
				configValue.setValue(value);

				configValues.add(configValue);
			}
			
			if(processConfigValueModelDao.deleteByProcessID(newProcessID) >= 0) {
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

	@RequestMapping(params = "atn=getitemareas")
	public ModelAndView getItemAreas(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getItemAreas start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ItemAreaModel> itemAreas = new ArrayList<ItemAreaModel>();
		try {
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);

			CondigDBModel condigDBModel = condigDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			itemAreas = getItemAreas(condigDBModel, type);
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
	
	@RequestMapping(params = "atn=getworkers")
	public ModelAndView getWorkers(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getWorkers start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<EmployeeModel> workers = new ArrayList<EmployeeModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(1);

			CondigDBModel condigDBModel = condigDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			workers = getWorkers(condigDBModel);
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

	private BasicDataSource getDataSource(String url, String username, String password) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	private String getUrl(CondigDBModel condigDBModel) {
		StringBuffer url = new StringBuffer();
		try {
			url.append("jdbc:mysql://");
			url.append(condigDBModel.getIp());
			url.append(":");
			url.append(condigDBModel.getPort());
			url.append("/");
			url.append(condigDBModel.getDbname());
			url.append("?characterEncoding=UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return new String();
		}
		return url.toString();
	}
	
	private List<ItemAreaModel> getItemAreas(CondigDBModel condigDBModel, Integer type) {
		List<ItemAreaModel> list = new ArrayList<ItemAreaModel>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_city ");
			if (type.equals(1)) {

			} else if (type.equals(2)) {

			} else if (type.equals(3)) {
				sql.append(" WHERE `type` = 2 ");
			} else {
				return list;
			}
			sql.append(" GROUP BY `province`,`city`");
			sql.append(" ORDER BY `type`,`id`");

			BasicDataSource dataSource = getDataSource(getUrl(condigDBModel), condigDBModel.getUser(), condigDBModel.getPassword());
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemAreaModel>(ItemAreaModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<ItemAreaModel>();
		}
		return list;
	}

	private Integer newProject(CondigDBModel condigDBModel, final ProjectModel newProject) {
		Integer newProjectID = -1;
		try{
			final StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO tb_projects (`protype`, `pdifficulty`, `priority`, `tasknum`, `systemid`, `description`, `createby`, `area`, `name`, `owner`, `overprogress`, `overstate`) ");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ");
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			BasicDataSource dataSource = getDataSource(getUrl(condigDBModel), condigDBModel.getUser(), condigDBModel.getPassword());
			new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
	            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
	                PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
	                ps.setInt(1, 0);
	                ps.setInt(2, 0);
	                ps.setInt(3, newProject.getPriority() == null ? 0:newProject.getPriority());
	                ps.setInt(4, 0);
	                ps.setInt(5, newProject.getSystemid() == null ? 0:newProject.getSystemid());
	                ps.setString(6, new String());
	                ps.setInt(7, newProject.getCreateby() == null ? 0:newProject.getCreateby());
	                ps.setString(8, new String());
	                ps.setString(9, newProject.getName() == null ? new String():newProject.getName());
	                ps.setInt(10, newProject.getOwner() == null ? 0:newProject.getOwner());
	                ps.setString(11, new String());
	                ps.setInt(12, 0);
	                return ps;
	            }
			}, keyHolder);
			newProjectID = keyHolder.getKey().intValue();
		} catch (Exception e) {
			e.printStackTrace();
			newProjectID = -1;
		}
		return newProjectID;
	}
	
	private List<EmployeeModel> getWorkers(CondigDBModel condigDBModel) {
		List<EmployeeModel> workers = new ArrayList<EmployeeModel>();
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_user_roles ");
			sql.append(" WHERE `roleid` in ( " + RoleType.ROLE_WORKER.getValue() + " , " + RoleType.ROLE_CHECKER.getValue() + " )");
			
			BasicDataSource dataSource = getDataSource(getUrl(condigDBModel), condigDBModel.getUser(), condigDBModel.getPassword());
			List<UserRoleModel> userRoleModels = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<UserRoleModel>(UserRoleModel.class));
			List<Integer> ids = new ArrayList<Integer>();
			for (UserRoleModel userRoleModel : userRoleModels) {
				Integer id = userRoleModel.getUserid();
				ids.add(id);
			}
			workers = emapgoAccountService.getEmployeeByIDS(ids);
		} catch(Exception e) {
			
		}
		return workers;
	}
}
