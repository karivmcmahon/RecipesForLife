package com.example.recipesforlife.controllers;

import java.util.ArrayList;

/**
 * Bean stores recipe information
 * @author Kari
 *
 */
public class recipeBean {
	String name;
	ArrayList<String> ingredients;
	ArrayList<String> stepNum;
	int id;
	ArrayList<String> steps;
	ArrayList<String> notes;
	ArrayList<String> values;
	ArrayList<String> amount;
	String desc;
	String recipeBook;
	String prep;
	String serves;
	String cooking;
	
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
	public ArrayList<String> getStepNum() {
		return stepNum;
	}
	public void setStepNum(ArrayList<String> stepNum) {
		this.stepNum = stepNum;
	}
	public ArrayList<String> getSteps() {
		return steps;
	}
	public void setSteps(ArrayList<String> steps) {
		this.steps = steps;
	}
	
	
	public ArrayList<String> getIngredients() {
		return ingredients;
	}
	public void setIngredients(ArrayList<String> ingredients) {
		this.ingredients = ingredients;
	}
	public ArrayList<String> getNotes() {
		return notes;
	}
	public void setNotes(ArrayList<String> notes) {
		this.notes = notes;
	}
	public ArrayList<String> getValues() {
		return values;
	}
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
	public ArrayList<String> getAmount() {
		return amount;
	}
	public void setAmount(ArrayList<String> amount) {
		this.amount = amount;
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
