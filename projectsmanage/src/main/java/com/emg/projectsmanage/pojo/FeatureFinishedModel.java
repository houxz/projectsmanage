package com.emg.projectsmanage.pojo;

import java.util.Date;

public class FeatureFinishedModel {
	private Long id;
	
	private Integer tasktype;

	private Long projectid;
	
	private Long featureid;

	private Integer userid;

	private Integer roleid;
	
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

	public Long getProjectid() {
		return projectid;
	}

	public void setProjectid(Long projectid) {
		this.projectid = projectid;
	}

	public Long getFeatureid() {
		return featureid;
	}

	public void setFeatureid(Long featureid) {
		this.featureid = featureid;
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

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof FeatureFinishedModel) {
			FeatureFinishedModel fetureFinieshed = (FeatureFinishedModel) o;
			return this.featureid.longValue() == (fetureFinieshed.featureid.longValue())
					&& this.projectid.longValue() == (fetureFinieshed.projectid.longValue())
					&& this.tasktype.intValue() == (fetureFinieshed.tasktype.intValue())
					&& this.userid.intValue() == (fetureFinieshed.userid.intValue())
					&& this.roleid.intValue() == (fetureFinieshed.roleid.intValue());
		}
		return super.equals(o);
	}
	
}
