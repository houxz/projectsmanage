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
//		if (!capacityEnable.equalsIgnoreCase("true"))
//			return;
//	
//
//		try {
//			CapacityTaskModelExample example = new CapacityTaskModelExample();
//			Criteria criteria = example.or();
//			criteria.andStateEqualTo(CapacityTaskStateEnum.NEW.getValue());
//			example.setOrderByClause("id desc");
//			List<CapacityTaskModel> newCapacityTasks = capacityTaskModelDao.selectByExample(example);
//
//			if (newCapacityTasks == null || newCapacityTasks.size() <= 0)
//				return;
//
//			List<Long> newCapacityTaskIDs = new ArrayList<Long>();
//			for (CapacityTaskModel newCapacityTask : newCapacityTasks) {
//				newCapacityTaskIDs.add(newCapacityTask.getId());
//			}
//			if (newCapacityTaskIDs.size() > 0) {
//				CapacityTaskModel record = new CapacityTaskModel();
//				record.setState(CapacityTaskStateEnum.DOING.getValue());
//				CapacityTaskModelExample _example = new CapacityTaskModelExample();
//				_example.or().andIdIn(newCapacityTaskIDs);
//				capacityTaskModelDao.updateByExampleSelective(record, _example);
//			}
//
//			for (CapacityTaskModel newCapacityTask : newCapacityTasks) {
//				try {
//					CapacityTaskModel record = new CapacityTaskModel();
//					Long curCapacityTaskID = newCapacityTask.getId();
//					record.setId(curCapacityTaskID);
//					record.setStarttime(new Date());
//					capacityTaskModelDao.updateByPrimaryKeySelective(record);
//
//					ProcessType processType = ProcessType.valueOf(newCapacityTask.getProcesstype());
//					if (processType.equals(ProcessType.UNKNOWN))
//						continue;
//					String[][] times = {{"08:30:00","17:30:00"},{"17:30:00","08:30:00"}};
//					for(int ii = 0; ii < times.length; ii++) {
//						String time = newCapacityTask.getTime();
//						if(times[ii][0].toString().equals("08:30:00") && times[ii][1].toString().equals("17:30:00")) {
//							if (processType.equals(ProcessType.POIEDIT)) {
//								logger.debug(String.format("Scheduler POIEDIT task( %s ) started.", time));
//								ProcessConfigModel config = processConfigModelService
//										.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//								if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//									ConfigDBModel configDBModel = configDBModelDao
//											.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//									
//									Map<CapacityUniq, CapacityModel> uniqRecords = new HashMap<CapacityUniq, CapacityModel>();
//									Map<QualityCapacityUniq, QualityCapcityModel> uniqSpecialRecords = new HashMap<QualityCapacityUniq, QualityCapcityModel>();
//									//add by lianhr begin 2019/03/08
//									List<FeatureFinishedModel> featureList = new ArrayList<FeatureFinishedModel>();
//									//add by lianhr end
//									List<Map<String, Object>> taskGroups = taskModelDao.groupTasksByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskGroup : taskGroups) {
//										Integer taskType = (Integer) taskGroup.get("tasktype");
//										Long projectid = (Long) taskGroup.get("projectid");
//										Integer editid = (Integer) taskGroup.get("editid");
//										Long editnum = (Long) taskGroup.get("editnum");
//										Integer checkid = (Integer) taskGroup.get("checkid");
//										Long checknum = (Long) taskGroup.get("checknum");
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
//										CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										CapacityModel checkCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(checkUniqRecord)) {
//											checkCapacityModel = uniqRecords.get(checkUniqRecord);
//											uniqRecords.remove(checkUniqRecord);
//										}
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										checkCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//												checkCapacityModel.setProcessid(processid);
//												checkCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//										checkCapacityModel.setTasktype(taskType);
//
//										editCapacityModel.setUserid(editid);
//										checkCapacityModel.setUserid(checkid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(editid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//										erecord.setId(checkid);
//										emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											checkCapacityModel.setUsername(emp.getRealname());
//										editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//										editCapacityModel.setTime(time);
//										checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//										checkCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
//										checkCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
//										editCapacityModel.setTaskcount(editCapacityModel.getTaskcount() + editnum);
//										checkCapacityModel.setTaskcount(checkCapacityModel.getTaskcount() + checknum);
//										
//										uniqRecords.put(editUniqRecord, editCapacityModel);
//										uniqRecords.put(checkUniqRecord, checkCapacityModel);
//									}
//									
//									List<Map<String, Object>> task15102Groups = taskBlockDetailModelDao.group15102ByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskBlockDetailGroup : task15102Groups) {
//										String featureid = (String) taskBlockDetailGroup.get("featureid");
//										
//										Integer taskType = (Integer) taskBlockDetailGroup.get("tasktype");
//										Integer editid = (Integer) taskBlockDetailGroup.get("editid");
//										Long projectid = (Long) taskBlockDetailGroup.get("projectid");
//										//TODO:
//										logger.debug("002 : group15102ByTime: editid:" + editid + " featureIds:" + featureid);
//										if (taskType.compareTo(0) <= 0)
//											continue;
//
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//										editCapacityModel.setUserid(editid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(editid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//										editCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
//										
//										//add by lianhr begin 2019/03/08
//										for(int fi = 0; fi < featureid.split(",").length; fi++) {
//											String strFeatureid = featureid.split(",")[fi];
//											FeatureFinishedModel featureRecord = new FeatureFinishedModel();
//											featureRecord.setTasktype(taskType);
//											featureRecord.setProjectid(projectid);
//											featureRecord.setUserid(editid);
//											featureRecord.setRoleid(RoleType.ROLE_WORKER.getValue());
//											featureRecord.setFeatureid(Long.parseLong(strFeatureid));
//											
//											int featurecount = featureFinishedModelDao.queryCount(featureRecord);
//											if(featurecount <= 0 && !featureList.contains(featureRecord)) {
//												featureList.add(featureRecord);
//											} else if(featurecount <= 0 && featureList.contains(featureRecord)) {
//												continue;
//											} else {
//												featureid = featureid.replaceAll(strFeatureid + ",", "").replaceAll(strFeatureid, "");
//											}
//										}
//										if(featureid.length() > 0 && featureid.substring(featureid.length() - 1).equals(",")) {
//											featureid = featureid.substring(0, featureid.length() - 1);
//											//TODO:
//											logger.debug("003 : group15102ByTime: editid:" + editid + " featureIds:" + featureid);
//										}
//										//add by lianhr end
//										
//										
//										ProcessConfigModel config15102 = processConfigModelService
//												.selectByPrimaryKey(ProcessConfigEnum.BIANJISHUJUKU, processType);
//										if (config15102 != null && config15102.getDefaultValue() != null && !config15102.getDefaultValue().isEmpty()) {
//											ConfigDBModel config15102DBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config15102.getDefaultValue()));
//											List<Map<String, Object>> task15102PoisCreate1 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "众包新增");
//											editCapacityModel.setCreatepoi(editCapacityModel.getCreatepoi() + Integer.parseInt(task15102PoisCreate1.get(0).get("countnum").toString()));
//											List<Map<String, Object>> task15102PoisCreate2 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "车调新增");
//											editCapacityModel.setCreatepoi(editCapacityModel.getCreatepoi() + Integer.parseInt(task15102PoisCreate2.get(0).get("countnum").toString()));
//											List<Map<String, Object>> task15102PoisModified1 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "众包修改");
//											editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + Integer.parseInt(task15102PoisModified1.get(0).get("countnum").toString()));
//											List<Map<String, Object>> task15102PoisModified2 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "众包移位");
//											editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + Integer.parseInt(task15102PoisModified2.get(0).get("countnum").toString()));
//											//modified by lianhr begin 2018/12/28
//											List<Map<String, Object>> task15102PoisModified4 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "众包确认");
//											editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + Integer.parseInt(task15102PoisModified4.get(0).get("countnum").toString()));
//											//modified by lianhr end
//											List<Map<String, Object>> task15102PoisModified3 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "车调修改");
//											editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + Integer.parseInt(task15102PoisModified3.get(0).get("countnum").toString()));
//											
//											List<Map<String, Object>> taskDelete15102Pois1 = taskBlockDetailModelDao.group15102ByPoiDelete(config15102DBModel, featureid, "ver is null");
//											editCapacityModel.setDeletepoi(editCapacityModel.getDeletepoi() + Integer.parseInt(taskDelete15102Pois1.get(0).get("countnum").toString()));
//											List<Map<String, Object>> taskDelete15102Pois2 = taskBlockDetailModelDao.group15102ByPoiDelete(config15102DBModel, featureid, "ver is not null");
//											editCapacityModel.setExistdeletepoi(editCapacityModel.getExistdeletepoi() + Integer.parseInt(taskDelete15102Pois2.get(0).get("countnum").toString()));
//											
//											uniqRecords.put(editUniqRecord, editCapacityModel);
//										}
//										
//									}
//
//									List<Map<String, Object>> taskBlockDetailGroups = taskBlockDetailModelDao.groupTaskBlockDetailsByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskBlockDetailGroup : taskBlockDetailGroups) {
//										Long blockid = (Long) taskBlockDetailGroup.get("blockid");
//										Integer editid = (Integer) taskBlockDetailGroup.get("editid");
//										Long editnum = (Long) taskBlockDetailGroup.get("editnum");
//										Integer checkid = (Integer) taskBlockDetailGroup.get("checkid");
//										Long checknum = (Long) taskBlockDetailGroup.get("checknum");
//										//add by lianhr begin 2019/03/08
//										String featureid = (String) taskBlockDetailGroup.get("featureid");
//										//add by lianhr end
//										
//										TaskModel task = taskModelDao.getTaskByBlockid(configDBModel, blockid);
//										
//										if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
//											logger.error("Can not find task by blockid: " + blockid);
//											continue;
//										}
//										
//										Integer taskType = task.getTasktype();
//										if (taskType.compareTo(0) <= 0)
//											continue;
//										Long projectid = task.getProjectid();
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										if (taskType.equals(TaskTypeEnum.POI_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_GEN.getValue())
//												) {
//											continue;
//										}
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
//										CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										CapacityModel checkCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(checkUniqRecord)) {
//											checkCapacityModel = uniqRecords.get(checkUniqRecord);
//											uniqRecords.remove(checkUniqRecord);
//										}
//										
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										checkCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//												checkCapacityModel.setProcessid(processid);
//												checkCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//										checkCapacityModel.setTasktype(taskType);
//
//										editCapacityModel.setUserid(editid);
//										checkCapacityModel.setUserid(checkid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(editid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//										erecord.setId(checkid);
//										emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											checkCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//										editCapacityModel.setTime(time);
//										checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//										checkCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
//										checkCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
//										//add by lianhr begin 2019/03/08
//										for(int fi = 0; fi < featureid.split(",").length; fi++) {
//											String strFeatureid = featureid.split(",")[fi];
//											FeatureFinishedModel featureRecord = new FeatureFinishedModel();
//											featureRecord.setTasktype(taskType);
//											featureRecord.setProjectid(projectid);
//											if(editid.intValue() != 0) {
//												featureRecord.setUserid(editid);
//												featureRecord.setRoleid(RoleType.ROLE_WORKER.getValue());
//											} else if(checkid.intValue() != 0) {
//												featureRecord.setUserid(checkid);
//												featureRecord.setRoleid(RoleType.ROLE_CHECKER.getValue());
//											}
//											
//											featureRecord.setFeatureid(Long.parseLong(strFeatureid));
//											
//											int featurecount = featureFinishedModelDao.queryCount(featureRecord);
//											if(featurecount <= 0 && !featureList.contains(featureRecord)) {
//												featureList.add(featureRecord);
//											} else if(featurecount <= 0 && featureList.contains(featureRecord)) {
//												continue;
//											} else {
//												if(editid.intValue() != 0) {
//													if(editnum > 0) {
//														editnum = editnum - 1;
//													} else {
//														editnum = new Long(0);
//													}
//												} else if(checkid.intValue() != 0) {
//													if(checknum > 0) {
//														checknum = checknum - 1;
//													} else {
//														checknum = new Long(0);
//													}
//												}
//											}
//										}
//										//add by lianhr end
//										
//										editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + editnum);
//										checkCapacityModel.setModifypoi(checkCapacityModel.getModifypoi() + checknum);
//										
//										uniqRecords.put(editUniqRecord, editCapacityModel);
//										uniqRecords.put(checkUniqRecord, checkCapacityModel);
//									}
//									
//									List<Map<String, Object>> specialTaskLinkErrorGroups = taskLinkErrorModelDao.specialGroupTaskLinkErrorByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskLinkErrorGroup : specialTaskLinkErrorGroups) {
//										Long taskid = (Long) taskLinkErrorGroup.get("taskid");
//										Long errortype = (Long) taskLinkErrorGroup.get("errortype");
//										Long count = (Long) taskLinkErrorGroup.get("count");
//										Long errorcount = (Long) taskLinkErrorGroup.get("errorcount");
//										Long visualerrorcount = (Long) taskLinkErrorGroup.get("visualerrorcount");
//										
//										TaskModel task = taskModelDao.getTaskByID(configDBModel, taskid);
//										if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
//											logger.error("Can not find task by taskid: " + taskid);
//											continue;
//										}
//										
//										Integer userid = 0;
//										Integer roleid = RoleType.UNKNOWN.getValue();
//										Integer taskType = task.getTasktype();
//										if (taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())){
//											userid = task.getEditid();
//											roleid = RoleType.ROLE_WORKER.getValue();
//										}
//										else {
//											continue;
//										}
//										Long projectid = task.getProjectid();
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										QualityCapacityUniq editUniqRecord = new QualityCapacityUniq(taskType, projectid, userid, errortype);
//										QualityCapcityModel editCapacityModel = new QualityCapcityModel();
//										if(uniqSpecialRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqSpecialRecords.get(editUniqRecord);
//											uniqSpecialRecords.remove(editUniqRecord);
//										}
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//												
//										editCapacityModel.setUserid(userid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(userid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(roleid);
//										editCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
//										
//										
//										editCapacityModel.setErrortype(errortype);
//										
//										editCapacityModel.setCount(editCapacityModel.getCount() + count);
//										editCapacityModel.setErrorcount(editCapacityModel.getErrorcount() + errorcount);
//										editCapacityModel.setVisualerrorcount(editCapacityModel.getVisualerrorcount() + visualerrorcount);
//										
//										uniqSpecialRecords.put(editUniqRecord, editCapacityModel);
//									}
//									
//									List<Map<String, Object>> taskLinkErrorGroups = taskLinkErrorModelDao.groupTaskLinkErrorByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskLinkErrorGroup : taskLinkErrorGroups) {
//										Long taskid = (Long) taskLinkErrorGroup.get("taskid");
//										Long errorcount = (Long) taskLinkErrorGroup.get("errorcount");
//										Long visualerrorcount = (Long) taskLinkErrorGroup.get("visualerrorcount");
//										//add by lianhr begin 2019/03/08
//										String featureid = (String) taskLinkErrorGroup.get("errorid");
//										//add by lianhr end
//										
//										TaskModel task = taskModelDao.getTaskByID(configDBModel, taskid);
//										
//										if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
//											logger.error("Can not find task by taskid: " + taskid);
//											continue;
//										}
//										
//										Integer userid = 0;
//										Integer roleid = RoleType.UNKNOWN.getValue();
//
//										Integer taskType = task.getTasktype();
//										if (taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())
//												|| 
//												taskType.equals(TaskTypeEnum.POI_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_KETOU.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_GEN.getValue())) {
//											userid = task.getEditid();
//											roleid = RoleType.ROLE_WORKER.getValue();
//										} else if (taskType.equals(TaskTypeEnum.POI_MC_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_KETOU.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_GEN.getValue())) {
//											userid = task.getCheckid();
//											roleid = RoleType.ROLE_CHECKER.getValue();
//										} else {
//											continue;
//										}
//										Long projectid = task.getProjectid();
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, userid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//
//										editCapacityModel.setUserid(userid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(userid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(roleid);
//										editCapacityModel.setTime(time);
//										
//										editCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
//										
//										//add by lianhr begin 2019/03/08
//										if (!taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) &&
//												!taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) &&
//												!taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())){
//											for(int fi = 0; fi < featureid.split(",").length; fi++) {
//												String strFeatureid = featureid.split(",")[fi];
//												FeatureFinishedModel featureRecord = new FeatureFinishedModel();
//												featureRecord.setTasktype(taskType);
//												featureRecord.setProjectid(projectid);
//												featureRecord.setUserid(userid);
//												featureRecord.setRoleid(roleid);
//												
//												featureRecord.setFeatureid(Long.parseLong(strFeatureid));
//												
//												int featurecount = featureFinishedModelDao.queryCount(featureRecord);
//												if(featurecount <= 0 && !featureList.contains(featureRecord)) {
//													featureList.add(featureRecord);
//												} else if(featurecount <= 0 && featureList.contains(featureRecord)) {
//													continue;
//												} else {
//													if(roleid == RoleType.ROLE_WORKER.getValue()) {
//														if(errorcount > 0) {
//															errorcount = errorcount - 1;
//														} else {
//															errorcount = new Long(0);
//														}
//														
//													} else if(roleid == RoleType.ROLE_CHECKER.getValue()) {
//														if(visualerrorcount > 0) {
//															visualerrorcount = visualerrorcount - 1;
//														} else {
//															visualerrorcount = new Long(0);
//														}
//													}
//												}
//											}
//										}
//										//add by lianhr end
//										
//										editCapacityModel.setErrorcount(editCapacityModel.getErrorcount() + errorcount);
//										editCapacityModel.setVisualerrorcount(editCapacityModel.getVisualerrorcount() + visualerrorcount);
//										
//										uniqRecords.put(editUniqRecord, editCapacityModel);
//									}
//									List<Map<String, Object>> taskLinkFielddataGroups = taskLinkFielddataModelDao.groupTaskLinkFielddataByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskLinkFielddataGroup : taskLinkFielddataGroups) {
//										Long taskid = (Long) taskLinkFielddataGroup.get("taskid");
//										Long count = (Long) taskLinkFielddataGroup.get("count");
//										TaskModel task = taskModelDao.getTaskByID(configDBModel, taskid);
//										//add by lianhr begin 2019/03/08
//										String featureid = (String) taskLinkFielddataGroup.get("shapeid");
//										//add by lianhr end
//										
//										if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
//											logger.error("Can not find task by taskid: " + taskid);
//											continue;
//										}
//										
//										Integer userid = 0;
//										Integer roleid = RoleType.UNKNOWN.getValue();
//
//										Integer taskType = task.getTasktype();
//										if (taskType.equals(TaskTypeEnum.POI_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())
//												|| taskType.equals(TaskTypeEnum.POI_KETOU.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_GEN.getValue())) {
//											userid = task.getEditid();
//											roleid = RoleType.ROLE_WORKER.getValue();
//										} else if (taskType.equals(TaskTypeEnum.POI_MC_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_KETOU.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_GEN.getValue())) {
//											userid = task.getCheckid();
//											roleid = RoleType.ROLE_CHECKER.getValue();
//										} else {
//											continue;
//										}
//										
//										Long projectid = task.getProjectid();
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, userid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//
//										editCapacityModel.setUserid(userid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(userid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(roleid);
//										editCapacityModel.setTime(time);
//										
//										editCapacityModel.setIswork(IsWorkTimeEnum.isWorkTime.getValue());
//										//add by lianhr begin 2019/03/08
//										for(int fi = 0; fi < featureid.split(",").length; fi++) {
//											String strFeatureid = featureid.split(",")[fi];
//											FeatureFinishedModel featureRecord = new FeatureFinishedModel();
//											featureRecord.setTasktype(taskType);
//											featureRecord.setProjectid(projectid);
//											featureRecord.setUserid(userid);
//											featureRecord.setRoleid(roleid);
//											
//											featureRecord.setFeatureid(Long.parseLong(strFeatureid));
//											
//											int featurecount = featureFinishedModelDao.queryCount(featureRecord);
//											if(featurecount <= 0 && !featureList.contains(featureRecord)) {
//												featureList.add(featureRecord);
//											} else if(featurecount <= 0 && featureList.contains(featureRecord)) {
//												continue;
//											} else {
//												if(count > 0) {
//													count = count - 1;
//												} else {
//													count = new Long(0);
//												}
//											}
//										}
//										//add by lianhr end
//										
//										editCapacityModel.setFielddatacount(editCapacityModel.getFielddatacount() + count);
//										
//										uniqRecords.put(editUniqRecord, editCapacityModel);
//									}
//
//									if (uniqRecords != null && !uniqRecords.isEmpty()) {
//										for (CapacityModel capacityModel : uniqRecords.values()) {
//											if (capacityModel.getErrorcount().equals(0L)
//													&& capacityModel.getTaskcount().equals(0L)
//													&& capacityModel.getModifypoi().equals(0L)
//													&& capacityModel.getCreatepoi().equals(0L)
//													&& capacityModel.getDeletepoi().equals(0L)
//													&& capacityModel.getExistdeletepoi().equals(0L)
//													&& capacityModel.getConfirmpoi().equals(0L)
//													&& capacityModel.getVisualerrorcount().equals(0L)
//													&& capacityModel.getFielddatacount().equals(0L))
//												continue;
//											capacityModelDao.insert(capacityModel);
//										}
//									}
//									if (uniqSpecialRecords != null && !uniqSpecialRecords.isEmpty()) {
//										for (QualityCapcityModel capacityModel : uniqSpecialRecords.values()) {
//											if (capacityModel.getErrorcount().equals(0L)
//													&& capacityModel.getCount().equals(0L)
//													&& capacityModel.getVisualerrorcount().equals(0L))
//												continue;
//											capacityModelDao.insertSpecial(capacityModel);
//										}
//									}
//									//add by lianhr begin 2019/03/08
//									for(int fi = 0; fi < featureList.size(); fi++) {
//										featureFinishedModelDao.insert(featureList.get(fi));
//									}
//									//add by lianhr end
//
//									logger.debug(
//											String.format("Scheduler POIEDIT task( %s ) finished.", newCapacityTask.getTime()));
//
//								} else {
//									logger.error("Scheduler POIEDIT task( %s ) has no configs.");
//									record = new CapacityTaskModel();
//									record.setId(curCapacityTaskID);
//									record.setState(CapacityTaskStateEnum.ERROR.getValue());
//									capacityTaskModelDao.updateByPrimaryKeySelective(record);
//								}
//							} else {
//								record = new CapacityTaskModel();
//								record.setId(curCapacityTaskID);
//								record.setState(CapacityTaskStateEnum.ERROR.getValue());
//								capacityTaskModelDao.updateByPrimaryKeySelective(record);
//							}
//						} else {
//							if (processType.equals(ProcessType.POIEDIT)) {
//								logger.debug(String.format("Scheduler POIEDIT task( %s ) started.", time));
//								ProcessConfigModel config = processConfigModelService
//										.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//								if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//									ConfigDBModel configDBModel = configDBModelDao
//											.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//									
//									Map<CapacityUniq, CapacityModel> uniqRecords = new HashMap<CapacityUniq, CapacityModel>();
//									
//									Map<QualityCapacityUniq, QualityCapcityModel> uniqSpecialRecords = new HashMap<QualityCapacityUniq, QualityCapcityModel>();
//									//add by lianhr begin 2019/03/08
//									List<FeatureFinishedModel> featureList = new ArrayList<FeatureFinishedModel>();
//									//add by lianhr end
//									List<Map<String, Object>> taskGroups = taskModelDao.groupTasksByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskGroup : taskGroups) {
//										Integer taskType = (Integer) taskGroup.get("tasktype");
//										Long projectid = (Long) taskGroup.get("projectid");
//										Integer editid = (Integer) taskGroup.get("editid");
//										Long editnum = (Long) taskGroup.get("editnum");
//										Integer checkid = (Integer) taskGroup.get("checkid");
//										Long checknum = (Long) taskGroup.get("checknum");
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
//										CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										CapacityModel checkCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(checkUniqRecord)) {
//											checkCapacityModel = uniqRecords.get(checkUniqRecord);
//											uniqRecords.remove(checkUniqRecord);
//										}
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										checkCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//												checkCapacityModel.setProcessid(processid);
//												checkCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//										checkCapacityModel.setTasktype(taskType);
//
//										editCapacityModel.setUserid(editid);
//										checkCapacityModel.setUserid(checkid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(editid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//										erecord.setId(checkid);
//										emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											checkCapacityModel.setUsername(emp.getRealname());
//										editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//										editCapacityModel.setTime(time);
//										checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//										checkCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
//										checkCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
//										editCapacityModel.setTaskcount(editCapacityModel.getTaskcount() + editnum);
//										checkCapacityModel.setTaskcount(checkCapacityModel.getTaskcount() + checknum);
//										
//										uniqRecords.put(editUniqRecord, editCapacityModel);
//										uniqRecords.put(checkUniqRecord, checkCapacityModel);
//									}
//									
//									List<Map<String, Object>> task15102Groups = taskBlockDetailModelDao.group15102ByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskBlockDetailGroup : task15102Groups) {
//										String featureid = (String) taskBlockDetailGroup.get("featureid");
//										Integer taskType = (Integer) taskBlockDetailGroup.get("tasktype");
//										Integer editid = (Integer) taskBlockDetailGroup.get("editid");
//										Long projectid = (Long) taskBlockDetailGroup.get("projectid");
//
//										if (taskType.compareTo(0) <= 0)
//											continue;
//
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//										
//										editCapacityModel.setUserid(editid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(editid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//										editCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
//										
//										//add by lianhr begin 2019/03/08
//										for(int fi = 0; fi < featureid.split(",").length; fi++) {
//											String strFeatureid = featureid.split(",")[fi];
//											FeatureFinishedModel featureRecord = new FeatureFinishedModel();
//											featureRecord.setTasktype(taskType);
//											featureRecord.setProjectid(projectid);
//											featureRecord.setUserid(editid);
//											featureRecord.setRoleid(RoleType.ROLE_WORKER.getValue());
//											featureRecord.setFeatureid(Long.parseLong(strFeatureid));
//											
//											int featurecount = featureFinishedModelDao.queryCount(featureRecord);
//											if(featurecount <= 0 && !featureList.contains(featureRecord)) {
//												featureList.add(featureRecord);
//											} else if(featurecount <= 0 && featureList.contains(featureRecord)) {
//												continue;
//											} else {
//												featureid = featureid.replaceAll(strFeatureid + ",", "").replaceAll(strFeatureid, "");
//											}
//										}
//										if(featureid.length() > 0 && featureid.substring(featureid.length() - 1).equals(",")) {
//											featureid = featureid.substring(0, featureid.length() - 1);
//										}
//										//add by lianhr end
//										
//										ProcessConfigModel config15102 = processConfigModelService
//												.selectByPrimaryKey(ProcessConfigEnum.BIANJISHUJUKU, processType);
//										if (config15102 != null && config15102.getDefaultValue() != null && !config15102.getDefaultValue().isEmpty()) {
//											ConfigDBModel config15102DBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config15102.getDefaultValue()));
//											List<Map<String, Object>> task15102PoisCreate1 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "众包新增");
//											editCapacityModel.setCreatepoi(editCapacityModel.getCreatepoi() + Integer.parseInt(task15102PoisCreate1.get(0).get("countnum").toString()));
//											List<Map<String, Object>> task15102PoisCreate2 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "车调新增");
//											editCapacityModel.setCreatepoi(editCapacityModel.getCreatepoi() + Integer.parseInt(task15102PoisCreate2.get(0).get("countnum").toString()));
//											List<Map<String, Object>> task15102PoisModified1 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "众包修改");
//											editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + Integer.parseInt(task15102PoisModified1.get(0).get("countnum").toString()));
//											List<Map<String, Object>> task15102PoisModified2 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "众包移位");
//											editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + Integer.parseInt(task15102PoisModified2.get(0).get("countnum").toString()));
//											//add by lianhr begin 2018/12/28
//											List<Map<String, Object>> task15102PoisModified4 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "众包确认");
//											editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + Integer.parseInt(task15102PoisModified4.get(0).get("countnum").toString()));
//											//add by lianhr end
//											List<Map<String, Object>> task15102PoisModified3 = taskBlockDetailModelDao.group15102ByPoi(config15102DBModel, featureid, "车调修改");
//											editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + Integer.parseInt(task15102PoisModified3.get(0).get("countnum").toString()));
//											
//											List<Map<String, Object>> taskDelete15102Pois1 = taskBlockDetailModelDao.group15102ByPoiDelete(config15102DBModel, featureid, "ver is null");
//											editCapacityModel.setDeletepoi(editCapacityModel.getDeletepoi() + Integer.parseInt(taskDelete15102Pois1.get(0).get("countnum").toString()));
//											List<Map<String, Object>> taskDelete15102Pois2 = taskBlockDetailModelDao.group15102ByPoiDelete(config15102DBModel, featureid, "ver is not null");
//											editCapacityModel.setExistdeletepoi(editCapacityModel.getExistdeletepoi() + Integer.parseInt(taskDelete15102Pois2.get(0).get("countnum").toString()));
//											
//											uniqRecords.put(editUniqRecord, editCapacityModel);
//										}
//									}
//									
//									
//									List<Map<String, Object>> taskBlockDetailGroups = taskBlockDetailModelDao.groupTaskBlockDetailsByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskBlockDetailGroup : taskBlockDetailGroups) {
//										Long blockid = (Long) taskBlockDetailGroup.get("blockid");
//										Integer editid = (Integer) taskBlockDetailGroup.get("editid");
//										Long editnum = (Long) taskBlockDetailGroup.get("editnum");
//										Integer checkid = (Integer) taskBlockDetailGroup.get("checkid");
//										Long checknum = (Long) taskBlockDetailGroup.get("checknum");
//										//add by lianhr begin 2019/03/08
//										String featureid = (String) taskBlockDetailGroup.get("featureid");
//										//add by lianhr end
//										
//										TaskModel task = taskModelDao.getTaskByBlockid(configDBModel, blockid);
//										
//										if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
//											logger.error("Can not find task by blockid: " + blockid);
//											continue;
//										}
//										
//										Integer taskType = task.getTasktype();
//										if (taskType.compareTo(0) <= 0)
//											continue;
//										Long projectid = task.getProjectid();
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										if (taskType.equals(TaskTypeEnum.POI_DATASET_31.getValue()) || // 车调POI创建制作任务
//												taskType.equals(TaskTypeEnum.POI_DATASET_32.getValue()) || // 车调制作32无照片
//												taskType.equals(TaskTypeEnum.POI_GEN.getValue())// 易淘金制作
//												) {
//											continue;
//										}
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
//										CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										CapacityModel checkCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(checkUniqRecord)) {
//											checkCapacityModel = uniqRecords.get(checkUniqRecord);
//											uniqRecords.remove(checkUniqRecord);
//										}
//										
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										checkCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//												checkCapacityModel.setProcessid(processid);
//												checkCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//										checkCapacityModel.setTasktype(taskType);
//
//										editCapacityModel.setUserid(editid);
//										checkCapacityModel.setUserid(checkid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(editid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//										erecord.setId(checkid);
//										emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											checkCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//										editCapacityModel.setTime(time);
//										checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//										checkCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
//										checkCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
//										
//										//add by lianhr begin 2019/03/08
//										for(int fi = 0; fi < featureid.split(",").length; fi++) {
//											String strFeatureid = featureid.split(",")[fi];
//											FeatureFinishedModel featureRecord = new FeatureFinishedModel();
//											featureRecord.setTasktype(taskType);
//											featureRecord.setProjectid(projectid);
//											if(editid.intValue() != 0) {
//												featureRecord.setUserid(editid);
//												featureRecord.setRoleid(RoleType.ROLE_WORKER.getValue());
//											} else if(checkid.intValue() != 0) {
//												featureRecord.setUserid(checkid);
//												featureRecord.setRoleid(RoleType.ROLE_CHECKER.getValue());
//											}
//											
//											featureRecord.setFeatureid(Long.parseLong(strFeatureid));
//											
//											int featurecount = featureFinishedModelDao.queryCount(featureRecord);
//											if(featurecount <= 0 && !featureList.contains(featureRecord)) {
//												featureList.add(featureRecord);
//											} else if(featurecount <= 0 && featureList.contains(featureRecord)) {
//												continue;
//											} else {
//												if(editid.intValue() != 0) {
//													if(editnum > 0) {
//														editnum = editnum - 1;
//													} else {
//														editnum = new Long(0);
//													}
//												} else if(checkid.intValue() != 0) {
//													if(checknum > 0) {
//														checknum = checknum - 1;
//													} else {
//														checknum = new Long(0);
//													}
//												}
//											}
//										}
//										//add by lianhr end
//										
//										editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + editnum);
//										checkCapacityModel.setModifypoi(checkCapacityModel.getModifypoi() + checknum);
//										
//										uniqRecords.put(editUniqRecord, editCapacityModel);
//										uniqRecords.put(checkUniqRecord, checkCapacityModel);
//									}
//									List<Map<String, Object>> specialTaskLinkErrorGroups = taskLinkErrorModelDao.specialGroupTaskLinkErrorByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskLinkErrorGroup : specialTaskLinkErrorGroups) {
//										Long taskid = (Long) taskLinkErrorGroup.get("taskid");
//										Long errortype = (Long) taskLinkErrorGroup.get("errortype");
//										Long count = (Long) taskLinkErrorGroup.get("count");
//										Long errorcount = (Long) taskLinkErrorGroup.get("errorcount");
//										Long visualerrorcount = (Long) taskLinkErrorGroup.get("visualerrorcount");
//										
//										TaskModel task = taskModelDao.getTaskByID(configDBModel, taskid);
//										if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
//											logger.error("Can not find task by taskid: " + taskid);
//											continue;
//										}
//										
//										Integer userid = 0;
//										Integer roleid = RoleType.UNKNOWN.getValue();
//										Integer taskType = task.getTasktype();
//										if (taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())){
//											userid = task.getEditid();
//											roleid = RoleType.ROLE_WORKER.getValue();
//										}
//										else {
//											continue;
//										}
//										Long projectid = task.getProjectid();
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										QualityCapacityUniq editUniqRecord = new QualityCapacityUniq(taskType, projectid, userid, errortype);
//										QualityCapcityModel editCapacityModel = new QualityCapcityModel();
//										if(uniqSpecialRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqSpecialRecords.get(editUniqRecord);
//											uniqSpecialRecords.remove(editUniqRecord);
//										}
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//												
//										editCapacityModel.setUserid(userid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(userid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(roleid);
//										editCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
//										
//										editCapacityModel.setErrortype(errortype);
//										
//										editCapacityModel.setCount(editCapacityModel.getCount() + count);
//										editCapacityModel.setErrorcount(editCapacityModel.getErrorcount() + errorcount);
//										editCapacityModel.setVisualerrorcount(editCapacityModel.getVisualerrorcount() + visualerrorcount);
//										
//										uniqSpecialRecords.put(editUniqRecord, editCapacityModel);
//									}
//																		
//									List<Map<String, Object>> taskLinkErrorGroups = taskLinkErrorModelDao.groupTaskLinkErrorByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskLinkErrorGroup : taskLinkErrorGroups) {
//										Long taskid = (Long) taskLinkErrorGroup.get("taskid");
//										Long errorcount = (Long) taskLinkErrorGroup.get("errorcount");
//										Long visualerrorcount = (Long) taskLinkErrorGroup.get("visualerrorcount");
//										//add by lianhr begin 2019/03/08
//										String featureid = (String) taskLinkErrorGroup.get("errorid");
//										//add by lianhr end
//										
//										TaskModel task = taskModelDao.getTaskByID(configDBModel, taskid);
//										
//										if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
//											logger.error("Can not find task by taskid: " + taskid);
//											continue;
//										}
//										
//										Integer userid = 0;
//										Integer roleid = RoleType.UNKNOWN.getValue();
//
//										Integer taskType = task.getTasktype();
//										if (taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())
//												|| 
//												taskType.equals(TaskTypeEnum.POI_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_KETOU.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_GEN.getValue())) {
//											userid = task.getEditid();
//											roleid = RoleType.ROLE_WORKER.getValue();
//										} else if (taskType.equals(TaskTypeEnum.POI_MC_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_KETOU.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_GEN.getValue())) {
//											userid = task.getCheckid();
//											roleid = RoleType.ROLE_CHECKER.getValue();
//										} else {
//											continue;
//										}
//										Long projectid = task.getProjectid();
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, userid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//
//										editCapacityModel.setUserid(userid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(userid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(roleid);
//										editCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
//										
//										//add by lianhr begin 2019/03/08
//										if (!taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) &&
//												!taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) &&
//												!taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())){
//											for(int fi = 0; fi < featureid.split(",").length; fi++) {
//												String strFeatureid = featureid.split(",")[fi];
//												FeatureFinishedModel featureRecord = new FeatureFinishedModel();
//												featureRecord.setTasktype(taskType);
//												featureRecord.setProjectid(projectid);
//												featureRecord.setUserid(userid);
//												featureRecord.setRoleid(roleid);
//												
//												featureRecord.setFeatureid(Long.parseLong(strFeatureid));
//												
//												int featurecount = featureFinishedModelDao.queryCount(featureRecord);
//												if(featurecount <= 0 && !featureList.contains(featureRecord)) {
//													featureList.add(featureRecord);
//												} else if(featurecount <= 0 && featureList.contains(featureRecord)) {
//													continue;
//												} else {
//													if(roleid == RoleType.ROLE_WORKER.getValue()) {
//														if(errorcount > 0) {
//															errorcount = errorcount - 1;
//														} else {
//															errorcount = new Long(0);
//														}
//														
//													} else if(roleid == RoleType.ROLE_CHECKER.getValue()) {
//														if(visualerrorcount > 0) {
//															visualerrorcount = visualerrorcount - 1;
//														} else {
//															visualerrorcount = new Long(0);
//														}
//													}
//												}
//											}
//										}
//										//add by lianhr end
//										
//										editCapacityModel.setErrorcount(editCapacityModel.getErrorcount() + errorcount);
//										editCapacityModel.setVisualerrorcount(editCapacityModel.getVisualerrorcount() + visualerrorcount);
//										
//										uniqRecords.put(editUniqRecord, editCapacityModel);
//									}
//									List<Map<String, Object>> taskLinkFielddataGroups = taskLinkFielddataModelDao.groupTaskLinkFielddataByTime(configDBModel, times[ii], time);
//									for (Map<String, Object> taskLinkFielddataGroup : taskLinkFielddataGroups) {
//										Long taskid = (Long) taskLinkFielddataGroup.get("taskid");
//										Long count = (Long) taskLinkFielddataGroup.get("count");
//										TaskModel task = taskModelDao.getTaskByID(configDBModel, taskid);
//										//add by lianhr begin 2019/03/08
//										String featureid = (String) taskLinkFielddataGroup.get("shapeid");
//										//add by lianhr end
//										
//										if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
//											logger.error("Can not find task by taskid: " + taskid);
//											continue;
//										}
//										
//										Integer userid = 0;
//										Integer roleid = RoleType.UNKNOWN.getValue();
//
//										Integer taskType = task.getTasktype();
//										if (taskType.equals(TaskTypeEnum.POI_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())
//												|| taskType.equals(TaskTypeEnum.POI_KETOU.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_GEN.getValue())) {
//											userid = task.getEditid();
//											roleid = RoleType.ROLE_WORKER.getValue();
//										} else if (taskType.equals(TaskTypeEnum.POI_MC_DATASET_31.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_DATASET_32.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_KETOU.getValue()) ||
//												taskType.equals(TaskTypeEnum.POI_MC_GEN.getValue())) {
//											userid = task.getCheckid();
//											roleid = RoleType.ROLE_CHECKER.getValue();
//										} else {
//											continue;
//										}
//										
//										Long projectid = task.getProjectid();
//										if (projectid.compareTo(0L) <= 0)
//											continue;
//										
//										CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, userid);
//										CapacityModel editCapacityModel = new CapacityModel();
//										if(uniqRecords.containsKey(editUniqRecord)) {
//											editCapacityModel = uniqRecords.get(editUniqRecord);
//											uniqRecords.remove(editUniqRecord);
//										}
//										
//										ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//										editCapacityModel.setProjectid(projectid);
//										if (project != null) {
//											Long processid = project.getProcessid();
//											ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//											if (process != null) {
//												editCapacityModel.setProcessid(processid);
//												editCapacityModel.setProcessname(process.getName());
//											}
//										}
//										
//										editCapacityModel.setTasktype(taskType);
//
//										editCapacityModel.setUserid(userid);
//										EmployeeModel erecord = new EmployeeModel();
//										erecord.setId(userid);
//										EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
//										if(emp != null)
//											editCapacityModel.setUsername(emp.getRealname());
//
//										editCapacityModel.setRoleid(roleid);
//										editCapacityModel.setTime(time);
//										editCapacityModel.setIswork(IsWorkTimeEnum.isNotWorkTime.getValue());
//										//add by lianhr begin 2019/03/08
//										for(int fi = 0; fi < featureid.split(",").length; fi++) {
//											String strFeatureid = featureid.split(",")[fi];
//											FeatureFinishedModel featureRecord = new FeatureFinishedModel();
//											featureRecord.setTasktype(taskType);
//											featureRecord.setProjectid(projectid);
//											featureRecord.setUserid(userid);
//											featureRecord.setRoleid(roleid);
//											
//											featureRecord.setFeatureid(Long.parseLong(strFeatureid));
//											
//											int featurecount = featureFinishedModelDao.queryCount(featureRecord);
//											if(featurecount <= 0 && !featureList.contains(featureRecord)) {
//												featureList.add(featureRecord);
//											} else if(featurecount <= 0 && featureList.contains(featureRecord)) {
//												continue;
//											} else {
//												if(count > 0) {
//													count = count - 1;
//												} else {
//													count = new Long(0);
//												}
//											}
//										}
//										//add by lianhr end
//										
//										editCapacityModel.setFielddatacount(editCapacityModel.getFielddatacount() + count);
//										
//										uniqRecords.put(editUniqRecord, editCapacityModel);
//									}
//
//									if (uniqRecords != null && !uniqRecords.isEmpty()) {
//										for (CapacityModel capacityModel : uniqRecords.values()) {
//											if (capacityModel.getErrorcount().equals(0L)
//													&& capacityModel.getTaskcount().equals(0L)
//													&& capacityModel.getModifypoi().equals(0L)
//													&& capacityModel.getCreatepoi().equals(0L)
//													&& capacityModel.getDeletepoi().equals(0L)
//													&& capacityModel.getExistdeletepoi().equals(0L)
//													&& capacityModel.getConfirmpoi().equals(0L)
//													&& capacityModel.getVisualerrorcount().equals(0L)
//													&& capacityModel.getFielddatacount().equals(0L))
//												continue;
//											capacityModelDao.insert(capacityModel);
//										}
//									}
//									if (uniqSpecialRecords != null && !uniqSpecialRecords.isEmpty()) {
//										for (QualityCapcityModel capacityModel : uniqSpecialRecords.values()) {
//											if (capacityModel.getErrorcount().equals(0L)
//													&& capacityModel.getCount().equals(0L)
//													&& capacityModel.getVisualerrorcount().equals(0L))
//												continue;
//											capacityModelDao.insertSpecial(capacityModel);
//										}
//									}
//									//add by lianhr begin 2019/03/08
//									for(int fi = 0; fi < featureList.size(); fi++) {
//										featureFinishedModelDao.insert(featureList.get(fi));
//									}
//									//add by lianhr end
//
//									logger.debug(
//											String.format("Scheduler POIEDIT task( %s ) finished.", newCapacityTask.getTime()));
//
//									record = new CapacityTaskModel();
//									record.setId(curCapacityTaskID);
//									record.setState(CapacityTaskStateEnum.FINISHED.getValue());
//									capacityTaskModelDao.updateByPrimaryKeySelective(record);
//								} else {
//									logger.error("Scheduler POIEDIT task( %s ) has no configs.");
//									record = new CapacityTaskModel();
//									record.setId(curCapacityTaskID);
//									record.setState(CapacityTaskStateEnum.ERROR.getValue());
//									capacityTaskModelDao.updateByPrimaryKeySelective(record);
//								}
//							} else {
//								record = new CapacityTaskModel();
//								record.setId(curCapacityTaskID);
//								record.setState(CapacityTaskStateEnum.ERROR.getValue());
//								capacityTaskModelDao.updateByPrimaryKeySelective(record);
//							}
//						}
//						
//					}
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//					Long curCapacityTaskID = newCapacityTask.getId();
//					CapacityTaskModel record = new CapacityTaskModel();
//					record.setId(curCapacityTaskID);
//					record.setState(CapacityTaskStateEnum.ERROR.getValue());
//					capacityTaskModelDao.updateByPrimaryKeySelective(record);
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//	}
	
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskERROR() {
//		try {
//			if (!errorWorktasksEnable.equalsIgnoreCase("true")) {
//				logger.debug("BREAK OUT CAUSE DISABLED");
//				return;
//			}
//			
//			logger.debug("ERROR START");
//			
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Calendar calendar = Calendar.getInstance();
//			Date now = new Date();
//			calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
//			String nowStr = sdf.format(calendar.getTime());
//			
//			ProcessType processType = ProcessType.UNKNOWN;
//			Map<Long, ProjectsProcessModel> uniqProcesses = new HashMap<Long, ProjectsProcessModel>();
//			try {
//				processType = ProcessType.ERROR;
//				
//				ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//				if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//					ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//					List<Map<String, Object>> groups = taskModelDao.groupTasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//						private static final long serialVersionUID = 7694111996341893515L;
//					{
//						add(TaskTypeEnum.ERROR);
//					}});
//					Map<WorkTasksUniq, WorkTasksModel> uniqRecords = new HashMap<WorkTasksUniq, WorkTasksModel>();
//					for (Map<String, Object> group : groups) {
//						Long projectid = (Long) group.get("projectid");
//						Integer state = (Integer) group.get("state");
//						Integer process = (Integer) group.get("process");
//						Integer editid = (Integer) group.get("editid");
//						Integer checkid = (Integer) group.get("checkid");
//						Integer count = ((Long) group.get("count")).intValue();
//						
//						ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//						if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//							continue;
//						
//						Long processid = project.getProcessid();
//						{
//							ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//							if (uniqProcesses.containsKey(processid)) {
//								projectsProcessModel = uniqProcesses.get(processid);
//								uniqProcesses.remove(processid);
//							}
//							projectsProcessModel.setProcessid(processid);
//							projectsProcessModel.setProcesstype(processType.getValue());
//							projectsProcessModel.setProjectid(projectid);
//							projectsProcessModel.setTime(nowStr);
//							
//							if (state.equals(0) && process.equals(0)) {
//								
//							} else if ((state.equals(0) && process.equals(5)) ||
//									(state.equals(1) && process.equals(5)) ||
//									(state.equals(2) && process.equals(6)) ||
//									(state.equals(2) && process.equals(52)) ||
//									(state.equals(2) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//								projectsProcessModel.setEdittask(projectsProcessModel.getEdittask() + count);
//							} else if ((state.equals(3) && process.equals(5) && !processType.equals(ProcessType.NRFC)) ||
//									(state.equals(0) && process.equals(6)) ||
//									(state.equals(1) && process.equals(6))) {
//								projectsProcessModel.setChecktask(projectsProcessModel.getChecktask() + count);
//							} else if ((state.equals(3) && process.equals(5) && processType.equals(ProcessType.NRFC)) ||
//									(state.equals(3) && process.equals(6)) ||
//									(state.equals(3) && process.equals(20))) {
//								projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//							} else if ((state.equals(1) && process.equals(52)) ||
//									(state.equals(2) && process.equals(5)) ||
//									(state.equals(0) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0) ||
//									(state.equals(1) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//								projectsProcessModel.setQctask(projectsProcessModel.getQctask() + count);
//							}
//							
//							projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//							uniqProcesses.put(processid, projectsProcessModel);
//						}
//						
//						if (editid != null && editid.compareTo(0) > 0) {
//							editid = editid.compareTo(500000) > 0 ? (editid - 500000) : editid;
//							WorkTasksUniq uniqRecord = new WorkTasksUniq(editid, RoleType.ROLE_WORKER.getValue(), processid);
//							WorkTasksModel workTasksModel = new WorkTasksModel();
//							if(uniqRecords.containsKey(uniqRecord)) {
//								workTasksModel = uniqRecords.get(uniqRecord);
//								uniqRecords.remove(uniqRecord);
//							}
//							workTasksModel.setUserid(editid);
//							workTasksModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//							workTasksModel.setRolename(RoleType.ROLE_WORKER.getDes());
//							workTasksModel.setProcesstype(processType.getValue());
//							workTasksModel.setProcessid(processid);
//							workTasksModel.setTime(nowStr);
//							
//							if (state.equals(0) && process.equals(0)) {
//								
//							} else if ((state.equals(0) && process.equals(5)) ||
//									(state.equals(1) && process.equals(5)) ||
//									(state.equals(2) && process.equals(6)) ||
//									(state.equals(2) && process.equals(52)) ||
//									(state.equals(2) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//								workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//							} else if ((state.equals(3) && process.equals(5) && !processType.equals(ProcessType.NRFC)) ||
//									(state.equals(0) && process.equals(6)) ||
//									(state.equals(1) && process.equals(6))) {
//								workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//							} else if ((state.equals(3) && process.equals(5) && processType.equals(ProcessType.NRFC)) ||
//									(state.equals(3) && process.equals(6)) ||
//									(state.equals(3) && process.equals(20))) {
//								workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//							} else if ((state.equals(1) && process.equals(52)) ||
//									(state.equals(2) && process.equals(5)) ||
//									(state.equals(0) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0) ||
//									(state.equals(1) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//								workTasksModel.setQctask(workTasksModel.getQctask() + count);
//							}
//							
//							uniqRecords.put(uniqRecord, workTasksModel);
//						}
//						
//						if (checkid != null && checkid.compareTo(0) > 0) {
//							checkid = checkid.compareTo(600000) > 0 ? (checkid - 600000) : checkid;
//							WorkTasksUniq uniqRecord = new WorkTasksUniq(checkid, RoleType.ROLE_CHECKER.getValue(), processid);
//							WorkTasksModel workTasksModel = new WorkTasksModel();
//							if(uniqRecords.containsKey(uniqRecord)) {
//								workTasksModel = uniqRecords.get(uniqRecord);
//								uniqRecords.remove(uniqRecord);
//							}
//							workTasksModel.setUserid(checkid);
//							workTasksModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//							workTasksModel.setRolename(RoleType.ROLE_CHECKER.getDes());
//							workTasksModel.setProcesstype(processType.getValue());
//							workTasksModel.setProcessid(processid);
//							workTasksModel.setTime(nowStr);
//							
//							if (state.equals(0) && process.equals(0)) {
//								
//							} else if ((state.equals(0) && process.equals(5)) ||
//									(state.equals(1) && process.equals(5)) ||
//									(state.equals(2) && process.equals(6)) ||
//									(state.equals(2) && process.equals(52)) ||
//									(state.equals(2) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//								workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//							} else if ((state.equals(3) && process.equals(5) && !processType.equals(ProcessType.NRFC)) ||
//									(state.equals(0) && process.equals(6)) ||
//									(state.equals(1) && process.equals(6))) {
//								workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//							} else if ((state.equals(3) && process.equals(5) && processType.equals(ProcessType.NRFC)) ||
//									(state.equals(3) && process.equals(6)) ||
//									(state.equals(3) && process.equals(20))) {
//								workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//							} else if ((state.equals(1) && process.equals(52)) ||
//									(state.equals(2) && process.equals(5)) ||
//									(state.equals(0) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0) ||
//									(state.equals(1) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//								workTasksModel.setQctask(workTasksModel.getQctask() + count);
//							}
//							
//							uniqRecords.put(uniqRecord, workTasksModel);
//						}
//						
//					}
//					if (uniqRecords != null && !uniqRecords.isEmpty()) {
//						for (WorkTasksModel workTasksModel : uniqRecords.values()) {
//							if (workTasksModel.getTotaltask().equals(0) &&
//									workTasksModel.getEdittask().equals(0) &&
//									workTasksModel.getQctask().equals(0) &&
//									workTasksModel.getChecktask().equals(0) &&
//									workTasksModel.getCompletetask().equals(0))
//								continue;
//							
//							try {
//								Integer userid = workTasksModel.getUserid();
//								EmployeeModel record = new EmployeeModel();
//								record.setId(userid);
//								EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(record );
//								if (emp == null)
//									continue;
//								workTasksModel.setUsername(emp.getRealname());
//								workTasksModel.setTotaltask(workTasksModel.getEdittask() + workTasksModel.getChecktask() + workTasksModel.getQctask() + workTasksModel.getCompletetask());
//								workTasksModelDao.newWorkTask(workTasksModel);
//							} catch (DuplicateKeyException e) {
//								logger.error(e.getMessage());
//							} catch (Exception e) {
//								logger.error(e.getMessage(), e);
//							}
//						}
//					} else {
//						logger.debug("workTasks has no records.");
//					}
//				} else {
//					logger.error("There's no Attach DB Config.");
//				}
//				logger.debug("ERROR END");
//			} catch(Exception e) {
//				logger.error(e.getMessage(), e);
//			}
//			if (uniqProcesses != null && !uniqProcesses.isEmpty()) {
//				for (ProjectsProcessModel projectsProcessModel : uniqProcesses.values()) {
//					try {
//						Long processid = projectsProcessModel.getProcessid();
//						ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//						if (process == null)
//							continue;
//						Integer _processType = projectsProcessModel.getProcesstype();
//						String processname = process.getName();
//						Long projectid = projectsProcessModel.getProjectid();
//						Integer totaltask = projectsProcessModel.getTotaltask();
//						Integer edittask = projectsProcessModel.getEdittask();
//						Integer qctask = projectsProcessModel.getQctask();
//						Integer checktask = projectsProcessModel.getChecktask();
//						Integer completetask = projectsProcessModel.getCompletetask();
//						Integer fielddatacount = projectsProcessModel.getFielddatacount();
//						Integer fielddatarest = projectsProcessModel.getFielddatarest();
//						Integer errorcount = projectsProcessModel.getErrorcount();
//						Integer errorrest = projectsProcessModel.getErrorrest();
//						
//						if (totaltask.equals(0) &&
//							edittask.equals(0) &&
//							qctask.equals(0) &&
//							checktask.equals(0) &&
//							completetask.equals(0) &&
//							fielddatacount.equals(0) &&
//							fielddatarest.equals(0) &&
//							errorcount.equals(0) &&
//							errorrest.equals(0))
//							continue;
//						
//						try {
//							projectsProcessModelDao.newProjectsProcess(projectsProcessModel);
//						} catch (DuplicateKeyException e) {
//							logger.error(e.getMessage());
//						} catch (Exception e) {
//							logger.error(e.getMessage(), e);
//						}
//						
//						try {
//							String sProgress = process.getProgress();
//							ArrayList<String> alProgress = sProgress.length() > 0 ? new ArrayList<String>(Arrays.asList(sProgress.split(","))) : new ArrayList<String>();
//							Integer length = alProgress.size();
//							while (length < CommonConstants.PROCESSCOUNT_ERROR) {
//								alProgress.add("0");
//								length++;
//							}
//							if (_processType.equals(ProcessType.POIEDIT.getValue())) {
//								DecimalFormat df = new DecimalFormat("0.000");
//								if (fielddatacount.compareTo(0) > 0) {
//									alProgress.set(0, df.format((float)(fielddatacount-fielddatarest)*100/fielddatacount));
//								} else {
//									alProgress.set(0, "0");
//								}
//								if (errorcount.compareTo(0) > 0) {
//									alProgress.set(1, df.format((float)(errorcount-errorrest)*100/errorcount));
//								} else {
//									alProgress.set(1, "0");
//								}
//							} else {
//								HashMap<Integer, Integer> stageTaskMap = projectsProcessModel.getStageTaskMap();
//								if (stageTaskMap != null && !stageTaskMap.isEmpty()) {
//									DecimalFormat df = new DecimalFormat("0.000");
//									if (stageTaskMap.containsKey(1)) {
//										alProgress.set(0, df.format((float)(stageTaskMap.get(1)*100)/totaltask));
//									}
//									if (stageTaskMap.containsKey(2)) {
//										alProgress.set(1, df.format((float)(stageTaskMap.get(2)*100)/totaltask));
//									}
//									if (stageTaskMap.containsKey(3)) {
//										alProgress.set(2, df.format((float)(stageTaskMap.get(3)*100)/totaltask));
//									}
//									if (stageTaskMap.containsKey(4)) {
//										alProgress.set(3, df.format((float)(stageTaskMap.get(4)*100)/totaltask));
//									}
//								}
//							}
//							StringBuilder sbProgress = new StringBuilder();
//							for (String p : alProgress) {
//								sbProgress.append(p);
//								sbProgress.append(",");
//							}
//							sbProgress.deleteCharAt(sbProgress.length() - 1);
//							process.setProgress(sbProgress.toString());
//							processModelDao.updateByPrimaryKeySelective(process );
//						} catch (Exception e) {
//							logger.error(e.getMessage(), e);
//						}
//						
//						if (processname.startsWith("POI易淘金编辑_"))
//							continue;
//						
//						if (totaltask.equals(completetask) &&
//								edittask.equals(0) &&
//								qctask.equals(0) &&
//								checktask.equals(0) &&
//								fielddatarest.equals(0) &&
//								errorrest.equals(0)) {
//							process.setState(ProcessState.COMPLETE.getValue());
//							processModelDao.updateByPrimaryKeySelective(process );
//							
//							ProjectModel project = new ProjectModel();
//							project.setId(projectid);
//							project.setOverstate(ProjectState.COMPLETE.getValue());
//							projectModelDao.updateByPrimaryKeySelective(project );
//						}
//					} catch (DuplicateKeyException e) {
//						logger.error(e.getMessage());
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//				}
//			} else {
//				logger.debug("projectsProcess has no records.");
//			}
//			
//			logger.debug("ERROR START");
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//	}
	
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskNRFC() {
//		if (!nrfcWorktasksEnable.equalsIgnoreCase("true")) {
//			logger.debug("BREAK OUT CAUSE DISABLED");
//			return;
//		}
//		
//		logger.debug("NRFC START");
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar calendar = Calendar.getInstance();
//		Date now = new Date();
//		calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
//		String nowStr = sdf.format(calendar.getTime());
//		
//		ProcessType processType = ProcessType.UNKNOWN;
//		Map<Long, ProjectsProcessModel> uniqProcesses = new HashMap<Long, ProjectsProcessModel>();
//		try {
//			processType = ProcessType.NRFC;
//			
//			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//				List<Map<String, Object>> groups = taskModelDao.groupTasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = 3858444676391259930L;
//				{
//					add(TaskTypeEnum.NRFC);
//				}});
//				Map<WorkTasksUniq, WorkTasksModel> uniqRecords = new HashMap<WorkTasksUniq, WorkTasksModel>();
//				for (Map<String, Object> group : groups) {
//					Long projectid = (Long) group.get("projectid");
//					Integer state = (Integer) group.get("state");
//					Integer process = (Integer) group.get("process");
//					Integer editid = (Integer) group.get("editid");
//					Integer checkid = (Integer) group.get("checkid");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					{
//						ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//						if (uniqProcesses.containsKey(processid)) {
//							projectsProcessModel = uniqProcesses.get(processid);
//							uniqProcesses.remove(processid);
//						}
//						projectsProcessModel.setProcessid(processid);
//						projectsProcessModel.setProcesstype(processType.getValue());
//						projectsProcessModel.setProjectid(projectid);
//						projectsProcessModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							projectsProcessModel.setIdletask(projectsProcessModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(52)) ||
//								(state.equals(2) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//							projectsProcessModel.setEdittask(projectsProcessModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5))) {
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//							projectsProcessModel.setPrepublishtask(projectsProcessModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(21))) {
//							projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//							projectsProcessModel.setStageTaskMapByStage(3, projectsProcessModel.getStageTaskMapByStage(3) + count);
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5)) ||
//								(state.equals(0) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0) ||
//								(state.equals(1) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//							projectsProcessModel.setQctask(projectsProcessModel.getQctask() + count);
//						}
//						
//						projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//						uniqProcesses.put(processid, projectsProcessModel);
//					}
//					
//					if (editid != null && editid.compareTo(0) > 0) {
//						editid = editid.compareTo(500000) > 0 ? (editid - 500000) : editid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(editid, RoleType.ROLE_WORKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(editid);
//						workTasksModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_WORKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							workTasksModel.setIdletask(workTasksModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(52)) ||
//								(state.equals(2) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5))) {
//							workTasksModel.setPrepublishtask(workTasksModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(20))) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5)) ||
//								(state.equals(0) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0) ||
//								(state.equals(1) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//							workTasksModel.setQctask(workTasksModel.getQctask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//					if (checkid != null && checkid.compareTo(0) > 0) {
//						checkid = checkid.compareTo(600000) > 0 ? (checkid - 600000) : checkid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(checkid, RoleType.ROLE_CHECKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(checkid);
//						workTasksModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_CHECKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							workTasksModel.setIdletask(workTasksModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(52)) ||
//								(state.equals(2) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5))) {
//							workTasksModel.setPrepublishtask(workTasksModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(20))) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5)) ||
//								(state.equals(0) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0) ||
//								(state.equals(1) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
//							workTasksModel.setQctask(workTasksModel.getQctask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//				}
//				if (uniqRecords != null && !uniqRecords.isEmpty()) {
//					for (WorkTasksModel workTasksModel : uniqRecords.values()) {
//						if (workTasksModel.getTotaltask().equals(0) &&
//								workTasksModel.getEdittask().equals(0) &&
//								workTasksModel.getQctask().equals(0) &&
//								workTasksModel.getChecktask().equals(0) &&
//								workTasksModel.getPrepublishtask().equals(0) &&
//								workTasksModel.getCompletetask().equals(0))
//							continue;
//						
//						try {
//							Integer userid = workTasksModel.getUserid();
//							EmployeeModel record = new EmployeeModel();
//							record.setId(userid);
//							EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(record );
//							if (emp == null)
//								continue;
//							workTasksModel.setUsername(emp.getRealname());
//							workTasksModel.setTotaltask(workTasksModel.getEdittask() + workTasksModel.getChecktask() + workTasksModel.getQctask() + workTasksModel.getPrepublishtask() + workTasksModel.getCompletetask());
//							workTasksModelDao.newWorkTask(workTasksModel);
//						} catch (DuplicateKeyException e) {
//							logger.error(e.getMessage());
//						} catch (Exception e) {
//							logger.error(e.getMessage(), e);
//						}
//					}
//				} else {
//					logger.debug("workTasks has no records.");
//				}
//			} else {
//				logger.error("There's no Attach DB Config.");
//			}
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		if (uniqProcesses != null && !uniqProcesses.isEmpty()) {
//			for (ProjectsProcessModel projectsProcessModel : uniqProcesses.values()) {
//				try {
//					Long processid = projectsProcessModel.getProcessid();
//					ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//					if (process == null)
//						continue;
//					Integer _processType = projectsProcessModel.getProcesstype();
//					String processname = process.getName();
//					Long projectid = projectsProcessModel.getProjectid();
//					Integer totaltask = projectsProcessModel.getTotaltask();
//					Integer edittask = projectsProcessModel.getEdittask();
//					Integer qctask = projectsProcessModel.getQctask();
//					Integer checktask = projectsProcessModel.getChecktask();
//					Integer prepublishtask = projectsProcessModel.getPrepublishtask();
//					Integer completetask = projectsProcessModel.getCompletetask();
//					Integer fielddatacount = projectsProcessModel.getFielddatacount();
//					Integer fielddatarest = projectsProcessModel.getFielddatarest();
//					Integer errorcount = projectsProcessModel.getErrorcount();
//					Integer errorrest = projectsProcessModel.getErrorrest();
//					
//					if (totaltask.equals(0) &&
//						edittask.equals(0) &&
//						qctask.equals(0) &&
//						checktask.equals(0) &&
//						prepublishtask.equals(0) &&
//						completetask.equals(0) &&
//						fielddatacount.equals(0) &&
//						fielddatarest.equals(0) &&
//						errorcount.equals(0) &&
//						errorrest.equals(0))
//						continue;
//					
//					try {
//						projectsProcessModelDao.newProjectsProcess(projectsProcessModel);
//					} catch (DuplicateKeyException e) {
//						logger.error(e.getMessage());
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					try {
//						String sProgress = process.getProgress();
//						ArrayList<String> alProgress = sProgress.length() > 0 ? new ArrayList<String>(Arrays.asList(sProgress.split(","))) : new ArrayList<String>();
//						Integer length = alProgress.size();
//						while (length < CommonConstants.PROCESSCOUNT_ERROR) {
//							alProgress.add("0");
//							length++;
//						}
//						if (_processType.equals(ProcessType.POIEDIT.getValue())) {
//							DecimalFormat df = new DecimalFormat("0.000");
//							if (fielddatacount.compareTo(0) > 0) {
//								alProgress.set(0, df.format((float)(fielddatacount-fielddatarest)*100/fielddatacount));
//							} else {
//								alProgress.set(0, "0");
//							}
//							if (errorcount.compareTo(0) > 0) {
//								alProgress.set(1, df.format((float)(errorcount-errorrest)*100/errorcount));
//							} else {
//								alProgress.set(1, "0");
//							}
//						} else {
//							HashMap<Integer, Integer> stageTaskMap = projectsProcessModel.getStageTaskMap();
//							if (stageTaskMap != null && !stageTaskMap.isEmpty()) {
//								DecimalFormat df = new DecimalFormat("0.000");
//								if (stageTaskMap.containsKey(1)) {
//									alProgress.set(0, df.format((float)(stageTaskMap.get(1)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(2)) {
//									alProgress.set(1, df.format((float)(stageTaskMap.get(2)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(3)) {
//									alProgress.set(2, df.format((float)(stageTaskMap.get(3)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(4)) {
//									alProgress.set(3, df.format((float)(stageTaskMap.get(4)*100)/totaltask));
//								}
//							}
//						}
//						StringBuilder sbProgress = new StringBuilder();
//						for (String p : alProgress) {
//							sbProgress.append(p);
//							sbProgress.append(",");
//						}
//						sbProgress.deleteCharAt(sbProgress.length() - 1);
//						process.setProgress(sbProgress.toString());
//						processModelDao.updateByPrimaryKeySelective(process );
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					if (processname.startsWith("POI易淘金编辑_"))
//						continue;
//					
//					if (totaltask.equals(completetask) &&
//							edittask.equals(0) &&
//							qctask.equals(0) &&
//							checktask.equals(0) &&
//							prepublishtask.equals(0) &&
//							fielddatarest.equals(0) &&
//							errorrest.equals(0)) {
//						process.setState(ProcessState.COMPLETE.getValue());
//						processModelDao.updateByPrimaryKeySelective(process );
//						
//						ProjectModel project = new ProjectModel();
//						project.setId(projectid);
//						project.setOverstate(ProjectState.COMPLETE.getValue());
//						projectModelDao.updateByPrimaryKeySelective(project );
//					}
//				} catch (DuplicateKeyException e) {
//					logger.error(e.getMessage());
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//		} else {
//			logger.debug("projectsProcess has no records.");
//		}
//		logger.debug("NRFC END");
//	}
	
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskAREA() {
//		if (!areaWorktasksEnable.equalsIgnoreCase("true")) {
//			logger.debug("BREAK OUT CAUSE DISABLED");
//			return;
//		}
//		
//		logger.debug("AREA START");
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar calendar = Calendar.getInstance();
//		Date now = new Date();
//		calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
//		String nowStr = sdf.format(calendar.getTime());
//		
//		ProcessType processType = ProcessType.UNKNOWN;
//		Map<Long, ProjectsProcessModel> uniqProcesses = new HashMap<Long, ProjectsProcessModel>();
//		try {
//			processType = ProcessType.AREA;
//			
//			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//				List<Map<String, Object>> groups = taskModelDao.groupTasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = 7739429730636924053L;
//				{
//					add(TaskTypeEnum.AREA_QUHUAN);
//					add(TaskTypeEnum.AREA_JIANCHENGQU);
//				}});
//				Map<WorkTasksUniq, WorkTasksModel> uniqRecords = new HashMap<WorkTasksUniq, WorkTasksModel>();
//				for (Map<String, Object> group : groups) {
//					Long projectid = (Long) group.get("projectid");
//					Integer state = (Integer) group.get("state");
//					Integer process = (Integer) group.get("process");
//					Integer editid = (Integer) group.get("editid");
//					Integer checkid = (Integer) group.get("checkid");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					{
//						ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//						if (uniqProcesses.containsKey(processid)) {
//							projectsProcessModel = uniqProcesses.get(processid);
//							uniqProcesses.remove(processid);
//						}
//						projectsProcessModel.setProcessid(processid);
//						projectsProcessModel.setProcesstype(processType.getValue());
//						projectsProcessModel.setProjectid(projectid);
//						projectsProcessModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							projectsProcessModel.setIdletask(projectsProcessModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							projectsProcessModel.setEdittask(projectsProcessModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							projectsProcessModel.setChecktask(projectsProcessModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//							projectsProcessModel.setPrepublishtask(projectsProcessModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(21))) {
//							projectsProcessModel.setStageTaskMapByStage(3, projectsProcessModel.getStageTaskMapByStage(3) + count);
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//							projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							projectsProcessModel.setQctask(projectsProcessModel.getQctask() + count);
//						}
//						
//						projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//						uniqProcesses.put(processid, projectsProcessModel);
//					}
//					
//					if (editid != null && editid.compareTo(0) > 0) {
//						editid = editid.compareTo(500000) > 0 ? (editid - 500000) : editid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(editid, RoleType.ROLE_WORKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(editid);
//						workTasksModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_WORKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							workTasksModel.setIdletask(workTasksModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							workTasksModel.setPrepublishtask(workTasksModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(20))) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							workTasksModel.setQctask(workTasksModel.getQctask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//					if (checkid != null && checkid.compareTo(0) > 0) {
//						checkid = checkid.compareTo(600000) > 0 ? (checkid - 600000) : checkid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(checkid, RoleType.ROLE_CHECKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(checkid);
//						workTasksModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_CHECKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							workTasksModel.setIdletask(workTasksModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							workTasksModel.setPrepublishtask(workTasksModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(20))) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							workTasksModel.setQctask(workTasksModel.getQctask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//				}
//				if (uniqRecords != null && !uniqRecords.isEmpty()) {
//					for (WorkTasksModel workTasksModel : uniqRecords.values()) {
//						if (workTasksModel.getTotaltask().equals(0) &&
//								workTasksModel.getEdittask().equals(0) &&
//								workTasksModel.getQctask().equals(0) &&
//								workTasksModel.getChecktask().equals(0) &&
//								workTasksModel.getPrepublishtask().equals(0) &&
//								workTasksModel.getCompletetask().equals(0))
//							continue;
//						
//						try {
//							Integer userid = workTasksModel.getUserid();
//							EmployeeModel record = new EmployeeModel();
//							record.setId(userid);
//							EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(record );
//							if (emp == null)
//								continue;
//							workTasksModel.setUsername(emp.getRealname());
//							workTasksModel.setTotaltask(workTasksModel.getEdittask() + workTasksModel.getChecktask() + workTasksModel.getQctask() + workTasksModel.getPrepublishtask() + workTasksModel.getCompletetask());
//							workTasksModelDao.newWorkTask(workTasksModel);
//						} catch (DuplicateKeyException e) {
//							logger.error(e.getMessage());
//						} catch (Exception e) {
//							logger.error(e.getMessage(), e);
//						}
//					}
//				} else {
//					logger.debug("workTasks has no records.");
//				}
//			} else {
//				logger.error("There's no Area DB Config.");
//			}
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		if (uniqProcesses != null && !uniqProcesses.isEmpty()) {
//			for (ProjectsProcessModel projectsProcessModel : uniqProcesses.values()) {
//				try {
//					Long processid = projectsProcessModel.getProcessid();
//					ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//					if (process == null)
//						continue;
//					Integer _processType = projectsProcessModel.getProcesstype();
//					String processname = process.getName();
//					Long projectid = projectsProcessModel.getProjectid();
//					Integer totaltask = projectsProcessModel.getTotaltask();
//					Integer edittask = projectsProcessModel.getEdittask();
//					Integer qctask = projectsProcessModel.getQctask();
//					Integer checktask = projectsProcessModel.getChecktask();
//					Integer prepublishtask = projectsProcessModel.getPrepublishtask();
//					Integer completetask = projectsProcessModel.getCompletetask();
//					Integer fielddatacount = projectsProcessModel.getFielddatacount();
//					Integer fielddatarest = projectsProcessModel.getFielddatarest();
//					Integer errorcount = projectsProcessModel.getErrorcount();
//					Integer errorrest = projectsProcessModel.getErrorrest();
//					
//					if (totaltask.equals(0) &&
//						edittask.equals(0) &&
//						qctask.equals(0) &&
//						checktask.equals(0) &&
//						prepublishtask.equals(0) &&
//						completetask.equals(0) &&
//						fielddatacount.equals(0) &&
//						fielddatarest.equals(0) &&
//						errorcount.equals(0) &&
//						errorrest.equals(0))
//						continue;
//					
//					try {
//						projectsProcessModelDao.newProjectsProcess(projectsProcessModel);
//					} catch (DuplicateKeyException e) {
//						logger.error(e.getMessage());
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					try {
//						String sProgress = process.getProgress();
//						ArrayList<String> alProgress = sProgress.length() > 0 ? new ArrayList<String>(Arrays.asList(sProgress.split(","))) : new ArrayList<String>();
//						Integer length = alProgress.size();
//						while (length < CommonConstants.PROCESSCOUNT_ERROR) {
//							alProgress.add("0");
//							length++;
//						}
//						if (_processType.equals(ProcessType.POIEDIT.getValue())) {
//							DecimalFormat df = new DecimalFormat("0.000");
//							if (fielddatacount.compareTo(0) > 0) {
//								alProgress.set(0, df.format((float)(fielddatacount-fielddatarest)*100/fielddatacount));
//							} else {
//								alProgress.set(0, "0");
//							}
//							if (errorcount.compareTo(0) > 0) {
//								alProgress.set(1, df.format((float)(errorcount-errorrest)*100/errorcount));
//							} else {
//								alProgress.set(1, "0");
//							}
//						} else {
//							HashMap<Integer, Integer> stageTaskMap = projectsProcessModel.getStageTaskMap();
//							if (stageTaskMap != null && !stageTaskMap.isEmpty()) {
//								DecimalFormat df = new DecimalFormat("0.000");
//								if (stageTaskMap.containsKey(1)) {
//									alProgress.set(0, df.format((float)(stageTaskMap.get(1)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(2)) {
//									alProgress.set(1, df.format((float)(stageTaskMap.get(2)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(3)) {
//									alProgress.set(2, df.format((float)(stageTaskMap.get(3)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(4)) {
//									alProgress.set(3, df.format((float)(stageTaskMap.get(4)*100)/totaltask));
//								}
//							}
//						}
//						StringBuilder sbProgress = new StringBuilder();
//						for (String p : alProgress) {
//							sbProgress.append(p);
//							sbProgress.append(",");
//						}
//						sbProgress.deleteCharAt(sbProgress.length() - 1);
//						process.setProgress(sbProgress.toString());
//						processModelDao.updateByPrimaryKeySelective(process );
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					if (processname.startsWith("POI易淘金编辑_"))
//						continue;
//					
//					if (totaltask.equals(completetask) &&
//							edittask.equals(0) &&
//							qctask.equals(0) &&
//							checktask.equals(0) &&
//							fielddatarest.equals(0) &&
//							prepublishtask.equals(0) &&
//							errorrest.equals(0)) {
//						process.setState(ProcessState.COMPLETE.getValue());
//						processModelDao.updateByPrimaryKeySelective(process );
//						
//						ProjectModel project = new ProjectModel();
//						project.setId(projectid);
//						project.setOverstate(ProjectState.COMPLETE.getValue());
//						projectModelDao.updateByPrimaryKeySelective(project );
//					}
//				} catch (DuplicateKeyException e) {
//					logger.error(e.getMessage());
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//		} else {
//			logger.debug("projectsProcess has no records.");
//		}
//		
//		logger.debug("AREA END");
//	}
	
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskATTACH() {
//		if (!attachWorktasksEnable.equalsIgnoreCase("true")) {
//			logger.debug("BREAK OUT CAUSE DISABLED");
//			return;
//		}
//		
//		logger.debug("ATTACH START");
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar calendar = Calendar.getInstance();
//		Date now = new Date();
//		calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
//		String nowStr = sdf.format(calendar.getTime());
//		
//		ProcessType processType = ProcessType.UNKNOWN;
//		Map<Long, ProjectsProcessModel> uniqProcesses = new HashMap<Long, ProjectsProcessModel>();
//		try {
//			processType = ProcessType.ATTACH;
//			
//			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//				List<Map<String, Object>> groups = taskModelDao.groupTasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = 7739429730636924053L;
//				{
//					add(TaskTypeEnum.ATTACH);
//				}});
//				Map<WorkTasksUniq, WorkTasksModel> uniqRecords = new HashMap<WorkTasksUniq, WorkTasksModel>();
//				for (Map<String, Object> group : groups) {
//					Long projectid = (Long) group.get("projectid");
//					Integer state = (Integer) group.get("state");
//					Integer process = (Integer) group.get("process");
//					Integer editid = (Integer) group.get("editid");
//					Integer checkid = (Integer) group.get("checkid");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					{
//						ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//						if (uniqProcesses.containsKey(processid)) {
//							projectsProcessModel = uniqProcesses.get(processid);
//							uniqProcesses.remove(processid);
//						}
//						projectsProcessModel.setProcessid(processid);
//						projectsProcessModel.setProcesstype(processType.getValue());
//						projectsProcessModel.setProjectid(projectid);
//						projectsProcessModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							projectsProcessModel.setIdletask(projectsProcessModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							projectsProcessModel.setEdittask(projectsProcessModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							projectsProcessModel.setChecktask(projectsProcessModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//							projectsProcessModel.setPrepublishtask(projectsProcessModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(21))) {
//							projectsProcessModel.setStageTaskMapByStage(3, projectsProcessModel.getStageTaskMapByStage(3) + count);
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//							projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							projectsProcessModel.setQctask(projectsProcessModel.getQctask() + count);
//						}
//						
//						projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//						uniqProcesses.put(processid, projectsProcessModel);
//					}
//					
//					if (editid != null && editid.compareTo(0) > 0) {
//						editid = editid.compareTo(500000) > 0 ? (editid - 500000) : editid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(editid, RoleType.ROLE_WORKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(editid);
//						workTasksModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_WORKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							workTasksModel.setIdletask(workTasksModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							workTasksModel.setPrepublishtask(workTasksModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(20))) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							workTasksModel.setQctask(workTasksModel.getQctask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//					if (checkid != null && checkid.compareTo(0) > 0) {
//						checkid = checkid.compareTo(600000) > 0 ? (checkid - 600000) : checkid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(checkid, RoleType.ROLE_CHECKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(checkid);
//						workTasksModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_CHECKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							workTasksModel.setIdletask(workTasksModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							workTasksModel.setPrepublishtask(workTasksModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(20))) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							workTasksModel.setQctask(workTasksModel.getQctask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//				}
//				if (uniqRecords != null && !uniqRecords.isEmpty()) {
//					for (WorkTasksModel workTasksModel : uniqRecords.values()) {
//						if (workTasksModel.getTotaltask().equals(0) &&
//								workTasksModel.getEdittask().equals(0) &&
//								workTasksModel.getQctask().equals(0) &&
//								workTasksModel.getChecktask().equals(0) &&
//								workTasksModel.getPrepublishtask().equals(0) &&
//								workTasksModel.getCompletetask().equals(0))
//							continue;
//						
//						try {
//							Integer userid = workTasksModel.getUserid();
//							EmployeeModel record = new EmployeeModel();
//							record.setId(userid);
//							EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(record );
//							if (emp == null)
//								continue;
//							workTasksModel.setUsername(emp.getRealname());
//							workTasksModel.setTotaltask(workTasksModel.getEdittask() + workTasksModel.getChecktask() + workTasksModel.getQctask() + workTasksModel.getPrepublishtask() + workTasksModel.getCompletetask());
//							workTasksModelDao.newWorkTask(workTasksModel);
//						} catch (DuplicateKeyException e) {
//							logger.error(e.getMessage());
//						} catch (Exception e) {
//							logger.error(e.getMessage(), e);
//						}
//					}
//				} else {
//					logger.debug("workTasks has no records.");
//				}
//			} else {
//				logger.error("There's no Attach DB Config.");
//			}
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		if (uniqProcesses != null && !uniqProcesses.isEmpty()) {
//			for (ProjectsProcessModel projectsProcessModel : uniqProcesses.values()) {
//				try {
//					Long processid = projectsProcessModel.getProcessid();
//					ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//					if (process == null)
//						continue;
//					Integer _processType = projectsProcessModel.getProcesstype();
//					String processname = process.getName();
//					Long projectid = projectsProcessModel.getProjectid();
//					Integer totaltask = projectsProcessModel.getTotaltask();
//					Integer edittask = projectsProcessModel.getEdittask();
//					Integer qctask = projectsProcessModel.getQctask();
//					Integer checktask = projectsProcessModel.getChecktask();
//					Integer prepublishtask = projectsProcessModel.getPrepublishtask();
//					Integer completetask = projectsProcessModel.getCompletetask();
//					Integer fielddatacount = projectsProcessModel.getFielddatacount();
//					Integer fielddatarest = projectsProcessModel.getFielddatarest();
//					Integer errorcount = projectsProcessModel.getErrorcount();
//					Integer errorrest = projectsProcessModel.getErrorrest();
//					
//					if (totaltask.equals(0) &&
//						edittask.equals(0) &&
//						qctask.equals(0) &&
//						checktask.equals(0) &&
//						prepublishtask.equals(0) &&
//						completetask.equals(0) &&
//						fielddatacount.equals(0) &&
//						fielddatarest.equals(0) &&
//						errorcount.equals(0) &&
//						errorrest.equals(0))
//						continue;
//					
//					try {
//						projectsProcessModelDao.newProjectsProcess(projectsProcessModel);
//					} catch (DuplicateKeyException e) {
//						logger.error(e.getMessage());
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					try {
//						String sProgress = process.getProgress();
//						ArrayList<String> alProgress = sProgress.length() > 0 ? new ArrayList<String>(Arrays.asList(sProgress.split(","))) : new ArrayList<String>();
//						Integer length = alProgress.size();
//						while (length < CommonConstants.PROCESSCOUNT_ERROR) {
//							alProgress.add("0");
//							length++;
//						}
//						if (_processType.equals(ProcessType.POIEDIT.getValue())) {
//							DecimalFormat df = new DecimalFormat("0.000");
//							if (fielddatacount.compareTo(0) > 0) {
//								alProgress.set(0, df.format((float)(fielddatacount-fielddatarest)*100/fielddatacount));
//							} else {
//								alProgress.set(0, "0");
//							}
//							if (errorcount.compareTo(0) > 0) {
//								alProgress.set(1, df.format((float)(errorcount-errorrest)*100/errorcount));
//							} else {
//								alProgress.set(1, "0");
//							}
//						} else {
//							HashMap<Integer, Integer> stageTaskMap = projectsProcessModel.getStageTaskMap();
//							if (stageTaskMap != null && !stageTaskMap.isEmpty()) {
//								DecimalFormat df = new DecimalFormat("0.000");
//								if (stageTaskMap.containsKey(1)) {
//									alProgress.set(0, df.format((float)(stageTaskMap.get(1)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(2)) {
//									alProgress.set(1, df.format((float)(stageTaskMap.get(2)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(3)) {
//									alProgress.set(2, df.format((float)(stageTaskMap.get(3)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(4)) {
//									alProgress.set(3, df.format((float)(stageTaskMap.get(4)*100)/totaltask));
//								}
//							}
//						}
//						StringBuilder sbProgress = new StringBuilder();
//						for (String p : alProgress) {
//							sbProgress.append(p);
//							sbProgress.append(",");
//						}
//						sbProgress.deleteCharAt(sbProgress.length() - 1);
//						process.setProgress(sbProgress.toString());
//						processModelDao.updateByPrimaryKeySelective(process );
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					if (processname.startsWith("附属表匹配专用"))
//						continue;
//					
//					if (totaltask.equals(completetask) &&
//							edittask.equals(0) &&
//							qctask.equals(0) &&
//							checktask.equals(0) &&
//							fielddatarest.equals(0) &&
//							prepublishtask.equals(0) &&
//							errorrest.equals(0)) {
//						process.setState(ProcessState.COMPLETE.getValue());
//						processModelDao.updateByPrimaryKeySelective(process );
//						
//						ProjectModel project = new ProjectModel();
//						project.setId(projectid);
//						project.setOverstate(ProjectState.COMPLETE.getValue());
//						projectModelDao.updateByPrimaryKeySelective(project );
//					}
//				} catch (DuplicateKeyException e) {
//					logger.error(e.getMessage());
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//		} else {
//			logger.debug("projectsProcess has no records.");
//		}
//		logger.debug("ATTACH END");
//	}
	
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskATTACHDATA() {
//		if (!attachdataWorktasksEnable.equalsIgnoreCase("true")) {
//			logger.debug("BREAK OUT CAUSE DISABLED");
//			return;
//		}
//		
//		logger.debug("ATTACHWITHDATA START");
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar calendar = Calendar.getInstance();
//		Date now = new Date();
//		calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
//		String nowStr = sdf.format(calendar.getTime());
//		
//		ProcessType processType = ProcessType.UNKNOWN;
//		Map<Long, ProjectsProcessModel> uniqProcesses = new HashMap<Long, ProjectsProcessModel>();
//		try {
//			processType = ProcessType.ATTACHWITHDATA;
//			
//			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//				List<Map<String, Object>> groups = taskModelDao.groupTasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = 7739429730636924053L;
//				{
//					add(TaskTypeEnum.ATTACHWITHDATA);
//				}});
//				Map<WorkTasksUniq, WorkTasksModel> uniqRecords = new HashMap<WorkTasksUniq, WorkTasksModel>();
//				for (Map<String, Object> group : groups) {
//					Long projectid = (Long) group.get("projectid");
//					Integer state = (Integer) group.get("state");
//					Integer process = (Integer) group.get("process");
//					Integer editid = (Integer) group.get("editid");
//					Integer checkid = (Integer) group.get("checkid");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					{
//						ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//						if (uniqProcesses.containsKey(processid)) {
//							projectsProcessModel = uniqProcesses.get(processid);
//							uniqProcesses.remove(processid);
//						}
//						projectsProcessModel.setProcessid(processid);
//						projectsProcessModel.setProcesstype(processType.getValue());
//						projectsProcessModel.setProjectid(projectid);
//						projectsProcessModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							projectsProcessModel.setIdletask(projectsProcessModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							projectsProcessModel.setEdittask(projectsProcessModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							projectsProcessModel.setChecktask(projectsProcessModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//							projectsProcessModel.setPrepublishtask(projectsProcessModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(21))) {
//							projectsProcessModel.setStageTaskMapByStage(3, projectsProcessModel.getStageTaskMapByStage(3) + count);
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//							projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							projectsProcessModel.setQctask(projectsProcessModel.getQctask() + count);
//						}
//						
//						projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//						uniqProcesses.put(processid, projectsProcessModel);
//					}
//					
//					if (editid != null && editid.compareTo(0) > 0) {
//						editid = editid.compareTo(500000) > 0 ? (editid - 500000) : editid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(editid, RoleType.ROLE_WORKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(editid);
//						workTasksModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_WORKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							workTasksModel.setIdletask(workTasksModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							workTasksModel.setPrepublishtask(workTasksModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(20))) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							workTasksModel.setQctask(workTasksModel.getQctask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//					if (checkid != null && checkid.compareTo(0) > 0) {
//						checkid = checkid.compareTo(600000) > 0 ? (checkid - 600000) : checkid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(checkid, RoleType.ROLE_CHECKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(checkid);
//						workTasksModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_CHECKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							workTasksModel.setIdletask(workTasksModel.getIdletask() + count);
//						} else if ((state.equals(0) && process.equals(5)) ||
//								(state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6)) ||
//								(state.equals(2) && process.equals(52))) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(0) && process.equals(6)) ||
//								(state.equals(1) && process.equals(6))) {
//							workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							workTasksModel.setPrepublishtask(workTasksModel.getPrepublishtask() + count);
//						} else if ((state.equals(3) && process.equals(20))) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						} else if ((state.equals(1) && process.equals(52)) ||
//								(state.equals(2) && process.equals(5))) {
//							workTasksModel.setQctask(workTasksModel.getQctask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//				}
//				if (uniqRecords != null && !uniqRecords.isEmpty()) {
//					for (WorkTasksModel workTasksModel : uniqRecords.values()) {
//						if (workTasksModel.getTotaltask().equals(0) &&
//								workTasksModel.getEdittask().equals(0) &&
//								workTasksModel.getQctask().equals(0) &&
//								workTasksModel.getChecktask().equals(0) &&
//								workTasksModel.getPrepublishtask().equals(0) &&
//								workTasksModel.getCompletetask().equals(0))
//							continue;
//						
//						try {
//							Integer userid = workTasksModel.getUserid();
//							EmployeeModel record = new EmployeeModel();
//							record.setId(userid);
//							EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(record );
//							if (emp == null)
//								continue;
//							workTasksModel.setUsername(emp.getRealname());
//							workTasksModel.setTotaltask(workTasksModel.getEdittask() + workTasksModel.getChecktask() + workTasksModel.getQctask() + workTasksModel.getPrepublishtask() + workTasksModel.getCompletetask());
//							workTasksModelDao.newWorkTask(workTasksModel);
//						} catch (DuplicateKeyException e) {
//							logger.error(e.getMessage());
//						} catch (Exception e) {
//							logger.error(e.getMessage(), e);
//						}
//					}
//				} else {
//					logger.debug("workTasks has no records.");
//				}
//			} else {
//				logger.error("There's no Attach DB Config.");
//			}
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		if (uniqProcesses != null && !uniqProcesses.isEmpty()) {
//			for (ProjectsProcessModel projectsProcessModel : uniqProcesses.values()) {
//				try {
//					Long processid = projectsProcessModel.getProcessid();
//					ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//					if (process == null)
//						continue;
//					Integer _processType = projectsProcessModel.getProcesstype();
//					String processname = process.getName();
//					Long projectid = projectsProcessModel.getProjectid();
//					Integer totaltask = projectsProcessModel.getTotaltask();
//					Integer edittask = projectsProcessModel.getEdittask();
//					Integer qctask = projectsProcessModel.getQctask();
//					Integer checktask = projectsProcessModel.getChecktask();
//					Integer prepublishtask = projectsProcessModel.getPrepublishtask();
//					Integer completetask = projectsProcessModel.getCompletetask();
//					Integer fielddatacount = projectsProcessModel.getFielddatacount();
//					Integer fielddatarest = projectsProcessModel.getFielddatarest();
//					Integer errorcount = projectsProcessModel.getErrorcount();
//					Integer errorrest = projectsProcessModel.getErrorrest();
//					
//					if (totaltask.equals(0) &&
//						edittask.equals(0) &&
//						qctask.equals(0) &&
//						checktask.equals(0) &&
//						prepublishtask.equals(0) &&
//						completetask.equals(0) &&
//						fielddatacount.equals(0) &&
//						fielddatarest.equals(0) &&
//						errorcount.equals(0) &&
//						errorrest.equals(0))
//						continue;
//					
//					try {
//						projectsProcessModelDao.newProjectsProcess(projectsProcessModel);
//					} catch (DuplicateKeyException e) {
//						logger.error(e.getMessage());
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					try {
//						String sProgress = process.getProgress();
//						ArrayList<String> alProgress = sProgress.length() > 0 ? new ArrayList<String>(Arrays.asList(sProgress.split(","))) : new ArrayList<String>();
//						Integer length = alProgress.size();
//						while (length < CommonConstants.PROCESSCOUNT_ERROR) {
//							alProgress.add("0");
//							length++;
//						}
//						if (_processType.equals(ProcessType.POIEDIT.getValue())) {
//							DecimalFormat df = new DecimalFormat("0.000");
//							if (fielddatacount.compareTo(0) > 0) {
//								alProgress.set(0, df.format((float)(fielddatacount-fielddatarest)*100/fielddatacount));
//							} else {
//								alProgress.set(0, "0");
//							}
//							if (errorcount.compareTo(0) > 0) {
//								alProgress.set(1, df.format((float)(errorcount-errorrest)*100/errorcount));
//							} else {
//								alProgress.set(1, "0");
//							}
//						} else {
//							HashMap<Integer, Integer> stageTaskMap = projectsProcessModel.getStageTaskMap();
//							if (stageTaskMap != null && !stageTaskMap.isEmpty()) {
//								DecimalFormat df = new DecimalFormat("0.000");
//								if (stageTaskMap.containsKey(1)) {
//									alProgress.set(0, df.format((float)(stageTaskMap.get(1)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(2)) {
//									alProgress.set(1, df.format((float)(stageTaskMap.get(2)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(3)) {
//									alProgress.set(2, df.format((float)(stageTaskMap.get(3)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(4)) {
//									alProgress.set(3, df.format((float)(stageTaskMap.get(4)*100)/totaltask));
//								}
//							}
//						}
//						StringBuilder sbProgress = new StringBuilder();
//						for (String p : alProgress) {
//							sbProgress.append(p);
//							sbProgress.append(",");
//						}
//						sbProgress.deleteCharAt(sbProgress.length() - 1);
//						process.setProgress(sbProgress.toString());
//						processModelDao.updateByPrimaryKeySelective(process );
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					if (processname.startsWith("附属表资料专用"))
//						continue;
//					
//					if (totaltask.equals(completetask) &&
//							edittask.equals(0) &&
//							qctask.equals(0) &&
//							checktask.equals(0) &&
//							fielddatarest.equals(0) &&
//							prepublishtask.equals(0) &&
//							errorrest.equals(0)) {
//						process.setState(ProcessState.COMPLETE.getValue());
//						processModelDao.updateByPrimaryKeySelective(process );
//						
//						ProjectModel project = new ProjectModel();
//						project.setId(projectid);
//						project.setOverstate(ProjectState.COMPLETE.getValue());
//						projectModelDao.updateByPrimaryKeySelective(project );
//					}
//				} catch (DuplicateKeyException e) {
//					logger.error(e.getMessage());
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//		} else {
//			logger.debug("projectsProcess has no records.");
//		}
//		logger.debug("ATTACHWITHDATA END");
//	}
	
	/**
	 * 全国质检项目进度监控模块
	 */
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskCOUNTRY() {
//		if (!countryWorktasksEnable.equalsIgnoreCase("true")) {
//			logger.debug("BREAK OUT CAUSE DISABLED");
//			return;
//		}
//		
//		logger.debug("COUNTRY START");
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar calendar = Calendar.getInstance();
//		Date now = new Date();
//		calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
//		String nowStr = sdf.format(calendar.getTime());
//		
//		ProcessType processType = ProcessType.UNKNOWN;
//		Map<Long, ProjectsProcessModel> uniqProcesses = new HashMap<Long, ProjectsProcessModel>();
//		try {
//			processType = ProcessType.COUNTRY;
//			
//			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZHIJIANRENWUKU, processType);
//			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//				
//				List<Map<String, Object>> groupTasks = taskModelDao.groupCountryTasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = -2621656397432390420L;
//				{
//					add(TaskTypeEnum.QC_JIUGONGGE);
//					add(TaskTypeEnum.QC_QUANYU);
//				}});
//				
//				for (Map<String, Object> group : groupTasks) {
//					Long projectid = (Long) group.get("projectid");
//					Integer state = (Integer) group.get("state");
//					Integer process = (Integer) group.get("process");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					{
//						ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//						if (uniqProcesses.containsKey(processid)) {
//							projectsProcessModel = uniqProcesses.get(processid);
//							uniqProcesses.remove(processid);
//						}
//						projectsProcessModel.setProcessid(processid);
//						projectsProcessModel.setProcesstype(processType.getValue());
//						projectsProcessModel.setProjectid(projectid);
//						projectsProcessModel.setTime(nowStr);
//						
//						if (state.equals(12) && process.equals(51)) {
//							projectsProcessModel.setIdletask(projectsProcessModel.getIdletask() + count);
//						} else if ((state.equals(11) && process.equals(52)) ||
//									(state.equals(13) && process.equals(52)) ||
//									(state.equals(14) && process.equals(52)) ||
//									(state.equals(15) && process.equals(52)) ||
//									(state.equals(16) && process.equals(52)) ||
//									(state.equals(17) && process.equals(52)) ||
//									(state.equals(18) && process.equals(52)) ||
//									(state.equals(19) && process.equals(52)) ||
//									(state.equals(22) && process.equals(52)) ||
//									(state.equals(23) && process.equals(52)) ||
//									(state.equals(100) && process.equals(52))) {
//							projectsProcessModel.setQctask(projectsProcessModel.getQctask() + count);
//						} else if ((state.equals(2) && process.equals(52))) {
//							projectsProcessModel.setStageTaskMapByStage(2, projectsProcessModel.getStageTaskMapByStage(2) + count);
//							projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//						}
//						
//						projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//						uniqProcesses.put(processid, projectsProcessModel);
//					}
//				}
//			} else {
//				logger.error("There's no COUNTRY DB Config.");
//			}
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		Set<ProcessModel> proNeedMails = new HashSet<ProcessModel>();
//		if (uniqProcesses != null && !uniqProcesses.isEmpty()) {
//			for (ProjectsProcessModel projectsProcessModel : uniqProcesses.values()) {
//				try {
//					Long processid = projectsProcessModel.getProcessid();
//					ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//					if (process == null)
//						continue;
//					
//					Long projectid = projectsProcessModel.getProjectid();
//					Integer totaltask = projectsProcessModel.getTotaltask();
//					Integer edittask = projectsProcessModel.getEdittask();
//					Integer qctask = projectsProcessModel.getQctask();
//					Integer checktask = projectsProcessModel.getChecktask();
//					Integer completetask = projectsProcessModel.getCompletetask();
//					Integer fielddatacount = projectsProcessModel.getFielddatacount();
//					Integer fielddatarest = projectsProcessModel.getFielddatarest();
//					Integer errorcount = projectsProcessModel.getErrorcount();
//					Integer errorrest = projectsProcessModel.getErrorrest();
//					
//					if (totaltask.equals(0) &&
//						edittask.equals(0) &&
//						qctask.equals(0) &&
//						checktask.equals(0) &&
//						completetask.equals(0) &&
//						fielddatacount.equals(0) &&
//						fielddatarest.equals(0) &&
//						errorcount.equals(0) &&
//						errorrest.equals(0))
//						continue;
//					
//					try {
//						projectsProcessModelDao.newProjectsProcess(projectsProcessModel);
//					} catch (DuplicateKeyException e) {
//						logger.error(e.getMessage());
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					try {
//						String sProgress = process.getProgress();
//						ArrayList<String> alProgress = sProgress.length() > 0 ? new ArrayList<String>(Arrays.asList(sProgress.split(","))) : new ArrayList<String>();
//						Integer length = alProgress.size();
//						while (length < CommonConstants.PROCESSCOUNT_ERROR) {
//							alProgress.add("0");
//							length++;
//						}
//						HashMap<Integer, Integer> stageTaskMap = projectsProcessModel.getStageTaskMap();
//						if (stageTaskMap != null && !stageTaskMap.isEmpty()) {
//							DecimalFormat df = new DecimalFormat("0.000");
//							if (stageTaskMap.containsKey(2)) {
//								alProgress.set(1, df.format((float)(stageTaskMap.get(2)*100)/totaltask));
//							}
//							alProgress.set(2, "0");
//							if (!alProgress.get(3).equals("100")) {
//								alProgress.set(3, "0");
//							}
//							if(!alProgress.get(3).equals("100") &&
//									totaltask.equals(completetask) &&
//									edittask.equals(0) &&
//									qctask.equals(0) &&
//									checktask.equals(0) &&
//									fielddatarest.equals(0) &&
//									errorrest.equals(0)) {
//								proNeedMails.add(process);
//								alProgress.set(3, "100");
//							}
//						}
//						StringBuilder sbProgress = new StringBuilder();
//						for (String p : alProgress) {
//							sbProgress.append(p);
//							sbProgress.append(",");
//						}
//						sbProgress.deleteCharAt(sbProgress.length() - 1);
//						process.setProgress(sbProgress.toString());
//						processModelDao.updateByPrimaryKeySelective(process );
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					if (totaltask.equals(completetask) &&
//							edittask.equals(0) &&
//							qctask.equals(0) &&
//							checktask.equals(0) &&
//							fielddatarest.equals(0) &&
//							errorrest.equals(0)) {
//						process.setState(ProcessState.COMPLETE.getValue());
//						processModelDao.updateByPrimaryKeySelective(process );
//						
//						ProjectModel project = new ProjectModel();
//						project.setId(projectid);
//						project.setOverstate(ProjectState.COMPLETE.getValue());
//						projectModelDao.updateByPrimaryKeySelective(project );
//					}
//				} catch (DuplicateKeyException e) {
//					logger.error(e.getMessage());
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//			
//			if (proNeedMails.size() > 0) {
//				String subject = new String("全国质检项目完成提醒");
//				StringBuilder text = new StringBuilder();
//				text.append("如下项目质检完成：<br>");
//				for (ProcessModel proNeedMail : proNeedMails) {
//					if (!proNeedMail.getType().equals(ProcessType.COUNTRY.getValue()) || !proNeedMail.getState().equals(ProcessState.COMPLETE.getValue()))
//						continue;
//					
//					text.append(String.format("  项目编号：%s，项目名称：%s<br>", proNeedMail.getId(), proNeedMail.getName()));
//				}
//				zMailService.sendRichEmail(subject, text.toString());
//			}
//		} else {
//			logger.debug("projectsProcess has no records.");
//		}
//		
//		logger.debug("COUNTRY END");
//	}
	
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskGENWEB() {
//		
//	}
	
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskPOIEDIT() {
//		if (!poieditWorktasksEnable.equalsIgnoreCase("true")) {
//			logger.debug("BREAK OUT CAUSE DISABLED");
//			return;
//		}
//		
//		logger.debug("POI START");
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar calendar = Calendar.getInstance();
//		Date now = new Date();
//		calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
//		String nowStr = sdf.format(calendar.getTime());
//		
//		ProcessType processType = ProcessType.UNKNOWN;
//		Map<Long, ProjectsProcessModel> uniqProcesses = new HashMap<Long, ProjectsProcessModel>();
//		try {
//			processType = ProcessType.POIEDIT;
//			
//			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//				
//				List<Map<String, Object>> groupEditTasks = taskModelDao.groupPOITasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = 7389125160132771037L;
//				{
//					addAll(TaskTypeEnum.getPoiEditTaskTypes());
//				}});
//				
//				for (Map<String, Object> group : groupEditTasks) {
//					Long projectid = (Long) group.get("projectid");
//					Integer state = (Integer) group.get("state");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//					if (uniqProcesses.containsKey(processid)) {
//						projectsProcessModel = uniqProcesses.get(processid);
//						uniqProcesses.remove(processid);
//					}
//					projectsProcessModel.setProcessid(processid);
//					projectsProcessModel.setProcesstype(processType.getValue());
//					projectsProcessModel.setProjectid(projectid);
//					projectsProcessModel.setTime(nowStr);
//					
//					if (state.equals(0)) {
//						projectsProcessModel.setIdletask(projectsProcessModel.getIdletask() + count);
//					} else if (state.equals(1)) {
//						projectsProcessModel.setEdittask(projectsProcessModel.getEdittask() + count);
//					} else if (state.equals(2)) {
//						projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//					}
//					
//					projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//					uniqProcesses.put(processid, projectsProcessModel);
//				}
//				
//				List<Map<String, Object>> groupCheckTasks = taskModelDao.groupPOITasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = -8886536407502577355L;
//				{
//					addAll(TaskTypeEnum.getPoiCheckTaskTypes());
//				}});
//				
//				for (Map<String, Object> group : groupCheckTasks) {
//					Long projectid = (Long) group.get("projectid");
//					Integer state = (Integer) group.get("state");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//					if (uniqProcesses.containsKey(processid)) {
//						projectsProcessModel = uniqProcesses.get(processid);
//						uniqProcesses.remove(processid);
//					}
//					projectsProcessModel.setProcessid(processid);
//					projectsProcessModel.setProcesstype(processType.getValue());
//					projectsProcessModel.setProjectid(projectid);
//					projectsProcessModel.setTime(nowStr);
//					
//					if (state.equals(0)) {
//						projectsProcessModel.setIdletask(projectsProcessModel.getIdletask() + count);
//					} else if (state.equals(1)) {
//						projectsProcessModel.setChecktask(projectsProcessModel.getChecktask() + count);
//					} else if (state.equals(2)) {
//						projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//					}
//					
//					projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//					uniqProcesses.put(processid, projectsProcessModel);
//				}
//				
//				List<Map<String, Object>> groupErrors = taskModelDao.groupErrors(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = 7389125160132771037L;
//				{
//					addAll(TaskTypeEnum.getPoiEditTaskTypes());
//					addAll(TaskTypeEnum.getPoiCheckTaskTypes());
//				}});
//				groupErrors.addAll(taskModelDao.groupErrorsInCache(configDBModel));
//				
//				for (Map<String, Object> group : groupErrors) {
//					Long projectid = (Long) group.get("projectid");
//					Integer total = ((Long) group.get("total")).intValue();
//					Integer rest = ((Long) group.get("rest")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//					if (uniqProcesses.containsKey(processid)) {
//						projectsProcessModel = uniqProcesses.get(processid);
//						uniqProcesses.remove(processid);
//					}
//					projectsProcessModel.setProcessid(processid);
//					projectsProcessModel.setProcesstype(processType.getValue());
//					projectsProcessModel.setProjectid(projectid);
//					projectsProcessModel.setTime(nowStr);
//					
//					projectsProcessModel.setErrorcount(projectsProcessModel.getErrorcount() + total);
//					projectsProcessModel.setErrorrest(projectsProcessModel.getErrorrest() + rest);
//					uniqProcesses.put(processid, projectsProcessModel);
//				}
//				
//				List<Map<String, Object>> groupFielddatas = taskModelDao.groupFielddatas(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = 3858444676391259930L;
//				{
//					addAll(TaskTypeEnum.getPoiEditTaskTypes());
//					addAll(TaskTypeEnum.getPoiCheckTaskTypes());
//				}});
//				groupFielddatas.addAll(taskModelDao.groupFielddatasInCache(configDBModel));
//				
//				for (Map<String, Object> group : groupFielddatas) {
//					Long projectid = (Long) group.get("projectid");
//					Integer total = ((Long) group.get("total")).intValue();
//					Integer rest = ((Long) group.get("rest")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//					if (uniqProcesses.containsKey(processid)) {
//						projectsProcessModel = uniqProcesses.get(processid);
//						uniqProcesses.remove(processid);
//					}
//					projectsProcessModel.setProcessid(processid);
//					projectsProcessModel.setProcesstype(processType.getValue());
//					projectsProcessModel.setProjectid(projectid);
//					projectsProcessModel.setTime(nowStr);
//					
//					projectsProcessModel.setFielddatacount(projectsProcessModel.getFielddatacount() + total);
//					projectsProcessModel.setFielddatarest(projectsProcessModel.getFielddatarest() + rest);
//					uniqProcesses.put(processid, projectsProcessModel);
//				}
//			} else {
//				logger.error("There's no POI DB Config.");
//			}
//			
//			ProcessConfigModel _config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJISHUJUKU, processType);
//			if (_config != null && _config.getDefaultValue() != null && !_config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(_config.getDefaultValue()));
//				
//				List<Map<String, Object>> groupPOIs = taskModelDao.groupPOIs(configDBModel);
//				for (Map<String, Object> group : groupPOIs) {
//					Long projectid = (Long) group.get("projectid");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//					if (uniqProcesses.containsKey(processid)) {
//						projectsProcessModel = uniqProcesses.get(processid);
//						uniqProcesses.remove(processid);
//					}
//					projectsProcessModel.setProcessid(processid);
//					projectsProcessModel.setProcesstype(processType.getValue());
//					projectsProcessModel.setProjectid(projectid);
//					projectsProcessModel.setTime(nowStr);
//					
//					projectsProcessModel.setPoiunexported(projectsProcessModel.getPoiunexported() + count);
//					uniqProcesses.put(processid, projectsProcessModel);
//				}
//			} else {
//				logger.error("There's no POI BIANJISHUJUKU DB Config.");
//			}
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		if (uniqProcesses != null && !uniqProcesses.isEmpty()) {
//			for (ProjectsProcessModel projectsProcessModel : uniqProcesses.values()) {
//				try {
//					Long processid = projectsProcessModel.getProcessid();
//					ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//					if (process == null)
//						continue;
//					Integer _processType = projectsProcessModel.getProcesstype();
//					Long projectid = projectsProcessModel.getProjectid();
//					Integer totaltask = projectsProcessModel.getTotaltask();
//					Integer edittask = projectsProcessModel.getEdittask();
//					Integer qctask = projectsProcessModel.getQctask();
//					Integer checktask = projectsProcessModel.getChecktask();
//					Integer completetask = projectsProcessModel.getCompletetask();
//					Integer fielddatacount = projectsProcessModel.getFielddatacount();
//					Integer fielddatarest = projectsProcessModel.getFielddatarest();
//					Integer errorcount = projectsProcessModel.getErrorcount();
//					Integer errorrest = projectsProcessModel.getErrorrest();
//					
//					if (totaltask.equals(0) &&
//						edittask.equals(0) &&
//						qctask.equals(0) &&
//						checktask.equals(0) &&
//						completetask.equals(0) &&
//						fielddatacount.equals(0) &&
//						fielddatarest.equals(0) &&
//						errorcount.equals(0) &&
//						errorrest.equals(0))
//						continue;
//					
//					try {
//						projectsProcessModelDao.newProjectsProcess(projectsProcessModel);
//					} catch (DuplicateKeyException e) {
//						logger.error(e.getMessage());
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					try {
//						String sProgress = process.getProgress();
//						ArrayList<String> alProgress = sProgress.length() > 0 ? new ArrayList<String>(Arrays.asList(sProgress.split(","))) : new ArrayList<String>();
//						Integer length = alProgress.size();
//						while (length < CommonConstants.PROCESSCOUNT_ERROR) {
//							alProgress.add("0");
//							length++;
//						}
//						if (_processType.equals(ProcessType.POIEDIT.getValue())) {
//							DecimalFormat df = new DecimalFormat("0.000");
//							if (fielddatacount.compareTo(0) > 0) {
//								alProgress.set(0, df.format((float)(fielddatacount-fielddatarest)*100/fielddatacount));
//							} else {
//								alProgress.set(0, "0");
//							}
//							if (errorcount.compareTo(0) > 0) {
//								alProgress.set(1, df.format((float)(errorcount-errorrest)*100/errorcount));
//							} else {
//								alProgress.set(1, "0");
//							}
//						} else {
//							HashMap<Integer, Integer> stageTaskMap = projectsProcessModel.getStageTaskMap();
//							if (stageTaskMap != null && !stageTaskMap.isEmpty()) {
//								DecimalFormat df = new DecimalFormat("0.000");
//								if (stageTaskMap.containsKey(1)) {
//									alProgress.set(0, df.format((float)(stageTaskMap.get(1)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(2)) {
//									alProgress.set(1, df.format((float)(stageTaskMap.get(2)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(3)) {
//									alProgress.set(2, df.format((float)(stageTaskMap.get(3)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(4)) {
//									alProgress.set(3, df.format((float)(stageTaskMap.get(4)*100)/totaltask));
//								}
//							}
//						}
//						StringBuilder sbProgress = new StringBuilder();
//						for (String p : alProgress) {
//							sbProgress.append(p);
//							sbProgress.append(",");
//						}
//						sbProgress.deleteCharAt(sbProgress.length() - 1);
//						process.setProgress(sbProgress.toString());
//						processModelDao.updateByPrimaryKeySelective(process );
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					if (processType.equals(ProcessType.POIEDIT))
//						continue;
//					
//					if (totaltask.equals(completetask) &&
//							edittask.equals(0) &&
//							qctask.equals(0) &&
//							checktask.equals(0) &&
//							fielddatarest.equals(0) &&
//							errorrest.equals(0)) {
//						process.setState(ProcessState.COMPLETE.getValue());
//						processModelDao.updateByPrimaryKeySelective(process );
//						
//						ProjectModel project = new ProjectModel();
//						project.setId(projectid);
//						project.setOverstate(ProjectState.COMPLETE.getValue());
//						projectModelDao.updateByPrimaryKeySelective(project );
//					}
//				} catch (DuplicateKeyException e) {
//					logger.error(e.getMessage());
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//		} else {
//			logger.debug("projectsProcess has no records.");
//		}
//		
//		logger.debug("POI END");
//	}
//	@Scheduled(cron = "${scheduler.worktasks.dotime}")
//	public void worktasksDoTaskADJUSTMAP() {
//		if (!adjustmapWorktasksEnable.equalsIgnoreCase("true")) {
//			logger.debug("BREAK OUT CAUSE DISABLED");
//			return;
//		}
//		
//		logger.debug("ADJUSTMAP START");
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar calendar = Calendar.getInstance();
//		Date now = new Date();
//		calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
//		String nowStr = sdf.format(calendar.getTime());
//		
//		ProcessType processType = ProcessType.UNKNOWN;
//		Map<Long, ProjectsProcessModel> uniqProcesses = new HashMap<Long, ProjectsProcessModel>();
//		try {
//			processType = ProcessType.ADJUSTMAP;
//			
//			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
//			if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
//				ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
//				List<Map<String, Object>> groups = taskModelDao.groupTasks(configDBModel, new ArrayList<TaskTypeEnum>() {
//					private static final long serialVersionUID = -2869201154554097L;
//				{
//					add(TaskTypeEnum.ADJUSTMAP);
//				}});
//				Map<WorkTasksUniq, WorkTasksModel> uniqRecords = new HashMap<WorkTasksUniq, WorkTasksModel>();
//				for (Map<String, Object> group : groups) {
//					Long projectid = (Long) group.get("projectid");
//					Integer state = (Integer) group.get("state");
//					Integer process = (Integer) group.get("process");
//					Integer editid = (Integer) group.get("editid");
//					Integer checkid = (Integer) group.get("checkid");
//					Integer count = ((Long) group.get("count")).intValue();
//					
//					ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
//					if (project == null || project.getProcessid() == null || project.getProcessid().compareTo(0L) < 0)
//						continue;
//					
//					Long processid = project.getProcessid();
//					{
//						ProjectsProcessModel projectsProcessModel = new ProjectsProcessModel();
//						if (uniqProcesses.containsKey(processid)) {
//							projectsProcessModel = uniqProcesses.get(processid);
//							uniqProcesses.remove(processid);
//						}
//						projectsProcessModel.setProcessid(processid);
//						projectsProcessModel.setProcesstype(processType.getValue());
//						projectsProcessModel.setProjectid(projectid);
//						projectsProcessModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							projectsProcessModel.setIdletask(projectsProcessModel.getIdletask() + count);
//						} else if ((state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6))) {
//							projectsProcessModel.setEdittask(projectsProcessModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(1) && process.equals(6))) {
//							projectsProcessModel.setChecktask(projectsProcessModel.getChecktask() + count);
//						} else if ((state.equals(3) && process.equals(6))) {
//							projectsProcessModel.setCompletetask(projectsProcessModel.getCompletetask() + count);
//							projectsProcessModel.setStageTaskMapByStage(1, projectsProcessModel.getStageTaskMapByStage(1) + count);
//						}
//						
//						projectsProcessModel.setTotaltask(projectsProcessModel.getTotaltask() + count);
//						uniqProcesses.put(processid, projectsProcessModel);
//					}
//					
//					if (editid != null && editid.compareTo(0) > 0) {
//						editid = editid.compareTo(500000) > 0 ? (editid - 500000) : editid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(editid, RoleType.ROLE_WORKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(editid);
//						workTasksModel.setRoleid(RoleType.ROLE_WORKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_WORKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							
//						} else if ((state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6))) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(1) && process.equals(6))) {
//							workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//						} else if (state.equals(3) && process.equals(6)) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//					if (checkid != null && checkid.compareTo(0) > 0) {
//						checkid = checkid.compareTo(600000) > 0 ? (checkid - 600000) : checkid;
//						WorkTasksUniq uniqRecord = new WorkTasksUniq(checkid, RoleType.ROLE_CHECKER.getValue(), processid);
//						WorkTasksModel workTasksModel = new WorkTasksModel();
//						if(uniqRecords.containsKey(uniqRecord)) {
//							workTasksModel = uniqRecords.get(uniqRecord);
//							uniqRecords.remove(uniqRecord);
//						}
//						workTasksModel.setUserid(checkid);
//						workTasksModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
//						workTasksModel.setRolename(RoleType.ROLE_CHECKER.getDes());
//						workTasksModel.setProcesstype(processType.getValue());
//						workTasksModel.setProcessid(processid);
//						workTasksModel.setTime(nowStr);
//						
//						if (state.equals(0) && process.equals(0)) {
//							
//						} else if ((state.equals(1) && process.equals(5)) ||
//								(state.equals(2) && process.equals(6))) {
//							workTasksModel.setEdittask(workTasksModel.getEdittask() + count);
//						} else if ((state.equals(3) && process.equals(5)) ||
//								(state.equals(1) && process.equals(6))) {
//							workTasksModel.setChecktask(workTasksModel.getChecktask() + count);
//						} else if (state.equals(3) && process.equals(6)) {
//							workTasksModel.setCompletetask(workTasksModel.getCompletetask() + count);
//						}
//						
//						uniqRecords.put(uniqRecord, workTasksModel);
//					}
//					
//				}
//				if (uniqRecords != null && !uniqRecords.isEmpty()) {
//					for (WorkTasksModel workTasksModel : uniqRecords.values()) {
//						if (workTasksModel.getTotaltask().equals(0) &&
//								workTasksModel.getEdittask().equals(0) &&
//								workTasksModel.getQctask().equals(0) &&
//								workTasksModel.getChecktask().equals(0) &&
//								workTasksModel.getCompletetask().equals(0))
//							continue;
//						
//						try {
//							Integer userid = workTasksModel.getUserid();
//							EmployeeModel record = new EmployeeModel();
//							record.setId(userid);
//							EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(record );
//							if (emp == null)
//								continue;
//							workTasksModel.setUsername(emp.getRealname());
//							workTasksModel.setTotaltask(workTasksModel.getEdittask() + workTasksModel.getChecktask() + workTasksModel.getQctask() + workTasksModel.getCompletetask());
//							workTasksModelDao.newWorkTask(workTasksModel);
//						} catch (DuplicateKeyException e) {
//							logger.error(e.getMessage());
//						} catch (Exception e) {
//							logger.error(e.getMessage(), e);
//						}
//					}
//				} else {
//					logger.debug("workTasks has no records.");
//				}
//			} else {
//				logger.error("There's no Attach DB Config.");
//			}
//		} catch(Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		if (uniqProcesses != null && !uniqProcesses.isEmpty()) {
//			for (ProjectsProcessModel projectsProcessModel : uniqProcesses.values()) {
//				try {
//					Long processid = projectsProcessModel.getProcessid();
//					ProcessModel process = processModelDao.selectByPrimaryKey(processid);
//					if (process == null)
//						continue;
//					Integer _processType = projectsProcessModel.getProcesstype();
//					String processname = process.getName();
//					Long projectid = projectsProcessModel.getProjectid();
//					Integer totaltask = projectsProcessModel.getTotaltask();
//					Integer edittask = projectsProcessModel.getEdittask();
//					Integer qctask = projectsProcessModel.getQctask();
//					Integer checktask = projectsProcessModel.getChecktask();
//					Integer completetask = projectsProcessModel.getCompletetask();
//					Integer fielddatacount = projectsProcessModel.getFielddatacount();
//					Integer fielddatarest = projectsProcessModel.getFielddatarest();
//					Integer errorcount = projectsProcessModel.getErrorcount();
//					Integer errorrest = projectsProcessModel.getErrorrest();
//					
//					if (totaltask.equals(0) &&
//						edittask.equals(0) &&
//						qctask.equals(0) &&
//						checktask.equals(0) &&
//						completetask.equals(0) &&
//						fielddatacount.equals(0) &&
//						fielddatarest.equals(0) &&
//						errorcount.equals(0) &&
//						errorrest.equals(0))
//						continue;
//					
//					try {
//						projectsProcessModelDao.newProjectsProcess(projectsProcessModel);
//					} catch (DuplicateKeyException e) {
//						logger.error(e.getMessage());
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//					
//					try {
//						String sProgress = process.getProgress();
//						ArrayList<String> alProgress = sProgress.length() > 0 ? new ArrayList<String>(Arrays.asList(sProgress.split(","))) : new ArrayList<String>();
//						Integer length = alProgress.size();
//						while (length < CommonConstants.PROCESSCOUNT_ERROR) {
//							alProgress.add("0");
//							length++;
//						}
//						if (_processType.equals(ProcessType.POIEDIT.getValue())) {
//							DecimalFormat df = new DecimalFormat("0.000");
//							if (fielddatacount.compareTo(0) > 0) {
//								alProgress.set(0, df.format((float)(fielddatacount-fielddatarest)*100/fielddatacount));
//							} else {
//								alProgress.set(0, "0");
//							}
//							if (errorcount.compareTo(0) > 0) {
//								alProgress.set(1, df.format((float)(errorcount-errorrest)*100/errorcount));
//							} else {
//								alProgress.set(1, "0");
//							}
//						} else {
//							HashMap<Integer, Integer> stageTaskMap = projectsProcessModel.getStageTaskMap();
//							if (stageTaskMap != null && !stageTaskMap.isEmpty()) {
//								DecimalFormat df = new DecimalFormat("0.000");
//								if (stageTaskMap.containsKey(1)) {
//									alProgress.set(0, df.format((float)(stageTaskMap.get(1)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(2)) {
//									alProgress.set(1, df.format((float)(stageTaskMap.get(2)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(3)) {
//									alProgress.set(2, df.format((float)(stageTaskMap.get(3)*100)/totaltask));
//								}
//								if (stageTaskMap.containsKey(4)) {
//									alProgress.set(3, df.format((float)(stageTaskMap.get(4)*100)/totaltask));
//								}
//							}
//						}
//						StringBuilder sbProgress = new StringBuilder();
//						for (String p : alProgress) {
//							sbProgress.append(p);
//							sbProgress.append(",");
//						}
//						sbProgress.deleteCharAt(sbProgress.length() - 1);
//						process.setProgress(sbProgress.toString());
//						processModelDao.updateByPrimaryKeySelective(process );
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//					}
//				} catch (DuplicateKeyException e) {
//					logger.error(e.getMessage());
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//				}
//			}
//		} else {
//			logger.debug("projectsProcess has no records.");
//		}
//		
//		logger.debug("ADJUSTMAP END");
//	}
//	
//	@Scheduled(cron = "${scheduler.attachcapacity.dotime}")
//	public void attachCapacityDoTask() {
//		if (!attachEnable.equalsIgnoreCase("true"))
//			return;
//		String date = getDateString(new Date());
//		attachCapacityDao.deleteCapacityByCountdate(date);
//		//制作
//		attachCapacityDao.doAttachCapacityTask(date); 
//	}
	
	/**
	 * 用来统计附属表校正
	 * @param date
	 */
//	@Scheduled(cron = "${scheduler.attachcapacity.dotime}")
//	public void countAttachError() {
//		if (!attachEnable.equalsIgnoreCase("true"))
//			return;
//		String date = getDateString(new Date());
//		attachCheckCapacityDao.deleteByCountDate(date);
//		//校正
//		attachCheckCapacityDao.doAttachCheckCapacityTask(date);
//	}
	
	/**
	 * 用来统一更新附属表中统计结果的用户名
	 * 在统计的时候由于统计是在存储过程中统计，不能跨数据库，所以统计的时候没有在表中插入用户名，此方法用来统一更新用户名
	 */
//	@Scheduled(cron = "${scheduler.attachcapacity.updateuser.dotime}")
//	public void updateCountUserName() {
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
		
//		if(  1 > 0)
//			return ;
		
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
				return 5;
			}else if(  (flag&4)==4 ) { // 未质检 
				return 3;// 继续找下个任务
			}else if( (flag & 8 ) == 8) {//质检错误
				// 存在待修改的质检错误
				if (taskModelClient.updateModifyTask(taskid, userid, 0, 6).compareTo(0L) <= 0) {
					logger.debug("修改任务状态失败0,6");
				}
				return 4;// 找到作业任务
				
			}else if( (flag & 2 ) == 2) {//质检OK
				// 质检OK 设置任务状态 3,6
				if (taskModelClient.submitModifyTask(taskid, userid, 3).compareTo(0L) <= 0) {
					// json.addObject("resultMsg", "任务提交失败");
				}
				return 2;// 继续找下个任务
			}else if( (flag & 32 ) == 32) {//其他系统占用
				taskModelClient.submitModifyTask(taskid, userid, 3);
				return 7;
			}else if( (flag & 1 ) == 1) {// 任务下不存在点
				// 关联POI不存在，任务设置质检完成 ?
				if (taskModelClient.submitModifyTask(taskid, userid, 3) <= 0) {
					// 修改状态失败
					logger.debug("修改任务状态失败3,6");
				}
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
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              		logger.debug("####scanfModifyTask()##start#####");
//		if( 1> 0)
//			return;
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
			//for test
			if( 1> 0)
				return;
			
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
	
	@Scheduled(cron = "${scheduler.attachcapacity.updateuser.dotime}")
	public void loadMakeTask() {
		if (!attachEnable.equalsIgnoreCase("true"))
			return;
		logger.debug("start update username.");
		AttachCapacityModelExample example = new AttachCapacityModelExample();
		AttachCapacityModelExample.Criteria criteria = example.or();
		criteria.andCountDate(getDateString(new Date()));
		List<AttachMakeCapacityModel> makes = attachCapacityDao.selectAttachCapacity(example);
		List<EmployeeModel> users = emapgoAccountService.getAllEmployees();
		if (makes != null && users != null) {
			for(AttachMakeCapacityModel make : makes) {
				for(EmployeeModel user : users) {
					if(make.getUserid() == user.getId()) {
						make.setUsername(user.getRealname());
					}
				}
			}
			attachCapacityDao.updateUserName(makes);
		}
		
		List<AttachCheckCapacityModel> checks = attachCheckCapacityDao.selectcheckAttachCapacity(example);
		
		if (checks != null && users != null) {
			for(AttachCheckCapacityModel check : checks) {
				for(EmployeeModel user : users) {
					if(check.getUserid() == user.getId()) {
						check.setUsername(user.getRealname());
					}
				}
			}
			attachCheckCapacityDao.updateUserName(checks);
		}
		
	}
	
	/*
	 * 定时扫描任务库 ,更新进度
	 * */
	@Scheduled(cron = "${schedulerpoi.updateprojectprogress.dotime}")
	public void updateProjectProgress() {
		logger.debug("####scanfModifyTask()##start#####");
		
		if(1>0)
			return ;
		
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
