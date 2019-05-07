package com.emg.poiwebeditor.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum DatabaseSeparator {
	/**
	 * DEFAULT, "`"
	 */
	DEFAULT(0, "`"),
	/**
	 * MySQL, "`"
	 */
	MYSQL(1, "`"),
	/**
	 * PostgreSQL, "\""
	 */
	POSTGRESQL(2, "\"");

	private Integer value;
	private String des;

	public String getSeparator() {
		return des;
	}

	private DatabaseSeparator(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	private void setValue(Integer value) {
		this.value = value;
	}
}
