package com.emg.poiwebeditor.common;

/**
 * 任务状态枚举
 * @author Administrator
 *
 */
public enum TypeEnum {
	/**
	 * 制作任务初始状态
	 */
	edit_init(0,0, "editid", 17001),
	/**
	 * 制作任务占用状态
	 */
	edit_using(1,5, "editid", 17001),
	
	/**
	 * 制作任务提交状态
	 */
	edit_submit(2, 5, "editid", 17001),
	/**
	 * 制作任务跳过状态（稍后修改状态）
	 */
	edit_used(5, 5, "editid", 17001),
	
	/**
	 * 抽检任务
	 */
	check_init(0,0,"checkid", 17002),
	check_using(1,7,"checkid", 17002),
	check_submit(2, 7, "checkid", 17002),
	check_used(5,7,"checkid", 17002);
	
	private int state;
	private int process;
	private String userColumn;
	private int taskType;
	private TypeEnum(int state, int process, String userColumn, int taskType) {
		this.state = state;
		this.process = process;
		this.userColumn = userColumn;
		this.taskType = taskType;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getProcess() {
		return process;
	}
	public void setProcess(int process) {
		this.process = process;
	}
	public String getUserColumn() {
		return userColumn;
	}
	public void setUserColumn(String userColumn) {
		this.userColumn = userColumn;
	}
	public int getTaskType() {
		return taskType;
	}
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

}
