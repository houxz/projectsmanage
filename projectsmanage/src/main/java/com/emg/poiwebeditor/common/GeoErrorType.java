package com.emg.poiwebeditor.common;

/**
 * 位置错误
 * 
 * @author zsen
 * 
 */
public enum GeoErrorType {
	/**
	 * 漏新增
	 */
	MISSNEW(21200010031L, "漏新增"),
	/**
	 * 漏确认
	 */
	MISSCONFIRM(21200010032L, "漏确认"),
	/**
	 * 多新增
	 */
	EXTRANEW(21200010033L, "多新增"),
	/**
	 * 多确认
	 */
	EXTRACONFIRM(21200010034L, "多确认"),
	/**
	 * 漏删除
	 */
	MISSDELETE(21200010035L, "漏删除"),
	/**
	 * 多删除
	 */
	EXTRADELETE(21200010036L, "多删除"),
	/**
	 * 真位置错误
	 */
	TRUEPOSITION(21200010037L, "真位置错误"),
	/**
	 * 方位错误
	 */
	RELATIVEPOSITION(21200010038L, "方位错误"),
	/**
	 * 引导位置错误
	 */
	GUIDEPOSITION(21200010039L, "引导位置错误");

	private Long value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private GeoErrorType(Long value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
}
