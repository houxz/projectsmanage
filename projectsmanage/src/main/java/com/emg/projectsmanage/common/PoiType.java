package com.emg.projectsmanage.common;

/**
 * POI类型
 * 
 * @author zsen
 * 
 */
public enum PoiType {
	/**
	 * 未知
	 */
	UNKNOW(0, "未知"),
	/**
	 * 常规POI
	 */
	COMMON(1, "常规POI"),
	/**
	 * 门址POI
	 */
	SITE(2, "门址POI");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private PoiType(Integer value, String des) {
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
