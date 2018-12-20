package com.emg.projectsmanage.pojo;

public class ProjectsTaskCountUniq {
	Integer userid;
	Integer roleid;
	Integer systemid;
	Long projectid;

	public ProjectsTaskCountUniq(Integer userid, Integer roleid, Integer systemid, Long projectid) {
		this.userid = userid;
		this.roleid = roleid;
		this.systemid = systemid;
		this.projectid = projectid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (obj instanceof ProjectsTaskCountUniq) {
			ProjectsTaskCountUniq another = (ProjectsTaskCountUniq) obj;
			return another.userid.equals(this.userid) && another.roleid.equals(this.roleid)
					&& another.systemid.equals(this.systemid) && another.projectid.equals(this.projectid);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (userid * roleid * systemid * ((int) (projectid ^ (projectid >>> 32))));
	}
}
