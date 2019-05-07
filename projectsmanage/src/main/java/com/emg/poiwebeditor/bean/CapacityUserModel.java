package com.emg.poiwebeditor.bean;

public class CapacityUserModel {
	private int id = -1;
	private String userName = "";
	private String realName = "";
	private String identity = "";
	private int count = 0;
	private int poiCount = 0;
	private int geoCount = 0;
	private CapacityErrorTypeModel missErrorTypeModel = new CapacityErrorTypeModel();
	private CapacityErrorTypeModel errorCodeTypeModel = new CapacityErrorTypeModel();
	private CapacityErrorTypeModel errorRuleTypeModel = new CapacityErrorTypeModel();
	private CapacityErrorTypeModel errorMoreTypeModel = new CapacityErrorTypeModel();
	private CapacityErrorTypeModel missDoneTypeModel = new CapacityErrorTypeModel();
	private CapacityErrorTypeModel errorSecretTypeModel = new CapacityErrorTypeModel();
	private String poiType = "";
	private CapacityPOICountModel poiAddCountModel = new CapacityPOICountModel();
	private CapacityPOICountModel poiSureCountModel = new CapacityPOICountModel();
	private CapacityPOICountModel poiModifyCountModel = new CapacityPOICountModel();
	private CapacityPOICountModel poiDelCountModel = new CapacityPOICountModel();
	private CapacityErrorGeoCountModel capacityGeoMissAddCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityGeoMissSureCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityGeoMoreAddCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityGeoMoreSureCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityGeoMissDelCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityGeoMoreDelCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityGeoPosErrorCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityGeoPosGuideErrorCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityGeoDirectionErrorCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityPOIErrorACountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityPOIErrorBCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityPOIErrorCCountModel = new CapacityErrorGeoCountModel();
	private CapacityErrorGeoCountModel capacityPOIErrorCountModel = new CapacityErrorGeoCountModel();

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdentity() {
		return this.identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public CapacityErrorTypeModel getMissErrorTypeModel() {
		return this.missErrorTypeModel;
	}

	public void setMissErrorTypeModel(CapacityErrorTypeModel missErrorTypeModel) {
		this.missErrorTypeModel = missErrorTypeModel;
	}

	public CapacityErrorTypeModel getErrorCodeTypeModel() {
		return this.errorCodeTypeModel;
	}

	public void setErrorCodeTypeModel(CapacityErrorTypeModel errorCodeTypeModel) {
		this.errorCodeTypeModel = errorCodeTypeModel;
	}

	public CapacityErrorTypeModel getErrorRuleTypeModel() {
		return this.errorRuleTypeModel;
	}

	public void setErrorRuleTypeModel(CapacityErrorTypeModel errorRuleTypeModel) {
		this.errorRuleTypeModel = errorRuleTypeModel;
	}

	public CapacityErrorTypeModel getErrorMoreTypeModel() {
		return this.errorMoreTypeModel;
	}

	public void setErrorMoreTypeModel(CapacityErrorTypeModel errorMoreTypeModel) {
		this.errorMoreTypeModel = errorMoreTypeModel;
	}

	public CapacityErrorTypeModel getMissDoneTypeModel() {
		return this.missDoneTypeModel;
	}

	public void setMissDoneTypeModel(CapacityErrorTypeModel missDoneTypeModel) {
		this.missDoneTypeModel = missDoneTypeModel;
	}

	public CapacityErrorTypeModel getErrorSecretTypeModel() {
		return this.errorSecretTypeModel;
	}

	public void setErrorSecretTypeModel(
			CapacityErrorTypeModel errorSecretTypeModel) {
		this.errorSecretTypeModel = errorSecretTypeModel;
	}

	public int getPoiCount() {
		return this.poiCount;
	}

	public void setPoiCount(int poiCount) {
		this.poiCount += poiCount;
	}

	public int getGeoCount() {
		return this.geoCount;
	}

	public void setGeoCount(int geoCount) {
		this.geoCount += geoCount;
	}

	public CapacityPOICountModel getPoiAddCountModel() {
		return this.poiAddCountModel;
	}

	public void setPoiAddCountModel(CapacityPOICountModel poiAddCountModel) {
		this.poiAddCountModel = poiAddCountModel;
	}

	public CapacityPOICountModel getPoiSureCountModel() {
		return this.poiSureCountModel;
	}

	public void setPoiSureCountModel(CapacityPOICountModel poiSureCountModel) {
		this.poiSureCountModel = poiSureCountModel;
	}

	public CapacityPOICountModel getPoiModifyCountModel() {
		return this.poiModifyCountModel;
	}

	public void setPoiModifyCountModel(CapacityPOICountModel poiModifyCountModel) {
		this.poiModifyCountModel = poiModifyCountModel;
	}

	public CapacityPOICountModel getPoiDelCountModel() {
		return this.poiDelCountModel;
	}

	public void setPoiDelCountModel(CapacityPOICountModel poiDelCountModel) {
		this.poiDelCountModel = poiDelCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoMissAddCountModel() {
		return this.capacityGeoMissAddCountModel;
	}

	public void setCapacityGeoMissAddCountModel(
			CapacityErrorGeoCountModel capacityGeoMissAddCountModel) {
		this.capacityGeoMissAddCountModel = capacityGeoMissAddCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoMissSureCountModel() {
		return this.capacityGeoMissSureCountModel;
	}

	public void setCapacityGeoMissSureCountModel(
			CapacityErrorGeoCountModel capacityGeoMissSureCountModel) {
		this.capacityGeoMissSureCountModel = capacityGeoMissSureCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoMoreAddCountModel() {
		return this.capacityGeoMoreAddCountModel;
	}

	public void setCapacityGeoMoreAddCountModel(
			CapacityErrorGeoCountModel capacityGeoMoreAddCountModel) {
		this.capacityGeoMoreAddCountModel = capacityGeoMoreAddCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoMoreSureCountModel() {
		return this.capacityGeoMoreSureCountModel;
	}

	public void setCapacityGeoMoreSureCountModel(
			CapacityErrorGeoCountModel capacityGeoMoreSureCountModel) {
		this.capacityGeoMoreSureCountModel = capacityGeoMoreSureCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoMissDelCountModel() {
		return this.capacityGeoMissDelCountModel;
	}

	public void setCapacityGeoMissDelCountModel(
			CapacityErrorGeoCountModel capacityGeoMissDelCountModel) {
		this.capacityGeoMissDelCountModel = capacityGeoMissDelCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoMoreDelCountModel() {
		return this.capacityGeoMoreDelCountModel;
	}

	public void setCapacityGeoMoreDelCountModel(
			CapacityErrorGeoCountModel capacityGeoMoreDelCountModel) {
		this.capacityGeoMoreDelCountModel = capacityGeoMoreDelCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoPosErrorCountModel() {
		return this.capacityGeoPosErrorCountModel;
	}

	public void setCapacityGeoPosErrorCountModel(
			CapacityErrorGeoCountModel capacityGeoPosErrorCountModel) {
		this.capacityGeoPosErrorCountModel = capacityGeoPosErrorCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoPosGuideErrorCountModel() {
		return this.capacityGeoPosGuideErrorCountModel;
	}

	public void setCapacityGeoPosGuideErrorCountModel(
			CapacityErrorGeoCountModel capacityGeoPosGuideErrorCountModel) {
		this.capacityGeoPosGuideErrorCountModel = capacityGeoPosGuideErrorCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityGeoDirectionErrorCountModel() {
		return this.capacityGeoDirectionErrorCountModel;
	}

	public void setCapacityGeoDirectionErrorCountModel(
			CapacityErrorGeoCountModel capacityGeoDirectionErrorCountModel) {
		this.capacityGeoDirectionErrorCountModel = capacityGeoDirectionErrorCountModel;
	}

	public int getAllTypeCount() {
		return this.missErrorTypeModel.getCount()
				+ this.errorCodeTypeModel.getCount()
				+ this.errorRuleTypeModel.getCount()
				+ this.errorMoreTypeModel.getCount()
				+ this.missDoneTypeModel.getCount()
				+ this.errorSecretTypeModel.getCount();
	}

	public int getAllPoiCount() {
		return this.poiAddCountModel.getCount()
				+ this.poiSureCountModel.getCount()
				+ this.poiModifyCountModel.getCount()
				+ this.poiDelCountModel.getCount();
	}

	public int getAllGeoCount() {
		return this.capacityGeoMissAddCountModel.getCount()
				+ this.capacityGeoMissSureCountModel.getCount()
				+ this.capacityGeoMoreAddCountModel.getCount()
				+ this.capacityGeoMoreSureCountModel.getCount()
				+ this.capacityGeoMissDelCountModel.getCount()
				+ this.capacityGeoMoreDelCountModel.getCount()
				+ this.capacityGeoPosErrorCountModel.getCount()
				+ this.capacityGeoPosGuideErrorCountModel.getCount()
				+ this.capacityGeoDirectionErrorCountModel.getCount();
	}

	public CapacityErrorGeoCountModel getCapacityPOIErrorACountModel() {
		return this.capacityPOIErrorACountModel;
	}

	public void setCapacityPOIErrorACountModel(
			CapacityErrorGeoCountModel capacityPOIErrorACountModel) {
		this.capacityPOIErrorACountModel = capacityPOIErrorACountModel;
	}

	public CapacityErrorGeoCountModel getCapacityPOIErrorBCountModel() {
		return this.capacityPOIErrorBCountModel;
	}

	public void setCapacityPOIErrorBCountModel(
			CapacityErrorGeoCountModel capacityPOIErrorBCountModel) {
		this.capacityPOIErrorBCountModel = capacityPOIErrorBCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityPOIErrorCCountModel() {
		return this.capacityPOIErrorCCountModel;
	}

	public void setCapacityPOIErrorCCountModel(
			CapacityErrorGeoCountModel capacityPOIErrorCCountModel) {
		this.capacityPOIErrorCCountModel = capacityPOIErrorCCountModel;
	}

	public CapacityErrorGeoCountModel getCapacityPOIErrorCountModel() {
		return this.capacityPOIErrorCountModel;
	}

	public void setCapacityPOIErrorCountModel(CapacityErrorGeoCountModel capacityPOIErrorCountModel) {
		this.capacityPOIErrorCountModel = capacityPOIErrorCountModel;
	}

	public String getPoiType() {
		return this.poiType;
	}

	public void setPoiType(String poiType) {
		this.poiType = poiType;
	}
}
