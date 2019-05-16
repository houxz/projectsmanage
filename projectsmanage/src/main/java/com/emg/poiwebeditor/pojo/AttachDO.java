package com.emg.poiwebeditor.pojo;

public class AttachDO extends EntityTagsDO {
	private Long junctionId;
	private String junctionVer;
	private Long froadId;
	private String froadVer;
	
	public Long getJunctionId() {
		return junctionId;
	}
	public void setJunctionId(Long junctionId) {
		this.junctionId = junctionId;
	}
	public String getJunctionVer() {
		return junctionVer;
	}
	public void setJunctionVer(String junctionVer) {
		this.junctionVer = junctionVer;
	}
	public Long getFroadId() {
		return froadId;
	}
	public void setFroadId(Long froadId) {
		this.froadId = froadId;
	}
	public String getFroadVer() {
		return froadVer;
	}
	public void setFroadVer(String froadVer) {
		this.froadVer = froadVer;
	}

}
