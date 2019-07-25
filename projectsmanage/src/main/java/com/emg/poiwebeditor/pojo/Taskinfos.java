package com.emg.poiwebeditor.pojo;

public class Taskinfos {
	private Long  id;
	private Integer state;
	private Integer process;
	private String  editname;
	private String  checkname;
	private Long    processid;
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setState(Integer state) {
		this.state = state;
	}
	
	public Integer getState() {
		return state;
	}
	
	public void setProcess(Integer process) {
		this.process = process;
	}
	public Integer getProcess() {
		return process;
	}
	public void setEditname(String editname) {
		this.editname = editname;
	}
	
	public String getEditname() {
		return editname;
	}
	
	public void setCheckname(String checkname) {
		this.checkname = checkname;
	}
	
	public String getCheckname() {
		return checkname;
	}
	public void setProcessid(Long processid) {
		this.processid = processid;	
	}
	public Long getProcessid() {
		return processid;
	}
	
	
}
