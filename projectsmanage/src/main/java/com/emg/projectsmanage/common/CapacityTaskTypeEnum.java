package com.emg.projectsmanage.common;

public enum CapacityTaskTypeEnum {
	/**
	 * 15101, "POI客投制作"
	 */
	KETOU(15101, "POI客投制作"),
	/**
	 * 15102, "POI易淘金制作"
	 */
	GEN(15102, "POI易淘金制作"),
	/**
	 * 15103, "POI易淘金导入自动完成任务"
	 */
	GEN_IMPORT(15103, "POI易淘金导入自动完成任务"),
	/**
	 * 15105, "POI非实测导入自动完成"
	 */
	FEISHICE_IMPORT(15105, "POI非实测导入自动完成"),
	/**
	 * 15106, "POI非实测改错任务"
	 */
	FEISHICE(15106, "POI非实测改错任务"),
	/**
	 * 15201, "POI客投校正"
	 */
	MC_KETOU(15201, "POI客投校正"),
	/**
	 * 15202, "POI易淘金校正"
	 */
	MC_GEN(15202, "POI易淘金校正"),
	/**
	 * 15203, "POI非实测自动校正"
	 */
	MC_FEISHICE(15203, "POI非实测自动校正"),
	/**
	 * 15204, "POI地址电话自动校正"
	 */
	MC_FEISHICEADDRESSTEL(15204, "POI地址电话自动校正"),
	/**
	 * 15107, "POI地址电话导入完成任务"
	 */
	FEISHICEADDRESSTEL_IMPORT(15107, "POI地址电话导入完成任务"),
	/**
	 * 15108, "POI地址电话改错任务"
	 */
	FEISHICEADDRESSTEL(15108, "POI地址电话改错任务"),
	/**
	 * 15119, "POI全国改错任务"
	 */
	QUANGUOQC(15119, "POI全国改错任务"),
	/**
	 * 15205, "POI全国质检自动校正"
	 */
	MC_QUANGUOQC(15205, "POI全国质检自动校正");

	private Integer value;
	private String des;

	private CapacityTaskTypeEnum(Integer value, String des) {
		this.setValue(value);
		this.des = des;
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
