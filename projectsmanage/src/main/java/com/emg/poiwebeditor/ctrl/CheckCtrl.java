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
import com.emg.poiwebeditor.pojo.ModifiedlogDO;
import com.emg.poiwebeditor.pojo.POIDo;
import com.emg.poiwebeditor.pojo.PoiMergeDO;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsUserModel;
import com.emg.poiwebeditor.pojo.ReferdataModel;
import com.emg.poiwebeditor.pojo.TagDO;
import com.emg.poiwebeditor.pojo.TaskLinkPoiModel;
import com.emg.poiwebeditor.pojo.TaskModel;


@Controller
@RequestMapping("/check.web")
public class CheckCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(CheckCtrl.class);
	
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
	
	private static final int BAIDU = 45;
	private static final int TENGXUN = 46;
	private static final int GAODE = 47;

	@RequestMapping(method = RequestMethod.GET)
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("OPENLADER");
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			// taskModelClient.updateTaskState(userid, 5, 5);
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
		return "check";
	}
	
	@RequestMapping(params = "atn=submitedittask")
	public ModelAndView submitEditTask(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START submit");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		long ret = -1l;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			String saveRelations = ParamUtils.getParameter(request, "relations");
			String source = ParamUtils.getParameter(request, "source");
			POIDo poi = null;
			List<ModifiedlogDO> logs = new ArrayList<ModifiedlogDO>();
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
				logger.debug("4.1: " + new Date().getTime() + "");
				publicClient.updateModifiedlogs( logs);
				
			}
			if (relations != null) {
				ret = publicClient.updateRelations(u,  relations);
			}
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
			ret = -1l;
			json.addObject("error", e.getMessage());
		}
		json.addObject("task", task);
		json.addObject("project", project);
		json.addObject("process", process);
		json.addObject("keywordid", keywordid);
		json.addObject("result", ret);

		logger.debug("END submit");
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
			taskModelClient.updateModifyTask(taskid, userid, 5, 7);
			Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
			
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
				ret = 1l;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			
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
	
	@RequestMapping(params = "atn=getrelatedpoi")
	public ModelAndView getRelatedPoi(Model model, HttpServletRequest request, HttpSession session) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		POIDo poi = new POIDo();
		try {
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			if (taskid != -1) {
				TaskLinkPoiModel link = taskModelClient.selectTaskPoi(taskid);
				if (link == null || link.getPoiId() == -1) return null;
				long oid = link.getPoiId();
				poi = poiClient.selectPOIByOid(oid);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			poi = new POIDo();
		}
		json.addObject("poi", poi);
		json.addObject("count", 1);
		json.addObject("result", 1);

		return json;
	}
	
	
	@RequestMapping(params = "atn=getRelationByOid")
	public ModelAndView getRelationByOid(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<PoiMergeDO> relations = null;
		try {
			long oid = ParamUtils.getLongParameter(request, "oid", -1l);
			relations = publicClient.selectPOIRelation(oid);
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
			for(ReferdataModel ref : referdatas) {
				if(ref.getTel() == null || ref.getTel().isEmpty()) continue;
				String tels[] = null;
				//提前把字符串排好序，方便在界面上比较四家电话是否完全相同
				if(ref.getSrcType() == BAIDU) tels = ref.getTel().split(",");
				else if(ref.getSrcType() == TENGXUN) tels = ref.getTel().split("; ");
				else if(ref.getSrcType() == GAODE) tels = ref.getTel().split(";");
				if (tels == null) continue;
				for (int i = 0; i < tels.length-1; i++) {
					for(int j = i + 1; j < tels.length ; j++) {
						if (tels[i].compareTo(tels[j]) > 0) {
							String temp = tels[i];
							tels[i]=tels[j];
							tels[j] = temp;
						}
					}
				}
				StringBuffer tel = new StringBuffer();
				for (int i = 0; i < tels.length; i++) {
					tel.append(tels[i] + ";");
				}
				ref.setTel(tel.substring(0,tel.length() - 1));
			}
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
			Long userid = ParamUtils.getLongAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			POIDo  poi = this.getPOI(request, null);
			poi.setConfirm(ConfirmEnum.confirm_ok);
			poi.setConfirmUId(userid);
			poi.setUid(userid);
			poi.setId(oid);
			poi.setSystemId(SystemType.poi_polymerize.getValue());
			logger.debug(JSON.toJSON(poi).toString());
			
			
			ret = poiClient.deletePOIByOid(poi);
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
			POIDo  poi = this.getPOI(request, null);
			String geo = ParamUtils.getParameter(request, "geo");
			poi.setGeo(geo);
			poi.setConfirm(ConfirmEnum.confirm_ok);
			poi.setConfirmUId(userid);
			poi.setUid(userid);
			
			
			logger.debug(JSON.toJSON(poi).toString());
			
			if (poi != null) {
				ret = poiClient.updatePOI(userid, poi);
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
	
	/**
	 * 根据前台传递过来的参数设置POI
	 * @return
	 */
	private POIDo getPOI(HttpServletRequest request, List<ModifiedlogDO> logs) throws Exception{
		Long oid = ParamUtils.getLongParameter(request, "oid", -1);
		
		String namec = ParamUtils.getParameter(request, "namec");
		Long featcode = ParamUtils.getLongParameter(request, "featcode", 0);
		String sortcode = ParamUtils.getParameter(request, "sortcode");
		String remark = ParamUtils.getParameter(request, "remark");
		String geo = ParamUtils.getParameter(request, "geo");
		Long projectId = ParamUtils.getLongParameter(request, "projectId", 0);
		POIDo poi = new POIDo();
		poi.setNamec(namec);
		
		logger.debug(JSON.toJSON(poi).toString());
		POIDo savePoi = poiClient.selectPOIByOid(oid);
		if (oid < 0) {
			oid = poiClient.getPoiId();
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
		poi.setSystemId(370);
		poi.setConfirm(ConfirmEnum.ready_for_qc);
		poi.setAutoCheck(CheckEnum.uncheck);
		poi.setManualCheck(CheckEnum.uncheck);
		poi.setOpTypeEnum(OpTypeEnum.submit);
		poi.setGeo(geo);
		poi.setFeatcode(featcode);
		poi.setSortcode(sortcode);
		poi.setProjectid(projectId);
		Set<TagDO> tags = poi.getPoitags();
		TagDO telTag = null;
		TagDO remarkTag = null;
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
				}else if ("remark".equals(tag.getK()) && remark != null) {
					
					SimpleDateFormat f   = new SimpleDateFormat("yyyyMMdd");   
			        String date = f.format(new Date()); 
			        tag.setV((tag.getV() == null ) ? date + "四方检索" : tag.getV() + ";" + date + "四方检索");
					tags.add(tag);
					remarkTag = tag;
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
	
	/**
	 * 保存address值，同时看数据库里相应的中英文是否为空，不为空则打isdel =t
	 * @param saveTags 
	 * @param tags
	 * @param address
	 * @param addressKey
	 * @param addresse
	 * @param addressp
	 * @param oid
	 */
	private void saveAddress(Set<TagDO> saveTags, Set<TagDO> tags, String address, String addressKey,String addresse, String addressp, long oid) {
		TagDO addresstag = this.getTag(saveTags, addressKey);
		TagDO tag4e = this.getTag(saveTags, addresse);
		TagDO tag4p = this.getTag(saveTags, addressp);
		if (addresstag == null && address != null) {
			TagDO tag4 = new TagDO();
			tag4.setId(oid);
			tag4.setK(addressKey);
			tag4.setV(address);
			tags.add(tag4);
			
			if(tag4e != null) {
				tag4e.setV(null);
				tags.add(tag4e);
			}
			
			if(tag4p != null) {
				tag4p.setV(null);
				tags.add(tag4p);
			}
		} else if (addresstag != null && address == null) {
			addresstag.setV(null);
			tags.add(addresstag);
			if(tag4e != null) {
				tag4e.setV(null);
				tags.add(tag4e);
			}
			
			if(tag4p != null) {
				tag4p.setV(null);
				tags.add(tag4p);
			}
		} else if(addresstag != null && address != null && !addresstag.getV().equals(address)) {
			addresstag.setV(address);
			tags.add(addresstag);
			if(tag4e != null) {
				tag4e.setV(null);
				tags.add(tag4e);
			}
			
			if(tag4p != null) {
				tag4p.setV(null);
				tags.add(tag4p);
			}
		}
	}
	
	/**
	 * 从tags中查找指定key的tag, 并且满足相应的条件
	 * @param tags
	 * @param key
	 * @param compare
	 * @return
	 */
	private TagDO getTag(Set<TagDO> tags, String key) {
		if(tags == null || tags.isEmpty() || key == null) return null;
		for (TagDO tag : tags) {
			if (key.equals(tag.getK())) {
				// tag.setV(null);
				return tag;
			}
		}
		return null;
	}
	
	/**
	 * 用数据库中现在已经存在的relation和需要保存的relation进行比较，确定哪些是需要新增修改的，哪些是需要删除的
	 */
	public void getSaveRelation(POIDo poi, Long keywordid)  throws Exception{
		List<PoiMergeDO> relations = poiClient.selectPOIRelation(poi.getId() + "");
		List<ReferdataModel>referdatas = publicClient.selectReferdatasByKeywordid(keywordid);
		// for ()
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
	private TaskModel getNextEditTask(Integer userid) {
		logger.debug("8: " + new Date().getTime() + "");
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
				
				task = taskModelClient.selectNextCheckTask(_myProjectIDs, userid);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("8.1: " + new Date().getTime() + "");
		return task;
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
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Long keywordId = ParamUtils.getLongParameter(request, "keywordid", -1);
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			
			Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
			Long u = new Long(userid);
			KeywordModel keyword = new KeywordModel();
			keyword.setId(keywordId);
			keyword.setState(1);
			publicClient.updateKeyword(keyword);
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
	
	@RequestMapping(params = "atn=markusererror")
	public ModelAndView markUserError(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			taskModelClient.updateCheckTaskState(taskid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
}