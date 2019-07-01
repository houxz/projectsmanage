package com.emg.poiwebeditor.pojo;

public class keywordModelForTask {
	private Long id;
	Integer emapcount;
	Integer bdcount;
	Integer txcount;
	Integer gdcount;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getEmapcount() {
		return emapcount;
	}
	
	public void setEmapcount(Integer count) {
		this.emapcount = count;
	}
	
	public Integer getBdcount() {
		return bdcount;
	}
	
	public void   setBdcount(Integer count) {
		this.bdcount = count;
	}
	
	public Integer getTxcount() {
		return txcount;
	}
	
	public void  setTxcount(Integer count) {
		this.txcount = count;
	}
	
	public Integer getGdcount() {
		return gdcount;
	}
	
	public void   setGdcount(Integer count) {
		this.gdcount = count;
	}
}
