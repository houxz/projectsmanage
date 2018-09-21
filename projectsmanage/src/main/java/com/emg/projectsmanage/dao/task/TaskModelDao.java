package com.emg.projectsmanage.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.Common;
import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.common.ProcessType;
import com.emg.projectsmanage.common.StateMap;
import com.emg.projectsmanage.common.TaskTypeEnum;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.TaskModel;

@Component
public class TaskModelDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskModelDao.class);

	private TaskTypeEnum getTaskType(ProcessType processType) {
		TaskTypeEnum tasktype = TaskTypeEnum.UNKNOWN;
		switch (processType) {
		case ERROR:
			tasktype = TaskTypeEnum.ERROR;
			break;
		case NRFC:
			tasktype = TaskTypeEnum.NRFC;
			break;
		case ATTACH:
			tasktype = TaskTypeEnum.ATTACH;
			break;
		default:
			tasktype = TaskTypeEnum.UNKNOWN;
			break;
		}
		return tasktype;
	}

	public List<TaskModel> selectTaskModels(ConfigDBModel configDBModel, TaskModel record, List<Long> projectids,
			List<Integer> editUserids, List<Integer> checkUserids, List<StateMap> stateMaps, Integer limit,
			Integer offset) {
		List<TaskModel> tasks = new ArrayList<TaskModel>();
		BasicDataSource dataSource = null;
		try {
			if (record == null || configDBModel == null)
				return tasks;
			Integer dbtype = configDBModel.getDbtype();
			ProcessType processType = ProcessType.valueOf(record.getProcesstype());
			TaskTypeEnum tasktype = getTaskType(processType);

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *, " + processType.getValue()
					+ " AS processType, to_char(operatetime, 'YYYY-MM-DD HH24:MI:SS') AS opttime FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE tasktype = " + tasktype.getValue());
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (projectids != null) {
				if (projectids.size() > 0) {
					sql.append(" AND " + separator + "projectid" + separator + " in( ");
					for (Long projectid : projectids) {
						sql.append(projectid);
						sql.append(",");
					}
					sql = sql.deleteCharAt(sql.length() - 1);
					sql.append(" )");
				} else {
					return tasks;
				}
			}
			if (stateMaps != null && stateMaps.size() > 0) {
				sql.append(" AND ( ");
				for (StateMap stateMap : stateMaps) {
					sql.append(" ( ");
					if (stateMap.getState() != null) {
						sql.append(" ( state = " + stateMap.getState() + ") AND");
					}
					if (stateMap.getCheckid() != null) {
						if (stateMap.getCheckid().equals(-1)) {
							sql.append(" ( checkid IS NULL OR checkid <= 0) AND");
						} else if (stateMap.getCheckid().equals(1)) {
							sql.append(" ( checkid > 0) AND");
						}
					}
					if (stateMap.getTasktype() != null) {
						sql.append(" ( tasktype = " + stateMap.getTasktype() + ") AND");
					}
					if (stateMap.getProcess() != null) {
						sql.append(" ( process = " + stateMap.getProcess() + ")");
					}
					sql.append(" ) OR");
				}
				sql = sql.delete(sql.length() - 2, sql.length());
				sql.append(" )");
			}
			if (editUserids != null && editUserids.size() > 0) {
				sql.append(" AND editid IN ( ");
				for (Integer editUserid : editUserids) {
					sql.append(editUserid + 500000);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			if (checkUserids != null && checkUserids.size() > 0) {
				sql.append(" AND checkid IN ( ");
				for (Integer checkUserid : checkUserids) {
					sql.append(checkUserid + 600000);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			sql.append(" ORDER BY id ");
			if (limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if (offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			dataSource = Common.getDataSource(configDBModel);
			tasks = new JdbcTemplate(dataSource).query(sql.toString(),
					new BeanPropertyRowMapper<TaskModel>(TaskModel.class));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			tasks = new ArrayList<TaskModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return tasks;
	}

	public Integer countTaskModels(ConfigDBModel configDBModel, TaskModel record, List<Long> projectids,
			List<Integer> editUserids, List<Integer> checkUserids, List<StateMap> stateMaps) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			if (record == null || configDBModel == null)
				return count;
			Integer dbtype = configDBModel.getDbtype();
			ProcessType processType = ProcessType.valueOf(record.getProcesstype());
			TaskTypeEnum tasktype = getTaskType(processType);

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE tasktype = " + tasktype.getValue());
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (projectids != null) {
				if (projectids.size() > 0) {
					sql.append(" AND " + separator + "projectid" + separator + " in( ");
					for (Long projectid : projectids) {
						sql.append(projectid);
						sql.append(",");
					}
					sql = sql.deleteCharAt(sql.length() - 1);
					sql.append(" )");
				} else {
					return 0;
				}
			}
			if (stateMaps != null && stateMaps.size() > 0) {
				sql.append(" AND ( ");
				for (StateMap stateMap : stateMaps) {
					sql.append(" ( ");
					if (stateMap.getState() != null) {
						sql.append(" ( state = " + stateMap.getState() + ") AND");
					}
					if (stateMap.getCheckid() != null) {
						if (stateMap.getCheckid().equals(-1)) {
							sql.append(" ( checkid IS NULL OR checkid <= 0) AND");
						} else if (stateMap.getCheckid().equals(1)) {
							sql.append(" ( checkid > 0) AND");
						}
					}
					if (stateMap.getTasktype() != null) {
						sql.append(" ( tasktype = " + stateMap.getTasktype() + ") AND");
					}
					if (stateMap.getProcess() != null) {
						sql.append(" ( process = " + stateMap.getProcess() + ")");
					}
					sql.append(" ) OR");
				}
				sql = sql.delete(sql.length() - 2, sql.length());
				sql.append(" )");
			}
			if (editUserids != null && editUserids.size() > 0) {
				sql.append(" AND editid IN ( ");
				for (Integer editUserid : editUserids) {
					sql.append(editUserid + 500000);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			if (checkUserids != null && checkUserids.size() > 0) {
				sql.append(" AND checkid IN ( ");
				for (Integer checkUserid : checkUserids) {
					sql.append(checkUserid + 600000);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
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
	
	public List<Map<String, Object>> groupTasksByTime(ConfigDBModel configDBModel, String time) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (time == null || time.isEmpty())
				return list;
			
			String startTime = time;
			String endTime = String.format("%s 23:59:59", time);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	tasktype,");
			sql.append("	projectid,");
			sql.append("	editid,");
			sql.append("	sum( CASE WHEN editid > 0 THEN 1 ELSE 0 END ) AS editnum,");
			sql.append("	checkid,");
			sql.append("	sum( CASE WHEN checkid > 0 THEN 1 ELSE 0 END ) AS checknum ");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task ");
			sql.append(" WHERE state = 2");
			sql.append("	AND operatetime BETWEEN '" + startTime + "' AND '" + endTime + "' ");
			sql.append("	AND ( editid > 0 OR checkid > 0 ) ");
			sql.append(" GROUP BY tasktype, projectid, editid, checkid");

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
	
	public TaskModel getTaskByID(ConfigDBModel configDBModel, Long taskid) {
		TaskModel task = new TaskModel();
		BasicDataSource dataSource = null;
		try {
			if (taskid == null || taskid.compareTo(0L) < 0)
				return task;
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE id = ?");

			dataSource = Common.getDataSource(configDBModel);
			task = new JdbcTemplate(dataSource).queryForObject(sql.toString(), new Object[]{taskid}, new BeanPropertyRowMapper<TaskModel>(TaskModel.class));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			task = new TaskModel();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return task;
	}
	
	public TaskModel getTaskByBlockid(ConfigDBModel configDBModel, Long blockid) {
		TaskModel task = new TaskModel();
		BasicDataSource dataSource = null;
		try {
			if (blockid == null || blockid.compareTo(0L) < 0)
				return task;
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE blockid = ?");
			sql.append(" LIMIT 1 ");

			dataSource = Common.getDataSource(configDBModel);
			task = new JdbcTemplate(dataSource).queryForObject(sql.toString(), new Object[]{blockid}, new BeanPropertyRowMapper<TaskModel>(TaskModel.class));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			task = new TaskModel();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return task;
	}

	public List<TaskModel> getTaskByTime(ConfigDBModel configDBModel, String time) {
		List<TaskModel> tasks = new ArrayList<TaskModel>();
		BasicDataSource dataSource = null;
		try {
			if (time == null || time.isEmpty())
				return tasks;
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE state = 1");
			sql.append(" OR ( state = 2");
			sql.append(String.format(" AND time IS NOT NULL AND time > '%s')", time));
			sql.append(" ORDER BY id ");

			dataSource = Common.getDataSource(configDBModel);
			tasks = new JdbcTemplate(dataSource).query(sql.toString(),
					new BeanPropertyRowMapper<TaskModel>(TaskModel.class));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			tasks = new ArrayList<TaskModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return tasks;
	}

}
