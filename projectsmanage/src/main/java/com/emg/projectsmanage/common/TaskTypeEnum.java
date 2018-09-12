package com.emg.projectsmanage.common;

/**
 * 质检项集合类型
 * 
 * @author zsen
 * 
 */
public enum TaskTypeEnum {
	/**
	 * -1, "改错项目"
	 */
	UNKNOWN(-1, "未知任务类型"),
	/**
	 * 11, "改错任务"
	 */
	ERROR(11, "改错任务"),
	/**
	 * 12, "NR/FC项目"
	 */
	NRFC(12, "NR/FC任务"),
	/**
	 * 13, "关系附属表项目"
	 */
	ATTACH(13, "关系附属表任务");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private TaskTypeEnum(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public static TaskTypeEnum valueOf(Integer value) {
		TaskTypeEnum ret = TaskTypeEnum.UNKNOWN;
		for(TaskTypeEnum v : TaskTypeEnum.values()) {
			if(v.getValue().equals(value)) {
				ret = v;
				break;
			}
		}
		return ret;
	}
	
	public static String toJsonStr() {
		String str = new String("{");
		for (TaskTypeEnum val : TaskTypeEnum.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
