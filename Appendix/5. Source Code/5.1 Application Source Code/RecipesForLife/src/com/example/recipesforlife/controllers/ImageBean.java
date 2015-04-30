package com.example.recipesforlife.controllers;

/**
 * Stores image details
 * @author Kari
 *
 */
public class ImageBean {
	private byte[] image; 		//byte array of image
	private String uniqueid; 	//images uniqueid

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
