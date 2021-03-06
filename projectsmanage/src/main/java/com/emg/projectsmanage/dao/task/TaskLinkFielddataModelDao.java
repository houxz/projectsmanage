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
public class TaskLinkFielddataModelDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskLinkFielddataModelDao.class);
	
	public List<Map<String, Object>> groupTaskLinkFielddataByTime(ConfigDBModel configDBModel, String time) {
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
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_link_fielddata ");
			sql.append(" WHERE pstate = 2 AND groupid > 0 ");
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
	
	// add by lianhr begin 2018/12/13
	public List<Map<String, Object>> groupTaskLinkFielddataByTime(ConfigDBModel configDBModel, String[] times,
			String time) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (time == null || time.isEmpty())
				return list;

			String startTime = String.format("%s " + times[0], time);
			String endTime = String.format("%s " + times[1], time);

			boolean timeFlag = false;
			if (times[0].equals("08:30:00") && times[1].equals("17:30:00")) {
				timeFlag = true;
			}

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	taskid,");
			//add by lianhr begin 2019/03/08
			sql.append("	array_to_string(ARRAY(SELECT unnest(array_agg(shapeid))),',') AS shapeid ,");
			//add by lianhr end
			sql.append("	COUNT ( 1 ) AS count");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_link_fielddata ");
			sql.append(" WHERE pstate = 2 AND groupid > 0 ");
			if (timeFlag) {
				sql.append("	AND updatetime BETWEEN '" + startTime + "' AND '" + endTime + "' ");
			} else {
				sql.append("	AND ((updatetime BETWEEN '" + String.format("%s " + "00:00:00", time) + "' AND '"
						+ String.format("%s " + "08:29:59", time) + "') or (updatetime BETWEEN '"
						+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time)
						+ "')) ");
			}
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
	// add by lianhr end

}
