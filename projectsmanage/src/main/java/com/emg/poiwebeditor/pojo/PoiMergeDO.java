package com.emg.poiwebeditor.pojo;

import org.apache.ibatis.type.Alias;

@Alias("poimerge")
public class PoiMergeDO extends EntityPrimaryKeyDO{
	
	private static final long serialVersionUID = -348118479187698392L;
	private int srcType= -1;
	private String srcInnerId;
	private long oid = -1L;
	private String qid;
	private long errorType= -1L;
	private String ver;
	/*private Boolean isDel;*/
	private String createTime;
	private long taskId= -1L;
	private String importTime;
	public int getSrcType() {
		return srcType;
	}
	public void setSrcType(int srcType) {
		this.srcType = srcType;
	}
	public String getSrcInnerId() {
		return srcInnerId;
	}
	public void setSrcInnerId(String srcInnerId) {
		this.srcInnerId = srcInnerId;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public long getErrorType() {
		return errorType;
	}
	public void setErrorType(long errorType) {
		this.errorType = errorType;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	public String getImportTime() {
		return importTime;
	}
	public void setImportTime(String importTime) {
		this.importTime = importTime;
	}

}
