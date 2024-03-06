package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostYmlParams {

	boolean isDraft;
	String title;
	String seoTitle;
	String description;
	String seoDescription;
	String created;
	String updated;
	String slug;
	String permalink;
	String url;
	String absoluteUrl;
	List<String> tags;
	List<String> categories;
	String author;
	Page next, previous;
	String featuredImage;
	String content;

}
