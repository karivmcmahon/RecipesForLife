package com.example.recipesforlife.controllers;

/**
 * Controller for contributor
 * @author Kari
 *
 */
public class contributerBean {
	String account;
	String bookUniqId;
	String updateTime;
	String progress;
	String changeTime;
	
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
