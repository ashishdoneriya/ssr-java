package com.csetutorials.beans;

import java.util.ArrayList;
import java.util.List;

import com.csetutorials.utils.StringUtils;

public class SocialMediaLinks {

	private String facebookUrl, twitterUrl, instagram, linkedin, myspace, pinterest, youtube, wikipedia, website;
	
	private String twitterUsername;
	
	private transient List<String> list;

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public void setFacebookUrl(String facebook) {
		this.facebookUrl = facebook;
	}

	public String getTwitterUrl() {
		return twitterUrl;
	}

	public void setTwitterUrl(String twitter) {
		this.twitterUrl = twitter;
	}

	public String getInstagram() {
		return instagram;
	}

	public void setInstagram(String instagram) {
		this.instagram = instagram;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public String getMyspace() {
		return myspace;
	}

	public void setMyspace(String myspace) {
		this.myspace = myspace;
	}

	public String getPinterest() {
		return pinterest;
	}

	public void setPinterest(String pinterest) {
		this.pinterest = pinterest;
	}

	public String getYoutube() {
		return youtube;
	}

	public void setYoutube(String youtube) {
		this.youtube = youtube;
	}

	public String getWikipedia() {
		return wikipedia;
	}

	public void setWikipedia(String wikipedia) {
		this.wikipedia = wikipedia;
	}

	public synchronized List<String> getSocialLinks() {
		if (list != null) {
			return list;
		}
		list = new ArrayList<>();
		if (StringUtils.isNotBlank(facebookUrl)) {
			list.add(facebookUrl);
		}
		if (StringUtils.isNotBlank(twitterUrl)) {
			list.add(twitterUrl);
		}
		if (StringUtils.isNotBlank(instagram)) {
			list.add(instagram);
		}
		if (StringUtils.isNotBlank(linkedin)) {
			list.add(linkedin);
		}
		if (StringUtils.isNotBlank(myspace)) {
			list.add(myspace);
		}
		if (StringUtils.isNotBlank(pinterest)) {
			list.add(pinterest);
		}
		if (StringUtils.isNotBlank(youtube)) {
			list.add(youtube);
		}
		if (StringUtils.isNotBlank(wikipedia)) {
			list.add(wikipedia);
		}
		return list;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	
	public String getTwitterUserName() {
		if (StringUtils.isBlank(this.twitterUrl)) {
			return null;
		}
		if (this.twitterUsername != null) {
			return this.twitterUsername;
		}
		
		int index = twitterUrl.lastIndexOf("/");
		this.twitterUsername = this.twitterUrl.substring(index + 1);
		return this.twitterUsername;
	}

}
