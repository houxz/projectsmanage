package com.emg.poiwebeditor.pojo;

import java.util.List;

public class STaskModel {
	private TaskModel task ;
	private List<ErrorModel2> errorList;
	private Long poiid;
	
	public void setTaskMode(TaskModel task) {
		this.task = task;
	}
	
	public TaskModel getTaskModel() {
		return task;
	}
	
	public void setErrorList(List<ErrorModel2> errorList ) {
		this.errorList = errorList;
	}
	
	public List<ErrorModel2> getErrorList() {
		return errorList;
	}
	
	public void setPoiId(Long poiid) {
		this.poiid = poiid;
	}
	
	public Long getPoiId() {
		return poiid;
	}
	
}
