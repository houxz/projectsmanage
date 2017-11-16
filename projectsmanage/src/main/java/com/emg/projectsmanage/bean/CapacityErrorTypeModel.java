package com.emg.projectsmanage.bean;

/**
 * 产量错误类型
 * 
 * @author AD
 * 
 */
public class CapacityErrorTypeModel {

	private Long id = -1L;// id
	private int category = -1;// 类别，1:属性，2：几何
	private String name = "";// 名称
	private int count = 0;// 数量
	private CapacityErrorFieldModel chineseNameFieldModel = new CapacityErrorFieldModel();// 中文正式名称
	private CapacityErrorFieldModel englishNameFieldModel = new CapacityErrorFieldModel();// 英文正式名称
	private CapacityErrorFieldModel objTypeFieldModel = new CapacityErrorFieldModel();// 对象类型代码
	private CapacityErrorFieldModel addTypeFieldModel = new CapacityErrorFieldModel();// 补充类别
	private CapacityErrorFieldModel seriesCodeFieldModel = new CapacityErrorFieldModel();// 系列代码
	private CapacityErrorFieldModel telFieldModel = new CapacityErrorFieldModel();// 电话号码
	private CapacityErrorFieldModel levelFieldModel = new CapacityErrorFieldModel();// 等级
	private CapacityErrorFieldModel addInfoFieldModel = new CapacityErrorFieldModel();// 附加信息
	private CapacityErrorFieldModel addrTownFieldModel = new CapacityErrorFieldModel();// 详细地址乡镇级
	private CapacityErrorFieldModel addrAreaFieldModel = new CapacityErrorFieldModel();// 详细地址村级、除行政区划外的其它区域
	private CapacityErrorFieldModel addrStreetFieldModel = new CapacityErrorFieldModel();// 详细地址大街路巷里
	private CapacityErrorFieldModel addrNumFieldModel = new CapacityErrorFieldModel();// 详细地址号组
	private CapacityErrorFieldModel addrOtherFieldModel = new CapacityErrorFieldModel();// 详细地址其它
	private CapacityErrorFieldModel remarkFieldModel = new CapacityErrorFieldModel();// 备注

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = this.count + count;
	}

	public CapacityErrorFieldModel getChineseNameFieldModel() {
		return chineseNameFieldModel;
	}

	public void setChineseNameFieldModel(
			CapacityErrorFieldModel chineseNameFieldModel) {
		this.chineseNameFieldModel = chineseNameFieldModel;
	}

	public CapacityErrorFieldModel getEnglishNameFieldModel() {
		return englishNameFieldModel;
	}

	public void setEnglishNameFieldModel(
			CapacityErrorFieldModel englishNameFieldModel) {
		this.englishNameFieldModel = englishNameFieldModel;
	}

	public CapacityErrorFieldModel getObjTypeFieldModel() {
		return objTypeFieldModel;
	}

	public void setObjTypeFieldModel(CapacityErrorFieldModel objTypeFieldModel) {
		this.objTypeFieldModel = objTypeFieldModel;
	}

	public CapacityErrorFieldModel getAddTypeFieldModel() {
		return addTypeFieldModel;
	}

	public void setAddTypeFieldModel(CapacityErrorFieldModel addTypeFieldModel) {
		this.addTypeFieldModel = addTypeFieldModel;
	}

	public CapacityErrorFieldModel getSeriesCodeFieldModel() {
		return seriesCodeFieldModel;
	}

	public void setSeriesCodeFieldModel(
			CapacityErrorFieldModel seriesCodeFieldModel) {
		this.seriesCodeFieldModel = seriesCodeFieldModel;
	}

	public CapacityErrorFieldModel getTelFieldModel() {
		return telFieldModel;
	}

	public void setTelFieldModel(CapacityErrorFieldModel telFieldModel) {
		this.telFieldModel = telFieldModel;
	}

	public CapacityErrorFieldModel getLevelFieldModel() {
		return levelFieldModel;
	}

	public void setLevelFieldModel(CapacityErrorFieldModel levelFieldModel) {
		this.levelFieldModel = levelFieldModel;
	}

	public CapacityErrorFieldModel getAddInfoFieldModel() {
		return addInfoFieldModel;
	}

	public void setAddInfoFieldModel(CapacityErrorFieldModel addInfoFieldModel) {
		this.addInfoFieldModel = addInfoFieldModel;
	}

	public CapacityErrorFieldModel getAddrTownFieldModel() {
		return addrTownFieldModel;
	}

	public void setAddrTownFieldModel(CapacityErrorFieldModel addrTownFieldModel) {
		this.addrTownFieldModel = addrTownFieldModel;
	}

	public CapacityErrorFieldModel getAddrAreaFieldModel() {
		return addrAreaFieldModel;
	}

	public void setAddrAreaFieldModel(CapacityErrorFieldModel addrAreaFieldModel) {
		this.addrAreaFieldModel = addrAreaFieldModel;
	}

	public CapacityErrorFieldModel getAddrStreetFieldModel() {
		return addrStreetFieldModel;
	}

	public void setAddrStreetFieldModel(
			CapacityErrorFieldModel addrStreetFieldModel) {
		this.addrStreetFieldModel = addrStreetFieldModel;
	}

	public CapacityErrorFieldModel getAddrNumFieldModel() {
		return addrNumFieldModel;
	}

	public void setAddrNumFieldModel(CapacityErrorFieldModel addrNumFieldModel) {
		this.addrNumFieldModel = addrNumFieldModel;
	}

	public CapacityErrorFieldModel getAddrOtherFieldModel() {
		return addrOtherFieldModel;
	}

	public void setAddrOtherFieldModel(
			CapacityErrorFieldModel addrOtherFieldModel) {
		this.addrOtherFieldModel = addrOtherFieldModel;
	}

	public CapacityErrorFieldModel getRemarkFieldModel() {
		return remarkFieldModel;
	}

	public void setRemarkFieldModel(CapacityErrorFieldModel remarkFieldModel) {
		this.remarkFieldModel = remarkFieldModel;
	}

	public int getAllFieldCount() {
		return chineseNameFieldModel.getCount()
				+ englishNameFieldModel.getCount()
				+ objTypeFieldModel.getCount() + addTypeFieldModel.getCount()
				+ seriesCodeFieldModel.getCount() + telFieldModel.getCount()
				+ levelFieldModel.getCount() + addInfoFieldModel.getCount()
				+ addrTownFieldModel.getCount() + addrAreaFieldModel.getCount()
				+ addrStreetFieldModel.getCount()
				+ addrNumFieldModel.getCount() + addrOtherFieldModel.getCount()
				+ remarkFieldModel.getCount();
	}

}
