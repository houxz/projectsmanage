package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.ProcessState;
import com.emg.projectsmanage.common.ItemAreaType;
import com.emg.projectsmanage.dao.process.CondigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.pojo.CondigDBModel;
import com.emg.projectsmanage.pojo.ItemAreaModel;
import com.emg.projectsmanage.pojo.ItemSetModel;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;
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
	
	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesConfigCtrl-openLader start.");
		
		model.addAttribute("processStates", ProcessState.toJsonStr());
		model.addAttribute("itemAreaTypes", ItemAreaType.toJsonStr());
		
		List<Map<String,Object>> configDBModels = processConfigModelDao.selectAllConfigDBModels();
		model.addAttribute("configDBModels", configDBModels);
		
		return "processesmanage";
	}
	
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProcessesManageCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");
			
			ProcessModelExample example = new ProcessModelExample();
			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);
			List<ProcessModel> projectsTaskCountModels = processModelDao.selectByExample(example );
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
		try{
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
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_projects ");
			sql.append(" WHERE `systemid` = ");
			sql.append(systemid);
			if(projectid.compareTo(0L) > 0)
				sql.append(" AND `id` = " + projectid);
			if(projectName != null && projectName.length() > 0)
				sql.append(" AND `name` like '%" + projectName + "%'");
			
			BasicDataSource dataSource = getDataSource(getUrl(condigDBModel), condigDBModel.getUser(), condigDBModel.getPassword());
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ProjectModel>(ProjectModel.class));
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<ProjectModel>();
		}
		return list;
	}
	
	private List<ItemSetModel> getItemSets(CondigDBModel condigDBModel) {
		List<ItemSetModel> list = new ArrayList<ItemSetModel>();
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_itemset ");
			sql.append(" WHERE `enable` = 0 ");
			
			BasicDataSource dataSource = getDataSource(getUrl(condigDBModel), condigDBModel.getUser(), condigDBModel.getPassword());
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemSetModel>(ItemSetModel.class));
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<ItemSetModel>();
		}
		return list;
	}
	
	private List<ItemAreaModel> getItemAreas(CondigDBModel condigDBModel, Integer type) {
		List<ItemAreaModel> list = new ArrayList<ItemAreaModel>();
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_city ");
			if(type.equals(1)) {
				
			} else if(type.equals(2)) {
				
			} else if(type.equals(3)) {
				sql.append(" WHERE `type` = 2 ");
			} else {
				return list;
			}
			sql.append(" GROUP BY `province`,`city`");
			sql.append(" ORDER BY `type`,`id`");
			
			
			BasicDataSource dataSource = getDataSource(getUrl(condigDBModel), condigDBModel.getUser(), condigDBModel.getPassword());
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemAreaModel>(ItemAreaModel.class));
		} catch(Exception e) {
			e.printStackTrace();
			list = new ArrayList<ItemAreaModel>();
		}
		return list;
	}
}
