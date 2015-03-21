package com.example.recipesforlife.controllers;

import java.util.ArrayList;

/**
 * Bean stores recipe information
 * @author Kari
 *
 */
public class RecipeBean {
	String name; //name of recipe
	int id; //recipes id in db
	String desc; //description of recipe
	String recipeBook; //unique id of recipe book which contains this recipe
	String prep; //recipe preparation time
	String serves; //recipe serves number
	String cooking; //recipe cooking time
	String changeTime; //time when recipe was updated 
	String uniqueid; //recipe unique id
	byte image[]; //image relating to recipe
	String progress; //progress of recipe e.g. added, deleted
	String cusine; //cuisine type of recipe
	String difficulty;  //difficulty of recipe
	String tips; //recipe tips
	String dietary; //recipe dietary requirements

	
	public String getCusine() {
		return cusine;
	}
	public void setCusine(String cusine) {
		this.cusine = cusine;
	}
	public String getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}
	public String getTips() {
		return tips;
	}
	public void setTips(String tips) {
		this.tips = tips;
	}
	public String getDietary() {
		return dietary;
	}
	public void setDietary(String dietary) {
		this.dietary = dietary;
	}

	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
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
