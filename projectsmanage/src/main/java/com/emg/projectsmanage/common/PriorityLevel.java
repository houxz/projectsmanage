package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum PriorityLevel {
	/**
	 * -2, "极低"
	 */
	UNKNOW(-2, "极低"),
	/**
	 * -1, "低"
	 */
	EDIT(-1, "低"),
	/**
	 * 0, "一般"
	 */
	CHECK(0, "一般"),
	/**
	 * 1, "高"
	 */
	MODIFY(1, "高"),
	/**
	 * 2, "极高"
	 */
	CONFIRM(2, "极高");

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
