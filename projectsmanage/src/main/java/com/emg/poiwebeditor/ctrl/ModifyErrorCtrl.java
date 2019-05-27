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
import com.emg.poiwebeditor.pojo.ReferdataModel;
import com.emg.poiwebeditor.pojo.TagDO;
import com.emg.poiwebeditor.pojo.TaskLinkPoiModel;
import com.emg.poiwebeditor.pojo.TaskModel;

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

	// 一轮中已经查询过的项目(可能由于某种原因没作业而跳过，后面需要再次查询是否可作业)
	private List<Long> doneProjectList = new ArrayList<Long>();
	// 一个项目中已经查询过的任务(可能由于某种原因没作业而跳过，后面需要再次查询是否可作业)
	private List<Long> doneTaskidList;
	// 当前作业的项目
	private Long curProjectId;
	// 当前查询的任务id
	private Long curTaskId = 0L;
	// 当前作业点poiid
	private Long curPoiId;
	private Long curKeyWordId;
	List<ErrorModel> curErrorList = new ArrayList<ErrorModel>();

	// byhxz20190522
	@RequestMapping(method = RequestMethod.GET)
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("OPENLADER");
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		List< ErrorModel> errorlist = new ArrayList<ErrorModel>();
		Long keywordid = -1L;
	
		//查找第一个可作业的项目：存在可作业的任务 + 任务下可有web编辑器作业
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
			Boolean bFindTask = false;
			//找到一个可作业的任务
			while (!bFindTask) {
				if (curProjectId == null) {
					task = getNextModifyTask(userid);
					if( task != null && task.getId() != null) {
						curProjectId= task.getProjectid();
						doneProjectList.add(curProjectId);
						Boolean isAvaliable  = isTaskAvaliable(task);
						if(isAvaliable ) {
							break;
						}else{
							continue;
						}
					}else {//第一次查询就没找到可作业任务
						break;
					}//if( task != null && task.getId() != null)else
					
				} // if( curProjectid == null)
				else {//查询当前项目下的任务
					task = getNextModifyTaskByProjectId(userid,curProjectId,curTaskId);//刷新会调用次所以必须提交的时候才记录curtaskid
					if( task != null && task.getId() != null) {
						Boolean isAvaliable  = isTaskAvaliable(task);
						if(isAvaliable ) {
							break;
						}else{
							continue;
						}
					}else {//当前项目查询不到需要下一个项目了（但是有未处理跳过的项目）
						task = getNextModifyTaskNotDoneProject(userid);
						if( task != null && task.getId() != null) {
							Boolean isAvaliable  = isTaskAvaliable(task);
							if(isAvaliable ) {
								break;
							}else{
								continue;
							}
						}else {//项目都循环完了没找到任务
							break;
						}
					}//if( task != null && task.getId() != null)else

				} // if( curProjectid == null)else
			}
			
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
				
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		model.addAttribute("task", task);
		model.addAttribute("project", project);
		model.addAttribute("process", process);
		model.addAttribute("keywordid", keywordid);
		model.addAttribute("poiid",curPoiId);
		model.addAttribute("errorlist",curErrorList);
		
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
	private TaskLinkPoiModel getLinkPoi(Long taskid) {
		TaskLinkPoiModel linkpoi = new TaskLinkPoiModel();
		try {
			linkpoi = taskModelClient.selectTaskLinkPoiByTaskid(taskid);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
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

		// 提交任务
		@RequestMapping(params = "atn=submitmodifytask")
		public ModelAndView submitModifyTask(Model model, HttpServletRequest request, HttpSession session) {
			logger.debug("START");
			ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
			TaskModel task = new TaskModel();
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
				curTaskId = taskid;
				poiClient.updatePOIToDB(poi);
				List<Long> errorids = new ArrayList<Long>();
				Integer errorcount = curErrorList.size();
				for(Integer i = 0 ;i < errorcount ; i++) {
					errorids.add( curErrorList.get(i).getId() );
				}
				//先修改错误状态
				if(errorModelDao.updateErrors(errorids).compareTo(0L) <= 0  ) {
					json.addObject("resultMsg", "修改错误状态为解决失败");
					json.addObject("result", 0);
					return json;
				}
				if (taskModelClient.submitModifyTask(taskid, userid, 2).compareTo(0L) <= 0) {
					json.addObject("resultMsg", "任务提交失败");
					json.addObject("result", 0);
					return json;
				}

				if (getnext) {
					task = getModifyTask(userid);
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
			json.addObject("task", task);
			json.addObject("project", project);
			json.addObject("process", process);
			json.addObject("keywordid", keywordid);
			json.addObject("result", 1);

			model.addAttribute("poiid", curPoiId);
			model.addAttribute("errorlist", curErrorList);

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
			curTaskId = taskid;
			poiClient.updatePOIToDB(poi);


		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}	
		json.addObject("result", 1);
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
	private TaskModel getNextModifyTaskNotDoneProject(Integer userid) {
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

	// 任务是否可作业
	private Boolean isTaskAvaliable(TaskModel task) {
		try {
			Integer userid = task.getEditid();
			Long taskid = task.getId();
			// 查询质检错误
			// 获取任务关联的POI
			TaskLinkPoiModel linkpoi = taskModelClient.selectTaskLinkPoiByTaskid(task.getId());
			if (linkpoi == null) {
				// 关联POI不存在，任务设置质检完成 ?
				return false;// 继续找下个任务
			} else {
				curPoiId = linkpoi.getPoiId();
				POIDo poi = new POIDo();
				poi = poiClient.selectPOIByOid(curPoiId);
				if (poi.getSystemId() == 370) {// web编辑作业的点
					CheckEnum check = poi.getAutoCheck();
					if (check == CheckEnum.ok) {
						// 质检OK 设置任务状态 3,6
						if (taskModelClient.submitModifyTask(taskid, userid, 3).compareTo(0L) <= 0) {
							// json.addObject("resultMsg", "任务提交失败");
						}
						curTaskId = taskid;

						return false;// 继续找下个任务
					} else if (check == CheckEnum.uncheck) {
						// 未质检出 : 跳过任务
						curTaskId = taskid;
						ConfirmEnum confirm =  poi.getConfirm();
						if( confirm == ConfirmEnum.no_confirm) {
							//中途保存过POI
							curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
							Integer errcount = curErrorList.size();
							if (errcount > 0) {
								// 存在待修改的质检错误
								return true;// 找到作业任务
							} else {
								// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
								// 这是工具bug 或者流程 被人为修改
								return false;
							}
						}else {
							return false;// 继续找下个任务
						}
					} else if (check == CheckEnum.err) {
						// 质检出错误：加载错误改错
						// 根据POI查询错误
						curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
						Integer errcount = curErrorList.size();
						if (errcount > 0) {
							// 存在待修改的质检错误

							return true;// 找到作业任务
						} else {
							// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
							// 这是工具bug 或者流程 被人为修改
							return false;
						}
					}
				} else {// 其他作业的点暂时不能处理：跳过任务
					return false;
				}

			}
		} // if( task!=null && task.getId() != null)
		catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 根据前台传递过来的参数设置POI
	 * 
	 * @return
	 */
	private POIDo getPOI(HttpServletRequest request) throws Exception {
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
//		String geo = ParamUtils.getParameter(request, "dianpingGeo");
		String geo = ParamUtils.getParameter(request, "poigeo");

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
				}else if ("address8".equals(tag.getK())) {
					if (address8 != null && !address8.equals(tag.getV())) {
						tag.setV(null);
						tags.add(tag);
						TagDO tag8e = new TagDO();
						tag8e.setId(oid);
						tag8e.setK(POIAttrnameEnum.address8e.toString());
						tag8e.setV(null);
						tags.add(tag8e);
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
	 * 根据前台传递过来的参数设置POI
	 * 只保存POI
	 * @return
	 */
	private POIDo getSavePOI(HttpServletRequest request) throws Exception {
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
//		String geo = ParamUtils.getParameter(request, "dianpingGeo");
		String geo = ParamUtils.getParameter(request, "poigeo");

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
		poi.setGeo(geo);
		poi.setFeatcode(featcode);
		poi.setSortcode(sortcode);
		poi.setConfirm(ConfirmEnum.no_confirm);
//		poi.setAutoCheck(CheckEnum.uncheck); err 不应被修改，否则再次加载怎么验证
//		poi.setManualCheck(CheckEnum.uncheck);
//		poi.setOpTypeEnum(OpTypeEnum.submit);
		
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
				}else if ("address8".equals(tag.getK())) {
					if (address8 != null && !address8.equals(tag.getV())) {
						tag.setV(null);
						tags.add(tag);
						TagDO tag8e = new TagDO();
						tag8e.setId(oid);
						tag8e.setK(POIAttrnameEnum.address8e.toString());
						tag8e.setV(null);
						tags.add(tag8e);
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

	private List<PoiMergeDO> getRelation(HttpServletRequest request, POIDo poi) throws Exception {
		Long taskid = ParamUtils.getLongParameter(request, "taskid", -1);
		if (poi == null || poi.getId() < 0)
			return null;

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
		if (srcType != null) {
			// 如果srctype=null则说明该资料不是来自于点评，需要保存的关系的，emg-baidu,emg-gaode, emg-tengxun
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

	private TaskModel getModifyTask(Integer userid) {
		TaskModel task = new TaskModel();
		try {
			Boolean bFindTask = false;
			// 找到一个可作业的任务
			while (!bFindTask) {
				if (curProjectId == null) {
					task = getNextModifyTask(userid);
					if (task != null && task.getId() != null) {
						curProjectId = task.getProjectid();
						doneProjectList.add(curProjectId);
						Boolean isAvaliable = isTaskAvaliable(task);
						if (isAvaliable) {
							break;
						} else {
							continue;
						}
					} else {// 第一次查询就没找到可作业任务
						break;
					} // if( task != null && task.getId() != null)else

				} // if( curProjectid == null)
				else {// 查询当前项目下的任务
					task = getNextModifyTaskByProjectId(userid, curProjectId, curTaskId);// 刷新会调用次所以必须提交的时候才记录curtaskid
					if (task != null && task.getId() != null) {
						Boolean isAvaliable = isTaskAvaliable(task);
						break;
						
					} else {// 当前项目查询不到需要下一个项目了（但是有未处理跳过的项目）
						task = getNextModifyTaskNotDoneProject(userid);
						if (task != null && task.getId() != null) {
							Boolean isAvaliable = isTaskAvaliable(task);
							break;
						} else {// 项目都循环完了没找到任务
							break;
						}
					} // if( task != null && task.getId() != null)else

				} // if( curProjectid == null)else
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return task;
	}
	
}
