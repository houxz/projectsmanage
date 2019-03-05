package com.emg.projectsmanage.pojo;

import java.io.Serializable;

/**
 * 附属表产能统计类
 * @author liuniu
 *
 */
public class AttachCheckCapacityModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3030156661180784945L;
	private long id;
	private long userid;
	private String username;
	private String countdate;
	private int projectTypeCheck;
	private int roleType;
	private double efficiency;
	/**
	 * 校正量
	 */
	private int checkCount;
	
	private int lostDirection;
	  private int makeMoreDirection;
	  private int endRoadDirection;
	  private int infoDirection;
	  private int exitCodeDirection;
	  private int exitDirection;
	  private int unknownDirection;
	  private int lostLane;
	  private int makeMoreLane;
	  private int turnLane;
	  private int endRoadLane;
	  private int innerLinkLane;
	  private int unknownLane;
	  private int lostSceneJunctionview;
	  private int lostPatternJunctionview;
	  private int makeMoreSceneJunctionview;
	  private int makeMorePatternJunctionview;
	  private int pictureTypeSceneJunctionview;
	  private int pictureTypePatternJunctionview;
	  private int arrowSceneJunctionview;
	  private int arrowPatternJunctionview;
	  private int endRoadSceneJunctionview;
	  private int endRoadPatternJunctionview;
	  private int pictureChoiceSceneJunctionview;
	  private int pictureChoicePatternJunctionview;
	  private int unknownJunctionview;
	  
	  private int worktime;
	/**
	 * 错误量
	 */
	private int errorCount;
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public int getCheckCount() {
		return checkCount;
	}
	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}
	public int getLostDirection() {
		return lostDirection;
	}
	public void setLostDirection(int lostDirection) {
		this.lostDirection = lostDirection;
	}
	public int getMakeMoreDirection() {
		return makeMoreDirection;
	}
	public void setMakeMoreDirection(int makeMoreDirection) {
		this.makeMoreDirection = makeMoreDirection;
	}
	public int getEndRoadDirection() {
		return endRoadDirection;
	}
	public void setEndRoadDirection(int endRoadDirection) {
		this.endRoadDirection = endRoadDirection;
	}
	public int getInfoDirection() {
		return infoDirection;
	}
	public void setInfoDirection(int infoDirection) {
		this.infoDirection = infoDirection;
	}
	public int getExitCodeDirection() {
		return exitCodeDirection;
	}
	public void setExitCodeDirection(int exitCodeDirection) {
		this.exitCodeDirection = exitCodeDirection;
	}
	public int getExitDirection() {
		return exitDirection;
	}
	public void setExitDirection(int exitDirection) {
		this.exitDirection = exitDirection;
	}
	public int getUnknownDirection() {
		return unknownDirection;
	}
	public void setUnknownDirection(int unknownDirection) {
		this.unknownDirection = unknownDirection;
	}
	public int getLostLane() {
		return lostLane;
	}
	public void setLostLane(int lostLane) {
		this.lostLane = lostLane;
	}
	public int getMakeMoreLane() {
		return makeMoreLane;
	}
	public void setMakeMoreLane(int makeMoreLane) {
		this.makeMoreLane = makeMoreLane;
	}
	public int getTurnLane() {
		return turnLane;
	}
	public void setTurnLane(int turnLane) {
		this.turnLane = turnLane;
	}
	public int getEndRoadLane() {
		return endRoadLane;
	}
	public void setEndRoadLane(int endRoadLane) {
		this.endRoadLane = endRoadLane;
	}
	public int getInnerLinkLane() {
		return innerLinkLane;
	}
	public void setInnerLinkLane(int innerLinkLane) {
		this.innerLinkLane = innerLinkLane;
	}
	public int getUnknownLane() {
		return unknownLane;
	}
	public void setUnknownLane(int unknownLane) {
		this.unknownLane = unknownLane;
	}
	public int getLostSceneJunctionview() {
		return lostSceneJunctionview;
	}
	public void setLostSceneJunctionview(int lostSceneJunctionview) {
		this.lostSceneJunctionview = lostSceneJunctionview;
	}
	public int getLostPatternJunctionview() {
		return lostPatternJunctionview;
	}
	public void setLostPatternJunctionview(int lostPatternJunctionview) {
		this.lostPatternJunctionview = lostPatternJunctionview;
	}
	public int getMakeMoreSceneJunctionview() {
		return makeMoreSceneJunctionview;
	}
	public void setMakeMoreSceneJunctionview(int makeMoreSceneJunctionview) {
		this.makeMoreSceneJunctionview = makeMoreSceneJunctionview;
	}
	public int getMakeMorePatternJunctionview() {
		return makeMorePatternJunctionview;
	}
	public void setMakeMorePatternJunctionview(int makeMorePatternJunctionview) {
		this.makeMorePatternJunctionview = makeMorePatternJunctionview;
	}
	public int getPictureTypeSceneJunctionview() {
		return pictureTypeSceneJunctionview;
	}
	public void setPictureTypeSceneJunctionview(int pictureTypeSceneJunctionview) {
		this.pictureTypeSceneJunctionview = pictureTypeSceneJunctionview;
	}
	public int getPictureTypePatternJunctionview() {
		return pictureTypePatternJunctionview;
	}
	public void setPictureTypePatternJunctionview(int pictureTypePatternJunctionview) {
		this.pictureTypePatternJunctionview = pictureTypePatternJunctionview;
	}
	public int getArrowSceneJunctionview() {
		return arrowSceneJunctionview;
	}
	public void setArrowSceneJunctionview(int arrowSceneJunctionview) {
		this.arrowSceneJunctionview = arrowSceneJunctionview;
	}
	public int getArrowPatternJunctionview() {
		return arrowPatternJunctionview;
	}
	public void setArrowPatternJunctionview(int arrowPatternJunctionview) {
		this.arrowPatternJunctionview = arrowPatternJunctionview;
	}
	public int getEndRoadSceneJunctionview() {
		return endRoadSceneJunctionview;
	}
	public void setEndRoadSceneJunctionview(int endRoadSceneJunctionview) {
		this.endRoadSceneJunctionview = endRoadSceneJunctionview;
	}
	public int getEndRoadPatternJunctionview() {
		return endRoadPatternJunctionview;
	}
	public void setEndRoadPatternJunctionview(int endRoadPatternJunctionview) {
		this.endRoadPatternJunctionview = endRoadPatternJunctionview;
	}
	public int getPictureChoiceSceneJunctionview() {
		return pictureChoiceSceneJunctionview;
	}
	public void setPictureChoiceSceneJunctionview(int pictureChoiceSceneJunctionview) {
		this.pictureChoiceSceneJunctionview = pictureChoiceSceneJunctionview;
	}
	public int getPictureChoicePatternJunctionview() {
		return pictureChoicePatternJunctionview;
	}
	public void setPictureChoicePatternJunctionview(int pictureChoicePatternJunctionview) {
		this.pictureChoicePatternJunctionview = pictureChoicePatternJunctionview;
	}
	public int getUnknownJunctionview() {
		return unknownJunctionview;
	}
	public void setUnknownJunctionview(int unknownJunctionview) {
		this.unknownJunctionview = unknownJunctionview;
	}
	public int getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	public int getProjectTypeCheck() {
		return projectTypeCheck;
	}
	public void setProjectTypeCheck(int projectTypeCheck) {
		this.projectTypeCheck = projectTypeCheck;
	}
	public int getWorktime() {
		return worktime;
	}
	public void setWorktime(int worktime) {
		this.worktime = worktime;
	}
	
}
