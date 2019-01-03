package com.emg.projectsmanage.common;

public enum JobStatus {
	JOB_EXCEPTION(-1,"异常"),
	JOB_WAITING(0,"待执行"),
	JOB_DOING(1,"执行中"),
	JOB_STOP(2,"暂停"),
	JOB_LOCKED(3,"锁定"),
	JOB_DONE(4,"完成");
	
	private int value;
	
	private String description;
	
	JobStatus(int value,String description){
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
		for(JobStatus jobStatus : JobStatus.values()){
			str += "\"" + jobStatus.getValue() + "\":\"" + jobStatus.getDescription() + "\",";
		}
		str += "}";
		return str;
	}
}
