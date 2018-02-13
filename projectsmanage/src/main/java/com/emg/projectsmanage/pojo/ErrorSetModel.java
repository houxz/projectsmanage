package com.emg.projectsmanage.pojo;

public class ErrorSetModel {
    private Long id;

    private String name;

    private Integer type;

    private Integer systype;

    private String referdata;

    private Byte unit;

    private String desc;

    private String updatetime;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSystype() {
        return systype;
    }

    public void setSystype(Integer systype) {
        this.systype = systype;
    }

    public String getReferdata() {
        return referdata;
    }

    public void setReferdata(String referdata) {
        this.referdata = referdata == null ? null : referdata.trim();
    }

    public Byte getUnit() {
        return unit;
    }

    public void setUnit(Byte unit) {
        this.unit = unit;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc == null ? null : desc.trim();
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}