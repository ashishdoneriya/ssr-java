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

	boolean isDraft;
	String title;
	String seoTitle;
	String description;
	String seoDescription;
	Date created;
	Date updated;
	String createdUIDate;
	String updatedUIDate;
	Author author;
	String url;
	String absoluteUrl;
	String content;

}
