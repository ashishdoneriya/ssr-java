package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.*;
import com.csetutorials.ssj.exceptions.MetaDataException;
import com.csetutorials.ssj.utils.StringUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@CommonsLog
public class PostService {

	@Autowired
	FileService fileService;

	@Autowired
	Configuration configuration;

	public List<Post> readPosts() {
		String separator = Pattern.quote(configuration.getWebsite().getPostsMetaDataSeparator());
		Pattern pattern = Pattern.compile(separator + "(.+)" + separator + "(.+)", Pattern.MULTILINE);

		Yaml yaml = new Yaml();
		List<Post> posts = new ArrayList<>();
		SimpleDateFormat parseSdf = new SimpleDateFormat(configuration.getWebsite().getPostsMetaDateFormat());
		SimpleDateFormat formatSdf = new SimpleDateFormat(configuration.getWebsite().getPostsDisplayDateFormat());

		for (File file : fileService.listFiles(configuration.getSsjPaths().getPostsDir())) {
			String fileContent = fileService.getString(file);
			Matcher matcher = pattern.matcher(fileContent);
			if (!matcher.find()) {
				log.warn("Cannot identify content for - " + file.getAbsolutePath());
				continue;
			}
			String metaStr = matcher.group(1);
			PostYmlParams meta = parsePostMetadata(yaml, metaStr);
			if (meta.isDraft()) {
				continue;
			}
			meta.setContent(matcher.group(2));

			Post post = createPost(parseSdf, formatSdf, meta, file);
			if (post != null) {
				setContent(post, meta, file);
				posts.add(post);
			}
		}

		sortPosts(posts);
		setPostRelationships(posts);
		return posts;
	}

	private PostYmlParams parsePostMetadata(Yaml yaml, String metaStr) {
		return yaml.loadAs(metaStr, PostYmlParams.class);
	}

	private Post createPost(SimpleDateFormat parseSdf, SimpleDateFormat formatSdf, PostYmlParams meta, File file) {
		try {
			Post post = new Post();
			setPostBasicInfo(parseSdf, formatSdf, meta, post, file);
			setPostAuthor(meta, post);
			setPostCategories(meta, post, file);
			setPostTags(meta, post, file);
			setPostUrls(post, meta);
			return post;
		} catch (MetaDataException e) {
			log.error("Error creating post for file - " + file.getName() + ", " +  e.getMessage());
			return null;
		}
	}


	private void setPostBasicInfo(SimpleDateFormat parseSdf, SimpleDateFormat formatSdf, PostYmlParams meta, Post post, File file) throws MetaDataException {
		post.setTitle(meta.getTitle());
		post.setSeoTitle(StringUtils.isNotBlank(meta.getSeoTitle()) ? meta.getSeoTitle() : meta.getTitle());
		post.setDescription(meta.getDescription());
		post.setSeoDescription(StringUtils.isNotBlank(meta.getSeoDescription()) ? meta.getSeoDescription() : meta.getDescription());
		post.setDraft(meta.isDraft());
		try {
			post.setCreated(parseDate(meta.getCreated(), parseSdf, file.getName()));
		} catch (ParseException e) {
			throw new MetaDataException("Incorrect created date");
		}
		post.setCreatedUIDate(formatSdf.format(post.getCreated()));
		if (StringUtils.isNotBlank(meta.getUpdated())) {
			try {
				post.setUpdated(parseDate(meta.getUpdated(), parseSdf, file.getName()));
			} catch (ParseException e) {
				throw new MetaDataException("Incorrect updated date");
			}
		} else {
			post.setUpdated(post.getCreated());
		}
		post.setUpdatedUIDate(formatSdf.format(post.getUpdated()));
	}

	private Date parseDate(String dateString, SimpleDateFormat sdf, String fileName) throws ParseException {
		if (StringUtils.isNotBlank(dateString)) {
			return sdf.parse(dateString);
		} else {
			log.warn("Created Date not defined in post - " + fileName);
			return new Date();
		}
	}

	private void setPostAuthor(PostYmlParams meta, Post post) {
		String authorName = meta.getAuthor();
		Optional<Author> authorOpt = StringUtils.isNotBlank(authorName) ? configuration.getAuthor(authorName) : configuration.getDefaultAuthor();
		if (authorOpt.isPresent()) {
			post.setAuthor(authorOpt.get());
		} else {
			log.error("Author information not found for post - " + post.getTitle());
		}
	}


	private void setPostCategories(PostYmlParams meta, Post post, File file) {
		List<String> categoriesStrList = meta.getCategories();
		List<Category> categories = new ArrayList<>();
		for (String category : categoriesStrList) {
			Optional<Category> categoryOpt = configuration.getCategory(category);
			if (categoryOpt.isPresent()) {
				categories.add(categoryOpt.get());
			} else {
				log.warn("Category [" + category + "] information not found for post - " + file.getName());
			}
		}
		categories.sort(Comparator.comparing(Category::getName));
		post.setCategories(categories);
	}

	private void setPostTags(PostYmlParams meta, Post post, File file) {
		List<String> tagsStrList = meta.getTags();
		List<Tag> tags = new ArrayList<>();
		for (String tag : tagsStrList) {
			Optional<Tag> tagOpt = configuration.getTag(tag);
			if (tagOpt.isPresent()) {
				tags.add(tagOpt.get());
			} else {
				log.warn("Tag [" + tag + "] information not found for post - " + file.getName());
			}
		}
		tags.sort(Comparator.comparing(Tag::getName));
		post.setTags(tags);
	}

	private void setPostUrls(Post post, PostYmlParams meta) {
		post.setUrl(StringUtils.removeExtraSlash("/" + configuration.getWebsite().getBaseUrl() + "/" + generatePostUrl(post, meta)));
		post.setAbsoluteUrl(StringUtils.removeExtraSlash(configuration.getWebsite().getUrl() + post.getUrl()));
	}

	private void setContent(Post post, PostYmlParams meta, File file) {
		post.setContent(file.getName().endsWith(".md") ? StringUtils.parseMarkdown(meta.getContent()) : meta.getContent());
	}

	private void sortPosts(List<Post> posts) {
		posts.sort((p1, p2) -> p2.getCreated().compareTo(p1.getCreated()));
	}

	private void setPostRelationships(List<Post> posts) {
		for (int i = 0; i < posts.size() - 1; i++) {
			posts.get(i).setNext(posts.get(i + 1));
		}
		for (int i = 1; i < posts.size(); i++) {
			posts.get(i).setPrevious(posts.get(i - 1));
		}
	}

	private String generatePostUrl(Post post, PostYmlParams meta) {
		String slug = StringUtils.isNotBlank(meta.getSlug()) ? meta.getSlug().replaceAll(" +", "-") : post.getTitle().replaceAll("[^\\sa-zA-Z0-9]", "").replaceAll("\\s+", "-");
		if (StringUtils.isNotBlank(slug)) {
			return configuration.getWebsite().getPostPermalink().replace(":slug", slug.trim());
		}
		String permalink = meta.getPermalink();
		if (StringUtils.isNotBlank(permalink)) {
			return permalink;
		}
		String newSlug = post.getTitle().replaceAll("[^\\sa-zA-Z0-9]", "").replaceAll("\\s+", "-");
		return configuration.getWebsite().getPostPermalink().replace(":slug", newSlug);
	}
}
