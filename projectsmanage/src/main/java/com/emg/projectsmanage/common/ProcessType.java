package com.emg.projectsmanage.common;

/**
 * 质检项集合类型
 * 
 * @author zsen
 * 
 */
public enum ProcessType {
	/**
	 * -1, "未知项目类型"
	 */
	UNKNOWN(-1, "未知项目类型"),
	/**
	 * 1, "改错项目"
	 */
	ERROR(1, "综检改错项目"),
	/**
	 * 2, "NR/FC项目"
	 */
	NRFC(2, "NR/FC项目"),
	/**
	 * 3, "关系附属表项目"
	 */
	ATTACH(3, "关系附属表项目"),
	/**
	 * 4, "全国质检项目"
	 */
	COUNTRY(4, "全国质检项目"),
	/**
	 * 5, "POI编辑项目"
	 */
	POIEDIT(5, "POI编辑项目"),
	/**
	 * 6, "整图编辑项目"
	 */
	ADJUSTMAP(6, "整图编辑项目"),
	/**
	 * 7, "易淘金项目"
	 */
	GEN(7, "易淘金项目");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private ProcessType(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public static ProcessType valueOf(Integer value) {
		ProcessType ret = ProcessType.UNKNOWN;
		for(ProcessType v : ProcessType.values()) {
			if(v.getValue().equals(value)) {
				ret = v;
				break;
			}
		}
		return ret;
	}
	
	public static String toJsonStr() {
		String str = new String("{");
		for (ProcessType val : ProcessType.values()) {
			if(val.equals(UNKNOWN)) continue;
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
