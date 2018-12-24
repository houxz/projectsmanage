package com.emg.projectsmanage.pojo;

public class WorkTasksUniq {
	Integer userid;
	Integer roleid;
	Long processid;

	public WorkTasksUniq(Integer userid, Integer roleid, Long projectid) {
		this.userid = userid;
		this.roleid = roleid;
		this.processid = projectid;
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
					&& another.processid.equals(this.processid);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (userid * roleid * ((int) (processid ^ (processid >>> 32))));
	}
}
