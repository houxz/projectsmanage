package com.emg.projectsmanage.dao.task;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.emg.projectsmanage.common.Common;
import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ItemConfigModel;

public class ErrorModelDao {
	public List<String> getErrorBatchids(ConfigDBModel configDBModel) {
		List<String> batchids = new ArrayList<String>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DISTINCT " + separator + "batchid" + separator + " ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error ");

			dataSource = Common.getDataSource(configDBModel);
			batchids = new JdbcTemplate(dataSource).queryForList(sql.toString(), String.class);
		} catch (Exception e) {
			e.printStackTrace();
			batchids = new ArrayList<String>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return batchids;
	}

	public List<ErrorSetModel> getErrorSets(ConfigDBModel configDBModel) {
		List<ErrorSetModel> errorSets = new ArrayList<ErrorSetModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");

			dataSource = Common.getDataSource(configDBModel);
			errorSets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ErrorSetModel>(ErrorSetModel.class));

		} catch (Exception e) {
			e.printStackTrace();
			errorSets = new ArrayList<ErrorSetModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return errorSets;
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
					e.printStackTrace();
				}
			}
		}
		return items;
	}

	public List<ItemConfigModel> selectErrorTypesByIDs(ConfigDBModel configDBModel, List<Long> itemIDs) {
		List<ItemConfigModel> itemConfigs = new ArrayList<ItemConfigModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemconfig ");
			sql.append(" WHERE " + separator + "id" + separator + " IN ( ");
			for (Long itemID : itemIDs) {
				sql.append(itemID + ",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");

			dataSource = Common.getDataSource(configDBModel);
			itemConfigs = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemConfigModel>(ItemConfigModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemConfigs = new ArrayList<ItemConfigModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return itemConfigs;
	}

	public List<ErrorModel> selectErrors(ConfigDBModel configDBModel, ErrorModel record, Integer limit, Integer offset, List<Long> errortypes) {
		List<ErrorModel> errors = new ArrayList<ErrorModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND " + separator + "batchid" + separator + " =  " + record.getBatchid());
			if (errortypes != null && !errortypes.isEmpty()) {
				sql.append(" AND " + separator + "errortype" + separator + " IN ( ");
				for (Long errortype : errortypes) {
					sql.append(errortype + ",");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" ) ");
			}
			sql.append(" ORDER BY " + separator + "id" + separator + " ");
			if (limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if (offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			dataSource = Common.getDataSource(configDBModel);
			errors = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ErrorModel>(ErrorModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			errors = new ArrayList<ErrorModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return errors;
	}

	public Integer exportErrors(ConfigDBModel configDBModel, List<ErrorModel> errors) {
		Integer ret = -1;
		Connection connection = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer prefix = new StringBuffer();
			StringBuffer suffix = new StringBuffer();
			String sql = new String();
			prefix.append(" INSERT INTO ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				prefix.append(configDBModel.getDbschema()).append(".");
			}
			prefix.append("tb_error ( ");
			for (Field field : ErrorModel.class.getDeclaredFields()) {
				prefix.append(field.getName() + ",");
				suffix.append("?,");
			}
			prefix.deleteCharAt(prefix.length() - 1);
			suffix.deleteCharAt(suffix.length() - 1);
			prefix.append(") VALUES ");

			sql = prefix.toString() + "(" + suffix.toString() + ")";

			String url = Common.getUrl(configDBModel);
			String user = configDBModel.getUser();
			String password = configDBModel.getPassword();
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, user, password);
			if (connection != null && !connection.isClosed()) {
				connection.setAutoCommit(false);
				PreparedStatement pst = connection.prepareStatement(sql);

				Integer batch = 10;
				for (Integer i = 0; i <= errors.size() / batch; i++) {
					for (int j = 0; j < batch; j++) {
						Integer index = i * batch + j;
						if (index.compareTo(errors.size()) < 0) {
							ErrorModel error = errors.get(index);
							Field[] fields = error.getClass().getDeclaredFields();
							for (Integer k = 0; k < fields.length; k++) {
								Field field = fields[k];
								field.setAccessible(true);
								String type = field.getType().getName();
								switch (type) {
								case "java.lang.Long":
									pst.setLong(k + 1, Long.valueOf(field.get(error).toString()));
									break;
								case "java.lang.Integer":
									pst.setInt(k + 1, Integer.valueOf(field.get(error).toString()));
									break;
								case "java.lang.String":
									pst.setString(k + 1, field.get(error).toString());
									break;
								case "java.lang.Object":
									pst.setObject(k + 1, field.get(error));
									break;
								case "java.util.Date":
									Timestamp t = Timestamp.valueOf(field.get(error).toString());
									pst.setTimestamp(k + 1, t);
									break;
								default:
									pst.setString(k + 1, field.get(error).toString());
									break;
								}
							}
							pst.addBatch();
						}
					}
					int[] _rets = pst.executeBatch();
					for (int _ret : _rets) {
						ret += _ret;
					}
					connection.commit();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connection = null;
			}
		}
		return ret;
	}

	public Integer countErrors(ConfigDBModel configDBModel, ErrorModel record, List<Long> errortypes) {
		Integer ret = -1;
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
			sql.append("tb_error ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND " + separator + "batchid" + separator + " =  " + record.getBatchid());
			if (errortypes != null && !errortypes.isEmpty()) {
				sql.append(" AND " + separator + "errortype" + separator + " IN ( ");
				for (Long errortype : errortypes) {
					sql.append(errortype + ",");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" ) ");
			}

			dataSource = Common.getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
}
