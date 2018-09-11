package com.emg.projectsmanage.common;

public class StateMap{
	private Integer state;
	private Integer process;
	private Integer tasktype;
	private Integer checkid;
	
	public StateMap(Integer state, Integer process, Integer tasktype, Integer checkid){
		this.state = state;
		this.process = process;
		this.tasktype = tasktype;
		this.checkid = checkid;
	}
	
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getProcess() {
		return process;
	}

	public void setProcess(Integer process) {
		this.process = process;
	}

	public Integer getTasktype() {
		return tasktype;
	}

	public void setTasktype(Integer tasktype) {
		this.tasktype = tasktype;
	}

	public Integer getCheckid() {
		return checkid;
	}

	public void setCheckid(Integer checkid) {
		this.checkid = checkid;
	}
}
