package com.example.recipesforlife.controllers;

import java.util.ArrayList;

/**
 * Bean stores recipe information
 * @author Kari
 *
 */
public class recipeBean {
	String name;
	int id;
	String desc;
	String recipeBook;
	String prep;
	String serves;
	String cooking;
	String changeTime;
	String uniqueid;
	ArrayList<String> ingredIdList;
	public ArrayList<String> getIngredIdList() {
		return ingredIdList;
	}
	public void setIngredIdList(ArrayList<String> ingredIdList) {
		this.ingredIdList = ingredIdList;
	}
	public ArrayList<String> getPrepIdList() {
		return prepIdList;
	}
	public void setPrepIdList(ArrayList<String> prepIdList) {
		this.prepIdList = prepIdList;
	}
	ArrayList<String> prepIdList;
	
	public String getUniqueid() {
		return uniqueid;
	}
	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}
	public String getChangeTime() {
		return changeTime;
	}
	public void setChangeTime(String changeTime) {
		this.changeTime = changeTime;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	String addedBy;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	String updateTime;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getRecipeBook() {
		return recipeBook;
	}
	public void setRecipeBook(String recipeBook) {
		this.recipeBook = recipeBook;
	}
	public String getPrep() {
		return prep;
	}
	public void setPrep(String prep) {
		this.prep = prep;
	}
	public String getServes() {
		return serves;
	}
	public void setServes(String serves) {
		this.serves = serves;
	}
	public String getCooking() {
		return cooking;
	}
	public void setCooking(String cooking) {
		this.cooking = cooking;
	}
	

}
