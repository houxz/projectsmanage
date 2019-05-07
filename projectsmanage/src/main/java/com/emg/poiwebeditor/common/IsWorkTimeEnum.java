package com.emg.poiwebeditor.common;

public enum IsWorkTimeEnum {
	/**
	 * 0, "上班时间"
	 * @return 
	 */
	isWorkTime(0, "上班"),
	/**
	 * 1, "下班时间"
	 */
	isNotWorkTime(1, "下班");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private IsWorkTimeEnum(Integer value, String des) {
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
		for (IsWorkTimeEnum val : IsWorkTimeEnum.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
