package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
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
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.task.ErrorModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorAndErrorRelatedModel;
import com.emg.projectsmanage.pojo.ErrorModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ItemConfigModel;

@Controller
@RequestMapping("/errorsmanage.web")
public class ErrorsManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ErrorsManageCtrl.class);

	@Autowired
	private ConfigDBModelDao configDBModelDao;
	@Autowired
	private ErrorModelDao errorModelDao;

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
			List<ConfigDBModel> configDBModelsForQctask = new ArrayList<ConfigDBModel>();
			List<ConfigDBModel> configDBModelsForQcerror = new ArrayList<ConfigDBModel>();
			List<ConfigDBModel> configDBModelsForQcerror2 = new ArrayList<ConfigDBModel>();
			ConfigDBModel configDBModelFirstQctask = null;

			List<ConfigDBModel> configDBModels = configDBModelDao.selectAllConfigDBModels();
			for (ConfigDBModel configDBModel : configDBModels) {
				if (configDBModel.getConnname().equalsIgnoreCase("task")) {
					configDBModelsForQctask.add(configDBModel);
					if (configDBModelFirstQctask == null) {
						configDBModelFirstQctask = configDBModel;
					}
				} else if (configDBModel.getConnname().equalsIgnoreCase("error")) {
					configDBModelsForQcerror.add(configDBModel);
				} else if (configDBModel.getConnname().equalsIgnoreCase("error2")) {
					configDBModelsForQcerror2.add(configDBModel);
				}

			}
			
			model.addAttribute("taskdbs", configDBModelsForQctask);
			model.addAttribute("errordbs", configDBModelsForQcerror);
			model.addAttribute("error2dbs", configDBModelsForQcerror2);
			
			List<String> batchids = errorModelDao.getErrorBatchids(configDBModelFirstQctask);
			model.addAttribute("batchids", batchids);

			List<ErrorSetModel> errorSets = errorModelDao.getErrorSets(configDBModelFirstQctask);
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
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);
			Integer taskdb = ParamUtils.getIntParameter(request, "taskdb", -1);
			Integer errordb = ParamUtils.getIntParameter(request, "errordb", -1);
			Integer erroridxiao = ParamUtils.getIntParameter(request, "erroridxiao", -1);
			Integer erroridda = ParamUtils.getIntParameter(request, "erroridda", -1);
			
			if (taskdb == null || taskdb.equals(-1) ||
					errordb == null || errordb.equals(-1)) {
				json.addObject("result", 0);
				json.addObject("option", "参数有误");
				return json;
			}
			

			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(taskdb);
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

			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(errordb);

			List<ErrorModel> rows = errorModelDao.selectErrors(_configDBModel, record, limit, offset, errortypes, erroridxiao, erroridda);
			Integer count = errorModelDao.countErrors(_configDBModel, record, errortypes, erroridxiao, erroridda);

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
	public ModelAndView getErrorSets(Model model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("start");
		try {
			Integer taskdb = ParamUtils.getIntParameter(request, "taskdb", -1);

			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(taskdb);
			List<ErrorSetModel> errorSets = errorModelDao.getErrorSets(configDBModel);
			List<String> batchids = errorModelDao.getErrorBatchids(configDBModel);

			json.addObject("errorsets", errorSets);
			json.addObject("batchids", batchids);
			json.addObject("ret", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("end");
		return json;
	}

	@RequestMapping(params = "atn=exporterrors")
	public ModelAndView exportErrors(Model model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("ErrorsManageCtrl-exportErrors start.");
		Integer ret = -1;
		try {
			Long batchID = ParamUtils.getLongParameter(request, "batchid", 0);
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);
			Integer taskdb = ParamUtils.getIntParameter(request, "taskdb", -1);
			Integer errordb = ParamUtils.getIntParameter(request, "errordb", -1);
			Integer error2db = ParamUtils.getIntParameter(request, "error2db", -1);
			Integer erroridxiao = ParamUtils.getIntParameter(request, "erroridxiao", -1);
			Integer erroridda = ParamUtils.getIntParameter(request, "erroridda", -1);
			
			if (taskdb == null || taskdb.equals(-1) ||
					errordb == null || errordb.equals(-1) ||
					error2db == null || error2db.equals(-1)) {
				json.addObject("result", 0);
				json.addObject("option", "参数有误");
				return json;
			}
			

			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(taskdb);
			List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorSetID);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
				for (ItemConfigModel itemConfig : itemConfigs) {
					errortypes.add(itemConfig.getErrortype());
				}
			}
			ErrorModel record = new ErrorModel();
			if(batchID != null && batchID.compareTo(0L) > 0)
				record.setBatchid(batchID);
			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(errordb);

			/**
			 * 每次处理条数
			 */
			Integer batchNum = 20000, curNum = 0;
			Integer totalNum = errorModelDao.countErrorAndErrorRelateds(_configDBModel, record, errortypes, erroridxiao, erroridda);
			ConfigDBModel __configDBModel = configDBModelDao.selectByPrimaryKey(error2db);
			while (curNum < totalNum) {
				List<ErrorAndErrorRelatedModel> errorAndRelateds = errorModelDao.selectErrorAndErrorRelateds(_configDBModel, record, errortypes, batchNum, curNum, erroridxiao, erroridda);
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
