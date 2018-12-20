package com.emg.projectsmanage.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.CapacityTaskStateEnum;
import com.emg.projectsmanage.common.TaskTypeEnum;
import com.emg.projectsmanage.common.ProcessConfigEnum;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.common.SystemType;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.dao.projectsmanager.CapacityModelDao;
import com.emg.projectsmanage.dao.projectsmanager.CapacityTaskModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsTaskCountModelDao;
import com.emg.projectsmanage.dao.task.TaskBlockDetailModelDao;
import com.emg.projectsmanage.dao.task.TaskLinkErrorModelDao;
import com.emg.projectsmanage.dao.task.TaskLinkFielddataModelDao;
import com.emg.projectsmanage.dao.task.TaskModelDao;
import com.emg.projectsmanage.pojo.CapacityModel;
import com.emg.projectsmanage.pojo.CapacityTaskModel;
import com.emg.projectsmanage.pojo.CapacityTaskModelExample;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.pojo.ProjectsTaskCountModel;
import com.emg.projectsmanage.pojo.ProjectsTaskCountUniq;
import com.emg.projectsmanage.pojo.TaskModel;
import com.emg.projectsmanage.service.EmapgoAccountService;
import com.emg.projectsmanage.service.ProcessConfigModelService;
import com.emg.projectsmanage.pojo.CapacityTaskModelExample.Criteria;
import com.emg.projectsmanage.pojo.CapacityUniq;

@Component
public class SchedulerTask {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

	@Value("${scheduler.capacity.enable}")
	private String capacityEnable;

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
	private ProjectsTaskCountModelDao projectsTaskCountDao;

	/**
	 * 半夜三更 创建每天的任务
	 */
	@Scheduled(cron = "${scheduler.capacity.createtime}")
	public void capacityCreateTask() {
		if (!capacityEnable.equalsIgnoreCase("true"))
			return;

		Date now = new Date();
		String time = new SimpleDateFormat("yyyy-MM-dd").format(now);
		try {
			CapacityTaskModel record = new CapacityTaskModel();
			for (ProcessType processType : ProcessType.values()) {
				if (processType.equals(ProcessType.UNKNOWN))
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
	@Scheduled(cron = "${scheduler.capacity.dotime}")
	public void capacityDoTask() {
		if (!capacityEnable.equalsIgnoreCase("true"))
			return;

		try {
			CapacityTaskModelExample example = new CapacityTaskModelExample();
			Criteria criteria = example.or();
			criteria.andStateEqualTo(CapacityTaskStateEnum.NEW.getValue());
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

					String time = newCapacityTask.getTime();

					if (processType.equals(ProcessType.POIEDIT)) {
						logger.debug(String.format("Scheduler POIEDIT task( %s ) started.", time));

						ProcessConfigModel config = processConfigModelService
								.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
						if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
							ConfigDBModel configDBModel = configDBModelDao
									.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
							
							Map<CapacityUniq, CapacityModel> uniqRecords = new HashMap<CapacityUniq, CapacityModel>();
							
							List<Map<String, Object>> taskGroups = taskModelDao.groupTasksByTime(configDBModel, time);
							if (taskGroups != null && taskGroups.size() > 0) {
								for (Map<String, Object> taskGroup : taskGroups) {
									Integer taskType = (Integer) taskGroup.get("tasktype");
									Long projectid = (Long) taskGroup.get("projectid");
									Integer editid = (Integer) taskGroup.get("editid");
									Long editnum = (Long) taskGroup.get("editnum");
									Integer checkid = (Integer) taskGroup.get("checkid");
									Long checknum = (Long) taskGroup.get("checknum");
									
									CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
									CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
									CapacityModel editCapacityModel = new CapacityModel();
									if(uniqRecords.containsKey(editUniqRecord)) {
										editCapacityModel = uniqRecords.get(editUniqRecord);
										uniqRecords.remove(editUniqRecord);
									}
									CapacityModel checkCapacityModel = new CapacityModel();
									if(uniqRecords.containsKey(checkUniqRecord)) {
										checkCapacityModel = uniqRecords.get(checkUniqRecord);
										uniqRecords.remove(checkUniqRecord);
									}
									
									ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
									editCapacityModel.setProjectid(projectid);
									checkCapacityModel.setProjectid(projectid);
									if (project != null) {
										Long processid = project.getProcessid();
										ProcessModel process = processModelDao.selectByPrimaryKey(processid);
										if (process != null) {
											editCapacityModel.setProcessid(processid);
											editCapacityModel.setProcessname(process.getName());
											checkCapacityModel.setProcessid(processid);
											checkCapacityModel.setProcessname(process.getName());
										}
									}
									
									editCapacityModel.setTasktype(taskType);
									checkCapacityModel.setTasktype(taskType);
	
									editCapacityModel.setUserid(editid);
									checkCapacityModel.setUserid(checkid);
									EmployeeModel erecord = new EmployeeModel();
									erecord.setId(editid);
									EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
									if(emp != null)
										editCapacityModel.setUsername(emp.getRealname());
									erecord.setId(checkid);
									emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
									if(emp != null)
										checkCapacityModel.setUsername(emp.getRealname());
	
									editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
									editCapacityModel.setTime(time);
									checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
									checkCapacityModel.setTime(time);
									
									editCapacityModel.setTaskcount(editCapacityModel.getTaskcount() + editnum);
									checkCapacityModel.setTaskcount(checkCapacityModel.getTaskcount() + checknum);
									
									uniqRecords.put(editUniqRecord, editCapacityModel);
									uniqRecords.put(checkUniqRecord, checkCapacityModel);
								}
							}
							
							List<Map<String, Object>> taskBlockDetailGroups = taskBlockDetailModelDao.groupTaskBlockDetailsByTime(configDBModel, time);
							if (taskBlockDetailGroups != null && taskBlockDetailGroups.size() > 0) {
								for (Map<String, Object> taskBlockDetailGroup : taskBlockDetailGroups) {
									Long blockid = (Long) taskBlockDetailGroup.get("blockid");
									Integer editid = (Integer) taskBlockDetailGroup.get("editid");
									Long editnum = (Long) taskBlockDetailGroup.get("editnum");
									Integer checkid = (Integer) taskBlockDetailGroup.get("checkid");
									Long checknum = (Long) taskBlockDetailGroup.get("checknum");
									
									TaskModel task = taskModelDao.getTaskByBlockid(configDBModel, blockid);
									
									if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
										logger.error("Can not find task by blockid: " + blockid);
										continue;
									}
									
									Integer taskType = task.getTasktype();
									if (taskType.compareTo(0) <= 0)
										continue;
									Long projectid = task.getProjectid();
									if (projectid.compareTo(0L) <= 0)
										continue;
									
									CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, editid);
									CapacityUniq checkUniqRecord = new CapacityUniq(taskType, projectid, checkid);
									CapacityModel editCapacityModel = new CapacityModel();
									if(uniqRecords.containsKey(editUniqRecord)) {
										editCapacityModel = uniqRecords.get(editUniqRecord);
										uniqRecords.remove(editUniqRecord);
									}
									CapacityModel checkCapacityModel = new CapacityModel();
									if(uniqRecords.containsKey(checkUniqRecord)) {
										checkCapacityModel = uniqRecords.get(checkUniqRecord);
										uniqRecords.remove(checkUniqRecord);
									}
									
									ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
									editCapacityModel.setProjectid(projectid);
									checkCapacityModel.setProjectid(projectid);
									if (project != null) {
										Long processid = project.getProcessid();
										ProcessModel process = processModelDao.selectByPrimaryKey(processid);
										if (process != null) {
											editCapacityModel.setProcessid(processid);
											editCapacityModel.setProcessname(process.getName());
											checkCapacityModel.setProcessid(processid);
											checkCapacityModel.setProcessname(process.getName());
										}
									}
									
									editCapacityModel.setTasktype(taskType);
									checkCapacityModel.setTasktype(taskType);
	
									editCapacityModel.setUserid(editid);
									checkCapacityModel.setUserid(checkid);
									EmployeeModel erecord = new EmployeeModel();
									erecord.setId(editid);
									EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
									if(emp != null)
										editCapacityModel.setUsername(emp.getRealname());
									erecord.setId(checkid);
									emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
									if(emp != null)
										checkCapacityModel.setUsername(emp.getRealname());
	
									editCapacityModel.setRoleid(RoleType.ROLE_WORKER.getValue());
									editCapacityModel.setTime(time);
									checkCapacityModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
									checkCapacityModel.setTime(time);
									
									editCapacityModel.setModifypoi(editCapacityModel.getModifypoi() + editnum);
									checkCapacityModel.setModifypoi(checkCapacityModel.getModifypoi() + checknum);
									
									uniqRecords.put(editUniqRecord, editCapacityModel);
									uniqRecords.put(checkUniqRecord, checkCapacityModel);
								}
							}
							
							List<Map<String, Object>> taskLinkErrorGroups = taskLinkErrorModelDao.groupTaskLinkErrorByTime(configDBModel, time);
							if (taskLinkErrorGroups != null && taskLinkErrorGroups.size() > 0) {
								for (Map<String, Object> taskLinkErrorGroup : taskLinkErrorGroups) {
									Long taskid = (Long) taskLinkErrorGroup.get("taskid");
									Long errorcount = (Long) taskLinkErrorGroup.get("errorcount");
									Long visualerrorcount = (Long) taskLinkErrorGroup.get("visualerrorcount");
									
									TaskModel task = taskModelDao.getTaskByID(configDBModel, taskid);
									
									if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
										logger.error("Can not find task by taskid: " + taskid);
										continue;
									}
									
									Integer userid = 0;
									Integer roleid = RoleType.UNKNOWN.getValue();
	
									Integer taskType = task.getTasktype();
									if (taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) || // 非实测
											taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) || // 全国质检改错
											taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())// 地址电话改错
											|| taskType.equals(TaskTypeEnum.POI_KETOU.getValue()) || // 客投制作
											taskType.equals(TaskTypeEnum.POI_GEN.getValue())) {// 易淘金制作
										userid = task.getEditid();
										roleid = RoleType.ROLE_WORKER.getValue();
									} else if (taskType.equals(TaskTypeEnum.POI_MC_KETOU.getValue()) || // 客投校正
											taskType.equals(TaskTypeEnum.POI_MC_GEN.getValue())) {// 易淘金校正
										userid = task.getCheckid();
										roleid = RoleType.ROLE_CHECKER.getValue();
									} else {
										continue;
									}
									
									Long projectid = task.getProjectid();
									if (projectid.compareTo(0L) <= 0)
										continue;
									
									CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, userid);
									CapacityModel editCapacityModel = new CapacityModel();
									if(uniqRecords.containsKey(editUniqRecord)) {
										editCapacityModel = uniqRecords.get(editUniqRecord);
										uniqRecords.remove(editUniqRecord);
									}
									
									ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
									editCapacityModel.setProjectid(projectid);
									if (project != null) {
										Long processid = project.getProcessid();
										ProcessModel process = processModelDao.selectByPrimaryKey(processid);
										if (process != null) {
											editCapacityModel.setProcessid(processid);
											editCapacityModel.setProcessname(process.getName());
										}
									}
									
									editCapacityModel.setTasktype(taskType);
	
									editCapacityModel.setUserid(userid);
									EmployeeModel erecord = new EmployeeModel();
									erecord.setId(userid);
									EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
									if(emp != null)
										editCapacityModel.setUsername(emp.getRealname());
	
									editCapacityModel.setRoleid(roleid);
									editCapacityModel.setTime(time);
									
									editCapacityModel.setErrorcount(editCapacityModel.getErrorcount() + errorcount);
									editCapacityModel.setVisualerrorcount(editCapacityModel.getVisualerrorcount() + visualerrorcount);
									
									uniqRecords.put(editUniqRecord, editCapacityModel);
								}
							}
							
							List<Map<String, Object>> taskLinkFielddataGroups = taskLinkFielddataModelDao.groupTaskLinkFielddataByTime(configDBModel, time);
							if (taskLinkFielddataGroups != null && taskLinkFielddataGroups.size() > 0) {
								for (Map<String, Object> taskLinkFielddataGroup : taskLinkFielddataGroups) {
									Long taskid = (Long) taskLinkFielddataGroup.get("taskid");
									Long count = (Long) taskLinkFielddataGroup.get("count");
									
									TaskModel task = taskModelDao.getTaskByID(configDBModel, taskid);
									
									if (task == null || task.getId() == null || task.getId().compareTo(0L) <= 0) {
										logger.error("Can not find task by taskid: " + taskid);
										continue;
									}
									
									Integer userid = 0;
									Integer roleid = RoleType.UNKNOWN.getValue();
	
									Integer taskType = task.getTasktype();
									if (taskType.equals(TaskTypeEnum.POI_FEISHICE.getValue()) || // 非实测
											taskType.equals(TaskTypeEnum.POI_QUANGUOQC.getValue()) || // 全国质检改错
											taskType.equals(TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue())// 地址电话改错
											|| taskType.equals(TaskTypeEnum.POI_KETOU.getValue()) || // 客投制作
											taskType.equals(TaskTypeEnum.POI_GEN.getValue())) {// 易淘金制作
										userid = task.getEditid();
										roleid = RoleType.ROLE_WORKER.getValue();
									} else if (taskType.equals(TaskTypeEnum.POI_MC_KETOU.getValue()) || // 客投校正
											taskType.equals(TaskTypeEnum.POI_MC_GEN.getValue())) {// 易淘金校正
										userid = task.getCheckid();
										roleid = RoleType.ROLE_CHECKER.getValue();
									} else {
										continue;
									}
									
									Long projectid = task.getProjectid();
									if (projectid.compareTo(0L) <= 0)
										continue;
									
									CapacityUniq editUniqRecord = new CapacityUniq(taskType, projectid, userid);
									CapacityModel editCapacityModel = new CapacityModel();
									if(uniqRecords.containsKey(editUniqRecord)) {
										editCapacityModel = uniqRecords.get(editUniqRecord);
										uniqRecords.remove(editUniqRecord);
									}
									
									ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
									editCapacityModel.setProjectid(projectid);
									if (project != null) {
										Long processid = project.getProcessid();
										ProcessModel process = processModelDao.selectByPrimaryKey(processid);
										if (process != null) {
											editCapacityModel.setProcessid(processid);
											editCapacityModel.setProcessname(process.getName());
										}
									}
									
									editCapacityModel.setTasktype(taskType);
	
									editCapacityModel.setUserid(userid);
									EmployeeModel erecord = new EmployeeModel();
									erecord.setId(userid);
									EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(erecord);
									if(emp != null)
										editCapacityModel.setUsername(emp.getRealname());
	
									editCapacityModel.setRoleid(roleid);
									editCapacityModel.setTime(time);
									
									editCapacityModel.setFielddatacount(editCapacityModel.getFielddatacount() + count);
									
									uniqRecords.put(editUniqRecord, editCapacityModel);
								}
							}

							if (uniqRecords != null && !uniqRecords.isEmpty()) {
								for (CapacityModel capacityModel : uniqRecords.values()) {
									if (capacityModel.getErrorcount().equals(0L)
											&& capacityModel.getTaskcount().equals(0L)
											&& capacityModel.getModifypoi().equals(0L)
											&& capacityModel.getCreatepoi().equals(0L)
											&& capacityModel.getDeletepoi().equals(0L)
											&& capacityModel.getConfirmpoi().equals(0L)
											&& capacityModel.getVisualerrorcount().equals(0L)
											&& capacityModel.getFielddatacount().equals(0L))
										continue;
									capacityModelDao.insert(capacityModel);
								}
							}

							logger.debug(
									String.format("Scheduler POIEDIT task( %s ) finished.", newCapacityTask.getTime()));

							record = new CapacityTaskModel();
							record.setId(curCapacityTaskID);
							record.setState(CapacityTaskStateEnum.FINISHED.getValue());
							capacityTaskModelDao.updateByPrimaryKeySelective(record);
						} else {
							logger.error("Scheduler POIEDIT task( %s ) has no configs.");
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
	
	@Scheduled(cron = "${scheduler.worktasks.dotime}")
	public void worktasksDoTask() {
		try {
			logger.debug("START");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar calendar = Calendar.getInstance();
			Date now = new Date();
			calendar.setTimeInMillis(now.getTime() - (now.getTime()%(600000)));
			String nowStr = sdf.format(calendar.getTime());
			
			ProcessType processType = ProcessType.UNKNOWN;
			SystemType systemType = SystemType.Unknow;
			{
				processType = ProcessType.ATTACH;
				systemType =SystemType.MapDbEdit_Attach;
				
				ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.BIANJIRENWUKU, processType);
				if (config != null && config.getDefaultValue() != null && !config.getDefaultValue().isEmpty()) {
					ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
					List<Map<String, Object>> groups = taskModelDao.groupTasks(configDBModel);
					Map<ProjectsTaskCountUniq, ProjectsTaskCountModel> uniqRecords = new HashMap<ProjectsTaskCountUniq, ProjectsTaskCountModel>();
					for (Map<String, Object> group : groups) {
						Integer systemid = systemType.getValue();
						
						Long projectid = (Long) group.get("projectid");
						Integer state = (Integer) group.get("state");
						Integer process = (Integer) group.get("process");
						Integer editid = (Integer) group.get("editid");
						Integer checkid = (Integer) group.get("checkid");
						Integer count = ((Long) group.get("count")).intValue();
						
						if (count.compareTo(0) <= 0)
							continue;
						
						if (editid != null && editid.compareTo(0) > 0) {
							editid = editid.compareTo(500000) > 0 ? (editid - 500000) : editid;
							ProjectsTaskCountUniq uniqRecord = new ProjectsTaskCountUniq(editid, RoleType.ROLE_WORKER.getValue(), systemid, projectid);
							ProjectsTaskCountModel projectsTaskCountModel = new ProjectsTaskCountModel();
							if(uniqRecords.containsKey(uniqRecord)) {
								projectsTaskCountModel = uniqRecords.get(uniqRecord);
								uniqRecords.remove(uniqRecord);
							}
							projectsTaskCountModel.setUserid(editid);
							projectsTaskCountModel.setRoleid(RoleType.ROLE_WORKER.getValue());
							projectsTaskCountModel.setRolename(RoleType.ROLE_WORKER.getDes());
							projectsTaskCountModel.setSystemid(systemid);
							projectsTaskCountModel.setProjectid(projectid.toString());
							projectsTaskCountModel.setTime(nowStr);
							
							if (state.equals(0) && process.equals(0)) {
								
							} else if ((state.equals(0) && process.equals(5)) ||
									(state.equals(1) && process.equals(5)) ||
									(state.equals(2) && process.equals(6)) ||
									(state.equals(2) && process.equals(52)) ||
									(state.equals(2) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
								projectsTaskCountModel.setEdittask(projectsTaskCountModel.getEdittask() + count);
							} else if ((state.equals(3) && process.equals(5) && !systemid.equals(SystemType.MapDbEdit_NRFC.getValue())) ||
									(state.equals(0) && process.equals(6)) ||
									(state.equals(1) && process.equals(6))) {
								projectsTaskCountModel.setChecktask(projectsTaskCountModel.getChecktask() + count);
							} else if ((state.equals(3) && process.equals(5) && systemid.equals(SystemType.MapDbEdit_NRFC.getValue())) ||
									(state.equals(3) && process.equals(6)) ||
									(state.equals(3) && process.equals(20))) {
								projectsTaskCountModel.setCompletetask(projectsTaskCountModel.getCompletetask() + count);
							} else if ((state.equals(1) && process.equals(52)) ||
									(state.equals(2) && process.equals(5)) ||
									(state.equals(0) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0) ||
									(state.equals(1) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
								projectsTaskCountModel.setQctask(projectsTaskCountModel.getQctask() + count);
							}
							
							uniqRecords.put(uniqRecord, projectsTaskCountModel);
						}
						
						if (checkid != null && checkid.compareTo(0) > 0) {
							checkid = checkid.compareTo(600000) > 0 ? (checkid - 600000) : checkid;
							ProjectsTaskCountUniq uniqRecord = new ProjectsTaskCountUniq(checkid, RoleType.ROLE_CHECKER.getValue(), systemid, projectid);
							ProjectsTaskCountModel projectsTaskCountModel = new ProjectsTaskCountModel();
							if(uniqRecords.containsKey(uniqRecord)) {
								projectsTaskCountModel = uniqRecords.get(uniqRecord);
								uniqRecords.remove(uniqRecord);
							}
							projectsTaskCountModel.setUserid(checkid);
							projectsTaskCountModel.setRoleid(RoleType.ROLE_CHECKER.getValue());
							projectsTaskCountModel.setRolename(RoleType.ROLE_CHECKER.getDes());
							projectsTaskCountModel.setSystemid(systemid);
							projectsTaskCountModel.setProjectid(projectid.toString());
							projectsTaskCountModel.setTime(nowStr);
							
							if (state.equals(0) && process.equals(0)) {
								
							} else if ((state.equals(0) && process.equals(5)) ||
									(state.equals(1) && process.equals(5)) ||
									(state.equals(2) && process.equals(6)) ||
									(state.equals(2) && process.equals(52)) ||
									(state.equals(2) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
								projectsTaskCountModel.setEdittask(projectsTaskCountModel.getEdittask() + count);
							} else if ((state.equals(3) && process.equals(5) && !systemid.equals(SystemType.MapDbEdit_NRFC.getValue())) ||
									(state.equals(0) && process.equals(6)) ||
									(state.equals(1) && process.equals(6))) {
								projectsTaskCountModel.setChecktask(projectsTaskCountModel.getChecktask() + count);
							} else if ((state.equals(3) && process.equals(5) && systemid.equals(SystemType.MapDbEdit_NRFC.getValue())) ||
									(state.equals(3) && process.equals(6)) ||
									(state.equals(3) && process.equals(20))) {
								projectsTaskCountModel.setCompletetask(projectsTaskCountModel.getCompletetask() + count);
							} else if ((state.equals(1) && process.equals(52)) ||
									(state.equals(2) && process.equals(5)) ||
									(state.equals(0) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0) ||
									(state.equals(1) && process.compareTo(11) >= 0 && process.compareTo(15) <= 0)) {
								projectsTaskCountModel.setQctask(projectsTaskCountModel.getQctask() + count);
							}
							
							uniqRecords.put(uniqRecord, projectsTaskCountModel);
						}
						
					}
					if (uniqRecords != null && !uniqRecords.isEmpty()) {
						for (ProjectsTaskCountModel projectsTaskCountModel : uniqRecords.values()) {
							if (projectsTaskCountModel.getTotaltask().equals(0) &&
									projectsTaskCountModel.getEdittask().equals(0) &&
									projectsTaskCountModel.getQctask().equals(0) &&
									projectsTaskCountModel.getChecktask().equals(0) &&
									projectsTaskCountModel.getCompletetask().equals(0))
								continue;
							try {
								Integer userid = projectsTaskCountModel.getUserid();
								EmployeeModel record = new EmployeeModel();
								record.setId(userid);
								EmployeeModel emp = emapgoAccountService.getOneEmployeeWithCache(record );
								if (emp == null)
									continue;
								projectsTaskCountModel.setUsername(emp.getRealname());
								projectsTaskCountModel.setTotaltask(projectsTaskCountModel.getEdittask() + projectsTaskCountModel.getChecktask() + projectsTaskCountModel.getQctask() + projectsTaskCountModel.getCompletetask());
								projectsTaskCountDao.newProjectsProgress(projectsTaskCountModel);
							} catch (DuplicateKeyException e) {
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
						}
					}
				}
			}
			
			logger.debug("END");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}