package com.emg.projectsmanage.pojo;

public class ConfigDefaultModel {
    private Integer id;

    private Integer configid;

    private Integer processtype;

    private String name;

    private Byte visible;

    private Byte editable;

    private String defaultvalue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getConfigid() {
        return configid;
    }

    public void setConfigid(Integer configid) {
        this.configid = configid;
    }

    public Integer getProcesstype() {
        return processtype;
    }

    public void setProcesstype(Integer processtype) {
        this.processtype = processtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Byte getVisible() {
        return visible;
    }

    public void setVisible(Byte visible) {
        this.visible = visible;
    }

    public Byte getEditable() {
        return editable;
    }

    public void setEditable(Byte editable) {
        this.editable = editable;
    }

    public String getDefaultvalue() {
        return defaultvalue;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue == null ? null : defaultvalue.trim();
    }
}