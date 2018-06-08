package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.emg.projectsmanage.common.ItemSetEnable;
import com.emg.projectsmanage.common.ItemSetSysType;
import com.emg.projectsmanage.common.ItemSetType;
import com.emg.projectsmanage.common.ItemSetUnit;
import com.emg.projectsmanage.common.LayerElement;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.SystemCPUType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.task.ItemSetModelDao;
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

	private ItemSetModelDao itemSetModelDao = new ItemSetModelDao();

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
			model.addAttribute("processTypes", ProcessType.toJsonStr());

			return "itemsetmanage";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ItemSetModel record = new ItemSetModel();
			Integer processType = -1;
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
					case "processType":
						processType = Integer.valueOf(filterPara.get(key).toString());
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
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			if (processType.compareTo(0) > 0) {
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put("id", 2);
				map.put("processType", processType);
				ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
				record.setProcessType(processType);
				List<ItemSetModel> rows = itemSetModelDao.selectItemSets(configDBModel, record, limit, offset);
				Integer count = itemSetModelDao.countItemSets(configDBModel, record, limit, offset);

				json.addObject("rows", rows);
				json.addObject("total", count);
				json.addObject("result", 1);
			} else {
				HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
				Integer total = 0;
				HashMap<ProcessType, Integer[]> doProTypes = new HashMap<ProcessType, Integer[]>();
				List<ItemSetModel> totalRows = new ArrayList<ItemSetModel>();
				for (ProcessType pType : ProcessType.values()) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put("id", 2);
					map.put("processType", pType.getValue());
					ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
					ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
					Integer count = itemSetModelDao.countItemSets(configDBModel, record, limit, offset);
					if (count.compareTo(0) >= 0) {
						total += count;
						counts.put(pType.getValue(), count);
					} else {
						counts.put(pType.getValue(), 0);
					}
				}
				for (ProcessType pType : ProcessType.values()) {
					Integer count = counts.get(pType.getValue());
					if (count.compareTo(0) <= 0)
						continue;
					if (count.compareTo(offset) < 0) {
						offset = offset - count;
					} else if (count.compareTo(offset) >= 0 && count.compareTo(offset + limit) < 0) {
						doProTypes.put(pType, new Integer[] { offset, count - offset });
						limit = limit - (count - offset);
						offset = 0;
					} else if (count.compareTo(offset + limit) >= 0) {
						doProTypes.put(pType, new Integer[] { offset, limit });
						break;
					}
				}
				for (ProcessType doProType : doProTypes.keySet()) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put("id", 2);
					map.put("processType", doProType.getValue());
					ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
					ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
					record.setProcessType(doProType.getValue());
					List<ItemSetModel> rows = itemSetModelDao.selectItemSets(configDBModel, record, doProTypes.get(doProType)[1], doProTypes.get(doProType)[0]);
					totalRows.addAll(rows);
				}

				json.addObject("rows", totalRows);
				json.addObject("total", total);
				json.addObject("result", 1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);
			ItemSetModel record = new ItemSetModel();
			record.setId(itemsetid);
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			record.setProcessType(processType);
			List<ItemSetModel> rows = itemSetModelDao.selectItemSets(configDBModel, record, 1, 0);
			if (rows.size() >= 0) {
				itemset = rows.get(0);
				List<Long> itemids = itemSetModelDao.getItemSetDetailsByItemSetID(configDBModel, itemsetid);
				if (itemids.size() > 0) {
					List<ItemInfoModel> itemInfos = itemSetModelDao.selectItemInfosByItemids(configDBModel, itemids);
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
			logger.error(e.getMessage(), e);
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
			String filter = ParamUtils.getParameter(request, "filter", "");
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
			logger.error(e.getMessage(), e);
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
			String filter = ParamUtils.getParameter(request, "filter", "");
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

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
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			items = itemSetModelDao.selectQIDs(configDBModel, oid, name, limit, offset);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

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

			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			Boolean isNewItemSet = itemSetID.compareTo(0L) == 0;
			Integer itemInfoCount = 0;
			if (isNewItemSet) {
				if (systype.equals(SystemCPUType.X86.getValue())) {
					// POI + 其他
					List<ItemInfoModel> itemInfos = itemSetModelDao.selectPOIAndOtherItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
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

						itemSetID = itemSetModelDao.insertItemset(configDBModel, record);
						if (itemSetID.compareTo(0L) > 0) {
							if (itemSetModelDao.setItemSetDetails(configDBModel, itemSetID, itemDetails) > 0)
								ret = true;
						}
					}
					// POI + 其他

					// Road + 其他
					itemInfos.clear();
					itemInfos = itemSetModelDao.selectRoadAndOtherItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
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

						itemSetID = itemSetModelDao.insertItemset(configDBModel, record);
						if (itemSetID.compareTo(0L) > 0) {
							if (itemSetModelDao.setItemSetDetails(configDBModel, itemSetID, itemDetails) > 0)
								ret = true;
						}
					}
					// Road + 其他

					// Road + POI
					itemInfos.clear();
					itemInfos = itemSetModelDao.selectPOIRoadAndOtherItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
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

						itemSetID = itemSetModelDao.insertItemset(configDBModel, record);
						if (itemSetID.compareTo(0L) > 0) {
							if (itemSetModelDao.setItemSetDetails(configDBModel, itemSetID, itemDetails) > 0)
								ret = true;
						}
					}
					// Road + POI

					// 其它
					itemInfos.clear();
					itemInfos = itemSetModelDao.selectOtherItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
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
								if (refer.contains(":")) {
									String[] rs = refer.substring(0, refer.lastIndexOf(":")).split("\\|");
									for (String r : rs) {
										referLayers.add(r);
									}

									Integer rc = Integer.valueOf(refer.substring(refer.lastIndexOf(":") + 1));
									layercount = layercount > rc ? layercount : rc;
								}
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

						itemSetID = itemSetModelDao.insertItemset(configDBModel, record);
						if (itemSetID.compareTo(0L) > 0) {
							if (itemSetModelDao.setItemSetDetails(configDBModel, itemSetID, itemDetails) > 0)
								ret = true;
						}
					}
					// 其它
				} else if (systype.equals(SystemCPUType.X64.getValue())) {
					List<ItemInfoModel> itemInfos = itemSetModelDao.selectX64ItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
					if (!itemInfos.isEmpty()) {
						itemInfoCount += itemInfos.size();

						for (ItemInfoModel itemInfo : itemInfos) {
							List<Long> itemDetails = new ArrayList<Long>();
							itemDetails.add(itemInfo.getId());

							String layer = itemInfo.getLayername();
							String refer = itemInfo.getReferdata();

							ItemSetModel record = new ItemSetModel();
							record.setName(name + "_" + itemInfo.getOid());
							record.setLayername(layer);
							record.setType(type);
							record.setSystype(systype);
							record.setReferdata(refer);
							record.setUnit(unit.byteValue());
							record.setDesc(desc);

							itemSetID = itemSetModelDao.insertItemset(configDBModel, record);
							if (itemSetID.compareTo(0L) > 0) {
								if (itemSetModelDao.setItemSetDetails(configDBModel, itemSetID, itemDetails) > 0)
									ret = true;
							}
						}

					}
				}

			} else {
				if (systype.equals(SystemCPUType.X86.getValue())) {
					ItemSetModel record = new ItemSetModel();
					record.setId(itemSetID);
					List<ItemSetModel> curItemSetList = itemSetModelDao.selectItemSets(configDBModel, record, 1, 0);
					if (curItemSetList != null && curItemSetList.size() == 1) {
						ItemSetModel curItemSet = curItemSetList.get(0);
						String curName = curItemSet.getName();
						List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
						String newName = new String();
						String suffix = curName.substring(curName.lastIndexOf("_"));
						switch (suffix) {
						case "_POI+其他":
							newName = name.endsWith("_POI+其他") ? name : name + "_POI+其他";
							itemInfos = itemSetModelDao.selectPOIAndOtherItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
							break;
						case "_Road+其他":
							newName = name.endsWith("_Road+其他") ? name : name + "_Road+其他";
							itemInfos = itemSetModelDao.selectRoadAndOtherItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
							break;
						case "_POI+Road":
							newName = name.endsWith("_POI+Road") ? name : name + "_POI+Road";
							itemInfos = itemSetModelDao.selectPOIRoadAndOtherItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
							break;
						case "_其他":
							newName = name.endsWith("_其他") ? name : name + "_其他";
							itemInfos = itemSetModelDao.selectOtherItemInfosByOids(configDBModel, layernames, qids, type, systype, unit);
							break;
						}

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

							ItemSetModel newItemSet = new ItemSetModel();
							newItemSet.setId(itemSetID);
							newItemSet.setName(newName);
							newItemSet.setLayername(sb_layername.toString());
							newItemSet.setType(type);
							newItemSet.setSystype(systype);
							newItemSet.setReferdata(sb_referdata.toString());
							newItemSet.setUnit(unit.byteValue());
							newItemSet.setDesc(desc);

							if (itemSetModelDao.updateItemset(configDBModel, newItemSet) && (itemSetModelDao.setItemSetDetails(configDBModel, itemSetID, itemDetails) > 0)) {
								ret = true;
							}
						}
					}
				} else if (systype.equals(SystemCPUType.X64.getValue())) {

				}
			}

			if (itemInfoCount.equals(0)) {
				json.addObject("result", false);
				json.addObject("option", "图层、质检项、类型、操作系统、质检单位不匹配");
				return json;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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

			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			ret = itemSetModelDao.deleteItemSet(configDBModel, itemSetID);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("result", ret);
		logger.debug("ItemSetManageCtrl-deleteItemSet end.");
		return json;
	}

}
