package com.emg.poiwebeditor.service;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emg.poiwebeditor.scheduler.ErrorExportJob;

@Service("quartzService")
public class QuartzService {

	@Autowired
	private Scheduler quartzScheduler;
	
	private final static String JOBGROUPNAME = "JOBGROUPNAME";
	
	private final static String TRIGGERGROUPNAME = "TRIGGERGROUPNAME";
	
	public void addJob(Date time, Long taskid) {
		try {
			JobDetail job = JobBuilder.newJob(ErrorExportJob.class)
					.withIdentity(taskid.toString(), JOBGROUPNAME)
					.usingJobData("taskid", taskid)
					.build();
			SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
					.withIdentity(taskid.toString(), TRIGGERGROUPNAME)
					.startAt(time)
					.build();
			quartzScheduler.scheduleJob(job, trigger);
			if (!quartzScheduler.isShutdown()) {
				quartzScheduler.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void removeJob(Long taskid) {
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey(taskid.toString(), TRIGGERGROUPNAME);
			JobKey jobKey = JobKey.jobKey(taskid.toString(), JOBGROUPNAME);
			if (quartzScheduler.checkExists(jobKey) && quartzScheduler.checkExists(triggerKey)) {
				quartzScheduler.pauseJob(jobKey);
				quartzScheduler.interrupt(jobKey);
				quartzScheduler.pauseTrigger(triggerKey);
				quartzScheduler.unscheduleJob(triggerKey);
				quartzScheduler.deleteJob(jobKey);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
