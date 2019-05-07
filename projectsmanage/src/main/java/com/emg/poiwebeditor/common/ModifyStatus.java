package com.emg.poiwebeditor.common;

/**
 * 修改状态
 * 
 * @author zsen
 * 
 */
public enum ModifyStatus {
	/**
	 * 未知
	 */
	UNKNOW(0, "未知"),
	/**
	 * 接受
	 */
	ACCEPTED(1, "接受"),
	/**
	 * 不接受
	 */
	UNACCEPTED(2, "不接受"),
	/**
	 * 其它值
	 */
	OTHERS(3, "其它值"),
	/**
	 * 搁置
	 */
	HANGUP(4, "搁置");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ModifyStatus(Integer value, String des) {
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
