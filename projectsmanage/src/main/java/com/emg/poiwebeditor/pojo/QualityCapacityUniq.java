package com.emg.poiwebeditor.pojo;

public class QualityCapacityUniq {
	Integer tasktype;
	Long projectid;
	Integer userid;
	Long errortype;

	public QualityCapacityUniq(Integer tasktype, Long projectid, Integer userid,Long errortype) {
		this.tasktype = tasktype;
		this.projectid = projectid;
		this.userid = userid;
		this.errortype = errortype;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (obj instanceof QualityCapacityUniq) {
			QualityCapacityUniq another = (QualityCapacityUniq) obj;
			return another.tasktype.equals(this.tasktype) && another.projectid.equals(this.projectid)
					&& another.userid.equals(this.userid) && another.errortype.equals(this.errortype);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (tasktype * ((int) (projectid ^ (projectid >>> 32))) * userid * ((int) (errortype ^ (errortype >>> 32))));
	}
}
