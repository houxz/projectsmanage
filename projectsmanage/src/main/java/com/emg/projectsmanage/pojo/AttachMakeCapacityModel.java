package com.emg.projectsmanage.pojo;

import java.io.Serializable;

/**
 * 附属表产能统计类
 * @author liuniu
 *
 */
public class AttachMakeCapacityModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3030156661180784945L;
	private long id;
	private long userid;
	private String username;
	private String countdate;
	private int laneCreate;
	private int laneUpdate;
	private int laneDelete;
	private int directionCreate;
	private int directionUpdate;
	private int directionDelete;
	private int junctionviewCreate;
	private int junctionviewUpdate;
	private int junctionviewDelete;	
	private int makeErrorCount;
	private String correctRate;
	private int projectType;
	private int roleType;
	private double efficiency;
	private int worktime;
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCountdate() {
		return countdate;
	}
	public void setCountdate(String countdate) {
		this.countdate = countdate;
	}
	/*public int getMakeErrorCount() {
		return makeErrorCount;
	}
	public void setMakeErrorCount(int makeErrorCount) {
		this.makeErrorCount = makeErrorCount;
	}
	public int getCheckErrorCount() {
		return checkErrorCount;
	}
	public void setCheckErrorCount(int checkErrorCount) {
		this.checkErrorCount = checkErrorCount;
	}
	public int getCheckCount() {
		return checkCount;
	}
	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}*/
	public int getLaneCreate() {
		return laneCreate;
	}
	public void setLaneCreate(int laneCreate) {
		this.laneCreate = laneCreate;
	}
	public int getLaneUpdate() {
		return laneUpdate;
	}
	public void setLaneUpdate(int laneUpdate) {
		this.laneUpdate = laneUpdate;
	}
	public int getLaneDelete() {
		return laneDelete;
	}
	public void setLaneDelete(int laneDelete) {
		this.laneDelete = laneDelete;
	}
	public int getDirectionCreate() {
		return directionCreate;
	}
	public void setDirectionCreate(int directionCreate) {
		this.directionCreate = directionCreate;
	}
	public int getDirectionUpdate() {
		return directionUpdate;
	}
	public void setDirectionUpdate(int directionUpdate) {
		this.directionUpdate = directionUpdate;
	}
	public int getDirectionDelete() {
		return directionDelete;
	}
	public void setDirectionDelete(int directionDelete) {
		this.directionDelete = directionDelete;
	}
	public int getJunctionviewCreate() {
		return junctionviewCreate;
	}
	public void setJunctionviewCreate(int junctionviewCreate) {
		this.junctionviewCreate = junctionviewCreate;
	}
	public int getJunctionviewUpdate() {
		return junctionviewUpdate;
	}
	public void setJunctionviewUpdate(int junctionviewUpdate) {
		this.junctionviewUpdate = junctionviewUpdate;
	}
	public int getJunctionviewDelete() {
		return junctionviewDelete;
	}
	public void setJunctionviewDelete(int junctionviewDelete) {
		this.junctionviewDelete = junctionviewDelete;
	}
	public int getProjectType() {
		return projectType;
	}
	public void setProjectType(int projectType) {
		this.projectType = projectType;
	}
	public int getRoleType() {
		return roleType;
	}
	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}
	public double getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(double efficiency) {
		this.efficiency = efficiency;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getMakeErrorCount() {
		return makeErrorCount;
	}
	public void setMakeErrorCount(int makeErrorCount) {
		this.makeErrorCount = makeErrorCount;
	}
	public int getWorktime() {
		return worktime;
	}
	public void setWorktime(int worktime) {
		this.worktime = worktime;
	}
	public String getCorrectRate() {
		return correctRate;
	}
	public void setCorrectRate(String correctRate) {
		this.correctRate = correctRate;
	}

}
