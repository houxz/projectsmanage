package com.emg.projectsmanage.scheduler;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldJob implements Job{
	
	private static final Logger logger = LoggerFactory.getLogger(HelloWorldJob.class);
	
	@Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug("----hello world---" + new Date());
    }
}
