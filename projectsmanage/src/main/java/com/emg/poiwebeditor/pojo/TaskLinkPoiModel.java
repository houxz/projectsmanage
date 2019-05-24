package com.emg.poiwebeditor.pojo;

public class TaskLinkPoiModel {
	private Long  id;
	private Long  taskid;
	private Long  poiid;
	
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
}
