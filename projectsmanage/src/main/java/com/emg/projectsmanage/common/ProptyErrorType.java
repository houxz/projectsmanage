package com.emg.projectsmanage.common;

/**
 * 属性错误
 * 
 * @author zsen
 * 
 */
public enum ProptyErrorType {
	/**
	 * 无
	 */
	NONE(21100010000L, "无"),
	/**
	 * 错别字
	 */
	TYPOS(21100010001L, "错别字"),
	/**
	 * 未按规则制作
	 */
	ILLEGAL(21100010002L, "未按规则制作"),
	/**
	 * 多制作
	 */
	EXTRA(21100010003L, "多制作"),
	/**
	 * 漏制作
	 */
	MISS(21100010004L, "漏制作"),
	/**
	 * 未对保密信息处理
	 */
	SECRECY(21100010005L, "未对保密信息处理");

	private Long value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ProptyErrorType(Long value, String des) {
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
