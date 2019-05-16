package com.emg.poiwebeditor.pojo;

public class TagDO implements Comparable<TagDO> {

	private Long id = -1L;
	private String k;
	private String v;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getK() {
		return k;
	}

	public void setK(String k) {
		this.k = k;
	}

	public String getV() {
		return v;
	}

	public void setV(String v) {
		this.v = v;
	}

	@Override
	public int compareTo(TagDO obj) {
		if (this.id.equals(obj.id) &&
				this.k.equals(obj.getK())) {
			return 0;
		} else {
			return this.id.compareTo(obj.getId()) * 100 + this.k.compareTo(obj.getK());
		}
	}

}
