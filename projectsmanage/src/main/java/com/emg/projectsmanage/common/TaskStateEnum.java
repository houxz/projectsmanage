package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum TaskStateEnum {
	WEIZHIZUO(1,2,null,null, "未制作");
	
	private TaskStateEnum(Integer state, Integer process, Integer tasktype, Integer checkid, String des) {
		this.state = state;
		this.process = process;
		this.tasktype = tasktype;
		this.checkid = checkid;
		this.des = des;
	}
	
	private Integer state;
	private Integer process;
	private Integer tasktype;
	private Integer checkid;
	private String des;

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
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}

}
