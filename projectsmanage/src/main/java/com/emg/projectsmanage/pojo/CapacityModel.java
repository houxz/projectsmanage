package com.emg.projectsmanage.pojo;

import java.util.Date;

public class CapacityModel {
	private Long id;

	private Integer tasktype;

	private Long processid;

	private String processname;

	private Long projectid;

	private Integer userid;

	private String username;

	private Integer roleid;

	private String time;

	private Integer iswork;

	private Long errorcount = 0L;

	private Long taskcount = 0L;

	private Long modifypoi = 0L;

	private Long createpoi = 0L;

	private Long deletepoi = 0L;

	private Long existdeletepoi = 0L;

	private Long confirmpoi = 0L;

	private Long visualerrorcount = 0L;

	private Long fielddatacount = 0L;

	private Date createtime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTasktype() {
		return tasktype;
	}

	public void setTasktype(Integer tasktype) {
		this.tasktype = tasktype;
	}

	public Long getProcessid() {
		return processid;
	}

	public void setProcessid(Long processid) {
		this.processid = processid;
	}

	public String getProcessname() {
		return processname;
	}

	public void setProcessname(String processname) {
		this.processname = processname == null ? null : processname.trim();
	}

	public Long getProjectid() {
		return projectid;
	}

	public void setProjectid(Long projectid) {
		this.projectid = projectid;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username == null ? null : username.trim();
	}

	public Integer getRoleid() {
		return roleid;
	}

	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time == null ? null : time.trim();
	}

	public Integer getIswork() {
		return iswork;
	}

	public void setIswork(Integer iswork) {
		this.iswork = iswork;
	}

	public Long getErrorcount() {
		return errorcount;
	}

	public void setErrorcount(Long errorcount) {
		this.errorcount = errorcount;
	}

	public Long getTaskcount() {
		return taskcount;
	}

	public void setTaskcount(Long taskcount) {
		this.taskcount = taskcount;
	}

	public Long getModifypoi() {
		return modifypoi;
	}

	public void setModifypoi(Long modifypoi) {
		this.modifypoi = modifypoi;
	}

	public Long getCreatepoi() {
		return createpoi;
	}

	public void setCreatepoi(Long createpoi) {
		this.createpoi = createpoi;
	}

	// modified by lianhr begin 2018/12/18
	public Long getDeletepoi() {
		return deletepoi;
	}

	public void setDeletepoi(Long deletepoi) {
		this.deletepoi = deletepoi;
	}

	public Long getExistdeletepoi() {
		return existdeletepoi;
	}

	public void setExistdeletepoi(Long existdeletepoi) {
		this.existdeletepoi = existdeletepoi;
	}
	// modified by lianhr end

	public Long getConfirmpoi() {
		return confirmpoi;
	}

	public void setConfirmpoi(Long confirmpoi) {
		this.confirmpoi = confirmpoi;
	}

	public Long getVisualerrorcount() {
		return visualerrorcount;
	}

	public void setVisualerrorcount(Long visualerrorcount) {
		this.visualerrorcount = visualerrorcount;
	}

	public Long getFielddatacount() {
		return fielddatacount;
	}

	public void setFielddatacount(Long fielddatacount) {
		this.fielddatacount = fielddatacount;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
}