package com.emg.projectsmanage.ctrl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		logger.debug("ItemSetManageCtrl-openLader start.");
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
		logger.debug("ItemSetManageCtrl-pages start.");
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

		logger.debug("ItemSetManageCtrl-pages end.");
		return json;
	}

	@RequestMapping(params = "atn=getitemset")
	public ModelAndView getItemSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-getItemSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ItemSetModel itemset = new ItemSetModel();
		StringBuilder sb_items = new StringBuilder();
		try {
			Long itemsetid = ParamUtils.getLongParameter(request, "itemsetid", -1L);
			ItemSetModel record = new ItemSetModel();
			record.setId(itemsetid);
			List<ItemSetModel> rows = selectItemSets(record, 1, 0);
			if (rows.size() >= 0) {
				itemset = rows.get(0);
				List<Long> itemids = getItemSetDetailsByItemSetID(itemsetid);
				if (itemids.size() > 0) {
					List<ItemInfoModel> itemInfos = selectItemInfosByItemids(itemids);
					if (itemInfos != null && itemInfos.size() > 0) {
						for (ItemInfoModel itemInfo : itemInfos) {
							sb_items.append(itemInfo.getOid());
							sb_items.append(";");
						}
						sb_items.deleteCharAt(sb_items.length() - 1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("itemset", itemset);
		json.addObject("items", sb_items.toString());
		logger.debug("ItemSetManageCtrl-getItemSet end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getlayers")
	public ModelAndView getLayers(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-getLayerSet start.");
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
				if (!filterPara.isEmpty()) {
					if (filterPara.containsKey("id") && !val.getValue().equals(Integer.valueOf(filterPara.get("id").toString()))) {
						continue;
					}
					if (filterPara.containsKey("name") && !val.toString().contains(filterPara.get("name").toString())) {
						continue;
					}
					if (filterPara.containsKey("desc") && !val.getDes().contains(filterPara.get("desc").toString())) {
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
		logger.debug("ItemSetManageCtrl-getLayerSet end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getqids")
	public ModelAndView getQIDs(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-getQIDs start.");
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
			items = selectQIDs(oid, name, limit, offset);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("rows", items);
		json.addObject("total", items.size());
		json.addObject("result", 1);
		logger.debug("ItemSetManageCtrl-getQIDs end.");
		return json;
	}

	@RequestMapping(params = "atn=submititemset")
	public ModelAndView submitItemSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-submitItemSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean ret = false;
		try {
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			String name = ParamUtils.getParameter(request, "name");
			String layername = ParamUtils.getParameter(request, "layername");
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			Integer systype = ParamUtils.getIntParameter(request, "systype", -1);
			Integer unit = ParamUtils.getIntParameter(request, "unit", -1);
			String desc = ParamUtils.getParameter(request, "desc");
			String items = ParamUtils.getParameter(request, "items");

			Set<String> qids = new HashSet<String>();
			if (items != null && !items.isEmpty()) {
				for (String strItem : items.split(";")) {
					qids.add(strItem);
				}
			}
			Set<String> layernames = new HashSet<String>();
			if (layername != null && !layername.isEmpty()) {
				for (String strItem : layername.split(";")) {
					layernames.add(strItem);
				}
			}
			if (qids.isEmpty() || layernames.isEmpty()) {
				json.addObject("result", false);
				json.addObject("option", "质检项、图层未选择");
				return json;
			}

			Boolean isNewItemSet = itemSetID.compareTo(0L) == 0;
			Integer itemInfoCount = 0;
			if (isNewItemSet) {
				// POI + 其他
				List<ItemInfoModel> itemInfos = selectPOIAndOtherItemInfosByOids(layernames, qids, type, systype, unit);
				if (!itemInfos.isEmpty()) {
					itemInfoCount += itemInfos.size();
					List<Long> itemDetails = new ArrayList<Long>();
					Set<String> referLayers = new HashSet<String>();
					Set<String> layers = new HashSet<String>();
					Integer layercount = 0;
					for (ItemInfoModel itemInfo : itemInfos) {
						itemDetails.add(itemInfo.getId());

						String layer = itemInfo.getLayername();
						layers.add(layer);

						String refer = itemInfo.getReferdata();
						if (refer != null && !refer.isEmpty()) {
							String[] rs = refer.substring(0, refer.lastIndexOf(":")).split("\\|");
							for (String r : rs) {
								referLayers.add(r);
							}

							Integer rc = Integer.valueOf(refer.substring(refer.lastIndexOf(":") + 1));
							layercount = layercount > rc ? layercount : rc;
						}
					}

					StringBuilder sb_layername = new StringBuilder();
					for (String layer : layers) {
						sb_layername.append(layer);
						sb_layername.append(";");
					}
					sb_layername.deleteCharAt(sb_layername.length() - 1);

					StringBuilder sb_referdata = new StringBuilder();
					if (referLayers != null && referLayers.size() > 0) {
						for (String layer : referLayers) {
							sb_referdata.append(layer);
							sb_referdata.append("|");
						}
						sb_referdata.deleteCharAt(sb_referdata.length() - 1);
						sb_referdata.append(":");
						sb_referdata.append(layercount);
					}

					ItemSetModel record = new ItemSetModel();
					record.setName(name + "_POI+其他");
					record.setLayername(sb_layername.toString());
					record.setType(type);
					record.setSystype(systype);
					record.setReferdata(sb_referdata.toString());
					record.setUnit(unit.byteValue());
					record.setDesc(desc);

					itemSetID = insertItemset(record);
					if (itemSetID.compareTo(0L) > 0) {
						if (setItemSetDetails(itemSetID, itemDetails) > 0)
							ret = true;
					}
				}
				// POI + 其他

				// Road + 其他
				itemInfos.clear();
				itemInfos = selectRoadAndOtherItemInfosByOids(layernames, qids, type, systype, unit);
				if (!itemInfos.isEmpty()) {
					itemInfoCount += itemInfos.size();
					List<Long> itemDetails = new ArrayList<Long>();
					Set<String> referLayers = new HashSet<String>();
					Set<String> layers = new HashSet<String>();
					Integer layercount = 0;
					for (ItemInfoModel itemInfo : itemInfos) {
						itemDetails.add(itemInfo.getId());

						String layer = itemInfo.getLayername();
						layers.add(layer);

						String refer = itemInfo.getReferdata();
						if (refer != null && !refer.isEmpty()) {
							String[] rs = refer.substring(0, refer.lastIndexOf(":")).split("\\|");
							for (String r : rs) {
								referLayers.add(r);
							}

							Integer rc = Integer.valueOf(refer.substring(refer.lastIndexOf(":") + 1));
							layercount = layercount > rc ? layercount : rc;
						}
					}

					StringBuilder sb_layername = new StringBuilder();
					for (String layer : layers) {
						sb_layername.append(layer);
						sb_layername.append(";");
					}
					sb_layername.deleteCharAt(sb_layername.length() - 1);

					StringBuilder sb_referdata = new StringBuilder();
					if (referLayers != null && referLayers.size() > 0) {
						for (String layer : referLayers) {
							sb_referdata.append(layer);
							sb_referdata.append("|");
						}
						sb_referdata.deleteCharAt(sb_referdata.length() - 1);
						sb_referdata.append(":");
						sb_referdata.append(layercount);
					}

					ItemSetModel record = new ItemSetModel();
					record.setName(name + "_Road+其他");
					record.setLayername(sb_layername.toString());
					record.setType(type);
					record.setSystype(systype);
					record.setReferdata(sb_referdata.toString());
					record.setUnit(unit.byteValue());
					record.setDesc(desc);

					itemSetID = insertItemset(record);
					if (itemSetID.compareTo(0L) > 0) {
						if (setItemSetDetails(itemSetID, itemDetails) > 0)
							ret = true;
					}
				}
				// Road + 其他

				// Road + POI
				itemInfos.clear();
				itemInfos = selectPOIRoadAndOtherItemInfosByOids(layernames, qids, type, systype, unit);
				if (!itemInfos.isEmpty()) {
					itemInfoCount += itemInfos.size();
					List<Long> itemDetails = new ArrayList<Long>();
					Set<String> referLayers = new HashSet<String>();
					Set<String> layers = new HashSet<String>();
					Integer layercount = 0;
					for (ItemInfoModel itemInfo : itemInfos) {
						itemDetails.add(itemInfo.getId());

						String layer = itemInfo.getLayername();
						layers.add(layer);

						String refer = itemInfo.getReferdata();
						if (refer != null && !refer.isEmpty()) {
							String[] rs = refer.substring(0, refer.lastIndexOf(":")).split("\\|");
							for (String r : rs) {
								referLayers.add(r);
							}

							Integer rc = Integer.valueOf(refer.substring(refer.lastIndexOf(":") + 1));
							layercount = layercount > rc ? layercount : rc;
						}
					}

					StringBuilder sb_layername = new StringBuilder();
					for (String layer : layers) {
						sb_layername.append(layer);
						sb_layername.append(";");
					}
					sb_layername.deleteCharAt(sb_layername.length() - 1);

					StringBuilder sb_referdata = new StringBuilder();
					if (referLayers != null && referLayers.size() > 0) {
						for (String layer : referLayers) {
							sb_referdata.append(layer);
							sb_referdata.append("|");
						}
						sb_referdata.deleteCharAt(sb_referdata.length() - 1);
						sb_referdata.append(":");
						sb_referdata.append(layercount);
					}

					ItemSetModel record = new ItemSetModel();
					record.setName(name + "_POI+Road");
					record.setLayername(sb_layername.toString());
					record.setType(type);
					record.setSystype(systype);
					record.setReferdata(sb_referdata.toString());
					record.setUnit(unit.byteValue());
					record.setDesc(desc);

					itemSetID = insertItemset(record);
					if (itemSetID.compareTo(0L) > 0) {
						if (setItemSetDetails(itemSetID, itemDetails) > 0)
							ret = true;
					}
				}
				// Road + POI

				// 其它
				itemInfos.clear();
				itemInfos = selectOtherItemInfosByOids(layernames, qids, type, systype, unit);
				if (!itemInfos.isEmpty()) {
					itemInfoCount += itemInfos.size();
					List<Long> itemDetails = new ArrayList<Long>();
					Set<String> referLayers = new HashSet<String>();
					Set<String> layers = new HashSet<String>();
					Integer layercount = 0;
					for (ItemInfoModel itemInfo : itemInfos) {
						itemDetails.add(itemInfo.getId());

						String layer = itemInfo.getLayername();
						layers.add(layer);

						String refer = itemInfo.getReferdata();
						if (refer != null && !refer.isEmpty()) {
							String[] rs = refer.substring(0, refer.lastIndexOf(":")).split("\\|");
							for (String r : rs) {
								referLayers.add(r);
							}

							Integer rc = Integer.valueOf(refer.substring(refer.lastIndexOf(":") + 1));
							layercount = layercount > rc ? layercount : rc;
						}
					}

					StringBuilder sb_layername = new StringBuilder();
					for (String layer : layers) {
						sb_layername.append(layer);
						sb_layername.append(";");
					}
					sb_layername.deleteCharAt(sb_layername.length() - 1);

					StringBuilder sb_referdata = new StringBuilder();
					if (referLayers != null && referLayers.size() > 0) {
						for (String layer : referLayers) {
							sb_referdata.append(layer);
							sb_referdata.append("|");
						}
						sb_referdata.deleteCharAt(sb_referdata.length() - 1);
						sb_referdata.append(":");
						sb_referdata.append(layercount);
					}

					ItemSetModel record = new ItemSetModel();
					record.setName(name + "_其他");
					record.setLayername(sb_layername.toString());
					record.setType(type);
					record.setSystype(systype);
					record.setReferdata(sb_referdata.toString());
					record.setUnit(unit.byteValue());
					record.setDesc(desc);

					itemSetID = insertItemset(record);
					if (itemSetID.compareTo(0L) > 0) {
						if (setItemSetDetails(itemSetID, itemDetails) > 0)
							ret = true;
					}
				}
				// 其它

				if (itemInfoCount.equals(0)) {
					json.addObject("result", false);
					json.addObject("option", "图层与质检项不匹配");
					return json;
				}
			} else {
				ItemSetModel record = new ItemSetModel();
				record.setId(itemSetID);
				record.setName(name);
				record.setType(type);
				record.setSystype(systype);
				record.setUnit(unit.byteValue());
				record.setDesc(desc);

				if (updateItemset(record)) {
					ret = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("result", ret);
		logger.debug("ItemSetManageCtrl-submitItemSet end.");
		return json;
	}

	@RequestMapping(params = "atn=deleteitemset")
	public ModelAndView deleteItemSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-deleteItemSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Boolean ret = false;
		try {
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			if (itemSetID.compareTo(0L) <= 0) {
				json.addObject("result", 0);
				return json;
			}

			ret = deleteItemSet(itemSetID);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		json.addObject("result", ret);
		logger.debug("ItemSetManageCtrl-deleteItemSet end.");
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

	private List<ItemSetModel> selectItemSets(ItemSetModel record, Integer limit, Integer offset) {
		List<ItemSetModel> itemSets = new ArrayList<ItemSetModel>();
		try {
			if (record == null)
				return itemSets;
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemset ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND id = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND name like '%" + record.getName() + "%'");
			}
			if (record.getLayername() != null && !record.getLayername().isEmpty()) {
				sql.append(" AND layername like '%" + record.getLayername() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND type = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(" AND systype = " + record.getSystype());
			}
			if (record.getReferdata() != null && !record.getReferdata().isEmpty()) {
				sql.append(" AND referdata like '%" + record.getReferdata() + "%'");
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(" AND unit = " + record.getUnit());
			}
			if (record.getDesc() != null && !record.getDesc().isEmpty()) {
				sql.append(" AND desc like '%" + record.getDesc() + "%'");
			}
			sql.append(" ORDER BY id ");
			if (limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if (offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			BasicDataSource dataSource = getDataSource(configDBModel);
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
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			final StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemset (\"name\", \"layername\", \"type\", \"systype\", \"referdata\", \"unit\", \"desc\") ");
			sql.append(" VALUES (?,?,?,?,?,?,?) ");

			KeyHolder keyHolder = new GeneratedKeyHolder();
			BasicDataSource dataSource = getDataSource(configDBModel);
			new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, record.getName() == null ? new String() : record.getName());
					ps.setString(2, record.getLayername() == null ? new String() : record.getLayername());
					ps.setInt(3, record.getType() == null ? 0 : record.getType());
					ps.setInt(4, record.getSystype() == null ? 0 : record.getSystype());
					ps.setString(5, record.getReferdata() == null ? new String() : record.getReferdata());
					ps.setInt(6, record.getUnit() == null ? 0 : record.getUnit());
					ps.setString(7, record.getDesc() == null ? new String() : record.getDesc());
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

	private Boolean updateItemset(ItemSetModel record) {
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
			sql.append("tb_itemset ");
			sql.append(" SET id = id");
			if (record.getName() != null) {
				sql.append(", name = '" + record.getName() + "'");
			}
			if (record.getLayername() != null) {
				sql.append(", layername = '" + record.getLayername() + "'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(", type = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(", systype = " + record.getSystype());
			}
			if (record.getReferdata() != null) {
				sql.append(", referdata = '" + record.getReferdata() + "'");
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(", unit = " + record.getUnit());
			}
			if (record.getDesc() != null) {
				sql.append(", desc = '" + record.getDesc() + "'");
			}

			sql.append(" WHERE id = " + record.getId());

			BasicDataSource dataSource = getDataSource(configDBModel);
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
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" DELETE FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemset ");
			sql.append(" WHERE id = " + itemSetID);

			BasicDataSource dataSource = getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;

			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql_del.append(configDBModel.getDbschema()).append(".");
			}
			sql_del.append("tb_itemsetdetail ");
			sql_del.append(" WHERE itemsetid = " + itemSetID);

			ret = ret && new JdbcTemplate(dataSource).update(sql_del.toString()) >= 0;
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
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT count(*) FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemset ");
			sql.append(" WHERE 1=1 ");

			BasicDataSource dataSource = getDataSource(configDBModel);
			count = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			count = -1;
		}
		return count;
	}

	private List<ItemInfoModel> selectItemInfosByItemids(List<Long> itemids) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DISTINCT ON (oid) * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE enable = 1 AND id in ( ");
			for (Long itemid : itemids) {
				sql.append("'" + itemid + "',");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(") ");

			BasicDataSource dataSource = getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemInfos = new ArrayList<ItemInfoModel>();
		}
		return itemInfos;
	}

	/**
	 * 获取质检项分类属于 POI+其他 的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	private List<ItemInfoModel> selectPOIAndOtherItemInfosByOids(Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE enable = 1 ");
			sql.append(" AND type = " + type);
			sql.append(" AND unit = " + unit);
			sql.append(" AND systype = " + systype);
			sql.append(" AND referdata LIKE '%POI%' ");
			sql.append(" AND referdata NOT LIKE '%Road%' ");
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND layername in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND oid in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			BasicDataSource dataSource = getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemInfos = new ArrayList<ItemInfoModel>();
		}
		return itemInfos;
	}

	/**
	 * 获取质检项分类属于 Road+其他 的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	private List<ItemInfoModel> selectRoadAndOtherItemInfosByOids(Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE enable = 1 ");
			sql.append(" AND type = " + type);
			sql.append(" AND unit = " + unit);
			sql.append(" AND systype = " + systype);
			sql.append(" AND referdata LIKE '%Road%' ");
			sql.append(" AND referdata NOT LIKE '%POI%' ");
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND layername in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND oid in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			BasicDataSource dataSource = getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemInfos = new ArrayList<ItemInfoModel>();
		}
		return itemInfos;
	}

	/**
	 * 获取质检项分类属于 POI+Road+其他 的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	private List<ItemInfoModel> selectPOIRoadAndOtherItemInfosByOids(Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE enable = 1 ");
			sql.append(" AND type = " + type);
			sql.append(" AND unit = " + unit);
			sql.append(" AND systype = " + systype);
			sql.append(" AND (referdata LIKE '%Road%POI%' ");
			sql.append(" OR referdata LIKE '%POI%Road%') ");
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND layername in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND oid in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			BasicDataSource dataSource = getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemInfos = new ArrayList<ItemInfoModel>();
		}
		return itemInfos;
	}

	/**
	 * 获取质检项分类属于 其他 的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	private List<ItemInfoModel> selectOtherItemInfosByOids(Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE enable = 1 ");
			sql.append(" AND type = " + type);
			sql.append(" AND unit = " + unit);
			sql.append(" AND systype = " + systype);
			sql.append(" AND referdata NOT LIKE '%POI%' ");
			sql.append(" AND referdata NOT LIKE '%Road%' ");
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND layername in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND oid in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			BasicDataSource dataSource = getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemInfos = new ArrayList<ItemInfoModel>();
		}
		return itemInfos;
	}

	private List<ItemInfoModel> selectQIDs(String oid, String name, Integer limit, Integer offset) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT oid, name FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE enable = 1 ");
			if (oid != null && !oid.isEmpty()) {
				sql.append(" AND oid like '%" + oid + "%'");
			}
			if (name != null && !name.isEmpty()) {
				sql.append(" AND name like '%" + name + "%'");
			}
			sql.append("GROUP BY oid, name");

			BasicDataSource dataSource = getDataSource(configDBModel);
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
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT itemid FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemsetdetail ");
			sql.append(" WHERE itemsetid = " + itemSetID);

			BasicDataSource dataSource = getDataSource(configDBModel);
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
			Integer dbtype = configDBModel.getDbtype();
			
			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql_del.append(configDBModel.getDbschema()).append(".");
			}
			sql_del.append("tb_itemsetdetail ");
			sql_del.append(" WHERE itemsetid = " + itemSetID);

			BasicDataSource dataSource = getDataSource(configDBModel);
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			Integer ret_del = jdbc.update(sql_del.toString());
			if (ret_del >= 0) {
				StringBuffer sql = new StringBuffer();
				sql.append(" INSERT INTO ");
				if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
					sql.append(configDBModel.getDbschema()).append(".");
				}
				sql.append("tb_itemsetdetail (itemsetid, itemid) ");
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
