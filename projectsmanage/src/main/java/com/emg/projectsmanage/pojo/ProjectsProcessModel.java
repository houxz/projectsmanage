package com.emg.projectsmanage.pojo;

import java.util.HashMap;

public class ProjectsProcessModel {
	private String id;
	private Long processid;
	private Integer processtype;
	private String processname;
	private Long projectid;
	private Integer totaltask = 0;
	private Integer idletask = 0;
	private Integer edittask = 0;
	private Integer qctask = 0;
	private Integer checktask = 0;
	private Integer prepublishtask = 0;
	private Integer poiunexported = 0;
	private Integer completetask = 0;
	private Integer fielddatacount = 0;
	private Integer fielddatarest = 0;
	private Integer errorcount = 0;
	private Integer errorrest = 0;
	private String time;
	private HashMap<Integer, Integer> stageTaskMap = new HashMap<Integer, Integer>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getTotaltask() {
		return totaltask;
	}
	public void setTotaltask(Integer totaltask) {
		this.totaltask = totaltask;
	}
	public Integer getEdittask() {
		return edittask;
	}
	public void setEdittask(Integer edittask) {
		this.edittask = edittask;
	}
	public Integer getQctask() {
		return qctask;
	}
	public void setQctask(Integer qctask) {
		this.qctask = qctask;
	}
	public Integer getChecktask() {
		return checktask;
	}
	public void setChecktask(Integer checktask) {
		this.checktask = checktask;
	}
	public Integer getCompletetask() {
		return completetask;
	}
	public void setCompletetask(Integer completetask) {
		this.completetask = completetask;
	}
	public Integer getIdletask() {
		return idletask;
	}
	public void setIdletask(Integer idletask) {
		this.idletask = idletask;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Long getProcessid() {
		return processid;
	}
	public void setProcessid(Long processid) {
		this.processid = processid;
	}
	public Integer getProcesstype() {
		return processtype;
	}
	public void setProcesstype(Integer processtype) {
		this.processtype = processtype;
	}
	public Integer getFielddatacount() {
		return fielddatacount;
	}
	public void setFielddatacount(Integer fielddatacount) {
		this.fielddatacount = fielddatacount;
	}
	public Integer getErrorcount() {
		return errorcount;
	}
	public void setErrorcount(Integer errorcount) {
		this.errorcount = errorcount;
	}
	public String getProcessname() {
		return processname;
	}
	public void setProcessname(String processname) {
		this.processname = processname;
	}
	public Long getProjectid() {
		return projectid;
	}
	public void setProjectid(Long projectid) {
		this.projectid = projectid;
	}
	public Integer getFielddatarest() {
		return fielddatarest;
	}
	public void setFielddatarest(Integer fielddatarest) {
		this.fielddatarest = fielddatarest;
	}
	public Integer getErrorrest() {
		return errorrest;
	}
	public void setErrorrest(Integer errorrest) {
		this.errorrest = errorrest;
	}
	public HashMap<Integer, Integer> getStageTaskMap() {
		return stageTaskMap;
	}
	public Integer getStageTaskMapByStage(Integer stage) {
		if (this.stageTaskMap.containsKey(stage))
			return this.stageTaskMap.get(stage);
		else
			return 0;
	}
	public void setStageTaskMapByStage(Integer stage, Integer count) {
		this.stageTaskMap.put(stage, count);
	}
	public Integer getPrepublishtask() {
		return prepublishtask;
	}
	public void setPrepublishtask(Integer prepublishtask) {
		this.prepublishtask = prepublishtask;
	}
	public Integer getPoiunexported() {
		return poiunexported;
	}
	public void setPoiunexported(Integer poiunexported) {
		this.poiunexported = poiunexported;
	}
}
