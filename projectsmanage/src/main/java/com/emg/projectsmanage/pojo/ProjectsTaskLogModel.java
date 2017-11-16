package com.emg.projectsmanage.pojo;

public class ProjectsTaskLogModel {
    private String id;

    private Integer systemid;

    private String projectid;

    private String taskid;

    private Integer userid;
    
    private Integer roleid;

    private Integer statebefore;
    
    private Integer processbefore;
    
    private Integer stateafter;
    
    private Integer processafter;

    private String createtime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSystemid() {
		return systemid;
	}

	public void setSystemid(Integer systemid) {
		this.systemid = systemid;
	}

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public Integer getStatebefore() {
		return statebefore;
	}

	public void setStatebefore(Integer statebefore) {
		this.statebefore = statebefore;
	}

	public Integer getProcessbefore() {
		return processbefore;
	}

	public void setProcessbefore(Integer processbefore) {
		this.processbefore = processbefore;
	}

	public Integer getStateafter() {
		return stateafter;
	}

	public void setStateafter(Integer stateafter) {
		this.stateafter = stateafter;
	}

	public Integer getProcessafter() {
		return processafter;
	}

	public void setProcessafter(Integer processafter) {
		this.processafter = processafter;
	}

	public Integer getRoleid() {
		return roleid;
	}

	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}

}