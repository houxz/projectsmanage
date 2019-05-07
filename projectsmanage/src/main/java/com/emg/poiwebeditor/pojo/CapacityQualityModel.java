package com.emg.poiwebeditor.pojo;

import java.util.Date;

public class CapacityQualityModel {
    private Long id;

    private Integer tasktype;

    private Long processid;

    private String processname;

    private Long projectid;

    private Long errortype;

    private Integer userid;

    private String username;

    private Integer roleid;

    private String time;

    private Byte iswork;

    private Long count;

    private Long errorcount;

    private Long visualerrorcount;

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

    public Long getProcessid() {
        return processid;
    }

    public void setProcessid(Long processid) {
        this.processid = processid;
    }

    public String getProcessname() {
        return processname;
    }

    public void setProcessname(String processname) {
        this.processname = processname == null ? null : processname.trim();
    }

    public Long getProjectid() {
        return projectid;
    }

    public void setProjectid(Long projectid) {
        this.projectid = projectid;
    }

    public Long getErrortype() {
        return errortype;
    }

    public void setErrortype(Long errortype) {
        this.errortype = errortype;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
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

    public Byte getIswork() {
        return iswork;
    }

    public void setIswork(Byte iswork) {
        this.iswork = iswork;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getErrorcount() {
        return errorcount;
    }

    public void setErrorcount(Long errorcount) {
        this.errorcount = errorcount;
    }

    public Long getVisualerrorcount() {
        return visualerrorcount;
    }

    public void setVisualerrorcount(Long visualerrorcount) {
        this.visualerrorcount = visualerrorcount;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}