package com.emg.projectsmanage.common;

/**
 * 操作类型
 * 
 * @author zsen
 * 
 */
public enum SystemType {
	/**
	 * (-99, "未知系统"
	 */
	Unknow(-99, "未知系统"),
	/**
	 * 0, "POI视频编辑平台"
	 */
	poivideoedit(0, "POI视频编辑平台"),
	/**
	 * 332, "数据库质检系统"
	 */
	DBMapChecker(332, "数据库质检系统"),
	/**
	 * 349, "综合编辑平台"
	 */
	MapDbEdit(349, "综合编辑平台"),
	/**
	 * 3491, "综合编辑平台_NRFC编辑"
	 */
	MapDbEdit_NRFC(3491, "综合编辑平台_NRFC编辑"),
	/**
	 * 3492, "综合编辑平台_关系附属表"
	 */
	MapDbEdit_Attach(3492, "综合编辑平台_关系附属表"),
	/**
	 * 3493, "综合编辑平台_全国质检"
	 */
	MapDbEdit_Country(3493, "综合编辑平台_全国质检"),
	/**
	 * 350, "整图编辑平台"
	 */
	AdjustMap(350, "整图编辑平台"),
	/**
	 * 360, "易淘金在线编辑平台"
	 */
	poi_GEN(360, "易淘金在线编辑平台");

	private Integer value;
	private String des;

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	private SystemType(Integer value, String des) {
		this.setValue(value);
		this.des = des;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public static SystemType valueOf(Integer value) {
		SystemType ret = SystemType.Unknow;
		for(SystemType sysType : SystemType.values()) {
			if(sysType.getValue().equals(value)) {
				ret = sysType;
				break;
			}
		}
		return ret;
	}
	
	public static String toJsonStr() {
		String str = new String("{");
		for (SystemType val : SystemType.values()) {
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}
}
