package com.emg.poiwebeditor.pojo;

public class ErrorsTaskModel {
    private Long id;

    private String name;
    
    private Integer createby;

    private Integer qctask;
    
    private String qctaskdbname;
    private String qctaskdbschema;
    private String qctaskip;
    private String qctaskport;

    private Integer errorsrc;
    
    private String errorsrcdbname;
    private String errorsrcdbschema;
    private String errorsrcip;
    private String errorsrcport;

    private Integer errortar;
    
    private String errortardbname;
    private String errortardbschema;
    private String errortarip;
    private String errortarport;

	private Integer state;

    private Long minerrorid;

    private Long curerrorid;

    private Long maxerrorid;

    private String dotasktime;

    private Long batchid;

    private Long errorsetid;

    private String errorsetname;

    private Integer enable;

    private String createtime;

    private String updatetime;
    
    private String mact;
    
    //add by lianhr begin 2019/03/06
    private String remarkname;
    //add by lianhr end

    public String getRemarkname() {
		return remarkname;
	}

	public void setRemarkname(String remarkname) {
		this.remarkname = remarkname;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getQctask() {
        return qctask;
    }

    public void setQctask(Integer qctask) {
        this.qctask = qctask;
    }

    public Integer getErrorsrc() {
        return errorsrc;
    }

    public void setErrorsrc(Integer errorsrc) {
        this.errorsrc = errorsrc;
    }

    public Integer getErrortar() {
        return errortar;
    }

    public void setErrortar(Integer errortar) {
        this.errortar = errortar;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getMinerrorid() {
        return minerrorid;
    }

    public void setMinerrorid(Long minerrorid) {
        this.minerrorid = minerrorid;
    }

    public Long getCurerrorid() {
        return curerrorid;
    }

    public void setCurerrorid(Long curerrorid) {
        this.curerrorid = curerrorid;
    }

    public Long getMaxerrorid() {
        return maxerrorid;
    }

    public void setMaxerrorid(Long maxerrorid) {
        this.maxerrorid = maxerrorid;
    }

    public String getDotasktime() {
        return dotasktime;
    }

    public void setDotasktime(String dotasktime) {
        this.dotasktime = dotasktime == null ? null : dotasktime.trim();
    }

    public Long getBatchid() {
        return batchid;
    }

    public void setBatchid(Long batchid) {
        this.batchid = batchid;
    }

    public Long getErrorsetid() {
        return errorsetid;
    }

    public void setErrorsetid(Long errorsetid) {
        this.errorsetid = errorsetid;
    }

    public String getErrorsetname() {
        return errorsetname;
    }

    public void setErrorsetname(String errorsetname) {
        this.errorsetname = errorsetname == null ? null : errorsetname.trim();
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

	public String getQctaskdbname() {
		return qctaskdbname;
	}

	public void setQctaskdbname(String qctaskdbname) {
		this.qctaskdbname = qctaskdbname;
	}

	public String getQctaskdbschema() {
		return qctaskdbschema;
	}

	public void setQctaskdbschema(String qctaskdbschema) {
		this.qctaskdbschema = qctaskdbschema;
	}

	public String getQctaskip() {
		return qctaskip;
	}

	public void setQctaskip(String qctaskip) {
		this.qctaskip = qctaskip;
	}

	public String getQctaskport() {
		return qctaskport;
	}

	public void setQctaskport(String qctaskport) {
		this.qctaskport = qctaskport;
	}

	public String getErrorsrcdbname() {
		return errorsrcdbname;
	}

	public void setErrorsrcdbname(String errorsrcdbname) {
		this.errorsrcdbname = errorsrcdbname;
	}

	public String getErrorsrcdbschema() {
		return errorsrcdbschema;
	}

	public void setErrorsrcdbschema(String errorsrcdbschema) {
		this.errorsrcdbschema = errorsrcdbschema;
	}

	public String getErrorsrcip() {
		return errorsrcip;
	}

	public void setErrorsrcip(String errorsrcip) {
		this.errorsrcip = errorsrcip;
	}

	public String getErrorsrcport() {
		return errorsrcport;
	}

	public void setErrorsrcport(String errorsrcport) {
		this.errorsrcport = errorsrcport;
	}

	public String getErrortardbname() {
		return errortardbname;
	}

	public void setErrortardbname(String errortardbname) {
		this.errortardbname = errortardbname;
	}

	public String getErrortardbschema() {
		return errortardbschema;
	}

	public void setErrortardbschema(String errortardbschema) {
		this.errortardbschema = errortardbschema;
	}

	public String getErrortarip() {
		return errortarip;
	}

	public void setErrortarip(String errortarip) {
		this.errortarip = errortarip;
	}

	public String getErrortarport() {
		return errortarport;
	}

	public void setErrortarport(String errortarport) {
		this.errortarport = errortarport;
	}

	public Integer getCreateby() {
		return createby;
	}

	public void setCreateby(Integer createby) {
		this.createby = createby;
	}

	public String getMact() {
		return mact;
	}

	public void setMact(String mact) {
		this.mact = mact;
	}
}