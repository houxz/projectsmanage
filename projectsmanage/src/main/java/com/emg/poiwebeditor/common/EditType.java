package com.emg.poiwebeditor.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum EditType {
	/**
	 * 0-未知
	 */
	UNKNOW(0, "未知"),
	/**
	 * 5-制作
	 */
	EDIT(5, "制作"),
	/**
	 * 6-校正
	 */
	CHECK(6, "校正"),
	/**
	 * 7-修改错误
	 */
	MODIFY(7, "修改错误"),
	/**
	 * 8-验证
	 */
	CONFIRM(8, "验证");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private EditType(Integer value, String des) {
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
