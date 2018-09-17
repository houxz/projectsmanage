package com.emg.projectsmanage.pojo;

import java.util.Date;

public class CapacityModel {
    private Long id;

    private Integer tasktype;

    private Long processid;

    private String processname;

    private Long projectid;

    private Integer userid;

    private String username;

    private Integer roleid;

    private String time;

    private Integer errorcount = 0;

    private Integer taskcount = 0;

    private Integer modifypoi = 0;

    private Integer createpoi = 0;

    private Integer deletepoi = 0;

    private Integer confirmpoi = 0;

    private Integer visualerrorcount = 0;

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