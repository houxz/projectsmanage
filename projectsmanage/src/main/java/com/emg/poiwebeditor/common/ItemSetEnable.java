package com.emg.poiwebeditor.common;

/**
 * 质检项集合可用状态
 * 
 * @author zsen
 * 
 */
public enum ItemSetEnable {
	/**
	 * 0, "可用"
	 */
	ENABLE(0, "可用"),
	/**
	 * 1, "不可用"
	 */
	UNABLE(1, "不可用");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ItemSetEnable(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public static String toJsonStr() {
		String str = new String("{");
		for (ItemSetEnable val : ItemSetEnable.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
