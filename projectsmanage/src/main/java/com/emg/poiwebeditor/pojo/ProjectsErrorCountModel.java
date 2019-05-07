package com.emg.poiwebeditor.pojo;

public class ProjectsErrorCountModel {
    private Long id;

    private Long projectid;

    private Integer userid;

    private String username;

    private Integer roleid;

    private String rolename;

    private String starttime;

    private String endtime;

    private String fieldname;

    private Long errortype;

    private Integer modifystate;

    private Integer errorstate;

    private Integer count;

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

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname == null ? null : fieldname.trim();
    }

    public Long getErrortype() {
        return errortype;
    }

    public void setErrortype(Long errortype) {
        this.errortype = errortype;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}