package com.emg.poiwebeditor.pojo;

import java.util.List;

import com.emg.poiwebeditor.common.ComputeModeEnum;
import com.emg.poiwebeditor.common.RoleEnum;

public class ChangePOIVO {
	private long uId = 0L;

	private long manualCheckUId = 0L;

	private long confirmUId = 0L;

	private long changeSetId = 0L;
	
	private RoleEnum role = RoleEnum.none;

	private ComputeModeEnum computeModeEnum = ComputeModeEnum.none;

	private int featureType = 0;

	private long taskId = 0L;

	private long importId = 0L;

	private List<POIDo> poiCreate;
	
	private List<POIDo> poiModify;
	
	private List<POIDo> poiDel;

	public long getuId() {
		return uId;
	}

	public void setuId(long uId) {
		this.uId = uId;
	}

	public long getManualCheckUId() {
		return manualCheckUId;
	}

	public void setManualCheckUId(long manualCheckUId) {
		this.manualCheckUId = manualCheckUId;
	}

	public long getConfirmUId() {
		return confirmUId;
	}

	public void setConfirmUId(long confirmUId) {
		this.confirmUId = confirmUId;
	}

	public long getChangeSetId() {
		return changeSetId;
	}

	public void setChangeSetId(long changeSetId) {
		this.changeSetId = changeSetId;
	}

	public RoleEnum getRole() {
		return role;
	}

	public void setRole(RoleEnum role) {
		this.role = role;
	}

	public ComputeModeEnum getComputeModeEnum() {
		return computeModeEnum;
	}

	public void setComputeModeEnum(ComputeModeEnum computeModeEnum) {
		this.computeModeEnum = computeModeEnum;
	}

	public int getFeatureType() {
		return featureType;
	}

	public void setFeatureType(int featureType) {
		this.featureType = featureType;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public long getImportId() {
		return importId;
	}

	public void setImportId(long importId) {
		this.importId = importId;
	}

	public List<POIDo> getPoiCreate() {
		return poiCreate;
	}

	public void setPoiCreate(List<POIDo> poiCreate) {
		this.poiCreate = poiCreate;
	}

	public List<POIDo> getPoiModify() {
		return poiModify;
	}

	public void setPoiModify(List<POIDo> poiModify) {
		this.poiModify = poiModify;
	}

	public List<POIDo> getPoiDel() {
		return poiDel;
	}

	public void setPoiDel(List<POIDo> poiDel) {
		this.poiDel = poiDel;
	}

}
