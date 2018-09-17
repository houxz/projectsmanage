package com.emg.projectsmanage.pojo;

public class TaskLinkErrorModel {
	private Long id;
	
	private Long taskid;
	
	private Long errorid;

	private Integer layerid;
	
	private String updatetime;
	
	private Integer state;
	
	private Integer type;
	
	private Long errortype;
	
	private Integer pstate;
	
	private Long groupid;
	
	private Long featureid;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskid() {
		return taskid;
	}

	public void setTaskid(Long taskid) {
		this.taskid = taskid;
	}

	public Long getErrorid() {
		return errorid;
	}

	public void setErrorid(Long errorid) {
		this.errorid = errorid;
	}

	public Integer getLayerid() {
		return layerid;
	}

	public void setLayerid(Integer layerid) {
		this.layerid = layerid;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getErrortype() {
		return errortype;
	}

	public void setErrortype(Long errortype) {
		this.errortype = errortype;
	}

	public Integer getPstate() {
		return pstate;
	}

	public void setPstate(Integer pstate) {
		this.pstate = pstate;
	}

	public Long getGroupid() {
		return groupid;
	}

	public void setGroupid(Long groupid) {
		this.groupid = groupid;
	}

	public Long getFeatureid() {
		return featureid;
	}

	public void setFeatureid(Long featureid) {
		this.featureid = featureid;
	}

}