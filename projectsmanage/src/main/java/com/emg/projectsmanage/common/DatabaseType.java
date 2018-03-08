package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum DatabaseType {
	/**
	 * 0-未知
	 */
	UNKNOW(0, "未知"),
	/**
	 * 1, "MySQL"
	 */
	MYSQL(1, "MySQL"),
	/**
	 * 2, "PostgreSQL"
	 */
	POSTGRESQL(2, "PostgreSQL");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private DatabaseType(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
