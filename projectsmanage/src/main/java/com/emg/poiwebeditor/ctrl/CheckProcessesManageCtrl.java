package com.emg.poiwebeditor.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.emg.poiwebeditor.client.TaskModelClient;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.ProcessState;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.dao.process.ProcessModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.pojo.EmployeeModel;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProcessModelExample;
import com.emg.poiwebeditor.pojo.ProcessModelExample.Criteria;
import com.emg.poiwebeditor.service.EmapgoAccountService;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.SpotCheckProjectInfo;
import com.emg.poiwebeditor.pojo.SpotCheckTaskInfo;
import com.emg.poiwebeditor.pojo.TaskModel;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/checkprocessesmanage.web")
public class CheckProcessesManageCtrl extends BaseCtrl {
	private static final Logger logger = LoggerFactory.getLogger(DoneProcessesManageCtrl.class);
	
	@Autowired
	private ProcessModelDao processModelDao;
	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private TaskModelClient taskModelClient;
	@Autowired
	private EmapgoAccountService emapgoAccountService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("openlader()");
		return "checkprocessmanage";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("checkProcessesManageCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ProcessModelExample example = new ProcessModelExample();
			Criteria criteria = example.or();
			criteria.andTypeEqualTo(ProcessType.POIPOLYMERIZE.getValue());
			criteria.andStateNotEqualTo(ProcessState.COMPLETE.getValue());
		//	criteria.andStateEqualTo(ProcessState.COMPLETE.getValue());
			//hxz 根据id , 项目名称，用户，优先级，项目状态 过滤项目时触发以下代码
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
					case "priority":
						criteria.andPriorityEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "username":
						criteria.andUsernameLike("%" + filterPara.get(key).toString() + "%");
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
			example.setOrderByClause("priority desc, id");

			List<ProcessModel> rows = processModelDao.selectByExample(example);
			int count = processModelDao.countByExample(example);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("checkProcessesManageCtrl-pages end.");
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=geteditworkbyprocessid")
	public ModelAndView getWorksByProccessid(Model model,HttpServletRequest request,HttpSession session) {
		logger.debug("getworksbyprocessid");
		Integer ret =0;
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Long processid = ParamUtils.getLongParameter(request, "proccessid", -1);
			if( processid > 0) {
				ProjectModel project = new ProjectModel();
				
				ProjectModelExample example = new ProjectModelExample();
				com.emg.poiwebeditor.pojo.ProjectModelExample.Criteria criteria = example.or();
				criteria.andProcessidEqualTo(processid);
				List<ProjectModel> _projects = new ArrayList<ProjectModel>();
				_projects=projectModelDao.selectByExample(example);
				int projectsize = _projects.size();
				if( projectsize == 1) {
					Long projectid = _projects.get(0).getId();
					List<SpotCheckTaskInfo> tasklist = taskModelClient.selectSpotCheckTaskByProjectId( projectid );
					
					if(tasklist != null && tasklist.size() > 0) {
						List<Integer> userIDInRows = new ArrayList<Integer>();
						List<EmployeeModel> userInRows = new ArrayList<EmployeeModel>();
						int count = tasklist.size();
						int i = 0;
						for( i = 0 ; i < count ; i++) {
							Integer editid = tasklist.get(i).getEditid();
							userIDInRows.add( editid);
						}
						if (userIDInRows != null && !userIDInRows.isEmpty()) {
							userInRows = emapgoAccountService.getEmployeeByIDS(userIDInRows);
						}
						int usercount  = userInRows.size();
						for( i = 0 ; i < count ; i++) {
							SpotCheckTaskInfo taskinfo = tasklist.get(i);
							taskinfo.setProcessid(processid);
							Integer editid = tasklist.get(i).getEditid();
							for(int j = 0; j < usercount; j++) {
								EmployeeModel employee = userInRows.get(j);
								Integer id = employee.getId();
								if( id.equals(editid) ) {
									String name = employee.getRealname();
									taskinfo.setUsername(name);
									break;
								}
							}
						}
						
					}
					
					
					
					json.addObject("spotchecktaskinfo",tasklist);
					json.addObject("processid", processid);
					ret = 1;
					
				}else {
					logger.debug("select project by processid" + processid.toString() + " get project count:"+projectsize + " error");
					
				}
				
			}
		}catch(Exception e) {
			
		}
		json.addObject("result", ret);
		return json;
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=getdatasets")
	public ModelAndView getSpotCheckProjectInfo(Model model,HttpServletRequest request,HttpSession session) {
		logger.debug("getworksbyprocessid");
		Integer ret =0;
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Long processid = ParamUtils.getLongParameter(request, "processid", -1);
			int editid =	ParamUtils.getIntParameter(request, "editid", -1);
			String username = ParamUtils.getParameter(request, "username");
			if( processid > 0 && editid > 0) {
				ProjectModel project = new ProjectModel();
				
				ProjectModelExample example = new ProjectModelExample();
				com.emg.poiwebeditor.pojo.ProjectModelExample.Criteria criteria = example.or();
				criteria.andProcessidEqualTo(processid);
				List<ProjectModel> _projects = new ArrayList<ProjectModel>();
				_projects=projectModelDao.selectByExample(example);
				int projectsize = _projects.size();
				if( projectsize == 1) {
					Long projectid = _projects.get(0).getId();
					String projectname = _projects.get(0).getName();
					
					List<SpotCheckProjectInfo> pinfolist = taskModelClient.selectSpotCheckProjectInfo( projectid,editid );					
					int pinfocount = pinfolist.size();
					if( pinfocount > 0) {
						ProjectModelExample example2 = new ProjectModelExample();
						com.emg.poiwebeditor.pojo.ProjectModelExample.Criteria criteria2 = example2.or();
						List<ProjectModel> _projects2 = new ArrayList<ProjectModel>();
						List<Long> idlist = new ArrayList<Long>();
						for(int i =  0 ;i < pinfocount ; i++) {
							Long newpid =pinfolist.get(i).getNewprojectid();
							idlist.add(newpid);
							
						}
						criteria2.andIdIn(idlist);
						_projects2 =projectModelDao.selectByExample(example2);
						int cnt = _projects2.size();
						for( int i = 0 ;i < pinfocount ;i++) {
							SpotCheckProjectInfo spotinfo =	pinfolist.get(i);
							spotinfo.setProcessid(processid);
							spotinfo.setUsernmae(username);
							for(int j = 0 ;j < cnt;j++) {
								Long tmpid = _projects2.get(j).getId();
								if( tmpid.compareTo( spotinfo.getNewprojectid() ) ==0 ) {
									spotinfo.setNewprocessid( _projects2.get(j).getProcessid());
									break;
								}
							}
						}
					}
					
					json.addObject("rows",pinfolist);
					ret = 1;
					
				}else {
					logger.debug("select project by processid" + processid.toString() + " get project count:"+projectsize + " error");
					
				}
				
			}
		}catch(Exception e) {
			
		}
		json.addObject("result", ret);
		return json;
		
	}
}
