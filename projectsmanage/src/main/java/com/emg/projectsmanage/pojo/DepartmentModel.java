package com.emg.projectsmanage.pojo;

import java.io.Serializable;

public class DepartmentModel implements Serializable {

	private static final long serialVersionUID = -3443579375061715189L;
	private Integer id;
	private String name;
	private Integer sort;
	private Integer level;
	private Integer parentDepId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getParentDepId() {
		return parentDepId;
	}

	public void setParentDepId(Integer parentDepId) {
		this.parentDepId = parentDepId;
	}
}
