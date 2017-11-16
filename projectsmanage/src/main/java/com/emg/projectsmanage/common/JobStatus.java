package com.emg.projectsmanage.common;

public enum JobStatus {
	
	JOB_WAITING(0,"未开启"),
	JOB_DOING(1,"作业中"),
	JOB_STOP(2,"暂停"),
	JOB_LOCKED(3,"锁定"),
	JOB_DONE(4,"完成"),
	JOB_SUBMIT(5,"提交");
	
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
