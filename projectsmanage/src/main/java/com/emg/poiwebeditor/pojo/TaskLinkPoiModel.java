package com.emg.poiwebeditor.pojo;

public class TaskLinkPoiModel {
	private Long  id;
	private Long  taskid;
	private Long  poiid;
	
	private String updatetime;
	private int pstate;
	
	public  Long  getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getTaskId() {
		return taskid;
	}
	
	public void setTaskId(Long taskid) {
		this.taskid = taskid;
	}
	
	public Long getPoiId() {
		return poiid;
	}
	
	public void setPoiId(Long poiid) {
		this.poiid = poiid;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public int getPstate() {
		return pstate;
	}

	public void setPstate(int pstate) {
		this.pstate = pstate;
	}
}
