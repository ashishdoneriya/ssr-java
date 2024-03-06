package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class WebsiteInfo {

	String title = "My Site";
	String baseUrl = "/";
	String tagline = "";
	String description = "This is a description";
	String url = "";
	String favicon = "";
	String postPermalink = "/:slug.html";
	String pagePermalink = "/:slug";
	boolean pageUglyUrlEnabled;
	boolean postUglyUrlEnabled = true;
	String categoryBase = "topic";
	String tagBase =  "tag";
	String authorBase = "author";
	String latestPostsBase = "/";
	boolean paginationEnabled = true;
	int maxPosts = 10;
	String theme = "";
	String defaultAuthor = "";
	boolean displayCreatedDate = true;
	boolean displayUpdatedDate = true;
	String postsDir = "posts";
	String pagesDir = "pages";
	String authorsDir = "authors";
	String tagsDir = "tags";
	String categoriesDir = "categories";
	String tempDir = "temp";
	String themesDir = "themes";
	String generatedHtmlDir = "dist";
	String postsMetaDataSeparator = "---";
	String postsMetaDateFormat = "yyyy-MM-dd HH:mm:ss";
	String postsDisplayDateFormat = "MMM dd, yyyy";

}