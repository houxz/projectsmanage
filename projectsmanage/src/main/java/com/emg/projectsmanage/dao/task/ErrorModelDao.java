package com.emg.projectsmanage.dao.task;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emg.projectsmanage.common.Common;
import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorAndErrorRelatedModel;
import com.emg.projectsmanage.pojo.ErrorModel;
import com.emg.projectsmanage.pojo.ErrorRelatedModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ItemConfigModel;

@Component
public class ErrorModelDao {
	
	private static final Logger logger = LoggerFactory.getLogger(ErrorModelDao.class);
	
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
			sql.append("tb_task_batch ");

			dataSource = Common.getDataSource(configDBModel);
			batchids = new JdbcTemplate(dataSource).queryForList(sql.toString(), String.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			batchids = new ArrayList<String>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
			errorSets = new ArrayList<ErrorSetModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
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
					logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
			itemConfigs = new ArrayList<ItemConfigModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
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
			logger.error(e.getMessage(), e);
			errors = new ArrayList<ErrorModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return errors;
	}
	
	public Integer countErrorAndErrorRelateds(ConfigDBModel configDBModel, ErrorModel record, List<Long> errortypes) {
		Integer ret = -1;
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(*)");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error te ");
			sql.append(" LEFT JOIN ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error_related ter ");
			sql.append(" ON ter." + separator + "errorid" + separator + " = te." + separator + "id" + separator + " ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND te." + separator + "batchid" + separator + " =  " + record.getBatchid());
			if (errortypes != null && !errortypes.isEmpty()) {
				sql.append(" AND te." + separator + "errortype" + separator + " IN ( ");
				for (Long errortype : errortypes) {
					sql.append(errortype + ",");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" ) ");
			}

			dataSource = Common.getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).queryForObject(sql.toString(), Integer.class);
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

	public List<ErrorAndErrorRelatedModel> selectErrorAndErrorRelateds(ConfigDBModel configDBModel, ErrorModel record, List<Long> errortypes, Integer limit, Integer offset) {
		List<ErrorAndErrorRelatedModel> errors = new ArrayList<ErrorAndErrorRelatedModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT te.*, ter." + separator + "id" + separator + " AS rID, ter." + separator + "type" + separator + " AS rType, ter." + separator + "featureid" + separator + " AS rFeatureid ,ter." + separator + "layerid" + separator + " AS rLayerid ,ter." + separator + "editver" + separator + " AS rEditver");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error te ");
			sql.append(" LEFT JOIN ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error_related ter ");
			sql.append(" ON ter." + separator + "errorid" + separator + " = te." + separator + "id" + separator + " ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND te." + separator + "batchid" + separator + " =  " + record.getBatchid());
			if (errortypes != null && !errortypes.isEmpty()) {
				sql.append(" AND te." + separator + "errortype" + separator + " IN ( ");
				for (Long errortype : errortypes) {
					sql.append(errortype + ",");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" ) ");
			}
			sql.append(" ORDER BY te." + separator + "id" + separator + " ");
			sql.append(" LIMIT " + limit);
			sql.append(" OFFSET " + offset);

			dataSource = Common.getDataSource(configDBModel);
			errors = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ErrorAndErrorRelatedModel>(ErrorAndErrorRelatedModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			errors = new ArrayList<ErrorAndErrorRelatedModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return errors;
	}

	public Integer exportErrors(ConfigDBModel configDBModel, List<ErrorAndErrorRelatedModel> errorAndRelateds) {
		Integer ret = 0;
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
				if (field.getName().compareToIgnoreCase("id") == 0)
					continue;
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
				// 根据主键列名取得自动生成主键值
				String[] columnNames = { "id" };
				PreparedStatement pst = connection.prepareStatement(sql, columnNames);

				/**
				 * 每次插入条数
				 */
				Integer batch = 2000;
				LinkedHashSet<Long> errorids = new LinkedHashSet<Long>();
				ConcurrentHashMap<Long, Long> mact = new ConcurrentHashMap<Long, Long>();
				for (Integer i = 0; i <= errorAndRelateds.size() / batch; i++) {
					LinkedHashSet<Long> curBatchErrors = new LinkedHashSet<Long>();
					for (int j = 0; j < batch; j++) {
						Integer index = i * batch + j;
						if (index.compareTo(errorAndRelateds.size()) < 0) {
							ErrorAndErrorRelatedModel errorAndRelated = errorAndRelateds.get(index);
							Long errorID = errorAndRelated.getId();
							if (!errorids.contains(errorID)) {
								Field[] fields = ErrorModel.class.getDeclaredFields();
								for (Integer k = 1; k < fields.length; k++) {
									Field field = fields[k];
									if (field.getName().compareToIgnoreCase("id") == 0)
										continue;
									field.setAccessible(true);
									String type = field.getType().getName();
									switch (type) {
									case "java.lang.Long":
										if (field.get(errorAndRelated) == null) {
											pst.setNull(k, Types.BIGINT);
										} else {
											pst.setLong(k, Long.valueOf(field.get(errorAndRelated).toString()));
										}
										break;
									case "java.lang.Integer":
										if (field.get(errorAndRelated) == null) {
											pst.setNull(k, Types.INTEGER);
										} else {
											pst.setInt(k, Integer.valueOf(field.get(errorAndRelated).toString()));
										}
										break;
									case "java.lang.String":
										if (field.get(errorAndRelated) == null) {
											pst.setNull(k, Types.CHAR);
										} else {
											pst.setString(k, field.get(errorAndRelated).toString());
										}
										break;
									case "java.lang.Object":
										if (field.get(errorAndRelated) == null) {
											pst.setNull(k, Types.OTHER);
										} else {
											pst.setObject(k, field.get(errorAndRelated));
										}
										break;
									case "java.util.Date":
										if (field.get(errorAndRelated) == null) {
											pst.setNull(k, Types.TIMESTAMP);
										} else {
											Timestamp t = Timestamp.valueOf(field.get(errorAndRelated).toString());
											pst.setTimestamp(k, t);
										}
										break;
									default:
										if (field.get(errorAndRelated) == null) {
											pst.setNull(k, Types.CHAR);
										} else {
											pst.setString(k, field.get(errorAndRelated).toString());
										}
										break;
									}
								}
								pst.addBatch();
								errorids.add(errorID);
								curBatchErrors.add(errorID);
							}
						}
					}
					int[] _rets = pst.executeBatch();
					ResultSet rs = pst.getGeneratedKeys();
					Integer k = 0;
					Iterator<Long> it = curBatchErrors.iterator();
					while (rs.next()) {
						String before = it.next().toString();
						Long after = rs.getLong(1);
						mact.put(Long.valueOf(before), after);
						k++;
					}
					for (int _ret : _rets) {
						ret += _ret;
					}
					connection.commit();
				}

				List<ErrorRelatedModel> errorRelateds = new ArrayList<ErrorRelatedModel>();
				for (ErrorAndErrorRelatedModel errorAndRelated : errorAndRelateds) {
					if (errorAndRelated.getRid() == null)
						continue;
					ErrorRelatedModel errorRelated = new ErrorRelatedModel();
					errorRelated.setErrorid(mact.get(errorAndRelated.getId()));
					errorRelated.setType(errorAndRelated.getRtype());
					errorRelated.setFeatureid(errorAndRelated.getRfeatureid());
					errorRelated.setLayerid(errorAndRelated.getRlayerid());
					errorRelated.setEditver(errorAndRelated.getReditver());
					errorRelateds.add(errorRelated);
				}

				exportErrorRelateds(configDBModel, errorRelateds);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				connection = null;
			}
		}
		return ret;
	}

	public List<ErrorRelatedModel> selectErrorRelatedByIDs(ConfigDBModel configDBModel, HashSet<Long> errorids) {
		List<ErrorRelatedModel> errorRelateds = new ArrayList<ErrorRelatedModel>();
		BasicDataSource dataSource = null;
		try {
			if (errorids == null || errorids.isEmpty())
				return errorRelateds;

			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error_related ");
			sql.append(" WHERE " + separator + "errorid" + separator + " IN ( ");
			for (Long errorid : errorids) {
				sql.append(errorid + ",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" ) ");

			dataSource = Common.getDataSource(configDBModel);
			errorRelateds = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ErrorRelatedModel>(ErrorRelatedModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			errorRelateds = new ArrayList<ErrorRelatedModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return errorRelateds;
	}

	public Integer exportErrorRelateds(ConfigDBModel configDBModel, List<ErrorRelatedModel> errorRelateds) {
		Integer ret = 0;
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
			prefix.append("tb_error_related ( ");
			for (Field field : ErrorRelatedModel.class.getDeclaredFields()) {
				if (field.getName().compareToIgnoreCase("id") == 0)
					continue;
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

				Integer batch = 2000;
				for (Integer i = 0; i <= errorRelateds.size() / batch; i++) {
					for (int j = 0; j < batch; j++) {
						Integer index = i * batch + j;
						if (index.compareTo(errorRelateds.size()) < 0) {
							ErrorRelatedModel errorRelated = errorRelateds.get(index);
							Field[] fields = errorRelated.getClass().getDeclaredFields();
							for (Integer k = 1; k < fields.length; k++) {
								Field field = fields[k];
								if (field.getName().compareToIgnoreCase("id") == 0)
									continue;
								field.setAccessible(true);
								String type = field.getType().getName();
								switch (type) {
								case "java.lang.Long":
									if (field.get(errorRelated) == null) {
										pst.setNull(k, Types.BIGINT);
									} else {
										pst.setLong(k, Long.valueOf(field.get(errorRelated).toString()));
									}
									break;
								case "java.lang.Integer":
									if (field.get(errorRelated) == null) {
										pst.setNull(k, Types.INTEGER);
									} else {
										pst.setInt(k, Integer.valueOf(field.get(errorRelated).toString()));
									}
									break;
								case "java.lang.String":
									if (field.get(errorRelated) == null) {
										pst.setNull(k, Types.CHAR);
									} else {
										pst.setString(k, field.get(errorRelated).toString());
									}
									break;
								case "java.lang.Object":
									if (field.get(errorRelated) == null) {
										pst.setNull(k, Types.OTHER);
									} else {
										pst.setObject(k, field.get(errorRelated));
									}
									break;
								case "java.util.Date":
									if (field.get(errorRelated) == null) {
										pst.setNull(k, Types.TIMESTAMP);
									} else {
										Timestamp t = Timestamp.valueOf(field.get(errorRelated).toString());
										pst.setTimestamp(k, t);
									}
									break;
								default:
									if (field.get(errorRelated) == null) {
										pst.setNull(k, Types.CHAR);
									} else {
										pst.setString(k, field.get(errorRelated).toString());
									}
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
			logger.error(e.getMessage(), e);
			ret = -1;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
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
}
