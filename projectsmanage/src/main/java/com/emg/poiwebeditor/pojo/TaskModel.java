package com.emg.poiwebeditor.pojo;

import java.io.Serializable;

public class TaskModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String name;
	
	private Integer tasktype;

	private Integer state;

	private Integer process;
	
	private String statedes;
	
	private Integer editid;
	
	private Integer checkid;
	
	private Long projectid;

	private Integer priority;
	
	private Integer rank;

	private String operatetime;
	
	private Float lastexitposition;

	private String starttime;
	
	private String endtime;
	
	private Long keywordid;

	private Long processid;
	
	private String processname;
	
	private String editname;
	
	private String checkname;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getProcess() {
		return process;
	}

	public void setProcess(Integer process) {
		this.process = process;
	}

	public String getStatedes() {
		return statedes;
	}

	public void setStatedes(String statedes) {
		this.statedes = statedes;
	}

	public Integer getEditid() {
		return editid;
	}

	public void setEditid(Integer editid) {
		this.editid = editid;
	}

	public String getEditname() {
		return editname;
	}

	public void setEditname(String editname) {
		this.editname = editname;
	}

	public Integer getCheckid() {
		return checkid;
	}

	public void setCheckid(Integer checkid) {
		this.checkid = checkid;
	}

	public String getCheckname() {
		return checkname;
	}

	public void setCheckname(String checkname) {
		this.checkname = checkname;
	}

	public Long getProjectid() {
		return projectid;
	}

	public void setProjectid(Long projectid) {
		this.projectid = projectid;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getOperatetime() {
		return operatetime;
	}

	public void setOperatetime(String operatetime) {
		this.operatetime = operatetime;
	}

	public Float getLastexitposition() {
		return lastexitposition;
	}

	public void setLastexitposition(Float lastexitposition) {
		this.lastexitposition = lastexitposition;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
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

	public Long getKeywordid() {
		return keywordid;
	}

	public void setKeywordid(Long keywordid) {
		this.keywordid = keywordid;
	}

	public Integer getTasktype() {
		return tasktype;
	}

	public void setTasktype(Integer tasktype) {
		this.tasktype = tasktype;
	}
	

}