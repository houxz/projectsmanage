package com.emg.poiwebeditor.ctrl;

import java.util.ArrayList;
import java.util.List;
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

import com.emg.poiwebeditor.client.PublicClient;
import com.emg.poiwebeditor.client.TaskModelClient;
import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.dao.process.ProcessModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectsUserModelDao;
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsUserModel;
import com.emg.poiwebeditor.pojo.ReferdataModel;
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

	@RequestMapping(method = RequestMethod.GET)
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("OPENLADER");
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
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
			
			TaskModel task = new TaskModel();
			ProjectModel project = new ProjectModel();
			ProcessModel process = new ProcessModel();
			Long keywordid = -1L;
			
			if (myProjects != null && !myProjects.isEmpty()) {
				List<Long> _myProjectIDs = new ArrayList<Long>();
				for (ProjectModel myProject : myProjects) {
					_myProjectIDs.add(myProject.getId());
				}
				
				task = taskModelClient.selectMyNextEditTask(_myProjectIDs, userid);
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
			model.addAttribute("task", task);
			model.addAttribute("project", project);
			model.addAttribute("process", process);
			model.addAttribute("keywordid", keywordid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("OPENLADER OVER");
		return "edit";
	}
	
	@RequestMapping(params = "atn=getnextedittask")
	public ModelAndView getNextEditTask(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		TaskModel task = new TaskModel();
		ProjectModel project = new ProjectModel();
		ProcessModel process = new ProcessModel();
		Long keywordid = -1L;
		try {
			Integer userid = ParamUtils.getIntAttribute(session, CommonConstants.SESSION_USER_ID, -1);
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
		List<ReferdataModel> referdatas = new ArrayList<ReferdataModel>();
		try {
			Long oid = ParamUtils.getLongParameter(request, "oid", -1);
			referdatas = publicClient.selectPOIByOid(oid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			referdatas = new ArrayList<ReferdataModel>();
		}
		json.addObject("poi", referdatas);
		json.addObject("count", 1);
		json.addObject("result", 1);

		logger.debug("END");
		return json;
	}
}
