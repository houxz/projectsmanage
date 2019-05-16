package com.emg.poiwebeditor.pojo;

public class EntityCommonUserDO extends EntityPrimaryKeyDO {

	private Long uId;
	private Long manualCheckUId;
	private Long confirmUId;
	private Long modifyUId;
	private String userName;
	private String confirmUserName;
	private String checkUserName;
	private String modifyUserName;
	public Long getuId() {
		return uId;
	}
	public void setuId(Long uId) {
		this.uId = uId;
	}
	public Long getManualCheckUId() {
		return manualCheckUId;
	}
	public void setManualCheckUId(Long manualCheckUId) {
		this.manualCheckUId = manualCheckUId;
	}
	public Long getConfirmUId() {
		return confirmUId;
	}
	public void setConfirmUId(Long confirmUId) {
		this.confirmUId = confirmUId;
	}
	public Long getModifyUId() {
		return modifyUId;
	}
	public void setModifyUId(Long modifyUId) {
		this.modifyUId = modifyUId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getConfirmUserName() {
		return confirmUserName;
	}
	public void setConfirmUserName(String confirmUserName) {
		this.confirmUserName = confirmUserName;
	}
	public String getCheckUserName() {
		return checkUserName;
	}
	public void setCheckUserName(String checkUserName) {
		this.checkUserName = checkUserName;
	}
	public String getModifyUserName() {
		return modifyUserName;
	}
	public void setModifyUserName(String modifyUserName) {
		this.modifyUserName = modifyUserName;
	}
	
	
}
