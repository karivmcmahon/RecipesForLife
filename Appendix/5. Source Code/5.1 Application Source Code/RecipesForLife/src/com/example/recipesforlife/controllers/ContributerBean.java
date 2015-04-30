package com.example.recipesforlife.controllers;

/**
 * Controller for handling a contributors details
 * @author Kari
 *
 */
public class ContributerBean {
	private String account; 	//users account - email
	private String bookUniqId; 	//cookbooks uniqueid
	private String updateTime; 	//time it was added 
	private String progress; 	//contributors progress
	private String changeTime; 	//time it was updated

	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getChangeTime() {
		return changeTime;
	}
	public void setChangeTime(String changeTime) {
		this.changeTime = changeTime;
	}

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getBookUniqId() {
		return bookUniqId;
	}
	public void setBookUniqId(String bookUniqId) {
		this.bookUniqId = bookUniqId;
	}


}
