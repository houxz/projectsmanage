package com.emg.projectsmanage.common;

public enum ModelEnum {
	/**
	 * 1, "全部质检"
	 */
	ALLCHECK(1, "全部质检"),
	
	/**
	 * 2, "增量质检"
	 */
	ADDCHECK(2, "增量质检"),
	
	/**
	 * 3, "日发质检"
	 */
	DAYCHECK(3, "日发质检");
	
	private Integer value;
	private String des;
	
	private ModelEnum(Integer value, String des) {
		this.setValue(value);
		this.setDes(des);
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public static String toJsonStr(){
		String str = new String("{");
		for(ModelEnum jobStatus : ModelEnum.values()){
			str += "\"" + jobStatus.getValue() + "\":\"" + jobStatus.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
