package com.emg.poiwebeditor.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
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
import com.emg.poiwebeditor.dao.task.ErrorModelDao;
import com.emg.poiwebeditor.pojo.ErrorModel;
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.POIDo;
import com.emg.poiwebeditor.pojo.PoiMergeDO;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsUserModel;
import com.emg.poiwebeditor.pojo.RTaskInfo;
import com.emg.poiwebeditor.pojo.ReferdataModel;
import com.emg.poiwebeditor.pojo.STaskModel;
import com.emg.poiwebeditor.pojo.TagDO;
import com.emg.poiwebeditor.pojo.TaskLinkPoiModel;
import com.emg.poiwebeditor.pojo.TaskModel;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/modify.web")
public class ModifyErrorCtrl {
	private static final Logger logger = LoggerFactory.getLogger(ModifyCtrl.class);

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
	@Autowired
	private ErrorModelDao errorModelDao;
	
	

	// byhxz20190522
	@RequestMapping(method = RequestMethod.GET)
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("OPENLADER");
		
		TaskModel task = new TaskModel();
		STaskModel stask = new STaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
		try {
			stask = getModifyTask(userid);
			if (stask != null) {
				task = stask.getTaskModel();
				if (task != null && task.getId() != null) {
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

		model.addAttribute("task", task);
		model.addAttribute("project", project);
		model.addAttribute("process", process);
		model.addAttribute("keywordid", keywordid);
		if( stask != null) {
			model.addAttribute("poiid", stask.getPoiId());
			JSONArray errobject = JSONArray.fromObject(stask.getErrorList());
			model.addAttribute("errorlist", errobject);
		}else {
			model.addAttribute("poiid", -1L);
		//	model.addAttribute("errorlist", null);
		}

		logger.debug("OPENLADER OVER");
		return "modify";
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

	private TaskModel getNextModifyTask(Integer userid) {
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
			example.or().andSystemidEqualTo(systemType.getValue()).andOverstateEqualTo(1).andOwnerEqualTo(1)
					.andIdIn(myProjectIDs);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));

			example.clear();
			example.or().andSystemidEqualTo(systemType.getValue()).andOverstateEqualTo(1).andOwnerEqualTo(0);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));

			if (myProjects != null && !myProjects.isEmpty()) {
				List<Long> _myProjectIDs = new ArrayList<Long>();
				for (ProjectModel myProject : myProjects) {
					_myProjectIDs.add(myProject.getId());
				}

				task = taskModelClient.selectMyNextModifyTaskByProjectsAndUserId(_myProjectIDs, userid);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return task;
	}

	// byhxz20190520
//	private TaskLinkPoiModel getLinkPoi(Long taskid) {
//		TaskLinkPoiModel linkpoi = new TaskLinkPoiModel();
//		try {
//			linkpoi = taskModelClient.selectTaskLinkPoiByTaskid(taskid);
//
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//
//		return null;
//	}

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

	// 提交任务
	@RequestMapping(params = "atn=submitmodifytask")
	public ModelAndView submitModifyTask(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		TaskModel task = new TaskModel();
		STaskModel stask = new STaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			POIDo poi = this.getPOI(request);
			poi.setConfirmUId(Long.valueOf(userid));
			poi.setUid(Long.valueOf(userid));
			Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
			String strerrorids = ParamUtils.getParameter(request, "errorids");
//			curTaskId = taskid;
			Long ret = poiClient.updatePOIToDB(poi);
			if( ret <1 ) {
				throw new Exception("poi 提交失败 " + poi.getId() );
			}
			List<Long> errorids = new ArrayList<Long>();
//			Integer errorcount = curErrorList.size();
			Integer length = strerrorids.split(",").length;
			for (Integer i = 0; i < length; i++) {
				String sid = strerrorids.split(",")[i];
				errorids.add( Long.valueOf(sid) );
			}
			// 先修改错误状态
			if (errorModelDao.updateErrors(errorids).compareTo(0L) <= 0) {
				json.addObject("resultMsg", "修改错误状态为解决失败");
				json.addObject("result", 0);
				return json;
			}
			if (taskModelClient.submitModifyTask(taskid, userid, 2).compareTo(0L) <= 0) {
				if (taskModelClient.submitModifyTask(taskid, userid, 2).compareTo(0L) <= 0) {
					json.addObject("resultMsg", "任务提交失败");
					json.addObject("result", 0);
					return json;
				}
			}


			if (getnext) {
				stask = getModifyTask(userid);
				if (stask != null) {
					task = stask.getTaskModel();
					if (task != null && task.getId() != null) {
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
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("task", task);
		json.addObject("project", project);
		json.addObject("process", process);
		json.addObject("keywordid", keywordid);
		json.addObject("result", 1);

		if( stask != null) {
			model.addAttribute("poiid", stask.getPoiId());
			model.addAttribute("errorlist", stask.getErrorList());
		}else {
			model.addAttribute("poiid", -1L);
			model.addAttribute("errorlist", null);
		}

		logger.debug("END");
		return json;
	}

	// 保存POI
	@RequestMapping(params = "atn=updatepoibyoid")
	public ModelAndView updateModifyTask(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			POIDo poi = this.getSavePOI(request);
			poi.setConfirmUId(Long.valueOf(userid));
			poi.setUid(Long.valueOf(userid));
			Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
			Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
//			curTaskId = taskid;
			poiClient.updatePOIToDB(poi);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		json.addObject("result", 1);
		logger.debug("END");
		return json;
	}

	// 获取错误
	@RequestMapping(params = "atn=geterrorlistbyid")
	public ModelAndView getErrorList(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());

		List<ErrorModel> errorlist = new ArrayList<ErrorModel>();
		Long poiid = ParamUtils.getLongParameter(request, "poiid", -1);
		if(poiid > 0) {
			errorlist = errorModelDao.selectErrorsbyPoiid( poiid);
			if(errorlist.size() > 0)
				json.addObject("result", 1);
			else
				json.addObject("result", 0);
		}else {
			json.addObject("result", 0);	
		}
		json.addObject("errorlist", errorlist);
		

		logger.debug("END");
		return json;
	}

	private TaskModel getNextModifyTaskByProjectId(Integer userid, Long projectid, Long taskid) {
		TaskModel task = new TaskModel();
		// 查询当前项目下，当前任务 为基准的下一个任务 (前提条件：任务是没有优先级，就是顺序往下拿的)
		// 为什么不用任务List的任务 来筛选是担心任务id太多导致sql很长
		try {
			task = taskModelClient.selectMyNextModifyTaskByProjectIdAndTaskId(projectid, taskid, userid);
			if (task != null && task.getId() != null)
				taskModelClient.updateModifyTask(task.getId(), userid, 1, 6);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return task;
	}

	// 获取没有查询过的项目中可作业的任务
	private TaskModel getNextModifyTaskNotDoneProject(Integer userid,List<Long> doneProjectList) {
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
			example.or().andSystemidEqualTo(systemType.getValue()).andOverstateEqualTo(1).andOwnerEqualTo(1)
					.andIdIn(myProjectIDs);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));

			example.clear();
			example.or().andSystemidEqualTo(systemType.getValue()).andOverstateEqualTo(1).andOwnerEqualTo(0);
			example.setOrderByClause("priority DESC, id");
			myProjects.addAll(projectModelDao.selectByExample(example));

			if (myProjects != null && !myProjects.isEmpty()) {
				List<Long> _myProjectIDs = new ArrayList<Long>();
				Integer doneprojectcount = doneProjectList.size();
				for (ProjectModel myProject : myProjects) {
					Boolean bDone = false;
					for (int indexdone = 0; indexdone < doneprojectcount; indexdone++) {
						Long pid = myProject.getId();
						Long donepid = doneProjectList.get(indexdone);
						if (pid.compareTo(donepid) == 0) {
							bDone = true;
							break;
						}
					}
					if (!bDone)
						_myProjectIDs.add(myProject.getId());
				}

				task = taskModelClient.selectMyNextModifyTaskByProjectsAndUserId(_myProjectIDs, userid);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return task;
	}


	/**
	 * 根据前台传递过来的参数设置POI
	 * 
	 * @return
	 */
	private POIDo getPOI(HttpServletRequest request) throws Exception {
		Long oid = ParamUtils.getLongParameter(request, "oid", -1);

		String namec = checkString( ParamUtils.getParameter(request, "namec"));
//		String tel = checkString ( ParamUtils.getParameter(request, "tel") );
		Long featcode = ParamUtils.getLongParameter(request, "featcode", 0);
		String sortcode = checkString( ParamUtils.getParameter(request, "sortcode"));
//		String address4 = checkString( ParamUtils.getParameter(request, "address4"));
//		String address5 = checkString( ParamUtils.getParameter(request, "address5"));
//		String address6 = checkString( ParamUtils.getParameter(request, "address6"));
//		String address7 = checkString( ParamUtils.getParameter(request, "address7"));
//		String address8 = checkString( ParamUtils.getParameter(request, "address8"));
		String webnamep = checkString( ParamUtils.getParameter(request, "namep"));
		String names =    checkString( ParamUtils.getParameter(request, "names"));
		String webnamee = checkString( ParamUtils.getParameter(request, "namee"));
		String webnamesp = checkString( ParamUtils.getParameter(request, "namesp"));
		String address1 =  checkString( ParamUtils.getParameter(request, "address1"));
		String address1p = checkString( ParamUtils.getParameter(request, "address1p"));
		String address1e = checkString( ParamUtils.getParameter(request, "address1e"));
		String address2 =  checkString( ParamUtils.getParameter(request, "address2"));
		String address2p = checkString( ParamUtils.getParameter(request, "address2p"));
		String address2e = checkString( ParamUtils.getParameter(request, "address2e"));
		String address3 =  checkString( ParamUtils.getParameter(request, "address3"));
		String address3p = checkString( ParamUtils.getParameter(request, "address3p"));
		String address3e = checkString( ParamUtils.getParameter(request, "address3e"));
		Integer owner =  ParamUtils.getIntParameter(request, "owner", 0);
		String postalcode = checkString( ParamUtils.getParameter(request, "postalcode"));
		
		//主表字段赋值null 不会清空字段
		if(namec == null)
			namec = "";
		if(sortcode == null)
			sortcode = "";
		

		String geo = ParamUtils.getParameter(request, "poigeo");
		Long projectId = ParamUtils.getLongParameter(request, "projectId", 0);

		POIDo poi = new POIDo();
		poi.setNamec(namec);
		logger.debug(JSON.toJSON(poi).toString());

		POIDo savePoi = poiClient.selectPOIByOid(oid);

		if (oid < 0) {
			oid = poiClient.getPoiId();
			poi.setGrade(GradeEnum.general);
		} else {
			poi.setGrade(savePoi.getGrade());
		}
		poi.setId(oid);
		poi.setSystemId(370);
		poi.setGeo(geo);
		poi.setFeatcode(featcode);
		poi.setSortcode(sortcode);
		poi.setProjectid(projectId);
		poi.setOwner( owner);
		poi.setConfirm(ConfirmEnum.ready_for_qc);
		poi.setAutoCheck(CheckEnum.uncheck);
		poi.setManualCheck(CheckEnum.uncheck);
		poi.setOpTypeEnum(OpTypeEnum.submit);
	
		
	
		Set<TagDO> tags = poi.getPoitags();
		TagDO telTag = null;
		TagDO tagnamep = null;
		TagDO tagnames = null;
		TagDO tagnamee = null;
		TagDO tagnamesp = null;
		TagDO tag1 = null;
		TagDO tag1p = null;
		TagDO tag1e = null;
		TagDO tag2 = null;
		TagDO tag2p = null;
		TagDO tag2e = null;
		TagDO tag3 = null;
		TagDO tag3p = null;
		TagDO tag3e = null;
		TagDO tagpostalcode = null;
		if (savePoi != null && savePoi.getPoitags() != null) {
			Set<TagDO> saveTags = savePoi.getPoitags();

//			saveAddress(saveTags, tags, address4, "address4", "address4e", "address4p", oid);
//			saveAddress(saveTags, tags, address5, "address5", "address5e", "address5p", oid);
//			saveAddress(saveTags, tags, address6, "address6", "address6e", "address6p", oid);
//			saveAddress(saveTags, tags, address7, "address7", "address7e", "address7p", oid);
//			saveAddress(saveTags, tags, address8, "address8", "address8e", "address8p", oid);

			for (TagDO tag : saveTags) {
				if (!savePoi.getNamec().equals(namec)) {
					if ("namep".equals(tag.getK())) {
						TagDO namep = new TagDO();
						namep.setId(oid);
						namep.setK(POIAttrnameEnum.namep.toString());
						namep.setV(null);
						tags.add(namep);
					} else if ("namee".equals(tag.getK())) {
						TagDO namee = new TagDO();
						namee.setId(oid);
						namee.setK(POIAttrnameEnum.namee.toString());
						namee.setV(null);
						tags.add(namee);
					} else if ("names".equals(tag.getK())) {
						TagDO namees = new TagDO();
						namees.setId(oid);
						namees.setK(POIAttrnameEnum.names.toString());
						namees.setV(null);
						tags.add(namees);
					} else if ("namesp".equals(tag.getK())) {
						TagDO namesp = new TagDO();
						namesp.setId(oid);
						namesp.setK(POIAttrnameEnum.namesp.toString());
						namesp.setV(null);
						tags.add(namesp);
					} else if ("namese".equals(tag.getK())) {
						TagDO namese = new TagDO();
						namese.setId(oid);
						namese.setK(POIAttrnameEnum.namese.toString());
						namese.setV(null);
						tags.add(namese);
					}
				} else if ("tel".equals(tag.getK())) {
					telTag = tag;
				} else if ("namep".equals(tag.getK())) {
					tagnamep = tag;
				} else if ("names".equals(tag.getK())) {
					tagnames = tag;
				} else if ("namee".equals(tag.getK())) {
					tagnamee = tag;
				} else if ("namesp".equals(tag.getK())) {
					tagnamesp = tag;
				} else if ("address1".equals(tag.getK())) {
					tag1 = tag;
				} else if ("address1p".equals(tag.getK())) {
					tag1p = tag;
				} else if ("address1e".equals(tag.getK())) {
					tag1e = tag;
				} else if ("address2".equals(tag.getK())) {
					tag2 = tag;
				} else if ("address2p".equals(tag.getK())) {
					tag2p = tag;
				} else if ("address2e".equals(tag.getK())) {
					tag2e = tag;
				} else if ("address3".equals(tag.getK())) {
					tag3 = tag;
				} else if ("address3p".equals(tag.getK())) {
					tag3p = tag;
				} else if ("address3e".equals(tag.getK())) {
					tag3e = tag;
				}  else if ("postalcode".equals(tag.getK())) {
					tagpostalcode = tag;
				}
			}

		} // for (TagDO tag : saveTags) {

//		if (telTag != null) {
//			telTag.setId(oid);
//			telTag.setK(POIAttrnameEnum.tel.toString());
//			telTag.setV(tel);
//			tags.add(telTag);
//		} else if (telTag == null && tel != null && !tel.isEmpty()) {
//			telTag = new TagDO();
//			telTag.setId(oid);
//			telTag.setK(POIAttrnameEnum.tel.toString());
//			telTag.setV(tel);
//			tags.add(telTag);
//		}

		if (tagnamep != null) {
			tagnamep.setId(oid);
			tagnamep.setK(POIAttrnameEnum.namep.toString());
			tagnamep.setV(webnamep);
			tags.add(tagnamep);
		} else if (webnamep != null && !webnamep.isEmpty()) {
			tagnamep = new TagDO();
			tagnamep.setId(oid);
			tagnamep.setK(POIAttrnameEnum.namep.toString());
			tagnamep.setV(webnamep);
			tags.add(tagnamep);
		}

		if (tagnames != null) {
			tagnames.setId(oid);
			tagnames.setK(POIAttrnameEnum.names.toString());
			tagnames.setV(names);
			tags.add(tagnames);
		} else if (names != null && !names.isEmpty()) {
			tagnames = new TagDO();
			tagnames.setId(oid);
			tagnames.setK(POIAttrnameEnum.names.toString());
			tagnames.setV(names);
			tags.add(tagnames);
		}

		if (tagnamee != null) {
			tagnamee.setId(oid);
			tagnamee.setK(POIAttrnameEnum.namee.toString());
			tagnamee.setV(webnamee);
			tags.add(tagnamee);
		} else if (webnamee != null && !webnamee.isEmpty()) {
			tagnamee = new TagDO();
			tagnamee.setId(oid);
			tagnamee.setK(POIAttrnameEnum.namee.toString());
			tagnamee.setV(webnamee);
			tags.add(tagnamee);
		}

		if (tagnamesp != null) {
			tagnamesp.setId(oid);
			tagnamesp.setK(POIAttrnameEnum.namesp.toString());
			tagnamesp.setV(webnamesp);
			tags.add(tagnamesp);
		} else if (webnamesp != null && !webnamesp.isEmpty()) {
			tagnamesp = new TagDO();
			tagnamesp.setId(oid);
			tagnamesp.setK(POIAttrnameEnum.namesp.toString());
			tagnamesp.setV(webnamesp);
			tags.add(tagnamesp);
		}

		if (tag1 != null) {
			tag1.setId(oid);
			tag1.setK(POIAttrnameEnum.address1.toString());
			tag1.setV(address1);
			tags.add(tag1);
		} else if (address1 != null && !address1.isEmpty()) {
			tag1 = new TagDO();
			tag1.setId(oid);
			tag1.setK(POIAttrnameEnum.address1.toString());
			tag1.setV(address1);
			tags.add(tag1);
		}

		if (tag1p != null) {
			tag1p.setId(oid);
			tag1p.setK(POIAttrnameEnum.address1p.toString());
			tag1p.setV(address1p);
			tags.add(tag1p);
		} else if (address1p != null && !address1p.isEmpty()) {
			tag1p = new TagDO();
			tag1p.setId(oid);
			tag1p.setK(POIAttrnameEnum.address1p.toString());
			tag1p.setV(address1p);
			tags.add(tag1p);
		}

		if (tag1e != null) {
			tag1e.setId(oid);
			tag1e.setK(POIAttrnameEnum.address1e.toString());
			tag1e.setV(address1e);
			tags.add(tag1e);
		} else if (address1e != null && !address1e.isEmpty()) {
			tag1e = new TagDO();
			tag1e.setId(oid);
			tag1e.setK(POIAttrnameEnum.address1e.toString());
			tag1e.setV(address1e);
			tags.add(tag1e);
		}

		if (tag2 != null) {
			tag2.setId(oid);
			tag2.setK(POIAttrnameEnum.address2.toString());
			tag2.setV(address2);
			tags.add(tag2);
		} else if (address2 != null && !address2.isEmpty()) {
			tag2 = new TagDO();
			tag2.setId(oid);
			tag2.setK(POIAttrnameEnum.address2.toString());
			tag2.setV(address2);
			tags.add(tag2);
		}

		if (tag2p != null) {
			tag2p.setId(oid);
			tag2p.setK(POIAttrnameEnum.address2p.toString());
			tag2p.setV(address2p);
			tags.add(tag2p);
		} else if (address2p != null && !address2p.isEmpty()) {
			tag2p = new TagDO();
			tag2p.setId(oid);
			tag2p.setK(POIAttrnameEnum.address2p.toString());
			tag2p.setV(address2p);
			tags.add(tag2p);
		}

		if (tag2e != null) {
			tag2e.setId(oid);
			tag2e.setK(POIAttrnameEnum.address2e.toString());
			tag2e.setV(address2e);
			tags.add(tag2e);
		} else if (address2e != null && !address2e.isEmpty()) {
			tag2e = new TagDO();
			tag2e.setId(oid);
			tag2e.setK(POIAttrnameEnum.address2e.toString());
			tag2e.setV(address2e);
			tags.add(tag2e);
		}

		if (tag3 != null) {
			tag3.setId(oid);
			tag3.setK(POIAttrnameEnum.address3.toString());
			tag3.setV(address3);
			tags.add(tag3);
		} else if (address3 != null && !address3.isEmpty()) {
			tag3 = new TagDO();
			tag3.setId(oid);
			tag3.setK(POIAttrnameEnum.address3.toString());
			tag3.setV(address3);
			tags.add(tag3);
		}

		if (tag3p != null) {
			tag3p.setId(oid);
			tag3p.setK(POIAttrnameEnum.address3p.toString());
			tag3p.setV(address3p);
			tags.add(tag3p);
		} else if (address3p != null && !address3p.isEmpty()) {
			tag3p = new TagDO();
			tag3p.setId(oid);
			tag3p.setK(POIAttrnameEnum.address3p.toString());
			tag3p.setV(address3p);
			tags.add(tag3p);
		}

		if (tag3e != null) {
			tag3e.setId(oid);
			tag3e.setK(POIAttrnameEnum.address3e.toString());
			tag3e.setV(address3e);
			tags.add(tag3e);
		} else if (address3e != null && !address3e.isEmpty()) {
			tag3e = new TagDO();
			tag3e.setId(oid);
			tag3e.setK(POIAttrnameEnum.address3e.toString());
			tag3e.setV(address3e);
			tags.add(tag3e);
		}

		if (tagpostalcode != null) {
			tagpostalcode.setId(oid);
			tagpostalcode.setK(POIAttrnameEnum.postalcode.toString());
			tagpostalcode.setV(postalcode);
			tags.add(tagpostalcode);
		} else if (postalcode != null && !postalcode.isEmpty()) {
			tagpostalcode = new TagDO();
			tagpostalcode.setId(oid);
			tagpostalcode.setK(POIAttrnameEnum.postalcode.toString());
			tagpostalcode.setV(postalcode);
			tags.add(tagpostalcode);
		}

		return poi;
	}

	/**
	 * 根据前台传递过来的参数设置POI 只保存POI
	 * 
	 * @return
	 */
	private POIDo getSavePOI(HttpServletRequest request) throws Exception {
		Long oid = ParamUtils.getLongParameter(request, "oid", -1);

		String namec = checkString( ParamUtils.getParameter(request, "namec"));
//		String tel = checkString ( ParamUtils.getParameter(request, "tel") );
		Long featcode = ParamUtils.getLongParameter(request, "featcode", 0);
		String sortcode = checkString( ParamUtils.getParameter(request, "sortcode"));
//		String address4 = checkString( ParamUtils.getParameter(request, "address4"));
//		String address5 = checkString( ParamUtils.getParameter(request, "address5"));
//		String address6 = checkString( ParamUtils.getParameter(request, "address6"));
//		String address7 = checkString( ParamUtils.getParameter(request, "address7"));
//		String address8 = checkString( ParamUtils.getParameter(request, "address8"));
		String webnamep = checkString( ParamUtils.getParameter(request, "namep"));
		String names =    checkString( ParamUtils.getParameter(request, "names"));
		String webnamee = checkString( ParamUtils.getParameter(request, "namee"));
		String webnamesp = checkString( ParamUtils.getParameter(request, "namesp"));
		String address1 =  checkString( ParamUtils.getParameter(request, "address1"));
		String address1p = checkString( ParamUtils.getParameter(request, "address1p"));
		String address1e = checkString( ParamUtils.getParameter(request, "address1e"));
		String address2 =  checkString( ParamUtils.getParameter(request, "address2"));
		String address2p = checkString( ParamUtils.getParameter(request, "address2p"));
		String address2e = checkString( ParamUtils.getParameter(request, "address2e"));
		String address3 =  checkString( ParamUtils.getParameter(request, "address3"));
		String address3p = checkString( ParamUtils.getParameter(request, "address3p"));
		String address3e = checkString( ParamUtils.getParameter(request, "address3e"));
		Integer owner =  ParamUtils.getIntParameter(request, "owner", 0);
		String postalcode = checkString( ParamUtils.getParameter(request, "postalcode"));
		
		//主表字段赋值null 不会清空字段
		if(namec == null)
			namec = "";
		if(sortcode == null)
			sortcode = "";
		

		String geo = ParamUtils.getParameter(request, "poigeo");
		Long projectId = ParamUtils.getLongParameter(request, "projectId", 0);

		POIDo poi = new POIDo();
		poi.setNamec(namec);
		logger.debug(JSON.toJSON(poi).toString());

		POIDo savePoi = poiClient.selectPOIByOid(oid);

		if (oid < 0) {
			oid = poiClient.getPoiId();
			poi.setGrade(GradeEnum.general);
		} else {
			poi.setGrade(savePoi.getGrade());
		}
		poi.setId(oid);
		poi.setSystemId(370);
		poi.setGeo(geo);
		poi.setFeatcode(featcode);
		poi.setSortcode(sortcode);
		poi.setConfirm(ConfirmEnum.confirm_ok);
		poi.setProjectid(projectId);
		
		poi.setOwner( owner);
	
		Set<TagDO> tags = poi.getPoitags();
		TagDO telTag = null;
		TagDO tagnamep = null;
		TagDO tagnames = null;
		TagDO tagnamee = null;
		TagDO tagnamesp = null;
		TagDO tag1 = null;
		TagDO tag1p = null;
		TagDO tag1e = null;
		TagDO tag2 = null;
		TagDO tag2p = null;
		TagDO tag2e = null;
		TagDO tag3 = null;
		TagDO tag3p = null;
		TagDO tag3e = null;
		TagDO tagpostalcode = null;
		if (savePoi != null && savePoi.getPoitags() != null) {
			Set<TagDO> saveTags = savePoi.getPoitags();

//			saveAddress(saveTags, tags, address4, "address4", "address4e", "address4p", oid);
//			saveAddress(saveTags, tags, address5, "address5", "address5e", "address5p", oid);
//			saveAddress(saveTags, tags, address6, "address6", "address6e", "address6p", oid);
//			saveAddress(saveTags, tags, address7, "address7", "address7e", "address7p", oid);
//			saveAddress(saveTags, tags, address8, "address8", "address8e", "address8p", oid);

			for (TagDO tag : saveTags) {
				if (!savePoi.getNamec().equals(namec)) {
					if ("namep".equals(tag.getK())) {
						TagDO namep = new TagDO();
						namep.setId(oid);
						namep.setK(POIAttrnameEnum.namep.toString());
						namep.setV(null);
						tags.add(namep);
					} else if ("namee".equals(tag.getK())) {
						TagDO namee = new TagDO();
						namee.setId(oid);
						namee.setK(POIAttrnameEnum.namee.toString());
						namee.setV(null);
						tags.add(namee);
					} else if ("names".equals(tag.getK())) {
						TagDO namees = new TagDO();
						namees.setId(oid);
						namees.setK(POIAttrnameEnum.names.toString());
						namees.setV(null);
						tags.add(namees);
					} else if ("namesp".equals(tag.getK())) {
						TagDO namesp = new TagDO();
						namesp.setId(oid);
						namesp.setK(POIAttrnameEnum.namesp.toString());
						namesp.setV(null);
						tags.add(namesp);
					} else if ("namese".equals(tag.getK())) {
						TagDO namese = new TagDO();
						namese.setId(oid);
						namese.setK(POIAttrnameEnum.namese.toString());
						namese.setV(null);
						tags.add(namese);
					}
				} else if ("tel".equals(tag.getK())) {
					//telTag = tag;
				} else if ("namep".equals(tag.getK())) {
					tagnamep = tag;
				} else if ("names".equals(tag.getK())) {
					tagnames = tag;
				} else if ("namee".equals(tag.getK())) {
					tagnamee = tag;
				} else if ("namesp".equals(tag.getK())) {
					tagnamesp = tag;
				} else if ("address1".equals(tag.getK())) {
					tag1 = tag;
				} else if ("address1p".equals(tag.getK())) {
					tag1p = tag;
				} else if ("address1e".equals(tag.getK())) {
					tag1e = tag;
				} else if ("address2".equals(tag.getK())) {
					tag2 = tag;
				} else if ("address2p".equals(tag.getK())) {
					tag2p = tag;
				} else if ("address2e".equals(tag.getK())) {
					tag2e = tag;
				} else if ("address3".equals(tag.getK())) {
					tag3 = tag;
				} else if ("address3p".equals(tag.getK())) {
					tag3p = tag;
				} else if ("address3e".equals(tag.getK())) {
					tag3e = tag;
				}  else if ("postalcode".equals(tag.getK())) {
					tagpostalcode = tag;
				}
			}

		} // for (TagDO tag : saveTags) {

//		if (telTag != null) {
//			telTag.setId(oid);
//			telTag.setK(POIAttrnameEnum.tel.toString());
//			telTag.setV(tel);
//			tags.add(telTag);
//		} else if (telTag == null && tel != null && !tel.isEmpty()) {
//			telTag = new TagDO();
//			telTag.setId(oid);
//			telTag.setK(POIAttrnameEnum.tel.toString());
//			telTag.setV(tel);
//			tags.add(telTag);
//		}

		if (tagnamep != null) {
			tagnamep.setId(oid);
			tagnamep.setK(POIAttrnameEnum.namep.toString());
			tagnamep.setV(webnamep);
			tags.add(tagnamep);
		} else if (webnamep != null && !webnamep.isEmpty()) {
			tagnamep = new TagDO();
			tagnamep.setId(oid);
			tagnamep.setK(POIAttrnameEnum.namep.toString());
			tagnamep.setV(webnamep);
			tags.add(tagnamep);
		}

		if (tagnames != null) {
			tagnames.setId(oid);
			tagnames.setK(POIAttrnameEnum.names.toString());
			tagnames.setV(names);
			tags.add(tagnames);
		} else if (names != null && !names.isEmpty()) {
			tagnames = new TagDO();
			tagnames.setId(oid);
			tagnames.setK(POIAttrnameEnum.names.toString());
			tagnames.setV(names);
			tags.add(tagnames);
		}

		if (tagnamee != null) {
			tagnamee.setId(oid);
			tagnamee.setK(POIAttrnameEnum.namee.toString());
			tagnamee.setV(webnamee);
			tags.add(tagnamee);
		} else if (webnamee != null && !webnamee.isEmpty()) {
			tagnamee = new TagDO();
			tagnamee.setId(oid);
			tagnamee.setK(POIAttrnameEnum.namee.toString());
			tagnamee.setV(webnamee);
			tags.add(tagnamee);
		}

		if (tagnamesp != null) {
			tagnamesp.setId(oid);
			tagnamesp.setK(POIAttrnameEnum.namesp.toString());
			tagnamesp.setV(webnamesp);
			tags.add(tagnamesp);
		} else if (webnamesp != null && !webnamesp.isEmpty()) {
			tagnamesp = new TagDO();
			tagnamesp.setId(oid);
			tagnamesp.setK(POIAttrnameEnum.namesp.toString());
			tagnamesp.setV(webnamesp);
			tags.add(tagnamesp);
		}

		if (tag1 != null) {
			tag1.setId(oid);
			tag1.setK(POIAttrnameEnum.address1.toString());
			tag1.setV(address1);
			tags.add(tag1);
		} else if (address1 != null && !address1.isEmpty()) {
			tag1 = new TagDO();
			tag1.setId(oid);
			tag1.setK(POIAttrnameEnum.address1.toString());
			tag1.setV(address1);
			tags.add(tag1);
		}

		if (tag1p != null) {
			tag1p.setId(oid);
			tag1p.setK(POIAttrnameEnum.address1p.toString());
			tag1p.setV(address1p);
			tags.add(tag1p);
		} else if (address1p != null && !address1p.isEmpty()) {
			tag1p = new TagDO();
			tag1p.setId(oid);
			tag1p.setK(POIAttrnameEnum.address1p.toString());
			tag1p.setV(address1p);
			tags.add(tag1p);
		}

		if (tag1e != null) {
			tag1e.setId(oid);
			tag1e.setK(POIAttrnameEnum.address1e.toString());
			tag1e.setV(address1e);
			tags.add(tag1e);
		} else if (address1e != null && !address1e.isEmpty()) {
			tag1e = new TagDO();
			tag1e.setId(oid);
			tag1e.setK(POIAttrnameEnum.address1e.toString());
			tag1e.setV(address1e);
			tags.add(tag1e);
		}

		if (tag2 != null) {
			tag2.setId(oid);
			tag2.setK(POIAttrnameEnum.address2.toString());
			tag2.setV(address2);
			tags.add(tag2);
		} else if (address2 != null && !address2.isEmpty()) {
			tag2 = new TagDO();
			tag2.setId(oid);
			tag2.setK(POIAttrnameEnum.address2.toString());
			tag2.setV(address2);
			tags.add(tag2);
		}

		if (tag2p != null) {
			tag2p.setId(oid);
			tag2p.setK(POIAttrnameEnum.address2p.toString());
			tag2p.setV(address2p);
			tags.add(tag2p);
		} else if (address2p != null && !address2p.isEmpty()) {
			tag2p = new TagDO();
			tag2p.setId(oid);
			tag2p.setK(POIAttrnameEnum.address2p.toString());
			tag2p.setV(address2p);
			tags.add(tag2p);
		}

		if (tag2e != null) {
			tag2e.setId(oid);
			tag2e.setK(POIAttrnameEnum.address2e.toString());
			tag2e.setV(address2e);
			tags.add(tag2e);
		} else if (address2e != null && !address2e.isEmpty()) {
			tag2e = new TagDO();
			tag2e.setId(oid);
			tag2e.setK(POIAttrnameEnum.address2e.toString());
			tag2e.setV(address2e);
			tags.add(tag2e);
		}

		if (tag3 != null) {
			tag3.setId(oid);
			tag3.setK(POIAttrnameEnum.address3.toString());
			tag3.setV(address3);
			tags.add(tag3);
		} else if (address3 != null && !address3.isEmpty()) {
			tag3 = new TagDO();
			tag3.setId(oid);
			tag3.setK(POIAttrnameEnum.address3.toString());
			tag3.setV(address3);
			tags.add(tag3);
		}

		if (tag3p != null) {
			tag3p.setId(oid);
			tag3p.setK(POIAttrnameEnum.address3p.toString());
			tag3p.setV(address3p);
			tags.add(tag3p);
		} else if (address3p != null && !address3p.isEmpty()) {
			tag3p = new TagDO();
			tag3p.setId(oid);
			tag3p.setK(POIAttrnameEnum.address3p.toString());
			tag3p.setV(address3p);
			tags.add(tag3p);
		}

		if (tag3e != null) {
			tag3e.setId(oid);
			tag3e.setK(POIAttrnameEnum.address3e.toString());
			tag3e.setV(address3e);
			tags.add(tag3e);
		} else if (address3e != null && !address3e.isEmpty()) {
			tag3e = new TagDO();
			tag3e.setId(oid);
			tag3e.setK(POIAttrnameEnum.address3e.toString());
			tag3e.setV(address3e);
			tags.add(tag3e);
		}

		if (tagpostalcode != null) {
			tagpostalcode.setId(oid);
			tagpostalcode.setK(POIAttrnameEnum.postalcode.toString());
			tagpostalcode.setV(postalcode);
			tags.add(tagpostalcode);
		} else if (postalcode != null && !postalcode.isEmpty()) {
			tagpostalcode = new TagDO();
			tagpostalcode.setId(oid);
			tagpostalcode.setK(POIAttrnameEnum.postalcode.toString());
			tagpostalcode.setV(postalcode);
			tags.add(tagpostalcode);
		}

		return poi;
	}

	private STaskModel getModifyTask(Integer userid) {
		
		if( 1 > 0)
			return getModifyTask2( userid );
		///-------------------------------------------------------------------------
		
		STaskModel stask = new STaskModel();
		TaskModel task = new TaskModel();
		//--------
		// 当前作业的项目
		Long curProjectId = -1L;
		// 当前查询的任务id
		Long curTaskId = 0L;
		// 当前作业点poiid
		Long curPoiId = -1L;
		// 一轮中已经查询过的项目(可能由于某种原因没作业而跳过，后面需要再次查询是否可作业)
		List<Long> doneProjectList = new ArrayList<Long>();
		List<ErrorModel> curErrorList = new ArrayList<ErrorModel>();
		Boolean bFindTask = false;
		// 查找第一个可作业的项目：存在可作业的任务 + 任务下可有web编辑器作业
		try {
			Integer querycount = 1000;//最大查找次数
			// 找到一个可作业的任务
			while (!bFindTask && querycount > 0) {
				querycount--;
				if (curProjectId == -1L) {
					task = getNextModifyTask(userid);
					if (task != null && task.getId() != null) {
						curProjectId = task.getProjectid();
						doneProjectList.add(curProjectId);
						curTaskId = task.getId();
						// 查询质检错误
						// 获取任务关联的POI
						TaskLinkPoiModel linkpoi = taskModelClient.selectTaskLinkPoiByTaskid(curTaskId);
						if (linkpoi == null) {
							// 关联POI不存在，任务设置质检完成 ?
							continue;// 继续找下个任务
						} else {
							curPoiId = linkpoi.getPoiId();
							POIDo poi = new POIDo();
							poi = poiClient.selectPOIByOid(curPoiId);
							if (poi.getSystemId() == 370) {// web编辑作业的点
								CheckEnum check = poi.getAutoCheck();
								if (check == CheckEnum.ok) {
									// 质检OK 设置任务状态 3,6
									if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
										// json.addObject("resultMsg", "任务提交失败");
									}
									continue;// 继续找下个任务
								} else if (check == CheckEnum.uncheck) {// 改错保存 或者 还未质检
									ConfirmEnum confirm = poi.getConfirm();
									if (confirm == ConfirmEnum.confirm_ok) {
										// 中途保存过POI
										curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
										Integer errcount = curErrorList.size();
										if (errcount > 0) {
											// 存在待修改的质检错误
											stask.setTaskMode(task);
											stask.setErrorList(curErrorList);
											stask.setPoiId(curPoiId);
											
											bFindTask = true;
											break;// 找到作业任务
										} else {
											// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
											// 这是工具bug 或者流程 被人为修改
											continue;// 继续找下个任务
										}
									} else {// 未质检出 : 跳过任务
										continue;// 继续找下个任务
									}
								} else if (check == CheckEnum.err) {
									// 质检出错误：加载错误改错
									// 根据POI查询错误
									curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
									Integer errcount = curErrorList.size();
									if (errcount > 0) {
										// 存在待修改的质检错误
										stask.setTaskMode(task);
										stask.setErrorList(curErrorList);
										stask.setPoiId(curPoiId);
										bFindTask = true;
										break;// 找到作业任务
									} else {
										// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
										// 这是工具bug 或者流程 被人为修改
										continue;// 继续找下个任务
									}
								}
							} else {// 其他作业的点暂时不能处理：跳过任务
								continue;// 继续找下个任务
							}
						} // if (linkpoi == null) {
						// -----------------------------------------
					} else {// 第一次查询就没找到可作业任务
						int a = 0; 
						a = 1+1;
						a += 2;
						break;
					} // if( task != null && task.getId() != null)else

				} // if( curProjectid == null)
				else {// 查询当前项目下的任务
					task = getNextModifyTaskByProjectId(userid, curProjectId, curTaskId);// 刷新会调用次所以必须提交的时候才记录curtaskid
					if (task != null && task.getId() != null) {
						//---------------------
						curTaskId = task.getId();
						// 查询质检错误
						// 获取任务关联的POI
						TaskLinkPoiModel linkpoi = taskModelClient.selectTaskLinkPoiByTaskid(curTaskId);
						if (linkpoi == null) {
							// 关联POI不存在，任务设置质检完成 ?
							continue;// 继续找下个任务
						} else {
							curPoiId = linkpoi.getPoiId();
							POIDo poi = new POIDo();
							poi = poiClient.selectPOIByOid(curPoiId);
							if (poi.getSystemId() == 370) {// web编辑作业的点
								CheckEnum check = poi.getAutoCheck();
								if (check == CheckEnum.ok) {
									// 质检OK 设置任务状态 3,6
									if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
										// json.addObject("resultMsg", "任务提交失败");
									}
									continue;// 继续找下个任务
								} else if (check == CheckEnum.uncheck) {// 改错保存 或者 还未质检
									ConfirmEnum confirm = poi.getConfirm();
									if (confirm == ConfirmEnum.confirm_ok) {
										// 中途保存过POI
										curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
										Integer errcount = curErrorList.size();
										if (errcount > 0) {
											// 存在待修改的质检错误
											stask.setTaskMode(task);
											stask.setErrorList(curErrorList);
											stask.setPoiId(curPoiId);
											bFindTask = true;
											break;// 找到作业任务
										} else {
											// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
											// 这是工具bug 或者流程 被人为修改
											continue;// 继续找下个任务
										}
									} else {// 未质检出 : 跳过任务
										continue;// 继续找下个任务
									}
								} else if (check == CheckEnum.err) {
									// 质检出错误：加载错误改错
									// 根据POI查询错误
									curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
									Integer errcount = curErrorList.size();
									if (errcount > 0) {
										// 存在待修改的质检错误
										stask.setTaskMode(task);
										stask.setErrorList(curErrorList);
										stask.setPoiId(curPoiId);
										bFindTask = true;
										break;// 找到作业任务
									} else {
										// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
										// 这是工具bug 或者流程 被人为修改
										continue;// 继续找下个任务
									}
								}
							} else {// 其他作业的点暂时不能处理：跳过任务
								continue;// 继续找下个任务
							}
						} // if (linkpoi == null) {
						
						//-----------------------
					} else {// 当前项目查询不到，需要下一个项目了（但是有未处理跳过的项目）
						task = getNextModifyTaskNotDoneProject(userid,doneProjectList);
						if (task != null && task.getId() != null) {
							curProjectId = task.getProjectid();
							doneProjectList.add(curProjectId);
							//---------------------
							curTaskId = task.getId();
							// 查询质检错误
							// 获取任务关联的POI
							TaskLinkPoiModel linkpoi = taskModelClient.selectTaskLinkPoiByTaskid(curTaskId);
							if (linkpoi == null) {
								// 关联POI不存在，任务设置质检完成 ?
								continue;// 继续找下个任务
							} else {
								curPoiId = linkpoi.getPoiId();
								POIDo poi = new POIDo();
								poi = poiClient.selectPOIByOid(curPoiId);
								if (poi.getSystemId() == 370) {// web编辑作业的点
									CheckEnum check = poi.getAutoCheck();
									if (check == CheckEnum.ok) {
										// 质检OK 设置任务状态 3,6
										if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
											// json.addObject("resultMsg", "任务提交失败");
										}
										continue;// 继续找下个任务
									} else if (check == CheckEnum.uncheck) {// 改错保存 或者 还未质检
										ConfirmEnum confirm = poi.getConfirm();
										if (confirm == ConfirmEnum.confirm_ok) {
											// 中途保存过POI
											curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
											Integer errcount = curErrorList.size();
											if (errcount > 0) {
												// 存在待修改的质检错误
												stask.setTaskMode(task);
												stask.setErrorList(curErrorList);
												stask.setPoiId(curPoiId);
												bFindTask = true;
												break;// 找到作业任务
											} else {
												// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
												// 这是工具bug 或者流程 被人为修改
												continue;// 继续找下个任务
											}
										} else {// 未质检出 : 跳过任务
											continue;// 继续找下个任务
										}
									} else if (check == CheckEnum.err) {
										// 质检出错误：加载错误改错
										// 根据POI查询错误
										curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
										Integer errcount = curErrorList.size();
										if (errcount > 0) {
											// 存在待修改的质检错误
											stask.setTaskMode(task);
											stask.setErrorList(curErrorList);
											stask.setPoiId(curPoiId);
											bFindTask = true;
											break;// 找到作业任务
										} else {
											// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
											// 这是工具bug 或者流程 被人为修改
											continue;// 继续找下个任务
										}
									}
								} else {// 其他作业的点暂时不能处理：跳过任务
									continue;// 继续找下个任务
								}
							} // if (linkpoi == null) {
							
							//-----------------------
						} else {// 项目都循环完了没找到任务
							break;
						}
					} // if( task != null && task.getId() != null)else

				} // if( curProjectid == null)else
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		//-----------
		if( bFindTask == false)
			return null;
		else
			return stask;
	}

	/**
	 * 保存address值，同时看数据库里相应的中英文是否为空，不为空则打isdel =t
	 * 
	 * @param saveTags
	 * @param tags
	 * @param address
	 * @param addressKey
	 * @param addresse
	 * @param addressp
	 * @param oid
	 */
	private void saveAddress(Set<TagDO> saveTags, Set<TagDO> tags, String address, String addressKey, String addresse,
			String addressp, long oid) {
		TagDO addresstag = this.getTag(saveTags, addressKey);
		TagDO tag4e = this.getTag(saveTags, addresse);
		TagDO tag4p = this.getTag(saveTags, addressp);
		if (addresstag == null && address != null) {
			TagDO tag4 = new TagDO();
			tag4.setId(oid);
			tag4.setK(addressKey);
			tag4.setV(address);
			tags.add(tag4);

			if (tag4e != null) {
				tag4e.setV(null);
				tags.add(tag4e);
			}

			if (tag4p != null) {
				tag4p.setV(null);
				tags.add(tag4p);
			}
		} else if (addresstag != null && address == null) {
			addresstag.setV(null);
			tags.add(addresstag);
			if (tag4e != null) {
				tag4e.setV(null);
				tags.add(tag4e);
			}

			if (tag4p != null) {
				tag4p.setV(null);
				tags.add(tag4p);
			}
		} else if (addresstag != null && address != null && !addresstag.getV().equals(address)) {
			addresstag.setV(address);
			tags.add(addresstag);
			if (tag4e != null) {
				tag4e.setV(null);
				tags.add(tag4e);
			}

			if (tag4p != null) {
				tag4p.setV(null);
				tags.add(tag4p);
			}
		}
	}

	/**
	 * 从tags中查找指定key的tag, 并且满足相应的条件
	 * 
	 * @param tags
	 * @param key
	 * @param compare
	 * @return
	 */
	private TagDO getTag(Set<TagDO> tags, String key) {
		if (tags == null || tags.isEmpty() || key == null)
			return null;
		for (TagDO tag : tags) {
			if (key.equals(tag.getK())) {
				// tag.setV(null);
				return tag;
			}
		}
		return null;
	}
	
	/*20190705
	 * 不允许字符串为空字符串 -- null 
	 * */
	private String  checkString(String str) {
	
		if(StringUtils.isNotBlank(str))
			return str;
		return null;
	}
	
	//加载多个点的错误
	private STaskModel getModifyTask2(Integer userid) {
		STaskModel stask = new STaskModel();
		TaskModel task = new TaskModel();
		//--------
		// 当前作业的项目
		Long curProjectId = -1L;
		// 当前查询的任务id
		Long curTaskId = 0L;
		// 当前作业点poiid
		Long curPoiId = -1L;
		// 一轮中已经查询过的项目(可能由于某种原因没作业而跳过，后面需要再次查询是否可作业)
		List<Long> doneProjectList = new ArrayList<Long>();
		List<ErrorModel> curErrorList = new ArrayList<ErrorModel>();
		Boolean bFindTask = false;
		// 查找第一个可作业的项目：存在可作业的任务 + 任务下可有web编辑器作业
		try {
			Integer querycount = 3;//最大查找次数
			// 找到一个可作业的任务
			while (!bFindTask && querycount > 0) {
				querycount--;
				if (curProjectId == -1L) {//首次查询 
					task = getNextModifyTask(userid);
					if (task != null && task.getId() != null) {
						curProjectId = task.getProjectid();
						doneProjectList.add(curProjectId);
						curTaskId = task.getId();
						
						
						// 获取任务关联的POI
						ArrayList<TaskLinkPoiModel> linkpoilist = taskModelClient.selectTaskLinkPoisByTaskid(curTaskId);
					
						//存在未质检的poi  继续等待质检                        1
						//全部质检完，存在错误的，设置任务改错       2
						//全部质检完，全部ok的设置任务完成                4
						//异常：1 poi不存在的 ，设置任务完成           8
						//      2 poi 状态有错，错误表没错，设置任务异常     16
						//      3 POI systemid 已经被修改 且未发布。 设置任务异常  32
						//      4 POI systemid 已经被修改 且发布。     设置任务完成  64 
						//				      5 全部是删除点   63 设置任务完成
						
						String log="";
						RTaskInfo info =PoiCheck(linkpoilist);
						int  poicheck =  info.getRet();
						curErrorList = info.getErrorList();
						
//------------FOR TEST
						String slog;
						slog = "1任务"  + curTaskId.toString()+"错误：";
						for(ErrorModel e:curErrorList ) {
							slog +=	e.getId();
							slog +=",";
						}
						taskModelClient.InsertTaskLog(0L, slog, logger.getName());		
//--------------
						
						log = info.getLog();
						if( poicheck == 1){
							continue;
							
						}else if(poicheck == 2){
							// 存在待修改的质检错误
							stask.setTaskMode(task);
							stask.setErrorList(curErrorList);
							if( curErrorList.size() > 0) {
								ErrorModel em = curErrorList.get(0);
								curPoiId = em.getFeatureid();
								stask.setPoiId(curPoiId);	
							}
							bFindTask = true;
							break;// 找到作业任务
						}else if(poicheck == 4){
							// 质检OK 设置任务状态 3,6
							if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
								// json.addObject("resultMsg", "任务提交失败");
							}
							continue;// 继续找下个任务
						}else if(poicheck == 8 || poicheck == 64 || poicheck == 63 ){
							if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
								// json.addObject("resultMsg", "任务提交失败");
							}
							continue;// 继续找下个任务
						}else if(poicheck == 16){
							taskModelClient.InsertTaskLog(curTaskId, log,logger.getName());
							continue;
						}else if(poicheck == 32){
							
							taskModelClient.InsertTaskLog(curTaskId, log,logger.getName());
							continue;
						}
						
					} // if( task != null && task.getId() != null)else
					else
					{
						String slog;
						slog = "当前项目id为-1,查询" + userid.toString() + "该错误任务为空";
						taskModelClient.InsertTaskLog(0L, slog, logger.getName());
					}

				} // if( curProjectid == -1L)
				else {//  查询当前项目下的任务
					task = getNextModifyTaskByProjectId(userid, curProjectId, curTaskId);// 刷新会调用一次所以必须提交的时候才记录curtaskid
					if (task != null && task.getId() != null) {
						//---------------------
						curTaskId = task.getId();
						// 获取任务关联的POI
						ArrayList<TaskLinkPoiModel> linkpoilist = taskModelClient.selectTaskLinkPoisByTaskid(curTaskId);
					
						//存在未质检的poi  继续等待质检                        1
						//全部质检完，存在错误的，设置任务改错       2
						//全部质检完，全部ok的设置任务完成                4
						//异常：1 poi不存在的 ，设置任务完成           8
						//      2 poi 状态有错，错误表没错，设置任务异常     16
						//      3 POI systemid 已经被修改 。 设置任务异常  32
						String log="";
						RTaskInfo info =PoiCheck(linkpoilist);
						int  poicheck =  info.getRet();
						curErrorList = info.getErrorList();
						
//------------FOR TEST
						String slog;
						slog = "2任务"  + curTaskId.toString()+"错误：";
						for(ErrorModel e:curErrorList ) {
							slog +=	e.getId();
							slog +=",";
						}
						taskModelClient.InsertTaskLog(0L, slog, logger.getName());		
//--------------
						
						log = info.getLog();
						if( poicheck == 1){
							continue;
							
						}else if(poicheck == 2){
							// 存在待修改的质检错误
							stask.setTaskMode(task);
							stask.setErrorList(curErrorList);
							if( curErrorList.size() > 0) {
								ErrorModel em = curErrorList.get(0);
								curPoiId = em.getFeatureid();
								stask.setPoiId(curPoiId);	
							}
							bFindTask = true;
							break;// 找到作业任务
						}else if(poicheck == 4){
							// 质检OK 设置任务状态 3,6
							if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
								// json.addObject("resultMsg", "任务提交失败");
							}
							continue;// 继续找下个任务
						}else if(poicheck == 8 || poicheck == 64 || poicheck == 63 ){
							if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
								// json.addObject("resultMsg", "任务提交失败");
							}
							continue;// 继续找下个任务
						}else if(poicheck == 16){
							taskModelClient.InsertTaskLog(curTaskId, log,logger.getName());
							continue;
						}else if(poicheck == 32){
							taskModelClient.InsertTaskLog(curTaskId, log,logger.getName());
							continue;
						}
						
						//-----------------------
					} else {// 当前项目查询不到，需要下一个项目了（但是有未处理跳过的项目）
						task = getNextModifyTaskNotDoneProject(userid,doneProjectList);
						if (task != null && task.getId() != null) {
							curProjectId = task.getProjectid();
							doneProjectList.add(curProjectId);
							//---------------------
							curTaskId = task.getId();
							// 查询质检错误
							// 获取任务关联的POI
							// 获取任务关联的POI
							ArrayList<TaskLinkPoiModel> linkpoilist = taskModelClient.selectTaskLinkPoisByTaskid(curTaskId);
						
							//存在未质检的poi  继续等待质检                        1
							//全部质检完，存在错误的，设置任务改错       2
							//全部质检完，全部ok的设置任务完成                4
							//异常：1 poi不存在的 ，设置任务完成           8
							//      2 poi 状态有错，错误表没错，设置任务异常     16
							//      3 POI systemid 已经被修改 。 设置任务异常  32
							String log="";
							RTaskInfo info =PoiCheck(linkpoilist);
							int  poicheck =  info.getRet();
							curErrorList = info.getErrorList();
							
	//------------FOR TEST
							String slog;
							slog = "3任务"  + curTaskId.toString()+"错误：";
							for(ErrorModel e:curErrorList ) {
								slog +=	e.getId();
								slog +=",";
							}
							taskModelClient.InsertTaskLog(0L, slog, logger.getName());		
	//--------------
							
							log = info.getLog();
							if( poicheck == 1){
								continue;
								
							}else if(poicheck == 2){
								// 存在待修改的质检错误
								stask.setTaskMode(task);
								stask.setErrorList(curErrorList);
								if( curErrorList.size() > 0) {
									ErrorModel em = curErrorList.get(0);
									curPoiId = em.getFeatureid();
									stask.setPoiId(curPoiId);	
								}
								bFindTask = true;
								break;// 找到作业任务
							}else if(poicheck == 4){
								// 质检OK 设置任务状态 3,6
								if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
									// json.addObject("resultMsg", "任务提交失败");
								}
								continue;// 继续找下个任务
							}else if(poicheck == 8 || poicheck == 64 || poicheck == 63 ){
								if (taskModelClient.submitModifyTask(curTaskId, userid, 3).compareTo(0L) <= 0) {
									// json.addObject("resultMsg", "任务提交失败");
								}
								continue;// 继续找下个任务
							}else if(poicheck == 16){
								taskModelClient.InsertTaskLog(curTaskId, log,logger.getName());
								continue;
							}else if(poicheck == 32){
								taskModelClient.InsertTaskLog(curTaskId, log,logger.getName());
								continue;
							}
							
							//-----------------------
						} else {// 项目都循环完了没找到任务
							break;
						}
					} // if( task != null && task.getId() != null)else

				} // if( curProjectid == null)else
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		//-----------
		if( bFindTask == false)
			return null;
		else
			return stask;
	}
		
	private RTaskInfo PoiCheck(ArrayList<TaskLinkPoiModel> linkpoilist ){
		
		RTaskInfo info  = new RTaskInfo();
		List<ErrorModel> curErrorList = new ArrayList<ErrorModel>();;
		String log;
		
		//存在未质检的poi  继续等待质检                        1
		//全部质检完，存在错误的，设置任务改错       2
		//全部质检完，全部ok的设置任务完成                4
		//异常：1 poi不存在的 ，设置任务完成           8
		//      2 poi 状态有错，错误表没错，设置任务异常     16
		//      3 POI systemid 已经被修改 且未发布。 设置任务异常  32
		//      4 POI systemid 已经被修改 且发布。 设置任务完成  64 
		//      5 全部是删除点   63 设置任务完成
		
		//至少有一个错误
		boolean haveerr  = false;
		//所有点都质检ok 设置任务完成
		boolean isok = true;
		for (TaskLinkPoiModel linkpoi : linkpoilist) {
			Long curPoiId = linkpoi.getPoiId();
			POIDo poi = new POIDo();
			try {
				poi = poiClient.selectPOIByOid(curPoiId);
				Long getid = poi.getId();
				
				//删除点不加载
				if(getid<=0) {
					int re = info.getRet();
					if( re == 0)
						info.setRet(63);
					continue;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (poi.getSystemId() == 370) {// web编辑作业的点
				CheckEnum check = poi.getAutoCheck();
				
				if( check == CheckEnum.uncheck) {
					ConfirmEnum confirm = poi.getConfirm();
					if (confirm == ConfirmEnum.confirm_ok) {
						// 质检结果被修改过，中途保存过POI
						isok = false;
						haveerr = true;
						List<ErrorModel> ErrorList = new ArrayList<ErrorModel>();
						ErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
						Integer errcount = ErrorList.size();
						if (errcount > 0) {
						// 存在待修改的质检错误
							for (ErrorModel errorModel : ErrorList) {
								curErrorList.add(errorModel);
							}
						} 
						else{
								// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
								// 这是工具bug 或者流程 被人为修改
								log = "poi "+ curPoiId +"质检err，错误库没可用错误,导致任务无法改错";
								info.setLog(log);
								info.setRet(16);
								return info;
						}
						
						info.setRet(2);
					//	info.setErrorList(curErrorList);
					//	return info;
					}else if(confirm == ConfirmEnum.ready_for_qc) {
						//存在未质检即可退出
						
						info.setRet(1);
						return info;
					}
				}else if(check == CheckEnum.ok) {
					//质检ok
				}else if( check == CheckEnum.err){
					//质检错误
					isok = false;
					haveerr = true;
					List<ErrorModel> ErrorList = new ArrayList<ErrorModel>();
					ErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
					Integer errcount = ErrorList.size();
					if (errcount > 0) {
					// 存在待修改的质检错误
						for (ErrorModel errorModel : ErrorList) {
							curErrorList.add(errorModel);
						}
					} else 
					{
							// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
							// 这是工具bug 或者流程 被人为修改
							log = "poi "+ curPoiId +"质检err，错误库没可用错误,导致任务无法改错";
							info.setLog(log);
							info.setRet(16);
							return info;
					}
					
					
				}else {
					isok = false;
				}
			}//if (poi.getSystemId() == 370) {// web编辑作业的点
			else {//存在异常即可退出
				OpTypeEnum optype =	poi.getOpTypeEnum();
				if( optype != OpTypeEnum.published) {
					// 已经发布的的不处理
					log="关联poi "+ curPoiId  + " Systemid不为370,导致任务无法改错" ;
					info.setLog(log);
					info.setRet(32);
					return info;
				}else {
					//如果有其他返回值则返回其他返回值，如果没有则返回64
					int re = info.getRet();
					if( re == 0) {
						info.setRet(64);
					}
				}
			}
		}
		if( isok) {
			//全部质检ok
			info.setRet(4);
			return info;
		}
		else {
			if( haveerr) {
				//存在错误
				info.setRet(2);
				info.setErrorList(curErrorList);
			}
			return info;
		}
	}
		
		// 提交任务
		@RequestMapping(params = "atn=submitmodifytask2")
		public ModelAndView submitModifyTask2(Model model, HttpServletRequest request, HttpSession session) {
			logger.debug("START");
			ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
			TaskModel task = new TaskModel();
			STaskModel stask = new STaskModel();
			ProjectModel project = new ProjectModel();
			ProcessModel process = new ProcessModel();
			Long keywordid = -1L;
			
			
			
			try {
				Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
				
				Long projectId = ParamUtils.getLongParameter(request, "projectId", 0);
				
				Boolean getnext = ParamUtils.getBooleanParameter(request, "getnext");
				Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
				String strerrorids = ParamUtils.getParameter(request, "errorids");
				String oids = ParamUtils.getParameter(request, "oid");
				Integer oidcount = oids.split(",").length;
				
				String log;
				log = "提交改错任务:"+taskid.toString();
	            taskModelClient.InsertTaskLog(taskid, log, logger.getName());
				
				for( int indexoid = 0 ;indexoid < oidcount;indexoid++) {
					Long oid = Long.valueOf( oids.split(",")[indexoid] );
					POIDo poi = this.getPOI2(oid);
					poi.setConfirmUId(Long.valueOf(userid));
					poi.setUid(Long.valueOf(userid));
					poi.setProjectid(projectId);
					Long ret = poiClient.updatePOIToDB(poi);
					if( ret <1 ) {
						throw new Exception("poi 提交失败 " + poi.getId() );
					}
				}

				
				List<Long> errorids = new ArrayList<Long>();
				Integer length = strerrorids.split(",").length;
				for (Integer i = 0; i < length; i++) {
					String sid = strerrorids.split(",")[i];
					errorids.add( Long.valueOf(sid) );
				}
				// 先修改错误状态
				if (errorModelDao.updateErrors(errorids).compareTo(0L) <= 0) {
					json.addObject("resultMsg", "修改错误状态为解决失败");
					json.addObject("result", 0);
					return json;
				}
				if (taskModelClient.submitModifyTask(taskid, userid, 2).compareTo(0L) <= 0) {
					if (taskModelClient.submitModifyTask(taskid, userid, 2).compareTo(0L) <= 0) {
						
						if (taskModelClient.submitModifyTask(taskid, userid, 2).compareTo(0L) <= 0) {
						
							log = "设置任务" + taskid + "state=2,userid="+ userid+ " 失败,请手动处理";
							taskModelClient.InsertTaskLog(taskid, log,logger.getName() );
							json.addObject("resultMsg", "任务提交失败");
							json.addObject("result", 0);
							return json;
						}
					}
				}
			
				log = "提交改错任务状态2:"+taskid.toString();
                taskModelClient.InsertTaskLog(taskid, log, logger.getName());

				if (getnext) {
					stask = getModifyTask(userid);
					if (stask != null) {
						task = stask.getTaskModel();
						if (task != null && task.getId() != null) {
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
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			json.addObject("task", task);
			json.addObject("project", project);
			json.addObject("process", process);
			json.addObject("keywordid", keywordid);
			json.addObject("result", 1);

			if( stask != null) {
				model.addAttribute("poiid", stask.getPoiId());
				model.addAttribute("errorlist", stask.getErrorList());
			}else {
				model.addAttribute("poiid", -1L);
				model.addAttribute("errorlist", null);
			}

			logger.debug("END");
			return json;
		}
	
		/**
		 * 根据前台传递过来的参数设置POI
		 * 
		 * @return
		 */
		private POIDo getPOI2(Long oid) throws Exception {
			POIDo savePoi = poiClient.selectPOIByOid(oid);
			savePoi.setSystemId(370);
			savePoi.setConfirm(ConfirmEnum.ready_for_qc);
			savePoi.setAutoCheck(CheckEnum.uncheck);
			savePoi.setManualCheck(CheckEnum.uncheck);
			savePoi.setOpTypeEnum(OpTypeEnum.submit);
			logger.debug(JSON.toJSON(savePoi).toString());
			return savePoi;
		}
}
