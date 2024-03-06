package com.csetutorials.ssj.beans;

import com.csetutorials.ssj.services.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SocialMediaLinks {

	String facebookUrl = "";

	String twitterUrl = "";

	String instagram = "";

	String linkedin = "";

	String myspace = "";

	String pinterest = "";

	String youtube = "";

	String wikipedia = "";

	String website = "";

	String twitterUsername = "";

	@JsonIgnore
	private List<String> list = getSocialLinks();

	public synchronized List<String> getSocialLinks() {
		if (list != null) {
			return list;
		}
		list = new ArrayList<>();
		if (StringUtils.isNotBlank(facebookUrl)) {
			list.add(facebookUrl);
		}
		if (StringUtils.isNotBlank(twitterUrl)) {
			list.add(cleanTwitterUrl());
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

	private String cleanTwitterUrl() {
		int index = twitterUrl.lastIndexOf("/");
		String twitterUsername = twitterUrl.substring(index + 1);
		if (twitterUsername.contains("?")) {
			twitterUsername = twitterUsername.substring(0, twitterUsername.indexOf('?'));
		}
		return twitterUsername;
	}


}
