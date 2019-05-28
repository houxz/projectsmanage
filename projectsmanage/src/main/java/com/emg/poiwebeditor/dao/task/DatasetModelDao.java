package com.emg.poiwebeditor.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.emg.poiwebeditor.pojo.keywordModelForTask;

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
	
	//查询可创建交互确认的dataset byxhz20190517 state =3 process = 2
	public List<DatasetModel> selectOkDatasets(ConfigDBModel configDBModel, List<Integer> dataTypes, Integer limit, Integer offset) {
		List<DatasetModel> datasets = new ArrayList<DatasetModel>();
		BasicDataSource dataSource = null;
		try {
			if ( configDBModel == null)
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
			sql.append(" WHERE state = 3 and process = 2 ");
			if (dataTypes != null && !dataTypes.isEmpty()) {
				sql.append(" AND " + separator + "datatype" + separator + " IN ( " + StringUtils.join(dataTypes, ",") + " ) ");
			}
		
			sql.append(" ORDER BY id desc ");
			if (limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if (offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}
System.out.println(sql);
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
	
	//查询可创建交互确认任务的资料数 byhxz20190517  state =3 process = 2
	public Integer countOKDataSets(ConfigDBModel configDBModel, List<Integer> dataTypes) {
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
			sql.append(" WHERE state = 3 and process = 2 ");
			if (dataTypes != null && !dataTypes.isEmpty()) {
				sql.append(" AND " + separator + "datatype" + separator + " IN ( " + StringUtils.join(dataTypes, ",") + " ) ");
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

	//获取批次的keyid集合
	public List<keywordModelForTask> selectKeyidsbyDataset(ConfigDBModel configDBModel, Integer limit, Integer offset,String sdatasetid) {
		List<keywordModelForTask> datasets = new ArrayList<keywordModelForTask>();
		BasicDataSource dataSource = null;
		try {
			if ( configDBModel == null)
				return datasets;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT id");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_keywords ");
			sql.append(" WHERE 1=1 ");
		
			sql.append(" AND " + separator + "datasetid" + separator + " IN ( " + sdatasetid + " ) ");
			
System.out.println(sql);

			sql.append(" ORDER BY id asc ");
	
			dataSource = Common.getDataSource(configDBModel);
	
			datasets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<keywordModelForTask>(keywordModelForTask.class));
		
//			List<Long> das =  new JdbcTemplate(dataSource).queryForList(sql.toString(), Long.class);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			datasets = new ArrayList<keywordModelForTask>();
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
	
		//更新dataset 状态
	public Boolean updateDataSetStatebyDataset(ConfigDBModel configDBModel, Long datasetid, Integer state,Integer process) {
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return false;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" update ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_dataset ");
			sql.append(" set state = " + state);
			sql.append(" , process=" + process);
			sql.append(" where id = " + datasetid);

			System.out.println(sql);

			dataSource = Common.getDataSource(configDBModel);

			int row = new JdbcTemplate(dataSource).update(sql.toString());
			if (row == 1)
				return true;
			else
				return false;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
			if (dataSource != null) {
				try {
					dataSource.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return false;
	}
	
}
