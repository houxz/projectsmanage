package com.emg.poiwebeditor.common;

/**
 *  优先级
 * 
 * @author zsen
 * 
 */
public enum PriorityLevel {
	/**
	 * -2, "极低"
	 */
	LOWEST(-2, "极低"),
	/**
	 * -1, "低"
	 */
	LOW(-1, "低"),
	/**
	 * 0, "一般"
	 */
	NORMAL(0, "一般"),
	/**
	 * 1, "高"
	 */
	HIGH(1, "高"),
	/**
	 * 2, "极高"
	 */
	HIGHEST(2, "极高");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private PriorityLevel(Integer value, String des) {
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
		for (PriorityLevel priorityLevel : PriorityLevel.values()) {
			str += "\"" + priorityLevel.getValue() + "\":\"" + priorityLevel.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
