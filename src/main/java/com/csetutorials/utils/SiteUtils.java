package com.csetutorials.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.csetutorials.beans.CatTag;
import com.csetutorials.beans.Page;
import com.csetutorials.beans.Paginator;
import com.csetutorials.beans.SiteConfig;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class SiteUtils {

	public static SiteConfig getSiteConfig(String root) throws JsonSyntaxException, IOException {
		String json = FileUtils.getString(root + "/ssr.conf.json");
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> rawConfig = Constants.gson.fromJson(json, type);
		SiteConfig config = Constants.gson.fromJson(json, SiteConfig.class);
		config.setRawConfig(rawConfig);
		config.setRoot(root);
		return config;
	}

	private static Map<String, Object> createMap(Page page) throws IOException {
		Map<String, Object> map = new HashMap<>();
		for (Map.Entry<String, String> e : page.getRawParams().entrySet()) {
			map.put(e.getKey(), e.getValue());
		}
		map.put("title", page.getTitle());
		map.put("layout", page.getLayout());
		map.put("slug", page.getSlug());
		map.put("permalink", page.getPermalink());
		map.put("url", page.getUrl());
		map.put("created", page.getCreated());
		map.put("updated", page.getUpdated());
		map.put("author", page.getAuthor());
		map.put("categories", page.getCategories());
		map.put("tags", page.getTags());
		map.put("isDraft", page.getIsDraft());
		map.put("summary", page.getSummary());

		return map;
	}

	public static void generateLatestPostsPages(SiteConfig siteConfig) throws FileNotFoundException {
		String layoutsDir = siteConfig.getLayoutsDir();
		if (!new File(layoutsDir + File.separator + siteConfig.getLatestPostsLayout()).exists()) {
			return;
		}

		VelocityEngine engine = siteConfig.getEngine();
		VelocityContext context = new VelocityContext();

		context.put("site", siteConfig.getRawConfig());
		context.put("contentType", "latestPosts");
		context.put("data", siteConfig.getData());
		context.put("dateUtils", new DateUtils());

		int maxPosts = siteConfig.getMaxPosts();
		List<Page> posts = siteConfig.getPosts();
		if (!siteConfig.isPaginationEnabled()) {
			maxPosts = posts.size();
		}
		int totalPages = (int) Math.ceil(posts.size() / (maxPosts * 1.0));
		for (int i = 1; i <= totalPages; i++) {
			List<Page> list = getSublist(posts, i, maxPosts);
			Paginator paginator = new Paginator();
			paginator.setCurrentPage(i);
			paginator.setHasNextPage(i != totalPages);
			paginator.setHasPreviousPage(i != 1);
			paginator.setPosts(list);
			paginator.setPostsPerPage(maxPosts);
			paginator.setTotalPages(totalPages);
			paginator.setTotalPosts(posts.size());

			if (i != totalPages) {
				String nextPageUrl = siteConfig.getBaseUrl() + "/" + siteConfig.getIndexPostsBase() + "/page/" + i;
				nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
				paginator.setNextPageUrl(nextPageUrl);
			} else if (i != 1) {
				String previousPageUrl = siteConfig.getBaseUrl() + "/" + siteConfig.getIndexPostsBase()
						+ (i == 2 ? "" : "/page/" + i);
				previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
				paginator.setPreviousPageUrl(previousPageUrl);
			}
			String currentPageFilePath = siteConfig.getGeneratedHtmlDir() + File.separator + siteConfig.getBaseUrl()
					+ File.separator + siteConfig.getIndexPostsBase() + File.separator
					+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
			context.put("paginator", paginator);
			String pageLayoutContent = TemplateUtils.formatContent(engine, context, siteConfig.getLatestPostsLayout());
			FileUtils.write(currentPageFilePath, pageLayoutContent);
		}
	}

	public static void generateCategoriesPages(SiteConfig siteConfig, Map<CatTag, List<Page>> catsWithRelatedPosts)
			throws FileNotFoundException {
		String layoutsDir = siteConfig.getLayoutsDir();
		if (!new File(layoutsDir + File.separator + siteConfig.getCategoriesLayout()).exists()) {
			return;
		}

		VelocityEngine engine = siteConfig.getEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", siteConfig.getRawConfig());
		context.put("contentType", "category");
		context.put("data", siteConfig.getData());
		context.put("dateUtils", new DateUtils());

		for (Entry<CatTag, List<Page>> entry : catsWithRelatedPosts.entrySet()) {
			CatTag cat = entry.getKey();
			List<Page> catPosts = entry.getValue();
			int maxPosts = siteConfig.getMaxPosts();
			if (!siteConfig.isPaginationEnabled()) {
				maxPosts = catPosts.size();
			}
			int totalPages = (int) Math.ceil(catPosts.size() / (maxPosts * 1.0));
			for (int i = 1; i <= totalPages; i++) {
				List<Page> list = getSublist(catPosts, i, maxPosts);

				Paginator paginator = new Paginator();
				paginator.setCurrentPage(i);
				paginator.setHasNextPage(i != totalPages);
				paginator.setHasPreviousPage(i != 1);
				paginator.setPosts(list);
				paginator.setPostsPerPage(maxPosts);
				paginator.setTotalPages(totalPages);
				paginator.setTotalPosts(catPosts.size());

				if (i != totalPages) {
					String nextPageUrl = siteConfig.getBaseUrl() + "/" + siteConfig.getCategoryBase() + "/"
							+ cat.getShortcode() + "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = siteConfig.getBaseUrl() + "/" + siteConfig.getCategoryBase() + "/"
							+ cat.getShortcode() + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = siteConfig.getGeneratedHtmlDir() + File.separator + siteConfig.getBaseUrl()
						+ File.separator + siteConfig.getCategoryBase() + File.separator + cat.getShortcode()
						+ File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("category", cat);
				String pageLayoutContent = TemplateUtils.formatContent(engine, context,
						siteConfig.getCategoriesLayout());
				FileUtils.write(currentPageFilePath, pageLayoutContent);
			}
		}
	}

	public static void generateTagsPages(SiteConfig siteConfig, Map<CatTag, List<Page>> tagsWithRelatedPosts)
			throws FileNotFoundException {
		String layoutsDir = siteConfig.getLayoutsDir();
		if (!new File(layoutsDir + File.separator + siteConfig.getCategoriesLayout()).exists()) {
			return;
		}

		VelocityEngine engine = siteConfig.getEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", siteConfig.getRawConfig());
		context.put("contentType", "tag");
		context.put("data", siteConfig.getData());
		context.put("dateUtils", new DateUtils());

		for (Entry<CatTag, List<Page>> entry : tagsWithRelatedPosts.entrySet()) {
			CatTag tag = entry.getKey();
			List<Page> tagPosts = entry.getValue();
			int maxPosts = siteConfig.getMaxPosts();
			if (!siteConfig.isPaginationEnabled()) {
				maxPosts = tagPosts.size();
			}
			int totalPages = (int) Math.ceil(tagPosts.size() / (maxPosts * 1.0));
			for (int i = 1; i <= totalPages; i++) {
				List<Page> list = getSublist(tagPosts, i, maxPosts);
				Paginator paginator = new Paginator();
				paginator.setCurrentPage(i);
				paginator.setHasNextPage(i != totalPages);
				paginator.setHasPreviousPage(i != 1);
				paginator.setPosts(list);
				paginator.setPostsPerPage(maxPosts);
				paginator.setTotalPages(totalPages);
				paginator.setTotalPosts(tagPosts.size());

				if (i != totalPages) {
					String nextPageUrl = siteConfig.getBaseUrl() + "/" + siteConfig.getTagBase() + "/" + tag.getName()
							+ "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = siteConfig.getBaseUrl() + "/" + siteConfig.getTagBase() + "/"
							+ tag.getName() + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = siteConfig.getGeneratedHtmlDir() + File.separator + siteConfig.getBaseUrl()
						+ File.separator + siteConfig.getTagBase() + File.separator + tag.getName() + File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("tag", tag);
				String pageLayoutContent = TemplateUtils.formatContent(engine, context, siteConfig.getTagsLayout());
				FileUtils.write(currentPageFilePath, pageLayoutContent);
			}
		}
	}

	private static List<Page> getSublist(List<Page> list, int pageNumber, int maxPosts) {
		List<Page> sub = new ArrayList<>();
		int i = (pageNumber - 1) * maxPosts;
		while (i < list.size() && sub.size() < maxPosts) {
			sub.add(list.get(i++));
		}
		return sub;
	}

	public static void generatePosts(List<Page> pages, SiteConfig siteConfig, boolean isPost) throws IOException {

		VelocityContext context = new VelocityContext();
		VelocityEngine engine = siteConfig.getEngine();
		context.put("site", siteConfig.getRawConfig());
		context.put("contentType", isPost ? "post" : "page");
		context.put("data", siteConfig.getData());
		context.put("dateUtils", new DateUtils());

		for (Page page : pages) {
			if (page.getIsDraft()) {
				continue;
			}
			Map<String, Object> map = createMap(page);
			context.put(isPost ? "post" : "page", map);
			String content = StringUtils.getContentBody(FileUtils.getString(page.getFile()));
			if (page.getFile().getName().endsWith(".md") || page.getFile().getName().endsWith(".markdown")) {
				content = TemplateUtils.parseMarkdown(content);
			}
			TemplateUtils.addTemplate(siteConfig, "test-template-ssr", content);
			content = TemplateUtils.formatContent(engine, context, "test-template-ssr");
			map.put("content", content);
			context.put(isPost ? "post" : "page", map);
			String postLayoutContent = TemplateUtils.formatContent(engine, context, page.getLayout());
			write(page.getPermalink(), postLayoutContent, siteConfig,
					isPost ? siteConfig.isPostUglyUrlEnabled() : siteConfig.isPageUglyUrlEnabled());
		}
	}

	public static void write(String permalink, String content, SiteConfig siteConfig, boolean uglyUrl)
			throws FileNotFoundException {
		permalink = "/" + permalink + (uglyUrl ? "" : "/index.html");
		String path = siteConfig.getGeneratedHtmlDir() + permalink;
		path = path.replaceAll("/+", "/").replaceAll("/", File.separator);
		File file = new File(path);
		file.getParentFile().mkdirs();
		FileUtils.write(file.getAbsolutePath(), content);
	}

	public static void writePost(String postPermalink, String postContent, SiteConfig siteConfig)
			throws FileNotFoundException {
		postPermalink = "/" + postPermalink + (siteConfig.isPostUglyUrlEnabled() ? "" : "/index.html");
		String path = siteConfig.getGeneratedHtmlDir() + postPermalink;
		path = path.replaceAll("/+", "/").replaceAll("/", File.separator);
		File file = new File(path);
		file.getParentFile().mkdirs();
		FileUtils.write(file.getAbsolutePath(), postContent);
	}

	public static String getCotentFormatted(VelocityContext context, String content) {
		// Initialize the engine.
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.Log4JLogChute");
		engine.setProperty(Velocity.RESOURCE_LOADER, "string");
		engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
		engine.addProperty("string.resource.loader.repository.static", "false");
		// engine.addProperty("string.resource.loader.modificationCheckInterval", "1");
		engine.init();

		// Initialize my template repository. You can replace the "Hello $w" with your
		// String.
		StringResourceRepository repo = (StringResourceRepository) engine
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource("temp", content);

		// Get and merge the template with my parameters.
		Template template = engine.getTemplate("temp");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	public static void generateAuthorsPages(SiteConfig siteConfig, Map<String, List<Page>> authorsPostsMap)
			throws FileNotFoundException {
		String layoutsDir = siteConfig.getLayoutsDir();
		if (!new File(layoutsDir + File.separator + siteConfig.getAuthorLayout()).exists()) {
			return;
		}

		VelocityEngine engine = siteConfig.getEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", siteConfig.getRawConfig());
		context.put("contentType", "author");
		context.put("data", siteConfig.getData());
		context.put("dateUtils", new DateUtils());
		for (Entry<String, List<Page>> entry : authorsPostsMap.entrySet()) {
			String authorName = entry.getKey();
			List<Page> authorPosts = entry.getValue();
			int maxPosts = siteConfig.getMaxPosts();
			if (!siteConfig.isPaginationEnabled()) {
				maxPosts = authorPosts.size();
			}
			int totalPages = (int) Math.ceil(authorPosts.size() / (maxPosts * 1.0));
			for (int i = 1; i <= totalPages; i++) {
				List<Page> list = getSublist(authorPosts, i, maxPosts);
				Paginator paginator = new Paginator();
				paginator.setCurrentPage(i);
				paginator.setHasNextPage(i != totalPages);
				paginator.setHasPreviousPage(i != 1);
				paginator.setPosts(list);
				paginator.setPostsPerPage(maxPosts);
				paginator.setTotalPages(totalPages);
				paginator.setTotalPosts(authorPosts.size());

				if (i != totalPages) {
					String nextPageUrl = siteConfig.getBaseUrl() + "/" + siteConfig.getAuthorBase() + "/" + authorName
							+ "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = siteConfig.getBaseUrl() + "/" + siteConfig.getAuthorBase() + "/"
							+ authorName + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = siteConfig.getGeneratedHtmlDir() + File.separator + siteConfig.getBaseUrl()
						+ File.separator + siteConfig.getAuthorBase() + File.separator + authorName + File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("author", authorName);
				String pageLayoutContent = TemplateUtils.formatContent(engine, context, siteConfig.getAuthorLayout());
				FileUtils.write(currentPageFilePath, pageLayoutContent);
			}
		}
	}

}
