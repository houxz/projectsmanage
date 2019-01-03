package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum EnableEnum {
	/**
	 * 0, "不可用"
	 */
	UNABLE(0, "不可用"),
	/**
	 * 1, "可用"
	 */
	ENABLE(1, "可用");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private EnableEnum(Integer value, String des) {
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
