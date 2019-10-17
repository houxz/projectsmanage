package com.emg.poiwebeditor.common;

public enum PoiProjectType {

	/**
	 * poi 点项目类型
	 */
	POI_DOT_PROJECTTYPE(0,"POI点项目类型"),
	/**
	 * POI 面项目类型
	 */
	POI_POLOGN_PROJECTTYPE(1,"POI面项目类型");
	
	private Integer value;
	private String des;
	private PoiProjectType(Integer value, String des) {
		this.value = value;
		this.des = des;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}	
	public static String toJsonStr() {
    		String str = new String("{");
      		for (PoiProjectType type : PoiProjectType.values()) {
      			str += "\"" + type.getValue() + "\":\"" + type.getDes() + "\",";
      		}
      		str += "}";
      		return str;
      	}
	
} 