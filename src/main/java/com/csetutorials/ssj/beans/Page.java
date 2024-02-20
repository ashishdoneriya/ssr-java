package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Page {

	private String title;
	private Date created;
	private Date updated;
	private boolean isDraft;
	private String layout;
	private String slug, permalink, url, absoluteUrl;
	private Map<String, Object> rawParams;
	// For posts
	private String summary;
	private List<CatTag> tags;
	private List<CatTag> categories;
	private Author author;
	private transient File file;
	private Page next, previous;
	private List<Image> images;
	private String lastMod;
	private String featuredImage;
	private String featuredImageAbsoluteUrl;

}
