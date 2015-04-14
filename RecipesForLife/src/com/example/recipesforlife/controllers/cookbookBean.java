package com.example.recipesforlife.controllers;

/**
 * Controller for cookbook details
 * @author Kari
 *
 */
public class CookbookBean {
	private String name; 		//cookbooks name
	private String description; //cookbooks description
	private String privacy; 	//cookbooks privacy setting
	private String creator; 	// who created the cookbook
	private String uniqueid; 	//cookbooks uniqueid
	private byte[] image; 		//cookbooks image
	private String progress; 	//cookbooks progress e.g. added, deleted
	private String updateTime; 	//time inserted
	private String changeTime; 	//time changed

	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
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


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrivacy() {
		return privacy;
	}
	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getUniqueid() {
		return uniqueid;
	}
	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}


}
