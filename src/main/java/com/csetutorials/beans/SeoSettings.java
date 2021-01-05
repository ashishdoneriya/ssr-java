package com.csetutorials.beans;

public class SeoSettings {

	private boolean isPerson;

	private OrganizationInfo organizationInfo;

	private String personUsername;

	public boolean getIsPerson() {
		return isPerson;
	}

	public void setIsPerson(boolean isPerson) {
		this.isPerson = isPerson;
	}

	public OrganizationInfo getOrganizationInfo() {
		return organizationInfo;
	}

	public void setOrganizationInfo(OrganizationInfo organizationInfo) {
		this.organizationInfo = organizationInfo;
	}

	public String getPersonUsername() {
		return personUsername;
	}

	public void setPersonUsername(String personUsername) {
		this.personUsername = personUsername;
	}
	
	public String getDefaultAuthor() {
		return isPerson ? personUsername : organizationInfo.getDefaultAuthorUsername();
	}

}
