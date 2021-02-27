package com.csetutorials.beans;

import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;

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
	private SeoSettings seoSettings;

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
		this.setCategoriesLayout("category.html");
		this.setTagsLayout("tag.html");
		this.setCategoryBase("topics");
		this.setAuthorLayout("author.html");
		this.setTagBase("tag");
		this.setAuthorBase("author");
		this.setIndexPostsLayout("latest-posts.html");
		this.setIndexPostsBase("/");
		this.setPaginationEnabled(true);
		this.setMaxPosts(10);
	}

	// Getter Methods

	public SiteConfig(boolean b) {
		// TODO Auto-generated constructor stub
	}

	public String getTitle() {
		return title;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public String getDefaultAuthor() {
		return this.seoSettings.getDefaultAuthor();
	}

	public String getPostLayout() {
		return postLayout;
	}

	public String getPageLayout() {
		return pageLayout;
	}

	public String getCategory() {
		return category;
	}

	public String getPostPermalink() {
		return postPermalink;
	}

	public boolean isPageUglyUrlEnabled() {
		return pageUglyUrlEnabled;
	}

	public boolean isPostUglyUrlEnabled() {
		return postUglyUrlEnabled;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPostLayout(String postLayout) {
		this.postLayout = postLayout;
	}

	public void setPageLayout(String pageLayout) {
		this.pageLayout = pageLayout;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setPostPermalink(String postPermalink) {
		this.postPermalink = postPermalink;
	}

	public void setPageUglyUrlEnabled(boolean pageUglyUrlEnabled) {
		this.pageUglyUrlEnabled = pageUglyUrlEnabled;
	}

	public void setPostUglyUrlEnabled(boolean postUglyUrlEnabled) {
		this.postUglyUrlEnabled = postUglyUrlEnabled;
	}

	public String getPagePermalink() {
		return pagePermalink;
	}

	public void setPagePermalink(String pagePermalink) {
		this.pagePermalink = pagePermalink;
	}

	public Map<String, Object> getRawConfig() {
		return rawConfig;
	}

	public void setRawConfig(Map<String, Object> rawConfig) {
		this.rawConfig = rawConfig;
	}

	public VelocityEngine getEngine() {
		return velocityEngine;
	}

	public void setEngine(VelocityEngine engine) {
		this.velocityEngine = engine;
	}

	public String getCategoriesLayout() {
		return categoriesLayout;
	}

	public void setCategoriesLayout(String categoriesLayout) {
		this.categoriesLayout = categoriesLayout;
	}

	public String getTagsLayout() {
		return tagsLayout;
	}

	public void setTagsLayout(String tagsLayout) {
		this.tagsLayout = tagsLayout;
	}

	public String getLatestPostsLayout() {
		return latestPostsLayout;
	}

	public void setIndexPostsLayout(String latestPostsLayout) {
		this.latestPostsLayout = latestPostsLayout;
	}

	public String getCategoryBase() {
		return categoryBase;
	}

	public void setCategoryBase(String categoryBase) {
		this.categoryBase = categoryBase;
	}

	public String getTagBase() {
		return tagBase;
	}

	public void setTagBase(String tagBase) {
		this.tagBase = tagBase;
	}

	public String getIndexPostsBase() {
		return latestPostsBase;
	}

	public void setIndexPostsBase(String indexPostsBase) {
		this.latestPostsBase = indexPostsBase;
	}

	public boolean isPaginationEnabled() {
		return paginationEnabled;
	}

	public void setPaginationEnabled(boolean paginationEnabled) {
		this.paginationEnabled = paginationEnabled;
	}

	public int getMaxPosts() {
		return maxPosts;
	}

	public void setMaxPosts(int maxPosts) {
		this.maxPosts = maxPosts;
	}

	public String getAuthorBase() {
		return authorBase;
	}

	public void setAuthorBase(String authorBase) {
		this.authorBase = authorBase;
	}

	public String getAuthorLayout() {
		return authorLayout;
	}

	public void setAuthorLayout(String authorLayout) {
		this.authorLayout = authorLayout;
	}

	public List<Page> getPosts() {
		return posts;
	}

	public void setPosts(List<Page> posts) {
		this.posts = posts;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public List<CatTag> getCategories() {
		return categories;
	}

	public void setCategories(List<CatTag> categories) {
		this.categories = categories;
	}

	public List<CatTag> getTags() {
		return tags;
	}

	public void setTags(List<CatTag> tags) {
		this.tags = tags;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getActiveThemeDir() {
		return activeThemeDir;
	}

	public void setActiveThemeDir(String activeThemeDir) {
		this.activeThemeDir = activeThemeDir;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public SeoSettings getSeoSettings() {
		return seoSettings;
	}

	public void setSeoSettings(SeoSettings seoSettings) {
		this.seoSettings = seoSettings;
	}

	public String getFavicon() {
		return favicon;
	}

	public void setFavicon(String favicon) {
		this.favicon = favicon;
	}

	public Map<String, Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Map<String, Author> authors) {
		this.authors = authors;
	}

	public SocialMediaLinks getPublisherSocialLinks() {
		return publisherSocialLinks;
	}

	public void setPublisherSocialLinks(SocialMediaLinks publisherSocialLinks) {
		this.publisherSocialLinks = publisherSocialLinks;
	}
}