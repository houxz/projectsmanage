package com.emg.projectsmanage.pojo;

public class ProjectsTaskCountModel {
	private String id;
	private Integer userid;
	private String username;
	private Integer roleid;
	private String rolename;
	private Integer systemid;
	private String projectid;
	private String projectname;
	private String processid;
	private Integer totaltask;
	private Integer idletask;
	private Integer edittask;
	private Integer qctask;
	private Integer checktask;
	private Integer completetask;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getProjectid() {
		return projectid;
	}
	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}
	public Integer getTotaltask() {
		return totaltask;
	}
	public void setTotaltask(Integer totaltask) {
		this.totaltask = totaltask;
	}
	public Integer getEdittask() {
		return edittask;
	}
	public void setEdittask(Integer edittask) {
		this.edittask = edittask;
	}
	public Integer getQctask() {
		return qctask;
	}
	public void setQctask(Integer qctask) {
		this.qctask = qctask;
	}
	public Integer getChecktask() {
		return checktask;
	}
	public void setChecktask(Integer checktask) {
		this.checktask = checktask;
	}
	public Integer getCompletetask() {
		return completetask;
	}
	public void setCompletetask(Integer completetask) {
		this.completetask = completetask;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
	public Integer getSystemid() {
		return systemid;
	}
	public void setSystemid(Integer systemid) {
		this.systemid = systemid;
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
	public Integer getIdletask() {
		return idletask;
	}
	public void setIdletask(Integer idletask) {
		this.idletask = idletask;
	}
	public String getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid = processid;
	}
}
