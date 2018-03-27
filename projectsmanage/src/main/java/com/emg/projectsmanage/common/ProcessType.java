package com.emg.projectsmanage.common;

/**
 * 质检项集合类型
 * 
 * @author zsen
 * 
 */
public enum ProcessType {
	/**
	 * 1, "改错项目"
	 */
	ERROR(1, "改错项目"),
	/**
	 * 2, "NR/FC项目"
	 */
	NRFC(2, "NR/FC项目");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ProcessType(Integer value, String des) {
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
		for (ProcessType val : ProcessType.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}