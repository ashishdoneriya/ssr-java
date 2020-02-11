package com.csetutorials.beans;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;

import com.csetutorials.utils.StringUtils;

public class SiteConfig {
	private String title;
	private String baseUrl;
	private String description;
	private String url;
	private String postWritingDataFormat;
	private String author;
	private String postLayout;
	private String pageLayout;
	private String category;
	private String postPermalink;
	private String pagePermalink;
	private boolean pageUglyUrlEnabled;
	private boolean postUglyUrlEnabled;
	private String generatedHtmlDir;
	private String postsDir;
	private String pagesDir;
	private String tempDir;
	private String dataDir;
	private String themesDir;
	private String categoryBase;
	private String tagBase;
	private String authorBase;
	private String latestPostsBase;
	private boolean paginationEnabled;
	private int maxPosts;
	private String theme;

	private transient String categoriesLayout;
	private transient String tagsLayout;
	private transient String latestPostsLayout;
	private transient String authorLayout;
	private transient String activeThemeDir;

	private transient String root, tempLayoutsPath;
	private transient Map<String, Object> rawConfig;
	private transient VelocityEngine velocityEngine;
	private List<Page> posts, pages;
	private List<CatTag> categories, tags;
	private Map<String, Object> data;

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
		this.generatedHtmlDir = File.separator + "dist";
		this.postsDir = File.separator + "posts";
		this.pagesDir = File.separator + "pages";
		this.tempDir = File.separator + "temp";
		this.dataDir = File.separator + "data";
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
		this.setThemesDir(File.separator + "themes");
		this.postWritingDataFormat = "yyyy-MM-dd'T'HH:mm:ssXXX";
	}

	// Getter Methods

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

	public String getPostWritingDataFormat() {
		return postWritingDataFormat;
	}

	public String getAuthor() {
		return author;
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

	public String getGeneratedHtmlDir() {
		return generatedHtmlDir;
	}

	public String getPostsDir() {
		return postsDir;
	}

	public String getPagesDir() {
		return pagesDir;
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

	public void setPostWritingDataFormat(String postWritingDataFormat) {
		this.postWritingDataFormat = postWritingDataFormat;
	}

	public void setAuthor(String author) {
		this.author = author;
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

	public void setGeneratedHtmlDir(String generatedHtmlDir) {
		this.generatedHtmlDir = generatedHtmlDir;
	}

	public void setPostsDir(String postsDir) {
		this.postsDir = postsDir;
	}

	public void setPagesDir(String pagesDir) {
		this.pagesDir = pagesDir;
	}

	public String getPagePermalink() {
		return pagePermalink;
	}

	public void setPagePermalink(String pagePermalink) {
		this.pagePermalink = pagePermalink;
	}

	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = StringUtils.removeExtraSlash(root);
		this.postsDir = StringUtils.removeExtraSlash(this.root + File.separator + this.postsDir);
		this.pagesDir = StringUtils.removeExtraSlash(this.root + File.separator + this.pagesDir);
		this.generatedHtmlDir = StringUtils.removeExtraSlash(this.root + File.separator + this.generatedHtmlDir);
		this.tempDir = StringUtils.removeExtraSlash(this.root + File.separator + this.tempDir);
		this.tempLayoutsPath = StringUtils.removeExtraSlash(this.tempDir + File.separator + "layouts");
		this.dataDir = StringUtils.removeExtraSlash(this.root + File.separator + this.dataDir);
		this.themesDir = StringUtils.removeExtraSlash(this.root + File.separator + this.themesDir);
	}

	public String getTempLayoutsPath() {
		return tempLayoutsPath;
	}

	public void setTempLayoutsPath(String tempLayoutsPath) {
		this.tempLayoutsPath = tempLayoutsPath;
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

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
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

	public String getThemesDir() {
		return themesDir;
	}

	public void setThemesDir(String themesDir) {
		this.themesDir = themesDir;
	}

	public String getActiveThemeDir() {
		return activeThemeDir;
	}

	public void setActiveThemeDir(String activeThemeDir) {
		this.activeThemeDir = activeThemeDir;
	}
}