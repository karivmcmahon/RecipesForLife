package com.example.recipesforlife.controllers;

/**
 * Bean that stores ingredient information
 * @author Kari
 *
 */
public class ingredientBean {
	String name;
	int ingredId; 
	int amount;
	String value;
	String note;
	String uniqueid;
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
