package com.emg.poiwebeditor.pojo;

public class SpotCheckProjectInfo {
	private Long  id;
	private Long  projectid;
	private Long  processid;
	private Integer editid;
	private Integer percent;
	private Long  newprojectid;
	private Long  newprocessid;
	private String username;
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void  setProjectid(long projectid) {
		this.projectid = projectid;
	}
	
	public Long getProjectid() {
		return projectid;
	}
	
	public void setProcessid(Long processid) {
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
	
	public void setPercent(Integer percent) {
		this.percent = percent;
	}
	
	public Integer getPercent() {
		return percent;
	}
	
	public void setNewprojectid(Long newprojectid) {
		this.newprojectid = newprojectid;
	}
	
	public Long getNewprojectid() {
		return newprojectid;
	}
	
	public void setNewprocessid(Long newprocessid) {
		this.newprocessid = newprocessid;
	}
	
	public Long getNewprocessid() {
		return newprocessid;
	}
	
	public void setUsernmae(String username) {
		this.username  = username;
	}
	
	public String getUsername() {
		return username;
	}
	
}
