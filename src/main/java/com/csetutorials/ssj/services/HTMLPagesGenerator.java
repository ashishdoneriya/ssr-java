package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.*;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CommonsLog
public class HTMLPagesGenerator {

	@Autowired
	Configuration configuration;
	@Autowired
	PeebleTemplateService templateService;
	@Autowired
	FileService fileService;

	public void generateHtmlPages() {

		generateIndex();
		generatePostsIndex();
		generatePosts();
		generatePages();
		generateAuthors();
		generateTags();
		generateCategories();
		copyThemeStaticContent();
		copyWebsiteStaticContent();
	}


	private void generateIndex() {
		try {
			String htmlContent = templateService.generateIndexHtml();
		} catch (Exception e) {
			log.error("Couldn't generate index page html", e);
		}
	}

	private void generatePostsIndex() {
		try {
			if (!configuration.getWebsite().isPaginationEnabled()) {
				String htmlContent = templateService.generatePostsIndexHtml(configuration.getPosts(), 1, 1);
			} else {
				List<List<Post>> outerList = divideList(configuration.getPosts(), configuration.getWebsite().getMaxPosts());
				for (int i = 0; i < outerList.size(); i++) {
					String htmlContent = templateService.generatePostsIndexHtml(outerList.get(i), i + 1, outerList.size());
				}
			}
		} catch (Exception e) {
			log.error("Couldn't generate posts index page", e);
		}
	}

	private void generatePosts() {
		for (Post post : configuration.getPosts()) {
			try {
				String htmlContent = templateService.generateSinglePostHtml(post);
			} catch (Exception e) {
				log.error("Couldn't generate post page for " + post.getTitle(), e);
			}
		}
	}

	private void generatePages() {
		for (Page page : configuration.getPages()) {
			try {
				String htmlContent = templateService.generateSinglePageHtml(page);
			} catch (Exception e) {
				log.error("Couldn't generate post page for " + page.getTitle(), e);
			}
		}
	}

	private void generateAuthors() {
		for (Author author : configuration.getAuthors()) {
			try {
				List<Post> posts = configuration.getPosts().stream().filter(post -> post.getAuthor().getUsername().equals(author.getUsername())).collect(Collectors.toList());
				if (!configuration.getWebsite().isPaginationEnabled()) {
					String htmlContent = templateService.generateAuthorHtml(author, posts, 1, 1);
				} else {
					List<List<Post>> outerList = divideList(posts, configuration.getWebsite().getMaxPosts());
					for (int i = 0; i < outerList.size(); i++) {
						String htmlContent = templateService.generateAuthorHtml(author, outerList.get(i), i + 1, outerList.size());
					}
				}
			} catch (Exception e) {
				log.error("Couldn't generate author page for " + author.getUsername(), e);
			}
		}
	}

	private void generateTags() {
		for (Tag tag : configuration.getTags()) {
			try {
				List<Post> posts = configuration.getPosts().stream().filter(post -> post.getTags().stream().filter(obj -> obj.getShortcode().equals(tag.getShortcode())).findAny().isPresent()).collect(Collectors.toList());

				if (!configuration.getWebsite().isPaginationEnabled()) {
					String htmlContent = templateService.generateTagHtml(tag, posts, 1, 1);
				} else {
					List<List<Post>> outerList = divideList(posts, configuration.getWebsite().getMaxPosts());
					for (int i = 0; i < outerList.size(); i++) {
						String htmlContent = templateService.generateTagHtml(tag, outerList.get(i), i + 1, outerList.size());
					}
				}
			} catch (Exception e) {
				log.error("Couldn't generate tag page for " + tag.getShortcode(), e);
			}
		}
	}

	private void generateCategories() {
		for (Category category : configuration.getCategories()) {
			try {
				List<Post> posts = configuration.getPosts().stream().filter(post -> post.getTags().stream().filter(obj -> obj.getShortcode().equals(category.getShortcode())).findAny().isPresent()).collect(Collectors.toList());

				if (!configuration.getWebsite().isPaginationEnabled()) {
					String htmlContent = templateService.generateCategoryHtml(category, posts, 1, 1);
				} else {
					List<List<Post>> outerList = divideList(posts, configuration.getWebsite().getMaxPosts());
					for (int i = 0; i < outerList.size(); i++) {
						String htmlContent = templateService.generateCategoryHtml(category, outerList.get(i), i + 1, outerList.size());
					}
				}
			} catch (Exception e) {
				log.error("Couldn't generate category page for " + category.getShortcode(), e);
			}
		}
	}

	private void copyThemeStaticContent() {
		fileService.copyDirRecursively(configuration.getActiveThemeDir() + File.separator + configuration.getThemeConfig().getStaticContentDir(), configuration.getSsjPaths().getGeneratedHtmlDir());
	}

	private void copyWebsiteStaticContent() {
		fileService.copyDirRecursively(configuration.getSsjPaths().getStaticContentDir(), configuration.getSsjPaths().getGeneratedHtmlDir());
	}

	private List<List<Post>> divideList(List<Post> originalList, int batchSize) {
		List<List<Post>> batches = new ArrayList<>();
		for (int i = 0; i < originalList.size(); i += batchSize) {
			int endIndex = Math.min(i + batchSize, originalList.size());
			List<Post> subList = originalList.subList(i, endIndex);
			batches.add(subList);
		}
		return batches;
	}

}
