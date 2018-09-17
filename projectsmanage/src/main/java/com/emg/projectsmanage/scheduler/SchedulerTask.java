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
	private TaskBlockDetailModelDao taskBlockDetailModelDao;

	@Autowired
	private ProjectModelDao projectModelDao;

	@Autowired
	private ProcessModelDao processModelDao;

	@Autowired
	private EmployeeModelDao employeeModelDao;

	/**
	 * 创建每天的任务 凌晨1点45分创建
	 */
	@Scheduled(cron = "${scheduler.enable}")
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
	 * 执行任务每20秒检测是否有新任务
	 */
	@Scheduled(cron = "0/20 * * * * ?")
	public void doTask() {
		if (!enable.equalsIgnoreCase("true"))
			return;

		try {
			CapacityTaskModelExample example = new CapacityTaskModelExample();
			Criteria criteria = example.or();
			criteria.andStateEqualTo(CapacityTaskStateEnum.NEW.getValue());
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
							List<TaskModel> tasks = taskModelDao.getTaskByTime(configDBModel, time);

							Map<UniqRecord, CapacityModel> uniqRecords = new HashMap<UniqRecord, CapacityModel>();
							for (TaskModel task : tasks) {
								logger.debug(String.format("task: ( %s ) in", task.getId()));

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
								UniqRecord uniqRecord = new UniqRecord(taskType, projectid, userid);
								Long taskid = task.getId();
								Long blockid = task.getBlockid();

								CapacityModel capacityModel = new CapacityModel();
								if (uniqRecords.containsKey(uniqRecord)) {
									capacityModel = uniqRecords.get(uniqRecord);
									uniqRecords.remove(uniqRecord);
								}

								ProjectModel project = projectModelDao.selectByPrimaryKey(projectid);
								capacityModel.setProjectid(projectid);
								if (project != null) {
									Long processid = project.getProcessid();
									ProcessModel process = processModelDao.selectByPrimaryKey(processid);
									if(process != null) {
										capacityModel.setProcessid(processid);
										capacityModel.setProcessname(process.getName());
									}
								}

								capacityModel.setTasktype(taskType);

								EmployeeModel erecord = new EmployeeModel();
								erecord.setId(userid);
								EmployeeModel emp = employeeModelDao.getOneEmployee(erecord);
								capacityModel.setUserid(userid);
								capacityModel.setUsername(emp.getRealname());

								capacityModel.setRoleid(roleid);
								capacityModel.setTime(time);

								// 制作任务个数
								if (task.getState().equals(2) && task.getTime().startsWith(time))
									capacityModel.setTaskcount(capacityModel.getTaskcount() + 1);

								// 修改质检错误量
								Integer errorcount = taskLinkErrorModelDao.countTaskLinkErrorByTaskid(configDBModel,
										taskid, time);
								capacityModel.setErrorcount(capacityModel.getErrorcount() + errorcount);

								// 目视错误
								Integer visualerrorcount = taskLinkErrorModelDao
										.countTaskLinkVisualErrorByTaskid(configDBModel, taskid, time);
								capacityModel
										.setVisualerrorcount(capacityModel.getVisualerrorcount() + visualerrorcount);

								// 修改POI个数
								Integer modifypoi = taskBlockDetailModelDao.countModifyPOIByBlockid(configDBModel,
										blockid, time);
								capacityModel.setModifypoi(capacityModel.getModifypoi() + modifypoi);

								// 新增POI个数
								Integer createpoi = taskBlockDetailModelDao.countCreatePOIByBlockid(configDBModel,
										blockid, time);
								capacityModel.setCreatepoi(capacityModel.getCreatepoi() + createpoi);

								// 删除POI个数
								Integer deletepoi = taskBlockDetailModelDao.countDeletePOIByBlockid(configDBModel,
										blockid, time);
								capacityModel.setDeletepoi(capacityModel.getDeletepoi() + deletepoi);

								// 确认POI个数
								Integer confirmpoi = taskBlockDetailModelDao.countConfirmPOIByBlockid(configDBModel,
										blockid, time);
								capacityModel.setConfirmpoi(capacityModel.getConfirmpoi() + confirmpoi);

								uniqRecords.put(uniqRecord, capacityModel);
								logger.debug(String.format("task: ( %s ) out", task.getId()));
							}

							if (uniqRecords != null && !uniqRecords.isEmpty()) {
								for (CapacityModel capacityModel : uniqRecords.values()) {
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
