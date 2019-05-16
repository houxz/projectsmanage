package com.emg.poiwebeditor.pojo;

import java.util.HashSet;
import java.util.Set;

public class EntityTagsDO extends EntityCommonFieldsDO {
	private Set<TagDO> poitags = new HashSet<TagDO>();
	
	public Set<TagDO> getPoitags() {
		return poitags == null ? new HashSet<TagDO>() : poitags;
	}
	public void setPoitags(Set<TagDO> tags) {
		this.poitags = tags;
	}
}
