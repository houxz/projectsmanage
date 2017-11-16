package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum OperateType {
	/**
	 * 0-未知
	 */
	UNKNOW(0, "未知"),
	/**
	 * 1-确认
	 */
	CONFIRM(1, "确认"),
	/**
	 * 2-新增
	 */
	ADD(2, "新增"),
	/**
	 * 3-疑问
	 */
	DOUBT(3, "疑问"),
	/**
	 * 4-存量删除
	 */
	DELETE(4, "存量删除"),
	/**
	 * 5-新增删除
	 */
	NEWDELETE(5, "新增删除"),
	/**
	 * 6-修改
	 */
	MODIFY(6, "修改");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private OperateType(Integer value, String des) {
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
