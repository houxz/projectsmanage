package com.emg.projectsmanage.common;

/**
 * 质检项集合类型
 * 
 * @author zsen
 * 
 */
public enum ItemSetType {
	/**
	 * 0, "质检项"
	 */
	ITEM(0, "质检项"),
	/**
	 * 1, "工具"
	 */
	TOOLS(1, "工具"),
	/**
	 * 5, "全国九宫格检查逻辑-全国质检"
	 */
	JIUGONGGEZHIJIAN(5, "九宫格质检"),
	/**
	 * 6, "全域检查逻辑-全国质检"
	 */
	QUANYUZHIJIAN(6, "全域质检");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ItemSetType(Integer value, String des) {
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
		for (ItemSetType val : ItemSetType.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
