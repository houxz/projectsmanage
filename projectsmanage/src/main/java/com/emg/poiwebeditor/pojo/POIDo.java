package com.emg.poiwebeditor.pojo;

import com.emg.poiwebeditor.common.CheckEnum;
import com.emg.poiwebeditor.common.ConfirmEnum;

public class POIDo extends AttachDO {
	private String namec;
	private Integer owner = 0;
	private String sortcode;
	private String grade;
	private Long projectid = 0L;

	private CheckEnum autoCheck = null;
	private CheckEnum manualCheck = null;
	private ConfirmEnum confirm = null;
	
	private Integer newFeatCode;
	private String newSortCode;
	
	public String getNamec() {
		return namec;
	}
	public void setNamec(String namec) {
		this.namec = namec;
	}
	public Integer getOwner() {
		return owner;
	}
	public void setOwner(Integer owner) {
		this.owner = owner;
	}
	public String getSortcode() {
		return sortcode;
	}
	public void setSortcode(String sortcode) {
		this.sortcode = sortcode;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public Long getProjectid() {
		return projectid;
	}
	public void setProjectid(Long projectid) {
		this.projectid = projectid;
	}
	public CheckEnum getAutoCheck() {
		return autoCheck;
	}
	public void setAutoCheck(CheckEnum autoCheck) {
		this.autoCheck = autoCheck;
	}
	public CheckEnum getManualCheck() {
		return manualCheck;
	}
	public void setManualCheck(CheckEnum manualCheck) {
		this.manualCheck = manualCheck;
	}
	public ConfirmEnum getConfirm() {
		return confirm;
	}
	public void setConfirm(ConfirmEnum confirm) {
		this.confirm = confirm;
	}
	public Integer getNewFeatCode() {
		return newFeatCode;
	}
	public void setNewFeatCode(Integer newFeatCode) {
		this.newFeatCode = newFeatCode;
	}
	public String getNewSortCode() {
		return newSortCode;
	}
	public void setNewSortCode(String newSortCode) {
		this.newSortCode = newSortCode;
	}
	
	
}
