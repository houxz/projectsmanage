package com.emg.projectsmanage.service;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("quartzService")
public class QuartzService {

	@Autowired
	private Scheduler quartzScheduler;

	public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
			Class<? extends Job> cls, Date time, Long taskid) {
		try {
			Scheduler sched = quartzScheduler;
			JobDetail job = JobBuilder.newJob(cls)
					.withIdentity(jobName, jobGroupName)
					.usingJobData("taskid", taskid)
					.build();
			SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
					.withIdentity(triggerName, triggerGroupName)
					.startAt(time)
					.withSchedule(SimpleScheduleBuilder.simpleSchedule()
							.withIntervalInSeconds(10)
							.repeatForever())
					.build();
			sched.scheduleJob(job, trigger);
			if (!sched.isShutdown()) {
				sched.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
		try {
			Scheduler sched = quartzScheduler;
			sched.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroupName));
			sched.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroupName));
			sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void pauseJob(String jobName, String jobGroupName) {
		try {
			quartzScheduler.pauseJob(JobKey.jobKey(jobName, jobGroupName));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

	public void resumeJob(String jobName, String jobGroupName) {
		try {
			quartzScheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}
