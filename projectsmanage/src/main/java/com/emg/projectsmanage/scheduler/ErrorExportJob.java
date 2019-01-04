package com.emg.projectsmanage.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.JobStatus;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ErrorsTaskModelDao;
import com.emg.projectsmanage.dao.task.ErrorModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorAndErrorRelatedModel;
import com.emg.projectsmanage.pojo.ErrorsTaskModel;
import com.emg.projectsmanage.pojo.ItemConfigModel;

@Component
public class ErrorExportJob implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorExportJob.class);
	
	@Autowired
	private ErrorsTaskModelDao errorsTaskModelDao;
	@Autowired
	private ConfigDBModelDao configDBModelDao;
	@Autowired
	private ErrorModelDao errorModelDao;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();

		Long taskid = dataMap.getLong("taskid");
		
		try {
			logger.debug("START with taskid: " + taskid);
			
			try {
				ErrorsTaskModel record = new ErrorsTaskModel();
				record.setId(taskid);
				record.setState(JobStatus.JOB_DOING.getValue());
				errorsTaskModelDao.updateByPrimaryKeySelective(record);
			} catch (Exception e) {
				throw e;
			}
			
			//TODO 2019年1月3日 下午5:02:42 补充错误导出的业务逻辑
			try {
				ErrorsTaskModel errorsTask = errorsTaskModelDao.selectByPrimaryKey(taskid);
				Integer qctask = errorsTask.getQctask();
				Integer errorsrc = errorsTask.getErrorsrc();
				Integer errortar = errorsTask.getErrortar();
				Long batchid = errorsTask.getBatchid();
			    Long errorsetid = errorsTask.getErrorsetid();
			    Long curerrorid = errorsTask.getCurerrorid();
			    Long maxerrorid = errorsTask.getMaxerrorid();
			    
			    ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(qctask);
				List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorsetid);
				List<Long> errortypes = new ArrayList<Long>();
				if (itemIDs != null && !itemIDs.isEmpty()) {
					List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
					for (ItemConfigModel itemConfig : itemConfigs) {
						errortypes.add(itemConfig.getErrortype());
					}
				}

				Integer batchNum = 20000;
				ConfigDBModel configDBSrc = configDBModelDao.selectByPrimaryKey(errorsrc);
				ConfigDBModel configDBTar = configDBModelDao.selectByPrimaryKey(errortar);
				while (curerrorid < maxerrorid) {
					List<ErrorAndErrorRelatedModel> errorAndRelateds = errorModelDao.selectErrorAndErrorRelateds(configDBSrc, batchid, errortypes, curerrorid, curerrorid+batchNum-1);
					if (errorAndRelateds != null && !errorAndRelateds.isEmpty()) {
						errorModelDao.exportErrors(configDBTar, errorAndRelateds);
					}
					curerrorid += batchNum;
					curerrorid = curerrorid.compareTo(maxerrorid) > 0 ? maxerrorid : curerrorid;
					try {
						ErrorsTaskModel record = new ErrorsTaskModel();
						record.setId(taskid);
						record.setCurerrorid(curerrorid);
						errorsTaskModelDao.updateByPrimaryKeySelective(record);
					} catch (Exception e) {
						throw e;
					}
				}
			} catch (Exception e) {
				throw e;
			}
		    
			try {
				ErrorsTaskModel record = new ErrorsTaskModel();
				record.setId(taskid);
				record.setState(JobStatus.JOB_DONE.getValue());
				errorsTaskModelDao.updateByPrimaryKeySelective(record);
			} catch (Exception e) {
				throw e;
			}
			
			logger.debug("END with taskid: " + taskid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ErrorsTaskModel record = new ErrorsTaskModel();
			record.setId(taskid);
			record.setState(JobStatus.JOB_EXCEPTION.getValue());
			errorsTaskModelDao.updateByPrimaryKeySelective(record);
		}
	}
}
