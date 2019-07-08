package com.emg.poiwebeditor.ctrl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import com.emg.poiwebeditor.pojo.TaskModel;


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
		return "edit";
	}
	
	@RequestMapping(params = "atn=submitedittask")
	public ModelAndView submitEditTask(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START submit");
		logger.debug(new Date().getTime() + "");
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
			taskModelClient.updateModifyTask(taskid, userid, 5, 5);
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
		String tel = ParamUtils.getParameter(request, "tel");
		Long featcode = ParamUtils.getLongParameter(request, "featcode", 0);
		String sortcode = ParamUtils.getParameter(request, "sortcode");
		String address4 = ParamUtils.getParameter(request, "address4");
		String address5 = ParamUtils.getParameter(request, "address5");
		String address6 = ParamUtils.getParameter(request, "address6");
		String address7 = ParamUtils.getParameter(request, "address7");
		String address8 = ParamUtils.getParameter(request, "address8");
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
				}else if("sortcocde".equals(log.getK())) {
					log.setOldValue(flag ? null : savePoi.getSortcode());
				}else if("namec".equals(log.getK())) {
					log.setOldValue(flag ? null : savePoi.getNamec());
				}else if("address8".equals(log.getK())) {
					Optional<TagDO> tags = savePoi.getPoitags().stream().filter(e -> "address8".equals(e.getK())).findFirst();
					if (tags.isPresent()) {
						TagDO t = tags.get();
						log.setOldValue(flag ? null : t.getV());
					}
					
				}else if("tel".equals(log.getK())) {
					Optional<TagDO> tags = savePoi.getPoitags().stream().filter(e ->"tel".equals(e.getK())).findFirst();
					if (tags.isPresent()) {
						TagDO t = tags.get();
						log.setOldValue(flag ? null : t.getV());
					}
					
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
		if (savePoi != null && savePoi.getPoitags() != null) {
			Set<TagDO> saveTags = savePoi.getPoitags();
			saveAddress(saveTags, tags,address4, "address4", "address4e", "address4p", oid);
			saveAddress(saveTags, tags,address5, "address5", "address5e", "address5p", oid);
			saveAddress(saveTags, tags,address6, "address6", "address6e", "address6p", oid);
			saveAddress(saveTags, tags,address7, "address7", "address7e", "address7p", oid);
			saveAddress(saveTags, tags,address8, "address8", "address8e", "address8p", oid);
			
			for (TagDO tag : saveTags) {
				if (!savePoi.getNamec().equals(namec)) {
					if ("namep".equals(tag.getK())) {
						TagDO namep = new TagDO();
						namep.setId(oid);
						namep.setK(POIAttrnameEnum.namep.toString());
						namep.setV(null);
						tags.add(namep);
					}else if("namee".equals(tag.getK())) {
						TagDO namee = new TagDO();
						namee.setId(oid);
						namee.setK(POIAttrnameEnum.namee.toString());
						namee.setV(null);
						tags.add(namee);
					}else if("names".equals(tag.getK())) {
						TagDO namees = new TagDO();
						namees.setId(oid);
						namees.setK(POIAttrnameEnum.names.toString());
						namees.setV(null);
						tags.add(namees);
					}else if("namesp".equals(tag.getK())) {
						TagDO namesp = new TagDO();
						namesp.setId(oid);
						namesp.setK(POIAttrnameEnum.namesp.toString());
						namesp.setV(null);
						tags.add(namesp);
					}else if ("namese".equals(tag.getK())) {
						TagDO namese = new TagDO();
						namese.setId(oid);
						namese.setK(POIAttrnameEnum.namese.toString());
						namese.setV(null);
						tags.add(namese);
					}
				}else if ("tel".equals(tag.getK())) {
						telTag =tag;
				}
			}
		}
		if (telTag !=null) {
			telTag.setId(oid);
			telTag.setK(POIAttrnameEnum.tel.toString());
			telTag.setV(tel);
			tags.add(telTag);
		}else if(telTag == null && tel != null && !tel.isEmpty()){
			telTag = new TagDO();
			telTag.setId(oid);
			telTag.setK(POIAttrnameEnum.tel.toString());
			telTag.setV(tel);
			tags.add(telTag);
		}
		TagDO inputdatatype = new TagDO();
		inputdatatype.setId(oid);
		inputdatatype.setK(POIAttrnameEnum.inputdatatype.toString());
		inputdatatype.setV(4 + "");
		tags.add(inputdatatype);
		TagDO remarkTag = new TagDO();
		remarkTag.setId(oid);
		remarkTag.setK(POIAttrnameEnum.remark.toString());
		SimpleDateFormat f   = new SimpleDateFormat("yyyyMMdd");   
        String date = f.format(new Date()); 
        remarkTag.setV((remark == null || remark.isEmpty()) ? date + "四方检索" : remark + ";" + date + "四方检索");
		tags.add(remarkTag);
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
}
