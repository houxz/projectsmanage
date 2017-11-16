package com.emg.projectsmanage.pojo;

public class UserRoleModel {
	private Integer id;
	private Integer userid;
	private String username;
	private Integer roleid;
	private String rolename;
	private String roleremark;

	private Integer skilllevel;
	private String skilldes;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getSkilllevel() {
		return skilllevel;
	}

	public void setSkilllevel(Integer skilllevel) {
		this.skilllevel = skilllevel;
	}

	public String getSkilldes() {
		return skilldes;
	}

	public void setSkilldes(String skilldes) {
		this.skilldes = skilldes;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getRoleid() {
		return roleid;
	}

	public void setRoleid(Integer roleid) {
		this.roleid = roleid;
	}

	public String getRoleremark() {
		return roleremark;
	}

	public void setRoleremark(String roleremark) {
		this.roleremark = roleremark;
	}

}
