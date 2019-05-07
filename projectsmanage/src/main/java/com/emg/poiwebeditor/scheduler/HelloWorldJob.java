package com.emg.poiwebeditor.scheduler;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
public class HelloWorldJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(HelloWorldJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Hello world @ " + new Date());

		JobKey key = context.getJobDetail().getKey();

		JobDataMap dataMap = context.getJobDetail().getJobDataMap();

		Long taskid = dataMap.getLong("taskid");

		logger.debug("Instance " + key + " of DumbJob taskid: " + taskid);
	}
}
