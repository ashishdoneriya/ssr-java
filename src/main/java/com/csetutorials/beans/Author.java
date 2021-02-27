package com.csetutorials.beans;

public class Author {

	private String name;

	private String username;

	private String description;

	private String imageUrl;

	private SocialMediaLinks socialMediaLinks;

	private String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SocialMediaLinks getSocialMediaLinks() {
		return socialMediaLinks;
	}

	public void setSocialMediaLinks(SocialMediaLinks socialMediaLinks) {
		this.socialMediaLinks = socialMediaLinks;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
