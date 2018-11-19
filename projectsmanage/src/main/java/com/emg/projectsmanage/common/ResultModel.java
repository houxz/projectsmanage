package com.emg.projectsmanage.common;

import org.springframework.ui.ModelMap;

public class ResultModel extends ModelMap {

	private static final long serialVersionUID = -3625690762034071271L;

	{
		put("rows", null);
		put("total", 0);
		put("result", 0);
		put("resultMsg", new String("无消息"));
	}
	
	public void setRows(Object list) {
		this.put("rows", list);
	}
	
	public void setTotal(Integer total) {
		this.put("total", total);
	}
	
	public void setResult(Integer result) {
		this.put("result", result);
	}
	
	public void setResultMsg(String resultMsg) {
		this.put("resultMsg", resultMsg);
	}
}
