package com.example.recipesforlife.controllers;

/**
 * Bean stores preperation info
 * @author Kari
 *
 */
public class PreperationBean  {
	private int id; 			//preps row id in db
	private int prepNum; 		//preps step number
	private String preperation; //preperation details
	private String uniqueid; 	//prep uniqueid
	private String progress; 	//prep progress in database 
	private String recipeid; 	//recipe id for the prep 
	private String updateTime; 	//update time 

	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getRecipeid() {
		return recipeid;
	}
	public void setRecipeid(String recipeid) {
		this.recipeid = recipeid;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getUniqueid() {
		return uniqueid;
	}
	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPrepNum() {
		return prepNum;
	}
	public void setPrepNum(int prepNum) {
		this.prepNum = prepNum;
	}
	public String getPreperation() {
		return preperation;
	}
	public void setPreperation(String preperation) {
		this.preperation = preperation;
	}

}
