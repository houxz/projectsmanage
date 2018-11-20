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
import com.emg.projectsmanage.common.ResultModel;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.ProcessConfigEnum;
import com.emg.projectsmanage.common.SystemCPUType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.task.ItemSetModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ItemInfoModel;
import com.emg.projectsmanage.pojo.ItemSetModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;
import com.emg.projectsmanage.service.ProcessConfigModelService;

@Controller
@RequestMapping("/itemsetmanage.web")
public class ItemSetManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ItemSetManageCtrl.class);

	@Autowired
	private ProcessConfigModelService processConfigModelService;
	@Autowired
	private ConfigDBModelDao configDBModelDao;
	@Autowired
	private ItemSetModelDao itemSetModelDao;

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
		ResultModel result = new ResultModel();
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ItemSetModel record = new ItemSetModel();
			ProcessType processType = ProcessType.ERROR;
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
						processType = ProcessType.valueOf(Integer.valueOf(filterPara.get(key).toString()));
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

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			record.setProcessType(processType.getValue());
			List<ItemSetModel> rows = itemSetModelDao.selectItemSets(configDBModel, record, limit, offset);
			Integer count = itemSetModelDao.countItemSets(configDBModel, record, limit, offset);

			result.setRows(rows);
			result.setTotal(count);
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}

		json.addAllObjects(result);
		logger.debug("ItemSetManageCtrl-pages end.");
		return json;
	}

	@RequestMapping(params = "atn=getitemset")
	public ModelAndView getItemSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-getItemSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			ItemSetModel itemset = new ItemSetModel();
			StringBuilder sb_items = new StringBuilder();
			Set<String> itemsSet = new HashSet<String>();
			List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
			
			Long itemsetid = ParamUtils.getLongParameter(request, "itemsetid", -1L);
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));
			
			ItemSetModel record = new ItemSetModel();
			record.setId(itemsetid);
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			record.setProcessType(processType.getValue());
			List<ItemSetModel> rows = itemSetModelDao.selectItemSets(configDBModel, record, 1, 0);
			if (rows.size() >= 0) {
				itemset = rows.get(0);
				List<Long> itemids = itemSetModelDao.getItemSetDetailsByItemSetID(configDBModel, itemsetid);
				if (itemids.size() > 0) {
					itemInfos = itemSetModelDao.selectItemInfosByIDs(configDBModel, itemids, null, null , null);
					if (itemInfos != null && itemInfos.size() > 0) {
						for (ItemInfoModel itemInfo : itemInfos) {
							String oid = itemInfo.getOid();
							if (!itemsSet.contains(oid)) {
								itemsSet.add(oid);
								sb_items.append(oid);
								sb_items.append(";");
							}
						}
						sb_items.deleteCharAt(sb_items.length() - 1);
					}
				}
			}
			result.setResult(1);
			result.put("itemset", itemset);
			result.put("items", sb_items.toString());
			result.put("itemInfos", itemInfos);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResult(0);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("ItemSetManageCtrl-getItemSet end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getlayers")
	public ModelAndView getLayers(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-getLayerSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
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
			result.setRows(rows);
			result.setTotal(rows.size());
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResult(0);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("ItemSetManageCtrl-getLayerSet end.");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getiteminfos")
	public ModelAndView getItemInfos(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-getItemInfos start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		
		try {
			List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));

			Map<String, Object> filterPara = null;
			ItemInfoModel record = new ItemInfoModel();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					String value = filterPara.get(key).toString();
					switch (key) {
					case "id":
						record.setId(Long.valueOf(value));
						break;
					case "oid":
						record.setOid(value);
						break;
					case "name":
						record.setName(value);
						break;
					case "layername":
						record.setLayername(value);
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			itemInfos = itemSetModelDao.selectItemInfoModels(configDBModel, record, limit, offset);
			result.setRows(itemInfos);
			result.setTotal(itemInfos.size());
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResult(0);
			result.setResultMsg(e.getMessage());
		}
		
		json.addAllObjects(result);
		logger.debug("ItemSetManageCtrl-getItemInfos end.");
		return json;
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getqids")
	public ModelAndView getQIDs(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-getQIDs start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		
		try {
			List<ItemInfoModel> items = new ArrayList<ItemInfoModel>();
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));

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
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			items = itemSetModelDao.selectQIDs(configDBModel, oid, name, limit, offset);
			result.setRows(items);
			result.setTotal(items.size());
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResult(0);
			result.setResultMsg(e.getMessage());
		}
		
		json.addAllObjects(result);
		logger.debug("ItemSetManageCtrl-getQIDs end.");
		return json;
	}
	
	@RequestMapping(params = "atn=submititemsetcustom")
	public ModelAndView submitItemSetCustom(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-submitItemSetCustom start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			String name = ParamUtils.getParameter(request, "name");
			String itemInfoIDs = ParamUtils.getParameter(request, "itemInfos");
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			Integer systype = ParamUtils.getIntParameter(request, "systype", -1);
			Integer unit = ParamUtils.getIntParameter(request, "unit", -1);
			String desc = ParamUtils.getParameter(request, "desc");
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));
			
			List<Long> itemInfoIDSet = new ArrayList<Long>();
			if (itemInfoIDs != null && !itemInfoIDs.isEmpty()) {
				for (String strItem : itemInfoIDs.split(";")) {
					try {
						itemInfoIDSet.add(Long.valueOf(strItem));
					} catch (Exception e) {}
				}
			}
			
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			Boolean isNewItemSet = itemSetID.compareTo(0L) == 0;
			Integer itemInfoCount = 0;
			
			if (isNewItemSet) {
				List<ItemInfoModel> itemInfos = itemSetModelDao.selectItemInfosByIDs(configDBModel, itemInfoIDSet, type, systype, unit);
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
					record.setName(name);
					record.setLayername(sb_layername.toString());
					record.setType(type);
					record.setSystype(systype);
					record.setReferdata(sb_referdata.toString());
					record.setUnit(unit.byteValue());
					record.setDesc(desc);

					itemSetID = itemSetModelDao.insertItemset(configDBModel, record);
					if (itemSetID.compareTo(0L) > 0) {
						if (itemSetModelDao.setItemSetDetails(configDBModel, itemSetID, itemDetails) > 0)
							result.setResult(1);
					}
				} else {
					result.setResult(0);
					result.setResultMsg("图层、质检项、类型、操作系统、质检单位不匹配");
					json.addAllObjects(result);
					return json;
				}
			} else {
				List<ItemInfoModel> itemInfos = itemSetModelDao.selectItemInfosByIDs(configDBModel, itemInfoIDSet, type, systype, unit);
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
					newItemSet.setName(name);
					newItemSet.setLayername(sb_layername.toString());
					newItemSet.setType(type);
					newItemSet.setSystype(systype);
					newItemSet.setReferdata(sb_referdata.toString());
					newItemSet.setUnit(unit.byteValue());
					newItemSet.setDesc(desc);

					if (itemSetModelDao.updateItemset(configDBModel, newItemSet) && (itemSetModelDao.setItemSetDetails(configDBModel, itemSetID, itemDetails) > 0)) {
						result.setResult(1);
					}
				} else {
					result.setResult(0);
					result.setResultMsg("图层、质检项、类型、操作系统、质检单位不匹配");
					json.addAllObjects(result);
					return json;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResult(0);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("ItemSetManageCtrl-submitItemSetCustom end.");
		return json;
	}

	@RequestMapping(params = "atn=submititemsetpreliminary")
	public ModelAndView submitItemSetPreliminary(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-submitItemSetPreliminary start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			String name = ParamUtils.getParameter(request, "name");
			String layername = ParamUtils.getParameter(request, "layername");
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			Integer systype = ParamUtils.getIntParameter(request, "systype", -1);
			Integer unit = ParamUtils.getIntParameter(request, "unit", -1);
			String desc = ParamUtils.getParameter(request, "desc");
			String items = ParamUtils.getParameter(request, "items");
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));
			Integer preliminary = ParamUtils.getIntParameter(request, "preliminary", -1);

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
				result.setResult(0);
				result.setResultMsg("质检项、图层未选择");
				json.addAllObjects(result);
				return json;
			}
			if (preliminary <= 0) {
				result.setResult(0);
				result.setResultMsg("方案未知");
				json.addAllObjects(result);
				return json;
			}

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			Boolean isNewItemSet = itemSetID.compareTo(0L) == 0;
			Integer itemInfoCount = 0;
			
			if (preliminary.equals(1)) {
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
									result.setResult(1);
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
									result.setResult(1);
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
									result.setResult(1);
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
									result.setResult(1);
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
										result.setResult(1);
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
									result.setResult(1);
								}
							}
						}
					} else if (systype.equals(SystemCPUType.X64.getValue())) {
	
					}
				}
			} else {
				result.setResult(0);
				result.setResultMsg("未处理的预选方案：" + preliminary);
				json.addAllObjects(result);
				return json;
			}

			if (itemInfoCount.equals(0)) {
				result.setResult(0);
				result.setResultMsg("图层、质检项、类型、操作系统、质检单位不匹配");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResult(0);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("ItemSetManageCtrl-submitItemSetPreliminary end.");
		return json;
	}

	@RequestMapping(params = "atn=deleteitemset")
	public ModelAndView deleteItemSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ItemSetManageCtrl-deleteItemSet start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			Long itemSetID = ParamUtils.getLongParameter(request, "itemSetID", -1L);
			if (itemSetID.compareTo(0L) > 0) {
				ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));
				ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
	
				if (itemSetModelDao.deleteItemSet(configDBModel, itemSetID)) {
					result.setResult(1);
				} else {
					result.setResult(0);
					result.setResultMsg("删除失败");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResult(0);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("ItemSetManageCtrl-deleteItemSet end.");
		return json;
	}

}
