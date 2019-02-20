package com.emg.projectsmanage.common;

public enum ProcessConfigEnum {
	/**
	 * 1, "质检项目库"
	 */
	ZHIJIANXIANGMUKU(1, "质检项目库"),
	/**
	 * 2, "质检任务库"
	 */
	ZHIJIANRENWUKU(2, "质检任务库"),
	/**
	 * 3, "质检项目ID"
	 */
	ZHIJIANXIANGMUID(3, "质检项目ID"),
	/**
	 * 4, "质检项目名称"
	 */
	ZHIJIANXIANGMUMINGCHENG(4, "质检项目名称"),
	/**
	 * 5, "质检集合"
	 */
	ZHIJIANJIHE(5, "质检集合"),
	/**
	 * 6, "质检图层"
	 */
	ZHIJIANTUCENG(6, "质检图层"),
	/**
	 * 7, "质检区域"
	 */
	ZHIJIANQUYU(7, "质检区域"),
	/**
	 * 8, "质检启动类型"
	 */
	ZHIJIANQIDONGLEIXING(8, "质检启动类型"),
	/**
	 * 9, "编辑项目库"
	 */
	BIANJIXIANGMUKU(9, "编辑项目库"),
	/**
	 * 10, "编辑任务库"
	 */
	BIANJIRENWUKU(10, "编辑任务库"),
	/**
	 * 11, "编辑项目ID"
	 */
	BIANJIXIANGMUID(11, "编辑项目ID"),
	/**
	 * 12, "编辑项目名称"
	 */
	BIANJIXIANGMUMINGCHENG(12, "编辑项目名称"),
	/**
	 * 13, "改错任务组织方式"
	 */
	GAICUORENWUZUZHIFANGSHI(13, "改错任务组织方式"),
	/**
	 * 14, "错误个数"
	 */
	CUOWUGESHU(14, "错误个数"),
	/**
	 * 15, "错误距离"
	 */
	CUOWUJULI(15, "错误距离"),
	/**
	 * 16, "错误库"
	 */
	CUOWUKU(16, "质检错误库"),
	/**
	 * 17, "编辑启动类型"
	 */
	BIANJIQIDONGLEIXING(17, "编辑启动类型"),
	/**
	 * 18, "改错人员"
	 */
	GAICUORENYUAN(18, "改错人员"),
	/**
	 * 19, "公有私有"
	 */
	GONGYOUSIYOU(19, "公有私有"),
	/**
	 * 20, "错误导入库"
	 */
	CUOWUDAORUKU(20, "错误导入库"),
	/**
	 * 21, "校正人员"
	 */
	JIAOZHENGRENYUAN(21, "校正人员"),
	/**
	 * 22, "创建任务方式"
	 */
	CHUANJIANRENWUFANGSHI(22, "创建任务方式"),
	/**
	 * 23, "免校正"
	 */
	MIANJIAOZHENG(23, "免校正"),
	/**
	 * 24, "资料库"
	 */
	ZILIAOKU(24, "资料库"),
	/**
	 * 25, "绑定资料"
	 */
	BANGDINGZILIAO(25, "绑定资料"),
	/**
	 * 26, "制作任务数"
	 */
	ZHIZUORENWUSHU(26, "制作任务数"),
	/**
	 * 27, "编辑数据库"
	 */
	BIANJISHUJUKU(27, "编辑数据库"),
	/**
	 * 28, "任务模式"
	 */
	RENWUMOSHI(28, "任务模式"),
	/**
	 * 29, "质检模式"
	 */
	ZHIJIANMOSHI(29, "质检模式");

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