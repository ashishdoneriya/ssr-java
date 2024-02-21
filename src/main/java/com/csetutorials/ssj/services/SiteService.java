package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.*;
import com.csetutorials.ssj.contants.Layouts;
import com.csetutorials.ssj.contants.PathService;
import com.csetutorials.ssj.exceptions.JsonParsingException;
import com.csetutorials.ssj.exceptions.ThemeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

@Service
public class SiteService {

	@Autowired
	PathService pathService;
	@Autowired
	TemplateService templateService;
	@Autowired
	FileService fileService;
	@Autowired
	JsonService jsonService;


	private Map<String, Object> createMap(Page page) {
		Map<String, Object> map = new HashMap<>(page.getRawParams());
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
		map.put("isDraft", page.isDraft());
		map.put("summary", page.getSummary());
		map.put("absoluteUrl", page.getAbsoluteUrl());
		return map;
	}

	public void generateLatestPostsPages(WebsiteConfig websiteConfig) {
		if (templateService.isTemplateNotAvailable(websiteConfig, Layouts.latestPosts)) {
			return;
		}

		VelocityEngine engine = websiteConfig.getVelocityEngine();
		VelocityContext context = new VelocityContext();

		context.put("site", websiteConfig.getRawConfig());
		context.put("contentType", "latestPosts");
		context.put("data", websiteConfig.getData());
		context.put("dateUtils", new DateUtils());

		int maxPosts = websiteConfig.getMaxPosts();
		List<Page> posts = websiteConfig.getPosts();
		if (!websiteConfig.isPaginationEnabled()) {
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
				String nextPageUrl = websiteConfig.getBaseUrl() + "/" + websiteConfig.getLatestPostsBase() + "/page/" + i;
				nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
				paginator.setNextPageUrl(nextPageUrl);
			} else if (i != 1) {
				String previousPageUrl = websiteConfig.getBaseUrl() + "/" + websiteConfig.getLatestPostsBase()
						+ (i == 2 ? "" : "/page/" + i);
				previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
				paginator.setPreviousPageUrl(previousPageUrl);
			}
			String currentPageFilePath = pathService.getGeneratedHtmlDir() + File.separator + websiteConfig.getBaseUrl()
					+ File.separator + websiteConfig.getLatestPostsBase() + File.separator
					+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
			context.put("paginator", paginator);
			String pageLayoutContent = templateService.formatContent(engine, context, websiteConfig.getLatestPostsLayout());
			pageLayoutContent = Jsoup.parse(pageLayoutContent).toString();
			fileService.write(currentPageFilePath, pageLayoutContent);
		}
	}

	public void generateCategoriesPages(WebsiteConfig websiteConfig, Map<CatTag, List<Page>> catsWithRelatedPosts) {
		if (templateService.isTemplateNotAvailable(websiteConfig, Layouts.category)) {
			return;
		}

		VelocityEngine engine = websiteConfig.getVelocityEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", websiteConfig.getRawConfig());
		context.put("contentType", "category");
		context.put("data", websiteConfig.getData());
		context.put("dateUtils", new DateUtils());

		for (Entry<CatTag, List<Page>> entry : catsWithRelatedPosts.entrySet()) {
			CatTag cat = entry.getKey();
			List<Page> catPosts = entry.getValue();
			int maxPosts = websiteConfig.getMaxPosts();
			if (!websiteConfig.isPaginationEnabled()) {
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
					String nextPageUrl = websiteConfig.getBaseUrl() + "/" + websiteConfig.getCategoryBase() + "/"
							+ cat.getShortcode() + "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = websiteConfig.getBaseUrl() + "/" + websiteConfig.getCategoryBase() + "/"
							+ cat.getShortcode() + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = pathService.getGeneratedHtmlDir() + File.separator + websiteConfig.getBaseUrl()
						+ File.separator + websiteConfig.getCategoryBase() + File.separator + cat.getShortcode()
						+ File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("category", cat);
				String pageLayoutContent = templateService.formatContent(engine, context,
						websiteConfig.getCategoriesLayout());
				pageLayoutContent = Jsoup.parse(pageLayoutContent).toString();
				fileService.write(currentPageFilePath, pageLayoutContent);
			}
		}
	}

	public void generateTagsPages(WebsiteConfig websiteConfig, Map<CatTag, List<Page>> tagsWithRelatedPosts) {
		if (templateService.isTemplateNotAvailable(websiteConfig, Layouts.tag)) {
			return;
		}

		VelocityEngine engine = websiteConfig.getVelocityEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", websiteConfig.getRawConfig());
		context.put("contentType", "tag");
		context.put("data", websiteConfig.getData());
		context.put("dateUtils", new DateUtils());

		for (Entry<CatTag, List<Page>> entry : tagsWithRelatedPosts.entrySet()) {
			CatTag tag = entry.getKey();
			List<Page> tagPosts = entry.getValue();
			int maxPosts = websiteConfig.getMaxPosts();
			if (!websiteConfig.isPaginationEnabled()) {
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
					String nextPageUrl = websiteConfig.getBaseUrl() + "/" + websiteConfig.getTagBase() + "/" + tag.getName()
							+ "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = websiteConfig.getBaseUrl() + "/" + websiteConfig.getTagBase() + "/"
							+ tag.getName() + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = pathService.getGeneratedHtmlDir() + File.separator + websiteConfig.getBaseUrl()
						+ File.separator + websiteConfig.getTagBase() + File.separator + tag.getName() + File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("tag", tag);
				String pageLayoutContent = templateService.formatContent(engine, context, websiteConfig.getTagsLayout());
				pageLayoutContent = Jsoup.parse(pageLayoutContent).toString();
				fileService.write(currentPageFilePath, pageLayoutContent);
			}
		}
	}

	private List<Page> getSublist(List<Page> list, int pageNumber, int maxPosts) {
		List<Page> sub = new ArrayList<>();
		int i = (pageNumber - 1) * maxPosts;
		while (i < list.size() && sub.size() < maxPosts) {
			sub.add(list.get(i++));
		}
		return sub;
	}

	public void generatePosts(List<Page> pages, WebsiteConfig websiteConfig, boolean isPost) {
		VelocityContext context = new VelocityContext();
		VelocityEngine engine = websiteConfig.getVelocityEngine();
		context.put("site", websiteConfig.getRawConfig());
		context.put("contentType", isPost ? "post" : "page");
		context.put("data", websiteConfig.getData());
		context.put("dateUtils", new DateUtils());
		context.put("StringUtils", StringUtils.class);
		final String metaTagsFormat = fileService.getResourceContent("post-meta-tags.html");
		templateService.addTemplate(websiteConfig, "ssj-meta-tags", metaTagsFormat);
		for (Page page : pages) {
			if (page.isDraft()) {
				continue;
			}
			Map<String, Object> map = createMap(page);
			context.put("page", map);
			String content = StringUtils.getContentBody(fileService.getString(page.getFile()));
			if (page.getFile().getName().endsWith(".md") || page.getFile().getName().endsWith(".markdown")) {
				content = templateService.parseMarkdown(content);
			}
			templateService.addTemplate(websiteConfig, "test-template-ssr", content);
			content = templateService.formatContent(engine, context, "test-template-ssr");
			List<Image> images = extractImages(websiteConfig, content);
			content = formatAnchorTags(content);
			page.setImages(images);

			map.put("content", content);
			context.put("page", map);
			final String metaTags = templateService.formatContent(engine, context, "ssj-meta-tags");
			context.put("seoSettings", metaTags);
			String postLayoutContent = templateService.formatContent(engine, context, page.getLayout());
			postLayoutContent = Jsoup.parse(postLayoutContent).toString();
			write(page.getPermalink(), postLayoutContent,
					isPost ? websiteConfig.isPostUglyUrlEnabled() : websiteConfig.isPageUglyUrlEnabled());
		}
	}

	private List<Image> extractImages(WebsiteConfig websiteConfig, String content) {
		Document doc = Jsoup.parse(content);
		Elements imageElements = doc.select("img");
		List<Image> images = new ArrayList<>(1);
		for (Element imageEle : imageElements) {
			String src = imageEle.attr("src");

			if (!src.startsWith("http")) {
				if (!src.startsWith("/") && !websiteConfig.getBaseUrl().equals("/")) {
					src = websiteConfig.getBaseUrl() + src;
				}
				src = websiteConfig.getUrl() + "/" + src;
				src = StringUtils.removeExtraSlash(src);
			}

			String alt = imageEle.attr("alt");
			if (StringUtils.isBlank(alt)) {
				int slashLastIndex = src.lastIndexOf('/');
				if (slashLastIndex != -1) {
					alt = src.substring(slashLastIndex + 1);
				}
				alt = alt.split("\\.")[0].replaceAll("-", " ");
			}
			Image image = new Image();
			image.setAlt(alt);
			image.setSrc(src);
			images.add(image);
		}
		return images;
	}
	
	private String formatAnchorTags(String content) {
		Document doc = Jsoup.parse(content);
		Elements anchorTags = doc.select("a");
		for (Element imageEle : anchorTags) {
			String href = imageEle.attr("href");
			if (!href.startsWith("http")) {
				continue;
			}
			imageEle.attr("target", "_blank");
			imageEle.attr("rel", "noopener nofollow");
		}
		return doc.toString();
	}

	public void write(String permalink, String content, boolean uglyUrl) {
		permalink = "/" + permalink + (uglyUrl ? "" : "/index.html");
		String path = pathService.getGeneratedHtmlDir() + permalink;
		path = path.replaceAll("/+", "/").replaceAll("/", File.separator);
		File file = new File(path);
		fileService.mkdirs(file.getParentFile());
		fileService.write(file.getAbsolutePath(), content);
	}

	public void generateAuthorsPages(WebsiteConfig websiteConfig, Map<String, List<Page>> authorsPostsMap) {
		if (templateService.isTemplateNotAvailable(websiteConfig, Layouts.author)) {
			return;
		}

		VelocityEngine engine = websiteConfig.getVelocityEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", websiteConfig.getRawConfig());
		context.put("contentType", "author");
		context.put("data", websiteConfig.getData());
		context.put("dateUtils", new DateUtils());
		for (Entry<String, List<Page>> entry : authorsPostsMap.entrySet()) {
			String authorName = entry.getKey();
			List<Page> authorPosts = entry.getValue();
			int maxPosts = websiteConfig.getMaxPosts();
			if (!websiteConfig.isPaginationEnabled()) {
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
					String nextPageUrl = websiteConfig.getBaseUrl() + "/" + websiteConfig.getAuthorBase() + "/" + authorName
							+ "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = websiteConfig.getBaseUrl() + "/" + websiteConfig.getAuthorBase() + "/"
							+ authorName + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = pathService.getGeneratedHtmlDir() + File.separator + websiteConfig.getBaseUrl()
						+ File.separator + websiteConfig.getAuthorBase() + File.separator + authorName + File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("author", list.getFirst().getAuthor());
				String pageLayoutContent = templateService.formatContent(engine, context, websiteConfig.getAuthorLayout());
				pageLayoutContent = Jsoup.parse(pageLayoutContent).toString();
				fileService.write(currentPageFilePath, pageLayoutContent);
			}
		}
	}

}
