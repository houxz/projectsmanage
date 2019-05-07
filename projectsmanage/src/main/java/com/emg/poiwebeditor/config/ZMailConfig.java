package com.emg.poiwebeditor.config;

import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ZMailConfig {
	private Boolean enabled;
	private String from;
	private Set<String> tos;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public Set<String> getTos() {
		return tos;
	}
	public void setTos(Set<String> tos) {
		this.tos = tos;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled.equals("true");
	}
}
