package com.example.recipesforlife.controllers;

/**
 * This controller stores review information for recipes
 * @author Kari
 *
 */
public class ReviewBean {

	private String comment; 		//the review comment
	private String user; 			//who the review is by
	private int recipeid; 			//row id of recipe in db
	private String recipeuniqueid; 	//recipe the review relates to - unique id
	private String updateTime; 		//time review was added

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
