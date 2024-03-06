package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Post extends Page {

	List<Tag> tags;
	List<Category> categories;
	Post next, previous;
	List<Image> images;
	String featuredImage;
	String featuredImageAbsoluteUrl;

}
