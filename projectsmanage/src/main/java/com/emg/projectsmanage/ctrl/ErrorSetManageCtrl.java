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

import com.emg.projectsmanage.common.ItemSetSysType;
import com.emg.projectsmanage.common.ItemSetType;
import com.emg.projectsmanage.common.ItemSetUnit;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ItemInfoModel;
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
					case "referdata":
						record.setReferdata(filterPara.get(key).toString());
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
		String errorDetails = new String();
		try {
			Long errorsetid = ParamUtils.getLongParameter(request, "itemsetid", -1L);
			ErrorSetModel record = new ErrorSetModel();
			record.setId(errorsetid);
			List<ErrorSetModel> rows = selectErrorSets(record, 1, 0);
			if (rows.size() >= 0) {
				errorSet = rows.get(0);
				List<Long> items = getErrorSetDetailsByErrorSetID(errorsetid);
				if(items.size() > 0) {
					for(Long item : items) {
						errorDetails += item + ";";
					}
					errorDetails = errorDetails.substring(0, errorDetails.length() - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("errorset", errorSet);
		json.addObject("itemDetails", errorDetails);
		logger.debug("ErrorSetManageCtrl-getErrorSet end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=geterrortypes")
	public ModelAndView getErrorTypes(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ErrorSetManageCtrl-getErrorTypes start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ItemInfoModel> items = new ArrayList<ItemInfoModel>();
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Map<String, Object> filterPara = null;
			String oid = new String(), name = new String();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "oid":
						oid = filterPara.get(key).toString();
						break;
					case "name":
						name = filterPara.get(key).toString();
						break;
					default:
						logger.debug("未处理的筛选项：" + key);
						break;
					}
				}
			}
			items = selectErrorTypes(oid, name, limit, offset);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", items);
		json.addObject("total", items.size());
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
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			String name = ParamUtils.getParameter(request, "name");
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			Integer systype = ParamUtils.getIntParameter(request, "systype", -1);
			String referdata = ParamUtils.getParameter(request, "referdata");
			Integer unit = ParamUtils.getIntParameter(request, "unit", -1);
			String desc = ParamUtils.getParameter(request, "desc");
			String items = ParamUtils.getParameter(request, "items");
			
			List<Long> itemDetails = new ArrayList<Long>();
			if(items != null && !items.isEmpty()) {
				for(String strItem : items.split(";")) {
					itemDetails.add(Long.valueOf(strItem));
				}
			}
			
			Boolean isNewItemSet = itemSetID.compareTo(0L) == 0;
			if(isNewItemSet) {
				ErrorSetModel record = new ErrorSetModel();
				record.setName(name);
				record.setType(type);
				record.setSystype(systype);
				record.setReferdata(referdata);
				record.setUnit(unit.byteValue());
				record.setDesc(desc);
				
				itemSetID = insertErrorSet(record);
				if(itemSetID.compareTo(0L) > 0) {
					if(setErrorSetDetails(itemSetID, itemDetails) > 0)
						ret = true;
				}
			} else {
				ErrorSetModel record = new ErrorSetModel();
				record.setId(itemSetID);
				record.setName(name);
				record.setType(type);
				record.setSystype(systype);
				record.setReferdata(referdata);
				record.setUnit(unit.byteValue());
				record.setDesc(desc);
				
				if(updateErrorSet(record)) {
					if(setErrorSetDetails(itemSetID, itemDetails) > 0)
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
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			if(itemSetID.compareTo(0L) <= 0) {
				json.addObject("result", 0);
				return json;
			}
			
			ret = deleteErrorSet(itemSetID);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("result", ret);
		logger.debug("ErrorSetManageCtrl-deleteErrorSet end.");
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

	private String getUrl(ConfigDBModel configDBModel) {
		StringBuffer url = new StringBuffer();
		try {
			url.append("jdbc:mysql://");
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

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_errorset ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND `id` = " + record.getId());
			}
			if(record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND `name` like '%" + record.getName() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND `type` = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(" AND `systype` = " + record.getSystype());
			}
			if(record.getReferdata() != null && !record.getReferdata().isEmpty()) {
				sql.append(" AND `referdata` like '%" + record.getReferdata() + "%'");
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(" AND `unit` = " + record.getUnit());
			}
			if(record.getDesc() != null && !record.getDesc().isEmpty()) {
				sql.append(" AND `desc` like '%" + record.getDesc() + "%'");
			}
			sql.append(" ORDER BY `id` ");
			if(limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if(offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
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
			final StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO tb_errorset (`name`, `type`, `systype`, `referdata`, `unit`, `desc`) ");
			sql.append(" VALUES (?,?,?,?,?,?) ");

			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, record.getName());
					ps.setInt(2, record.getType() == null ? 0 : record.getType());
					ps.setInt(3, record.getSystype() == null ? 0 : record.getSystype());
					ps.setString(4, record.getReferdata());
					ps.setInt(5, record.getUnit() == null ? 0 : record.getUnit());
					ps.setString(6, record.getDesc());
					return ps;
				}
			}, keyHolder);
			ret = keyHolder.getKey().longValue();
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1L;
		}
		return ret;
	}
	
	private Boolean updateErrorSet(ErrorSetModel record) {
		Boolean ret = false;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE tb_errorset ");
			sql.append(" SET `id` = `id`");
			if(record.getName() != null) {
				sql.append(", `name` = '" + record.getName() + "'");
			}
			if(record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(", `type` = " + record.getType());
			}
			if(record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(", `systype` = " + record.getSystype());
			}
			if(record.getReferdata() != null) {
				sql.append(", `referdata` = '" + record.getReferdata() + "'");
			}
			if(record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(", `unit` = " + record.getUnit());
			}
			if(record.getDesc() != null) {
				sql.append(", `desc` = '" + record.getDesc() + "'");
			}
			
			sql.append(" WHERE `id` = " + record.getId());

			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			
			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
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
			
			StringBuffer sql = new StringBuffer();
			sql.append(" DELETE ");
			sql.append(" FROM tb_errorset ");
			sql.append(" WHERE `id` = " + errorSetID);

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;
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

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT count(*) ");
			sql.append(" FROM tb_errorset ");
			sql.append(" WHERE 1=1 ");

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			count = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}

	private List<ItemInfoModel> selectErrorTypes(String oid, String name, Integer limit, Integer offset) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT `oid`, `name` ");
			sql.append(" FROM tb_itemconfig ");
			sql.append(" WHERE 1=1 ");
			if(oid != null && !oid.isEmpty()) {
				sql.append(" AND `oid` like '%" + oid + "%'");
			}
			if(name != null && !name.isEmpty()) {
				sql.append(" AND `name` like '%" + name + "%'");
			}

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemInfos = new ArrayList<ItemInfoModel>();
		}
		return itemInfos;
	}
	
	private List<Long> getErrorSetDetailsByErrorSetID(Long errorSetID) {
		List<Long> items = new ArrayList<Long>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT `itemid` ");
			sql.append(" FROM tb_errorsetdetail ");
			sql.append(" WHERE `itemsetid` = " + errorSetID);
			
			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			items = new JdbcTemplate(dataSource).queryForList(sql.toString(), Long.class);
		} catch (Exception e) {
			items = new ArrayList<Long>();
		}
		return items;
	}
	
	private Integer setErrorSetDetails(Long itemSetID, List<Long> items) {
		Integer ret = -1;
		if (items.size() <= 0)
			return ret;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			
			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE ");
			sql_del.append(" FROM tb_errorsetdetail ");
			sql_del.append(" WHERE `itemsetid` = " + itemSetID);

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			Integer ret_del = jdbc.update(sql_del.toString());
			if (ret_del >= 0) {
				StringBuffer sql = new StringBuffer();
				sql.append(" INSERT INTO tb_errorsetdetail");
				sql.append(" (`itemsetid`, `itemid`) ");
				sql.append(" VALUES ");
				for (Long item : items) {
					sql.append("(");
					sql.append(itemSetID + ", ");
					sql.append(item);
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
