package com.emg.projectsmanage.service;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("quartzService")
public class QuartzService{
	
	@Autowired
	private Scheduler quartzScheduler;
	
	public void addJob(String jobName, String jobGroupName, String triggerName,
			String triggerGroupName, Class<? extends Job> cls, String cron) {
		try {
			Scheduler sched = quartzScheduler;
			JobDetail job = JobBuilder.newJob(cls)
					.withIdentity(jobName, jobGroupName).build();
			CronTrigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(triggerName, triggerGroupName)
					.withSchedule(CronScheduleBuilder.cronSchedule(cron))
					.build();
			sched.scheduleJob(job, trigger);
			if (!sched.isShutdown()) {
				sched.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean modifyJobTime(String oldjobName, String oldjobGroup, String oldtriggerName, String oldtriggerGroup, String jobName, String jobGroup,
			String triggerName, String triggerGroup, String cron) {
		try {
			Scheduler sched = quartzScheduler;
			CronTrigger trigger = (CronTrigger) sched.getTrigger(TriggerKey
					.triggerKey(oldtriggerName, oldtriggerGroup));
			if (trigger == null) {
				return false;
			}

			JobKey jobKey = JobKey.jobKey(oldjobName, oldjobGroup);
			TriggerKey triggerKey = TriggerKey.triggerKey(oldtriggerName,
					oldtriggerGroup);

			JobDetail job = sched.getJobDetail(jobKey);
			Class<? extends Job> jobClass = job.getJobClass();
			sched.pauseTrigger(triggerKey);
			sched.unscheduleJob(triggerKey);
			sched.deleteJob(jobKey);
			
			addJob(jobName, jobGroup, triggerName, triggerGroup, jobClass,
					cron);
			
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	public void modifyJobTime(String triggerName, String triggerGroupName,
			String time) {
		try {
			Scheduler sched = quartzScheduler;
			CronTrigger trigger = (CronTrigger) sched.getTrigger(TriggerKey
					.triggerKey(triggerName, triggerGroupName));
			if (trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				CronTrigger ct = (CronTrigger) trigger;
				ct.getTriggerBuilder()
						.withSchedule(CronScheduleBuilder.cronSchedule(time))
						.build();
				sched.resumeTrigger(TriggerKey.triggerKey(triggerName,
						triggerGroupName));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void removeJob(String jobName, String jobGroupName,
			String triggerName, String triggerGroupName) {
		try {
			Scheduler sched = quartzScheduler;
			sched.pauseTrigger(TriggerKey.triggerKey(triggerName,
					triggerGroupName));
			sched.unscheduleJob(TriggerKey.triggerKey(triggerName,
					triggerGroupName));
			sched.deleteJob(JobKey.jobKey(jobName, jobGroupName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void startSchedule() {
		try {
			Scheduler sched = quartzScheduler;
			sched.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void shutdownSchedule() {
		try {
			Scheduler sched = quartzScheduler;
			if (!sched.isShutdown()) {
				sched.shutdown();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void pauseJob(String jobName, String jobGroupName) {
		try {
			quartzScheduler.pauseJob( JobKey.jobKey(jobName, jobGroupName));
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
