package com.emg.projectsmanage.dao.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.emg.projectsmanage.common.Common;
import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ItemConfigModel;

public class ErrorSetModelDao {
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorSetModelDao.class);
	
	public List<ErrorSetModel> selectErrorSets(ConfigDBModel configDBModel, ErrorSetModel record, Integer limit, Integer offset) {
		List<ErrorSetModel> errorSets = new ArrayList<ErrorSetModel>();
		BasicDataSource dataSource = null;
		try {
			if (record == null)
				return errorSets;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND " + separator + "type" + separator + " = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(" AND " + separator + "systype" + separator + " = " + record.getSystype());
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(" AND " + separator + "unit" + separator + " = " + record.getUnit());
			}
			if (record.getDesc() != null && !record.getDesc().isEmpty()) {
				sql.append(" AND " + separator + "desc" + separator + " like '%" + record.getDesc() + "%'");
			}
			sql.append(" ORDER BY id ");
			if (limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if (offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			dataSource = Common.getDataSource(configDBModel);
			errorSets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ErrorSetModel>(ErrorSetModel.class));

		} catch (Exception e) {
			logger.error(e.getMessage());
			errorSets = new ArrayList<ErrorSetModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return errorSets;
	}

	public Long insertErrorSet(ConfigDBModel configDBModel, final ErrorSetModel record) {
		Long ret = -1L;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			final StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");
			sql.append(" (" + separator + "name" + separator + ", " + separator + "type" + separator + ", " + separator + "systype" + separator + ", " + separator + "unit"
					+ separator + ", " + separator + "desc" + separator + ") ");
			sql.append(" VALUES (?,?,?,?,?) ");

			KeyHolder keyHolder = new GeneratedKeyHolder();
			dataSource = Common.getDataSource(configDBModel);
			new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, record.getName());
					ps.setInt(2, record.getType() == null ? 0 : record.getType());
					ps.setInt(3, record.getSystype() == null ? 0 : record.getSystype());
					ps.setInt(4, record.getUnit() == null ? 0 : record.getUnit());
					ps.setString(5, record.getDesc() == null ? new String() : record.getDesc());
					return ps;
				}
			}, keyHolder);
			if (keyHolder.getKeys().size() > 1) {
				ret = (Long) keyHolder.getKeys().get("id");
			} else {
				ret = keyHolder.getKey().longValue();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			ret = -1L;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return ret;
	}

	public Boolean updateErrorSet(ConfigDBModel configDBModel, ErrorSetModel record) {
		Boolean ret = false;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");
			sql.append(" SET id = id");
			if (record.getName() != null) {
				sql.append(", " + separator + "name" + separator + " = '" + record.getName() + "'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(", " + separator + "type" + separator + " = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(", " + separator + "systype" + separator + " = " + record.getSystype());
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(", " + separator + "unit" + separator + " = " + record.getUnit());
			}
			if (record.getDesc() != null) {
				sql.append(", " + separator + "desc" + separator + " = '" + record.getDesc() + "'");
			}

			sql.append(" WHERE " + separator + "id" + separator + " = " + record.getId());

			dataSource = Common.getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			ret = false;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return ret;
	}

	public Boolean deleteErrorSet(ConfigDBModel configDBModel, Long errorSetID) {
		Boolean ret = false;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" DELETE FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");
			sql.append(" WHERE " + separator + "id" + separator + " = " + errorSetID);

			dataSource = Common.getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;

			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql_del.append(configDBModel.getDbschema()).append(".");
			}
			sql_del.append("tb_errorsetdetail ");
			sql_del.append(" WHERE " + separator + "itemsetid" + separator + " = " + errorSetID);

			ret = ret && new JdbcTemplate(dataSource).update(sql_del.toString()) >= 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			ret = false;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return ret;
	}

	public Integer countErrorSets(ConfigDBModel configDBModel, ErrorSetModel record, Integer limit, Integer offset) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT count(*) FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND " + separator + "type" + separator + " = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(" AND " + separator + "systype" + separator + " = " + record.getSystype());
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(" AND " + separator + "unit" + separator + " = " + record.getUnit());
			}
			if (record.getDesc() != null && !record.getDesc().isEmpty()) {
				sql.append(" AND " + separator + "desc" + separator + " like '%" + record.getDesc() + "%'");
			}

			dataSource = Common.getDataSource(configDBModel);
			count = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			count = -1;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return count;
	}

	public List<ItemConfigModel> selectErrorTypes(ConfigDBModel configDBModel) {
		List<ItemConfigModel> itemConfigs = new ArrayList<ItemConfigModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemconfig ");
			sql.append(" WHERE 1=1 ");

			dataSource = Common.getDataSource(configDBModel);
			itemConfigs = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemConfigModel>(ItemConfigModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage());
			itemConfigs = new ArrayList<ItemConfigModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return itemConfigs;
	}

	public List<Long> getErrorSetDetailsByErrorSetID(ConfigDBModel configDBModel, Long errorSetID) {
		List<Long> items = new ArrayList<Long>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT " + separator + "itemid" + separator + " FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorsetdetail ");
			sql.append(" WHERE " + separator + "itemsetid" + separator + " = " + errorSetID);

			dataSource = Common.getDataSource(configDBModel);
			items = new JdbcTemplate(dataSource).queryForList(sql.toString(), Long.class);
		} catch (Exception e) {
			items = new ArrayList<Long>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return items;
	}

	public Integer setErrorSetDetails(ConfigDBModel configDBModel, Long errorSetID, List<Long> errorTypes) {
		Integer ret = -1;
		if (errorTypes.size() <= 0)
			return ret;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql_del.append(configDBModel.getDbschema()).append(".");
			}
			sql_del.append("tb_errorsetdetail ");
			sql_del.append(" WHERE " + separator + "itemsetid" + separator + " = " + errorSetID);

			dataSource = Common.getDataSource(configDBModel);
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			Integer ret_del = jdbc.update(sql_del.toString());
			if (ret_del >= 0) {
				StringBuffer sql = new StringBuffer();
				sql.append(" INSERT INTO ");
				if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
					sql.append(configDBModel.getDbschema()).append(".");
				}
				sql.append("tb_errorsetdetail");
				sql.append(" (" + separator + "itemsetid" + separator + ", " + separator + "itemid" + separator + ") ");
				sql.append(" VALUES ");
				for (Long errorType : errorTypes) {
					sql.append("(");
					sql.append(errorSetID + ", ");
					sql.append(errorType);
					sql.append(" ),");
				}
				sql.deleteCharAt(sql.length() - 1);
				ret = jdbc.update(sql.toString());
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			ret = -1;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return ret;
	}
}
