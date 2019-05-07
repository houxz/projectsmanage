package com.emg.poiwebeditor.pojo;

import java.util.Date;

public class CycleModel {
	public static int ISEXIT_FALSE = 1;
	public static int ISEXIT_TRUE = 0;
    private Long id;

    private Long userid;

    private String username;

    private String logindate;

    private Date logintime;

    private Date logouttime;

    private Integer timecount;
    
    private Integer isexit;
    
    private int projecttype;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getLogindate() {
        return logindate;
    }

    public void setLogindate(String logindate) {
        this.logindate = logindate == null ? null : logindate.trim();
    }

    public Date getLogintime() {
        return logintime;
    }

    public void setLogintime(Date logintime) {
        this.logintime = logintime;
    }

    public Date getLogouttime() {
        return logouttime;
    }

    public void setLogouttime(Date logouttime) {
        this.logouttime = logouttime;
    }

    public Integer getTimecount() {
        return timecount;
    }

    public void setTimecount(Integer timecount) {
        this.timecount = timecount;
    }

	public Integer getIsexit() {
		return isexit;
	}

	public void setIsexit(Integer isexit) {
		this.isexit = isexit;
	}

	public int getProjecttype() {
		return projecttype;
	}

	public void setProjecttype(int projecttype) {
		this.projecttype = projecttype;
	}
}