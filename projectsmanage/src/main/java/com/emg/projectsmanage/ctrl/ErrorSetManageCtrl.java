package com.emg.projectsmanage.ctrl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.common.ItemSetSysType;
import com.emg.projectsmanage.common.ItemSetType;
import com.emg.projectsmanage.common.ItemSetUnit;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ItemConfigModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;

@Controller
@RequestMapping("/errorsetmanage.web")
public class ErrorSetManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ErrorSetManageCtrl.class);

	@Autowired
	private ProcessConfigModelDao processConfigModelDao;
	@Autowired
	private ConfigDBModelDao configDBModelDao;

	/**
	 * 系统配置页面
	 * 
	 * @param model
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("ErrorSetManageCtrl-openLader start.");
		try {
			model.addAttribute("errorsetSysTypes", ItemSetSysType.toJsonStr());
			model.addAttribute("errorsetTypes", ItemSetType.toJsonStr());
			model.addAttribute("errorsetUnits", ItemSetUnit.toJsonStr());

			return "errorsetmanage";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:login.jsp";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ErrorSetManageCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Map<String, Object> filterPara = null;
			ErrorSetModel record = new ErrorSetModel();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						record.setId(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						record.setName(filterPara.get(key).toString());
						break;
					case "type":
						record.setType(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "systype":
						record.setSystype(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "unit":
						record.setUnit(Byte.valueOf(filterPara.get(key).toString()));
						break;
					case "desc":
						record.setDesc(filterPara.get(key).toString());
						break;
					default:
						logger.debug("未处理的筛选项：" + key);
						break;
					}
				}
			}

			List<ErrorSetModel> rows = selectErrorSets(record, limit, offset);
			Integer count = countErrorSets(record, limit, offset);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}

		logger.debug("ErrorSetManageCtrl-pages end.");
		return json;
	}

	@RequestMapping(params = "atn=geterrorset")
	public ModelAndView getErrorSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ErrorSetManageCtrl-getErrorSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ErrorSetModel errorSet = new ErrorSetModel();
		String errorsetDetails = new String();
		try {
			Long errorsetid = ParamUtils.getLongParameter(request, "errorsetid", -1L);
			ErrorSetModel record = new ErrorSetModel();
			record.setId(errorsetid);
			List<ErrorSetModel> rows = selectErrorSets(record, 1, 0);
			if (rows.size() >= 0) {
				errorSet = rows.get(0);
				List<Long> details = getErrorSetDetailsByErrorSetID(errorsetid);
				if(details.size() > 0) {
					for(Long detail : details) {
						errorsetDetails += detail + ";";
					}
					errorsetDetails = errorsetDetails.substring(0, errorsetDetails.length() - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("errorset", errorSet);
		json.addObject("errorsetDetails", errorsetDetails);
		logger.debug("ErrorSetManageCtrl-getErrorSet end.");
		return json;
	}

	@RequestMapping(params = "atn=geterrortypes")
	public ModelAndView getErrorTypes(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ErrorSetManageCtrl-getErrorTypes start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ItemConfigModel> errorTypes = new ArrayList<ItemConfigModel>();
		try {
			errorTypes = selectErrorTypes();
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", errorTypes);
		json.addObject("total", errorTypes.size());
		json.addObject("result", 1);
		logger.debug("ErrorSetManageCtrl-getErrorTypes end.");
		return json;
	}
	
	@RequestMapping(params = "atn=submiterrorset")
	public ModelAndView submitErrorSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ErrorSetManageCtrl-submitErrorSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean ret = false;
		try {
			Long errorSetID = ParamUtils.getLongParameter(request, "errorSetID", -1L);
			String name = ParamUtils.getParameter(request, "name");
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			Integer systype = ParamUtils.getIntParameter(request, "systype", -1);
			Integer unit = ParamUtils.getIntParameter(request, "unit", -1);
			String desc = ParamUtils.getParameter(request, "desc");
			String errorTypes = ParamUtils.getParameter(request, "errorTypes");
			
			List<Long> errorSetDetails = new ArrayList<Long>();
			if(errorTypes != null && !errorTypes.isEmpty()) {
				for(String strItem : errorTypes.split(";")) {
					errorSetDetails.add(Long.valueOf(strItem));
				}
			}
			
			Boolean isNewItemSet = errorSetID.compareTo(0L) == 0;
			if(isNewItemSet) {
				ErrorSetModel record = new ErrorSetModel();
				record.setName(name);
				record.setType(type);
				record.setSystype(systype);
				record.setUnit(unit.byteValue());
				record.setDesc(desc);
				
				errorSetID = insertErrorSet(record);
				if(errorSetID.compareTo(0L) > 0) {
					if(setErrorSetDetails(errorSetID, errorSetDetails) > 0)
						ret = true;
				}
			} else {
				ErrorSetModel record = new ErrorSetModel();
				record.setId(errorSetID);
				record.setName(name);
				record.setType(type);
				record.setSystype(systype);
				record.setUnit(unit.byteValue());
				record.setDesc(desc);
				
				if(updateErrorSet(record)) {
					if(setErrorSetDetails(errorSetID, errorSetDetails) > 0)
						ret = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("result", ret);
		logger.debug("ErrorSetManageCtrl-submitErrorSet end.");
		return json;
	}
	
	@RequestMapping(params = "atn=deleteerrorset")
	public ModelAndView deleteErrorSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ErrorSetManageCtrl-deleteErrorSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean ret = false;
		try {
			Long errorSetID = ParamUtils.getLongParameter(request, "errorSetID", -1L);
			if(errorSetID.compareTo(0L) <= 0) {
				json.addObject("result", 0);
				return json;
			}
			
			ret = deleteErrorSet(errorSetID);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("result", ret);
		logger.debug("ErrorSetManageCtrl-deleteErrorSet end.");
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

	private List<ErrorSetModel> selectErrorSets(ErrorSetModel record, Integer limit, Integer offset) {
		List<ErrorSetModel> errorSets = new ArrayList<ErrorSetModel>();
		try {
			if (record == null)
				return errorSets;
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND \"id\" = " + record.getId());
			}
			if(record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND \"name\" like '%" + record.getName() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND \"type\" = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(" AND \"systype\" = " + record.getSystype());
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(" AND \"unit\" = " + record.getUnit());
			}
			if(record.getDesc() != null && !record.getDesc().isEmpty()) {
				sql.append(" AND \"desc\" like '%" + record.getDesc() + "%'");
			}
			sql.append(" ORDER BY id ");
			if(limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if(offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			BasicDataSource dataSource = getDataSource(configDBModel);
			errorSets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ErrorSetModel>(ErrorSetModel.class));

		} catch (Exception e) {
			e.printStackTrace();
			errorSets = new ArrayList<ErrorSetModel>();
		}
		return errorSets;
	}

	private Long insertErrorSet(final ErrorSetModel record) {
		Long ret = -1L;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			final StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");
			sql.append(" (\"name\", \"type\", \"systype\", \"unit\", \"desc\") ");
			sql.append(" VALUES (?,?,?,?,?) ");
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			BasicDataSource dataSource = getDataSource(configDBModel);
			new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, record.getName());
					ps.setInt(2, record.getType() == null ? 0 : record.getType());
					ps.setInt(3, record.getSystype() == null ? 0 : record.getSystype());
					ps.setInt(4, record.getUnit() == null ? 0 : record.getUnit());
					ps.setString(5, record.getDesc() == null ? new String() : record.getDesc());
					return ps;
				}
			}, keyHolder);
			if(keyHolder.getKeys().size() > 1) {
				ret = (Long)keyHolder.getKeys().get("id");
			} else {
				ret = keyHolder.getKey().longValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1L;
		}
		return ret;
	}
	
	private Boolean updateErrorSet(ErrorSetModel record) {
		Boolean ret = false;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");
			sql.append(" SET id = id");
			if(record.getName() != null) {
				sql.append(", \"name\" = '" + record.getName() + "'");
			}
			if(record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(", \"type\" = " + record.getType());
			}
			if(record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(", \"systype\" = " + record.getSystype());
			}
			if(record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(", \"unit\" = " + record.getUnit());
			}
			if(record.getDesc() != null) {
				sql.append(", \"desc\" = '" + record.getDesc() + "'");
			}
			
			sql.append(" WHERE \"id\" = " + record.getId());

			BasicDataSource dataSource = getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	
	private Boolean deleteErrorSet(Long errorSetID) {
		Boolean ret = false;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" DELETE FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");
			sql.append(" WHERE \"id\" = " + errorSetID);

			BasicDataSource dataSource = getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;
			
			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql_del.append(configDBModel.getDbschema()).append(".");
			}
			sql_del.append("tb_errorsetdetail ");
			sql_del.append(" WHERE \"itemsetid\" = " + errorSetID);
			
			ret = ret && new JdbcTemplate(dataSource).update(sql_del.toString()) >= 0;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	private Integer countErrorSets(ErrorSetModel record, Integer limit, Integer offset) {
		Integer count = -1;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT count(*) FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset");
			sql.append(" WHERE 1=1 ");

			BasicDataSource dataSource = getDataSource(configDBModel);
			count = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}

	private List<ItemConfigModel> selectErrorTypes() {
		List<ItemConfigModel> itemConfigs = new ArrayList<ItemConfigModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemconfig ");
			sql.append(" WHERE 1=1 ");

			BasicDataSource dataSource = getDataSource(configDBModel);
			itemConfigs = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemConfigModel>(ItemConfigModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemConfigs = new ArrayList<ItemConfigModel>();
		}
		return itemConfigs;
	}
	
	private List<Long> getErrorSetDetailsByErrorSetID(Long errorSetID) {
		List<Long> items = new ArrayList<Long>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT \"itemid\" FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorsetdetail ");
			sql.append(" WHERE \"itemsetid\" = " + errorSetID);
			
			BasicDataSource dataSource = getDataSource(configDBModel);
			items = new JdbcTemplate(dataSource).queryForList(sql.toString(), Long.class);
		} catch (Exception e) {
			items = new ArrayList<Long>();
		}
		return items;
	}
	
	private Integer setErrorSetDetails(Long errorSetID, List<Long> errorTypes) {
		Integer ret = -1;
		if (errorTypes.size() <= 0)
			return ret;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql_del.append(configDBModel.getDbschema()).append(".");
			}
			sql_del.append("tb_errorsetdetail ");
			sql_del.append(" WHERE \"itemsetid\" = " + errorSetID);

			BasicDataSource dataSource = getDataSource(configDBModel);
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			Integer ret_del = jdbc.update(sql_del.toString());
			if (ret_del >= 0) {
				StringBuffer sql = new StringBuffer();
				sql.append(" INSERT INTO ");
				if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
					sql.append(configDBModel.getDbschema()).append(".");
				}
				sql.append("tb_errorsetdetail");
				sql.append(" (\"itemsetid\", \"itemid\") ");
				sql.append(" VALUES ");
				for (Long errorType : errorTypes) {
					sql.append("(");
					sql.append(errorSetID + ", ");
					sql.append(errorType);
					sql.append(" ),");
				}
				sql.deleteCharAt(sql.length() - 1);
				ret = jdbc.update(sql.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}
	
}
