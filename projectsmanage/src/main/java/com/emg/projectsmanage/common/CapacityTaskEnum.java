package com.emg.projectsmanage.common;

public enum CapacityTaskEnum {
	/**
	 * 0：新任务
	 */
	NEW(0, "新任务"),
	/**
	 * 1, 执行中
	 */
	DOING(1, "执行中"),
	/**
	 * 2, 正常完成
	 */
	FINISHED(2, "正常完成"),
	/**
	 * -1, 异常结束
	 */
	ERROR(-1, "异常结束");

	private Integer value;
	private String des;

	private CapacityTaskEnum(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

}
