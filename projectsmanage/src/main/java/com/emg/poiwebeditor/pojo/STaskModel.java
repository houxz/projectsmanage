package com.emg.poiwebeditor.pojo;

import java.util.List;

public class STaskModel {
	private TaskModel task ;
	private List<ErrorModel> errorList;
	private Long poiid;
	
	public void setTaskMode(TaskModel task) {
		this.task = task;
	}
	
	public TaskModel getTaskModel() {
		return task;
	}
	
	public void setErrorList(List<ErrorModel> errorList ) {
		this.errorList = errorList;
	}
	
	public List<ErrorModel> getErrorList() {
		return errorList;
	}
	
	public void setPoiId(Long poiid) {
		this.poiid = poiid;
	}
	
	public Long getPoiId() {
		return poiid;
	}
	
}
