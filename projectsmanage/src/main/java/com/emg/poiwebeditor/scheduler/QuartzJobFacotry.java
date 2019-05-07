package com.emg.poiwebeditor.scheduler;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

public class QuartzJobFacotry extends SpringBeanJobFactory {
	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {

		Object jobInstance = super.createJobInstance(bundle);
		beanFactory.autowireBean(jobInstance);

		return jobInstance;
	}
}
