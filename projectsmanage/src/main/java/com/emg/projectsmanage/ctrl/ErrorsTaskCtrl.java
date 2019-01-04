package com.emg.projectsmanage.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.EnableEnum;
import com.emg.projectsmanage.common.ItemSetSysType;
import com.emg.projectsmanage.common.ItemSetType;
import com.emg.projectsmanage.common.ItemSetUnit;
import com.emg.projectsmanage.common.JobStatus;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.ResultModel;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ErrorsTaskModelDao;
import com.emg.projectsmanage.dao.task.ErrorModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ErrorsTaskModel;
import com.emg.projectsmanage.pojo.ErrorsTaskModelExample;
import com.emg.projectsmanage.pojo.ItemConfigModel;
import com.emg.projectsmanage.service.QuartzService;
import com.emg.projectsmanage.pojo.ErrorsTaskModelExample.Criteria;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/errorstask.web")
public class ErrorsTaskCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ErrorsTaskCtrl.class);
	
	@Autowired
	private ConfigDBModelDao configDBModelDao;
	@Autowired
	private ErrorModelDao errorModelDao;
	@Autowired
	private ErrorsTaskModelDao errorsTaskModelDao;
	@Autowired
	private QuartzService quartzService;

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

			model.addAttribute("errorsetSysTypes", ItemSetSysType.toJsonStr());
			model.addAttribute("errorsetTypes", ItemSetType.toJsonStr());
			model.addAttribute("errorsetUnits", ItemSetUnit.toJsonStr());
			model.addAttribute("jobStatus", JobStatus.toJsonStr());
			
			return "errorstask";
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
			
			ErrorsTaskModelExample example = new ErrorsTaskModelExample();
			Criteria criteria = example.or();
			criteria.andEnableEqualTo(EnableEnum.ENABLE.getValue());
			Map<String, Object> filterPara = null;
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						criteria.andIdEqualTo(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						criteria.andNameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "state":
						criteria.andStateEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}
			
			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);
			
			Integer count = errorsTaskModelDao.countByExample(example);
			if (count.compareTo(0) > 0) {
				List<ConfigDBModel> configDBModels = configDBModelDao.selectAllConfigDBModels();
				List<ErrorsTaskModel> rows = errorsTaskModelDao.selectByExample(example );
				
				for (ErrorsTaskModel errorsTask : rows) {
					Integer qctask = errorsTask.getQctask();
					Integer errorsrc = errorsTask.getErrorsrc();
					Integer errortar = errorsTask.getErrortar();
					
					for (ConfigDBModel configDBModel : configDBModels) {
						if (qctask.equals(configDBModel.getId())) {
							errorsTask.setQctaskdbname(configDBModel.getDbname());
							errorsTask.setQctaskdbschema(configDBModel.getDbschema());
							errorsTask.setQctaskip(configDBModel.getIp());
							errorsTask.setQctaskport(configDBModel.getPort());
						}
						if (errorsrc.equals(configDBModel.getId())) {
							errorsTask.setErrorsrcdbname(configDBModel.getDbname());
							errorsTask.setErrorsrcdbschema(configDBModel.getDbschema());
							errorsTask.setErrorsrcip(configDBModel.getIp());
							errorsTask.setErrorsrcport(configDBModel.getPort());
						}
						if (errortar.equals(configDBModel.getId())) {
							errorsTask.setErrortardbname(configDBModel.getDbname());
							errorsTask.setErrortardbschema(configDBModel.getDbschema());
							errorsTask.setErrortarip(configDBModel.getIp());
							errorsTask.setErrortarport(configDBModel.getPort());
						}
					}
				}
				
				result.setRows(rows);
				result.setTotal(count);
				result.setResult(1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}

		json.addAllObjects(result);
		logger.debug("ErrorsManageCtrl-pages end.");
		return json;
	}
	
	@RequestMapping(params = "atn=geterrorsets")
	public ModelAndView getErrorSets(Model model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("START");
		ResultModel result = new ResultModel();
		try {
			Integer taskdb = ParamUtils.getIntParameter(request, "taskdb", -1);

			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(taskdb);
			List<ErrorSetModel> errorSets = errorModelDao.getErrorSets(configDBModel, null);

			result.setRows(errorSets);
			result.setTotal(errorSets.size());
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}

		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=getbatches")
	public ModelAndView getBatches(Model model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("START");
		ResultModel result = new ResultModel();
		try {
			Integer taskdb = ParamUtils.getIntParameter(request, "taskdb", -1);

			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(taskdb);
			List<String> batchids = errorModelDao.getErrorBatchids(configDBModel);

			result.setRows(batchids);
			result.setTotal(batchids.size());
			result.setResult(1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}

		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=newtask")
	public ModelAndView newErrorsTask(Model model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("START");
		ResultModel result = new ResultModel();
		Long newTaskID = -1L;
		try {
			newTaskID = ParamUtils.getLongParameter(request, "taskid", -1L);
			String name = ParamUtils.getParameter(request, "name");
			Integer qctask = ParamUtils.getIntParameter(request, "qctask", 0);
			Integer errorsrc = ParamUtils.getIntParameter(request, "errorsrc", 0);
			Integer errortar = ParamUtils.getIntParameter(request, "errortar", 0);
			String dotasktime = ParamUtils.getParameter(request, "dotasktime");
			Long batchid = ParamUtils.getLongParameter(request, "batchid", -1L);
			Long errorsetid = ParamUtils.getLongParameter(request, "errorsetid", -1L);
			Integer uid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			
			Boolean isNewTask = newTaskID.compareTo(0L) <= 0;
			
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				Date time = sf.parse(dotasktime);
				dotasktime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
			} catch (Exception e) {
				result.setResult(0);
				result.setResultMsg(e.getMessage());
				json.addAllObjects(result);
				logger.debug("EXCEPTION");
				return json;
			}
			
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(qctask);
			if (configDBModel == null) {
				result.setResult(0);
				result.setResultMsg("未知的数据库配置: " + qctask);
				json.addAllObjects(result);
				logger.debug("EXCEPTION");
				return json;
			}
			ErrorSetModel record = new ErrorSetModel();
			record.setId(errorsetid);
			List<ErrorSetModel> errorSets = errorModelDao.getErrorSets(configDBModel, record);
			if (errorSets == null || errorSets.isEmpty()) {
				result.setResult(0);
				result.setResultMsg("未知的错误筛选集合: " + errorSets);
				json.addAllObjects(result);
				logger.debug("EXCEPTION");
				return json;
			}
			ErrorSetModel errorSet = errorSets.get(0);
			
			if (isNewTask) {
				List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorsetid);
				List<Long> errortypes = new ArrayList<Long>();
				if (itemIDs != null && !itemIDs.isEmpty()) {
					List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
					for (ItemConfigModel itemConfig : itemConfigs) {
						errortypes.add(itemConfig.getErrortype());
					}
				}

				ErrorModel errorsRecord = new ErrorModel();
				errorsRecord.setBatchid(batchid);
				ConfigDBModel configDBSrc = configDBModelDao.selectByPrimaryKey(errorsrc);

				Map<String, Object> map = errorModelDao.selectMinAndMaxID(configDBSrc, errorsRecord, errortypes);
				
				ErrorsTaskModel errorsTaskModel = new ErrorsTaskModel();
				errorsTaskModel.setName(name);
				errorsTaskModel.setCreateby(uid);
				errorsTaskModel.setQctask(qctask);
				errorsTaskModel.setErrorsrc(errorsrc);
				errorsTaskModel.setErrortar(errortar);
				errorsTaskModel.setDotasktime(dotasktime);
				errorsTaskModel.setBatchid(batchid);
				errorsTaskModel.setErrorsetid(errorsetid);
				errorsTaskModel.setErrorsetname(errorSet.getName());
				
				if (map != null && map.containsKey("min")) {
					errorsTaskModel.setMinerrorid(Long.valueOf(map.get("min").toString()));
					errorsTaskModel.setCurerrorid(Long.valueOf(map.get("min").toString()));
				} else {
					errorsTaskModel.setMinerrorid(0L);
					errorsTaskModel.setCurerrorid(0L);
				}
				if (map != null && map.containsKey("max")) {
					errorsTaskModel.setMaxerrorid(Long.valueOf(map.get("max").toString()));
				} else {
					errorsTaskModel.setMaxerrorid(0L);
				}
				
				if (errorsTaskModelDao.insertSelective(errorsTaskModel) > 0 ) {
					newTaskID = errorsTaskModel.getId();
					quartzService.addJob(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dotasktime), newTaskID);
					result.setResult(1);
				} else {
					result.setResult(0);
				}
			} else {
				ErrorsTaskModel errorsTaskModel = new ErrorsTaskModel();
				errorsTaskModel.setId(newTaskID);
				errorsTaskModel.setName(name);
				errorsTaskModel.setQctask(qctask);
				errorsTaskModel.setErrorsrc(errorsrc);
				errorsTaskModel.setErrortar(errortar);
				errorsTaskModel.setDotasktime(dotasktime);
				errorsTaskModel.setBatchid(batchid);
				errorsTaskModel.setErrorsetid(errorsetid);
				errorsTaskModel.setErrorsetname(errorSet.getName());
				
				if (errorsTaskModelDao.updateByPrimaryKeySelective(errorsTaskModel) > 0 ) {
					quartzService.removeJob(newTaskID);
					quartzService.addJob(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dotasktime), newTaskID);
					result.setResult(1);
				} else {
					result.setResult(0);
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
	
	@RequestMapping(params = "atn=deletetask")
	public ModelAndView deleteErrorsTask(Model model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("START");
		ResultModel result = new ResultModel();
		try {
			Long newTaskID = ParamUtils.getLongParameter(request, "taskid", -1L);
			
			if (newTaskID.compareTo(0L) <= 0) {
				result.setResult(0);
				result.setResultMsg("任务编号错误：" + newTaskID);
				json.addAllObjects(result);
				logger.debug("EXCEPTION");
				return json;
			}
			
			ErrorsTaskModel errorsTaskModel = new ErrorsTaskModel();
			errorsTaskModel.setId(newTaskID);
			errorsTaskModel.setEnable(EnableEnum.UNABLE.getValue());
			
			if (errorsTaskModelDao.updateByPrimaryKeySelective(errorsTaskModel) > 0 ) {
				result.setResult(1);
			} else {
				result.setResult(0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultMsg(e.getMessage());
		}

		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=geterrors")
	public ModelAndView getErrors(Model model, HttpServletRequest request, HttpSession session,
			HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("START");
		ResultModel result = new ResultModel();
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);

			Integer qctask = ParamUtils.getIntParameter(request, "qctask", 0);
			Integer errorsrc = ParamUtils.getIntParameter(request, "errorsrc", 0);
			Long batchid = ParamUtils.getLongParameter(request, "batchid", -1L);
			Long errorsetid = ParamUtils.getLongParameter(request, "errorsetid", -1L);
			
			if (qctask == null || qctask.equals(-1) ||
					errorsrc == null || errorsrc.equals(-1)) {
				json.addObject("result", 0);
				json.addObject("option", "参数有误");
				return json;
			}
			

			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(qctask);
			List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorsetid);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
				for (ItemConfigModel itemConfig : itemConfigs) {
					errortypes.add(itemConfig.getErrortype());
				}
			}

			ErrorModel record = new ErrorModel();
			record.setBatchid(batchid);

			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(errorsrc);

			List<ErrorModel> rows = errorModelDao.selectErrors(_configDBModel, record, limit, offset, errortypes, null, null);
			Integer count = errorModelDao.countErrors(_configDBModel, record, errortypes, null, null);

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

}
