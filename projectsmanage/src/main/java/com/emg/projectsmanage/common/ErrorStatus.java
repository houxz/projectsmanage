package com.emg.projectsmanage.common;

/**
 * 错误状态
 * 
 * @author zsen
 * 
 */
public enum ErrorStatus {
	/**
	 * 0-未知
	 */
	UNKNOW(0, "未知"),
	/**
	 * 1-未解决
	 */
	UNSOLVED(1, "未解决"),
	/**
	 * 2-解决
	 */
	SOLVED(2, "解决"),
	/**
	 * 3-关闭
	 */
	CLOSE(3, "关闭");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ErrorStatus(Integer value, String des) {
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
