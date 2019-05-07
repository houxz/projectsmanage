package com.emg.poiwebeditor.common;

/**
 * 图层要素
 * 
 * @author zsen
 * 
 */
public enum LayerElement {
	/**
	 * 1, "道路"
	 */
	Road(1, "道路"),
	/**
	 * 2, "兴趣点"
	 */
	POI(2, "兴趣点"),
	/**
	 * 3-电子眼
	 */
	Trafficcam(3, "电子眼"),
	/**
	 * 4, "危险警告牌"
	 */
	WarnInfo(4, "危险警告牌"),
	/**
	 * 5, "速度限制牌"
	 */
	SpeedLimit(5, "速度限制牌"),
	/**
	 * 6, "Obj"
	 */
	Obj(6, "三维建物"),
	/**
	 * 7, "铁路"
	 */
	Railway(7, "铁路"),
	/**
	 * 8, "植被"
	 */
	Vegetation(8, "植被"),
	/**
	 * 9, "水系"
	 */
	Water(9, "水系"),
	/**
	 * 10, "岛屿"
	 */
	Island(10, "岛屿"),
	/**
	 * 11, "线状建筑物"
	 */
	BuildingLine(11, "线状建筑物"),
	/**
	 * 12, "建筑物区域"
	 */
	BuildingArea(12, "建筑物区域"),
	/**
	 * 13, "独立建筑物"
	 */
	Building(13, "独立建筑物"),
	/**
	 * 14, "县级行政区划"
	 */
	Xian(14, "县级行政区划"),
	/**
	 * 15, "地区级行政区划"
	 */
	Diqu(15, "地区级行政区划"),
	/**
	 * 16, "省级行政区划"
	 */
	Province(16, "省级行政区划"),
	/**
	 * 17, "国家级行政区划"
	 */
	Country(17, "国家级行政区划"),
	/**
	 * 18, "国家级行政区划"
	 */
	Direction(18, "方向"),
	/**
	 * 19, "转向"
	 */
	Turn(19, "转向"),
	/**
	 * 18, "国家级行政区划"
	 */
	Lane(20, "车道"),
	/**
	 * 18, "国家级行政区划"
	 */
	JunctionView(21, "路口放大图");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private LayerElement(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public static String toJsonStr() {
		String str = new String("{");
		for (LayerElement val : LayerElement.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}

	public static String layers() {
		String str = new String("{");
		for (LayerElement val : LayerElement.values()) {
			str += "\"" + val.toString() + "\":\"" + val.toString() + "\",";
		}
		str += "}";
		return str;
	}
}
