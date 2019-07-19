package com.emg.poiwebeditor.pojo;

public class SpotCheckInfo {
	private Long processid;
	private Integer editid;
	private String  username;
	private Integer editnum;
	private Integer percent;
	private String  newprojectname;
	private Long    newprojectid; 
	private Long    newprocessid;
	
	public void  setProcessid(Long processid) {
		this.processid = processid;
	}
	
	public Long getProcessid() {
		return processid;
	}
	
	public void setEditid(Integer editid) {
		this.editid = editid;	
	}
	
	public Integer getEditid() {
		return editid;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPercent(Integer percent) {
		this.percent = percent;	
	}
	
	public Integer getPercent() {
		return percent;
	}
	
	public void  setNewprojectname(String newprojectname) {
		this.newprojectname = newprojectname;
	}
	
	public String getNewprojectname() {
		return newprojectname;
	}
	
	public void  setNewprojectid(Long newprojectid) {
		this.newprojectid = newprojectid;
	}
	
	public Long getNewprojectid() {
		return newprojectid;
	}
	
	public void setEditnum(Integer editnum) {
		this.editnum = editnum;
	}
	
	public Integer getEditnum() {
		return editnum;
	}
	
	public void setNewprocessid(Long newprocessid) {
		this.newprocessid = newprocessid;
	}
	
	public Long getNewprocessid() {
		return newprocessid;
	}
	
}
