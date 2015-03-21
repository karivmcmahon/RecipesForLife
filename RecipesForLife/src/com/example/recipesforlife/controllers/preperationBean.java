package com.example.recipesforlife.controllers;

/**
 * Bean stores preperation info
 * @author Kari
 *
 */
public class PreperationBean  {
	int id; //preps row id in db
	int prepNum; //preps step number
	String preperation; //preperation details
	String uniqueid; //prep uniqueid

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
