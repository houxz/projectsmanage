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

	// add by lianhr begin 2018/12/13
	public List<Map<String, Object>> groupTaskBlockDetailsByTime(ConfigDBModel configDBModel, String[] times,
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
			sql.append("	blockid,");
			sql.append("	editid,");
			if(timeFlag){
				sql.append("	sum( CASE WHEN editid > 0 AND ( edittime BETWEEN '" + startTime + "' AND '" + endTime
						+ "' ) THEN 1 ELSE 0 END ) AS editnum,");
			} else {
				sql.append("	sum( CASE WHEN editid > 0 AND (( edittime BETWEEN '" + String.format("%s " + "00:00:00", time) +"' AND '" + String.format("%s " + "08:29:59", time) + "' ) or (edittime BETWEEN '"+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time) +"')) THEN 1 ELSE 0 END ) AS editnum,");
			}
			
			sql.append("	checkid,");
			if (timeFlag) {
				sql.append("	sum( CASE WHEN checkid > 0 AND ( checktime BETWEEN '" + startTime + "' AND '" + endTime
						+ "' ) THEN 1 ELSE 0 END ) AS checknum, ");
			}else {
				sql.append("	sum( CASE WHEN checkid > 0 AND (( checktime BETWEEN '" + String.format("%s " + "00:00:00", time) +"' AND '" + String.format("%s " + "08:29:59", time) + "' ) or (checktime BETWEEN '"+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time) +"')) THEN 1 ELSE 0 END ) AS checknum,");
			}
			sql.append("array_to_string(ARRAY(SELECT unnest(array_agg(featureid))),',') AS featureid");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_blockdetail ");
			sql.append(" WHERE pstate = 2");
			sql.append("	AND (");
			if (timeFlag) {
				sql.append("	( editid > 0 AND ( edittime BETWEEN '" + startTime + "' AND '" + endTime + "' ) ) ");
				sql.append(
						"	OR ( checkid > 0 AND ( checktime BETWEEN '" + startTime + "' AND '" + endTime + "' ) ) ");
			} else {
				sql.append("	( editid > 0 AND (( edittime BETWEEN '" + String.format("%s " + "00:00:00", time)
						+ "' AND '" + String.format("%s " + "08:29:59", time) + "' ) or ( edittime BETWEEN '"
						+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time)
						+ "' )) ) ");
				sql.append("	OR ( checkid > 0 AND ( (checktime BETWEEN '" + String.format("%s " + "00:00:00", time)
						+ "' AND '" + String.format("%s " + "08:29:59", time) + "') or (checktime BETWEEN '"
						+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time)
						+ "') ) ) ");
			}
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
	
	public List<String> groupTaskBlockDetailsByTimeFeatureids(ConfigDBModel configDBModel, String[] times,
			String time) {
		List<String> list = new ArrayList<String>();
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
			sql.append("select array_to_string(ARRAY(SELECT unnest(array_agg( T.featureid))),',') AS featureid from(");
			sql.append(" SELECT");
			sql.append("	blockid,");
			sql.append("	editid,");
			if(timeFlag){
				sql.append("	sum( CASE WHEN editid > 0 AND ( edittime BETWEEN '" + startTime + "' AND '" + endTime
						+ "' ) THEN 1 ELSE 0 END ) AS editnum,");
			} else {
				sql.append("	sum( CASE WHEN editid > 0 AND (( edittime BETWEEN '" + String.format("%s " + "00:00:00", time) +"' AND '" + String.format("%s " + "08:29:59", time) + "' ) or (edittime BETWEEN '"+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time) +"')) THEN 1 ELSE 0 END ) AS editnum,");
			}
			
			sql.append("	checkid,");
			if (timeFlag) {
				sql.append("	sum( CASE WHEN checkid > 0 AND ( checktime BETWEEN '" + startTime + "' AND '" + endTime
						+ "' ) THEN 1 ELSE 0 END ) AS checknum ");
			}else {
				sql.append("	sum( CASE WHEN checkid > 0 AND (( checktime BETWEEN '" + String.format("%s " + "00:00:00", time) +"' AND '" + String.format("%s " + "08:29:59", time) + "' ) or (checktime BETWEEN '"+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time) +"')) THEN 1 ELSE 0 END ) AS checknum,");
			}
			sql.append("array_to_string(ARRAY(SELECT unnest(array_agg(featureid))),',') AS featureid");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_blockdetail ");
			sql.append(" WHERE pstate = 2");
			sql.append("	AND (");
			if (timeFlag) {
				sql.append("	( editid > 0 AND ( edittime BETWEEN '" + startTime + "' AND '" + endTime + "' ) ) ");
				sql.append(
						"	OR ( checkid > 0 AND ( checktime BETWEEN '" + startTime + "' AND '" + endTime + "' ) ) ");
			} else {
				sql.append("	( editid > 0 AND (( edittime BETWEEN '" + String.format("%s " + "00:00:00", time)
						+ "' AND '" + String.format("%s " + "08:29:59", time) + "' ) or ( edittime BETWEEN '"
						+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time)
						+ "' )) ) ");
				sql.append("	OR ( checkid > 0 AND ( (checktime BETWEEN '" + String.format("%s " + "00:00:00", time)
						+ "' AND '" + String.format("%s " + "08:29:59", time) + "') or (checktime BETWEEN '"
						+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time)
						+ "') ) ) ");
			}
			sql.append("	) ");
			sql.append(" GROUP BY editid, checkid, blockid) T");

			dataSource = Common.getDataSource(configDBModel);
			list = new JdbcTemplate(dataSource).queryForList(sql.toString(), String.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			list = new ArrayList<String>();
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

	// add by lianhr begin 201/12/13
	public List<Map<String, Object>> group15102ByTime(ConfigDBModel configDBModel, String[] times, String time) {
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
			sql.append("	A.projectid,");
			//sql.append("	A.blockid,");
			//sql.append("	B.featureid,");
			sql.append("	array_to_string(ARRAY(SELECT unnest(array_agg(B.featureid))),',') AS featureid,");
			sql.append("	B.editid,");
			sql.append("	A.tasktype,");
			sql.append("	B.checkid");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task A,");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_blockdetail B ");
			sql.append(" WHERE A.blockid = B.blockid AND B.pstate = 2 ");
			sql.append("	AND A.tasktype in (15102,15110,15111,15210,15211)");
			if (timeFlag) {
				sql.append("	AND ( B.edittime BETWEEN '" + startTime + "' AND '" + endTime + "' ) ");
			} else {
				sql.append("	AND (( B.edittime BETWEEN '" + String.format("%s " + "00:00:00", time) + "' AND '"
						+ String.format("%s " + "08:29:59", time) + "' ) or ( B.edittime BETWEEN '"
						+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time)
						+ "' )) ");
			}
			//sql.append(" GROUP BY A.tasktype,	A.projectid,	A.blockid,		A.editid,	A.checkid ");
			sql.append(" GROUP BY A.tasktype,	A.projectid,		B.editid,	B.checkid ");

			//TODO:
			logger.debug("001 : group15102ByTime: " + sql.toString());
			
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

	// add by lianhr begin 201/12/13
	
	public List<Map<String, Object>> group15102ByPoi(ConfigDBModel configDBModel, Long featureid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	oid,");
			sql.append("	attrvalue");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_poi_tags");
			sql.append(" WHERE oid=" + featureid);
			sql.append(" and attrname = 'remark'");
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

	// add by lianhr begin 201/12/13
	
	public List<Map<String, Object>> group15102ByPoiDelete(ConfigDBModel configDBModel, Long featureid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	oid,");
			sql.append("	ver,");
			sql.append("	isdel");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_poi");
			sql.append(" WHERE oid=" + featureid);
			sql.append(" and ((ver is null and isdel = true) or (ver is not null and isdel = true))");
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
	
	// add by lianhr begin 201/12/13
		
		public List<Map<String, Object>> group15102ByPoi(ConfigDBModel configDBModel, String featureid, String condition) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			BasicDataSource dataSource = null;
			try {

				StringBuffer sql = new StringBuffer();
				sql.append(" SELECT");
				sql.append("	count(*) as countnum");
				sql.append(" FROM ");
				sql.append(configDBModel.getDbschema()).append(".");
				sql.append(" tb_poi_tags");
				//modified by lianhr begin 2019/03/08
				//sql.append(" WHERE oid in (" + featureid + ")");
				if(featureid == null || featureid.equals("")){
					sql.append(" WHERE 1=0");
				} else {
					sql.append(" WHERE oid in (" + featureid + ")");
				}
				sql.append(" and attrname = 'remark' and attrvalue like'%" + condition + "%'");
				//TODO:
				logger.debug("004 : group15102ByTime: featureIdSql " + sql.toString());
				//modified by lianhr end
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
	
		// add by lianhr begin 201/12/13
		// ͨ��POI��ѯ
		public List<Map<String, Object>> group15102ByPoiDelete(ConfigDBModel configDBModel, String featureid, String condition) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			BasicDataSource dataSource = null;
			try {

				StringBuffer sql = new StringBuffer();
				sql.append(" SELECT");
				sql.append("	count(*) as countnum");
				sql.append(" FROM ");
				sql.append(configDBModel.getDbschema()).append(".");
				sql.append(" tb_poi");
				//modified by lianhr begin 2019/03/08
				//sql.append(" WHERE oid in (" + featureid + ")");
				if(featureid == null || featureid.equals("")){
					sql.append(" WHERE 1=0 ");
				} else {
					sql.append(" WHERE oid in (" + featureid + ") ");
				}
				//modified by lianhr end
				sql.append(" and (" + condition + " and isdel = true)");
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
