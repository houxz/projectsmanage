package com.emg.poiwebeditor.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emg.poiwebeditor.cache.ProductTask;
import com.emg.poiwebeditor.client.EditTaskClient;
import com.emg.poiwebeditor.client.POIClient;
import com.emg.poiwebeditor.client.PublicClient;
import com.emg.poiwebeditor.common.CheckEnum;
import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.ConfirmEnum;
import com.emg.poiwebeditor.common.GradeEnum;
import com.emg.poiwebeditor.common.OpTypeEnum;
import com.emg.poiwebeditor.common.POIAttrnameEnum;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.common.TypeEnum;
import com.emg.poiwebeditor.dao.process.ConfigValueModelDao;
import com.emg.poiwebeditor.dao.process.ProcessModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.pojo.ConfigValueModel;
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.ModifiedlogDO;
import com.emg.poiwebeditor.pojo.POIDo;
import com.emg.poiwebeditor.pojo.PoiMergeDO;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ReferdataModel;
import com.emg.poiwebeditor.pojo.TagDO;
import com.emg.poiwebeditor.pojo.TaskModel;

@Controller
@RequestMapping("/polygonedit.web")
public class PolygonEditCtrl extends BaseCtrl{
private static final Logger logger = LoggerFactory.getLogger(EditCtrl.class);
	
	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private ProcessModelDao processModelDao;
	@Autowired
	private PublicClient publicClient;
	@Autowired
	private POIClient poiClient;
	
	@Autowired
	private ProductTask productTask;
	
	@Autowired
	private EditTaskClient taskClient;
	
	@Autowired
	private ConfigValueModelDao configValueDao;
	
	public static final int configid = 33;


	@RequestMapping(method = RequestMethod.GET)
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("OPENLADER");
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		String featcode = "";
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			TaskModel tempTask = productTask.popCurrentTask(userid, ProductTask.TYPE_POLYGONEDIT_MAKING);
			if (tempTask == null) {
				productTask.loadUserTask(userid, ProductTask.TYPE_POLYGONEDIT_QUENE,ProductTask.TYPE_POLYGONEDIT_MAKING, TypeEnum.polygon_edit_init, TypeEnum.polygon_edit_using, TypeEnum.polygon_edit_making );
			}
			
			
			task = productTask.popUserTask(userid, ProductTask.TYPE_POLYGONEDIT_QUENE, ProductTask.TYPE_POLYGONEDIT_MAKING, TypeEnum.polygon_edit_init, TypeEnum.polygon_edit_using, TypeEnum.polygon_edit_making , 0);
			
			if (task != null  && task.getId() != null) {
				TaskModel taskdb = taskClient.getTaskByID(task.getId());
				if (!productTask.canEdit(taskdb, userid, ProductTask.TYPE_POLYGONEDIT_MAKING)) {
					JSONObject taskjson = (JSONObject) JSON.toJSON(taskdb);
					throw new Exception( "当前用户：" + userid + ", 产生错误的任务：" + taskjson.toJSONString());
				}
				Long projectid = task.getProjectid();
				if (projectid.compareTo(0L) > 0) {
					project = projectModelDao.selectByPrimaryKey(projectid);
					
					Long processid = project.getProcessid();
					if (processid.compareTo(0L) > 0) {
						process = processModelDao.selectByPrimaryKey(processid);
						featcode = getProjectFeatcode(processid);
					}
				}
				
				keywordid = task.getKeywordid();
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			
		}
		
		model.addAttribute("task", task);
		model.addAttribute("project", project);
		model.addAttribute("process", process);
		model.addAttribute("keywordid", keywordid);
		model.addAttribute("featcodes", featcode);
		
		logger.debug("OPENLADER OVER");
		return "polygonedit";
	}
	

	@RequestMapping(params = "atn=submitedittask")
	public ModelAndView submitEditTask(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START submit");
		// logger.debug(new Date().getTime() + "");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		POIDo poi = null;
		long ret = -1l;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			String saveRelations = ParamUtils.getParameter(request, "relations");
			String source = ParamUtils.getParameter(request, "source");
			List<ModifiedlogDO> logs = new ArrayList<ModifiedlogDO>();
			logger.debug( "submit poi:" + oid + ", taskid:" + taskid);
			//在提交任务的时候，判断是否一个任务被提交了多次
			TaskModel taskdb = taskClient.getTaskByID(taskid);
			if (!productTask.canEdit(taskdb, userid, ProductTask.TYPE_POLYGONEDIT_MAKING)) {
				JSONObject taskjson = (JSONObject) JSON.toJSON(taskdb);
				throw new Exception( "当前用户：" + userid + ", 产生错误的任务：" + taskjson.toJSONString());
			}
			//logger.debug("1: " + new Date().getTime() + "");
			if (source != null && !source.isEmpty()) {
				JSONArray temp = JSONArray.parseArray(source);
				for(int i = 0; i < temp.size(); i++) {
					ModifiedlogDO log = temp.getObject(i, ModifiedlogDO.class);
					logs.add(log);
				}
			}
			if (oid != -1) {
				poi = this.getPOI(request, logs);
				poi.setConfirmUId(Long.valueOf(userid));
				poi.setUid(Long.valueOf(userid));
			}
			Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
			List<PoiMergeDO> relations = this.getRelation(taskid, saveRelations);
			Long u = new Long(userid);
			if (poi != null) {
				ret = poiClient.updatePOI(u, poi);
			}
			if (relations != null) {
				ret = publicClient.updateRelations(u,  relations);
			}
			if (oid != null) {
				//当所有关系、数据都保存成功之后再往linkpoi里面写数据
				taskClient.InsertNewPOITask(taskid, oid);
				publicClient.updateModifiedlogs( logs);
			}
			//if (taskClient.submitEditTask(taskid, userid).compareTo(0L) <= 0) {
			if (taskClient.submitTask(taskid, userid, TypeEnum.polygon_edit_submit).compareTo(0L) <= 0) {
				json.addObject("resultMsg", "任务提交失败");
				json.addObject("result", 0);
				return json;
			}
			// 把所有更新过的点状态改为readyforqc
			String linkPois = taskClient.getLinkPoiIds(taskid);
			poiClient.updateForReady(linkPois);
			productTask.removeCurrentUserTask(userid, ProductTask.TYPE_POLYGONEDIT_MAKING);
			if (getnext) {
				task = productTask.popUserTask(userid, ProductTask.TYPE_POLYGONEDIT_QUENE, ProductTask.TYPE_POLYGONEDIT_MAKING, TypeEnum.polygon_edit_init, TypeEnum.polygon_edit_using, TypeEnum.polygon_edit_making , 0);
				// task = productTask.popUserEditTask(userid, ProductTask.TYPE_QUENE, ProductTask.TYPE_MAKING);
				// task = getNextEditTask(userid);
				logger.debug("9: " + new Date().getTime() + "");
				if (task != null  && task.getId() != null) {
					Long projectid = task.getProjectid();
					if (projectid.compareTo(0L) > 0) {
						project = projectModelDao.selectByPrimaryKey(projectid);
						Long processid = project.getProcessid();
						if (processid.compareTo(0L) > 0) {
							process = processModelDao.selectByPrimaryKey(processid);
						}
					}
					keywordid = task.getKeywordid();
				}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("error", e.getMessage());
			ret = -1;
			
		}
		json.addObject("task", task);
		json.addObject("project", project);
		json.addObject("process", process);
		json.addObject("keywordid", keywordid);
		json.addObject("result", ret);

		logger.debug("END submit");
		logger.debug(new Date().getTime() + "");
		return json;
	}
	
	@RequestMapping(params = "atn=getNextTask")
	public ModelAndView getNextTask(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		long ret = -1l;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			TaskModel taskdb = taskClient.getTaskByID(taskid);
			if (!productTask.canEdit(taskdb, userid, ProductTask.TYPE_POLYGONEDIT_MAKING)) {
				JSONObject taskjson = (JSONObject) JSON.toJSON(taskdb);
				throw new Exception( "当前用户：" + userid + ", 产生错误的任务：" + taskjson.toJSONString());
			}
			taskClient.updateUsedTask(taskid, TypeEnum.	polygon_edit_making);
			Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
			productTask.removeCurrentUserTask(userid, ProductTask.TYPE_POLYGONEDIT_MAKING);
			if (getnext) {
				task = productTask.popUserTask(userid, ProductTask.TYPE_POLYGONEDIT_QUENE, ProductTask.TYPE_POLYGONEDIT_MAKING, TypeEnum.polygon_edit_init, TypeEnum.polygon_edit_using, TypeEnum.polygon_edit_making , 0);
				if (task != null  && task.getId() != null) {
					Long projectid = task.getProjectid();
					if (projectid.compareTo(0L) > 0) {
						project = projectModelDao.selectByPrimaryKey(projectid);
						
						Long processid = project.getProcessid();
						if (processid.compareTo(0L) > 0) {
							process = processModelDao.selectByPrimaryKey(processid);
						}
					}
					
					keywordid = task.getKeywordid();
				}
				ret = 1l;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("error", e.getMessage());
			ret = -1;
			
		}
		json.addObject("task", task);
		json.addObject("project", project);
		json.addObject("process", process);
		json.addObject("keywordid", keywordid);
		json.addObject("result", ret);

		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "atn=getkeywordbyid")
	public ModelAndView getKeywordByID(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		KeywordModel keyword = new KeywordModel();
		try {
			Long keywordid = ParamUtils.getLongParameter(request, "keywordid", -1);
			keyword = publicClient.selectKeywordsByID(keywordid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			keyword = new KeywordModel();
		}
		json.addObject("rows", keyword);
		json.addObject("count", 1);
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	/**
	 * 以keyword坐标为中心点画圆查找半径为指定distance距离的点
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(params = "atn=getpoibydistance")
	public ModelAndView getPOIByDistance(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<POIDo> pois = new ArrayList<POIDo>();
		try {
			Long distance = ParamUtils.getLongParameter(request, "distance", -1);
			String location = ParamUtils.getParameter(request, "location", null);
			Long processid = ParamUtils.getLongParameter(request, "processid", -1);
			String featcode = getProjectFeatcode(processid);
			if (distance != -1 && location != null && featcode != null) {
				pois = poiClient.selectPoiByDistance(distance, location, featcode);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", pois);
		json.addObject("count", pois.size());
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	
	
	/**
	 * 以keyword坐标为中心点画圆查找半径为指定distance距离的道路
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(params = "atn=getwaybydistance")
	public ModelAndView getWayByDistance(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<String> ways = new ArrayList<String>();
		try {
			String location = ParamUtils.getParameter(request, "geo", null);
			
			if ( location != null) {
				ways = poiClient.selectWayByDistance( location);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", ways);
		json.addObject("count", ways.size());
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	/**
	 * 以keyword坐标为中心点画圆查找半径为指定distance距离的道路
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(params = "atn=getbackgroundbydistance")
	public ModelAndView getBackgroundByDistance(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<String> ways = new ArrayList<String>();
		try {
			String location = ParamUtils.getParameter(request, "geo", null);
			
			if ( location != null) {
				ways = poiClient.selectBackgroundByDistance( location);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", ways);
		json.addObject("count", ways.size());
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	/**
	 * 以keyword坐标为中心点画圆查找半径为指定distance距离的道路
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(params = "atn=getdatabybox")
	public ModelAndView getDataByBox(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<POIDo> pois = new ArrayList<POIDo>();
		try {
			String box = ParamUtils.getParameter(request, "box", "");
			Long processid = ParamUtils.getLongParameter(request, "processid", -1);
			String exceptOids = ParamUtils.getParameter(request, "exceptoids", "");
			
			String featcode = getProjectFeatcode(processid);
			
			if (box != null && !box.isEmpty() && featcode != null) {
				pois = poiClient.selectDataByBox(box, featcode, exceptOids);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", pois);
		json.addObject("count", pois.size());
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	private String getProjectFeatcode(long processid ) {
		StringBuffer result = new StringBuffer();
		if (processid > 0) {
			ConfigValueModel model = new ConfigValueModel();
			model.setProcessId(processid);
			model.setConfigId(configid);
			List<ConfigValueModel> configs = configValueDao.selectConfigs(model);
			if (configs != null && configs.size() > 0) {
				for (ConfigValueModel config : configs) {
					result.append(config.getValue()).append(",");
				}
				String str = result.subSequence(0, result.length() - 1).toString();
				return str.replace(";", ",");
			}
			
		}
		return null;
	}
	
	@RequestMapping(params = "atn=getRelationByOid")
	public ModelAndView getRelationByOid(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<PoiMergeDO> relations = null;
		try {
			String srcInnerId = ParamUtils.getParameter(request, "srcInnerId");
			int srcType = ParamUtils.getIntParameter(request, "srcType", 0);
			relations = publicClient.selectPOIRelation(srcInnerId, srcType);
			if (relations == null) relations = new ArrayList<PoiMergeDO>();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", relations);
		json.addObject("count", relations.size());
		
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=getreferdatabykeywordid")
	public ModelAndView getReferdataByKeywordID(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<ReferdataModel> referdatas = new ArrayList<ReferdataModel>();
		try {
			Long keywordid = ParamUtils.getLongParameter(request, "keywordid", -1);
			referdatas = publicClient.selectReferdatasByKeywordid(keywordid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			referdatas = new ArrayList<ReferdataModel>();
		}
		json.addObject("rows", referdatas);
		json.addObject("count", referdatas.size());
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=getpoibyoid")
	public ModelAndView getPOIByOid(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		POIDo poi = new POIDo();
		try {
			Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			poi = poiClient.selectPOIByOid(oid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			poi = new POIDo();
		}
		json.addObject("poi", poi);
		json.addObject("count", 1);
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=getpoibyoids")
	public ModelAndView getPOIByOids(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<POIDo> pois = new ArrayList<POIDo>();
		try {
			String oid = ParamUtils.getParameter(request, "oids");
			pois = poiClient.selectPOIByOids(oid);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("rows", pois);
		json.addObject("count", 1);
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=deletepoibyoid")
	public ModelAndView deletePOIByOid(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Long ret = -1L;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			TaskModel taskdb = taskClient.getTaskByID(taskid);
			if (!productTask.canEdit(taskdb, userid, ProductTask.TYPE_POLYGONEDIT_MAKING)) {
				JSONObject taskjson = (JSONObject) JSON.toJSON(taskdb);
				throw new Exception( "当前用户：" + userid + ", 产生错误的任务：" + taskjson.toJSONString());
			}
			POIDo  poi = this.getPOI(request, null);
			poi.setConfirm(ConfirmEnum.confirm_ok);
			poi.setConfirmUId((long)userid);
			poi.setUid((long)userid);
			poi.setId(oid);
			poi.setSystemId(SystemType.poi_polymerize.getValue());
			logger.debug(JSON.toJSON(poi).toString());
			
			
			ret = poiClient.deletePOIByOid(poi);
			taskClient.InsertNewPOITask(taskid, oid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("error", e.getMessage());
			ret = -1l;
			
		}
		json.addObject("result", ret);

		logger.debug("END");
		return json;
	}
	@RequestMapping(params = "atn=updatepoi")
	public ModelAndView updatePOI(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Long ret = -1L;
		try {
			Long userid = ParamUtils.getLongAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			POIDo  poi = this.getPOI(request, null);
			poi.setConfirm(ConfirmEnum.confirm_ok);
			poi.setConfirmUId(userid);
			poi.setUid(userid);
			
			logger.debug(JSON.toJSON(poi).toString());
			
			if (poi != null) {
				ret = poiClient.updatePOI(userid, poi);
					//当所有关系、数据都保存成功之后再往linkpoi里面写数据
					taskClient.InsertNewPOITask(taskid, poi.getId());
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1l;
			json.addObject("error", e.getMessage());
		}
		json.addObject("result", ret);

		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=updatetaskstate")
	public ModelAndView updateTastState(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Long ret = -1L;
		try {
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);			
			ret = taskClient.updateUsedTask(taskid, TypeEnum.polygon_edit_making);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1l;
			json.addObject("error", e.getMessage());
		}
		json.addObject("result", ret);

		logger.debug("END");
		return json;
	}
	
	@RequestMapping(params = "atn=updatepoibyoid")
	public ModelAndView updatePOIByOid(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Long ret = -1L;
		try {
			ret = poiClient.getPoiId();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1l;
			json.addObject("error", e.getMessage());
		}
		json.addObject("result", ret);

		logger.debug("END");
		return json;
	}
	
	/**
	 * 根据前台传递过来的参数设置POI
	 * @return
	 */
	private POIDo getPOI(HttpServletRequest request, List<ModifiedlogDO> logs) throws Exception{
		Long oid = ParamUtils.getLongParameter(request, "oid", -1);
		
		String namec = ParamUtils.getParameter(request, "namec");
		Long featcode = ParamUtils.getLongParameter(request, "featcode", 0);
		String sortcode = ParamUtils.getParameter(request, "sortcode", "");
		String remark = ParamUtils.getParameter(request, "remark");
		String geo = ParamUtils.getParameter(request, "geo");
		Long projectId = ParamUtils.getLongParameter(request, "projectId", 0);
		String poistate = ParamUtils.getParameter(request, "poistate");
		POIDo poi = new POIDo();
		poi.setNamec(namec);
		
		POIDo savePoi = poiClient.selectPOIByOid(oid );;
		
		if (savePoi == null || savePoi.getId() == -1) {
			poi.setGrade(GradeEnum.general);
		}else {
			poi.setGrade(savePoi.getGrade());
		}
		if (logs != null) {
			boolean flag = false;
			if(savePoi.getVer() == null || savePoi.getVer().isEmpty()) {
				flag = true;
			}
			for (ModifiedlogDO log : logs) {
				if(log.getOid() < 1) log.setOid(oid);
				if("featcode".equals(log.getK())) {
					log.setOldValue(flag ? null : savePoi.getFeatcode() + "");
				}else if("sortcode".equals(log.getK())) {
					log.setOldValue(flag ? null : savePoi.getSortcode());
				}else if("namec".equals(log.getK())) {
					log.setOldValue(flag ? null : savePoi.getNamec());
				}else if("geo".equals(log.getK())) {
					log.setOldValue(flag ? null : savePoi.getGeo());
				}
				// 1: 修改， 2： 新增
				log.setFlag(flag ? 2 : 1);
			}
			
		}
		
		poi.setId(oid);
		poi.setSystemId(SystemType.poi_polymerize.getValue());
		poi.setConfirm(ConfirmEnum.ready_for_qc);
		poi.setAutoCheck(CheckEnum.uncheck);
		poi.setManualCheck(CheckEnum.uncheck);
		poi.setOpTypeEnum(OpTypeEnum.submit);
		poi.setGeo(geo);
		poi.setFeatcode(featcode);
		poi.setSortcode(sortcode);
		poi.setProjectid(projectId);
		Set<TagDO> tags = poi.getPoitags();
		TagDO remarkTag = null;
		TagDO poistateTag = null;
		if (savePoi != null && savePoi.getPoitags() != null) {
			Set<TagDO> saveTags = savePoi.getPoitags();
			
			for (TagDO tag : saveTags) {
				if (!savePoi.getNamec().equals(namec)) {
					if ("namep".equals(tag.getK())) {
						tag.setV(null);
						tags.add(tag);
					}else if("namee".equals(tag.getK())) {
						tag.setV(null);
						tags.add(tag);
					}else if("names".equals(tag.getK())) {
						tag.setV(null);
						tags.add(tag);
					}else if("namesp".equals(tag.getK())) {
						tag.setV(null);
						tags.add(tag);
					}else if ("namese".equals(tag.getK())) {
						tag.setV(null);
						tags.add(tag);
					}
				}
				if ("remark".equals(tag.getK()) && remark != null) {
					
					SimpleDateFormat f   = new SimpleDateFormat("yyyyMMdd");   
			        String date = f.format(new Date()); 
			        tag.setV((tag.getV() == null ) ? date + "四方检索" : tag.getV() + ";" + date + "四方检索");
					tags.add(tag);
					remarkTag = tag;
				}else if ("poistate".equals(tag.getK()) && poistate != null) {
					poistateTag = tag;
					tag.setV(poistate);
					tags.add(tag);
				}
			}
		}
		if (remarkTag == null  && remark != null) {
			SimpleDateFormat f   = new SimpleDateFormat("yyyyMMdd");   
	        String date = f.format(new Date()); 
			remarkTag = new TagDO();
			remarkTag.setK(POIAttrnameEnum.remark.toString());
			remarkTag.setV(date + "四方检索");
			tags.add(remarkTag);
		}
		
		if(poistateTag == null && poistate != null) {
			poistateTag = new TagDO();
			poistateTag.setK(POIAttrnameEnum.poistate.toString());
			poistateTag.setV(poistate);
			tags.add(poistateTag);
		}
		TagDO inputdatatype = new TagDO();
		inputdatatype.setId(oid);
		inputdatatype.setK(POIAttrnameEnum.inputdatatype.toString());
		inputdatatype.setV(4 + "");
		tags.add(inputdatatype);
		
		TagDO dataset = new TagDO();
		dataset.setId(oid);
		dataset.setK(POIAttrnameEnum.dataset.toString());
		dataset.setV(2 + "");
		tags.add(dataset);
		
		 return poi;
	}
	
	private List<PoiMergeDO> getRelation(long taskid, String str) {
		// Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
		Object json = JSONArray.parse(str);
		List<PoiMergeDO> relations = new ArrayList<PoiMergeDO>();
		if (json instanceof JSONArray) {
			JSONArray data = (JSONArray) json;
			if (data != null && data.size() > 0) {
				for (Integer i = 0, len = data.size(); i < len; i++) {
					PoiMergeDO referdata = new PoiMergeDO();
					referdata = JSON.parseObject(data.getJSONObject(i).toJSONString(), PoiMergeDO.class);
					referdata.setTaskId(taskid);
					relations.add(referdata);
				}
			}
		}
		return relations;
	}
	
	/**
	 * keyword 打错误标识，并把根据getnext值决定是滞获取下一任务
	 * @param model
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(params = "atn=keywordError")
	public ModelAndView keywordError(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		int ret = 1;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long keywordId = ParamUtils.getLongParameter(request, "keywordid", -1);
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			String saveRelations = ParamUtils.getParameter(request, "relation");
			Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
			//在提交任务的时候，判断是否一个任务被提交了多次
			TaskModel taskdb = taskClient.getTaskByID(taskid);
			if (!productTask.canEdit(taskdb, userid, ProductTask.TYPE_POLYGONEDIT_MAKING)) {
				JSONObject taskjson = (JSONObject) JSON.toJSON(taskdb);
				throw new Exception( "当前用户：" + userid + ", 产生错误的任务：" + taskjson.toJSONString());
			}
			KeywordModel keyword = new KeywordModel();
			keyword.setId(keywordId);
			keyword.setState(1);
			
			publicClient.updateKeyword(keyword);
			if (saveRelations != null && !saveRelations.isEmpty()) {
				PoiMergeDO relation = JSONObject.parseObject(saveRelations, PoiMergeDO.class);
				if (relation != null) {
					publicClient.updateImportTime(relation);
				}
			}
			
			if (taskClient.submitTask(taskid, userid, TypeEnum.polygon_edit_submit).compareTo(0L) <= 0) {
				json.addObject("resultMsg", "任务提交失败");
				json.addObject("result", 0);
				return json;
			}
			productTask.removeCurrentUserTask(userid, ProductTask.TYPE_POLYGONEDIT_MAKING);
			if (getnext) {
				task = productTask.popUserTask(userid, ProductTask.TYPE_POLYGONEDIT_QUENE, ProductTask.TYPE_POLYGONEDIT_MAKING, TypeEnum.polygon_edit_init, TypeEnum.polygon_edit_using, TypeEnum.polygon_edit_making, 0);
				if (task != null  && task.getId() != null) {
					Long projectid = task.getProjectid();
					if (projectid.compareTo(0L) > 0) {
						project = projectModelDao.selectByPrimaryKey(projectid);
						
						Long processid = project.getProcessid();
						if (processid.compareTo(0L) > 0) {
							process = processModelDao.selectByPrimaryKey(processid);
						}
					}
					
					keywordid = task.getKeywordid();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("error", e.getMessage());
			ret = -1;
		}
		json.addObject("task", task);
		json.addObject("project", project);
		json.addObject("process", process);
		json.addObject("keywordid", keywordid);
		json.addObject("result", ret);

		logger.debug("END");
		return json;
	}
}
