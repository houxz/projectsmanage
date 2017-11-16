package com.emg.projectsmanage.common;

/**
 * 消息是否已读
 * 
 * @author zsen
 * 
 */
public enum MessageReadType {
	/**
	 * 0-未读
	 */
	UNREAD(0, "未读"),
	/**
	 * 1-已读
	 */
	ENREAD(1, "已读");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private MessageReadType(Integer value, String des) {
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
