package com.emg.projectsmanage.pojo;

import java.util.Date;

public class CapacityModel {
    private Long id;

    private Integer tasktype;

    private Integer userid;

    private Integer roleid;

    private String time;

    private Integer errorcount;

    private Integer taskcount;

    private Integer modifypoi;

    private Integer createpoi;

    private Integer deletepoi;

    private Integer confirmpoi;

    private Integer visualerrorcount;

    private Date createtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTasktype() {
        return tasktype;
    }

    public void setTasktype(Integer tasktype) {
        this.tasktype = tasktype;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time == null ? null : time.trim();
    }

    public Integer getErrorcount() {
        return errorcount;
    }

    public void setErrorcount(Integer errorcount) {
        this.errorcount = errorcount;
    }

    public Integer getTaskcount() {
        return taskcount;
    }

    public void setTaskcount(Integer taskcount) {
        this.taskcount = taskcount;
    }

    public Integer getModifypoi() {
        return modifypoi;
    }

    public void setModifypoi(Integer modifypoi) {
        this.modifypoi = modifypoi;
    }

    public Integer getCreatepoi() {
        return createpoi;
    }

    public void setCreatepoi(Integer createpoi) {
        this.createpoi = createpoi;
    }

    public Integer getDeletepoi() {
        return deletepoi;
    }

    public void setDeletepoi(Integer deletepoi) {
        this.deletepoi = deletepoi;
    }

    public Integer getConfirmpoi() {
        return confirmpoi;
    }

    public void setConfirmpoi(Integer confirmpoi) {
        this.confirmpoi = confirmpoi;
    }

    public Integer getVisualerrorcount() {
        return visualerrorcount;
    }

    public void setVisualerrorcount(Integer visualerrorcount) {
        this.visualerrorcount = visualerrorcount;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}