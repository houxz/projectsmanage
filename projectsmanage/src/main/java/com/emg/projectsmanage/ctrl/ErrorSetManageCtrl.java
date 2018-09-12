package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.emg.projectsmanage.common.ItemSetSysType;
import com.emg.projectsmanage.common.ItemSetType;
import com.emg.projectsmanage.common.ItemSetUnit;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.task.ErrorSetModelDao;
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
	@Autowired
	private ErrorSetModelDao errorSetModelDao;

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
			model.addAttribute("processTypes", ProcessType.toJsonStr());

			return "errorsetmanage";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ErrorSetModel record = new ErrorSetModel();
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
				List<ErrorSetModel> rows = errorSetModelDao.selectErrorSets(configDBModel, record, limit, offset);
				Integer count = errorSetModelDao.countErrorSets(configDBModel, record, limit, offset);

				json.addObject("rows", rows);
				json.addObject("total", count);
				json.addObject("result", 1);
			} else {
				HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
				Integer total = 0;
				HashMap<ProcessType, Integer[]> doProTypes = new HashMap<ProcessType, Integer[]>();
				List<ErrorSetModel> totalRows = new ArrayList<ErrorSetModel>();
				for (ProcessType pType : ProcessType.values()) {
					if(pType.equals(ProcessType.UNKNOWN)) continue;
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put("id", 2);
					map.put("processType", pType.getValue());
					ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
					ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
					Integer count = errorSetModelDao.countErrorSets(configDBModel, record, limit, offset);
					if (count.compareTo(0) >= 0) {
						total += count;
						counts.put(pType.getValue(), count);
					} else {
						counts.put(pType.getValue(), 0);
					}
				}
				for (ProcessType pType : ProcessType.values()) {
					if(pType.equals(ProcessType.UNKNOWN)) continue;
					Integer count = counts.get(pType.getValue());
					if(count.compareTo(0) <= 0) continue;
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
					List<ErrorSetModel> rows = errorSetModelDao.selectErrorSets(configDBModel, record, doProTypes.get(doProType)[1], doProTypes.get(doProType)[0]);
					totalRows.addAll(rows);
				}

				json.addObject("rows", totalRows);
				json.addObject("total", total);
				json.addObject("result", 1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			ErrorSetModel record = new ErrorSetModel();
			record.setId(errorsetid);
			List<ErrorSetModel> rows = errorSetModelDao.selectErrorSets(configDBModel, record, 1, 0);
			if (rows.size() >= 0) {
				errorSet = rows.get(0);
				List<Long> details = errorSetModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorsetid);
				if (details.size() > 0) {
					for (Long detail : details) {
						errorsetDetails += detail + ";";
					}
					errorsetDetails = errorsetDetails.substring(0, errorsetDetails.length() - 1);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			errorTypes = errorSetModelDao.selectErrorTypes(configDBModel);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

			List<Long> errorSetDetails = new ArrayList<Long>();
			if (errorTypes != null && !errorTypes.isEmpty()) {
				for (String strItem : errorTypes.split(";")) {
					errorSetDetails.add(Long.valueOf(strItem));
				}
			}
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			Boolean isNewItemSet = errorSetID.compareTo(0L) == 0;
			if (isNewItemSet) {
				ErrorSetModel record = new ErrorSetModel();
				record.setName(name);
				record.setType(type);
				record.setSystype(systype);
				record.setUnit(unit.byteValue());
				record.setDesc(desc);

				errorSetID = errorSetModelDao.insertErrorSet(configDBModel, record);
				if (errorSetID.compareTo(0L) > 0) {
					if (errorSetModelDao.setErrorSetDetails(configDBModel, errorSetID, errorSetDetails) > 0)
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

				if (errorSetModelDao.updateErrorSet(configDBModel, record)) {
					if (errorSetModelDao.setErrorSetDetails(configDBModel, errorSetID, errorSetDetails) > 0)
						ret = true;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
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
			if (errorSetID.compareTo(0L) <= 0) {
				json.addObject("result", 0);
				return json;
			}
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			ret = errorSetModelDao.deleteErrorSet(configDBModel, errorSetID);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("result", ret);
		logger.debug("ErrorSetManageCtrl-deleteErrorSet end.");
		return json;
	}

}
