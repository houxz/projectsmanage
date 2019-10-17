package com.emg.poiwebeditor.common;

import java.util.ArrayList;
import java.util.List;

public enum TaskTypeEnum {
	/**
	 * -1, "改错项目"
	 */
	UNKNOWN(-1, "未知任务类型"),
	/**
	 * 11, "改错任务"
	 */
	ERROR(11, "综检改错任务"),
	/**
	 * 12, "NR/FC项目"
	 */
	NRFC(12, "NR/FC任务"),
	/**
	 * 13, "关系附属表项目"
	 */
	ATTACH(13, "关系附属表任务"),
	/**
	 * 14, "整图编辑任务"
	 */
	ADJUSTMAP(14, "整图编辑任务"),
	/**
	 * 15101, "POI客投制作"
	 */
	POI_KETOU(15101, "POI客投制作"),
	/**
	 * 15102, "POI易淘金制作"
	 */
	POI_GEN(15102, "POI易淘金制作"),
	/**
	 * 15103, "POI易淘金导入自动完成任务"
	 */
	POI_GEN_IMPORT(15103, "POI易淘金导入自动完成任务"),
	/**
	 * 15105, "POI非实测导入自动完成"
	 */
	POI_FEISHICE_IMPORT(15105, "POI非实测导入自动完成"),
	/**
	 * 15106, "POI非实测改错任务"
	 */
	POI_FEISHICE(15106, "POI非实测改错任务"),
	/**
	 * 15107, "POI地址电话导入完成任务"
	 */
	POI_FEISHICEADDRESSTEL_IMPORT(15107, "POI地址电话导入完成任务"),
	/**
	 * 15108, "POI地址电话改错任务"
	 */
	POI_FEISHICEADDRESSTEL(15108, "POI地址电话改错任务"),
	/**
	 * 15110, "车调POI创建制作任务"
	 */
	POI_DATASET_31(15110, "车调POI创建制作任务"),
	/**
	 * 15111, "车调制作32无照片"
	 */
	POI_DATASET_32(15111, "车调PPNTPOI制作任务"),
	/**
	 * 15111, "车调制作32无照片"
	 */
	POI_LCS(15112, "LCS重要类型POI任务"),
	/**
	 * 15111, "车调制作32无照片"
	 */
	POI_GEN_WEB(15113, "易淘金WEB任务"),
	/**
	 * 15210, "车调校正31有照片"
	 */
	POI_MC_DATASET_31(15210, "车调POI创建校正任务"),
	/**
	 * 15211, "车调校正32无照片"
	 */
	POI_MC_DATASET_32(15211, "车调PPNTPOI校正任务"),
	/**
	 * 15119, "POI全国改错任务"
	 */
	POI_QUANGUOQC(15119, "POI全国改错任务"),
	/**
	 * 15201, "POI客投校正"
	 */
	POI_MC_KETOU(15201, "POI客投校正"),
	/**
	 * 15202, "POI易淘金校正"
	 */
	POI_MC_GEN(15202, "POI易淘金校正"),
	/**
	 * 14004, "九宫格质检"
	 */
	QC_JIUGONGGE(14004, "九宫格质检"),
	/**
	 * 14006, "全域质检"
	 */
	QC_QUANYU(14006, "全域质检"),
	/**
	 * 16101, "易淘金线上编辑任务"
	 */
	GEN_WEB(16101, "易淘金线上编辑任务"),
	/**
	 * 17, "区划任务"
	 */
	AREA_QUHUAN(17, "区划任务"),
	/**
	 * 18, "建成区任务"
	 */
	AREA_JIANCHENGQU(18, "建成区任务"),
	/**
	 * 19, "附属表资料"
	 */
	ATTACHWITHDATA(19, "附属表资料"),
	/**
	 * 20, "POI人工聚合项目"
	 */
	POIPOLYMERIZE(20, "POI人工聚合任务"),
	POIPOLYMERIZE_EDIT(17001,"人工聚合POI点制作任务"),
    POIPOLYMERIZE_CHECK(17002,"人工聚合POI点抽检任务"),
	POIPOLYMERIZE_EDIT2(17003,"人工聚合POI面制作任务"),
    POIPOLYMERIZE_CHECK2(17004,"人工聚合POI面抽检任务");

	private Integer value;
	private String des;

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

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public static List<TaskTypeEnum> getPoiEditTaskTypes() {
		List<TaskTypeEnum> taskTypes = new ArrayList<TaskTypeEnum>();
		for (TaskTypeEnum val : TaskTypeEnum.values()) {
			if (val.toString().startsWith("POI_") && !val.toString().startsWith("POI_MC_")) {
				taskTypes.add(val);
			}
		}
		return taskTypes;
	}
	
	public static List<TaskTypeEnum> getPoiCheckTaskTypes() {
		List<TaskTypeEnum> taskTypes = new ArrayList<TaskTypeEnum>();
		for (TaskTypeEnum val : TaskTypeEnum.values()) {
			if (val.toString().startsWith("POI_MC_")) {
				taskTypes.add(val);
			}
		}
		return taskTypes;
	}

	public static String toJsonStr() {
		String str = new String("{");
		for (TaskTypeEnum val : TaskTypeEnum.values()) {
			if (val.equals(UNKNOWN))
				continue;
			str += "\"" + val.getValue() + "\":\"" + val.getDes() + "\",";
		}
		str += "}";
		return str;
	}

	public static TaskTypeEnum valueOf(Integer tasktype) {
		TaskTypeEnum ret = UNKNOWN;
		for (TaskTypeEnum val : TaskTypeEnum.values()) {
			if (val.getValue().equals(tasktype)) {
				ret = val;
				break;
			}
		}
		return ret;
	}

}
