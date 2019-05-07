package com.emg.poiwebeditor.config;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.emg.poiwebeditor.auth.MenuAuthModel;

@Component
public class MenuConfig {
	private Set<MenuAuthModel> menus;

	public Set<MenuAuthModel> getMenus() {
		return menus;
	}

	public void setMenus(Set<MenuAuthModel> menus) {
		this.menus = menus;
	}
}
