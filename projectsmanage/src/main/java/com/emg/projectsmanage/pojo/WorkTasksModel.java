package com.emg.projectsmanage.pojo;

public class WorkTasksModel {
	private String id;
	private Integer userid;
	private String username;
	private Integer roleid;
	private String rolename;
	private Long processid;
	private Integer processtype;
	private String processname;
	private Integer totaltask = 0;
	private Integer idletask = 0;
	private Integer edittask = 0;
	private Integer qctask = 0;
	private Integer checktask = 0;
	private Integer prepublishtask = 0;
	private Integer completetask = 0;
	private String time;
	
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
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
		this.processname = processname;
	}
	public Integer getProcesstype() {
		return processtype;
	}
	public void setProcesstype(Integer processtype) {
		this.processtype = processtype;
	}
	public Integer getPrepublishtask() {
		return prepublishtask;
	}
	public void setPrepublishtask(Integer prepublishtask) {
		this.prepublishtask = prepublishtask;
	}
}
