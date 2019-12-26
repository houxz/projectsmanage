package com.emg.poiwebeditor.client;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.emg.poiwebeditor.common.Common;
import com.emg.poiwebeditor.common.DatabaseType;
import com.emg.poiwebeditor.common.TypeEnum;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.TaskLinkPoiModel;
import com.emg.poiwebeditor.pojo.TaskModel;

@Service
public  class TaskClient {

	@Value("${taskApi.host}")
	protected String host;
	@Value("${taskApi.port}")
	protected String port;
	@Value("${taskApi.path}")
	protected String path;
	
	protected final static String SELECT = "select";
	protected final static String UPDATE = "update";
	
	private static final Logger logger = LoggerFactory.getLogger(TaskClient.class);
	
	/*private String getUrl = "http://%s:%s/%s/mergetask/%s/%s/execute";
	private String postUrl = "http://%s:%s/%s/mergetask/%s";*/
	protected String getUrl = "http://%s:%s/%s/poitask/%s/%s/execute";
	protected String postUrl = "http://%s:%s/%s/poitask/%s";
	
	protected String contentType = "application/x-www-form-urlencoded";
	
	// public abstract TaskModel selectNextTask(List<Long> projectIDs, Integer userid)throws Exception;
	
	
	
	/**
	 * 获取用户已经占用的任务，即task.editid = userid
	 * @param projectIDs
	 * @param userid
	 * @param num
	 * @return
	 * @throws Exception
	 */
	public List selectUserTask(List<Long> projectIDs, Integer userid, long num, TypeEnum type) throws Exception {
		List tasks =  null;
		try {
			
			StringBuffer sql2 = new StringBuffer();
			
			sql2.append(" with ");
			sql2.append(" sorttable(projectid, sortvalue) as (values"); //(762,0), (824, 1), (883, 2), (763, 3), (761, 4))  ");
			for (int i = 0; i < projectIDs.size(); i++) {
				sql2.append("(").append(projectIDs.get(i)).append(",").append(i).append(")").append(",");
			}
			sql2.deleteCharAt(sql2.length() - 1);
			sql2.append(")");
			sql2.append(" update tb_task set operatetime=now(), ");
			// sql2.append(" update tb_task set starttime=now(),operatetime=now(), ");
			sql2.append(" state=").append(type.getState()).append(", ");
			sql2.append(" process=").append(type.getProcess()).append(", ");
			sql2.append(type.getUserColumn()).append("=").append(userid);
			sql2.append(" from ( ");
			sql2.append(" select id from tb_task t ");
			sql2.append(" join sorttable p on p.projectid = t.projectid ");
			// sql2.append(" where  t.projectid=p.projectid and (state=1 and process=5 ) and editid=").append(userid);
			sql2.append(" where  t.projectid=p.projectid and ");
			sql2.append(" (state=").append(type.getState()).append(" and ").append("process=").append(type.getProcess()).append(")");
			sql2.append(" and ").append(type.getUserColumn()).append("=").append(userid);
			
			sql2.append(" and tasktype=").append(type.getTaskType()).append(" order by p.sortvalue, t.id limit ").append(num).append("  for update ) as b(id) where tb_task.id = b.id returning tb_task.* ");

			logger.debug(sql2.toString());
			tasks =  ExecuteSQLApiClientUtils.postList(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql2.toString(), TaskModel.class);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return tasks;
	}
	
	
	
	/**
	 *  获取初始状态的任务，即task原状态为state=0, process=0，占用后任务状态为制作为1，5 校正为1，7
	 * @param projectIDs
	 * @param userid
	 * @param num
	 * @param type 初始状态
	 * @param using 占用状态
	 * @return
	 * @throws Exception
	 */
	public List selectUserInitTask(List<Long> projectIDs, Integer userid, long num, TypeEnum type, TypeEnum using) throws Exception {
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
			sql2.append(" update tb_task set starttime=now(),operatetime=now(),"); //state=1,process=5,editid=  ").append(userid);
			sql2.append(" state=").append(using.getState()).append(", ");
			sql2.append(" process=").append(using.getProcess()).append(", ");
			sql2.append(using.getUserColumn()).append("=").append(userid);
			sql2.append(" from ( ");
			sql2.append(" select id from tb_task t ");
			sql2.append(" join sorttable p on p.projectid = t.projectid ");
			sql2.append(" where  t.projectid=p.projectid and "); //(state=0 and process=0 ) and ( editid=0 or editid ISNULL) and tasktype=17001  order by p.sortvalue, t.id limit ");
			sql2.append(" (state=").append(type.getState()).append(" and ").append("process=").append(type.getProcess()).append(")");
			sql2.append(" and (").append(type.getUserColumn()).append("=0").append(" or ").append(type.getUserColumn()).append(" ISNULL )");
			sql2.append(" and tasktype=").append(type.getTaskType()).append("  order by p.sortvalue, t.id limit ");
			sql2.append(num).append("  for update ) as b(id) where tb_task.id = b.id returning tb_task.* ");

			logger.debug(sql2.toString());
			
			tasks =  ExecuteSQLApiClientUtils.postList(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql2.toString(), TaskModel.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return tasks;
	}
	
	
	
	public Long submitTask(Long taskid, Integer editid, TypeEnum type) throws Exception {
		Long ret = -1L;
		try {
			String sql = submitTaskSQL(taskid, editid, type);
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE, URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	
	
	private String submitTaskSQL(Long taskid, Integer editid, TypeEnum type) {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE tb_task");
		sb.append(" SET endtime=now(),operatetime=now(),state = ").append(type.getState());
		sb.append(" WHERE id = " + taskid);
		sb.append(" AND ").append(type.getUserColumn()).append(" = " + editid);
		return sb.toString();
	}
	
	//获取批次的keyid集合
	/*public Boolean InsertNewTask(ConfigDBModel configDBModel,Long projectid,Long shapeid,Integer state){
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
			sql.append(" (name,projectid,priority,rank,keywordid,state,tasktype) ");
			sql.append(" values('name'," + projectid +",0,0,"+ shapeid +" ," + state +","+"17001"+")");
				
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
	}*/
	//每次提交的时候向tb_link_poi中插入一条数据
	public Long InsertNewPOITask(Long taskid, Long oid) throws Exception{
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("insert into task.tb_task_link_poi(poiid, updatetime, taskid) values(");
			sb.append(oid).append(",").append("now()").append(",").append(taskid).append(") on conflict(poiid, taskid) do update set updatetime=now()");
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE, URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	//每次提交的时候向tb_link_poi中插入一条数据
		public Long updateLinkPoiTask(Long taskid, Long oid) throws Exception{
			Long ret = -1L;
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("update task.tb_task_link_poi set poiid=").append(oid);
				sb.append(", updatetime = now() where taskid = ").append(taskid);
				
				ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE, URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			return ret;
		}
		
		public String getLinkPoiIds(Long taskid) throws Exception{
			List links = null;
			String ids = "";
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("select poiid from tb_task_link_poi where taskid = ").append(taskid);
				
				links =  ExecuteSQLApiClientUtils.postList(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sb.toString(), TaskLinkPoiModel.class);
				if (links != null) {
					for (int i = 0; i < links.size(); i++) {
						TaskLinkPoiModel task = (TaskLinkPoiModel)links.get(i);
						ids += task.getPoiId() + ",";
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			return ids.length() > 0 ? ids.substring(0, ids.length() -1 ) :  ids;
		}
		
		//查询该任务是否已经标记了错误
		public int isMarkError(Long taskid) throws Exception{
			int ret = -1;
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(" select count(id) from tb_task_link_error where taskid=").append(taskid);
				
				ret = ExecuteSQLApiClientUtils.queryCount(String.format(getUrl, host, port, path, SELECT, URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
			return ret;
		}
	
	
	/**
	 * 把稍后修改的状态，改回初始状态（5，5-->1, 5,   5,7-->1, 7）
	 * @param editid
	 * @param type 原始状态
	 * @param typeUsed 被占用状态
	 * @return
	 * @throws Exception
	 */
	public Long updateTaskState_clear(Integer editid, TypeEnum type, TypeEnum typeUsed) throws Exception {
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE tb_task");
			sb.append(" SET operatetime=now(),state = " ).append(type.getState());
			sb.append(",process =" ).append(type.getProcess());
			sb.append(" WHERE state = ").append(typeUsed.getState()).append(" and process = ").append(typeUsed.getProcess());
			if (editid > 0) {
				sb.append(" and  ").append(typeUsed.getUserColumn()).append("=").append(editid);
			}
			logger.warn("updateTaskState_clear:" + sb.toString());
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	/**
	 * 把稍后修改的状态，改回初始状态（5，5-->1, 5,   5,7-->1, 7）
	 * @param editid
	 * @param type 原始状态
	 * @param typeUsed 被占用状态
	 * @return
	 * @throws Exception
	 */
	public Long updateTaskState_logout(Integer editid, TypeEnum type, TypeEnum typeUsed) throws Exception {
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE tb_task");
			sb.append(" SET operatetime=now(),state = " ).append(type.getState());
			sb.append(",process =" ).append(type.getProcess());
			sb.append(" WHERE state = ").append(typeUsed.getState()).append(" and process = ").append(typeUsed.getProcess());
			sb.append(" and  ").append(typeUsed.getUserColumn()).append("=").append(editid);
			if (editid < 1) {
				throw new Exception("当前退出的用户id不能为空");
			}
			logger.warn("updateTaskState_logout" + sb.toString());
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	
	/**
	 * 把消息队列中占用任务恢复回去,把任务状态改成0，0
	 * @param taskid
	 * @param editid
	 * @param state
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public Long initTaskState_logout(Integer editid, TypeEnum type) throws Exception {
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE tb_task");
			sb.append(" SET operatetime='2088-01-01 00:00',state = 0");
			sb.append(",process = 0,  starttime = null, ");
			sb.append(type.getUserColumn()).append("=0 ");
			sb.append(" WHERE state = ").append(type.getState()).append(" and process = ").append(type.getProcess());
			
			sb.append(" and  ").append(type.getUserColumn()).append(" = " + editid);
			if (editid < 0) {
				throw new Exception("initTaskState_logout:" + sb.toString());
			}
			logger.warn("initTaskState_logout: " + sb.toString());
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	/**
	 * 把消息队列中占用任务恢复回去,把任务状态改成0，0
	 * @param taskid
	 * @param editid
	 * @param state
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public Long initTaskState_clear(Integer editid, TypeEnum type) throws Exception {
		Long ret = -1L;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE tb_task");
			sb.append(" SET operatetime='2099-01-01 00:00',state = 0");
			sb.append(",process = 0,  starttime = null, ");
			sb.append(type.getUserColumn()).append("=0 ");
			sb.append(" WHERE state = ").append(type.getState()).append(" and process = ").append(type.getProcess());
			if (editid > 0) {
				sb.append(" and  ").append(type.getUserColumn()).append(" = " + editid);
			}
			logger.warn("initTaskState_clear: " + sb.toString());
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
		
		
		
		
	
	// 查询项目pid下的所有任务
	public Integer selectTaskTotalCountByProjectId(String projectid) throws Exception {
		Integer count = 0;
		try { 
				StringBuilder sb = new StringBuilder();
				sb.append("select count(1) from tb_task where projectid in(" + projectid + ")");
		
				String sql = sb.toString();		
				count = ExecuteSQLApiClientUtils.queryCount( String.format(getUrl, host, port, path, SELECT,
							URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")));

		} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
		}

		return count;
	}
	
	// 查询项目pid下的所有任务
	public Integer selectTaskDoneCountByProjectId(String projectid) throws Exception {
		Integer count = 0;
		try { 
				StringBuilder sb = new StringBuilder();
				sb.append("select count(1) from tb_task where state = 3 and projectid in(" + projectid + ")");
		
				String sql = sb.toString();		
				count = ExecuteSQLApiClientUtils.queryCount( String.format(getUrl, host, port, path, SELECT,
							URLEncoder.encode(URLEncoder.encode(sql, "utf-8"), "utf-8")));

		} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
		}

		return count;
	}
	
	/**
	 * 修改任务状态为稍后修改， 根据任务id, 修改任务状态
	 * @param taskid
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Long updateUsedTask(Long taskid,  TypeEnum type) throws Exception {
		Long ret = -1L;
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE tb_task");
			sb.append(" SET operatetime=now(),state = ").append(type.getState());
			sb.append(",process =" ).append(type.getProcess());
			sb.append(" WHERE id = " + taskid);
			ret = ExecuteSQLApiClientUtils.update(String.format(getUrl, host, port, path, UPDATE,
					URLEncoder.encode(URLEncoder.encode(sb.toString(), "utf-8"), "utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return ret;
	}
	
	/**
	 * 获取用户已经占用的任务，即task.editid = userid
	 * @param projectIDs
	 * @param userid
	 * @param num
	 * @return
	 * @throws Exception
	 */
	public TaskModel getTaskByID(long taskid) throws Exception {
		TaskModel task =  null;
		try {
			
			StringBuffer sql2 = new StringBuffer();
			
			sql2.append("select * from tb_task where id=").append(taskid);
			logger.debug(sql2.toString());
			task = (TaskModel) ExecuteSQLApiClientUtils.postModel(String.format(postUrl, host, port, path, SELECT), contentType, "sql=" + sql2.toString(), TaskModel.class);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		return task;
	}
}
