package com.emg.poiwebeditor.pojo;

import java.util.Date;

public class CapacityTaskModel {
    private Long id;

    private Integer processtype;

    private String time;

    private Integer state;

    private Date starttime;

    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProcesstype() {
        return processtype;
    }

    public void setProcesstype(Integer processtype) {
        this.processtype = processtype;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time == null ? null : time.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}