package com.csetutorials.beans;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Page {

	private String title;

	private Date created;

	private Date updated;

	private boolean isDraft;

	private String layout;

	private String slug, permalink, url;

	private Map<String, String> rawParams;

	// For posts
	private String summary;

	private List<CatTag> tags;

	private List<CatTag> categories;

	private Map<String, Object> author;

	private transient File file;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public boolean getIsDraft() {
		return isDraft;
	}

	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public Map<String, String> getRawParams() {
		return rawParams;
	}

	public void setRawParams(Map<String, String> rawParams) {
		this.rawParams = rawParams;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Map<String, Object> getAuthor() {
		return author;
	}

	public void setAuthor(Map<String, Object> author) {
		this.author = author;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<CatTag> getTags() {
		return tags;
	}

	public void setTags(List<CatTag> tags) {
		this.tags = tags;
	}

	public List<CatTag> getCategories() {
		return categories;
	}

	public void setCategories(List<CatTag> categories) {
		this.categories = categories;
	}

}
