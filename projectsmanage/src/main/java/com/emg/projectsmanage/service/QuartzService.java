package com.emg.projectsmanage.service;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emg.projectsmanage.scheduler.HelloWorldJob;

@Service("quartzService")
public class QuartzService {

	@Autowired
	private Scheduler quartzScheduler;
	
	private final static String JOBGROUPNAME = "JOBGROUPNAME";
	
	private final static String TRIGGERGROUPNAME = "TRIGGERGROUPNAME";
	
	private final static Integer IntervalInSeconds = 10;

	public void addJob(Date time, Long taskid) {
		try {
			JobDetail job = JobBuilder.newJob(HelloWorldJob.class)
					.withIdentity(taskid.toString(), JOBGROUPNAME)
					.usingJobData("taskid", taskid)
					.build();
			SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
					.withIdentity(taskid.toString(), TRIGGERGROUPNAME)
					.startAt(time)
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInSeconds(IntervalInSeconds)
							.repeatForever())
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
			quartzScheduler.pauseTrigger(triggerKey);
			quartzScheduler.unscheduleJob(triggerKey);
			quartzScheduler.deleteJob(jobKey);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
