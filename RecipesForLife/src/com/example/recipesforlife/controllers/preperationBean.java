package com.example.recipesforlife.controllers;

/**
 * Bean stores preperation info
 * @author Kari
 *
 */
public class preperationBean  {
	int id;
	int prepNum;
	String preperation;
	String uniqueid;
	
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
