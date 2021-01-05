package com.csetutorials.beans;

public class OrganizationInfo {

	private String name;

	private String logo;

	private SocialMediaLinks socialMediaLinks;

	private String customJsonLdSchema;
	
	private String defaultAuthorUsername;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public SocialMediaLinks getSocialMediaLinks() {
		return socialMediaLinks;
	}

	public void setSocialMediaLinks(SocialMediaLinks socialMediaLinks) {
		this.socialMediaLinks = socialMediaLinks;
	}

	public String getCustomJsonLdSchema() {
		return customJsonLdSchema;
	}

	public void setCustomJsonLdSchema(String customJsonLdSchema) {
		this.customJsonLdSchema = customJsonLdSchema;
	}

	public String getDefaultAuthorUsername() {
		return defaultAuthorUsername;
	}

	public void setDefaultAuthorUsername(String defaultAuthorUsername) {
		this.defaultAuthorUsername = defaultAuthorUsername;
	}

}
