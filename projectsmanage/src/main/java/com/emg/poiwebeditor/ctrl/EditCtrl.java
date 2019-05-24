package com.emg.poiwebeditor.ctrl;

import java.util.ArrayList;
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
import com.emg.poiwebeditor.client.POIClient;
import com.emg.poiwebeditor.client.PublicClient;
import com.emg.poiwebeditor.client.TaskModelClient;
import com.emg.poiwebeditor.common.CheckEnum;
import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.ConfirmEnum;
import com.emg.poiwebeditor.common.GradeEnum;
import com.emg.poiwebeditor.common.OpTypeEnum;
import com.emg.poiwebeditor.common.POIAttrnameEnum;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.dao.process.ProcessModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.POIDo;
import com.emg.poiwebeditor.pojo.PoiMergeDO;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsUserModel;
import com.emg.poiwebeditor.pojo.ReferdataModel;
import com.emg.poiwebeditor.pojo.TagDO;
import com.emg.poiwebeditor.pojo.TaskModel;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/edit.web")
public class EditCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(EditCtrl.class);
	
	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private ProjectsUserModelDao projectsUserModelDao;
	@Autowired
	private ProcessModelDao processModelDao;
	@Autowired
	private TaskModelClient taskModelClient;
	@Autowired
	private PublicClient publicClient;
	@Autowired
	private POIClient poiClient;

	@RequestMapping(method = RequestMethod.GET)
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("OPENLADER");
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			
			task = getNextEditTask(userid);
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
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		model.addAttribute("task", task);
		model.addAttribute("project", project);
		model.addAttribute("process", process);
		model.addAttribute("keywordid", keywordid);
		
		logger.debug("OPENLADER OVER");
		return "edit";
	}
	
	@RequestMapping(params = "atn=submitedittask")
	public ModelAndView submitEditTask(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			String saveRelations = ParamUtils.getParameter(request, "relations");
			POIDo poi = null;
			if (oid != -1) {
				poi = this.getPOI(request);
				poi.setConfirmUId(Long.valueOf(userid));
				poi.setUid(Long.valueOf(userid));
			}
			
			Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
			List<PoiMergeDO> relations = this.getRelation(taskid, saveRelations);
			Long u = new Long(userid);
			poiClient.updatePOI(u, poi, relations);
			if (oid != -1) {
				taskModelClient.InsertNewPOITask(taskid, oid);
			}
			
			if (taskModelClient.submitEditTask(taskid, userid).compareTo(0L) <= 0) {
				json.addObject("resultMsg", "任务提交失败");
				json.addObject("result", 0);
				return json;
			}
			
			if (getnext) {
				task = getNextEditTask(userid);
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
		}
		json.addObject("task", task);
		json.addObject("project", project);
		json.addObject("process", process);
		json.addObject("keywordid", keywordid);
		json.addObject("result", 1);

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
	
	@RequestMapping(params = "atn=getRelationByOid")
	public ModelAndView getRelationByOid(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<PoiMergeDO> relations = new ArrayList<PoiMergeDO>();
		try {
			String oid = ParamUtils.getParameter(request, "oid");
			relations = poiClient.selectPOIRelation(oid);
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
	@RequestMapping(params = "atn=deletepoibyoid")
	public ModelAndView deletePOIByOid(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		Long ret = -1L;
		try {
			Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			ret = poiClient.deletePOIByOid(oid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1L;
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
			Long userid = ParamUtils.getLongAttribute(session, CommonConstants.SESSION_USER_ID, -1);
		/*	Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			String namec = ParamUtils.getParameter(request, "namec");
			String tel = ParamUtils.getParameter(request, "tel");
			Long featcode = ParamUtils.getLongParameter(request, "featcode", 0);
			String sortcode = ParamUtils.getParameter(request, "sortcode");
			String address4 = ParamUtils.getParameter(request, "address4");
			String address5 = ParamUtils.getParameter(request, "address5");
			String address6 = ParamUtils.getParameter(request, "address6");
			String address7 = ParamUtils.getParameter(request, "address7");
			String address8 = ParamUtils.getParameter(request, "address8");
			
			POIDo poi = poiClient.selectPOIByOid(oid);
			logger.debug(JSON.toJSON(poi).toString());
			poi.setNamec(namec);
			poi.setFeatcode(featcode);
			poi.setSortcode(sortcode);
			Set<TagDO> tags = poi.getPoitags();
			{
				TagDO tag = new TagDO();
				tag.setId(oid);
				tag.setK(POIAttrnameEnum.tel.toString());
				tag.setV(tel);
				tags.add(tag);
			}
			{
				TagDO tag = new TagDO();
				tag.setId(oid);
				tag.setK(POIAttrnameEnum.address4.toString());
				tag.setV(address4);
				tags.add(tag);
			}
			{
				TagDO tag = new TagDO();
				tag.setId(oid);
				tag.setK(POIAttrnameEnum.address5.toString());
				tag.setV(address5);
				tags.add(tag);
			}
			{
				TagDO tag = new TagDO();
				tag.setId(oid);
				tag.setK(POIAttrnameEnum.address6.toString());
				tag.setV(address6);
				tags.add(tag);
			}
			{
				TagDO tag = new TagDO();
				tag.setId(oid);
				tag.setK(POIAttrnameEnum.address7.toString());
				tag.setV(address7);
				tags.add(tag);
			}
			{
				TagDO tag = new TagDO();
				tag.setId(oid);
				tag.setK(POIAttrnameEnum.address8.toString());
				tag.setV(address8);
				tags.add(tag);
			}*/
			POIDo  poi = this.getPOI(request);
			String dianpingGeo = ParamUtils.getParameter(request, "dianpingGeo");
			poi.setGeo(dianpingGeo);
			poi.setConfirmUId(userid);
			poi.setUid(userid);
			
			
			logger.debug(JSON.toJSON(poi).toString());
			
			ret = poiClient.updatePOI(userid, poi, null);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1L;
		}
		json.addObject("result", ret);

		logger.debug("END");
		return json;
	}
	
	/**
	 * 根据前台传递过来的参数设置POI
	 * @return
	 */
	private POIDo getPOI(HttpServletRequest request) throws Exception{
		Long oid = ParamUtils.getLongParameter(request, "oid", -1);
		
		String namec = ParamUtils.getParameter(request, "namec");
		String tel = ParamUtils.getParameter(request, "tel");
		Long featcode = ParamUtils.getLongParameter(request, "featcode", 0);
		String sortcode = ParamUtils.getParameter(request, "sortcode");
		String address4 = ParamUtils.getParameter(request, "address4");
		String address5 = ParamUtils.getParameter(request, "address5");
		String address6 = ParamUtils.getParameter(request, "address6");
		String address7 = ParamUtils.getParameter(request, "address7");
		String address8 = ParamUtils.getParameter(request, "address8");
		String geo = ParamUtils.getParameter(request, "dianpingGeo");
		
		
		POIDo poi = new POIDo();
		logger.debug(JSON.toJSON(poi).toString());
		POIDo savePoi = poiClient.selectPOIByOid(oid);
		poi.setNamec(namec);
		if (oid < 0) {
			oid = poiClient.getPoiId();
			poi.setGrade(GradeEnum.general);
		}else {
			poi.setGrade(savePoi.getGrade());
		}
		poi.setId(oid);
		poi.setSystemId(370);
		poi.setConfirm(ConfirmEnum.ready_for_qc);
		poi.setAutoCheck(CheckEnum.uncheck);
		poi.setManualCheck(CheckEnum.uncheck);
		poi.setOpTypeEnum(OpTypeEnum.submit);
		poi.setGeo(geo);
		poi.setFeatcode(featcode);
		poi.setSortcode(sortcode);
		
		Set<TagDO> tags = poi.getPoitags();
		if (savePoi != null && savePoi.getPoitags() != null) {
			Set<TagDO> saveTags = savePoi.getPoitags();
			
			for (TagDO tag : saveTags) {
				if (!savePoi.getNamec().equals(namec)) {
					TagDO namep = new TagDO();
					namep.setId(oid);
					namep.setK(POIAttrnameEnum.namep.toString());
					namep.setV(null);
					tags.add(namep);
					
					TagDO namee = new TagDO();
					namee.setId(oid);
					namee.setK(POIAttrnameEnum.namee.toString());
					namee.setV(null);
					tags.add(namee);
					
					TagDO namees = new TagDO();
					namees.setId(oid);
					namees.setK(POIAttrnameEnum.names.toString());
					namees.setV(null);
					tags.add(namees);
					
					TagDO namesp = new TagDO();
					namesp.setId(oid);
					namesp.setK(POIAttrnameEnum.namesp.toString());
					namesp.setV(null);
					tags.add(namesp);
					
					TagDO namese = new TagDO();
					namese.setId(oid);
					namese.setK(POIAttrnameEnum.namese.toString());
					namese.setV(null);
					tags.add(namese);
				}
				
				if ("address4".equals(tag.getK())) {
					if (address4 == null || address4.isEmpty()) {
						tag.setV(null);
						tags.add(tag);
						TagDO tag4e = new TagDO();
						tag4e.setId(oid);
						tag4e.setK(POIAttrnameEnum.address4e.toString());
						tag4e.setV(null);
						tags.add(tag4e);
						TagDO tag4p = new TagDO();
						tag4p.setId(oid);
						tag4p.setK(POIAttrnameEnum.address4p.toString());
						tag4p.setV(null);
						tags.add(tag4p);
						
					}
				}else if ("address5".equals(tag.getK())) {
					if (address5 == null || address5.isEmpty()) {
						tag.setV(null);
						tags.add(tag);
						TagDO tag5e = new TagDO();
						tag5e.setId(oid);
						tag5e.setK(POIAttrnameEnum.address5e.toString());
						tag5e.setV(null);
						tags.add(tag5e);
						TagDO tag5p = new TagDO();
						tag5p.setId(oid);
						tag5p.setK(POIAttrnameEnum.address5p.toString());
						tag5p.setV(null);
						tags.add(tag5p);
						
					}
				}else if ("address6".equals(tag.getK())) {
					if (address6 == null || address6.isEmpty()) {
						tag.setV(null);
						tags.add(tag);
						TagDO tag6e = new TagDO();
						tag6e.setId(oid);
						tag6e.setK(POIAttrnameEnum.address6e.toString());
						tag6e.setV(null);
						tags.add(tag6e);
						TagDO tag6p = new TagDO();
						tag6p.setId(oid);
						tag6p.setK(POIAttrnameEnum.address6p.toString());
						tag6p.setV(null);
						tags.add(tag6p);
						
					}
				}else if ("address7".equals(tag.getK())) {
					if (address7 == null || address7.isEmpty()) {
						tag.setV(null);
						tags.add(tag);
						TagDO tag7e = new TagDO();
						tag7e.setId(oid);
						tag7e.setK(POIAttrnameEnum.address7e.toString());
						tag7e.setV(null);
						tags.add(tag7e);
						TagDO tag7p = new TagDO();
						tag7p.setId(oid);
						tag7p.setK(POIAttrnameEnum.address7p.toString());
						tag7p.setV(null);
						tags.add(tag7p);
						
					}
				}else if ("address8e".equals(tag.getK())) {
					if (address8 != null && !address8.equals(tag.getV())) {
						/*tag.setV(null);
						tags.add(tag);*/
						TagDO tag8e = new TagDO();
						tag8e.setId(oid);
						tag8e.setK(POIAttrnameEnum.address8e.toString());
						tag8e.setV(null);
						tags.add(tag8e);
						
						
					}
				}else if ("address8p".equals(tag.getK())) {
					if (address8 != null && !address8.equals(tag.getV())) {
						/*tag.setV(null);
						tags.add(tag);*/
						
						TagDO tag8p = new TagDO();
						tag8p.setId(oid);
						tag8p.setK(POIAttrnameEnum.address8p.toString());
						tag8p.setV(null);
						tags.add(tag8p);
						
					}
				}
			}
		}
		
		
		TagDO tag = new TagDO();
		tag.setId(oid);
		tag.setK(POIAttrnameEnum.tel.toString());
		tag.setV(tel);
		tags.add(tag);
		
		TagDO tag8 = new TagDO();
		tag8.setId(oid);
		tag8.setK(POIAttrnameEnum.address8.toString());
		tag8.setV(address8);
		tags.add(tag8);
		 return poi;
	}
	
	/**
	 * 用数据库中现在已经存在的relation和需要保存的relation进行比较，确定哪些是需要新增修改的，哪些是需要删除的
	 */
	public void getSaveRelation(POIDo poi, Long keywordid)  throws Exception{
		List<PoiMergeDO> relations = poiClient.selectPOIRelation(poi.getId() + "");
		List<ReferdataModel>referdatas = publicClient.selectReferdatasByKeywordid(keywordid);
		// for ()
	}
	
	/*private List<PoiMergeDO> getRelation(HttpServletRequest request, POIDo poi) throws Exception{
		Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
		if  (poi == null || poi.getId() < 0) return null;
		
		String qid = ParamUtils.getParameter(request, "qid");
		Long errorType = ParamUtils.getLongParameter(request, "errorType", 0);
		String srcType = ParamUtils.getParameter(request, "srcType");
		String srcInnerId = ParamUtils.getParameter(request, "srcInnerId");
		String baiduSrcInnerId = ParamUtils.getParameter(request, "baiduSrcInnerId");
		String emgSrcInnerId = ParamUtils.getParameter(request, "emgSrcInnerId");
		String gaodeSrcInnerId = ParamUtils.getParameter(request, "gaodeSrcInnerId");
		String tengxunSrcInnerId = ParamUtils.getParameter(request, "tengxunSrcInnerId");
		int emgSrcType = ParamUtils.getIntParameter(request, "emgSrcType", 0);
		int baiduSrcType = ParamUtils.getIntParameter(request, "baiduSrcType", 0);
		int gaodeSrcType = ParamUtils.getIntParameter(request, "gaodeSrcType", 0);
		int tengxunSrcType = ParamUtils.getIntParameter(request, "tengxunSrcType", 0);
		
		List<PoiMergeDO> relations = new ArrayList<PoiMergeDO>();
		PoiMergeDO tengxunRelation = new PoiMergeDO();
		tengxunRelation.setTaskId(taskid);
		tengxunRelation.setSrcInnerId(tengxunSrcInnerId);
		tengxunRelation.setSrcType(tengxunSrcType);
		tengxunRelation.setOid(poi.getId());
		tengxunRelation.setQid(qid);
		tengxunRelation.setErrorType(errorType);
		relations.add(tengxunRelation);
		
		PoiMergeDO baiduRelation = new PoiMergeDO();
		baiduRelation.setTaskId(taskid);
		baiduRelation.setSrcInnerId(baiduSrcInnerId);
		baiduRelation.setSrcType(baiduSrcType);
		baiduRelation.setOid(poi.getId());
		baiduRelation.setQid(qid);
		baiduRelation.setErrorType(baiduSrcType);
		relations.add(baiduRelation);
		
		PoiMergeDO gaodeRelation = new PoiMergeDO();
		gaodeRelation.setTaskId(taskid);
		gaodeRelation.setSrcInnerId(gaodeSrcInnerId);
		gaodeRelation.setSrcType(gaodeSrcType);
		gaodeRelation.setOid(poi.getId());
		gaodeRelation.setQid(qid);
		gaodeRelation.setErrorType(gaodeSrcType);
		relations.add(gaodeRelation);
		if(srcType != null) {
			//如果srctype=null则说明该资料不是来自于点评，需要保存的关系的，emg-baidu,emg-gaode, emg-tengxun
			PoiMergeDO dianpingRelation = new PoiMergeDO();
			dianpingRelation.setTaskId(taskid);
			dianpingRelation.setSrcInnerId(srcInnerId);
			dianpingRelation.setSrcType(Integer.parseInt(srcType));
			dianpingRelation.setOid(poi.getId());
			dianpingRelation.setQid(qid);
			dianpingRelation.setErrorType(errorType);
			relations.add(dianpingRelation);
		}
		return relations;
	}
	*/
	
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
	private TaskModel getNextEditTask(Integer userid) {
		TaskModel task = new TaskModel();
		try {
			RoleType roleType = RoleType.ROLE_WORKER;
			SystemType systemType = SystemType.poi_polymerize;
			
			List<ProjectModel> myProjects = new ArrayList<ProjectModel>();
			
			ProjectsUserModel record = new ProjectsUserModel();
			record.setUserid(userid);
			record.setRoleid(roleType.getValue());
			
			List<ProjectsUserModel> projectsUserModels = projectsUserModelDao.queryProjectUsers(record);
			List<Long> myProjectIDs = new ArrayList<Long>();
			myProjectIDs.add(-1L);
			for (ProjectsUserModel projectsUserModel : projectsUserModels) {
				myProjectIDs.add(Long.valueOf(projectsUserModel.getPid()));
			}

			ProjectModelExample example = new ProjectModelExample();
			example.or()
				.andSystemidEqualTo(systemType.getValue())
				.andOverstateEqualTo(1)
				.andOwnerEqualTo(1)
				.andIdIn(myProjectIDs);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));
			
			example.clear();
			example.or()
				.andSystemidEqualTo(systemType.getValue())
				.andOverstateEqualTo(1)
				.andOwnerEqualTo(0);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));
			
			if (myProjects != null && !myProjects.isEmpty()) {
				List<Long> _myProjectIDs = new ArrayList<Long>();
				for (ProjectModel myProject : myProjects) {
					_myProjectIDs.add(myProject.getId());
				}
				
				task = taskModelClient.selectMyNextEditTask(_myProjectIDs, userid);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return task;
	}
}
