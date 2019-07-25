package com.emg.poiwebeditor.dao.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emg.poiwebeditor.client.ExecuteSQLApiClientUtils;
import com.emg.poiwebeditor.client.HttpClientResult;
import com.emg.poiwebeditor.client.HttpClientUtils;
import com.emg.poiwebeditor.common.Common;
import com.emg.poiwebeditor.common.DatabaseType;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.DatasetModel;
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.keywordModelForTask;

import com.alibaba.fastjson.JSONObject;

@Component
public class DatasetModelDao {
	
	@Value("${fielddatabatchidurl}")
	private String batchidUrl ;
	
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
				if(record.getState() != null && record.getState().compareTo(0) > 0) {
					sql.append(" AND " + separator + "state" + separator + "=" + record.getState() );
				}
				if(record.getProcess() != null && record.getProcess().compareTo(0) > 0) {
					sql.append(" AND " + separator + "process" + separator + "=" + record.getProcess() );
				}
			}
			
			sql.append(" AND (state != 3 OR process != 8) ");//不显示项目完成的资料
			
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
				if(record.getState() != null && record.getState().compareTo(0) > 0) {
					sql.append(" AND " + separator + "state" + separator + "=" + record.getState() );
				}
				if(record.getProcess() != null && record.getProcess().compareTo(0) > 0) {
					sql.append(" AND " + separator + "process" + separator + "=" + record.getProcess() );
				}
			}
			
			sql.append(" AND (state != 3 OR process != 8) ");//不显示项目完成的资料

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
			sql.append(" select tb_keywords.\"id\", \"count\"(tb_referdata.src_type = 1 or null ) as emapcount,\"count\"(tb_referdata.src_type = 45 or null ) as bdcount ,\r\n" + 
					" \"count\"(tb_referdata.src_type = 46 or null ) as txcount , \"count\"(tb_referdata.src_type = 47 or null ) as gdcount ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_keywords LEFT JOIN tb_referdata on \r\n" + 
					"tb_keywords.\"id\" = tb_referdata.ref_keyword_id ");
			sql.append(" WHERE state != 3 ");//不要废弃的
		
			sql.append(" AND " + separator + "datasetid" + separator + " IN ( " + sdatasetid + " ) ");
			
			sql.append("  GROUP BY tb_keywords.id ORDER BY id asc ");
	
			dataSource = Common.getDataSource(configDBModel);
	
			datasets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<keywordModelForTask>(keywordModelForTask.class));
		
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
	public Boolean updateDataSetStatebyDataset(ConfigDBModel configDBModel, String datasetid, Integer state,Integer process) {
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
			sql.append(" where id in ( " + datasetid +" )");

			System.out.println(sql);

			dataSource = Common.getDataSource(configDBModel);

			int row = new JdbcTemplate(dataSource).update(sql.toString());
			if (row > 0 )
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
	
	//插入dataset空记录
	public Long InsertDataset(ConfigDBModel configDBModel) {
		Long id = -1L;
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return -1L;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" insert into  ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			
			sql.append("tb_dataset (recordcount,datatype,batchid)values(0,0,0)");
			sql.append(" RETURNING id");
			dataSource = Common.getDataSource(configDBModel);

			id = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null,Long.class);
			
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
		return id;
	}
	
	
	public Boolean updateDataset(ConfigDBModel configDBModel,DatasetModel dataset) {
		BasicDataSource dataSource = null;
		Boolean bret = false;
		try {
			if (configDBModel == null)
				return false;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" update  ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_dataset ");
			sql.append("set ver = 0 " );
			if(dataset.getRecordcount() != null)
			sql.append(",recordcount ="+ dataset.getRecordcount().toString()); 
			if( dataset.getPath() != null)
				sql.append(",path='" + dataset.getPath() +"'");
			if( dataset.getName() != null)
				sql.append(",name='"+ dataset.getName() +"'");
			sql.append(",datatype=37");
			sql.append(",startdatetime=now()");
			if( dataset.getUsername() != null)
				sql.append(",username='"+dataset.getUsername() +"'");
			if( dataset.getRoleid() != null)
				sql.append(",userid="+ dataset.getRoleid().toString());
			if(dataset.getReason() != null)
				sql.append(",reason="+ dataset.getReason().toString() );
			if( dataset.getMode() != null)
				sql.append(",mode=" + dataset.getMode().toString() );
			if( dataset.getDatasource() != null)
				sql.append(",datasource="+dataset.getDatasource() );
			if( dataset.getArea_code() != null)
				sql.append(",area_code=" + dataset.getArea_code().toString());
			if( dataset.getCity_code() != null)
				sql.append(",city_code="+ dataset.getCity_code().toString() );
			if(dataset.getBatchid() != null)
				sql.append(",batchid=" + dataset.getBatchid() );
			if( dataset.getEnvelope() != null)
				sql.append(",envelope='" + dataset.getEnvelope().toString() +"'");// 这里可能有问题
			if( dataset.getState() != null)
				sql.append(",state = " +  dataset.getState().toString());
			if( dataset.getProcess() != null)
				sql.append(" , process=" + dataset.getProcess().toString() );
			
			sql.append(" where id ="+ dataset.getId() );
System.out.println(sql);

			dataSource = Common.getDataSource(configDBModel);

			int row = new JdbcTemplate(dataSource).update(sql.toString());
			if (row == 1)
				bret = true;
		
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
		return bret;
	}
	
	/*
	 * tb_keywords表插入记录
	 * */
	public Boolean Insertkeyword(ConfigDBModel configDBModel,KeywordModel kmodel) {
		BasicDataSource dataSource = null;
		Boolean bret = false;
		try {
			if (configDBModel == null)
				return false;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			String fieldsname = "";
			
			
			StringBuffer sql = new StringBuffer();
			sql.append(" insert into  ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_keywords (province,city,district,name,address,telephone,geo,\"desc\",src_type,src_inner_id,remark,datasetid,query_type,distance,poi_type) values(");
			if( kmodel.getProvince() !=null)
				sql.append("'"+ kmodel.getProvince() +"',");
			else
				sql.append("'',");
			if(kmodel.getCity() != null)
				sql.append("'" + kmodel.getCity() +"',");
			else
				sql.append("'',");
			
			if(kmodel.getDistrict() != null)
				sql.append("'" + kmodel.getDistrict() +"',");
			else
				sql.append("'',");
			
			if(kmodel.getName() != null)
				sql.append("'" + kmodel.getName() + "',");
			else
				sql.append("'',");
			
			if(kmodel.getAddress()!= null)
				sql.append("'" + kmodel.getAddress() + "',");
			else
				sql.append("'',");
			
			if(kmodel.getTelephone() != null)
				sql.append("'" + kmodel.getTelephone() +"',");
			else
				sql.append("'',");
			
			if(kmodel.getGeo() != null)
				sql.append("'" + kmodel.getGeo() +"',");
			else
				sql.append("NULL,");
			
			if(kmodel.getDesc() != null)
				sql.append( "'" + kmodel.getDesc() + "',");
			else
				sql.append("'',");
				
	
			
			if(kmodel.getSrcType() != null)
				sql.append( kmodel.getSrcType() +",");
			else
				sql.append("NULL,");
			
			if(kmodel.getSrcInnerId() != null)
				sql.append("'"+kmodel.getSrcInnerId() + "',");
			else
				sql.append("'',");
			
			if(kmodel.getRemark() != null)
				sql.append("'"+ kmodel.getRemark() +"',");
			else
				sql.append("'',");
			
			if(kmodel.getDatasetId() != null)
				sql.append(kmodel.getDatasetId() +",");
			else
				sql.append("NULL,");
			
			if(kmodel.getQueryType() != null)
				sql.append(kmodel.getQueryType() + ",");
			else
				sql.append("NULL,");
				
			if(kmodel.getDistance() != null)
				sql.append(kmodel.getDistance() + ",");
			else
				sql.append("NULL,");
			
			if(kmodel.getPoiType() != null)
				sql.append("'" +kmodel.getPoiType()+"'");
			else
				sql.append("''");
			
			sql.append(" )");
System.out.println(sql);

			dataSource = Common.getDataSource(configDBModel);

			int row = new JdbcTemplate(dataSource).update(sql.toString());
			if (row == 1)
				bret = true;
			else {
				int a = 0;
				a +=1;
			}

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
		return bret;
	}
	
	/*
	 * tb_batch表插入数据
	 * */
	public Long InsertBatch(ConfigDBModel configDBModel,Long batchid,Integer userid,String username) {
		Long id = -1L;
		BasicDataSource dataSource = null;
		try {
			if (configDBModel == null)
				return -1L;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			StringBuffer sql = new StringBuffer();
			sql.append(" insert into  ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			
			sql.append("tb_batch (batchid,userid,username,uploadstarttime)values(");
			sql.append( batchid +",");
			sql.append(userid + ",'");
			sql.append(username  + "',");
			sql.append("now()" +")" );
			sql.append(" RETURNING id");
			dataSource = Common.getDataSource(configDBModel);

			id = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null,Long.class);
			
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
		return id;
	}
	
	/*
	 * 更新endtime
	 * */
	public Boolean updateBatch(ConfigDBModel configDBModel,Long bid) {
		BasicDataSource dataSource = null;
		Boolean bret = false;
		try {
			if (configDBModel == null)
				return false;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			String fieldsname = "";
			
			
			StringBuffer sql = new StringBuffer();
			sql.append(" update  ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append(" tb_batch set uploadendtime = now() where id = " + bid);
			
System.out.println(sql);

			dataSource = Common.getDataSource(configDBModel);

			int row = new JdbcTemplate(dataSource).update(sql.toString());
			if (row == 1)
				bret = true;

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
		return bret;
	}
	
	public Long getBatchid() {
		String httpurl = batchidUrl;
		Long ret = -1L;
		try {
			HttpClientResult result = HttpClientUtils.doGet(httpurl);
			if (!result.getStatus().equals(HttpStatus.OK))
				return ret;
			
			JSONObject json = JSONObject.parseObject(result.getJson());
			if (json.containsKey("batchId")) {
		    	ret = (Long)json.get("batchId");
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/*
	 * tb_keywords 更新记录
	 * */
	public Boolean Updatekeywordsrcinnerid(ConfigDBModel configDBModel,Long datasetid) {
		BasicDataSource dataSource = null;
		Boolean bret = false;
		try {
			if (configDBModel == null)
				return false;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			String fieldsname = "";
			
			
			StringBuffer sql = new StringBuffer();
			sql.append(" update  ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_keywords set src_inner_id = id where datasetid = " + datasetid.toString());
		
System.out.println(sql);

			dataSource = Common.getDataSource(configDBModel);

			int row = new JdbcTemplate(dataSource).update(sql.toString());
			if (row > 0)
				bret = true;

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
		return bret;
	}
	
	public Boolean Updatekeywordstate(ConfigDBModel configDBModel,Long id,Integer state) {
		BasicDataSource dataSource = null;
		Boolean bret = false;
		try {
			if (configDBModel == null)
				return false;
			Integer dbtype = configDBModel.getDbtype();

			String separator = Common.getDatabaseSeparator(dbtype);

			String fieldsname = "";
			
			
			StringBuffer sql = new StringBuffer();
			sql.append(" update  ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_keywords set state = "+ state +" where id = " + id.toString());
		
System.out.println(sql);

			dataSource = Common.getDataSource(configDBModel);

			int row = new JdbcTemplate(dataSource).update(sql.toString());
			if (row > 0)
				bret = true;

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
		return bret;
	}
	
	public List<DatasetModel> selectDatasetsByDatasetids(ConfigDBModel configDBModel, String datasetids) {
		List<DatasetModel> datasets = new ArrayList<DatasetModel>();
		BasicDataSource dataSource = null;
		try {
			Integer dbtype = configDBModel.getDbtype();
			String separator = Common.getDatabaseSeparator(dbtype);
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_dataset ");
			sql.append(" WHERE id in ( " + datasetids);
			sql.append(" )ORDER BY id desc ");
			

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
}
