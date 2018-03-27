package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum SystemType {
	/**
	 * "POI视频编辑平台"
	 */
	poivideoedit(0, "POI视频编辑平台"),
	/**
	 * 332, "数据库质检系统"
	 */
	DBMapChecker(332, "数据库质检系统"),
	/**
	 * 349, "综合编辑平台"
	 */
	MapDbEdit(349, "综合编辑平台"),
	/**
	 * 3491, "综合编辑平台_NRFC编辑"
	 */
	MapDbEdit_NRFC(3491, "综合编辑平台_NRFC编辑");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private SystemType(Integer value, String des) {
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
		for (SystemType val : SystemType.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}