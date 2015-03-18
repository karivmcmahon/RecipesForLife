package com.example.recipesforlife.controllers;

public class reviewBean {
	
	String comment;
	String user;
	int recipeid;
	String recipeuniqueid;
	String updateTime;

	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getRecipeuniqueid() {
		return recipeuniqueid;
	}
	public void setRecipeuniqueid(String recipeuniqueid) {
		this.recipeuniqueid = recipeuniqueid;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getRecipeid() {
		return recipeid;
	}
	public void setRecipeid(int recipeid) {
		this.recipeid = recipeid;
	}
	
}
