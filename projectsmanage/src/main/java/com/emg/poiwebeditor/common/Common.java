package com.emg.poiwebeditor.common;

import org.apache.commons.dbcp.BasicDataSource;

import com.emg.poiwebeditor.pojo.ConfigDBModel;

public class Common {

	public static String getDatabaseSeparator(Integer databaseType) throws Exception {
		String separator = new String();
		if (databaseType.equals(DatabaseType.POSTGRESQL.getValue())) {
			separator = DatabaseSeparator.POSTGRESQL.getSeparator();
		} else if (databaseType.equals(DatabaseType.MYSQL.getValue())) {
			separator = DatabaseSeparator.MYSQL.getSeparator();
		} else {
			separator = DatabaseSeparator.DEFAULT.getSeparator();
		}

		return separator;
	}

	public static String getUrl(ConfigDBModel configDBModel) throws Exception {
		StringBuffer url = new StringBuffer();
		Integer dbtype = configDBModel.getDbtype();
		if (dbtype.equals(DatabaseType.MYSQL.getValue())) {
			url.append("jdbc:mysql://");
		} else if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
			url.append("jdbc:postgresql://");
		} else {
			return null;
		}
		url.append(configDBModel.getIp());
		url.append(":");
		url.append(configDBModel.getPort());
		url.append("/");
		url.append(configDBModel.getDbname());
		url.append("?characterEncoding=UTF-8");
		return url.toString();
	}

	public static BasicDataSource getDataSource(ConfigDBModel configDBModel) throws Exception {
		BasicDataSource dataSource = new BasicDataSource();
		Integer dbtype = configDBModel.getDbtype();
		if (dbtype.equals(DatabaseType.MYSQL.getValue())) {
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		} else if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
			dataSource.setDriverClassName("org.postgresql.Driver");
		} else {
			return null;
		}
		dataSource.setUrl(getUrl(configDBModel));
		dataSource.setUsername(configDBModel.getUser());
		dataSource.setPassword(configDBModel.getPassword());
		return dataSource;
	}

}
