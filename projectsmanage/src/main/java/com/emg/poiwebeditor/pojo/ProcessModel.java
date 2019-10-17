package com.emg.poiwebeditor.pojo;

public class ProcessModel {
    private Long id;

    private Integer type;

    private String name;

    private Integer priority;

    private Integer state;

    private Integer stage;

    private Integer stagestate;

    private String progress;

    private Integer userid;

    private String username;

    private String createtime;

    private String time;
    //poi点面项目类型 是20191015才加的
    //之前生成的项目都没有，存储在配置表
    //需要的时候需要单独查
    private Integer poiprojecttype;

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public Integer getStagestate() {
        return stagestate;
    }

    public void setStagestate(Integer stagestate) {
        this.stagestate = stagestate;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress == null ? null : progress.trim();
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

	public Integer getPoiprojecttype() {
		return poiprojecttype;
	}

	public void setPoiprojecttype(Integer poiprojecttype) {
		this.poiprojecttype = poiprojecttype;
	}
}