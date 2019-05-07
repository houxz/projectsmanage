package com.emg.poiwebeditor.common;

/**
 * @author zsen
 */
public class CommonConstants {
	private static final String PACKAGE = "com_emg_webeditor";
	public static final String SESSION_USER_ACC = PACKAGE + "_SESSION_USER_ACC";
	public static final String SESSION_USER_ID = PACKAGE + "_SESSION_USER_ID";
	public static final String SESSION_USER_NAME = PACKAGE + "_SESSION_USER_NAME";
	
	/**
	 * 改错项目阶段数，默认为4个阶段
	 * 1：质检准备，2：质检，3：改错准备，4：改错；
	 */
	public static final Integer PROCESSCOUNT_ERROR = 4;
	
	/**
	 * 全国质检项目阶段数：默认为2个阶段
	 * 1： 质检准备， 2， 质检
	 */
	public static final Integer PROCESSCOUNT_COUNTRY = 2;
	
	/**
	 * NR/FC项目阶段数，默认为3个阶段
	 * 1：编辑准备，2：编辑，3：待发布准备。
	 */
	public static final Integer PROCESSCOUNT_NRFC = 3;
}
