package com.emg.projectsmanage.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.CapacityTaskEnum;
import com.emg.projectsmanage.dao.projectsmanager.CapacityTaskModelDao;
import com.emg.projectsmanage.pojo.CapacityTaskModel;
import com.emg.projectsmanage.pojo.CapacityTaskModelExample;
import com.emg.projectsmanage.pojo.CapacityTaskModelExample.Criteria;

@Component
public class SchedulerTask {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerTask.class);

	@Value("${scheduler.enable}")
	private String enable;

	@Autowired
	private CapacityTaskModelDao capacityTaskModelDao;

	/**
	 * 创建每天的任务 凌晨1点创建
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void task() {
		if (!enable.equalsIgnoreCase("true"))
			return;

		Date now = new Date();
		String time = new SimpleDateFormat("yyyy-MM-dd").format(now);
		try {
			CapacityTaskModel record = new CapacityTaskModel();
			record.setStarttime(now);
			record.setState(CapacityTaskEnum.NEW.getValue());
			record.setTime(time);
			capacityTaskModelDao.insertSelective(record);
			Long curTaskID = record.getId();

			logger.debug(String.format("Scheduler new task created, curTaskID: %s, time: %s.", curTaskID, time));
		} catch (DuplicateKeyException e) {
			logger.error(String.format("Scheduler task( %s ) exist.", time));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("Scheduler new task create error.");
		}
	}

	/**
	 * 执行任务 运行时间通过配置文件配置
	 */
	@Scheduled(cron = "0/5 * * * * ?")
	public void doTask() {
		if (!enable.equalsIgnoreCase("true"))
			return;

		logger.debug("Scheduler tasks started.");
		try {
			CapacityTaskModelExample example = new CapacityTaskModelExample();
			Criteria criteria = example.or();
			criteria.andStateEqualTo(CapacityTaskEnum.NEW.getValue());
			List<CapacityTaskModel> newTasks = capacityTaskModelDao.selectByExample(example);

			for (CapacityTaskModel newTask : newTasks) {
				try {
					Long curTaskID = newTask.getId();
					CapacityTaskModel record = new CapacityTaskModel();
					record.setId(curTaskID);
					record.setStarttime(new Date());
					record.setState(CapacityTaskEnum.DOING.getValue());
					capacityTaskModelDao.updateByPrimaryKeySelective(record);

					logger.debug(String.format("Scheduler task( %s ) started.", newTask.getTime()));
					
					
					
					logger.debug(String.format("Scheduler task( %s ) finished.", newTask.getTime()));

					record = new CapacityTaskModel();
					record.setId(curTaskID);
					record.setState(CapacityTaskEnum.FINISHED.getValue());
					capacityTaskModelDao.updateByPrimaryKeySelective(record);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					Long curTaskID = newTask.getId();
					CapacityTaskModel record = new CapacityTaskModel();
					record.setId(curTaskID);
					record.setState(CapacityTaskEnum.ERROR.getValue());
					capacityTaskModelDao.updateByPrimaryKeySelective(record);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("Scheduler tasks finished.");
	}

}
