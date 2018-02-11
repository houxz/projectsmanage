package com.emg.projectsmanage.common;

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
	 * 2, "POI"
	 */
	POI(2, "POI"),
	/**
	 * 3, "VS"
	 */
	VS(3, "VS"),
	/**
	 * 4-待补充
	 */
	Trafficcam(4, "待补充"),
	/**
	 * 5, "危险警告"
	 */
	WarnInfo(5, "危险警告"),
	/**
	 * 6, "速度限制"
	 */
	SpeedLimit(6, "速度限制"),
	/**
	 * 7, "OBJ"
	 */
	OBJ(7, "OBJ"),
	/**
	 * 8, "铁路"
	 */
	Railway(8, "铁路"),
	/**
	 * 9, "待补充"
	 */
	Vegetation(9, "待补充"),
	/**
	 * 10, "水域"
	 */
	Water(10, "水域"),
	/**
	 * 11, "验证"
	 */
	Island(11, "验证");

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
}
