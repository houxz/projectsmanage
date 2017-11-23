package com.emg.projectsmanage.common;

public enum OwnerStatus {
	
	DISABLED(-1,"不可用"),
	PUBLIC(0,"公有"),
	PRIVATE(1,"私有");
	
	private int value;
	
	private String description;
	
	OwnerStatus(int value,String description){
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
		for(OwnerStatus jobStatus : OwnerStatus.values()){
			str += "\"" + jobStatus.getValue() + "\":\"" + jobStatus.getDescription() + "\",";
		}
		str += "}";
		return str;
	}
}
