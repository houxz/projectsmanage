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
public class TaskBlockDetailModelDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskBlockDetailModelDao.class);

	public List<Map<String, Object>> groupTaskBlockDetailsByTime(ConfigDBModel configDBModel, String time) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (time == null || time.isEmpty())
				return list;
			
			String startTime = time;
			String endTime = String.format("%s 23:59:59", time);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	blockid,");
			sql.append("	editid,");
			sql.append("	sum( CASE WHEN editid > 0 AND ( edittime BETWEEN '" + startTime + "' AND '" + endTime + "' ) THEN 1 ELSE 0 END ) AS editnum,");
			sql.append("	checkid,");
			sql.append("	sum( CASE WHEN checkid > 0 AND ( checktime BETWEEN '" + startTime + "' AND '" + endTime + "' ) THEN 1 ELSE 0 END ) AS checknum ");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_blockdetail ");
			sql.append(" WHERE pstate = 2");
			sql.append("	AND (");
			sql.append("	( editid > 0 AND ( edittime BETWEEN '" + startTime + "' AND '" + endTime + "' ) ) ");
			sql.append("	OR ( checkid > 0 AND ( checktime BETWEEN '" + startTime + "' AND '" + endTime + "' ) ) ");
			sql.append("	) ");
			sql.append(" GROUP BY editid, checkid, blockid");

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
