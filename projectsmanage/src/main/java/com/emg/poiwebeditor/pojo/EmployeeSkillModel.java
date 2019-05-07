package com.emg.poiwebeditor.pojo;

import java.util.Date;

public class EmployeeSkillModel {

	private String id;
	private Integer systemid;
	private Integer skillmodule;
	private Integer skilllevel;
	private Integer userid;
	private String username;
	private Integer roleid;
	private String rolename;
	private Integer opuid;
	private Date optime;

	private String skillModuleDesc;
	private String skillLevelDesc;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getSkillmodule() {
		return skillmodule;
	}

	public void setSkillmodule(Integer skillmodule) {
		this.skillmodule = skillmodule;
	}

	public Integer getSkilllevel() {
		return skilllevel;
	}

	public void setSkilllevel(Integer skilllevel) {
		this.skilllevel = skilllevel;
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

	public Integer getOpuid() {
		return opuid;
	}

	public void setOpuid(Integer opuid) {
		this.opuid = opuid;
	}

	public Date getOptime() {
		return optime;
	}

	public void setOptime(Date optime) {
		this.optime = optime;
	}

	public Integer getSystemid() {
		return systemid;
	}

	public void setSystemid(Integer systemid) {
		this.systemid = systemid;
	}

	public String getSkillModuleDesc() {
		return skillModuleDesc;
	}

	public void setSkillModuleDesc(String skillModuleDesc) {
		this.skillModuleDesc = skillModuleDesc;
	}

	public String getSkillLevelDesc() {
		return skillLevelDesc;
	}

	public void setSkillLevelDesc(String skillLevelDesc) {
		this.skillLevelDesc = skillLevelDesc;
	}

}
