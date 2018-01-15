package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum ProcessState {
	/**
	 * 0-新建
	 */
	NEW(0, "新建"),
	/**
	 * 1-开始
	 */
	START(1, "开始"),
	/**
	 * 2-暂停
	 */
	PAUSE(2, "暂停"),
	/**
	 * 3-完成
	 */
	COMPLETE(3, "完成");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ProcessState(Integer value, String des) {
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
		for (ProcessState values : ProcessState.values()) {
			str += "\"" + values.getValue() + "\":\"" + values.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
