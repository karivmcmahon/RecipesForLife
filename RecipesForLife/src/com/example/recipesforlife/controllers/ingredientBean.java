package com.example.recipesforlife.controllers;

public class ingredientBean {
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIngredId() {
		return ingredId;
	}
	public void setIngredId(int ingredId) {
		this.ingredId = ingredId;
	}
	public int getRecipeId() {
		return recipeId;
	}
	public void setRecipeId(int recipeId) {
		this.recipeId = recipeId;
	}
	public int getRecipeIngredId() {
		return recipeIngredId;
	}
	public void setRecipeIngredId(int recipeIngredId) {
		this.recipeIngredId = recipeIngredId;
	}
	public int getDetsConnectId() {
		return detsConnectId;
	}
	public void setDetsConnectId(int detsConnectId) {
		this.detsConnectId = detsConnectId;
	}
	public int getDetsConnectIngredId() {
		return detsConnectIngredId;
	}
	public void setDetsConnectIngredId(int detsConnectIngredId) {
		this.detsConnectIngredId = detsConnectIngredId;
	}
	public int getDetailsId() {
		return detailsId;
	}
	public void setDetailsId(int detailsId) {
		this.detailsId = detailsId;
	}
	public int getIngredDetsId() {
		return ingredDetsId;
	}
	public void setIngredDetsId(int ingredDetsId) {
		this.ingredDetsId = ingredDetsId;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	int ingredId; 
	//For recipe and ingred connection
	int recipeId;
	int recipeIngredId;
	//For connection between details and ingred
	int detsConnectId;
	int detsConnectIngredId;
	//For details
	int detailsId;
	int ingredDetsId;
	int amount;
	String value;
	String note;
	

}
