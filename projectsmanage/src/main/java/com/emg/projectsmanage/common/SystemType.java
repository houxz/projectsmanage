package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum SystemType {
	/**
	 * 0-未知
	 */
	UNKNOW(0, "未知"),
	/**
	 * 332, "质检项目"
	 */
	CHECK(332, "质检项目"),
	/**
	 * 349, "改错项目"
	 */
	ERROR(349, "改错项目"),
	/**
	 * 349, "改错项目"
	 */
	NRFC(366, "NR/FC项目");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private SystemType(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public static String toJsonStr() {
		String str = new String("{");
		for (SystemType val : SystemType.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
