package com.emg.poiwebeditor.common;

/**
 * 质检项集合质检单位
 * 
 * @author zsen
 * 
 */
public enum ItemSetUnit {
	/**
	 * 0, "自定义"
	 */
	OTHER(0, "自定义"),
	/**
	 * 1, "全图"
	 */
	COUNTRY(1, "全图"),
	/**
	 * 2, "省"
	 */
	PROVINCE(2, "省"),
	/**
	 * 3, "市"
	 */
	CITY(3, "市");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ItemSetUnit(Integer value, String des) {
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
		for (ItemSetUnit val : ItemSetUnit.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
