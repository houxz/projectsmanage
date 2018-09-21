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
import com.emg.projectsmanage.common.OperateType;
import com.emg.projectsmanage.common.RoleType;
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

	public Integer countPOIByBlockid(ConfigDBModel configDBModel, Long blockid, Integer userid, String time,
			RoleType roleType) {
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
			if (blockid != null && blockid.compareTo(0L) > 0) {
				sql.append(" AND " + separator + "blockid" + separator + " = " + blockid);
			}
			if (time != null && !time.isEmpty()) {
				if (roleType.equals(RoleType.ROLE_WORKER)) {
					sql.append(String.format(" AND %seditid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				} else if (roleType.equals(RoleType.ROLE_CHECKER)) {
					sql.append(String.format(" AND %scheckid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %schecktime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				}
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

	public Integer countModifyPOIByBlockid(ConfigDBModel configDBModel, Long blockid, Integer userid, String time,
			RoleType roleType) {
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
			if (time != null && !time.isEmpty()) {
				if (roleType.equals(RoleType.ROLE_WORKER)) {
					sql.append(String.format(" AND %seditid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				} else if (roleType.equals(RoleType.ROLE_CHECKER)) {
					sql.append(String.format(" AND %scheckid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %schecktime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				}
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

	public Integer countCreatePOIByBlockid(ConfigDBModel configDBModel, Long blockid, Integer userid, String time,
			RoleType roleType) {
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
			if (time != null && !time.isEmpty()) {
				if (roleType.equals(RoleType.ROLE_WORKER)) {
					sql.append(String.format(" AND %seditid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				} else if (roleType.equals(RoleType.ROLE_CHECKER)) {
					sql.append(String.format(" AND %scheckid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %schecktime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				}
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

	public Integer countDeletePOIByBlockid(ConfigDBModel configDBModel, Long blockid, Integer userid, String time,
			RoleType roleType) {
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
			if (time != null && !time.isEmpty()) {
				if (roleType.equals(RoleType.ROLE_WORKER)) {
					sql.append(String.format(" AND %seditid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				} else if (roleType.equals(RoleType.ROLE_CHECKER)) {
					sql.append(String.format(" AND %scheckid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %schecktime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				}
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

	public Integer countConfirmPOIByBlockid(ConfigDBModel configDBModel, Long blockid, Integer userid, String time,
			RoleType roleType) {
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
			if (time != null && !time.isEmpty()) {
				if (roleType.equals(RoleType.ROLE_WORKER)) {
					sql.append(String.format(" AND %seditid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %sedittime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				} else if (roleType.equals(RoleType.ROLE_CHECKER)) {
					sql.append(String.format(" AND %scheckid%s = %d", separator, separator, userid));
					sql.append(String.format(" AND %schecktime%s BETWEEN '%s' AND '%s 23:59:59'", separator, separator,
							time, time));
				}
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
