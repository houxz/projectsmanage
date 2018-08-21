package com.emg.projectsmanage.dao.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import com.emg.projectsmanage.pojo.ItemAreaModel;
import com.emg.projectsmanage.pojo.ItemInfoModel;
import com.emg.projectsmanage.pojo.ItemSetModel;

public class ItemSetModelDao {
	
	private static final Logger logger = LoggerFactory.getLogger(ItemSetModelDao.class);
	
	public List<ItemSetModel> selectItemSets(ConfigDBModel configDBModel, ItemSetModel record, Integer limit, Integer offset) {
		List<ItemSetModel> itemSets = new ArrayList<ItemSetModel>();
		BasicDataSource dataSource = null;
		try {
			if (record == null)
				return itemSets;
			Integer dbtype = configDBModel.getDbtype();
			Integer processType = record.getProcessType();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *, " + processType + " AS processType FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemset ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (record.getLayername() != null && !record.getLayername().isEmpty()) {
				sql.append(" AND " + separator + "layername" + separator + " like '%" + record.getLayername() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND " + separator + "type" + separator + " = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(" AND " + separator + "systype" + separator + " = " + record.getSystype());
			}
			if (record.getReferdata() != null && !record.getReferdata().isEmpty()) {
				sql.append(" AND " + separator + "referdata" + separator + " like '%" + record.getReferdata() + "%'");
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(" AND " + separator + "unit" + separator + " = " + record.getUnit());
			}
			if (record.getDesc() != null && !record.getDesc().isEmpty()) {
				sql.append(" AND " + separator + "desc" + separator + " like '%" + record.getDesc() + "%'");
			}
			sql.append(" ORDER BY " + separator + "id" + separator + " ");
			if (limit != null && limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if (offset != null && offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			dataSource = Common.getDataSource(configDBModel);
			itemSets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemSetModel>(ItemSetModel.class));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			itemSets = new ArrayList<ItemSetModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				dataSource = null;
			}
		}
		return itemSets;
	}
	
	public Long insertItemset(ConfigDBModel configDBModel, final ItemSetModel record) {
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
			sql.append("tb_itemset (" + separator + "name" + separator + ", " + separator + "layername" + separator + ", " + separator + "type" + separator + ", " + separator
					+ "systype" + separator + ", " + separator + "referdata" + separator + ", " + separator + "unit" + separator + ", " + separator + "desc" + separator + ") ");
			sql.append(" VALUES (?,?,?,?,?,?,?) ");

			KeyHolder keyHolder = new GeneratedKeyHolder();
			dataSource = Common.getDataSource(configDBModel);
			new JdbcTemplate(dataSource).update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, record.getName() == null ? new String() : record.getName());
					ps.setString(2, record.getLayername() == null ? new String() : record.getLayername());
					ps.setInt(3, record.getType() == null ? 0 : record.getType());
					ps.setInt(4, record.getSystype() == null ? 0 : record.getSystype());
					ps.setString(5, record.getReferdata() == null ? new String() : record.getReferdata());
					ps.setInt(6, record.getUnit() == null ? 0 : record.getUnit());
					ps.setString(7, record.getDesc() == null ? new String() : record.getDesc());
					return ps;
				}
			}, keyHolder);
			if (keyHolder.getKeys().size() > 1) {
				ret = (Long) keyHolder.getKeys().get("id");
			} else {
				ret = keyHolder.getKey().longValue();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = -1L;
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

	public Boolean updateItemset(ConfigDBModel configDBModel, ItemSetModel record) {
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
			sql.append("tb_itemset ");
			sql.append(" SET " + separator + "id" + separator + " = id");
			if (record.getName() != null) {
				sql.append(", " + separator + "name" + separator + " = '" + record.getName() + "'");
			}
			if (record.getLayername() != null) {
				sql.append(", " + separator + "layername" + separator + " = '" + record.getLayername() + "'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(", " + separator + "type" + separator + " = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(", " + separator + "systype" + separator + " = " + record.getSystype());
			}
			if (record.getReferdata() != null) {
				sql.append(", " + separator + "referdata" + separator + " = '" + record.getReferdata() + "'");
			}
			if (record.getUnit() != null && record.getUnit() >= 0) {
				sql.append(", " + separator + "unit" + separator + " = " + record.getUnit());
			}
			if (record.getDesc() != null) {
				sql.append(", " + separator + "desc" + separator + " = '" + record.getDesc() + "'");
			}

			sql.append(" WHERE id = " + record.getId());

			dataSource = Common.getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = false;
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

	public Boolean deleteItemSet(ConfigDBModel configDBModel, Long itemSetID) {
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
			sql.append("tb_itemset ");
			sql.append(" WHERE " + separator + "id" + separator + " = " + itemSetID);

			dataSource = Common.getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).update(sql.toString()) >= 0;

			StringBuffer sql_del = new StringBuffer();
			sql_del.append(" DELETE FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql_del.append(configDBModel.getDbschema()).append(".");
			}
			sql_del.append("tb_itemsetdetail ");
			sql_del.append(" WHERE " + separator + "itemsetid" + separator + " = " + itemSetID);

			ret = ret && new JdbcTemplate(dataSource).update(sql_del.toString()) >= 0;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			ret = false;
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

	public Integer countItemSets(ConfigDBModel configDBModel, ItemSetModel record, Integer limit, Integer offset) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			if(configDBModel == null)	return count;
			
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT count(*) FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemset ");
			sql.append(" WHERE 1=1 ");
			if (record.getId() != null && record.getId().compareTo(0L) > 0) {
				sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
			}
			if (record.getName() != null && !record.getName().isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
			}
			if (record.getLayername() != null && !record.getLayername().isEmpty()) {
				sql.append(" AND " + separator + "layername" + separator + " like '%" + record.getLayername() + "%'");
			}
			if (record.getType() != null && record.getType().compareTo(0) >= 0) {
				sql.append(" AND " + separator + "type" + separator + " = " + record.getType());
			}
			if (record.getSystype() != null && record.getSystype().compareTo(0) >= 0) {
				sql.append(" AND " + separator + "systype" + separator + " = " + record.getSystype());
			}
			if (record.getReferdata() != null && !record.getReferdata().isEmpty()) {
				sql.append(" AND " + separator + "referdata" + separator + " like '%" + record.getReferdata() + "%'");
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

	public List<ItemInfoModel> selectItemInfosByItemids(ConfigDBModel configDBModel, List<Long> itemids) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DISTINCT ON (" + separator + "oid" + separator + ") * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE " + separator + "enable" + separator + " = 1 AND " + separator + "id" + separator + " in ( ");
			for (Long itemid : itemids) {
				sql.append("'" + itemid + "',");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(") ");

			dataSource = Common.getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			itemInfos = new ArrayList<ItemInfoModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return itemInfos;
	}

	/**
	 * 获取质检项分类属于 POI+其他 的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	public List<ItemInfoModel> selectPOIAndOtherItemInfosByOids(ConfigDBModel configDBModel, Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE " + separator + "enable" + separator + " = 1 ");
			sql.append(" AND " + separator + "type" + separator + " = " + type);
			sql.append(" AND " + separator + "unit" + separator + " = " + unit);
			sql.append(" AND " + separator + "systype" + separator + " = " + systype);
			sql.append(" AND " + separator + "referdata" + separator + " LIKE '%POI%' ");
			sql.append(" AND " + separator + "referdata" + separator + " NOT LIKE '%Road%' ");
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND " + separator + "layername" + separator + " in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND " + separator + "oid" + separator + " in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			dataSource = Common.getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			itemInfos = new ArrayList<ItemInfoModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return itemInfos;
	}

	/**
	 * 获取质检项分类属于 Road+其他 的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	public List<ItemInfoModel> selectRoadAndOtherItemInfosByOids(ConfigDBModel configDBModel, Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE " + separator + "enable" + separator + " = 1 ");
			sql.append(" AND " + separator + "type" + separator + " = " + type);
			sql.append(" AND " + separator + "unit" + separator + " = " + unit);
			sql.append(" AND " + separator + "systype" + separator + " = " + systype);
			sql.append(" AND " + separator + "referdata" + separator + " LIKE '%Road%' ");
			sql.append(" AND " + separator + "referdata" + separator + " NOT LIKE '%POI%' ");
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND " + separator + "layername" + separator + " in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND " + separator + "oid" + separator + " in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			dataSource = Common.getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			itemInfos = new ArrayList<ItemInfoModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return itemInfos;
	}

	/**
	 * 获取质检项分类属于 POI+Road+其他 的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	public List<ItemInfoModel> selectPOIRoadAndOtherItemInfosByOids(ConfigDBModel configDBModel, Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE " + separator + "enable" + separator + " = 1 ");
			sql.append(" AND " + separator + "type" + separator + " = " + type);
			sql.append(" AND " + separator + "unit" + separator + " = " + unit);
			sql.append(" AND " + separator + "systype" + separator + " = " + systype);
			sql.append(" AND (" + separator + "referdata" + separator + " LIKE '%Road%POI%' ");
			sql.append(" OR " + separator + "referdata" + separator + " LIKE '%POI%Road%') ");
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND " + separator + "layername" + separator + " in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND " + separator + "oid" + separator + " in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			dataSource = Common.getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			itemInfos = new ArrayList<ItemInfoModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return itemInfos;
	}

	/**
	 * 获取质检项分类属于 其他 的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	public List<ItemInfoModel> selectOtherItemInfosByOids(ConfigDBModel configDBModel, Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE " + separator + "enable" + separator + " = 1 ");
			sql.append(" AND " + separator + "type" + separator + " = " + type);
			sql.append(" AND " + separator + "unit" + separator + " = " + unit);
			sql.append(" AND " + separator + "systype" + separator + " = " + systype);
			sql.append(" AND " + separator + "referdata" + separator + " NOT LIKE '%POI%' ");
			sql.append(" AND " + separator + "referdata" + separator + " NOT LIKE '%Road%' ");
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND " + separator + "layername" + separator + " in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND " + separator + "oid" + separator + " in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			dataSource = Common.getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			itemInfos = new ArrayList<ItemInfoModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return itemInfos;
	}

	/**
	 * 获取64位质检项的ItemInfo
	 * 
	 * @param oids
	 *            质检项
	 * @return
	 */
	public List<ItemInfoModel> selectX64ItemInfosByOids(ConfigDBModel configDBModel, Set<String> layernames, Set<String> oids, Integer type, Integer systype, Integer unit) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE " + separator + "enable" + separator + " = 1 ");
			sql.append(" AND " + separator + "type" + separator + " = " + type);
			sql.append(" AND " + separator + "unit" + separator + " = " + unit);
			sql.append(" AND " + separator + "systype" + separator + " = " + systype);
			if (layernames != null && layernames.size() > 0) {
				sql.append(" AND " + separator + "layername" + separator + " in ( ");
				for (String layername : layernames) {
					sql.append("'" + layername + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}
			if (oids != null && oids.size() > 0) {
				sql.append(" AND " + separator + "oid" + separator + " in ( ");
				for (String oid : oids) {
					sql.append("'" + oid + "',");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(") ");
			}

			dataSource = Common.getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			itemInfos = new ArrayList<ItemInfoModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return itemInfos;
	}

	public List<ItemInfoModel> selectQIDs(ConfigDBModel configDBModel, String oid, String name, Integer limit, Integer offset) {
		List<ItemInfoModel> itemInfos = new ArrayList<ItemInfoModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT " + separator + "oid" + separator + ", " + separator + "name" + separator + " FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_iteminfo ");
			sql.append(" WHERE " + separator + "enable" + separator + " = 1 ");
			if (oid != null && !oid.isEmpty()) {
				sql.append(" AND " + separator + "oid" + separator + " like '%" + oid + "%'");
			}
			if (name != null && !name.isEmpty()) {
				sql.append(" AND " + separator + "name" + separator + " like '%" + name + "%'");
			}
			sql.append("GROUP BY " + separator + "oid" + separator + ", " + separator + "name" + separator + "");

			dataSource = Common.getDataSource(configDBModel);
			itemInfos = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemInfoModel>(ItemInfoModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			itemInfos = new ArrayList<ItemInfoModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return itemInfos;
	}

	public List<Long> getItemSetDetailsByItemSetID(ConfigDBModel configDBModel, Long itemSetID) {
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
			sql.append("tb_itemsetdetail ");
			sql.append(" WHERE " + separator + "itemsetid" + separator + " = " + itemSetID);

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

	public Integer setItemSetDetails(ConfigDBModel configDBModel, Long itemSetID, List<Long> items) {
		Integer ret = -1;
		if (items.size() <= 0)
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
			sql_del.append("tb_itemsetdetail ");
			sql_del.append(" WHERE " + separator + "itemsetid" + separator + " = " + itemSetID);

			dataSource = Common.getDataSource(configDBModel);
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			Integer ret_del = jdbc.update(sql_del.toString());
			if (ret_del >= 0) {
				StringBuffer sql = new StringBuffer();
				sql.append(" INSERT INTO ");
				if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
					sql.append(configDBModel.getDbschema()).append(".");
				}
				sql.append("tb_itemsetdetail (" + separator + "itemsetid" + separator + ", " + separator + "itemid" + separator + ") ");
				sql.append(" VALUES ");
				for (Long item : items) {
					sql.append("(");
					sql.append(itemSetID + ", ");
					sql.append(item);
					sql.append(" ),");
				}
				sql.deleteCharAt(sql.length() - 1);
				ret = jdbc.update(sql.toString());
			}

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
	
	public List<ItemAreaModel> getItemAreas(ConfigDBModel configDBModel, Integer type, ItemAreaModel itemArea) {
		List<ItemAreaModel> list = new ArrayList<ItemAreaModel>();
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)	return list;
			
			Integer dbtype = configDBModel.getDbtype();
			
			String separator = Common.getDatabaseSeparator(dbtype);
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DISTINCT ON (" + separator + "province" + separator + "," + separator + "city" + separator + "," + separator + "type" + separator + ") * FROM ");
			if(dbtype.equals(DatabaseType.POSTGRESQL.getValue())){
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_city ");
			sql.append(" WHERE " + separator + "type" + separator + " in (0, 2) ");
			if (itemArea.getId() != null) {
				sql.append(" AND " + separator + "id" + separator + " = " + itemArea.getId());
			}
			if (itemArea.getType() != null) {
				sql.append(" AND " + separator + "type" + separator + " = " + itemArea.getType());
			}
			if (itemArea.getProvince() != null) {
				sql.append(" AND " + separator + "province" + separator + " like '%" + itemArea.getProvince() + "%'");
			}
			if (itemArea.getCity() != null) {
				sql.append(" AND " + separator + "city" + separator + " like '%" + itemArea.getCity() + "%'");
			}
			if (type.equals(1)) {

			} else if (type.equals(2)) {

			} else if (type.equals(3)) {
				sql.append(" AND " + separator + "type" + separator + " = 2 ");
			} else {
				return list;
			}
			sql.append(" ORDER BY " + separator + "type" + separator + "," + separator + "province" + separator + "," + separator + "city" + separator + "");

			dataSource = Common.getDataSource(configDBModel);
			list = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemAreaModel>(ItemAreaModel.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			list = new ArrayList<ItemAreaModel>();
		} finally {
			try {
				if (dataSource != null) {
					dataSource.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return list;
	}
}
