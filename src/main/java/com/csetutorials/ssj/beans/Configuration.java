package com.csetutorials.ssj.beans;

import com.csetutorials.ssj.contants.SSJPaths;
import com.csetutorials.ssj.services.FileService;
import com.csetutorials.ssj.services.JsonService;
import com.csetutorials.ssj.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Getter
@Setter
public class Configuration {

	String baseDir;
	WebsiteInfo websiteInfo;
	String activeThemeDir;
	ThemeConfig themeConfig;
	List<Post> posts;
	List<Page> pages;
	List<Author> authors;
	List<Category> categories;
	List<Tag> tags;
	@Autowired
	FileService fileService;
	@Autowired
	JsonService jsonService;
	SSJPaths ssjPaths;

	public Optional<Author> getDefaultAuthor() {
		if (StringUtils.isBlank(websiteInfo.getDefaultAuthor())) {
			return Optional.empty();
		}
		return getAuthor(websiteInfo.getDefaultAuthor());
	}

	public Optional<Author> getAuthor(String username) {
		for (Author author : authors) {
			if (username.equals(author.getUsername())) {
				return Optional.of(author);
			}
		}
		return Optional.empty();
	}

	public Optional<Category> getCategory(String shortcode) {
		for (Category category : categories) {
			if (shortcode.equals(category.getShortcode())) {
				return Optional.of(category);
			}
		}
		return Optional.empty();
	}

	public Optional<Tag> getTag(String shortcode) {
		for (Tag tag : tags) {
			if (shortcode.equals(tag.getShortcode())) {
				return Optional.of(tag);
			}
		}
		return Optional.empty();
	}

}
