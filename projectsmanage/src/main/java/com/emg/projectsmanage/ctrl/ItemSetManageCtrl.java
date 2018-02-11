package com.emg.projectsmanage.ctrl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.emg.projectsmanage.common.ItemSetEnable;
import com.emg.projectsmanage.common.ItemSetSysType;
import com.emg.projectsmanage.common.ItemSetType;
import com.emg.projectsmanage.common.ItemSetUnit;
import com.emg.projectsmanage.common.LayerElement;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ItemInfoModel;
import com.emg.projectsmanage.pojo.ItemSetModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;

@Controller
@RequestMapping("/itemsetmanage.web")
public class ItemSetManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ItemSetManageCtrl.class);

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
		logger.debug("LayerManageCtrl-openLader start.");
		try {
			model.addAttribute("itemsetEnables", ItemSetEnable.toJsonStr());
			model.addAttribute("itemsetSysTypes", ItemSetSysType.toJsonStr());
			model.addAttribute("itemsetTypes", ItemSetType.toJsonStr());
			model.addAttribute("itemsetUnits", ItemSetUnit.toJsonStr());
			model.addAttribute("layerElements", LayerElement.toJsonStr());

			return "itemsetmanage";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:login.jsp";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("LayerManageCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Map<String, Object> filterPara = null;
			ItemSetModel record = new ItemSetModel();
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
					case "layername":
						record.setLayername(filterPara.get(key).toString());
						break;
					case "type":
						record.setType(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "enable":
						record.setEnable(Integer.valueOf(filterPara.get(key).toString()));
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

			List<ItemSetModel> rows = selectItemSets(record, limit, offset);
			Integer count = countItemSets(record, limit, offset);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}

		logger.debug("LayerManageCtrl-pages end.");
		return json;
	}

	@RequestMapping(params = "atn=getitemset")
	public ModelAndView getItemSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("LayerManageCtrl-getItemSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ItemSetModel itemset = new ItemSetModel();
		String itemDetails = new String();
		try {
			Long itemsetid = ParamUtils.getLongParameter(request, "itemsetid", -1L);
			ItemSetModel record = new ItemSetModel();
			record.setId(itemsetid);
			List<ItemSetModel> rows = selectItemSets(record, 1, 0);
			if (rows.size() >= 0) {
				itemset = rows.get(0);
				List<Long> items = getItemSetDetailsByItemSetID(itemsetid);
				if(items.size() > 0) {
					for(Long item : items) {
						itemDetails += item + ";";
					}
					itemDetails = itemDetails.substring(0, itemDetails.length() - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("itemset", itemset);
		json.addObject("itemDetails", itemDetails);
		logger.debug("LayerManageCtrl-getItemSet end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getlayers")
	public ModelAndView getLayers(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("LayerManageCtrl-getLayerSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		try {
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");
			Map<String, Object> filterPara = new HashMap<String, Object>();
			if (filter != null && !filter.isEmpty()) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
			}
			for (LayerElement val : LayerElement.values()) {
				Map<String, Object> row = new HashMap<String, Object>();
				if(!filterPara.isEmpty()) {
					if(filterPara.containsKey("id") && !val.getValue().equals(Integer.valueOf(filterPara.get("id").toString()))) {
						continue;
					}
					if(filterPara.containsKey("name") && !val.toString().contains(filterPara.get("name").toString())) {
						continue;
					}
					if(filterPara.containsKey("desc") && !val.getDes().contains(filterPara.get("desc").toString())) {
						continue;
					}
				}
				row.put("id", val.getValue());
				row.put("name", val.toString());
				row.put("desc", val.getDes());
				rows.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", rows);
		json.addObject("total", rows.size());
		json.addObject("result", 1);
		logger.debug("LayerManageCtrl-getLayerSet end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getitems")
	public ModelAndView getItems(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("LayerManageCtrl-getItems start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ItemInfoModel> items = new ArrayList<ItemInfoModel>();
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Map<String, Object> filterPara = null;
			ItemInfoModel record = new ItemInfoModel();
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
					case "layername":
						record.setLayername(filterPara.get(key).toString());
						break;
					case "type":
						record.setType(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "enable":
						record.setEnable(Byte.valueOf(filterPara.get(key).toString()));
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
						record.setModule(filterPara.get(key).toString());
						break;
					default:
						logger.debug("未处理的筛选项：" + key);
						break;
					}
				}
			}
			items = selectItemInfos(record, limit, offset);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", items);
		json.addObject("total", items.size());
		json.addObject("result", 1);
		logger.debug("LayerManageCtrl-getItems end.");
		return json;
	}
	
	@RequestMapping(params = "atn=submititemset")
	public ModelAndView submitItemSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("LayerManageCtrl-submitItemSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean ret = false;
		try {
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			String name = ParamUtils.getParameter(request, "name");
			String layername = ParamUtils.getParameter(request, "layername");
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			Integer enable = ParamUtils.getIntParameter(request, "enable", -1);
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
				ItemSetModel record = new ItemSetModel();
				record.setName(name);
				record.setLayername(layername);
				record.setEnable(enable);
				record.setType(type);
				record.setSystype(systype);
				record.setReferdata(referdata);
				record.setUnit(unit.byteValue());
				record.setDesc(desc);
				
				itemSetID = insertItemset(record);
				if(itemSetID.compareTo(0L) > 0) {
					if(setItemSetDetails(itemSetID, itemDetails) > 0)
						ret = true;
				}
			} else {
				ItemSetModel record = new ItemSetModel();
				record.setId(itemSetID);
				record.setName(name);
				record.setLayername(layername);
				record.setEnable(enable);
				record.setType(type);
				record.setSystype(systype);
				record.setReferdata(referdata);
				record.setUnit(unit.byteValue());
				record.setDesc(desc);
				
				if(updateItemset(record)) {
					if(setItemSetDetails(itemSetID, itemDetails) > 0)
						ret = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("result", ret);
		logger.debug("LayerManageCtrl-submitItemSet end.");
		return json;
	}
	
	@RequestMapping(params = "atn=deleteitemset")
	public ModelAndView deleteItemSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("LayerManageCtrl-deleteItemSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean ret = false;
		try {
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			if(itemSetID.compareTo(0L) <= 0) {
				json.addObject("result", 0);
				return json;
			}
			
			ret = deleteItemSet(itemSetID);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("result", ret);
		logger.debug("LayerManageCtrl-deleteItemSet end.");
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

	private List<ItemSetModel> selectItemSets(ItemSetModel record, Integer limit, Integer offset) {
		List<ItemSetModel> itemSets = new ArrayList<ItemSetModel>();
		try {
			if (record == null)
				return itemSets;
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_itemset ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND `id` = " + record.getId());
			}
			if(record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND `name` like '%" + record.getName() + "%'");
			}
			if(record.getLayername() != null && !record.getLayername().isEmpty()) {
				sql.append(" AND `layername` like '%" + record.getLayername() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND `type` = " + record.getType());
			}
			if (record.getEnable() != null && record.getEnable().compareTo(0) >= 0) {
				sql.append(" AND `enable` = " + record.getEnable());
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
			itemSets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemSetModel>(ItemSetModel.class));

		} catch (Exception e) {
			e.printStackTrace();
			itemSets = new ArrayList<ItemSetModel>();
		}
		return itemSets;
	}

	private Long insertItemset(final ItemSetModel record) {
		Long ret = -1L;
		try {
			final StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO tb_itemset (`name`, `layername`, `type`, `enable`, `systype`, `referdata`, `unit`, `desc`) ");
			sql.append(" VALUES (?,?,?,?,?,?,?,?) ");

			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			
			KeyHolder keyHolder = new GeneratedKeyHolder();
			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, record.getName());
					ps.setString(2, record.getLayername());
					ps.setInt(3, record.getType() == null ? 0 : record.getType());
					ps.setInt(4, record.getEnable() == null ? 0 : record.getEnable());
					ps.setInt(5, record.getSystype() == null ? 0 : record.getSystype());
					ps.setString(6, record.getReferdata());
					ps.setInt(7, record.getUnit() == null ? 0 : record.getUnit());
					ps.setString(8, record.getDesc());
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
	
	private Boolean updateItemset(ItemSetModel record) {
		Boolean ret = false;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE tb_itemset ");
			sql.append(" SET `id` = `id`");
			if(record.getName() != null) {
				sql.append(", `name` = '" + record.getName() + "'");
			}
			if(record.getLayername() != null) {
				sql.append(", `layername` = '" + record.getLayername() + "'");
			}
			if(record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(", `type` = " + record.getType());
			}
			if(record.getEnable() != null && record.getEnable().compareTo(0) >= 0) {
				sql.append(", `enable` = " + record.getEnable());
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
	
	private Boolean deleteItemSet(Long itemSetID) {
		Boolean ret = false;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			
			StringBuffer sql = new StringBuffer();
			sql.append(" DELETE ");
			sql.append(" FROM tb_itemset ");
			sql.append(" WHERE `id` = " + itemSetID);

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	private Integer countItemSets(ItemSetModel record, Integer limit, Integer offset) {
		Integer count = -1;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT count(*) ");
			sql.append(" FROM tb_itemset ");
			sql.append(" WHERE 1=1 ");

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			count = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}

	private List<ItemInfoModel> selectItemInfos(ItemInfoModel record, Integer limit, Integer offset) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		try {
			if (record == null)
				return itemInfos;
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM tb_iteminfo ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND `id` = " + record.getId());
			}
			if(record.getOid() != null && !record.getOid().isEmpty()) {
				sql.append(" AND `oid` like '%" + record.getOid() + "%'");
			}
			if(record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND `name` like '%" + record.getName() + "%'");
			}
			if(record.getLayername() != null && !record.getLayername().isEmpty()) {
				sql.append(" AND `layername` like '%" + record.getLayername() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND `type` = " + record.getType());
			}
			if (record.getEnable() != null && record.getEnable() >= 0) {
				sql.append(" AND `enable` = " + record.getEnable());
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
			if(record.getModule() != null && !record.getModule().isEmpty()) {
				sql.append(" AND `module` like '%" + record.getModule() + "%'");
			}

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemInfos = new ArrayList<ItemInfoModel>();
		}
		return itemInfos;
	}
	
	private List<Long> getItemSetDetailsByItemSetID(Long itemSetID) {
		List<Long> items = new ArrayList<Long>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT `itemid` ");
			sql.append(" FROM tb_itemsetdetail ");
			sql.append(" WHERE `itemsetid` = " + itemSetID);
			
			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			items = new JdbcTemplate(dataSource).queryForList(sql.toString(), Long.class);
		} catch (Exception e) {
			items = new ArrayList<Long>();
		}
		return items;
	}
	
	private Integer setItemSetDetails(Long itemSetID, List<Long> items) {
		Integer ret = -1;
		if (items.size() <= 0)
			return ret;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			
			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE ");
			sql_del.append(" FROM tb_itemsetdetail ");
			sql_del.append(" WHERE `itemsetid` = " + itemSetID);

			BasicDataSource dataSource = getDataSource(getUrl(configDBModel), configDBModel.getUser(), configDBModel.getPassword());
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			Integer ret_del = jdbc.update(sql_del.toString());
			if (ret_del >= 0) {
				StringBuffer sql = new StringBuffer();
				sql.append(" INSERT INTO tb_itemsetdetail");
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