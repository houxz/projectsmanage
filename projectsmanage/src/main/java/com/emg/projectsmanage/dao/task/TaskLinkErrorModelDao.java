package com.emg.projectsmanage.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.Common;
import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.pojo.ConfigDBModel;

@Component
public class TaskLinkErrorModelDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskLinkErrorModelDao.class);
	
	public List<Map<String, Object>> groupTaskLinkErrorByTime(ConfigDBModel configDBModel, String time) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (time == null || time.isEmpty())
				return list;
			
			String startTime = time;
			String endTime = String.format("%s 23:59:59", time);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	taskid,");
			sql.append("	COUNT ( 1 )");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_link_error ");
			sql.append(" WHERE pstate = 2 ");
			sql.append("	AND updatetime BETWEEN '" + startTime + "' AND '" + endTime + "' ");
			sql.append(" GROUP BY taskid");

			dataSource = Common.getDataSource(configDBModel);
			list = new JdbcTemplate(dataSource).queryForList(sql.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			list = new ArrayList<Map<String, Object>>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return list;
	}

	public Integer countTaskLinkErrorByTaskid(ConfigDBModel configDBModel, Long taskid, String time) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			String separator = Common.getDatabaseSeparator(dbtype);
			
			String startTime = time;
			String endTime = String.format("%s 23:59:59", time);
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_task_link_error ");
			sql.append(" WHERE pstate = 2");
			if (taskid != null && taskid.compareTo(0L) > 0) {
				sql.append(" AND " + separator + "taskid" + separator + " = " + taskid);
			}
			if (time != null && !time.isEmpty()) {
				sql.append(" AND " + separator + "updatetime" + separator + " IS NOT NULL ");
				sql.append(String.format(" AND (" + separator + "updatetime" + separator + " BETWEEN '%s' AND '%s')", startTime, endTime));
			}

			dataSource = Common.getDataSource(configDBModel);
			count = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			count = -1;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return count;
	}
	
	public Integer countTaskLinkVisualErrorByTaskid(ConfigDBModel configDBModel, Long taskid, String time) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			String separator = Common.getDatabaseSeparator(dbtype);
			
			String startTime = time;
			String endTime = String.format("%s 23:59:59", time);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_task_link_error ");
			sql.append(" WHERE pstate = 2");
			sql.append(" AND (errortype BETWEEN 19999999999 AND 30000000000)");
			if (taskid != null && taskid.compareTo(0L) > 0) {
				sql.append(" AND " + separator + "taskid" + separator + " = " + taskid);
			}
			if (time != null && !time.isEmpty()) {
				sql.append(" AND " + separator + "updatetime" + separator + " IS NOT NULL ");
				sql.append(String.format(" AND (" + separator + "updatetime" + separator + " BETWEEN '%s' AND '%s')", startTime, endTime));
			}

			dataSource = Common.getDataSource(configDBModel);
			count = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			count = -1;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return count;
	}
	
}
