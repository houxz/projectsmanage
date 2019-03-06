package com.emg.projectsmanage.pojo;

public class ErrorlistModel {
	private Long batchid;
	
	private String qid;
	
	private Long errortype;
	
	private String errorremark;
	
	private String updatetime;
	
	private Long countnum;

	public Long getBatchid() {
		return batchid;
	}

	public void setBatchid(Long batchid) {
		this.batchid = batchid;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public Long getErrortype() {
		return errortype;
	}

	public void setErrortype(Long errortype) {
		this.errortype = errortype;
	}

	public String getErrorremark() {
		return errorremark;
	}

	public void setErrorremark(String errorremark) {
		this.errorremark = errorremark;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public Long getCountnum() {
		return countnum;
	}

	public void setCountnum(Long countnum) {
		this.countnum = countnum;
	}
	
	
}
