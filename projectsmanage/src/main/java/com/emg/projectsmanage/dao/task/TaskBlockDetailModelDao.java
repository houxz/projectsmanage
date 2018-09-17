package com.emg.projectsmanage.dao.task;

import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.Common;
import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.common.OperateType;
import com.emg.projectsmanage.pojo.ConfigDBModel;

@Component
public class TaskBlockDetailModelDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskBlockDetailModelDao.class);

	public Integer countModifyPOIByBlockid(ConfigDBModel configDBModel, Long blockid, String time) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_task_blockdetail ");
			sql.append(" WHERE pstate = 2");
			sql.append(" AND operatetype = " + OperateType.CONFIRMMODIFY.getValue());
			if (blockid != null && blockid.compareTo(0L) > 0) {
				sql.append(" AND " + separator + "blockid" + separator + " = " + blockid);
			}
			if(time != null && !time.isEmpty()) {
				sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator, time, time));
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
	
	public Integer countCreatePOIByBlockid(ConfigDBModel configDBModel, Long blockid, String time) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_task_blockdetail ");
			sql.append(" WHERE pstate = 2");
			sql.append(" AND operatetype = " + OperateType.NEW.getValue());
			if (blockid != null && blockid.compareTo(0L) > 0) {
				sql.append(" AND " + separator + "blockid" + separator + " = " + blockid);
			}
			if(time != null && !time.isEmpty()) {
				sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator, time, time));
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
	
	public Integer countDeletePOIByBlockid(ConfigDBModel configDBModel, Long blockid, String time) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_task_blockdetail ");
			sql.append(" WHERE pstate = 2");
			sql.append(" AND operatetype = " + OperateType.DELETE.getValue());
			if (blockid != null && blockid.compareTo(0L) > 0) {
				sql.append(" AND " + separator + "blockid" + separator + " = " + blockid);
			}
			if(time != null && !time.isEmpty()) {
				sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator, time, time));
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
	
	public Integer countConfirmPOIByBlockid(ConfigDBModel configDBModel, Long blockid, String time) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_task_blockdetail ");
			sql.append(" WHERE pstate = 2");
			sql.append(" AND operatetype = " + OperateType.CONFIRM.getValue());
			if (blockid != null && blockid.compareTo(0L) > 0) {
				sql.append(" AND " + separator + "blockid" + separator + " = " + blockid);
			}
			if(time != null && !time.isEmpty()) {
				sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator, time, time));
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
