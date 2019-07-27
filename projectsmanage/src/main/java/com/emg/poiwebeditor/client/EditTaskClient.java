package com.emg.poiwebeditor.client;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.emg.poiwebeditor.pojo.TaskModel;

@Service
public class EditTaskClient extends TaskClient{
	
	private static final Logger logger = LoggerFactory.getLogger(EditTaskClient.class);
	
	// @Override
	public TaskModel selectNextTask(List<Long> projectIDs, Integer userid) throws Exception {
		TaskModel task = null;
		try {
			String sql = getEditTaskSQL(projectIDs, userid);
			logger.debug(sql);
			task = (TaskModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql, TaskModel.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return task;
	}
	
	private String getEditTaskSQL(List<Long> projectIDs, Integer userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" with projectid(projectid) as (select * from unnest(array[" + StringUtils.join(projectIDs, ",") + "])), ");
		sb.append("		sorttable(state, process, sortvalue) as (values(1,5,0)) ");
		sb.append(" update tb_task ");
		sb.append(" set starttime=now(),operatetime=now(),state=1,process=5,editid= " + userid);
		sb.append(" from ( ");
		sb.append("		select * from ( ");
		sb.append("			select coalesce ( ");
		sb.append("				( select id from tb_task ");
		sb.append("					join sorttable using(state, process) ");
		sb.append("					where editid=" + userid +" and projectid=p.projectid ");
		sb.append("					order by sortvalue ,id ");
		sb.append("					limit 1 for update), ");
		sb.append("				( select id from tb_task ");
		sb.append("					where (state=0 and process=0 ) and projectid=p.projectid and ( editid=0 or editid ISNULL) and tasktype=17001");
		sb.append("					order by id ");
		sb.append("					limit 1 for update) ");
		sb.append("			) as taskid ");
		sb.append("			from projectid as p ");
		sb.append("		) as b where taskid is not null limit 1 ");
		sb.append(" ) as a(id) where tb_task.id = a.id returning tb_task.*; ");
		return sb.toString();
	}
	
	/**
	 * 获取用户已经占用的任务，即task.editid = userid
	 * @param projectIDs
	 * @param userid
	 * @param num
	 * @return
	 * @throws Exception
	 */
	public List selectUserTask(List<Long> projectIDs, Integer userid, long num) throws Exception {
		List tasks =  null;
		try {
			
			StringBuffer sql2 = new StringBuffer();
			
			sql2.append(" with");
			sql2.append(" sorttable(projectid, sortvalue) as (values"); //(762,0), (824, 1), (883, 2), (763, 3), (761, 4))  ");
			for (int i = 0; i < projectIDs.size(); i++) {
				sql2.append("(").append(projectIDs.get(i)).append(",").append(i).append(")").append(",");
			}
			sql2.deleteCharAt(sql2.length() - 1);
			sql2.append(")");
			sql2.append(" update tb_task set starttime=now(),operatetime=now(),state=1,process=5,editid=  ").append(userid);
			sql2.append(" from ( ");
			sql2.append(" select id from tb_task t ");
			sql2.append(" join sorttable p on p.projectid = t.projectid ");
			sql2.append(" where (state=1 and process=5 ) and t.projectid=p.projectid and editid=").append(userid).append(" and tasktype=17001 order by p.sortvalue, t.id limit ").append(num).append(" ) as b(id) where tb_task.id = b.id returning tb_task.* ");

			logger.debug(sql2.toString());
			tasks =  ExecuteSQLApiClientUtils.postList(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql2.toString(), TaskModel.class);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return tasks;
	}

}
