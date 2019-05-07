package com.emg.poiwebeditor.pojo;

import java.util.Date;

public class ProjectsUserModel {
	
	 private String id;
	 private String pid;
	 private Integer userid;
	 private String username;
	 private Integer roleid;
	 private String rolename;
	 private Date optime;
	 private Integer opuid;
	 private Integer systemid;
	 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
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
		this.username = username;
	}
	public Integer getRoleid() {
		return roleid;
	}
	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public Date getOptime() {
		return optime;
	}
	public void setOptime(Date optime) {
		this.optime = optime;
	}
	public Integer getOpuid() {
		return opuid;
	}
	public void setOpuid(Integer opuid) {
		this.opuid = opuid;
	}
	public Integer getSystemid() {
		return systemid;
	}
	public void setSystemid(Integer systemid) {
		this.systemid = systemid;
	}
	
}
