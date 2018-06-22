package com.emg.projectsmanage.common;

/**
 * 错误状态
 * 
 * @author zsen
 * 
 */
public enum ItemAreaType {
	/**
	 * 0-国家
	 */
	COUNTRY(0, "国家"),
	/**
	 * 1-省
	 */
//	PROVINCE(1, "省"),
	/**
	 * 2-市
	 */
	CITY(2, "市");
	/**
	 * 3-区
	 */
//	CLOSE(3, "区");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ItemAreaType(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public static String toJsonStr(){
		String str = new String("{");
		for(ItemAreaType jobStatus : ItemAreaType.values()){
			str += "\"" + jobStatus.getValue() + "\":\"" + jobStatus.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
