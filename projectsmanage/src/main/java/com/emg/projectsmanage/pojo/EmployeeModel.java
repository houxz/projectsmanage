package com.emg.projectsmanage.pojo;

import java.io.Serializable;

public class EmployeeModel implements Serializable {

	private static final long serialVersionUID = 8149311454008988914L;

	private Integer id;
	private String realname;
	private String username;
	private String password;
	private Integer department;
	private Integer enabled;
	private Integer isshow;
	private Integer sampling;

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

	public Integer getSampling() {
		return sampling;
	}

	public void setSampling(Integer sampling) {
		this.sampling = sampling;
	}

}
