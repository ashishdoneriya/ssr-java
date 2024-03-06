package com.csetutorials.ssj.contants;

import com.csetutorials.ssj.beans.WebsiteInfo;

import java.io.File;

public class SSJPaths {

	private final String baseDir;
	private final WebsiteInfo websiteInfo;

	public SSJPaths(String baseDir, WebsiteInfo websiteInfo) {
		this.baseDir = baseDir;
		this.websiteInfo = websiteInfo;
	}

	public String getPostsDir() {
		return baseDir + File.separator + websiteInfo.getPostsDir();
	}

	public String getPagesDir() {
		return baseDir + File.separator + websiteInfo.getPagesDir();
	}

	public String getAuthorsDir() {
		return baseDir + File.separator + websiteInfo.getAuthorsDir();
	}

	public String getTempDir() {
		return baseDir + File.separator + websiteInfo.getTempDir();
	}

	public String getThemesDir() {
		return baseDir + File.separator + websiteInfo.getThemesDir();
	}

	public String getGeneratedHtmlDir() {
		return baseDir + File.separator + websiteInfo.getGeneratedHtmlDir();
	}

	public String getCategoriesDir() {
		return baseDir + File.separator + websiteInfo.getCategoriesDir();
	}

	public String getTagsDir() {
		return baseDir + File.separator + websiteInfo.getTagsDir();
	}

}
