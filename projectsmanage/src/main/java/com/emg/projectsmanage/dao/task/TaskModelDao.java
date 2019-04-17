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

	private List<TaskTypeEnum> getTaskType(ProcessType processType) {
		List<TaskTypeEnum> tasktypes = new ArrayList<TaskTypeEnum>();
		switch (processType) {
		case ERROR:
			tasktypes.add(TaskTypeEnum.ERROR);
			break;
		case NRFC:
			tasktypes.add(TaskTypeEnum.NRFC);
			break;
		case ATTACH:
			tasktypes.add(TaskTypeEnum.ATTACH);
			break;
		//add by lianhr begin 2019/02/21
		case COUNTRY:
			tasktypes.add(TaskTypeEnum.QC_JIUGONGGE);
			tasktypes.add(TaskTypeEnum.QC_QUANYU);
			break;
		//add by lianhr edn
		case POIEDIT:
			tasktypes.addAll(TaskTypeEnum.getPoiEditTaskTypes());
			tasktypes.addAll(TaskTypeEnum.getPoiCheckTaskTypes());
			break;
		case ADJUSTMAP:
			tasktypes.add(TaskTypeEnum.ADJUSTMAP);
			break;
		case GEN:
			tasktypes.add(TaskTypeEnum.GEN_WEB);
			break;
		case AREA:
			tasktypes.add(TaskTypeEnum.AREA_QUHUAN);
			tasktypes.add(TaskTypeEnum.AREA_JIANCHENGQU);
			break;
		case ATTACHWITHDATA:
			tasktypes.add(TaskTypeEnum.ATTACHWITHDATA);
			break;
		default:
			tasktypes.add(TaskTypeEnum.UNKNOWN);
			break;
		}
		return tasktypes;
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
			
			List<TaskTypeEnum> tasktypes = getTaskType(processType);
			if (record.getTasktype() != null && record.getTasktype() > 0) {
				if (tasktypes.contains(TaskTypeEnum.valueOf(record.getTasktype()))) {
					tasktypes.clear();
					tasktypes.add(TaskTypeEnum.valueOf(record.getTasktype()));
				} else {
					return tasks;
				}
			}

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *, " + processType.getValue()
					+ " AS processType, to_char(operatetime, 'YYYY-MM-DD HH24:MI:SS') AS opttime FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE 1=1");
			if (tasktypes != null && tasktypes.size() > 0) {
				sql.append(" AND tasktype IN (");
				for (TaskTypeEnum tasktype : tasktypes) {
					sql.append(tasktype.getValue());
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (record.getBatchid() != null && record.getBatchid().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "batchid" + separator + " = " + record.getBatchid());
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
			if (record.getPriority() != null && record.getPriority().compareTo(-2) >= 0 && record.getPriority().compareTo(2) <= 0) {
				sql.append(" AND " + separator + "priority" + separator + " = " + record.getPriority());
			}
			if (stateMaps != null && stateMaps.size() > 0) {
				sql.append(" AND ( ");
				for (StateMap stateMap : stateMaps) {
					sql.append(" ( ");
					if (stateMap.getState() != null) {
						sql.append(" ( state = " + stateMap.getState() + ") ");
					}
					if (stateMap.getCheckid() != null) {
						if (stateMap.getCheckid().equals(-1)) {
							sql.append(" AND ( checkid IS NULL OR checkid <= 0) ");
						} else if (stateMap.getCheckid().equals(1)) {
							sql.append(" AND ( checkid > 0) ");
						}
					}
					if (stateMap.getTasktype() != null) {
						sql.append(" AND ( tasktype = " + stateMap.getTasktype() + ")");
					}
					if (stateMap.getProcess() != null) {
						sql.append(" AND ( process = " + stateMap.getProcess() + ")");
					}
					sql.append(" ) OR");
				}
				sql = sql.delete(sql.length() - 2, sql.length());
				sql.append(" )");
			}
			if (editUserids != null && editUserids.size() > 0) {
				sql.append(" AND editid IN ( ");
				for (Integer editUserid : editUserids) {
					sql.append(editUserid);
					sql.append(",");
					sql.append(editUserid + 500000);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			if (checkUserids != null && checkUserids.size() > 0) {
				sql.append(" AND checkid IN ( ");
				for (Integer checkUserid : checkUserids) {
					sql.append(checkUserid);
					sql.append(",");
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
			List<TaskTypeEnum> tasktypes = getTaskType(processType);
			if (record.getTasktype() != null && record.getTasktype() > 0) {
				if (tasktypes.contains(TaskTypeEnum.valueOf(record.getTasktype()))) {
					tasktypes.clear();
					tasktypes.add(TaskTypeEnum.valueOf(record.getTasktype()));
				} else {
					return 0;
				}
			}

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE 1=1");
			if (tasktypes != null && tasktypes.size() > 0) {
				sql.append(" AND tasktype IN (");
				for (TaskTypeEnum tasktype : tasktypes) {
					sql.append(tasktype.getValue());
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			
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
			if (record.getPriority() != null && record.getPriority().compareTo(-2) >= 0 && record.getPriority().compareTo(2) <= 0) {
				sql.append(" AND " + separator + "priority" + separator + " = " + record.getPriority());
			}
			//add by lianhr begin 2019/02/22
			if (record.getBatchid() != null ) {
				sql.append(" AND " + separator + "batchid" + separator + " = " + record.getBatchid());
			}
			//add by lianhr end
			if (stateMaps != null && stateMaps.size() > 0) {
				sql.append(" AND ( ");
				for (StateMap stateMap : stateMaps) {
					sql.append(" ( ");
					if (stateMap.getState() != null) {
						sql.append(" ( state = " + stateMap.getState() + ") ");
					}
					if (stateMap.getCheckid() != null) {
						if (stateMap.getCheckid().equals(-1)) {
							sql.append(" AND ( checkid IS NULL OR checkid <= 0) ");
						} else if (stateMap.getCheckid().equals(1)) {
							sql.append(" AND ( checkid > 0) ");
						}
					}
					if (stateMap.getTasktype() != null) {
						sql.append(" AND ( tasktype = " + stateMap.getTasktype() + ")");
					}
					if (stateMap.getProcess() != null) {
						sql.append(" AND ( process = " + stateMap.getProcess() + ")");
					}
					sql.append(" ) OR");
				}
				sql = sql.delete(sql.length() - 2, sql.length());
				sql.append(" )");
			}
			if (editUserids != null && editUserids.size() > 0) {
				sql.append(" AND editid IN ( ");
				for (Integer editUserid : editUserids) {
					sql.append(editUserid);
					sql.append(",");
					sql.append(editUserid + 500000);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			if (checkUserids != null && checkUserids.size() > 0) {
				sql.append(" AND checkid IN ( ");
				for (Integer checkUserid : checkUserids) {
					sql.append(checkUserid);
					sql.append(",");
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
	
	public List<Map<String, Object>> groupTasks(ConfigDBModel configDBModel, List<TaskTypeEnum> taskTypes) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	projectid,");
			sql.append("	state,");
			sql.append("	process,");
			sql.append("	editid,");
			sql.append("	checkid,");
			sql.append("	COUNT(1)");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task ");
			if (taskTypes != null && taskTypes.size() > 0) {
				sql.append(" WHERE tasktype IN ( ");
				for (TaskTypeEnum taskType : taskTypes) {
					sql.append(taskType.getValue());
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			sql.append(" GROUP BY");
			sql.append("	projectid,");
			sql.append("	state,");
			sql.append("	process,");
			sql.append("	editid,");
			sql.append("	checkid");

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
	
	public List<Map<String, Object>> groupCountryTasks(ConfigDBModel configDBModel, List<TaskTypeEnum> taskTypes) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	projectid,");
			sql.append("	state,");
			sql.append("	process,");
			sql.append("	COUNT(1)");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task ");
			if (taskTypes != null && taskTypes.size() > 0) {
				sql.append(" WHERE tasktype IN ( ");
				for (TaskTypeEnum taskType : taskTypes) {
					sql.append(taskType.getValue());
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			sql.append(" GROUP BY");
			sql.append("	projectid,");
			sql.append("	state,");
			sql.append("	process");

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
	
	public List<Map<String, Object>> groupPOITasks(ConfigDBModel configDBModel, List<TaskTypeEnum> taskTypes) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	projectid,");
			sql.append("	state,");
			sql.append("	COUNT(1)");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task ");
			if (taskTypes != null && taskTypes.size() > 0) {
				sql.append(" WHERE tasktype IN ( ");
				for (TaskTypeEnum taskType : taskTypes) {
					sql.append(taskType.getValue());
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			sql.append(" GROUP BY");
			sql.append("	projectid,");
			sql.append("	state");

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
	
	public List<Map<String, Object>> countErrorsByTaskid(ConfigDBModel configDBModel, List<Long> taskIDs) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	te.taskid,");
			sql.append("	COUNT( 1 ) AS total,");
			sql.append("	COUNT( CASE WHEN te.pstate IN ( 0, 1, 3, 4 ) THEN te.id ELSE NULL END ) AS rest ");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_link_error te ");
			sql.append(" WHERE te.pstate IN ( 0, 1, 2, 3, 4 ) ");
			if (taskIDs != null && taskIDs.size() > 0) {
				sql.append(" AND te.taskid IN ( ");
				for (Long taskID : taskIDs) {
					sql.append(taskID);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			sql.append(" GROUP BY te.taskid ");
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
	
	public List<Map<String, Object>> groupErrors(ConfigDBModel configDBModel, List<TaskTypeEnum> taskTypes) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	t.projectid,");
			sql.append("	COUNT( 1 ) AS total,");
			sql.append("	COUNT( CASE WHEN te.pstate IN ( 0, 1, 3, 4 ) THEN te.id ELSE NULL END ) AS rest ");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task t ");
			sql.append(" JOIN ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_link_error te ON te.taskid = t.id ");
			sql.append(" WHERE te.pstate IN ( 0, 1, 2, 3, 4 ) ");
			if (taskTypes != null && taskTypes.size() > 0) {
				sql.append(" AND t.tasktype IN ( ");
				for (TaskTypeEnum taskType : taskTypes) {
					sql.append(taskType.getValue());
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			sql.append(" GROUP BY t.projectid");

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
	
	public List<Map<String, Object>> countFielddatasByTaskid(ConfigDBModel configDBModel, List<Long> taskIDs) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	tf.taskid,");
			sql.append("	COUNT( DISTINCT tf.shapeid ) AS total ,");
			sql.append(" 	COUNT( DISTINCT CASE WHEN tf.pstate IN (0, 1, 3, 4) THEN tf.shapeid ELSE NULL END ) AS rest");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_link_fielddata tf ");
			sql.append(" WHERE tf.pstate IN ( 0, 1, 2, 3, 4 ) ");
			if (taskIDs != null && taskIDs.size() > 0) {
				sql.append(" AND tf.taskid IN ( ");
				for (Long taskID : taskIDs) {
					sql.append(taskID);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			sql.append(" GROUP BY tf.taskid ");
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
	
	public List<Map<String, Object>> groupFielddatas(ConfigDBModel configDBModel, List<TaskTypeEnum> taskTypes) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	t.projectid,");
			sql.append("	COUNT( DISTINCT tf.shapeid ) AS total ,");
			sql.append(" 	COUNT( DISTINCT CASE WHEN tf.pstate IN (0, 1, 3, 4) THEN tf.shapeid ELSE NULL END ) AS rest");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task t ");
			sql.append(" JOIN ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_task_link_fielddata tf ON tf.taskid = t.id ");
			sql.append(" WHERE tf.pstate IN ( 0, 1, 2, 3, 4 )");
			if (taskTypes != null && taskTypes.size() > 0) {
				sql.append(" AND t.tasktype IN ( ");
				for (TaskTypeEnum taskType : taskTypes) {
					sql.append(taskType.getValue());
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			sql.append(" GROUP BY t.projectid");

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
	
	public List<Map<String, Object>> groupErrorsInCache(ConfigDBModel configDBModel) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	wte.pid AS projectid,");
			sql.append("	COUNT(1) AS total,");
			sql.append("	COUNT(1) AS rest");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_batch_willcreatetask_error wte ");
			sql.append(" GROUP BY wte.pid");

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
	
	public List<Map<String, Object>> groupFielddatasInCache(ConfigDBModel configDBModel) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	wt.projectid AS projectid,");
			sql.append("	COUNT(1) AS total,");
			sql.append("	COUNT(1) AS rest");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_batch_willcreatetask wt ");
			sql.append(" GROUP BY wt.projectid");

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
	
	public List<Map<String, Object>> groupPOIs(ConfigDBModel configDBModel) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT");
			sql.append("	projectid,");
			sql.append("	COUNT(1)");
			sql.append(" FROM ");
			sql.append(configDBModel.getDbschema()).append(".");
			sql.append(" tb_poi ");
			sql.append(" WHERE projectid > 0");
			sql.append(" AND NOT ( confirm = 'no_confirm' AND optype = 'init' ) ");
			sql.append(" AND NOT ( confirm = 'no_confirm' AND optype = 'published' )");
			sql.append(" GROUP BY projectid");

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
	
	public List<Map<String, Object>> groupTasksByTime(ConfigDBModel configDBModel, String time) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
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
			if (configDBModel == null)
				return task;
			
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
			if (configDBModel == null)
				return task;
			
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
	
	public Integer updateTaskByIDSelective(ConfigDBModel configDBModel, TaskModel record) {
		Integer ret = -1;
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return ret;
			if (record == null || record.getId() == null || record.getId().compareTo(0L) <= 0)
				return ret;
			
			Integer dbtype = configDBModel.getDbtype();
			String separator = Common.getDatabaseSeparator(dbtype);
			
			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" SET id = id");
			if (record.getPriority() != null) {
				sql.append(", " + separator + "priority" + separator + " = '" + record.getPriority() + "'");
			}

			sql.append(" WHERE " + separator + "id" + separator + " = " + record.getId());

			dataSource = Common.getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return ret;
	}
	
	// add by lianhr begin 2018/12/18
	public List<Map<String, Object>> groupTasksByTime(ConfigDBModel configDBModel, String[] times, String time) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return list;
			
			if (times == null || times.length == 0)
				return list;

			String startTime = String.format("%s " + times[0], time);
			String endTime = String.format("%s " + times[1], time);
			boolean timeFlag = false;
			if (times[0].equals("08:30:00") && times[1].equals("17:30:00")) {
				timeFlag = true;
			}

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
			if (timeFlag) {
				sql.append("	AND operatetime BETWEEN '" + startTime + "' AND '" + endTime + "' ");
			} else {
				sql.append("	AND ((operatetime BETWEEN '" + String.format("%s " + "00:00:00", time) + "' AND '"
						+ String.format("%s " + "08:29:59", time) + "') or (operatetime BETWEEN '"
						+ String.format("%s " + "17:30:00", time) + "' AND '" + String.format("%s " + "23:59:59", time)
						+ "')) ");
			}
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
	// add by lianhr end

}
