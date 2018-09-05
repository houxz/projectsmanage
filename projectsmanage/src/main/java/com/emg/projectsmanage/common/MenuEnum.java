package com.emg.projectsmanage.common;

public enum MenuEnum {
	/**
	 * 0, "/logout.web"
	 */
	logout(0, "/logout.web"),
	/**
	 * 990, "/systemsets.web"
	 */
	systemsets(990, "/systemsets.web"),
	/**
	 * 999, "/usersmanage.web"
	 */
	usersmanage(999, "/usersmanage.web"),
	/**
	 * 1000, "/skillsmanage.web"
	 */
	skillsmanage(1000, "/skillsmanage.web"),
	/**
	 * 1001, "/processesmanage.web"
	 */
	processesmanage(1001, "/processesmanage.web"),
	/**
	 * 1002, "/projectsmanage.web"
	 */
	projectsmanage(1002, "/projectsmanage.web"),
	/**
	 * 1003, "/worktasks.web"
	 */
	worktasks(1003, "/worktasks.web"),
	/**
	 * 1004, "/projectsprocess.web"
	 */
	projectsprocess(1004, "/projectsprocess.web"),
	/**
	 * 1005, "/capacitycount.web"
	 */
	capacitycount(1005, "/capacitycount.web"),
	/**
	 * 1006, "/itemsetmanage.web"
	 */
	itemsetmanage(1006, "/itemsetmanage.web"),
	/**
	 * 1007, "/errorsetmanage.web"
	 */
	errorsetmanage(1007, "/errorsetmanage.web"),
	/**
	 * 1008, "/errorsmanage.web"
	 */
	errorsmanage(1008, "/errorsmanage.web"),
	/**
	 * 1009, "/iteminfo.web"
	 */
	iteminfo(1009, "/iteminfo.web");

	private Integer code;
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private MenuEnum(Integer code, String url) {
		this.setCode(code);
		this.url = url;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	
}
