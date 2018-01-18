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
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.common.ItemAreaType;
import com.emg.projectsmanage.dao.process.CondigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigValueModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.pojo.CondigDBModel;
import com.emg.projectsmanage.pojo.ItemAreaModel;
import com.emg.projectsmanage.pojo.ItemSetModel;
import com.emg.projectsmanage.pojo.ProcessConfigValueModel;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;
import com.emg.projectsmanage.pojo.ProcessModelExample.Criteria;
import com.emg.projectsmanage.pojo.ProjectModel;

@Controller
@RequestMapping("/processesmanage.web")
public class ProcessesManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProcessesManageCtrl.class);

	@Autowired
	private ProcessModelDao processModelDao;

	@Autowired
	private ProcessConfigModelDao processConfigModelDao;

	@Autowired
	private CondigDBModelDao condigDBModelDao;
	
	@Autowired
	private ProcessConfigValueModelDao processConfigValueModelDao;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesConfigCtrl-openLader start.");

		model.addAttribute("processStates", ProcessState.toJsonStr());
		model.addAttribute("itemAreaTypes", ItemAreaType.toJsonStr());

		List<Map<String, Object>> configDBModels = processConfigModelDao.selectAllConfigDBModels();
		model.addAttribute("configDBModels", configDBModels);

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
			String newProcessName = ParamUtils.getParameter(request, "newProcessName");
			Integer uid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			String username = (String) session.getAttribute(CommonConstants.SESSION_USER_NAME);

			ProcessModel newProcess = new ProcessModel();
			newProcess.setName(newProcessName);
			newProcess.setState(0);
			newProcess.setUserid(uid);
			newProcess.setUsername(username);

			if(processModelDao.insertSelective(newProcess) <= 0)	return json;
			Long newProcessID = newProcess.getId();
			System.out.println("---------------->newProcessID: " + newProcessID);

			Boolean newproject332 = ParamUtils.getBooleanParameter(request, "newproject332");
			Boolean newproject349 = ParamUtils.getBooleanParameter(request, "newproject349");
			Integer config_1_1 = (Integer) ParamUtils.getIntParameter(request, "config_1_1", -1);
			String config_1_4 = ParamUtils.getParameter(request, "config_1_4");
			Integer config_2_9 = (Integer) ParamUtils.getIntParameter(request, "config_2_9", -1);
			String config_2_11 = ParamUtils.getParameter(request, "config_2_11");

			List<ProcessConfigValueModel> configValues = new ArrayList<ProcessConfigValueModel>();

			if (newproject332) {
				CondigDBModel condigDBModel332 = condigDBModelDao.selectByPrimaryKey(config_1_1);
				Integer projectid332 = newProject(condigDBModel332, uid, 332, config_1_4);
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
					_configValue.setConfigid(16);
					_configValue.setValue(projectid332.toString());

					configValues.add(_configValue);
				}
			}

			if (newproject349) {
				CondigDBModel condigDBModel349 = condigDBModelDao.selectByPrimaryKey(config_2_9);
				Integer projectid349 = newProject(condigDBModel349, uid, 349, config_2_11);
				if (projectid349 > 0) {
					ProcessConfigValueModel configValue = new ProcessConfigValueModel();
					configValue.setProcessid(newProcessID);
					configValue.setModuleid(2);
					configValue.setConfigid(11);
					configValue.setValue(config_2_11);

					configValues.add(configValue);
					
					ProcessConfigValueModel _configValue = new ProcessConfigValueModel();
					_configValue.setProcessid(newProcessID);
					_configValue.setModuleid(2);
					_configValue.setConfigid(17);
					_configValue.setValue(projectid349.toString());

					configValues.add(_configValue);
				}
			}

			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String paramName = paramNames.nextElement();
				if (!paramName.startsWith("config_") || paramName.equals("config_1_4") || paramName.equals("config_2_11"))
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
			
			ret = processConfigValueModelDao.insert(configValues);

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

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getprojects")
	public ModelAndView getProjects(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getProjects start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ProjectModel> projects = new ArrayList<ProjectModel>();
		try {
			Integer systemid = ParamUtils.getIntParameter(request, "systemid", -1);
			Integer configDBid = ParamUtils.getIntParameter(request, "configDBid", -1);
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Map<String, Object> filterPara = null;
			Long pid = -1L;
			String pName = new String();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						pid = Long.valueOf(filterPara.get(key).toString());
						break;
					case "name":
						pName = filterPara.get(key).toString();
						break;
					default:
						break;
					}
				}
			}

			CondigDBModel condigDBModel = condigDBModelDao.selectByPrimaryKey(configDBid);
			projects = getProjects(condigDBModel, systemid, pid, pName);
		} catch (Exception e) {
			e.printStackTrace();
			projects = new ArrayList<ProjectModel>();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", projects);
		json.addObject("total", projects.size());
		json.addObject("result", 1);

		logger.debug("ProcessesManageCtrl-getProjects end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getitemsets")
	public ModelAndView getItemSets(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getItemSets start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ItemSetModel> itemSets = new ArrayList<ItemSetModel>();
		try {
			Integer systemid = ParamUtils.getIntParameter(request, "systemid", -1);
			Integer configDBid = ParamUtils.getIntParameter(request, "configDBid", -1);
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Map<String, Object> filterPara = null;
			Long pid = -1L;
			String pName = new String();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						pid = Long.valueOf(filterPara.get(key).toString());
						break;
					case "name":
						pName = filterPara.get(key).toString();
						break;
					default:
						break;
					}
				}
			}

			CondigDBModel condigDBModel = condigDBModelDao.selectByPrimaryKey(configDBid);
			itemSets = getItemSets(condigDBModel);
		} catch (Exception e) {
			e.printStackTrace();
			itemSets = new ArrayList<ItemSetModel>();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", itemSets);
		json.addObject("total", itemSets.size());
		json.addObject("result", 1);

		logger.debug("ProcessesManageCtrl-getItemSets end.");
		return json;
	}

	@RequestMapping(params = "atn=getitemareas")
	public ModelAndView getItemAreas(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-getItemAreas start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ItemAreaModel> itemAreas = new ArrayList<ItemAreaModel>();
		try {
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			Integer configDBid = ParamUtils.getIntParameter(request, "configDBid", -1);

			CondigDBModel condigDBModel = condigDBModelDao.selectByPrimaryKey(configDBid);
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

	private List<ProjectModel> getProjects(CondigDBModel condigDBModel, Integer systemid, Long projectid, String projectName) {
		List<ProjectModel> list = new ArrayList<ProjectModel>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_projects ");
			sql.append(" WHERE `systemid` = ");
			sql.append(systemid);
			if (projectid.compareTo(0L) > 0)
				sql.append(" AND `id` = " + projectid);
			if (projectName != null && projectName.length() > 0)
				sql.append(" AND `name` like '%" + projectName + "%'");

			BasicDataSource dataSource = getDataSource(getUrl(condigDBModel), condigDBModel.getUser(), condigDBModel.getPassword());
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ProjectModel>(ProjectModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<ProjectModel>();
		}
		return list;
	}

	private List<ItemSetModel> getItemSets(CondigDBModel condigDBModel) {
		List<ItemSetModel> list = new ArrayList<ItemSetModel>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_itemset ");
			sql.append(" WHERE `enable` = 0 ");

			BasicDataSource dataSource = getDataSource(getUrl(condigDBModel), condigDBModel.getUser(), condigDBModel.getPassword());
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemSetModel>(ItemSetModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			list = new ArrayList<ItemSetModel>();
		}
		return list;
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

	private Integer newProject(CondigDBModel condigDBModel, final Integer createby, final Integer systemid, final String newProjectName) {
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
	                ps.setInt(3, 0);
	                ps.setInt(4, 0);
	                ps.setInt(5, systemid);
	                ps.setString(6, new String());
	                ps.setInt(7, createby);
	                ps.setString(8, new String());
	                ps.setString(9, newProjectName);
	                ps.setInt(10, 0);
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
}
