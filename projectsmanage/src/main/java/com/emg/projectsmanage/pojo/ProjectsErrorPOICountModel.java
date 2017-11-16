package com.emg.projectsmanage.pojo;

public class ProjectsErrorPOICountModel {
    private Long id;

    private Long projectid;

    private Integer userid;

    private String username;

    private Integer roleid;

    private String rolename;

    private String starttime;

    private String endtime;

    private Integer count;

    private Integer countErrorA;

    private Integer countErrorB;

    private Integer countErrorC;

    private String createtime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename == null ? null : rolename.trim();
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCountErrorA() {
        return countErrorA;
    }

    public void setCountErrorA(Integer countErrorA) {
        this.countErrorA = countErrorA;
    }

    public Integer getCountErrorB() {
        return countErrorB;
    }

    public void setCountErrorB(Integer countErrorB) {
        this.countErrorB = countErrorB;
    }

    public Integer getCountErrorC() {
        return countErrorC;
    }

    public void setCountErrorC(Integer countErrorC) {
        this.countErrorC = countErrorC;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}