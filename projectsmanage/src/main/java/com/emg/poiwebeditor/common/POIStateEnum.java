package com.emg.poiwebeditor.common;

public enum POIStateEnum {
	NORMAL(0, "正常"),	
	BUILD(1, "在建"),
	NOTOPEN(2, "不开放"),
	REPAIRE(3, "维修"),
	NOTCONFIRM(4,"未调查");
	
	private int value;
	private String detail;
	private POIStateEnum(int value, String detail) {
		this.value = value;
		this.detail = detail;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	public static String getSexEnumByCode(int value){
	    for(POIStateEnum poistate : POIStateEnum.values()){
	    	if(poistate.getValue() == value) {
	    		return poistate.getSexEnumByCode(value);
	    	}
	    }
	    return "";
	  }
	
	

}
