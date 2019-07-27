package com.emg.poiwebeditor.client;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.emg.poiwebeditor.pojo.TaskLinkPoiModel;
import com.emg.poiwebeditor.pojo.TaskModel;

@Service
public class CheckTaskClient extends TaskClient{
	
	private static final Logger logger = LoggerFactory.getLogger(CheckTaskClient.class);
	
	// @Override
	public TaskModel selectNextTask(List<Long> projectIDs, Integer userid) throws Exception {
		TaskModel task = null;
		try {
			String sql = getCheckTaskSQL(projectIDs, userid);
			logger.debug(sql);
			task = (TaskModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql, TaskModel.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return task;
	}
	private String getCheckTaskSQL(List<Long> projectIDs, Integer userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" with projectid(projectid) as (select * from unnest(array[" + StringUtils.join(projectIDs, ",") + "])), ");
		sb.append("		sorttable(state, process, sortvalue) as (values(1,7,0)) ");
		sb.append(" update tb_task ");
		sb.append(" set starttime=now(),operatetime=now(),state=1,process=7,checkid= " + userid);
		sb.append(" from ( ");
		sb.append("		select * from ( ");
		sb.append("			select coalesce ( ");
		sb.append("				( select id from tb_task ");
		sb.append("					join sorttable using(state, process) ");
		sb.append("					where checkid=" + userid +" and projectid=p.projectid and tasktype=17002");
		sb.append("					order by sortvalue ,id ");
		sb.append("					limit 1 for update), ");
		sb.append("				( select id from tb_task ");
		sb.append("					where (state=0 and process=0 ) and projectid=p.projectid and ( editid=0 or editid ISNULL) and tasktype=17002 ");
		sb.append("					order by id ");
		sb.append("					limit 1 for update) ");
		sb.append("			) as taskid ");
		sb.append("			from projectid as p ");
		sb.append("		) as b where taskid is not null limit 1 ");
		sb.append(" ) as a(id) where tb_task.id = a.id returning tb_task.*; ");
		return sb.toString();
	}
	
	/**
	 * 抽检标记错误	
	 * @param taskid
	 * @return
	 * @throws Exception
	 */
	public Long updateCheckTaskState(long taskid) throws Exception {
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("insert into tb_task_link_error(taskid, errorid, updatetime, shapeid, pstate) values(");
			sb.append(taskid).append(", 0, now(), 0, 0) ");
			
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	public TaskLinkPoiModel selectTaskPoi(long taskid) throws Exception {
		TaskLinkPoiModel task = null;
		try {
			String sql = "select * from tb_task_link_poi where taskid = " +taskid;
			logger.debug(sql);
			task = (TaskLinkPoiModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql, TaskLinkPoiModel.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return task;
	}

}
