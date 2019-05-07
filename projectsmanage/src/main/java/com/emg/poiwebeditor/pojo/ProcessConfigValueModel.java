package com.emg.poiwebeditor.pojo;

public class ProcessConfigValueModel {
	private Integer id;

	private Long processid;

	private Integer moduleid;

	private Integer configid;

	private String value;

	public ProcessConfigValueModel() {

	}

	public ProcessConfigValueModel(Long processid, Integer moduleid, Integer configid, String value) {
		this.processid = processid;
		this.moduleid = moduleid;
		this.configid = configid;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getProcessid() {
		return processid;
	}

	public void setProcessid(Long processid) {
		this.processid = processid;
	}

	public Integer getModuleid() {
		return moduleid;
	}

	public void setModuleid(Integer moduleid) {
		this.moduleid = moduleid;
	}

	public Integer getConfigid() {
		return configid;
	}

	public void setConfigid(Integer configid) {
		this.configid = configid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}
}