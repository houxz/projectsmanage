package com.emg.poiwebeditor.client;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.emg.poiwebeditor.common.Common;
import com.emg.poiwebeditor.common.DatabaseType;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.SpotCheckProjectInfo;
import com.emg.poiwebeditor.pojo.SpotCheckTaskInfo;
import com.emg.poiwebeditor.pojo.TaskLinkPoiModel;
import com.emg.poiwebeditor.pojo.TaskModel;

@Service
public class TaskModelClient {

	@Value("${taskApi.host}")
	private String host;
	@Value("${taskApi.port}")
	private String port;
	@Value("${taskApi.path}")
	private String path;
	
	private final static String SELECT = "select";
	private final static String UPDATE = "update";
	
	private static final Logger logger = LoggerFactory.getLogger(TaskModelClient.class);
	
	/*private String getUrl = "http://%s:%s/%s/mergetask/%s/%s/execute";
	private String postUrl = "http://%s:%s/%s/mergetask/%s";*/
	private String getUrl = "http://%s:%s/%s/poitask/%s/%s/execute";
	private String postUrl = "http://%s:%s/%s/poitask/%s";
	
	private String contentType = "application/x-www-form-urlencoded";
	
	public TaskModel selectMyNextEditTask(List<Long> projectIDs, Integer userid) throws Exception {
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
	
	public TaskModel selectNextCheckTask(List<Long> projectIDs, Integer userid) throws Exception {
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
	
	
	
	public Long submitEditTask(Long taskid, Integer editid) throws Exception {
		Long ret = -1L;
		try {
			String sql = submitEditTaskSQL(taskid, editid);
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE, URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	private String submitEditTaskSQL(Long taskid, Integer editid) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE tb_task");
		sb.append(" SET endtime=now(),operatetime=now(),state = 2");
		sb.append(" WHERE id = " + taskid);
		sb.append(" AND editid = " + editid);
		return sb.toString();
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
		sb.append("					where (state=0 and process=0 ) and projectid=p.projectid and ( editid=0 or editid ISNULL) ");
		sb.append("					order by id ");
		sb.append("					limit 1 for update) ");
		sb.append("			) as taskid ");
		sb.append("			from projectid as p ");
		sb.append("		) as b where taskid is not null limit 1 ");
		sb.append(" ) as a(id) where tb_task.id = a.id returning tb_task.*; ");
		return sb.toString();
	}
	
	private String getCheckTaskSQL(List<Long> projectIDs, Integer userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" with projectid(projectid) as (select * from unnest(array[" + StringUtils.join(projectIDs, ",") + "])), ");
		sb.append("		sorttable(state, process, sortvalue) as (values(1,7,0)) ");
		sb.append(" update tb_task ");
		sb.append(" set starttime=now(),operatetime=now(),state=1,process=7,editid= " + userid);
		sb.append(" from ( ");
		sb.append("		select * from ( ");
		sb.append("			select coalesce ( ");
		sb.append("				( select id from tb_task ");
		sb.append("					join sorttable using(state, process) ");
		sb.append("					where editid=" + userid +" and projectid=p.projectid and tasktype=17002");
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
	
	
	/*private String getEditTaskSQL(List<Long> projectIDs, Integer userid) {
		StringBuilder sb = new StringBuilder();
		//sb.append(" with projectid(projectid) as (select * from unnest(array[" + StringUtils.join(projectIDs, ",") + "])), ");
		//sb.append("		sorttable(state, process, sortvalue) as (values(1,5,0)) ");
		sb.append(" update tb_task ");
		sb.append(" set starttime=now(),operatetime=now(),state=1,process=5,editid= " + userid);
		sb.append(" from ( ");
		sb.append("		select * from ( ");
		sb.append("			select coalesce ( ");
		sb.append("				( select id from tb_task ");
		//sb.append("					join sorttable using(state, process) ");
		//sb.append("					where editid=" + userid +" and projectid=p.projectid ");
		sb.append("					where editid=" + userid +" and projectid in (" + StringUtils.join(projectIDs, ",") + ")");
		//sb.append("					order by sortvalue ,id ");
		sb.append("					and state = 1 and process = 5 ");
		sb.append("					limit 1 for update), ");
		sb.append("				( select id from tb_task ");
		sb.append("					where (state=0 and process=0 ) and projectid in (" + StringUtils.join(projectIDs, ",") + ") and ( editid=0 or editid ISNULL) ");
		// sb.append("					order by id ");
		sb.append("					limit 1 for update) ");
		sb.append("			) as taskid ");
		// sb.append("			from projectid as p ");
		sb.append("		) as b where taskid is not null limit 1 ");
		sb.append(" ) as a(id) where tb_task.id = a.id returning tb_task.*; ");
		return sb.toString();
	}*/
	
	//获取批次的keyid集合
	public Boolean InsertNewTask(ConfigDBModel configDBModel,Long projectid,Long shapeid,Integer state){
		BasicDataSource dataSource = null;
		try {
			if ( configDBModel == null)
				return false;
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" insert into ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_task  ");
			sql.append(" (name,projectid,priority,rank,keywordid,state) ");
			sql.append(" values('name'," + projectid +",0,0,"+ shapeid +" ," + state +")");
				
			dataSource = Common.getDataSource(configDBModel);
			int insertcount = new  JdbcTemplate(dataSource).update(sql.toString());
			
			if( insertcount > 0 )
				return true;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
	
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return false;
	}
	//每次提交的时候向tb_link_poi中插入一条数据
	public Long InsertNewPOITask(Long taskid, Long oid) throws Exception{
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("insert into task.tb_task_link_poi(poiid, updatetime, taskid) values(");
			sb.append(oid).append(",").append("now()").append(",").append(taskid).append(")");
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE, URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	// byhxz20190520
	private String getModifyTaskSQL(List<Long> projectIDs, Integer userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" with projectid(projectid) as (select * from unnest(array[" + StringUtils.join(projectIDs, ",")
				+ "])), ");
		sb.append("		sorttable(state, process, sortvalue) as (values(1,6,0)) ");
		sb.append(" update tb_task ");
		sb.append(" set operatetime=now(),state=1,process=6,editid= " + userid);
		sb.append(" from ( ");
		sb.append("		select * from ( ");
		sb.append("			select coalesce ( ");
		sb.append("				( select id from tb_task ");
		sb.append("					join sorttable using(state, process) ");
		sb.append("					where editid=" + userid + " and projectid=p.projectid ");
		sb.append("					order by sortvalue ,id ");
		sb.append("					limit 1 for update), ");
		sb.append("				( select id from tb_task ");
		sb.append("					where (state=2 and process=5 ) and projectid=p.projectid and ( editid= " + userid
				+ " or editid ISNULL) ");
		sb.append("					order by id ");
		sb.append("					limit 1 for update) ");
		sb.append("			) as taskid ");
		sb.append("			from projectid as p ");
		sb.append("		) as b where taskid is not null limit 1 ");
		sb.append(" ) as a(id) where tb_task.id = a.id returning tb_task.*; ");

		System.out.println(sb.toString());

		return sb.toString();
	}

	// byhxz20190520
	public TaskModel selectMyNextModifyTask(List<Long> projectIDs, Integer userid) throws Exception {
		TaskModel task = null;
		try {
			String sql = getModifyTaskSQL(projectIDs, userid);
			task = (TaskModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT),
					contentType, "sql=" + sql, TaskModel.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		return task;
	}

	// byhxz 20190520
	public TaskLinkPoiModel selectTaskLinkPoiByTaskid(Long taskid) {
		TaskLinkPoiModel linkpoi = null;
		try {
			String sql = "select * from tb_task_link_poi where taskid= " + taskid;
			linkpoi = (TaskLinkPoiModel) ExecuteSQLApiClientUtils.postModel(
					String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql,
					TaskLinkPoiModel.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return linkpoi;
	}

	// byhxz20190522
	public TaskModel selectMyNextModifyTaskByProjectId(Long projectId, Long taskId, Integer userid) throws Exception {
		TaskModel task = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select * from tb_task where projectid=" + projectId + " and id>" + taskId + " and editid="
					+ userid);
			sb.append(" and ( (state = 2 and process = 5) or ( state = 1 and process = 6) ) order by id limit 1 ;");
			String sql = sb.toString();
			task = (TaskModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT),
					contentType, "sql=" + sql, TaskModel.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		return task;
	}

	// byhxz20190522
	public Long submitModifyTask(Long taskid, Integer editid, Integer state) throws Exception {
		Long ret = -1L;
		try {
			String sql = submitModifyTaskSQL(taskid, editid, state);
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}

	// byhxz
	private String submitModifyTaskSQL(Long taskid, Integer editid, Integer state) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE tb_task");
		sb.append(" SET endtime=now(),operatetime=now(),state = " + state);
		sb.append(" WHERE id = " + taskid);
		sb.append(" AND editid = " + editid);
		return sb.toString();
	}

	// byhxz20190522
	public Long updateModifyTask(Long taskid, Integer editid, Integer state, Integer process) throws Exception {
		Long ret = -1L;
		try {
			String sql = updateModifyTaskSQL(taskid, editid, state, process);
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	/**
	 * 把稍后修改的状态由1，5 改成5，5
	 * @param taskid
	 * @param editid
	 * @param state
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public Long updateTaskState(Integer editid, Integer state, Integer process) throws Exception {
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE tb_task");
			sb.append(" SET operatetime=now(),state = " + state);
			sb.append(",process =" + process);
			sb.append(" WHERE state = 5 and process = 5 and  editid = " + editid);
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	/**
	 * 把抽检的状态由5，7改回1，7
	 * @param taskid
	 * @param editid
	 * @param state
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public Long updateCheckTaskState(Integer editid, Integer state, Integer process) throws Exception {
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE tb_task");
			sb.append(" SET operatetime=now(),state = " + state);
			sb.append(",process =" + process);
			sb.append(" WHERE state = 5 and process = 7 and  editid = " + editid);
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}

	// byhxz
	private String updateModifyTaskSQL(Long taskid, Integer editid, Integer state, Integer process) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE tb_task");
		sb.append(" SET operatetime=now(),state = " + state);
		sb.append(",process =" + process);
		sb.append(" WHERE id = " + taskid);
		sb.append(" AND editid = " + editid);
		return sb.toString();
	}

	// 查询项目pid下的所有制作提交（2,5），改错提交(2,6)的任务
	public List<TaskModel> selectTaskByProjectId(Long projectid) throws Exception {
		List<TaskModel> tasklist = new ArrayList<TaskModel>();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select * from tb_task where projectid=" + projectid);
			sb.append(" and ( (state = 2 and process =5) or ( state = 2 and process =6) )");
			String sql = sb.toString();
//				tasklist = (List<TaskModel>) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql, TaskModel.class);
			ArrayList<Object> arr = ExecuteSQLApiClientUtils.getList(String.format(getUrl, host, port, path, SELECT,
					URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")), TaskModel.class);

			int count = arr.size();
			for (int i = 0; i < count; i++) {
				TaskModel task = (TaskModel) arr.get(i);
				tasklist.add(task);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		return tasklist;
	}
	
	// 查询项目pid下的所有制作提交（2,5），改错提交(2,6)的任务
		public List<TaskModel> selectTaskByProjectId(String projectid) throws Exception {
			List<TaskModel> tasklist = new ArrayList<TaskModel>();
			try { 
				StringBuilder sb = new StringBuilder();
				sb.append("select * from tb_task where projectid in(" + projectid + ")");
				sb.append(" and ( (state = 2 and process =5) or ( state = 2 and process =6) )");
				String sql = sb.toString();
//					tasklist = (List<TaskModel>) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql, TaskModel.class);
				ArrayList<Object> arr = ExecuteSQLApiClientUtils.getList(String.format(getUrl, host, port, path, SELECT,
						URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")), TaskModel.class);

				int count = arr.size();
				for (int i = 0; i < count; i++) {
					TaskModel task = (TaskModel) arr.get(i);
					tasklist.add(task);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}

			return tasklist;
		}

	// byhxz20190520
	public TaskModel selectMyNextModifyTaskByProjectsAndUserId(List<Long> projectIDs, Integer userid) throws Exception {
		TaskModel task = null;
		try {
			String sql = getModifyTaskSQLByProjectsAndUserId(projectIDs, userid);
			task = (TaskModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT),
					contentType, "sql=" + sql, TaskModel.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		return task;
	}

	// byhxz20190523
	private String getModifyTaskSQLByProjectsAndUserId(List<Long> projectIDs, Integer userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(" with projectid(projectid) as (select * from unnest(array[" + StringUtils.join(projectIDs, ",")
				+ "])), ");
		sb.append("		sorttable(state, process, sortvalue) as (values(1,6,0)) ");
		sb.append(" update tb_task ");
		sb.append(" set operatetime=now(),state=1,process=6,editid= " + userid);
		sb.append(" from ( ");
		sb.append("		select * from ( ");
		sb.append("			select coalesce ( ");
		sb.append("				( select id from tb_task ");
		sb.append("					join sorttable using(state, process) ");
		sb.append("					where editid=" + userid + " and projectid=p.projectid ");
		sb.append("					order by sortvalue ,id ");
		sb.append("					limit 1 for update), ");
		sb.append("				( select id from tb_task ");
		sb.append("					where (state=0 and process=6 ) and projectid=p.projectid and ( editid= " + userid
				+ " or editid ISNULL) ");
		sb.append("					order by id ");
		sb.append("					limit 1 for update) ");
		sb.append("			) as taskid ");
		sb.append("			from projectid as p ");
		sb.append("		) as b where taskid is not null limit 1 ");
		sb.append(" ) as a(id) where tb_task.id = a.id returning tb_task.*; ");

		System.out.println(sb.toString());

		return sb.toString();
	}
	
	// byhxz20190522
		public TaskModel selectMyNextModifyTaskByProjectIdAndTaskId(Long projectId, Long taskId, Integer userid) throws Exception {
			TaskModel task = null;
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("select * from tb_task where projectid=" + projectId + " and id>" + taskId + " and editid="
						+ userid);
				sb.append(" and   (  (state = 0 or state = 1) and process = 6)  order by id limit 1 ;");
				String sql = sb.toString();
				task = (TaskModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT),
						contentType, "sql=" + sql, TaskModel.class);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}

			return task;
		}
		
			/**
		 * 把抽检的状态由5，7改回1，7
		 * @param taskid
		 * @param editid
		 * @param state
		 * @param process
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
		
		// 查询项目pid下的所有制作人制作的资料数
		public List<SpotCheckTaskInfo> selectSpotCheckTaskByProjectId(Long projectid ,Integer limit,Integer offset) throws Exception {
			List<SpotCheckTaskInfo> tasklist = new ArrayList<SpotCheckTaskInfo>();
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("select 	editid,	sum( CASE WHEN editid > 0 THEN 1 ELSE 0 END ) AS editnum from tb_task where projectid=" + projectid);
				sb.append(" and state = 3  GROUP BY  projectid, editid limit " + limit);
				sb.append(" offset "+ offset);
			
				String sql = sb.toString();
				ArrayList<Object> arr = ExecuteSQLApiClientUtils.getList(String.format(getUrl, host, port, path, SELECT,
						URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")), SpotCheckTaskInfo.class);
System.out.println(sb.toString());
				int count = arr.size();
				for (int i = 0; i < count; i++) {
					SpotCheckTaskInfo task = (SpotCheckTaskInfo) arr.get(i);
					tasklist.add(task);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}

			return tasklist;
		}
		
		public Integer selectSpotCheckTaskCountByProjectId(Long projectid ) throws Exception {
			Integer count = 0;
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("select count(1) from ( select 	count( editid)  from tb_task where projectid=" + projectid);
				sb.append(" and state = 3  GROUP BY  projectid, editid ) as t1 " );
		
				String sql = sb.toString();
				System.out.println(sb.toString());
				
				count  = ExecuteSQLApiClientUtils.queryCount(String.format(getUrl, host, port, path, SELECT,
						URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")));

				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			return count;
		}
		
	// 查询项目pid下的所有制作人制作的资料数
	public List<SpotCheckProjectInfo> selectSpotCheckProjectInfo(Long projectid, Integer editid) throws Exception {
		List<SpotCheckProjectInfo> tasklist = new ArrayList<SpotCheckProjectInfo>();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select * from 	 tb_spotcheckprojectinfo  where projectid= "+ projectid);
			sb.append(" and editid =" + editid);
			sb.append(" order by  id ");
			String sql = sb.toString();
			ArrayList<Object> arr = ExecuteSQLApiClientUtils.getList(String.format(getUrl, host, port, path, SELECT,
					URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")), SpotCheckProjectInfo.class);
			System.out.println(sb.toString());
			int count = arr.size();
			for (int i = 0; i < count; i++) {
				SpotCheckProjectInfo task = (SpotCheckProjectInfo) arr.get(i);
				tasklist.add(task);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		return tasklist;
	}
	
	public List<TaskModel> selectSpotCheckProjectInfo2(Long projectid, Integer editid) throws Exception {
		List<TaskModel> tasklist = new ArrayList<TaskModel>();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select id from tb_task  where projectid= "+ projectid);
			sb.append(" and editid =" + editid);
			sb.append(" and state = 3 and (select count(1) as num from tb_spotchecktask_link_task where tb_task.id = tb_spotchecktask_link_task.oldtaskid) = 0 " );
			String sql = sb.toString();
			ArrayList<Object> arr = ExecuteSQLApiClientUtils.getList(String.format(getUrl, host, port, path, SELECT,
					URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")), TaskModel.class);
			System.out.println(sb.toString());
			int count = arr.size();
			for (int i = 0; i < count; i++) {
				TaskModel task = (TaskModel) arr.get(i);
				tasklist.add(task);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		return tasklist;
	}
	
	public boolean createspotchecktask(Long taskid, Long newprojectid) throws Exception {
		boolean bret = false;
		try {
			
			TaskModel task = null;
			//insert into tb_task(keywordid, tasktype,projectid)  values( (select keywordid	from tb_task where id = 1549634),17002,1);
			StringBuilder sb = new StringBuilder();
			sb.append("insert into tb_task(keywordid, tasktype,projectid)  values( (select keywordid from tb_task where id = "+ taskid);
			sb.append(" ),17002," + newprojectid);
			sb.append(" ) returning tb_task.*" );
			String sql = sb.toString();
			System.out.println(sb.toString());
			
			task = (TaskModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT),
					contentType, "sql=" + sql, TaskModel.class);
			
			
			if(task != null) {
				StringBuilder sb2 = new StringBuilder();
				//数据问题： 有一个oid 写多次情况
				sb2.append("insert into tb_task_link_poi(poiid,taskid)values( (select distinct poiid from tb_task_link_poi where taskid="+taskid);
				sb2.append(" )," + task.getId());
				sb2.append(")");
				System.out.println(sb2.toString());
				
				Long ret = ExecuteSQLApiClientUtils.update( String.format(getUrl, host,port,path,UPDATE,
						URLEncoder.encode(URLEncoder.encode(sb2.toString(), "utf-8"),"utf-8")));
				if (ret > 0) {
					StringBuilder sb3 = new StringBuilder();
					sb3.append("insert into tb_spotchecktask_link_task (oldtaskid,newtaskid)values(" + taskid);
					sb3.append("," + task.getId());
					sb3.append(" )");

					System.out.println(sb3.toString());
					ret = ExecuteSQLApiClientUtils.update( String.format(getUrl, host,port,path,UPDATE,
							URLEncoder.encode(URLEncoder.encode(sb3.toString(), "utf-8"),"utf-8")));
					bret = true;
				}

			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		return bret;
	}
	
	public boolean insertSpotcheckprojectinfo(Long projectid,Integer editid,Integer percent, Long newprojectid) throws Exception {
		boolean bret = false;
		try {
			TaskModel task = null;
			//insert into tb_task(keywordid, tasktype,projectid)  values( (select keywordid	from tb_task where id = 1549634),17002,1);
			StringBuilder sb = new StringBuilder();
			sb.append("insert into tb_spotcheckprojectinfo(projectid, editid,percent,newprojectid) values(  "+ projectid);
			sb.append("," + editid);
			sb.append("," + percent);
			sb.append("," + newprojectid);
			sb.append(" ) " );
			String sql = sb.toString();
			Long ret = ExecuteSQLApiClientUtils.update( String.format(getUrl, host,port,path,UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"),"utf-8")));
				
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		return bret;
	}
	
}
