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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.poiwebeditor.client.TaskModelClient;
import com.emg.poiwebeditor.common.IsWorkTimeEnum;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.PoiProjectType;
import com.emg.poiwebeditor.common.TaskTypeEnum;
import com.emg.poiwebeditor.dao.process.ConfigValueModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.pojo.ConfigValueModel;
import com.emg.poiwebeditor.pojo.EmployeeModel;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.SpotCheckProjectInfo;
import com.emg.poiwebeditor.pojo.SpotCheckTaskInfo;
import com.emg.poiwebeditor.service.EmapgoAccountService;

@Controller
@RequestMapping("/spotcheckcapacity.web")
public class SpotCheckCapacityCtrl extends BaseCtrl{

	private static final Logger logger = LoggerFactory.getLogger(SpotCheckCapacityCtrl.class);
	
	@Autowired
	private TaskModelClient taskModelClient;
	@Autowired
	private ProjectModelDao projectModelDao;
	@Autowired
	private EmapgoAccountService emapgoAccountService;
	@Autowired
	private ConfigValueModelDao    configValueModelDao;
	
	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("SpotCheckCapacityCtrl-openLader start.");
		
		model.addAttribute("poiprojectTypes",PoiProjectType.toJsonStr() );
		return "spotcheckcapacity";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView getPages(Model model,HttpServletRequest request,HttpSession session) {
		logger.debug("getPages");
		Integer ret =0;
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			int count = 0;
			int totalcount = 0;
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");
			
			List<SpotCheckProjectInfo> tasklist2 = taskModelClient.selectSpotCheckProjectInfo2( limit,offset);
			totalcount = taskModelClient.selectSpotCheckTaskCount2();
			List<SpotCheckProjectInfo> tasklist = new ArrayList<SpotCheckProjectInfo>();
			for(SpotCheckProjectInfo t1 : tasklist2) {
				boolean bhave = false;
				for(SpotCheckProjectInfo t2: tasklist) {
					if( t1.getEditid().equals(t2.getEditid()) && t1.getProjectid().equals( t2.getProjectid())) {
						bhave =true;
						break;
					}
				}
				if(!bhave)
					tasklist.add(t1);
				
			}
			
			if(tasklist != null && tasklist.size() > 0) {
				List<Integer> userIDInRows = new ArrayList<Integer>();
				List<Long>    projectIDInRows = new ArrayList<Long>();
				List<EmployeeModel> userInRows = new ArrayList<EmployeeModel>();
				count = tasklist.size();
				int i = 0;
				for( i = 0 ; i < count ; i++) {
					SpotCheckProjectInfo taskinfo = tasklist.get(i);
					Integer editid = taskinfo.getEditid();
					Long    projectid = taskinfo.getProjectid();
					userIDInRows.add( editid);
					projectIDInRows.add(projectid);
					
				}
				if (userIDInRows != null && !userIDInRows.isEmpty()) {
					userInRows = emapgoAccountService.getEmployeeByIDS(userIDInRows);
				}
				int usercount  = userInRows.size();
				for( i = 0 ; i < count ; i++) {
					SpotCheckProjectInfo taskinfo = tasklist.get(i);
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
				//查询项目信息
				ProjectModelExample example = new ProjectModelExample();
				com.emg.poiwebeditor.pojo.ProjectModelExample.Criteria criteria = example.or();
				List<ProjectModel> _projects = new ArrayList<ProjectModel>();
				criteria.andIdIn(projectIDInRows);
				_projects=projectModelDao.selectByExample(example);
				int projectsize = _projects.size();
				for( i = 0 ; i < count ; i++) {
					SpotCheckProjectInfo taskinfo = tasklist.get(i);
					Long projectid = tasklist.get(i).getProjectid();
					for(int j = 0; j < projectsize; j++) {
						ProjectModel projectmodel =  _projects.get(j);
						Long tid = projectmodel.getId();
						if( tid.equals(projectid) ) {
							String name = projectmodel.getName();
							taskinfo.setProcessname(name);
							taskinfo.setProcessid(projectmodel.getProcessid());
							
							ConfigValueModel configmodel = new  ConfigValueModel();
							configmodel.setConfigId(32);
							configmodel.setProcessId(projectmodel.getProcessid());
							List< ConfigValueModel> configlist =	configValueModelDao.selectConfigs(configmodel);
							boolean bfind = false;
							for( ConfigValueModel cm:configlist) {
								if(null ==	cm.getValue())
									taskinfo.setPoiprojecttype(0);
								else
									taskinfo.setPoiprojecttype( Integer.valueOf(cm.getValue()));
								bfind = true;
							}
							if( !bfind)
								taskinfo.setPoiprojecttype(0);
							
							break;
						}
					}
				}
				
				json.addObject("rows",tasklist);
				json.addObject("total", totalcount);	
				ret = 1;
			}else {
				json.addObject("rows",null);
				json.addObject("total", 0);	
				ret = 0;
			}

		}catch(Exception e) {
			
		}
		json.addObject("result", ret);
		return json;
		
	}
	
	
}
