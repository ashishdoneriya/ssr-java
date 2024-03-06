package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.*;
import com.csetutorials.ssj.contants.SSJPaths;
import com.csetutorials.ssj.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
@Service
public class DataLoader {

	@Autowired
	Configuration configuration;
	@Autowired
	FileService fileService;
	@Autowired
	JsonService jsonService;
	@Autowired
	PostService postService;
	@Autowired
	PageService pageService;

	public void load(String baseDir) {
		setBaseDir(baseDir);
		loadWebsiteConfig();
		loadSsjPaths();
		loadAuthors();
		loadCategories();
		loadTags();
		loadPosts();
	}

	private void setBaseDir(String baseDir) {
		baseDir = baseDir.trim();
		if (baseDir.endsWith("/") || baseDir.endsWith("\\")) {
			baseDir = baseDir.substring(0, baseDir.length() - 1);
		}
		configuration.setBaseDir(baseDir);
	}

	private void loadWebsiteConfig() {
		String configFile = configuration.getBaseDir() + File.separator + "ssj.json";
		configuration.setWebsiteInfo(jsonService.convert(fileService.getString(configFile), WebsiteInfo.class));
	}

	private void loadSsjPaths() {
		configuration.setSsjPaths(new SSJPaths(configuration.getBaseDir(), configuration.getWebsiteInfo()));
	}

	private void loadAuthors() {
		List<Author> authors = new ArrayList<>();
		for (File authorFile : fileService.listFiles(configuration.getSsjPaths().getAuthorsDir())) {
			String content = fileService.getString(authorFile);
			Author author = jsonService.convert(content, Author.class);
			String username = author.getUsername();
			String url = configuration.getWebsiteInfo().getBaseUrl() + "/" + configuration.getWebsiteInfo().getAuthorBase() + "/" + username;
			url = StringUtils.removeExtraSlash(url);
			author.setUrl(url);
			authors.add(author);
		}
		Collections.sort(authors, Comparator.comparing(Author::getName));
		configuration.setAuthors(authors);
	}

	private void loadCategories() {
		List<Category> categories = new ArrayList<>();
		for (File authorFile : fileService.listFiles(configuration.getSsjPaths().getCategoriesDir())) {
			String content = fileService.getString(authorFile);
			Category category = jsonService.convert(content, Category.class);
			String url = configuration.getWebsiteInfo().getBaseUrl() + "/" + configuration.getWebsiteInfo().getCategoryBase() + "/" + category.getShortcode();
			url = StringUtils.removeExtraSlash(url);
			category.setUrl(url);
			categories.add(category);
		}
		Collections.sort(categories, Comparator.comparing(Category::getName));
		configuration.setCategories(categories);
	}

	private void loadTags() {
		List<Tag> tags = new ArrayList<>();
		for (File authorFile : fileService.listFiles(configuration.getSsjPaths().getTagsDir())) {
			String content = fileService.getString(authorFile);
			Tag tag = jsonService.convert(content, Tag.class);
			String url = configuration.getWebsiteInfo().getBaseUrl() + "/" + configuration.getWebsiteInfo().getCategoryBase() + "/" + tag.getShortcode();
			url = StringUtils.removeExtraSlash(url);
			tag.setUrl(url);
			tags.add(tag);
		}
		Collections.sort(tags, Comparator.comparing(Tag::getName));
		configuration.setTags(tags);
	}

	private void loadPosts() {
		configuration.setPosts(postService.readPosts());
	}

	private void loadPages() {
		configuration.setPages(pageService.readPages());
	}
}
