package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum ProcessEditType {
	/**
	 * 0, "未知"
	 */
	NEW(0, "未知"),
	/**
	 * 1, "区划"
	 */
	QUHUA(1, "区划"),
	/**
	 * 2, "建成区"
	 */
	JIANCHENGQU(2, "建成区");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ProcessEditType(Integer value, String des) {
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
		for (ProcessEditType value : ProcessEditType.values()) {
			str += "\"" + value.getValue() + "\":\"" + value.getDes() + "\",";
		}
		str += "}";
		return str;
	}

}
