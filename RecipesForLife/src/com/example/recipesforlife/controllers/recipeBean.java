package com.example.recipesforlife.controllers;

import java.util.ArrayList;

/**
 * Bean stores recipe information
 * @author Kari
 *
 */
public class RecipeBean {
	private String name; 					//name of recipe
	private int id; 						//recipes id in db
	private String desc; 					//description of recipe
	private String recipeBook; 				//unique id of recipe book which contains this recipe
	private String prep; 	   				//recipe preparation time
	private String serves; 	  				//recipe serves number
	private String cooking; 				//recipe cooking time
	private String changeTime;	 			//time when recipe was updated 
	private String uniqueid; 				//recipe unique id
	private byte image[]; 					//image relating to recipe
	private String progress; 				//progress of recipe e.g. added, deleted
	private String cusine; 					//cuisine type of recipe
	private String difficulty;  			//difficulty of recipe
	private String tips; 					//recipe tips
	private String dietary; 				//recipe dietary requirements
	private ArrayList<String> prepIdList; 	//list of prep ids
	private String addedBy; 				//who inserted
	private String updateTime; 				//time inserted

	
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
