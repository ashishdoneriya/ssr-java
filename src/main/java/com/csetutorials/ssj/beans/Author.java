package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Author {

	private String name;
	private String username;
	private String description;
	private String imageUrl;
	private SocialMediaLinks socialMediaLinks;
	private String url;

}
