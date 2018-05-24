package com.emg.projectsmanage.pojo;

public class ErrorAndErrorRelatedModel extends ErrorModel {
	
	private Long rid;

    private Integer rtype;

    private Long rfeatureid;

    private Integer rlayerid;

    private Long reditver;

    public Integer getRtype() {
        return rtype;
    }

    public void setRtype(Integer rtype) {
        this.rtype = rtype;
    }

    public Long getRfeatureid() {
        return rfeatureid;
    }

    public void setRfeatureid(Long rfeatureid) {
        this.rfeatureid = rfeatureid;
    }

    public Integer getRlayerid() {
        return rlayerid;
    }

    public void setRlayerid(Integer rlayerid) {
        this.rlayerid = rlayerid;
    }

    public Long getReditver() {
        return reditver;
    }

    public void setReditver(Long reditver) {
        this.reditver = reditver;
    }

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}
}