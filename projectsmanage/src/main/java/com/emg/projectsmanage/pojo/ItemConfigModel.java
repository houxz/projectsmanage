package com.emg.projectsmanage.pojo;

import java.sql.Date;

public class ItemConfigModel {
    private Integer id;
    
    private String qname;

    private String name;

    private String qid;
    
    private Long errortype;
    
    private String desc;
    
    private Integer usable;
    
    private Integer unit;
    
    private Integer isexistokerror;
    
    private Date createtime;
    
    private Integer iswarning;
    
    private String uuidruler;
    
    private String version;
    
    private String keyword;
    
    private Date updatetime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQname() {
		return qname;
	}

	public void setQname(String qname) {
		this.qname = qname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public Long getErrortype() {
		return errortype;
	}

	public void setErrortype(Long errortype) {
		this.errortype = errortype;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getUnit() {
		return unit;
	}

	public void setUnit(Integer unit) {
		this.unit = unit;
	}

	public Integer getIsexistokerror() {
		return isexistokerror;
	}

	public void setIsexistokerror(Integer isexistokerror) {
		this.isexistokerror = isexistokerror;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Integer getIswarning() {
		return iswarning;
	}

	public void setIswarning(Integer iswarning) {
		this.iswarning = iswarning;
	}

	public String getUuidruler() {
		return uuidruler;
	}

	public void setUuidruler(String uuidruler) {
		this.uuidruler = uuidruler;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public Integer getUsable() {
		return usable;
	}

	public void setUsable(Integer usable) {
		this.usable = usable;
	}
    
}