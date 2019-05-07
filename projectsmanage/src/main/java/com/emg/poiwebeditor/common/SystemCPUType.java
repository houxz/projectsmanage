package com.emg.poiwebeditor.common;

public enum SystemCPUType {
	
	X86(86,"86"),
	X64(64,"64");
	
	private int value;
	
	private String description;
	
	SystemCPUType(int value,String description){
		this.setValue(value);
		this.setDescription(description);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static String toJsonStr(){
		String str = new String("{");
		for(SystemCPUType jobStatus : SystemCPUType.values()){
			str += "\"" + jobStatus.getValue() + "\":\"" + jobStatus.getDescription() + "\",";
		}
		str += "}";
		return str;
	}
}
