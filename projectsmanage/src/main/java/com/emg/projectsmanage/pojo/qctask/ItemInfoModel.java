package com.emg.projectsmanage.pojo.qctask;

public class ItemInfoModel {
    private Long id;

    private String oid;

    private String name;

    private String layername;

    private Short enable;

    private Short unit;

    private Integer type;

    private Integer systype;

    private String referdata;

    private String module;

    private String updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid == null ? null : oid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getLayername() {
        return layername;
    }

    public void setLayername(String layername) {
        this.layername = layername == null ? null : layername.trim();
    }

    public Short getEnable() {
        return enable;
    }

    public void setEnable(Short enable) {
        this.enable = enable;
    }

    public Short getUnit() {
        return unit;
    }

    public void setUnit(Short unit) {
        this.unit = unit;
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

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module == null ? null : module.trim();
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}