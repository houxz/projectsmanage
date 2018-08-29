package com.emg.projectsmanage.common;

/**
 * 角色类型
 * 
 * @author zsen
 * 
 */
public enum RoleType {
	/**
	 * 0, "未知角色"
	 */
	UNKNOW(0, "未知角色"),
	/**
	 * 1, "系统管理员"
	 */
	ROLE_ADMIN(1, "系统管理员"),
	/**
	 * -1, "超级管理员"
	 */
	ROLE_SUPERADMIN(-1, "超级管理员"),
	/**
	 * 7, "流程管理人员"
	 */
	ROLE_PROCESSMANAGER(7, "流程管理人员"),
	/**
	 * 2, "管理人员"
	 */
	ROLE_POIVIDEOEDIT(2, "管理人员"),
	/**
	 * 5, "编辑人员"
	 */
	ROLE_WORKER(5, "编辑人员"),
	/**
	 * 6, "校验人员"
	 */
	ROLE_CHECKER(6, "校验人员");

	private Integer value;
	private String des;

	public static RoleType valueOf(Integer value) {
		RoleType ret = RoleType.UNKNOW;
		for(RoleType roleType : RoleType.values()) {
			if(roleType.getValue().equals(value)) {
				ret = roleType;
				break;
			}
		}
		return ret;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private RoleType(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
