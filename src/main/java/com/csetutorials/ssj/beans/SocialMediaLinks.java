package com.csetutorials.ssj.beans;

import java.util.ArrayList;
import java.util.List;

import com.csetutorials.ssj.services.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialMediaLinks {

	private String facebookUrl, twitterUrl, instagram, linkedin, myspace, pinterest, youtube, wikipedia, website;
	
	private String twitterUsername;

	private transient List<String> list;

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

}
