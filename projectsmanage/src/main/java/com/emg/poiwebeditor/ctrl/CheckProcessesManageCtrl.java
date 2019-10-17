package com.emg.poiwebeditor.ctrl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.emg.poiwebeditor.common.PoiProjectType;
import com.emg.poiwebeditor.common.ProcessConfigEnum;
import com.emg.poiwebeditor.common.ProcessConfigModuleEnum;
import com.emg.poiwebeditor.common.ProcessState;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.dao.process.ConfigValueModelDao;
import com.emg.poiwebeditor.dao.process.ProcessConfigValueModelDao;
import com.emg.poiwebeditor.dao.process.ProcessModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.ConfigValueModel;
import com.emg.poiwebeditor.pojo.EmployeeModel;
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.PoiMergeDO;
import com.emg.poiwebeditor.pojo.ProcessConfigModel;
import com.emg.poiwebeditor.pojo.ProcessConfigValueModel;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProcessModelExample;
import com.emg.poiwebeditor.pojo.ProcessModelExample.Criteria;
import com.emg.poiwebeditor.service.EmapgoAccountService;
import com.emg.poiwebeditor.service.ProcessConfigModelService;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsUserModel;
import com.emg.poiwebeditor.pojo.SpotCheckInfo;
import com.emg.poiwebeditor.pojo.SpotCheckProjectInfo;
import com.emg.poiwebeditor.pojo.SpotCheckTaskInfo;
import com.emg.poiwebeditor.pojo.TaskModel;
import com.emg.poiwebeditor.pojo.keywordModelForTask;

import net.sf.json.JSONArray;
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
	@Autowired
	private ProcessConfigModelService processConfigModelService;
	@Autowired
	private ProcessConfigValueModelDao processConfigValueModelDao;
	@Autowired
	private PublicClient publicClient;
	@Autowired
	private ConfigValueModelDao    configValueModelDao;
	
	@RequestMapping(method = RequestMethod.GET)
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("openlader()");
		model.addAttribute("poiprojectTypes",PoiProjectType.toJsonStr() );
		
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
		//	criteria.andStateNotEqualTo(ProcessState.COMPLETE.getValue());
			criteria.andStateEqualTo(ProcessState.COMPLETE.getValue());
			criteria.andNameNotLike("%_抽检_%");
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
					case "poiprojecttype":
						criteria.addPoiProjectType(Integer.valueOf( filterPara.get(key).toString() ) );
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

//			List<ProcessModel> rows = processModelDao.selectByExample(example);
//			int count = processModelDao.countByExample(example);

			List<ProcessModel> rows = processModelDao.selectViewByExample(example);
			int count = processModelDao.countViewByExample(example);
			//设置之前没有没有这个字段时的 值
			for(ProcessModel pm : rows) {
				if( null == pm.getPoiprojecttype() )
					pm.setPoiprojecttype(0);
			}
			
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
	@RequestMapping(params = "atn=pages2")
	public ModelAndView getWorksByProccessid(Model model,HttpServletRequest request,HttpSession session) {
		logger.debug("getworksbyprocessid");
		Integer ret =0;
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");
			
			Long processid = ParamUtils.getLongParameter(request, "processid", -1);
			if( processid > 0) {
				ProjectModel project = new ProjectModel();
				
				ProjectModelExample example = new ProjectModelExample();
				com.emg.poiwebeditor.pojo.ProjectModelExample.Criteria criteria = example.or();
				criteria.andProcessidEqualTo(processid);
				List<ProjectModel> _projects = new ArrayList<ProjectModel>();
				_projects=projectModelDao.selectByExample(example);
				int projectsize = _projects.size();
				int count = 0;
				int totalcount = 0;
				if( projectsize == 1) {
					Long projectid = _projects.get(0).getId();
					List<SpotCheckTaskInfo> tasklist = taskModelClient.selectSpotCheckTaskByProjectId( projectid ,limit,offset);
					totalcount = taskModelClient.selectSpotCheckTaskCountByProjectId(projectid);
					if(tasklist != null && tasklist.size() > 0) {
						List<Integer> userIDInRows = new ArrayList<Integer>();
						List<EmployeeModel> userInRows = new ArrayList<EmployeeModel>();
						count = tasklist.size();
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

					json.addObject("rows",tasklist);
					json.addObject("total", totalcount);
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
							spotinfo.setUsername(username);
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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=createspotchecktask")
	public ModelAndView getSpotCheckProjectInfo2(Model model,HttpServletRequest request,HttpSession session) {
		logger.debug("getworksbyprocessid");
		Integer ret =0;
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Long processid =  -1L;
			int editid =	 -1;
			
			Integer uid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
			String username = (String) session.getAttribute(CommonConstants.SESSION_USER_NAME);
			
			String spotchekcinfos = ParamUtils.getParameter(request, "spotchekcinfos");
			JSONArray jsonarray = JSONArray.fromObject(spotchekcinfos);		
			SpotCheckInfo[] spotlist = (SpotCheckInfo[])JSONArray.toArray(jsonarray,SpotCheckInfo.class );
			Integer length = spotlist.length;
			for( int i = 0 ;i < length ; i++) {
				processid = spotlist[i].getProcessid();
				break;
			}
			
			if( processid > 0 ) {
				
				ConfigValueModel configmodel = new  ConfigValueModel();
				configmodel.setConfigId(32);
				configmodel.setProcessId(processid);
				List< ConfigValueModel> configlist =	configValueModelDao.selectConfigs(configmodel);
				Integer tasktype = 17002;
				for( ConfigValueModel cm :configlist) {
					//32：poi项目类型
					if( cm.getConfigId().equals(32)) {
						//1：poi面状项目 0：poi点状项目
						if( cm.getValue().equals("1"))
							tasktype = 17004;
				       break;
					}
				}
				
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
					for(int j = 0 ;j < length ; j++) {
						editid = spotlist[j].getEditid();
						
						List<TaskModel> tasklist2 = taskModelClient.selectSpotCheckProjectInfo2(projectid, editid );
//						List<Long> keywordids = new ArrayList<Long>();
						List< List<Long>> klist = new ArrayList<List<Long>>();
						int listcount = 500;// 1000;
						List<Long> keywordids = null;
						for(TaskModel tm:tasklist2){
							if(listcount == 500)
								keywordids = new ArrayList<Long>();
							keywordids.add(tm.getKeywordid());
							listcount--;
							if(listcount == 0) {
								listcount = 500;
								klist.add(keywordids);
							}
							//if(keywordids.size() > 1000)break;
						}
						if(listcount > 0) {
							klist.add(keywordids);
						}
//						List<KeywordModel> keylist = publicClient.selectKeywordsByIDs(keywordids);
						List<KeywordModel> keylist = new ArrayList<KeywordModel>();
						for(List<Long> kewordids : klist) {
							List<KeywordModel> k1 = publicClient.selectKeywordsByIDs(kewordids);
							int sz = k1.size();
							for(KeywordModel k : k1)
								keylist.add(k);
						}
						
						List<TaskModel> tasklist = new ArrayList<TaskModel>();
						for(TaskModel tm:tasklist2) {
							for( KeywordModel km :keylist) {
								if(km.getId().equals(tm.getKeywordid())) {
									Integer state = km.getState();
									if( state.equals(0))
										tasklist.add(tm);
									break;
								}
							}
						}
						List<SpotCheckProjectInfo> pinfolist = taskModelClient.selectSpotCheckProjectInfo( projectid,editid );
						int pinfocount = pinfolist.size();
						
						// editid 新建抽检的项目名称
						int index = pinfocount + 1;
						String newprojectname = projectname  +"_抽检_" + spotlist[j].getUsername() +"_"  + index; 
						spotlist[j].setNewprojectname(newprojectname);
						//spotlist[j].setNewprojectid( createProject(newprojectname) );
						createProject(newprojectname,spotlist[j],uid, username);
						
						//抽检理论是创建任务的个数
						Integer newtaskcount = spotlist[j].getEditnum() * spotlist[j].getPercent() / 100;
						//实际可抽检的任务数
						Integer newtaskcount2 =tasklist.size();
						
						Long[] taskids = new Long[newtaskcount2];
						for(int m = 0 ; m < newtaskcount2;m++)
							taskids[m] = tasklist.get(m).getId();
						Long[]spotchecktaskids= getRandomFromArray(taskids , newtaskcount);
						Integer newctcount = 0;
						for( int n = 0 ; n < spotchecktaskids.length;n++) {
							TaskModel task = 	taskModelClient.createspotchecktask(spotchecktaskids[n], spotlist[j].getNewprojectid(),tasktype );
							if( task != null) {
								Long taskid = task.getId();
								KeywordModel keyw = publicClient.selectKeywordsByID(task.getKeywordid());
								Long featureid = 0L;
								if( keyw != null) {
									Integer srctype = keyw.getSrcType();
									String  srcinnerid = keyw.getSrcInnerId();
									List<PoiMergeDO>  relationlist =  publicClient.selectPOIRelation(srcinnerid,srctype);
									int resize = relationlist.size();
									for( PoiMergeDO pm : relationlist) {
										int srctypetmp = pm.getSrcType();
										String srcinneridtmp = pm.getSrcInnerId();
										if(srctype.equals( srctypetmp) && srcinneridtmp.equals( srcinnerid )) {
											featureid = pm.getOid();
											break;
										}
									}											
								}//if( keyw != null) {
								taskModelClient.updatetasklinkpoi(taskid,featureid);
								
								newctcount++;
							}
						}
						//更新项目信息
						ProjectModel pro = new ProjectModel();
						pro.setId(spotlist[j].getNewprojectid());
						pro.setTasknum(newctcount);
						pro.setOverstate(1);
						projectModelDao.updateByPrimaryKeySelective(pro);
						
						//更新状态
						ProcessModel process = new ProcessModel();
						process.setId(spotlist[j].getNewprocessid());
						process.setState(1);
						processModelDao.updateByPrimaryKeySelective(process);
						
						//更新任务信息
						taskModelClient.insertSpotcheckprojectinfo(projectid, editid,spotlist[j].getPercent(),spotlist[j].getNewprojectid());
					}
					
					
					
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
	
	public Long createProject(String newprojectname,SpotCheckInfo pinfo,Integer uid, String username) {
		Long newProcessID = -1L;
		Long newprojectid = -1l;
		Integer type = ProcessType.POIPOLYMERIZE.getValue();
		Integer priority = 0;
		Integer systemid  = SystemType.poi_polymerize.getValue();
	
		
		ProcessModel newProcess = new ProcessModel();
		newProcess.setName(newprojectname);
		newProcess.setType(type);
		newProcess.setPriority(priority);
		newProcess.setState(0);
		newProcess.setUserid(uid);
		newProcess.setUsername(username);
		newProcess.setProgress("0,0,0,0");
		if (processModelDao.insertSelective(newProcess) <= 0) {
		//	json.addObject("resultMsg", "新建项目失败");
			return newprojectid;
		}
		newProcessID = newProcess.getId();
		List<ProcessConfigValueModel> configValues = new ArrayList<ProcessConfigValueModel>();
		
		//************************************
		
		ProjectModel newpro = new ProjectModel();
		newpro.setProcessid(newProcessID);
		newpro.setName(newprojectname);
		newpro.setSystemid(systemid);
		newpro.setProtype(type);
		newpro.setPdifficulty(0);
		newpro.setTasknum(-1);
		newpro.setOverstate(0);
		newpro.setCreateby(uid);
		newpro.setPriority(priority);
		newpro.setOwner(1);//私有
		
		if (projectModelDao.insert(newpro) > 0) {
			newprojectid = newpro.getId();
		}
		if (newprojectid > 0) {
			configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJIXIANGMUID.getValue(), newprojectid.toString()));
			configValues.add(new ProcessConfigValueModel(newProcessID, ProcessConfigModuleEnum.GAICUOPEIZHI.getValue(), ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue(), newprojectname ));
		}
		// TODO: 新增项目类型需要指定其他配置项
		List<ProcessConfigModel> processConfigs = processConfigModelService.selectAllProcessConfigModels(type);
		for (ProcessConfigModel processConfig : processConfigs) {
			Integer moduleid = processConfig.getModuleid();
			Integer configid = processConfig.getId();
			String defaultValue = processConfig.getDefaultValue() == null ? new String() : processConfig.getDefaultValue().toString();

			// 这是前边代码特殊处理的部分配置
			if ((moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANXIANGMUID.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANXIANGMUMINGCHENG.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIXIANGMUID.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJIXIANGMUMINGCHENG.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BANGDINGZILIAO.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.ZHIJIANPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.ZHIJIANMOSHI.getValue())) ||
					(moduleid.equals(ProcessConfigModuleEnum.GAICUOPEIZHI.getValue()) && configid.equals(ProcessConfigEnum.BIANJILEIXING.getValue())))
					continue;

			if(configid == 19) {
				//默认1私有
				defaultValue = "1";
			}else if(configid == 23) {
				//默认面校正 1
				defaultValue = "1";
			}
			configValues.add(new ProcessConfigValueModel(newProcessID, moduleid, configid, defaultValue));
		}
		Integer ret = -1;
		if (processConfigValueModelDao.deleteByProcessID(newProcessID) >= 0) {
			ret = processConfigValueModelDao.insert(configValues);
		}
		pinfo.setNewprojectid(newprojectid);
		pinfo.setNewprocessid(newProcessID);
		return newprojectid;
	}
	
	public Long[] getRandomFromArray(Long[] array , Integer count) {
		Long[] a = array;
		Long[] result = new Long[count];
		boolean r[] = new boolean[array.length];
		Random random = new Random();
		int m = count;
		if( m> a.length || m < 0)
			return a;
		
		int n =0 ;
		while(true) {
			int temp = random.nextInt(a.length);
			if( !r[temp]) {
				n++;
				result[n-1] = a[temp];
				r[temp] = true;
				if( n == m)
					break;
			}
		}
		
		return result;
	}


	
	
}
