package com.emg.poiwebeditor.bean;

/**
 * 几何错误统计表
 * 
 * @author AD
 * 
 */
public class CapacityErrorGeoCountModel {

	private String name = ""; // 编辑工作量统计poi名称
	private int count = 0; // 数量

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

}
