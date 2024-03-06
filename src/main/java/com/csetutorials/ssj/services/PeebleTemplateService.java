package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.*;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.FileLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PeebleTemplateService {

	@Autowired
	Configuration configuration;

	PebbleEngine engine;

	Map<String, PebbleTemplate> templatesCache = new HashMap<>();

	public void initialize(String themeDir) {
		PebbleEngine.Builder builder = new PebbleEngine.Builder();
		FileLoader loader = new FileLoader();
		loader.setPrefix(themeDir);
		loader.setSuffix(".html");
		builder.loader(loader);
		engine = builder.build();
	}

	public String generateSinglePageHtml(Page page) throws Exception {
		Map<String, Object> context = configuration.getContextCopy();
		context.put("page", page);
		String layout = configuration.getThemeConfig().getPageLayout();
		PebbleTemplate template = templatesCache.computeIfAbsent(layout, k -> engine.getTemplate(layout));
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		return writer.toString();
	}

	public String generateSinglePostHtml(Post post) throws Exception {
		Map<String, Object> context = configuration.getContextCopy();
		context.put("post", post);
		String layout = configuration.getThemeConfig().getPostLayout();
		PebbleTemplate template = templatesCache.computeIfAbsent(layout, k -> engine.getTemplate(layout));
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		return writer.toString();
	}

	public String generateCategoryHtml(Category category, List<Post> posts, int currentPageNumber, int totalPages) throws Exception {
		Map<String, Object> context = configuration.getContextCopy();
		context.put("category", category);
		context.put("posts", posts);
		context.put("totalPages", totalPages);
		context.put("currentPageNumber", currentPageNumber);
		String layout = configuration.getThemeConfig().getCategoriesLayout();
		PebbleTemplate template = templatesCache.computeIfAbsent(layout, k -> engine.getTemplate(layout));
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		return writer.toString();
	}

	public String generateTagHtml(Tag tag, List<Post> posts, int currentPageNumber, int totalPages) throws Exception {
		Map<String, Object> context = configuration.getContextCopy();
		context.put("tag", tag);
		context.put("posts", posts);
		context.put("totalPages", totalPages);
		context.put("currentPageNumber", currentPageNumber);
		String layout = configuration.getThemeConfig().getTagsLayout();
		PebbleTemplate template = templatesCache.computeIfAbsent(layout, k -> engine.getTemplate(layout));
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		return writer.toString();
	}

	public String generateAuthorHtml(Author author, List<Post> posts, int currentPageNumber, int totalPages) throws Exception {
		Map<String, Object> context = configuration.getContextCopy();
		context.put("author", author);
		context.put("posts", posts);
		context.put("totalPages", totalPages);
		context.put("currentPageNumber", currentPageNumber);
		String layout = configuration.getThemeConfig().getPostLayout();
		PebbleTemplate template = templatesCache.computeIfAbsent(layout, k -> engine.getTemplate(layout));
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		return writer.toString();
	}

	public String generatePostsIndexHtml(List<Post> posts, int currentPageNumber, int totalPages) throws Exception {
		Map<String, Object> context = configuration.getContextCopy();
		context.put("posts", posts);
		context.put("totalPages", totalPages);
		context.put("currentPageNumber", currentPageNumber);
		String layout = configuration.getThemeConfig().getLatestPostsLayout();
		PebbleTemplate template = templatesCache.computeIfAbsent(layout, k -> engine.getTemplate(layout));
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		return writer.toString();
	}

	public String generateIndexHtml() throws Exception {
		Map<String, Object> context = configuration.getContextCopy();
		String layout = configuration.getThemeConfig().getLatestPostsLayout();
		PebbleTemplate template = templatesCache.computeIfAbsent(layout, k -> engine.getTemplate(layout));
		Writer writer = new StringWriter();
		template.evaluate(writer, context);
		return writer.toString();
	}

}
