package com.emg.projectsmanage.pojo;

public class ConfigValueModel {
	
    private Long idc;
    
    private Long processid;

    private Integer moduleid;

    private Integer configid;

    private String value;
    
    private String name;

    public Long getId() {
        return idc;
    }

    public void setId(Long id) {
        this.idc = id;
    }
    
    public Long getProcessId() {
        return processid;
    }

    public void setProcessId(Long id) {
        this.processid = id;
    }


    public Integer getModuleid() {
        return moduleid;
    }

    public void setModuleid(Integer id) {
        this.moduleid = id;
    }
    
    public Integer getConfigId() {
        return configid;
    }

    public void setConfigId(Integer id) {
        this.configid = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}