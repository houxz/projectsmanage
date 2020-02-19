package com.emg.poiwebeditor.scheduler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.emg.poiwebeditor.cache.ProductTask;
import com.emg.poiwebeditor.client.POIClient;
import com.emg.poiwebeditor.client.TaskClient;
import com.emg.poiwebeditor.client.TaskModelClient;
import com.emg.poiwebeditor.common.CapacityTaskStateEnum;
import com.emg.poiwebeditor.common.CheckEnum;
import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.IsWorkTimeEnum;
import com.emg.poiwebeditor.common.ProcessConfigEnum;
import com.emg.poiwebeditor.common.ProcessState;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.common.ProjectState;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.common.SystemType;
import com.emg.poiwebeditor.common.TaskTypeEnum;
import com.emg.poiwebeditor.common.TypeEnum;
import com.emg.poiwebeditor.dao.attach.AttachCapacityModelDao;
import com.emg.poiwebeditor.dao.attach.AttachCheckCapacityModelDao;
import com.emg.poiwebeditor.dao.process.ConfigDBModelDao;
import com.emg.poiwebeditor.dao.process.ProcessModelDao;
import com.emg.poiwebeditor.dao.process.ProjectsProcessModelDao;
import com.emg.poiwebeditor.dao.process.WorkTasksModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.CapacityModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.CapacityTaskModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ConfirmPoiCapacityModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.FeatureFinishedModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ProjectModelDao;
import com.emg.poiwebeditor.dao.task.ErrorModelDao;
import com.emg.poiwebeditor.dao.task.TaskBlockDetailModelDao;
import com.emg.poiwebeditor.dao.task.TaskLinkErrorModelDao;
import com.emg.poiwebeditor.dao.task.TaskLinkFielddataModelDao;
import com.emg.poiwebeditor.dao.task.TaskModelDao;
import com.emg.poiwebeditor.pojo.AttachCapacityModelExample;
import com.emg.poiwebeditor.pojo.AttachCheckCapacityModel;
import com.emg.poiwebeditor.pojo.AttachMakeCapacityModel;
import com.emg.poiwebeditor.pojo.CapacityModel;
import com.emg.poiwebeditor.pojo.CapacityTaskModel;
import com.emg.poiwebeditor.pojo.CapacityTaskModelExample;
import com.emg.poiwebeditor.pojo.CapacityTaskModelExample.Criteria;
import com.emg.poiwebeditor.pojo.CapacityUniq;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.ConfigValueModel;
import com.emg.poiwebeditor.pojo.ConfirmPoiCapacityModel;
import com.emg.poiwebeditor.pojo.EmployeeModel;
import com.emg.poiwebeditor.pojo.ErrorModel;
import com.emg.poiwebeditor.pojo.FeatureFinishedModel;
import com.emg.poiwebeditor.pojo.POIDo;
import com.emg.poiwebeditor.pojo.ProcessConfigModel;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProjectModel;
import com.emg.poiwebeditor.pojo.ProjectModelExample;
import com.emg.poiwebeditor.pojo.ProjectsProcessModel;
import com.emg.poiwebeditor.pojo.QualityCapacityUniq;
import com.emg.poiwebeditor.pojo.QualityCapcityModel;
import com.emg.poiwebeditor.pojo.TaskLinkPoiModel;
import com.emg.poiwebeditor.pojo.TaskModel;
import com.emg.poiwebeditor.pojo.WorkTasksModel;
import com.emg.poiwebeditor.pojo.WorkTasksUniq;
import com.emg.poiwebeditor.service.EmapgoAccountService;
import com.emg.poiwebeditor.service.ProcessConfigModelService;
import com.emg.poiwebeditor.service.ZMailService;

@Component
public class SchedulerTask {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

	@Value("${scheduler.capacity.enable}")
	private String capacityEnable;
	
	@Value("${schedulerpoi.capacity.enable}")
	private String poicapacityEnable;
	
	@Value("${scheduler.worktasks.adjustmap.enable}")
	private String adjustmapWorktasksEnable;
	@Value("${scheduler.worktasks.area.enable}")
	private String areaWorktasksEnable;
	@Value("${scheduler.worktasks.attach.enable}")
	private String attachWorktasksEnable;
	@Value("${scheduler.worktasks.attachdata.enable}")
	private String attachdataWorktasksEnable;
	@Value("${scheduler.worktasks.country.enable}")
	private String countryWorktasksEnable;
	@Value("${scheduler.worktasks.error.enable}")
	private String errorWorktasksEnable;
	@Value("${scheduler.worktasks.genweb.enable}")
	private String genwebWorktasksEnable;
	@Value("${scheduler.worktasks.nrfc.enable}")
	private String nrfcWorktasksEnable;
	@Value("${scheduler.worktasks.poiedit.enable}")
	private String poieditWorktasksEnable;
	
	@Value("${scheduler.attachcapacity.enable}")
	private String attachEnable;
	
	@Value("${project.projectdbname}")
	private String projectdbname;
	@Value("${project.processdbname}")
	private String processdbname;
	
	
	@Autowired
	private CapacityTaskModelDao capacityTaskModelDao;

	@Autowired
	private CapacityModelDao capacityModelDao;

	@Autowired
	private ProcessConfigModelService processConfigModelService;

	@Autowired
	private ConfigDBModelDao configDBModelDao;

	@Autowired
	private TaskModelDao taskModelDao;

	@Autowired
	private TaskLinkErrorModelDao taskLinkErrorModelDao;
	
	@Autowired
	private TaskLinkFielddataModelDao taskLinkFielddataModelDao;

	@Autowired
	private TaskBlockDetailModelDao taskBlockDetailModelDao;

	@Autowired
	private ProjectModelDao projectModelDao;

	@Autowired
	private ProcessModelDao processModelDao;

	@Autowired
	private EmapgoAccountService emapgoAccountService;
	
	@Autowired
	private WorkTasksModelDao workTasksModelDao;
	
	@Autowired
	private ProjectsProcessModelDao projectsProcessModelDao;
	
	@Autowired
	private AttachCapacityModelDao attachCapacityDao;
	
	@Autowired
	private AttachCheckCapacityModelDao attachCheckCapacityDao;
	
	@Autowired
	private ZMailService zMailService;
	
	//add by lianhr begin 2019/03/08
	@Autowired
	private FeatureFinishedModelDao featureFinishedModelDao;
	//add by lianhr end
	
	@Autowired
	private TaskModelClient taskModelClient;
	
	@Autowired
	private POIClient poiClient;
	@Autowired
	private ErrorModelDao errorModelDao;

	@Autowired
	private ConfirmPoiCapacityModelDao  confirmpoicapacitymodeldao;
	
	@Autowired
	private ProductTask productTask;
	
	@Autowired
	private TaskClient taskClient;
	

	/**
	 * 半夜三更 创建每天的任务
	 */
	@Scheduled(cron = "${schedulerpoi.capacity.createtime}")
	public void capacityCreateTask() {
		if (!poicapacityEnable.equalsIgnoreCase("true"))
			return;

		Date now = new Date();
		String time = new SimpleDateFormat("yyyy-MM-dd").format(now);
		try {
			CapacityTaskModel record = new CapacityTaskModel();
			for (ProcessType processType : ProcessType.values()) {
				if (processType.equals(ProcessType.UNKNOWN))
					continue;
				//现在只需要创建poi确认的任务
				if( !processType.equals(ProcessType.POIPOLYMERIZE))
					continue;
				try {
					record.setProcesstype(processType.getValue());
					record.setStarttime(now);
					record.setState(CapacityTaskStateEnum.NEW.getValue());
					record.setTime(time);
					capacityTaskModelDao.insertSelective(record);

					logger.debug(String.format("Scheduler new task created, processType: %s, time: %s.",
							processType.getValue(), time));
				} catch (DuplicateKeyException e) {
					logger.error(e.getMessage());
				} catch (Exception e) {
					logger.error("Scheduler new task create error.");
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 凌晨执行任务
	 */
//	@Scheduled(cron = "${scheduler.capacity.dotime}")
//	public void capacityDoTask() {
					
	
	/**
	 * 获得今天日期的指定格式字符串
	 * @return
	 */
	private String getDateString(Date now) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowStr = sdf.format(now.getTime());
		return nowStr;
		// return "2019-03-19";
	}
	
	/*
	 * 定时扫描任务库，修改制作完成的任务为 1）可改错 2） 改错完成(质检ok)
	 * */
	// @Scheduled(cron = "${scheduler.modifytask.dotime}")
	public void scanfModifyTask() {
		logger.debug("####scanfModifyTask()##start#####");
		//--for test
				if(1 > 0) {
					return;
				}
		// 1.0 获取所有开启的项目	
		
		ProjectModelExample example = new ProjectModelExample();
		com.emg.poiwebeditor.pojo.ProjectModelExample.Criteria criteria = example.or();
		criteria.andOverstateEqualTo(ProjectState.START.getValue());
		criteria.andSystemidEqualTo(SystemType.poi_polymerize.getValue());
		

		example.setOrderByClause("priority desc, id");
		List<ProjectModel> rows = projectModelDao.selectByExample(example);
		// 2.0 遍历项目id,根据项目id ；变量所有的任务
		//3.0 查看某任务状态
		try {
			Integer projectcount = rows.size();
			logger.debug("本次扫描项目数:" + projectcount);
			for( int indexproject = 0; indexproject < projectcount ; indexproject++) {
				Long projectid = rows.get(indexproject).getId();
				logger.debug("本次扫描项目:" + projectid);
				List<TaskModel> tasklist = taskModelClient.selectTaskByProjectId(projectid);
				Integer taskcount = tasklist.size();
				for( int indextask = 0 ; indextask < taskcount ; indextask++) {
					TaskModel task = tasklist.get(indextask);
					if(  (task.getState() == 2 && task.getProcess() == 5) ||
						 (task.getState() == 2 && task.getProcess() == 6)	) {
							Integer ret = isTaskAvaliable(task);
							if( ret == 1 || ret == 2 || ret ==3 || ret ==4 ) {
								//处理方案待定
							}else {
								//处理方案待定 怎么避免不停的重复查询?
							}
					}
					else {
						logger.debug("任务存在其他状态请查找原因");
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("####scanfModifyTask()##end#####\"");
		
	}
	
	/*修改任务状态
	 *  0：未知
	 *  1: 任务下没有POI，任务自动置为完成
	 *  2: 任务下POI 质检OK，任务自动置为完成
	 *  3： 任务下POI还未质检出结果
	 *  4：任务下POI质检出错误，任务自动设置为待改错 0,6
	 *  5: POI编辑库状态为err,但是错误库没有找到待修改的错误 数据异常
	 *  6：POI被其他系统占用
	 *  7：POI已经被发布过了
	 *  
	 */
	private Integer isTaskAvaliable(TaskModel task) {
		try {
			Integer userid = task.getEditid();
			Long taskid = task.getId();
			if(taskid == 180600) {
				int a = 0;
				a+=1;
			}
			// 查询质检错误
			// 获取任务关联的POI
			ArrayList<TaskLinkPoiModel> poilist = taskModelClient.selectTaskLinkPoisByTaskid(task.getId());
			
			int taskstate = -1;
			int taskprocess=-1;
			// 0000 ：从右到左 
			//第一位表示是否存在 1:不存在   1：存在 0
			//第二位表示表示质检是否ok   ok 1  不错 0
			//第三位表示是否没质检  没质检 1 ，质检 0
			//第4位表示质检是否错误  错误 1 不 ok 0
			//第5位表示是否异常   1 异常  0不异常
			//第6位被其他系统占用，是否要强刷  1 是  0 否
			int flag = 0;
			for (TaskLinkPoiModel linkpoi : poilist) {
				if (linkpoi == null || linkpoi.getId() ==null ) {
					// 任务下没有POI点
					flag |=1;
				}else {
					Long poiid = linkpoi.getPoiId();
					POIDo poi = new POIDo();
					poi = poiClient.selectPOIByOid(poiid);
					if (poi.getSystemId() == 370  ) {// web编辑作业的点
						CheckEnum check = poi.getAutoCheck();
						if (check == CheckEnum.ok) {
							// 质检OK 设置任务状态 3,6
							flag |=2;
						} else if (check == CheckEnum.uncheck) {
							// 未质检出 : 跳过任务
							flag |= 4;
						} else if (check == CheckEnum.err) {
							// 质检出错误：加载错误改错
							// 根据POI查询错误
							List<ErrorModel> curErrorList = new ArrayList<ErrorModel>();
							curErrorList = errorModelDao.selectErrorsbyPoiid(linkpoi.getPoiId());
							Integer errcount = curErrorList.size();
							if (errcount > 0) {
								// 存在待修改的质检错误
								flag |= 8;
							} else {
								// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
								// 这是工具bug 或者流程 被人为修改
								flag |= 16;
							}
						}
					} else {// 其他作业的点暂时不能处理：跳过任务
						//分两种情况：1 被发布了 2 被其他系统占了
						Integer state = task.getState();
						if(state == 2) {//强制刷任务状态
							flag |= 32;
						}
					}
				}
				
			}
			
			//存在异常就要处理
			//存在未质检就要等待
			//存在错误就要改错
			//
	
			if( (flag & 16 ) == 16)   {//  异常 
				// 没找到质检错误：1）质检没写入 2） 查询失败 3）错误被其他途径修改了状态
				// 这是工具bug 或者流程 被人为修改
				if (taskModelClient.updateModifyTask(taskid, userid, 4, 6).compareTo(0L) <= 0) {
					
					logger.debug("修改任务状态失败4,6");
				}
				String log;
				log= "任务关联的错误不存在或者状态已经被修改";
				taskModelClient.InsertTaskLog(taskid, log, logger.getName());
				logger.debug(log);
				return 5;
			}else if(  (flag&4)==4 ) { // 未质检 
				return 3;// 继续找下个任务
			}else if( (flag & 8 ) == 8) {//质检错误
				// 存在待修改的质检错误
				if (taskModelClient.updateModifyTask(taskid, userid, 0, 6).compareTo(0L) <= 0) {
					logger.debug("修改任务状态失败0,6");
				}
				//------------FOR log
				String slog;
				slog = "3任务"  + taskid.toString()+"状态刷为0,6";
			
				taskModelClient.InsertTaskLog(taskid, slog, logger.getName());	
				logger.debug(slog);
				//--------------
				return 4;// 找到作业任务
				
			}else if( (flag & 2 ) == 2) {//质检OK
				// 质检OK 设置任务状态 3,6
				if (taskModelClient.submitModifyTask(taskid, userid, 3).compareTo(0L) <= 0) {
					// json.addObject("resultMsg", "任务提交失败");
				}
				//------------FOR log
				String slog;
				slog = "3任务"  + taskid.toString()+"状态刷为3,x";
			
				taskModelClient.InsertTaskLog(taskid, slog, logger.getName());	
				logger.debug(slog);
				//--------------
				return 2;// 继续找下个任务
			}else if( (flag & 32 ) == 32) {//其他系统占用
				taskModelClient.submitModifyTask(taskid, userid, 3);
				//------------FOR log
				String slog;
				slog = "3任务"  + taskid.toString()+"状态刷为3,x1";
			
				taskModelClient.InsertTaskLog(taskid, slog, logger.getName());	
				logger.debug(slog);
				//--------------
				return 7;
			}else if( (flag & 1 ) == 1) {// 任务下不存在点
				// 关联POI不存在，任务设置质检完成 ?
				if (taskModelClient.submitModifyTask(taskid, userid, 3) <= 0) {
					// 修改状态失败
					logger.debug("修改任务状态失败3,6");
				}
				//------------FOR log
				String slog;
				slog = "3任务"  + taskid.toString()+"状态刷为3,x2";
			
				taskModelClient.InsertTaskLog(taskid, slog, logger.getName());		
				logger.debug(slog);
				//--------------
				return 1;// 继续找下个任务
			}
			
		} // if( task!=null && task.getId() != null)
		catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
		return 0;
	}
	
	/*
	 * 定时扫描任务库，修改制作完成的任务为 1）可改错 2） 改错完成(质检ok)
	 * */
	@Scheduled(cron = "${scheduler.modifytask.dotime}")
	public void updateTaskState() {
		//--for test
				if(1 > 2) {
					return;
				}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     		logger.debug("####scanfModifyTask()##start#####");
		
		// 1.0 获取所有开启的项目	
	
		List<ProjectModel> rows = projectModelDao.selectProjectWithConfig( projectdbname,processdbname,ProjectState.START.getValue(), SystemType.poi_polymerize.getValue());
		// 2.0 遍历项目id,根据项目id ；变量所有的任务
		//3.0 查看某任务状态
		try {
			Integer projectcount = rows.size();
			logger.debug("本次扫描项目数:" + projectcount);
			//用来存储所有免校正的项目ID
			StringBuilder uncheckProject = new StringBuilder();
			StringBuilder projectIds = new StringBuilder();
			for( ProjectModel project : rows) {
				Long projectid = project.getId();
				logger.debug("本次扫描项目:" + projectid);
				projectIds.append(projectid).append(",");
				List<ConfigValueModel> configs = project.getConfigs();
				if (configs != null) {
					for (ConfigValueModel config : configs) {
						// 如果为免校正的项目则给当前项目下的所有poi 打manucheck 为ok的标签
						if(config.getValue() ==null)
							continue;
						if ("免校正".equals(config.getName()) && config.getValue().equals("1")) {
							uncheckProject.append(projectid).append(",");
						}
					}
				}
				
				
			}
			
			
			//处理所有改错项目
			if(projectIds != null && projectIds.length() > 0) {
				List<TaskModel> tasklist = taskModelClient.selectTaskByProjectId(projectIds.substring(0, projectIds.length() - 1));
				Integer taskcount = tasklist.size();
				for( int indextask = 0 ; indextask < taskcount ; indextask++) {
					TaskModel task = tasklist.get(indextask);
					Long taskid = task.getId();
					Long pid = task.getProjectid();
					System.out.println(taskid.toString() +" : "+pid.toString() );
					if(task.getProjectid().equals(928L)) {
						int a = 0;
						a +=1;
					}
					if(  (task.getState() == 2 && task.getProcess() == 5) ||
						 (task.getState() == 2 && task.getProcess() == 6)	||
						 (task.getState() == 2 && task.getProcess() == 7)
							) {
							Integer ret = isTaskAvaliable(task);
							if( ret == 1 || ret == 2 || ret ==3 || ret ==4 ) {
								//处理方案待定
							}else {
								//处理方案待定 怎么避免不停的重复查询?
							}
					}
					else {
						logger.debug("任务存在其他状态请查找原因");
					}
				}
			}
			
			if(1 > 0) {
				return;
			} 
			
			//处理所有免检项目
			if(uncheckProject != null && uncheckProject.length() > 0) {
				logger.debug("免校正的项目ID为：" + uncheckProject);
				poiClient.updateManucheck(uncheckProject.substring(0, uncheckProject.length() - 1));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("####scanfModifyTask()##end#####\"");
		
	}
	
	/**
	 * 凌晨执行任务  统计poi确认产能
	 */
	@Scheduled(cron = "${schedulerpoi.capacity.dotime}")
	public void poiconfirmcapacityDoTask() {
		if (!poicapacityEnable.equalsIgnoreCase("true"))
			return;
		
		logger.debug(String.format("Scheduler POIPOLYMERIZE task started."));
		
		try {
			CapacityTaskModelExample example = new CapacityTaskModelExample();
			Criteria criteria = example.or();
			criteria.andStateEqualTo(CapacityTaskStateEnum.NEW.getValue());
			criteria.andProcesstypeEqualTo(10);//
			example.setOrderByClause("id desc");
			List<CapacityTaskModel> newCapacityTasks = capacityTaskModelDao.selectByExample(example);

			if (newCapacityTasks == null || newCapacityTasks.size() <= 0)
				return;

			List<Long> newCapacityTaskIDs = new ArrayList<Long>();
			for (CapacityTaskModel newCapacityTask : newCapacityTasks) {
				newCapacityTaskIDs.add(newCapacityTask.getId());
			}
			if (newCapacityTaskIDs.size() > 0) {
				CapacityTaskModel record = new CapacityTaskModel();
				record.setState(CapacityTaskStateEnum.DOING.getValue());
				CapacityTaskModelExample _example = new CapacityTaskModelExample();
				_example.or().andIdIn(newCapacityTaskIDs);
				capacityTaskModelDao.updateByExampleSelective(record, _example);
			}

			for (CapacityTaskModel newCapacityTask : newCapacityTasks) {
				try {
					CapacityTaskModel record = new CapacityTaskModel();
					Long curCapacityTaskID = newCapacityTask.getId();
					record.setId(curCapacityTaskID);
					record.setStarttime(new Date());
					capacityTaskModelDao.updateByPrimaryKeySelective(record);

					ProcessType processType = ProcessType.valueOf(newCapacityTask.getProcesstype());
					if (processType.equals(ProcessType.UNKNOWN))
						continue;
					String[][] times = {{"08:30:00","17:30:00"},{"17:30:00","08:30:00"}};
					for(int ii = 0; ii < times.length; ii++) {
						String time = newCapacityTask.getTime();
						if(times[ii][0].toString().equals("08:30:00") && times[ii][1].toString().equals("17:30:00")) {
							if (processType.equals(ProcessType.POIPOLYMERIZE)) {
								logger.debug(String.format("Scheduler POIPOLYMERIZE task( %s ) started.", time));
								ProcessConfigModel config = processConfigModelService
										.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
								if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
									ConfigDBModel configDBModel = configDBModelDao
											.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
									
									Map<CapacityUniq, ConfirmPoiCapacityModel> uniqRecords = new HashMap<CapacityUniq, ConfirmPoiCapacityModel>();
									List<FeatureFinishedModel> featureList = new ArrayList<FeatureFinishedModel>();
									//add by lianhr end
									List<Map<String, Object>> taskGroups = taskModelDao.groupTasksByTime2(configDBModel, times[ii], time);
									for (Map<String, Object> taskGroup : taskGroups) {
										Integer taskType =  (Integer) taskGroup.get("tasktype");
										Long projectid = (Long) taskGroup.get("projectid");
										Integer editid = (Integer) taskGroup.get("editid");
										Long editnum = (Long) taskGroup.get("editnum");
										Integer checkid = (Integer) taskGroup.get("checkid");
										Long checknum = (Long) taskGroup.get("checknum");
										//poi 点状项目制作任务 ，poi 面状项目制作任务 
										if(taskType.equals(17001) || taskType.equals(17003)) {
											CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
											ConfirmPoiCapacityModel editCapacityModel = new ConfirmPoiCapacityModel();
											if(uniqRecords.containsKey(editUniqRecord)) {
												editCapacityModel = uniqRecords.get(editUniqRecord);
												uniqRecords.remove(editUniqRecord);
											}
											
											ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
											editCapacityModel.setProjectid(projectid);
	
											if (project != null) {
												Long processid = project.getProcessid();
												ProcessModel process = processModelDao.selectViewByPrimaryKey(processid);
												if (process != null) {
													editCapacityModel.setProcessid(processid);
													editCapacityModel.setProcessname(process.getName());
													if( null == process.getPoiprojecttype())
														editCapacityModel.setPoiprojecttype(0);
													else
														editCapacityModel.setPoiprojecttype(process.getPoiprojecttype());
													
												}
											}
											
											editCapacityModel.setTasktype(taskType);
										

											editCapacityModel.setUserid(editid);
											
											EmployeeModel erecord = new EmployeeModel();
											erecord.setId(editid);
											EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
											if(emp != null)
												editCapacityModel.setUsername(emp.getRealname());
											
											editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
											editCapacityModel.setTime(time);
										
											editCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
											
											editCapacityModel.setTaskcount(editCapacityModel.getTaskcount() + editnum);
											
											
											editCapacityModel.setFielddatacount( editnum);
											
											uniqRecords.put(editUniqRecord, editCapacityModel);
										
										}else if(taskType.equals(17002) || taskType.equals(17004)) {
											
											CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
											
											ConfirmPoiCapacityModel checkCapacityModel = new ConfirmPoiCapacityModel();
											if(uniqRecords.containsKey(checkUniqRecord)) {
												checkCapacityModel = uniqRecords.get(checkUniqRecord);
												uniqRecords.remove(checkUniqRecord);
											}
											ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
											
											checkCapacityModel.setProjectid(projectid);
											if (project != null) {
												Long processid = project.getProcessid();
												ProcessModel process = processModelDao.selectViewByPrimaryKey(processid);
												if (process != null) {
													
													checkCapacityModel.setProcessid(processid);
													checkCapacityModel.setProcessname(process.getName());
													if( null == process.getPoiprojecttype())
														checkCapacityModel.setPoiprojecttype(0);
													else
														checkCapacityModel.setPoiprojecttype(process.getPoiprojecttype());
												}
											}
											
											
											checkCapacityModel.setTasktype(taskType);

									
											checkCapacityModel.setUserid(checkid);
											EmployeeModel erecord = new EmployeeModel();
											erecord.setId(checkid);
											EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
									
											if(emp != null)
												checkCapacityModel.setUsername(emp.getRealname());
											
											checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
											checkCapacityModel.setTime(time);
											
											checkCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
										
											checkCapacityModel.setTaskcount(checkCapacityModel.getTaskcount() + checknum);
										
											checkCapacityModel.setFielddatacount( checknum);
										
											uniqRecords.put(checkUniqRecord, checkCapacityModel);
										}
										
										
									}//for
									
									List<Map<String, Object>> task15102Groups = taskBlockDetailModelDao.group1ByTime(configDBModel, times[ii], time);
									for (Map<String, Object> taskBlockDetailGroup : task15102Groups) {
										String featureid = (String) taskBlockDetailGroup.get("featureid");
										
										Integer taskType = (Integer) taskBlockDetailGroup.get("tasktype");
										Integer editid = (Integer) taskBlockDetailGroup.get("editid");
										Integer checkid = (Integer) taskBlockDetailGroup.get("checkid");
										Long projectid = (Long) taskBlockDetailGroup.get("projectid");
										//TODO:
										if( taskType.equals(17001) || taskType.equals(17003)) {
											logger.debug("002 : group15102ByTime: editid:" + editid + " featureIds:" + featureid);
											if (projectid.compareTo(0L) <= 0)
												continue;
											
											CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
											
											ConfirmPoiCapacityModel editCapacityModel = new ConfirmPoiCapacityModel();
											if(uniqRecords.containsKey(editUniqRecord)) {
												editCapacityModel = uniqRecords.get(editUniqRecord);
												uniqRecords.remove(editUniqRecord);
											}
											
											
											
											ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
											editCapacityModel.setProjectid(projectid);
										
											if (project != null) {
												Long processid = project.getProcessid();
												ProcessModel process = processModelDao.selectViewByPrimaryKey(processid);
												if (process != null) {
													editCapacityModel.setProcessid(processid);
													editCapacityModel.setProcessname(process.getName());
													if( null == process.getPoiprojecttype())
														editCapacityModel.setPoiprojecttype(0);
													else
														editCapacityModel.setPoiprojecttype(process.getPoiprojecttype());
													
												}
											}
											
											editCapacityModel.setTasktype(taskType);
											editCapacityModel.setUserid(editid);
											EmployeeModel erecord = new EmployeeModel();
											erecord.setId(editid);
											EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
											if(emp != null)
												editCapacityModel.setUsername(emp.getRealname());

											editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
											editCapacityModel.setTime(time);
											editCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
										
											Integer modifypoicount = featureid.split(",").length;
											
											editCapacityModel.setModifypoi(modifypoicount.longValue());
											
											uniqRecords.put(editUniqRecord, editCapacityModel);
										
										}
										else if( taskType.equals(17002) || taskType.equals(17004) ) {
											logger.debug("002 : group15102ByTime: checkid:" + checkid + " featureIds:" + featureid);
											
											if (projectid.compareTo(0L) <= 0)
												continue;
											
											CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
											
											ConfirmPoiCapacityModel checkCapacityModel = new ConfirmPoiCapacityModel();
											if(uniqRecords.containsKey(checkUniqRecord)) {
												checkCapacityModel = uniqRecords.get(checkUniqRecord);
												uniqRecords.remove(checkUniqRecord);
											}
											
											ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
									
											checkCapacityModel.setProjectid(projectid);
											if (project != null) {
												Long processid = project.getProcessid();
												ProcessModel process = processModelDao.selectViewByPrimaryKey(processid);
												if (process != null) {
													
													checkCapacityModel.setProcessid(processid);
													checkCapacityModel.setProcessname(process.getName());
													if( null == process.getPoiprojecttype())
														checkCapacityModel.setPoiprojecttype(0);
													else
														checkCapacityModel.setPoiprojecttype(process.getPoiprojecttype());
												}
											}
											
											EmployeeModel erecord = new EmployeeModel();
											erecord.setId(checkid);
											EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
											if(emp != null)
												checkCapacityModel.setUsername(emp.getRealname());
											
											checkCapacityModel.setTasktype(taskType);
											checkCapacityModel.setUserid(checkid);
											
											checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
											checkCapacityModel.setTime(time);
											checkCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
											
											Integer modifypoicount = featureid.split(",").length;
											
											checkCapacityModel.setModifypoi(modifypoicount.longValue());
										
											uniqRecords.put(checkUniqRecord, checkCapacityModel);
										}

										
										
									}//for()

									if (uniqRecords != null && !uniqRecords.isEmpty()) {
										for ( ConfirmPoiCapacityModel capacityModel : uniqRecords.values()) {
											if ( capacityModel.getFielddatacount().equals(0L))
												continue;
											confirmpoicapacitymodeldao.insert(capacityModel);
										}
									}
							

									logger.debug(
											String.format("Scheduler POIPOLYMERIZE task( %s ) finished.", newCapacityTask.getTime()));

								} else {
									logger.error("Scheduler POIPOLYMERIZE task( %s ) has no configs.");
									record = new CapacityTaskModel();
									record.setId(curCapacityTaskID);
									record.setState(CapacityTaskStateEnum.ERROR.getValue());
									capacityTaskModelDao.updateByPrimaryKeySelective(record);
								}
							} else {
								record = new CapacityTaskModel();
								record.setId(curCapacityTaskID);
								record.setState(CapacityTaskStateEnum.ERROR.getValue());
								capacityTaskModelDao.updateByPrimaryKeySelective(record);
							}
						} else {
							if (processType.equals(ProcessType.POIPOLYMERIZE)) {
								logger.debug(String.format("Scheduler POIPOLYMERIZE task( %s ) started.", time));
								ProcessConfigModel config = processConfigModelService
										.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
								if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
									ConfigDBModel configDBModel = configDBModelDao
											.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
									
									Map<CapacityUniq, ConfirmPoiCapacityModel> uniqRecords = new HashMap<CapacityUniq, ConfirmPoiCapacityModel>();
									
									
									List<FeatureFinishedModel> featureList = new ArrayList<FeatureFinishedModel>();
									List<Map<String, Object>> taskGroups = taskModelDao.groupTasksByTime2(configDBModel, times[ii], time);
									for (Map<String, Object> taskGroup : taskGroups) {
										Integer taskType =  (Integer) taskGroup.get("tasktype");
										Long projectid = (Long) taskGroup.get("projectid");
										Integer editid = (Integer) taskGroup.get("editid");
										Long editnum = (Long) taskGroup.get("editnum");
										Integer checkid = (Integer) taskGroup.get("checkid");
										Long checknum = (Long) taskGroup.get("checknum");
										
										if(taskType.equals(17001) || taskType.equals(17003)) {
											CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
											
											ConfirmPoiCapacityModel editCapacityModel = new ConfirmPoiCapacityModel();
											if(uniqRecords.containsKey(editUniqRecord)) {
												editCapacityModel = uniqRecords.get(editUniqRecord);
												uniqRecords.remove(editUniqRecord);
											}
											
											ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
											editCapacityModel.setProjectid(projectid);
											
											if (project != null) {
												Long processid = project.getProcessid();
												ProcessModel process = processModelDao.selectViewByPrimaryKey(processid);
												if (process != null) {
													editCapacityModel.setProcessid(processid);
													editCapacityModel.setProcessname(process.getName());
													
													if( null == process.getPoiprojecttype())
														editCapacityModel.setPoiprojecttype(0);
													else
														editCapacityModel.setPoiprojecttype(process.getPoiprojecttype());
													
												}
											}
											
											editCapacityModel.setTasktype(taskType);
											

											editCapacityModel.setUserid(editid);
											
											EmployeeModel erecord = new EmployeeModel();
											erecord.setId(editid);
											EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
											if(emp != null)
												editCapacityModel.setUsername(emp.getRealname());
											
											editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
											editCapacityModel.setTime(time);
											
											editCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
									
											editCapacityModel.setTaskcount(editCapacityModel.getTaskcount() + editnum);
									
											editCapacityModel.setFielddatacount( editnum);
											
											uniqRecords.put(editUniqRecord, editCapacityModel);
										
										}else if(taskType.equals(17002) || taskType.equals(17004) ) {
											
											CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
											
											ConfirmPoiCapacityModel checkCapacityModel = new ConfirmPoiCapacityModel();
											if(uniqRecords.containsKey(checkUniqRecord)) {
												checkCapacityModel = uniqRecords.get(checkUniqRecord);
												uniqRecords.remove(checkUniqRecord);
											}
											ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
											
											checkCapacityModel.setProjectid(projectid);
											if (project != null) {
												Long processid = project.getProcessid();
												ProcessModel process = processModelDao.selectViewByPrimaryKey(processid);
												if (process != null) {
													
													checkCapacityModel.setProcessid(processid);
													checkCapacityModel.setProcessname(process.getName());
													
													if( null == process.getPoiprojecttype())
														checkCapacityModel.setPoiprojecttype(0);
													else
														checkCapacityModel.setPoiprojecttype(process.getPoiprojecttype());
												}
											}
										
											checkCapacityModel.setTasktype(taskType);

											checkCapacityModel.setUserid(checkid);
											EmployeeModel erecord = new EmployeeModel();
											erecord.setId(checkid);
											EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
										
											if(emp != null)
												checkCapacityModel.setUsername(emp.getRealname());
											
											checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
											checkCapacityModel.setTime(time);
										
											checkCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
										
											checkCapacityModel.setTaskcount(checkCapacityModel.getTaskcount() + checknum);
											
											checkCapacityModel.setFielddatacount( checknum);
											
											uniqRecords.put(checkUniqRecord, checkCapacityModel);
										}
										
									}//for()
									
									List<Map<String, Object>> task15102Groups = taskBlockDetailModelDao.group1ByTime(configDBModel, times[ii], time);
									for (Map<String, Object> taskBlockDetailGroup : task15102Groups) {
										String featureid = (String) taskBlockDetailGroup.get("featureid");
										Integer taskType = (Integer) taskBlockDetailGroup.get("tasktype");
										Integer editid = (Integer) taskBlockDetailGroup.get("editid");
										Integer checkid = (Integer) taskBlockDetailGroup.get("checkid");
										Long projectid = (Long) taskBlockDetailGroup.get("projectid");
										if (projectid.compareTo(0L) <= 0)
											continue;
										
										if( taskType.equals(17001) || taskType.equals(17003) ) {
											logger.debug("002 : group15102ByTime: editid:" + editid + " featureIds:" + featureid);
											CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
											
											ConfirmPoiCapacityModel editCapacityModel = new ConfirmPoiCapacityModel();
											if(uniqRecords.containsKey(editUniqRecord)) {
												editCapacityModel = uniqRecords.get(editUniqRecord);
												uniqRecords.remove(editUniqRecord);
											}
											
											
											ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
											editCapacityModel.setProjectid(projectid);
											
											if (project != null) {
												Long processid = project.getProcessid();
												ProcessModel process = processModelDao.selectViewByPrimaryKey(processid);
												if (process != null) {
													editCapacityModel.setProcessid(processid);
													editCapacityModel.setProcessname(process.getName());
													
													if( null == process.getPoiprojecttype())
														editCapacityModel.setPoiprojecttype(0);
													else
														editCapacityModel.setPoiprojecttype(process.getPoiprojecttype());
													
												}
											}
											
											editCapacityModel.setTasktype(taskType);
											
											
											editCapacityModel.setUserid(editid);
											EmployeeModel erecord = new EmployeeModel();
											erecord.setId(editid);
											EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
											if(emp != null)
												editCapacityModel.setUsername(emp.getRealname());

											editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
											editCapacityModel.setTime(time);
											editCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
											
											
											Integer modifypoicount = featureid.split(",").length;
											
											editCapacityModel.setModifypoi(modifypoicount.longValue());
											
											uniqRecords.put(editUniqRecord, editCapacityModel);
											
										}
										else if(taskType.equals(17002) || taskType.equals(17004) ) {
											logger.debug("002 : group15102ByTime: checkid:" + editid + " featureIds:" + featureid);
											
											CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
											
											ConfirmPoiCapacityModel checkCapacityModel = new ConfirmPoiCapacityModel();
											if(uniqRecords.containsKey(checkUniqRecord)) {
												checkCapacityModel = uniqRecords.get(checkUniqRecord);
												uniqRecords.remove(checkUniqRecord);
											}
											
											ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
											
											checkCapacityModel.setProjectid(projectid);
											if (project != null) {
												Long processid = project.getProcessid();
												ProcessModel process = processModelDao.selectViewByPrimaryKey(processid);
												if (process != null) {
													
													checkCapacityModel.setProcessid(processid);
													checkCapacityModel.setProcessname(process.getName());
													
													if( null == process.getPoiprojecttype())
														checkCapacityModel.setPoiprojecttype(0);
													else
														checkCapacityModel.setPoiprojecttype(process.getPoiprojecttype());
												}
											}
											
											EmployeeModel erecord = new EmployeeModel();
											erecord.setId(checkid);
											EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
											
											checkCapacityModel.setTasktype(taskType);
											checkCapacityModel.setUserid(checkid);
											erecord.setId(checkid);
											
											if(emp != null)
												checkCapacityModel.setUsername(emp.getRealname());
											checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
											checkCapacityModel.setTime(time);
											checkCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
											
											Integer modifypoicount = featureid.split(",").length;
											
											checkCapacityModel.setModifypoi(modifypoicount.longValue());
											
											uniqRecords.put(checkUniqRecord, checkCapacityModel);
										}
									
										

							
									}//for
									if (uniqRecords != null && !uniqRecords.isEmpty()) {
										for (ConfirmPoiCapacityModel capacityModel : uniqRecords.values()) {
											if ( capacityModel.getFielddatacount().equals(0L))
												continue;
											confirmpoicapacitymodeldao.insert(capacityModel);
								
										}
									}
									

									logger.debug(
											String.format("Scheduler POIPOLYMERIZE task( %s ) finished.", newCapacityTask.getTime()));

									record = new CapacityTaskModel();
									record.setId(curCapacityTaskID);
									record.setState(CapacityTaskStateEnum.FINISHED.getValue());
									capacityTaskModelDao.updateByPrimaryKeySelective(record);
								} else {
									logger.error("Scheduler POIPOLYMERIZE task( %s ) has no configs.");
									record = new CapacityTaskModel();
									record.setId(curCapacityTaskID);
									record.setState(CapacityTaskStateEnum.ERROR.getValue());
									capacityTaskModelDao.updateByPrimaryKeySelective(record);
								}
							} else {
								record = new CapacityTaskModel();
								record.setId(curCapacityTaskID);
								record.setState(CapacityTaskStateEnum.ERROR.getValue());
								capacityTaskModelDao.updateByPrimaryKeySelective(record);
							}
						}
						
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					Long curCapacityTaskID = newCapacityTask.getId();
					CapacityTaskModel record = new CapacityTaskModel();
					record.setId(curCapacityTaskID);
					record.setState(CapacityTaskStateEnum.ERROR.getValue());
					capacityTaskModelDao.updateByPrimaryKeySelective(record);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
//	@Scheduled(cron = "${scheduler.attachcapacity.updateuser.dotime}")
//	public void loadMakeTask() {
//		if (!attachEnable.equalsIgnoreCase("true"))
//			return;
//		logger.debug("start update username.");
//		AttachCapacityModelExample example = new AttachCapacityModelExample();
//		AttachCapacityModelExample.Criteria criteria = example.or();
//		criteria.andCountDate(getDateString(new Date()));
//		List<AttachMakeCapacityModel> makes = attachCapacityDao.selectAttachCapacity(example);
//		List<EmployeeModel> users = emapgoAccountService.getAllEmployees();
//		if (makes != null && users != null) {
//			for(AttachMakeCapacityModel make : makes) {
//				for(EmployeeModel user : users) {
//					if(make.getUserid() == user.getId()) {
//						make.setUsername(user.getRealname());
//					}
//				}
//			}
//			attachCapacityDao.updateUserName(makes);
//		}
//		
//		List<AttachCheckCapacityModel> checks = attachCheckCapacityDao.selectcheckAttachCapacity(example);
//		
//		if (checks != null && users != null) {
//			for(AttachCheckCapacityModel check : checks) {
//				for(EmployeeModel user : users) {
//					if(check.getUserid() == user.getId()) {
//						check.setUsername(user.getRealname());
//					}
//				}
//			}
//			attachCheckCapacityDao.updateUserName(checks);
//		}
//		
//	}
	
	/*
	 * 定时扫描任务库 ,更新进度
	 * */
	@Scheduled(cron = "${schedulerpoi.updateprojectprogress.dotime}")
	public void updateProjectProgress() {
		logger.debug("####scanfModifyTask()##start#####");
		
		//--for test
		if(1 > 0) {
			return;
		}
		
		// 1.0 获取所有开启的项目	
		
		List<ProjectModel> rows = projectModelDao.selectProjectWithConfig( projectdbname,processdbname,ProjectState.START.getValue(), SystemType.poi_polymerize.getValue());
		// 2.0 遍历项目id,根据项目id ；变量所有的任务
		//3.0 查看某任务状态
		try {
			Integer projectcount = rows.size();
			logger.debug("本次扫描项目数:" + projectcount);
			//用来存储所有免校正的项目ID
			StringBuilder uncheckProject = new StringBuilder();
			StringBuilder projectIds = new StringBuilder();
			for( ProjectModel project : rows) {
				Long projectid = project.getId();
				logger.debug("本次扫描项目:" + project.getProcessid() );
				Integer totalcount =	taskModelClient.selectTaskTotalCountByProjectId( projectid.toString());
				if(totalcount.equals(0))
					continue;
				Integer count  = taskModelClient.selectTaskDoneCountByProjectId(projectid.toString());
				ProcessModel pm = new ProcessModel();
				pm.setId( project.getProcessid());
				Integer percent = count *100/ totalcount ;
				if( percent.equals(0) && count >0)
					percent = 1;
				pm.setProgress( String.format("%d,0,0,0", percent) );
				if( percent.equals(100)) {
					pm.setState(3);
					pm.setStagestate(3);
					
					ProjectModel pjm = new ProjectModel();
					pjm.setId(projectid);
					pjm.setOverstate(4);
					projectModelDao.updateByPrimaryKeySelective(pjm);
				}
				processModelDao.updateByPrimaryKeySelective(pm);
			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.debug("####updateProjectProgress()##end#####\"");
		
	}
	
	/*
	 * 定时扫描任务库 ,更新进度， 凌晨的时候把1，5->0,0    5,5->0,0
	 * */
	@Scheduled(cron = "${scheduler.clearcache.dotime}")
	public void clearCache() {
		logger.debug("####clear cache##start#####");
		
		try {
			//删除缓存里面的任务
			productTask.removeUserTask();
			taskClient.initTaskState_clear(-1, TypeEnum.edit_using);
			taskClient.initTaskState_clear(-1, TypeEnum.check_using);
			//删除稍后修改的任务，制作和抽检
			taskClient.initTaskState_clear(-1,  TypeEnum.edit_used);
			taskClient.initTaskState_clear(-1,   TypeEnum.check_used);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		logger.debug("####clearcache##end#####\"");
		
	}

}
