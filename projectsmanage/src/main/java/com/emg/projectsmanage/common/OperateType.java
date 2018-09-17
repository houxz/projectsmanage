package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum OperateType {
	/**
	 * -1, "无"
	 */
	NO(-1, "无"),
	/**
	 * 0-未知
	 */
	UNKNOW(0, "未确认"),
	/**
	 * 1-确认
	 */
	CONFIRM(1, "确认"),
	/**
	 * 2-新增
	 */
	NEW(2, "新增"),
	/**
	 * 3-疑问
	 */
	QUESTION(3, "疑问"),
	/**
	 * 4-存量删除
	 */
	DELETE(4, "存量删除"),
	/**
	 * 5-新增删除
	 */
	NEWDELETE(5, "新增删除"),
	/**
	 * 6-确认修改
	 */
	CONFIRMMODIFY(6, "确认修改"),
	/**
	 * 7, "外业确认"
	 */
	COLLECTCONFIRM(7, "外业确认"),
	/**
	 * 8, "外业修改"
	 */
	COLLECTMODIFY(8, "外业修改"),
	/**
	 * 9, "外业移位"
	 */
	COLLECTMOVEPT(9, "外业移位");

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
