package com.emg.projectsmanage.common;

public enum ProcessConfigEnum {
	ZHIJIANXIANGMUKU(1, "质检项目库"),
	ZHIJIANRENWUKU(2, "质检任务库"),
	ZHIJIANXIANGMUID(3, "质检项目ID"),
	ZHIJIANXIANGMUMINGCHENG(4, "质检项目名称"),
	ZHIJIANJIHE(5, "质检集合"),
	ZHIJIANTUCENG(6, "质检图层"),
	ZHIJIANQUYU(7, "质检区域"),
	ZHIJIANQIDONGLEIXING(8, "质检启动类型"),
	BIANJIXIANGMUKU(9, "编辑项目库"),
	BIANJIRENWUKU(10, "编辑任务库"),
	BIANJIXIANGMUID(11, "编辑项目ID"),
	BIANJIXIANGMUMINGCHENG(12, "编辑项目名称"),
	GAICUORENWUZUZHIFANGSHI(13, "改错任务组织方式"),
	CUOWUGESHU(14, "错误个数"),
	CUOWUJULI(15, "错误距离"),
	CUOWUKU(16, "错误库"),
	BIANJIQIDONGLEIXING(17, "编辑启动类型"),
	GAICUORENYUAN(18, "改错人员"),
	GONGYOUSIYOU(19, "公有私有"),
	CUOWUDAORUKU(20, "错误导入库");

	private Integer value;
	private String des;
	
	private ProcessConfigEnum(Integer value, String des) {
		this.setValue(value);
		this.setDes(des);
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

}