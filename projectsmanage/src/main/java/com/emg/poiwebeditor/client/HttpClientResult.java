package com.emg.poiwebeditor.client;

import org.springframework.http.HttpStatus;

public class HttpClientResult {
	
	private HttpStatus status = HttpStatus.NOT_FOUND;
	
	private String resultMsg;
	
	private String json;

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	
	
}
