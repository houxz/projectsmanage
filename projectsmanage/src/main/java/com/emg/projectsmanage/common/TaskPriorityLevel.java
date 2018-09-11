package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum TaskPriorityLevel {
	/**
	 * -9999, "最高"
	 */
	MOSTHIGH(-9999, "最高"),
	/**
	 * -1, "高"
	 */
	HIGH(-1, "高"),
	/**
	 * 0, "一般"
	 */
	NORMAL(0, "一般"),
	/**
	 * 1, "低"
	 */
	LOW(1, "低"),
	/**
	 * 9999, "最低"
	 */
	MOSTLOW(9999, "最低");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private TaskPriorityLevel(Integer value, String des) {
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
		for (TaskPriorityLevel priorityLevel : TaskPriorityLevel.values()) {
			str += "\"" + priorityLevel.getValue() + "\":\"" + priorityLevel.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
