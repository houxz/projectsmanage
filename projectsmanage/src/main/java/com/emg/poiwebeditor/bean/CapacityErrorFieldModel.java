package com.emg.poiwebeditor.bean;

/**
 * 产量错误字段
 * 
 * @author AD
 * 
 */
public class CapacityErrorFieldModel {

	private int id = -1;// id
	private String name = "";// 名称
	private int category = -1;// 类别，1:属性，2：几何
	private int fieldType = -1;// 错误字段
	private int showFlag = -1;// 显示
	private int count = 0;// 数量

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getFieldType() {
		return fieldType;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public int getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(int showFlag) {
		this.showFlag = showFlag;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = this.count + count;
	}

}
