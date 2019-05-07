package com.emg.poiwebeditor.common;

import org.springframework.ui.ModelMap;

public class InterfaceResultModel extends ModelMap {

	private static final long serialVersionUID = -3625690762034071271L;

	{
		put("status", false);
		put("option", new String("无消息"));
	}
	
	public void setStatus(Boolean status) {
		this.put("status", status);
	}
	
	public void setOption(Object option) {
		this.put("option", option);
	}
	
}
