package com.emg.projectsmanage.common;

public class Common {

	public static String getDatabaseSeparator(Integer databaseType) {
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
}
