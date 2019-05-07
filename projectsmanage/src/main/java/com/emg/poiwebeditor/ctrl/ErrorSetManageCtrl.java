package com.emg.poiwebeditor.ctrl;

import java.util.ArrayList;
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

import com.emg.poiwebeditor.common.ItemSetSysType;
import com.emg.poiwebeditor.common.ItemSetType;
import com.emg.poiwebeditor.common.ItemSetUnit;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.ProcessConfigEnum;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.common.ResultModel;
import com.emg.poiwebeditor.dao.process.ConfigDBModelDao;
import com.emg.poiwebeditor.dao.task.ErrorSetModelDao;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.ErrorSetModel;
import com.emg.poiwebeditor.pojo.ItemConfigModel;
import com.emg.poiwebeditor.pojo.ProcessConfigModel;
import com.emg.poiwebeditor.service.ProcessConfigModelService;

@Controller
@RequestMapping("/errorsetmanage.web")
public class ErrorSetManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ErrorSetManageCtrl.class);

	@Autowired
	private ProcessConfigModelService processConfigModelService;
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
		logger.debug("OPENLADER");
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
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ErrorSetModel record = new ErrorSetModel();
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

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			record.setProcessType(processType.getValue());
			List<ErrorSetModel> rows = errorSetModelDao.selectErrorSets(configDBModel, record, limit, offset);
			Integer count = errorSetModelDao.countErrorSets(configDBModel, record, limit, offset);

			result.setRows(rows);
			result.setTotal(count);
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}

		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "atn=geterrorset")
	public ModelAndView getErrorSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ErrorSetModel errorSet = new ErrorSetModel();
		String errorsetDetails = new String();
		ResultModel result = new ResultModel();
		try {
			Long errorsetid = ParamUtils.getLongParameter(request, "errorsetid", -1L);
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			ErrorSetModel record = new ErrorSetModel();
			record.setId(errorsetid);
			record.setProcessType(processType.getValue());
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
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}
		result.put("errorset", errorSet);
		result.put("errorsetDetails", errorsetDetails);
		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=geterrortypes")
	public ModelAndView getErrorTypes(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ItemConfigModel record = new ItemConfigModel();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						record.setId(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						record.setName(filterPara.get(key).toString());
						break;
					case "qid":
						record.setQid(filterPara.get(key).toString());
						break;
					case "errortype":
						record.setErrortype(Long.valueOf(filterPara.get(key).toString()));
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
			List<ItemConfigModel> errorTypes = errorSetModelDao.selectErrorTypes(configDBModel, record, null);
			
			result.setRows(errorTypes);
			result.setTotal(errorTypes.size());
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "atn=recogniseErrortypes")
	public ModelAndView recogniseErrortypes(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			List<ItemConfigModel> errorTypes = new ArrayList<ItemConfigModel>();
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));
			String errortypes = ParamUtils.getParameter(request, "errortypes", new String());
			List<Long> errortypeList = new ArrayList<Long>();
			if (errortypes != null && !errortypes.isEmpty() && !errortypes.trim().isEmpty()) {
				for(String errortype :errortypes.split(";")) {
					try {
						if (errortype == null || errortype.isEmpty() || errortype.trim().isEmpty())
							continue;
						
						errortypeList.add(Long.parseLong(errortype.trim()));
					} catch (NumberFormatException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			
			if (errortypeList.size() > 0) {
				ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
				errorTypes = errorSetModelDao.selectErrorTypes(configDBModel, null, errortypeList);
			}
			
			result.setRows(errorTypes);
			result.setTotal(errorTypes.size());
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=submiterrorset")
	public ModelAndView submitErrorSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			Long errorSetID = ParamUtils.getLongParameter(request, "errorSetID", -1L);
			String name = ParamUtils.getParameter(request, "name");
			Integer type = ParamUtils.getIntParameter(request, "type", -1);
			Integer systype = ParamUtils.getIntParameter(request, "systype", -1);
			Integer unit = ParamUtils.getIntParameter(request, "unit", -1);
			String desc = ParamUtils.getParameter(request, "desc");
			String errorTypes = ParamUtils.getParameter(request, "errorTypes");
			ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));

			List<Long> errorSetDetails = new ArrayList<Long>();
			if (errorTypes != null && !errorTypes.isEmpty()) {
				for (String strItem : errorTypes.split(";")) {
					errorSetDetails.add(Long.valueOf(strItem));
				}
			}
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
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
						result.setResult(1);
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
						result.setResult(1);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "atn=deleteerrorset")
	public ModelAndView deleteErrorSet(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			Long errorSetID = ParamUtils.getLongParameter(request, "errorSetID", -1L);
			if (errorSetID.compareTo(0L) > 0) {
				ProcessType processType = ProcessType.valueOf(ParamUtils.getIntParameter(request, "processType", -1));
	
				ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
	
				if (errorSetModelDao.deleteErrorSet(configDBModel, errorSetID)) {
					result.setResult(1);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}

}
