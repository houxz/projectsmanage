package com.emg.projectsmanage.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.Common;
import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.common.StateMap;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.EmployeeModel;
import com.emg.projectsmanage.pojo.TaskModel;
import com.emg.projectsmanage.service.EmapgoAccountService;

@Component
public class TaskModelDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskModelDao.class);

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	public List<TaskModel> selectTaskModels(ConfigDBModel configDBModel, TaskModel record, List<Long> projectids,
			List<StateMap> stateMaps, Integer limit, Integer offset) {
		List<TaskModel> tasks = new ArrayList<TaskModel>();
		BasicDataSource dataSource = null;
		try {
			if (record == null || configDBModel == null)
				return tasks;
			Integer dbtype = configDBModel.getDbtype();
			Integer processType = record.getProcesstype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *, " + processType
					+ " AS processType, to_char(operatetime, 'YYYY-MM-DD HH24:MI:SS') AS opttime FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (projectids != null && projectids.size() > 0) {
				sql.append(" AND " + separator + "projectid" + separator + " in( ");
				for (Long projectid : projectids) {
					sql.append(projectid);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
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
							sql.append(" ( ISNULL(checkid) OR checkid <= 0) AND");
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
			if (record.getEditid() != null && record.getEditid().compareTo(0) > 0) {
				sql.append(" AND " + separator + "editid" + separator + " = " + (record.getEditid() + 500000));
			} else if (record.getEditid() != null && record.getEditid().compareTo(0) == 0) {
				sql.append(" AND " + separator + "editid" + separator + " = 0");
			}
			if (record.getCheckid() != null && record.getCheckid().compareTo(0) > 0) {
				sql.append(" AND " + separator + "checkid" + separator + " = " + (record.getCheckid() + 600000));
			} else if (record.getCheckid() != null && record.getCheckid().compareTo(0) == 0) {
				sql.append(" AND " + separator + "checkid" + separator + " = 0");
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
			List<StateMap> stateMaps) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			if (record == null || configDBModel == null)
				return count;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (projectids != null && projectids.size() > 0) {
				sql.append(" AND " + separator + "projectid" + separator + " in( ");
				for (Long projectid : projectids) {
					sql.append(projectid);
					sql.append(",");
				}
				sql = sql.deleteCharAt(sql.length() - 1);
				sql.append(" )");
			}
			if (record.getEditid() != null && record.getEditid().compareTo(0) > 0) {
				sql.append(" AND " + separator + "editid" + separator + " = " + (record.getEditid() + 500000));
			} else if (record.getEditid() != null && record.getEditid().compareTo(0) == 0) {
				sql.append(" AND " + separator + "editid" + separator + " = 0");
			}
			if (record.getCheckid() != null && record.getCheckid().compareTo(0) > 0) {
				sql.append(" AND " + separator + "checkid" + separator + " = " + (record.getCheckid() + 600000));
			} else if (record.getCheckid() != null && record.getCheckid().compareTo(0) == 0) {
				sql.append(" AND " + separator + "checkid" + separator + " = 0");
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

	public List<EmployeeModel> groupEditers(ConfigDBModel configDBModel, Integer processType) {
		List<EmployeeModel> users = new ArrayList<EmployeeModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT " + separator + "editid" + separator + " FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" GROUP BY " + separator + "editid" + separator);

			dataSource = Common.getDataSource(configDBModel);
			List<Integer> userids = new JdbcTemplate(dataSource).queryForList(sql.toString(), Integer.class);
			List<Integer> _userids = new ArrayList<Integer>();
			for (Integer userid : userids) {
				if (userid == null)
					continue;
				_userids.add(userid.compareTo(500000) >= 0 ? (userid - 500000) : userid);
			}
			if (_userids.size() > 0)
				users = emapgoAccountService.getEmployeeByIDS(_userids);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			users = new ArrayList<EmployeeModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return users;
	}

	public List<EmployeeModel> groupCheckers(ConfigDBModel configDBModel, Integer processType) {
		List<EmployeeModel> users = new ArrayList<EmployeeModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT " + separator + "checkid" + separator + " FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_task ");
			sql.append(" GROUP BY " + separator + "checkid" + separator);

			dataSource = Common.getDataSource(configDBModel);
			List<Integer> userids = new JdbcTemplate(dataSource).queryForList(sql.toString(), Integer.class);
			List<Integer> _userids = new ArrayList<Integer>();
			for (Integer userid : userids) {
				if (userid == null)
					continue;
				_userids.add(userid.compareTo(600000) >= 0 ? (userid - 600000) : userid);
			}
			if (_userids.size() > 0)
				users = emapgoAccountService.getEmployeeByIDS(_userids);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			users = new ArrayList<EmployeeModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return users;
	}
}
