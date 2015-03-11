package com.example.recipesforlife.controllers;

/**
 * Stores an images details
 * @author Kari
 *
 */
public class imageBean {
	byte[] image;
	String uniqueid;

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
