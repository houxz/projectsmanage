package com.emg.poiwebeditor.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emg.poiwebeditor.common.Common;
import com.emg.poiwebeditor.common.DatabaseType;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.DatasetModel;

@Component
public class DatasetModelDao {
	
	private static final Logger logger = LoggerFactory.getLogger(DatasetModelDao.class);
	
	public List<DatasetModel> selectDatasets(ConfigDBModel configDBModel, List<Integer> dataTypes, DatasetModel record, Integer limit, Integer offset) {
		List<DatasetModel> datasets = new ArrayList<DatasetModel>();
		BasicDataSource dataSource = null;
		try {
			if (record == null || configDBModel == null)
				return datasets;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_dataset ");
			sql.append(" WHERE 1=1 ");
			if (dataTypes != null && !dataTypes.isEmpty()) {
				sql.append(" AND " + separator + "datatype" + separator + " IN ( " + StringUtils.join(dataTypes, ",") + " ) ");
			}
			if (record != null) {
				if (record.getId() != null && record.getId().compareTo(0L) > 0) {
					sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
				}
				if (record.getName() != null && !record.getName().isEmpty()) {
					sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
				}
				if (record.getDatatype() != null && record.getDatatype().compareTo(0) > 0) {
					sql.append(" AND " + separator + "datatype" + separator + " = " + record.getDatatype());
				}
				if (record.getBatchid() != null && !record.getBatchid().isEmpty()) {
					sql.append(" AND " + separator + "batchid" + separator + " = " + record.getBatchid());
				}
				if (record.getPath() != null && !record.getPath().isEmpty()) {
					sql.append(" AND " + separator + "path" + separator + " like '%" + record.getPath() + "%'");
				}
			}
			sql.append(" ORDER BY id desc ");
			if (limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if (offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			dataSource = Common.getDataSource(configDBModel);
			datasets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<DatasetModel>(DatasetModel.class));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			datasets = new ArrayList<DatasetModel>();
		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return datasets;
	}

	public Integer countErrorSets(ConfigDBModel configDBModel, List<Integer> dataTypes, DatasetModel record, Integer limit, Integer offset) {
		Integer count = -1;
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)	return count;
			
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT count(*) FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_dataset");
			sql.append(" WHERE 1=1 ");
			if (dataTypes != null && !dataTypes.isEmpty()) {
				sql.append(" AND " + separator + "datatype" + separator + " IN ( " + StringUtils.join(dataTypes, ",") + " ) ");
			}
			if (record != null) {
				if (record.getId() != null && record.getId().compareTo(0L) > 0) {
					sql.append(" AND " + separator + "id" + separator + " = " + record.getId());
				}
				if (record.getName() != null && !record.getName().isEmpty()) {
					sql.append(" AND " + separator + "name" + separator + " like '%" + record.getName() + "%'");
				}
				if (record.getDatatype() != null && record.getDatatype().compareTo(0) > 0) {
					sql.append(" AND " + separator + "datatype" + separator + " = " + record.getDatatype());
				}
				if (record.getBatchid() != null && !record.getBatchid().isEmpty()) {
					sql.append(" AND " + separator + "batchid" + separator + " = " + record.getBatchid());
				}
				if (record.getPath() != null && !record.getPath().isEmpty()) {
					sql.append(" AND " + separator + "path" + separator + " like '%" + record.getPath() + "%'");
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
