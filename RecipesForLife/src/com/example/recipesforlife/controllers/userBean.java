package com.example.recipesforlife.controllers;

/**
 * Controller for user bean
 * @author Kari
 *
 */
public class UserBean {
	
	String name; //name of the user
	String country; //country they are in
	String city; //city they are in
	String bio; //something about themselves
	int id; //row id in db
	String cookingInterest; //users cooking interest
	String email; //users email

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
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
