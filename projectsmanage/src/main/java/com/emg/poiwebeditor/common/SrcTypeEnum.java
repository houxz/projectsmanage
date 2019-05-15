package com.emg.poiwebeditor.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum SrcTypeEnum {
	/**
	 * 0, "未知"
	 */
	UNKNOW(0, "未知"),
	/**
	 * 1, "易图通"
	 */
	EMG(1, "易图通"),
	/**
	 * 31, "百度"
	 */
	BAIDU(45, "百度"),
	/**
	 * 32,"高德"
	 */
	GAODE(47,"高德"),
	/**
	 * 33,"腾讯"
	 */
	TENGXUN(46,"腾讯");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private SrcTypeEnum(Integer value, String des) {
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
