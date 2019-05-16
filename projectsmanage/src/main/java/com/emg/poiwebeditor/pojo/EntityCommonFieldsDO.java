package com.emg.poiwebeditor.pojo;

import com.emg.poiwebeditor.common.ChangeTypeEnum;
import com.emg.poiwebeditor.common.CheckEnum;
import com.emg.poiwebeditor.common.ConfirmEnum;
import com.emg.poiwebeditor.common.ErrorStatusEnum;
import com.emg.poiwebeditor.common.ManualCheckErrorEnum;
import com.emg.poiwebeditor.common.OpTypeEnum;

public class EntityCommonFieldsDO extends EntityCommonUserDO {

	private String geo;

	private Boolean isDel = false;
	private Long bgUId = 0L;

	private OpTypeEnum opTypeEnum;
	private String ver;
	private CheckEnum autoCheck = CheckEnum.uncheck;
	private String autoCheckTimeStamp;
	private ConfirmEnum confirm = ConfirmEnum.no_confirm;

	private String confirmTimeStamp;
	private CheckEnum manualCheck = CheckEnum.uncheck;

	private String manualCheckTimeStamp;
	private String updateTime;

	private ChangeTypeEnum changeType = ChangeTypeEnum.no_change;
	private Long changeSetId = 0L;
	private Long editVer = 0L;

	private String errorRemark;
	private String modifyRemark;
	private String modify;
	private String modifyTime;

	private ErrorStatusEnum errorStatus = ErrorStatusEnum.none;
	private ErrorStatusEnum errorStatusForBg;

	private ManualCheckErrorEnum manualCheckError;

	private String wayHistoryJson;
	private Long featcode = 0L;

	private Boolean isSplit = false;
	private Long splitedParentId = -1L;

	private Long taskId;

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public Boolean isDel() {
		return isDel;
	}

	public void setDel(Boolean isDel) {
		this.isDel = isDel;
	}

	public Long getBgUId() {
		return bgUId;
	}

	public void setBgUId(Long bgUId) {
		this.bgUId = bgUId;
	}

	public OpTypeEnum getOpTypeEnum() {
		return opTypeEnum;
	}

	public void setOpTypeEnum(OpTypeEnum opTypeEnum) {
		this.opTypeEnum = opTypeEnum;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public CheckEnum getAutoCheck() {
		return autoCheck;
	}

	public void setAutoCheck(CheckEnum autoCheck) {
		this.autoCheck = autoCheck;
	}

	public String getAutoCheckTimeStamp() {
		return autoCheckTimeStamp;
	}

	public void setAutoCheckTimeStamp(String autoCheckTimeStamp) {
		this.autoCheckTimeStamp = autoCheckTimeStamp;
	}

	public ConfirmEnum getConfirm() {
		return confirm;
	}

	public void setConfirm(ConfirmEnum confirm) {
		this.confirm = confirm;
	}

	public String getConfirmTimeStamp() {
		return confirmTimeStamp;
	}

	public void setConfirmTimeStamp(String confirmTimeStamp) {
		this.confirmTimeStamp = confirmTimeStamp;
	}

	public CheckEnum getManualCheck() {
		return manualCheck;
	}

	public void setManualCheck(CheckEnum manualCheck) {
		this.manualCheck = manualCheck;
	}

	public String getManualCheckTimeStamp() {
		return manualCheckTimeStamp;
	}

	public void setManualCheckTimeStamp(String manualCheckTimeStamp) {
		this.manualCheckTimeStamp = manualCheckTimeStamp;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public ChangeTypeEnum getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeTypeEnum changeType) {
		this.changeType = changeType;
	}

	public Long getChangeSetId() {
		return changeSetId;
	}

	public void setChangeSetId(Long changeSetId) {
		this.changeSetId = changeSetId;
	}

	public Long getEditVer() {
		return editVer;
	}

	public void setEditVer(Long editVer) {
		this.editVer = editVer;
	}

	public String getErrorRemark() {
		return errorRemark;
	}

	public void setErrorRemark(String errorRemark) {
		this.errorRemark = errorRemark;
	}

	public String getModifyRemark() {
		return modifyRemark;
	}

	public void setModifyRemark(String modifyRemark) {
		this.modifyRemark = modifyRemark;
	}

	public String getModify() {
		return modify;
	}

	public void setModify(String modify) {
		this.modify = modify;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public ErrorStatusEnum getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(ErrorStatusEnum errorStatus) {
		this.errorStatus = errorStatus;
	}

	public ErrorStatusEnum getErrorStatusForBg() {
		return errorStatusForBg;
	}

	public void setErrorStatusForBg(ErrorStatusEnum errorStatusForBg) {
		this.errorStatusForBg = errorStatusForBg;
	}

	public ManualCheckErrorEnum getManualCheckError() {
		return manualCheckError;
	}

	public void setManualCheckError(ManualCheckErrorEnum manualCheckError) {
		this.manualCheckError = manualCheckError;
	}

	public String getWayHistoryJson() {
		return wayHistoryJson;
	}

	public void setWayHistoryJson(String wayHistoryJson) {
		this.wayHistoryJson = wayHistoryJson;
	}

	public Long getFeatcode() {
		return featcode;
	}

	public void setFeatcode(Long featcode) {
		this.featcode = featcode;
	}

	public Boolean isSplit() {
		return isSplit;
	}

	public void setSplit(Boolean isSplit) {
		this.isSplit = isSplit;
	}

	public Long getSplitedParentId() {
		return splitedParentId;
	}

	public void setSplitedParentId(Long splitedParentId) {
		this.splitedParentId = splitedParentId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

}
