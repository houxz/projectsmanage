package com.emg.projectsmanage.pojo;

public class CapacityUniq {
	Integer tasktype;
	Long projectid;
	Integer userid;

	public CapacityUniq(Integer tasktype, Long projectid, Integer userid) {
		this.tasktype = tasktype;
		this.projectid = projectid;
		this.userid = userid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (obj instanceof CapacityUniq) {
			CapacityUniq another = (CapacityUniq) obj;
			return another.tasktype.equals(this.tasktype) && another.projectid.equals(this.projectid)
					&& another.userid.equals(this.userid);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (tasktype * ((int) (projectid ^ (projectid >>> 32))) * userid);
	}
}
