package com.emg.poiwebeditor.pojo;

public class ModifiedlogDO extends EntityPrimaryKeyDO{
	/**
	 * 
	 */
	private static final long serialVersionUID = -348118479187698392L;
	private int srcType= -1;
	private String srcInnerId;
	private long shapeId = -1L;
	private long keywordId = -1L;
	private long oid = -1L;
	private String k;
	private String oldValue;
	private String newValue;
	private String createTime;
	private int flag;
	private String importTime;
	private boolean isDel = false;
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
	public long getShapeId() {
		return shapeId;
	}
	public void setShapeId(long shapeId) {
		this.shapeId = shapeId;
	}
	public long getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(long keywordId) {
		this.keywordId = keywordId;
	}
	public long getOid() {
		return oid;
	}
	public void setOid(long oid) {
		this.oid = oid;
	}
	public String getK() {
		return k;
	}
	public void setK(String k) {
		this.k = k;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getImportTime() {
		return importTime;
	}
	public void setImportTime(String importTime) {
		this.importTime = importTime;
	}
	public boolean isDel() {
		return isDel;
	}
	public void setDel(boolean isDel) {
		this.isDel = isDel;
	}
	
	 
}
