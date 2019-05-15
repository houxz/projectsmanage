package com.emg.poiwebeditor.pojo;

import java.util.ArrayList;
import java.util.List;

public class EntityTagsDO extends EntityCommonFieldsDO {
	private List<TagDO> poitags = new ArrayList<TagDO>();
	
	public List<TagDO> getPoitags() {
		return poitags == null ? new ArrayList<TagDO>() : poitags;
	}
	public void setPoitags(List<TagDO> tags) {
		this.poitags = tags;
	}
}
