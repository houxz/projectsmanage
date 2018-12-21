package com.emg.projectsmanage.pojo;

public class WorkTasksUniq {
	Integer userid;
	Integer roleid;
	Integer systemid;
	Long projectid;

	public WorkTasksUniq(Integer userid, Integer roleid, Integer systemid, Long projectid) {
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
		if (obj instanceof WorkTasksUniq) {
			WorkTasksUniq another = (WorkTasksUniq) obj;
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
