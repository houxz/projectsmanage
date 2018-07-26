package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.task.ErrorModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorAndErrorRelatedModel;
import com.emg.projectsmanage.pojo.ErrorModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ItemConfigModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;

@Controller
@RequestMapping("/errorsmanage.web")
public class ErrorsManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ErrorsManageCtrl.class);

	@Autowired
	private ProcessConfigModelDao processConfigModelDao;
	@Autowired
	private ConfigDBModelDao configDBModelDao;

	private ErrorModelDao errorModelDao = new ErrorModelDao();

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
		logger.debug("ErrorsManageCtrl-openLader start.");
		try {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 16);
			map.put("processType", -1);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			List<String> batchids = errorModelDao.getErrorBatchids(configDBModel);
			model.addAttribute("batchids", batchids);

			Map<String, Integer> _map = new HashMap<String, Integer>();
			_map.put("id", 2);
			_map.put("processType", ProcessType.ERROR.getValue());
			ProcessConfigModel _config = processConfigModelDao.selectByPrimaryKey(_map);
			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(_config.getDefaultValue()));
			List<ErrorSetModel> errorSets = errorModelDao.getErrorSets(_configDBModel);
			model.addAttribute("errorSets", errorSets);

			return "errorsmanage";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:login.jsp";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ErrorsManageCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Long batchID = ParamUtils.getLongParameter(request, "batchid", 0);
			if (batchID == null || batchID <= 0) {
				json.addObject("result", 0);
				json.addObject("option", "批次信息有误");
				return json;
			}
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorSetID);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
				for (ItemConfigModel itemConfig : itemConfigs) {
					errortypes.add(itemConfig.getErrortype());
				}
			}

			Map<String, Object> filterPara = null;
			ErrorModel record = new ErrorModel();
			record.setBatchid(batchID);

			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						record.setId(Long.valueOf(filterPara.get(key).toString()));
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			Map<String, Integer> _map = new HashMap<String, Integer>();
			_map.put("id", 16);
			_map.put("processType", -1);
			ProcessConfigModel _config = processConfigModelDao.selectByPrimaryKey(_map);
			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(_config.getDefaultValue()));

			List<ErrorModel> rows = errorModelDao.selectErrors(_configDBModel, record, limit, offset, errortypes);
			Integer count = errorModelDao.countErrors(_configDBModel, record, errortypes);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("ErrorsManageCtrl-pages end.");
		return json;
	}

	@RequestMapping(params = "atn=geterrorsets")
	public ModelAndView getErrorSets(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("start");
		try {
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);
			
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			List<ErrorSetModel> errorSets = errorModelDao.getErrorSets(configDBModel);

			json.addObject("errorsets", errorSets);
			json.addObject("ret", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("end");
		return json;
	}

	@RequestMapping(params = "atn=exporterrors")
	public ModelAndView exportErrors(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("ErrorsManageCtrl-exportErrors start.");
		Integer ret = -1;
		try {
			Long batchID = ParamUtils.getLongParameter(request, "batchid", 0);
			if (batchID == null || batchID == 0) {
				json.addObject("result", 0);
				json.addObject("option", "批次信息有误");
				return json;
			}
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);
			Integer processType = ParamUtils.getIntParameter(request, "processType", -1);

			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("id", 2);
			map.put("processType", processType);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(map);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorSetID);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
				for (ItemConfigModel itemConfig : itemConfigs) {
					errortypes.add(itemConfig.getErrortype());
				}
			}
			ErrorModel record = new ErrorModel();
			record.setBatchid(batchID);
			Map<String, Integer> _map = new HashMap<String, Integer>();
			_map.put("id", 16);
			_map.put("processType", -1);
			ProcessConfigModel _config = processConfigModelDao.selectByPrimaryKey(_map);
			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(_config.getDefaultValue()));

			/**
			 * 每次处理条数
			 */
			Integer batchNum = 50000, curNum = 0;
			Integer totalNum = errorModelDao.countErrorAndErrorRelateds(_configDBModel, record, errortypes);
			Map<String, Integer> __map = new HashMap<String, Integer>();
			__map.put("id", 20);
			__map.put("processType", -1);
			ProcessConfigModel __config = processConfigModelDao.selectByPrimaryKey(__map);
			ConfigDBModel __configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(__config.getDefaultValue()));
			while (curNum < totalNum) {
				List<ErrorAndErrorRelatedModel> errorAndRelateds = errorModelDao.selectErrorAndErrorRelateds(_configDBModel, record, errortypes, batchNum, curNum);
				if (errorAndRelateds != null && !errorAndRelateds.isEmpty()) {
					ret += errorModelDao.exportErrors(__configDBModel, errorAndRelateds);
					curNum += batchNum;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		json.addObject("ret", ret);
		logger.debug("ErrorsManageCtrl-exportErrors end.");
		return json;
	}

}
