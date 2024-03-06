package com.csetutorials.ssj.beans;

import com.csetutorials.ssj.contants.SSJPaths;
import com.csetutorials.ssj.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Getter
@Setter
public class Configuration {

	WebsiteInfo website;
	List<Post> posts;
	List<Page> pages;
	List<Author> authors;
	List<Category> categories;
	List<Tag> tags;

	String baseDir;
	SSJPaths ssjPaths;
	String activeThemeDir;
	ThemeConfig themeConfig;
	Map<String, Object> context;

	public Map<String, Object> getContextCopy() {
		return new HashMap<>(context);
	}

	public Optional<Author> getDefaultAuthor() {
		if (StringUtils.isBlank(website.getDefaultAuthor())) {
			return Optional.empty();
		}
		return getAuthor(website.getDefaultAuthor());
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
