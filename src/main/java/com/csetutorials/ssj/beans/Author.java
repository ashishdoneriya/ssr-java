package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Author {

	String name = "";

	String username = "";

	String description = "";

	String imageUrl = "";

	SocialMediaLinks socialMediaLinks = new SocialMediaLinks();

	String url;

}
