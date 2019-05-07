package com.emg.poiwebeditor.pojo;

import java.util.List;

public class ProjectModel {
	private Long id;

	private Long processid;

	private Integer protype;

	private Integer pdifficulty;

	private Integer priority;

	private Integer tasknum;

	private Integer systemid;

	private String description;

	private Integer createby;

	private String createtime;

	private String area;

	private String name;

	private Integer owner;

	private String overprogress;

	private Integer overstate;

	private Double poitotckdisl;

	private Double poitotckdisr;

	private String workers;

	private String checkers;

	private List<ProjectsUserModel> checkusers;

	private List<ProjectsUserModel> workusers;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProcessid() {
		return processid;
	}

	public void setProcessid(Long processid) {
		this.processid = processid;
	}

	public Integer getProtype() {
		return protype;
	}

	public void setProtype(Integer protype) {
		this.protype = protype;
	}

	public Integer getPdifficulty() {
		return pdifficulty;
	}

	public void setPdifficulty(Integer pdifficulty) {
		this.pdifficulty = pdifficulty;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getTasknum() {
		return tasknum;
	}

	public void setTasknum(Integer tasknum) {
		this.tasknum = tasknum;
	}

	public Integer getSystemid() {
		return systemid;
	}

	public void setSystemid(Integer systemid) {
		this.systemid = systemid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public Integer getCreateby() {
		return createby;
	}

	public void setCreateby(Integer createby) {
		this.createby = createby;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area == null ? null : area.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public String getOverprogress() {
		return overprogress;
	}

	public void setOverprogress(String overprogress) {
		this.overprogress = overprogress == null ? null : overprogress.trim();
	}

	public Integer getOverstate() {
		return overstate;
	}

	public void setOverstate(Integer overstate) {
		this.overstate = overstate;
	}

	public Double getPoitotckdisl() {
		return poitotckdisl;
	}

	public void setPoitotckdisl(Double poitotckdisl) {
		this.poitotckdisl = poitotckdisl;
	}

	public Double getPoitotckdisr() {
		return poitotckdisr;
	}

	public void setPoitotckdisr(Double poitotckdisr) {
		this.poitotckdisr = poitotckdisr;
	}

	public String getWorkers() {
		return workers;
	}

	public void setWorkers(String workers) {
		this.workers = workers;
	}

	public String getCheckers() {
		return checkers;
	}

	public void setCheckers(String checkers) {
		this.checkers = checkers;
	}

	public List<ProjectsUserModel> getCheckusers() {
		return checkusers;
	}

	public void setCheckusers(List<ProjectsUserModel> checkusers) {
		this.checkusers = checkusers;
	}

	public List<ProjectsUserModel> getWorkusers() {
		return workusers;
	}

	public void setWorkusers(List<ProjectsUserModel> workusers) {
		this.workusers = workusers;
	}
	
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
    public boolean equals(Object obj) {
		ProjectModel pro = (ProjectModel) obj;
        return id.equals(pro.getId());
    }
}