package com.example.recipesforlife.controllers;

/**
 * Stores an images details
 * @author Kari
 *
 */
public class ImageBean {
	byte[] image; //byte array of image
	String uniqueid; //images uniqueid

	public String getUniqueid() {
		return uniqueid;
	}

	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
}
