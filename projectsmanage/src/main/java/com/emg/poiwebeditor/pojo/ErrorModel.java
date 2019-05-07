package com.emg.poiwebeditor.pojo;

import java.util.Date;

public class ErrorModel {
    private Long id;

    private Long taskid; 

    private Long blockid;

    private Long featureid;

    private Integer layerid;

    private Long editver;

    private Date updatetime;

    private String field_name;

    private String editvalue;

    private String checkvalue;

    private String qid;

    private Long errortype;

    private Integer level;

    private Integer modifystate;

    private Integer errorstate;

    private String errorremark;

    private Integer checkroleid;

    private Integer changeroleid;

    private String md5;

    private Long batchid;

    private Integer isrelated;

    private String sbound;

    private Object bound;
    
    //add by lianhr begin 2019/02/25
    private Long countnum;
    //add by lianhr end

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskid() {
        return taskid;
    }

    public void setTaskid(Long taskid) {
        this.taskid = taskid;
    }

    public Long getBlockid() {
        return blockid;
    }

    public void setBlockid(Long blockid) {
        this.blockid = blockid;
    }

    public Long getFeatureid() {
        return featureid;
    }

    public void setFeatureid(Long featureid) {
        this.featureid = featureid;
    }

    public Integer getLayerid() {
        return layerid;
    }

    public void setLayerid(Integer layerid) {
        this.layerid = layerid;
    }

    public Long getEditver() {
        return editver;
    }

    public void setEditver(Long editver) {
        this.editver = editver;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getFieldName() {
        return field_name;
    }

    public void setFieldName(String fieldName) {
        this.field_name = fieldName == null ? null : fieldName.trim();
    }

    public String getEditvalue() {
        return editvalue;
    }

    public void setEditvalue(String editvalue) {
        this.editvalue = editvalue == null ? null : editvalue.trim();
    }

    public String getCheckvalue() {
        return checkvalue;
    }

    public void setCheckvalue(String checkvalue) {
        this.checkvalue = checkvalue == null ? null : checkvalue.trim();
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid == null ? null : qid.trim();
    }

    public Long getErrortype() {
        return errortype;
    }

    public void setErrortype(Long errortype) {
        this.errortype = errortype;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getModifystate() {
        return modifystate;
    }

    public void setModifystate(Integer modifystate) {
        this.modifystate = modifystate;
    }

    public Integer getErrorstate() {
        return errorstate;
    }

    public void setErrorstate(Integer errorstate) {
        this.errorstate = errorstate;
    }

    public String getErrorremark() {
        return errorremark;
    }

    public void setErrorremark(String errorremark) {
        this.errorremark = errorremark == null ? null : errorremark.trim();
    }

    public Integer getCheckroleid() {
        return checkroleid;
    }

    public void setCheckroleid(Integer checkroleid) {
        this.checkroleid = checkroleid;
    }

    public Integer getChangeroleid() {
        return changeroleid;
    }

    public void setChangeroleid(Integer changeroleid) {
        this.changeroleid = changeroleid;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5 == null ? null : md5.trim();
    }

    public Long getBatchid() {
        return batchid;
    }

    public void setBatchid(Long batchid) {
        this.batchid = batchid;
    }

    public Integer getIsrelated() {
        return isrelated;
    }

    public void setIsrelated(Integer isrelated) {
        this.isrelated = isrelated;
    }

    public String getSbound() {
        return sbound;
    }

    public void setSbound(String sbound) {
        this.sbound = sbound == null ? null : sbound.trim();
    }

    public Object getBound() {
        return bound;
    }

    public void setBound(Object bound) {
        this.bound = bound;
    }
    
    //add by lianhr begin 2019/02/25
    public Long getCountnum() {
        return countnum;
    }

    public void setCountnum(Long countnum) {
        this.countnum = countnum;
    }
    //add by lianhr end
}