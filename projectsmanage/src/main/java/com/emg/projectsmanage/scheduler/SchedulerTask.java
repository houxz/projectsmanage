package com.emg.projectsmanage.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.emg.projectsmanage.common.POITaskTypeEnum;
import com.emg.projectsmanage.common.ProcessConfigEnum;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.emapgoaccount.EmployeeModelDao;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.dao.projectsmanager.CapacityModelDao;
import com.emg.projectsmanage.dao.projectsmanager.CapacityTaskModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
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
import com.emg.projectsmanage.pojo.TaskModel;
import com.emg.projectsmanage.service.ProcessConfigModelService;
import com.emg.projectsmanage.pojo.CapacityTaskModelExample.Criteria;

@Component
public class SchedulerTask {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

	@Value("${scheduler.enable}")
	private String enable;

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
	private EmployeeModelDao employeeModelDao;

	/**
	 * 半夜三更 创建每天的任务
	 */
	@Scheduled(cron = "${scheduler.createtime}")
	public void task() {
		if (!enable.equalsIgnoreCase("true"))
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
	@Scheduled(cron = "${scheduler.dotime}")
	public void doTask() {
		if (!enable.equalsIgnoreCase("true"))
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
							
							Map<UniqRecord, CapacityModel> uniqRecords = new HashMap<UniqRecord, CapacityModel>();
							
							List<Map<String, Object>> taskGroups = taskModelDao.groupTasksByTime(configDBModel, time);
							if (taskGroups != null && taskGroups.size() > 0) {
								for (Map<String, Object> taskGroup : taskGroups) {
									Integer taskType = (Integer) taskGroup.get("tasktype");
									Long projectid = (Long) taskGroup.get("projectid");
									Integer editid = (Integer) taskGroup.get("editid");
									Long editnum = (Long) taskGroup.get("editnum");
									Integer checkid = (Integer) taskGroup.get("checkid");
									Long checknum = (Long) taskGroup.get("checknum");
									
									UniqRecord editUniqRecord = new UniqRecord(taskType, projectid, editid);
									UniqRecord checkUniqRecord = new UniqRecord(taskType, projectid, checkid);
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
									EmployeeModel emp = employeeModelDao.getOneEmployee(erecord);
									if(emp != null)
										editCapacityModel.setUsername(emp.getRealname());
									erecord.setId(checkid);
									emp = employeeModelDao.getOneEmployee(erecord);
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
									
									UniqRecord editUniqRecord = new UniqRecord(taskType, projectid, editid);
									UniqRecord checkUniqRecord = new UniqRecord(taskType, projectid, checkid);
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
									EmployeeModel emp = employeeModelDao.getOneEmployee(erecord);
									if(emp != null)
										editCapacityModel.setUsername(emp.getRealname());
									erecord.setId(checkid);
									emp = employeeModelDao.getOneEmployee(erecord);
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
									if (taskType.equals(POITaskTypeEnum.FEISHICE.getValue()) || // 非实测
											taskType.equals(POITaskTypeEnum.QUANGUOQC.getValue()) || // 全国质检改错
											taskType.equals(POITaskTypeEnum.FEISHICEADDRESSTEL.getValue())// 地址电话改错
											|| taskType.equals(POITaskTypeEnum.KETOU.getValue()) || // 客投制作
											taskType.equals(POITaskTypeEnum.GEN.getValue())) {// 易淘金制作
										userid = task.getEditid();
										roleid = RoleType.ROLE_WORKER.getValue();
									} else if (taskType.equals(POITaskTypeEnum.MC_KETOU.getValue()) || // 客投校正
											taskType.equals(POITaskTypeEnum.MC_GEN.getValue())) {// 易淘金校正
										userid = task.getCheckid();
										roleid = RoleType.ROLE_CHECKER.getValue();
									} else {
										continue;
									}
									
									Long projectid = task.getProjectid();
									if (projectid.compareTo(0L) <= 0)
										continue;
									
									UniqRecord editUniqRecord = new UniqRecord(taskType, projectid, userid);
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
									EmployeeModel emp = employeeModelDao.getOneEmployee(erecord);
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
									if (taskType.equals(POITaskTypeEnum.FEISHICE.getValue()) || // 非实测
											taskType.equals(POITaskTypeEnum.QUANGUOQC.getValue()) || // 全国质检改错
											taskType.equals(POITaskTypeEnum.FEISHICEADDRESSTEL.getValue())// 地址电话改错
											|| taskType.equals(POITaskTypeEnum.KETOU.getValue()) || // 客投制作
											taskType.equals(POITaskTypeEnum.GEN.getValue())) {// 易淘金制作
										userid = task.getEditid();
										roleid = RoleType.ROLE_WORKER.getValue();
									} else if (taskType.equals(POITaskTypeEnum.MC_KETOU.getValue()) || // 客投校正
											taskType.equals(POITaskTypeEnum.MC_GEN.getValue())) {// 易淘金校正
										userid = task.getCheckid();
										roleid = RoleType.ROLE_CHECKER.getValue();
									} else {
										continue;
									}
									
									Long projectid = task.getProjectid();
									if (projectid.compareTo(0L) <= 0)
										continue;
									
									UniqRecord editUniqRecord = new UniqRecord(taskType, projectid, userid);
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
									EmployeeModel emp = employeeModelDao.getOneEmployee(erecord);
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
}

class UniqRecord {
	Integer tasktype;
	Long projectid;
	Integer userid;

	UniqRecord(Integer tasktype, Long projectid, Integer userid) {
		this.tasktype = tasktype;
		this.projectid = projectid;
		this.userid = userid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (obj instanceof UniqRecord) {
			UniqRecord another = (UniqRecord) obj;
			return another.tasktype.equals(this.tasktype) && another.projectid.equals(this.projectid)
					&& another.userid.equals(this.userid);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (tasktype * ((int) (projectid ^ (projectid >>> 32))) * userid);
	}
}
