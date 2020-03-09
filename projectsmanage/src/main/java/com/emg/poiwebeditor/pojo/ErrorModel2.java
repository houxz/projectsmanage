package com.emg.poiwebeditor.pojo;

import java.util.Date;

public class ErrorModel2 {
	private Long id;
    private Long featureid;
  
    private String errorremark;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFeatureid() {
		return featureid;
	}
	public void setFeatureid(Long featureid) {
		this.featureid = featureid;
	}
	
	public String getErrorremark() {
		return errorremark;
	}
	public void setErrorremark(String errorremark) {
		this.errorremark = errorremark;
	}

  
}
