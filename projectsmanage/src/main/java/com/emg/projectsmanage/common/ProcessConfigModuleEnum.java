package com.emg.projectsmanage.common;

public enum ProcessConfigModuleEnum {
	/**
	 * 1, "质检配置"
	 */
	ZHIJIANPEIZHI(1, "质检配置"),
	/**
	 * 2, "改错配置"
	 */
	GAICUOPEIZHI(2, "改错配置");

	private Integer value;
	private String des;
	
	private ProcessConfigModuleEnum(Integer value, String des) {
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

}