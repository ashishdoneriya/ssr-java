package com.csetutorials.ssj.beans;

import lombok.Getter;
import lombok.Setter;
import org.apache.velocity.app.VelocityEngine;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SiteConfig {

	private String title;
	private String baseUrl;
	private String tagline;
	private String description;
	private String url;
	private String favicon;
	private String postLayout;
	private String pageLayout;
	private String category;
	private String postPermalink;
	private String pagePermalink;
	private boolean pageUglyUrlEnabled;
	private boolean postUglyUrlEnabled = true;
	private String categoryBase;
	private String tagBase;
	private String authorBase;
	private String latestPostsBase;
	private boolean paginationEnabled;
	private int maxPosts;
	private String theme;
	private String defaultAuthor;
	private transient String categoriesLayout;
	private transient String tagsLayout;
	private transient String latestPostsLayout;
	private transient String authorLayout;
	private transient String activeThemeDir;
	private transient Map<String, Object> rawConfig;
	private transient VelocityEngine velocityEngine;
	private List<Page> posts, pages;
	private List<CatTag> categories, tags;
	private Map<String, Object> data;
	private transient Map<String, Author> authors;
	private SocialMediaLinks publisherSocialLinks;

	public SiteConfig() {
		this.title = "My Site";
		this.baseUrl = "/";
		this.description = "This is a description";
		this.postLayout = "post.html";
		this.pageLayout = "page.html";
		this.category = "others";
		this.postPermalink = "/:slug.html";
		this.postUglyUrlEnabled = true;
		this.pagePermalink = "/:slug";
		this.pageUglyUrlEnabled = true;
		this.categoriesLayout = "category.html";
		this.tagsLayout = "tag.html";
		this.categoryBase = "topics";
		this.authorLayout = "author.html";
		this.tagBase = "tag";
		this.authorBase = "author";
		this.latestPostsLayout = "latest-posts.html";
		this.latestPostsBase = "/";
		this.paginationEnabled = true;
		this.maxPosts = 10;
	}

	// Getter Methods

	public SiteConfig(boolean b) {
		// TODO Auto-generated constructor stub
	}

}