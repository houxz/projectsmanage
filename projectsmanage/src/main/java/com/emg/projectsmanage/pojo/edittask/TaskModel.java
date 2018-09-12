package com.emg.projectsmanage.pojo.edittask;

import java.util.Date;

public class TaskModel {
    private Long id;

    private Long blockid;

    private String name;

    private Integer tasktype;

    private Integer flowtype;

    private Integer state;

    private Integer process;

    private Integer editid;

    private Integer checkid;

    private Long projectid;

    private String referdata;

    private Long batchid;

    private String editlist;

    private Integer priority;

    private Integer rank;

    private Date operatetime;

    private Integer systype;

    private Date starttime;

    private Date endtime;

    private String time;

    private String ip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBlockid() {
        return blockid;
    }

    public void setBlockid(Long blockid) {
        this.blockid = blockid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getTasktype() {
        return tasktype;
    }

    public void setTasktype(Integer tasktype) {
        this.tasktype = tasktype;
    }

    public Integer getFlowtype() {
        return flowtype;
    }

    public void setFlowtype(Integer flowtype) {
        this.flowtype = flowtype;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getProcess() {
        return process;
    }

    public void setProcess(Integer process) {
        this.process = process;
    }

    public Integer getEditid() {
        return editid;
    }

    public void setEditid(Integer editid) {
        this.editid = editid;
    }

    public Integer getCheckid() {
        return checkid;
    }

    public void setCheckid(Integer checkid) {
        this.checkid = checkid;
    }

    public Long getProjectid() {
        return projectid;
    }

    public void setProjectid(Long projectid) {
        this.projectid = projectid;
    }

    public String getReferdata() {
        return referdata;
    }

    public void setReferdata(String referdata) {
        this.referdata = referdata == null ? null : referdata.trim();
    }

    public Long getBatchid() {
        return batchid;
    }

    public void setBatchid(Long batchid) {
        this.batchid = batchid;
    }

    public String getEditlist() {
        return editlist;
    }

    public void setEditlist(String editlist) {
        this.editlist = editlist == null ? null : editlist.trim();
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Date getOperatetime() {
        return operatetime;
    }

    public void setOperatetime(Date operatetime) {
        this.operatetime = operatetime;
    }

    public Integer getSystype() {
        return systype;
    }

    public void setSystype(Integer systype) {
        this.systype = systype;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time == null ? null : time.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }
}