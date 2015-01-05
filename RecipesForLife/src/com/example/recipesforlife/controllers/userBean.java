package com.example.recipesforlife.controllers;

/**
 * Controller for user bean
 * @author Kari
 *
 */
public class userBean {
	String name;
	String country;
	String city;
	String bio;
	int id;
	String cookingInterest;
	
	public String getCookingInterest() {
		return cookingInterest;
	}
	public void setCookingInterest(String cookingInterest) {
		this.cookingInterest = cookingInterest;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	

}
