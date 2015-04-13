package com.example.recipesforlife.controllers;

/**
 * Bean that stores ingredient information
 * @author Kari
 *
 */
public class IngredientBean {
	String name; //name of ingredient
	int ingredId;  //id of row in db
	int amount; //amount of this ingredient
	String value; //ingredients unit
	String note; //notes relating to ingred
	String uniqueid; //ingreds uniqueid
	String progress; //holds the progress of this data
	String recipeid; //holds the id of the recipe the ingredient relates
	String updateTime; //holds the update time for the ingredient
	
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



}
