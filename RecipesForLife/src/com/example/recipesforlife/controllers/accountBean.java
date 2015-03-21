package com.example.recipesforlife.controllers;

/**
 * Controller for Account Details
 * @author Kari
 *
 */
public class AccountBean 
{
	private int id; //row id in db
	private String email; //users email
	private String password; //users password - in hashed form
	private String updateTime; //time it was added

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

}
