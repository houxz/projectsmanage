package com.emg.poiwebeditor.pojo;

import java.io.Serializable;

public class AuthorityModel implements Serializable {

	private static final long serialVersionUID = 8149311454008988914L;

	private Integer id;
	private String realname;
	private String username;
	private String password;
	private Integer department;
	private Integer enabled;
	private Integer isshow;
	private String roletype;
	private String roleremark;
	private String rolename;

	public String getRoletype() {
		return roletype;
	}

	public void setRoletype(String roletype) {
		this.roletype = roletype;
	}

	public String getRoleremark() {
		return roleremark;
	}

	public void setRoleremark(String roleremark) {
		this.roleremark = roleremark;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getDepartment() {
		return department;
	}

	public void setDepartment(Integer department) {
		this.department = department;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public Integer getIsshow() {
		return isshow;
	}

	public void setIsshow(Integer isshow) {
		this.isshow = isshow;
	}

}
