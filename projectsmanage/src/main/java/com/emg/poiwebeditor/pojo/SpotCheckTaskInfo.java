package com.emg.poiwebeditor.pojo;

public class SpotCheckTaskInfo {
	private Integer editid;
	private Integer editnum;
	private String  username;
	private Long    processid;
	
	
	public void setEditid(Integer editid) {
		this.editid = editid;
	}
	
	public Integer getEditid() {
		return editid;
	}
	
	public void setEditnum(Integer editnum) {
		this.editnum = editnum;
	}
	
	public Integer getEditnum() {
		return editnum;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setProcessid(Long processid) {
		this.processid = processid;
	}
	
	public Long  getProcessid() {
		return processid;
	}
}
