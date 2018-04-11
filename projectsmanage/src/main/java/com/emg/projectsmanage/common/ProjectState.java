package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 项目状态：0未开始1开始2暂停3锁定4完成5发布
 * @author zsen
 * 
 */
public enum ProjectState {
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
	 * 3, "锁定"
	 */
	LOCK(3, "锁定"),
	/**
	 * 4-完成
	 */
	COMPLETE(4, "完成"),
	/**
	 * 5, "公布"
	 */
	PUBLIC(5, "公布"),;

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ProjectState(Integer value, String des) {
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
		for (ProjectState values : ProjectState.values()) {
			str += "\"" + values.getValue() + "\":\"" + values.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
