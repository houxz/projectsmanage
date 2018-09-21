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
			sql.append("	COUNT ( 1 ) AS count");
			sql.append("	SUM( CASE WHEN errortype BETWEEN 10000000000 AND 19999999999 THEN 1 ELSE 0 END ) AS errorcount,");
			sql.append("	SUM( CASE WHEN errortype BETWEEN 20000000000 AND 29999999999 THEN 1 ELSE 0 END ) AS visualerrorcount");
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

}
