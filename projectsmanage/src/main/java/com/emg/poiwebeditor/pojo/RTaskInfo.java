package com.emg.poiwebeditor.pojo;

import java.util.List;

public class RTaskInfo {
	private int    ret=0;
	private	List<ErrorModel> ErrorList;
	private String log;
	
	
	
	public List<ErrorModel> getErrorList() {
		return ErrorList;
	}
	public void setErrorList(List<ErrorModel> errorList) {
		ErrorList = errorList;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public int getRet() {
		return ret;
	}
	public void setRet(int ret) {
		this.ret = ret;
	}
}
