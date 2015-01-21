package com.example.recipesforlife.controllers;

public class preperationBean {
	int id;
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
	public int getPrepId() {
		return prepId;
	}
	public void setPrepId(int prepId) {
		this.prepId = prepId;
	}
	public int getRecipeId() {
		return recipeId;
	}
	public void setRecipeId(int recipeId) {
		this.recipeId = recipeId;
	}
	int prepNum;
	String preperation;
	//Connectors
	int prepId;
	int recipeId;
	

}
