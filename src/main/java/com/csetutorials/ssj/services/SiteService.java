package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.*;
import com.csetutorials.ssj.contants.Layouts;
import com.csetutorials.ssj.contants.SSJPaths;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
	SSJPaths SSJPaths;
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

	public void generateLatestPostsPages(WebsiteInfo websiteInfo) {
		if (templateService.isTemplateNotAvailable(websiteInfo, Layouts.latestPosts)) {
			return;
		}

		VelocityEngine engine = websiteInfo.getVelocityEngine();
		VelocityContext context = new VelocityContext();

		context.put("site", websiteInfo.getRawConfig());
		context.put("contentType", "latestPosts");
		context.put("data", websiteInfo.getData());
		context.put("dateUtils", new DateUtils());

		int maxPosts = websiteInfo.getMaxPosts();
		List<Page> posts = websiteInfo.getPosts();
		if (!websiteInfo.isPaginationEnabled()) {
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
				String nextPageUrl = websiteInfo.getBaseUrl() + "/" + websiteInfo.getLatestPostsBase() + "/page/" + i;
				nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
				paginator.setNextPageUrl(nextPageUrl);
			} else if (i != 1) {
				String previousPageUrl = websiteInfo.getBaseUrl() + "/" + websiteInfo.getLatestPostsBase()
						+ (i == 2 ? "" : "/page/" + i);
				previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
				paginator.setPreviousPageUrl(previousPageUrl);
			}
			String currentPageFilePath = SSJPaths.getGeneratedHtmlDir() + File.separator + websiteInfo.getBaseUrl()
					+ File.separator + websiteInfo.getLatestPostsBase() + File.separator
					+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
			context.put("paginator", paginator);
			String pageLayoutContent = templateService.formatContent(engine, context, websiteInfo.getLatestPostsLayout());
			pageLayoutContent = Jsoup.parse(pageLayoutContent).toString();
			fileService.write(currentPageFilePath, pageLayoutContent);
		}
	}

	public void generateCategoriesPages(WebsiteInfo websiteInfo, Map<Category, List<Page>> catsWithRelatedPosts) {
		if (templateService.isTemplateNotAvailable(websiteInfo, Layouts.category)) {
			return;
		}

		VelocityEngine engine = websiteInfo.getVelocityEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", websiteInfo.getRawConfig());
		context.put("contentType", "category");
		context.put("data", websiteInfo.getData());
		context.put("dateUtils", new DateUtils());

		for (Entry<Category, List<Page>> entry : catsWithRelatedPosts.entrySet()) {
			Category cat = entry.getKey();
			List<Page> catPosts = entry.getValue();
			int maxPosts = websiteInfo.getMaxPosts();
			if (!websiteInfo.isPaginationEnabled()) {
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
					String nextPageUrl = websiteInfo.getBaseUrl() + "/" + websiteInfo.getCategoryBase() + "/"
							+ cat.getShortcode() + "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = websiteInfo.getBaseUrl() + "/" + websiteInfo.getCategoryBase() + "/"
							+ cat.getShortcode() + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = SSJPaths.getGeneratedHtmlDir() + File.separator + websiteInfo.getBaseUrl()
						+ File.separator + websiteInfo.getCategoryBase() + File.separator + cat.getShortcode()
						+ File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("category", cat);
				String pageLayoutContent = templateService.formatContent(engine, context,
						websiteInfo.getCategoriesLayout());
				pageLayoutContent = Jsoup.parse(pageLayoutContent).toString();
				fileService.write(currentPageFilePath, pageLayoutContent);
			}
		}
	}

	public void generateTagsPages(WebsiteInfo websiteInfo, Map<Category, List<Page>> tagsWithRelatedPosts) {
		if (templateService.isTemplateNotAvailable(websiteInfo, Layouts.tag)) {
			return;
		}

		VelocityEngine engine = websiteInfo.getVelocityEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", websiteInfo.getRawConfig());
		context.put("contentType", "tag");
		context.put("data", websiteInfo.getData());
		context.put("dateUtils", new DateUtils());

		for (Entry<Category, List<Page>> entry : tagsWithRelatedPosts.entrySet()) {
			Category tag = entry.getKey();
			List<Page> tagPosts = entry.getValue();
			int maxPosts = websiteInfo.getMaxPosts();
			if (!websiteInfo.isPaginationEnabled()) {
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
					String nextPageUrl = websiteInfo.getBaseUrl() + "/" + websiteInfo.getTagBase() + "/" + tag.getName()
							+ "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = websiteInfo.getBaseUrl() + "/" + websiteInfo.getTagBase() + "/"
							+ tag.getName() + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = SSJPaths.getGeneratedHtmlDir() + File.separator + websiteInfo.getBaseUrl()
						+ File.separator + websiteInfo.getTagBase() + File.separator + tag.getName() + File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("tag", tag);
				String pageLayoutContent = templateService.formatContent(engine, context, websiteInfo.getTagsLayout());
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

	public void generatePosts(List<Page> pages, WebsiteInfo websiteInfo, boolean isPost) {
		VelocityContext context = new VelocityContext();
		VelocityEngine engine = websiteInfo.getVelocityEngine();
		context.put("site", websiteInfo.getRawConfig());
		context.put("contentType", isPost ? "post" : "page");
		context.put("data", websiteInfo.getData());
		context.put("dateUtils", new DateUtils());
		context.put("StringUtils", StringUtils.class);
		final String metaTagsFormat = fileService.getResourceContent("post-meta-tags.html");
		templateService.addTemplate(websiteInfo, "ssj-meta-tags", metaTagsFormat);
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
			templateService.addTemplate(websiteInfo, "test-template-ssr", content);
			content = templateService.formatContent(engine, context, "test-template-ssr");
			List<Image> images = extractImages(websiteInfo, content);
			content = formatAnchorTags(content);
			page.setImages(images);

			map.put("content", content);
			context.put("page", map);
			final String metaTags = templateService.formatContent(engine, context, "ssj-meta-tags");
			context.put("seoSettings", metaTags);
			String postLayoutContent = templateService.formatContent(engine, context, page.getLayout());
			postLayoutContent = Jsoup.parse(postLayoutContent).toString();
			write(page.getPermalink(), postLayoutContent,
					isPost ? websiteInfo.isPostUglyUrlEnabled() : websiteInfo.isPageUglyUrlEnabled());
		}
	}

	private List<Image> extractImages(WebsiteInfo websiteInfo, String content) {
		Document doc = Jsoup.parse(content);
		Elements imageElements = doc.select("img");
		List<Image> images = new ArrayList<>(1);
		for (Element imageEle : imageElements) {
			String src = imageEle.attr("src");

			if (!src.startsWith("http")) {
				if (!src.startsWith("/") && !websiteInfo.getBaseUrl().equals("/")) {
					src = websiteInfo.getBaseUrl() + src;
				}
				src = websiteInfo.getUrl() + "/" + src;
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
		String path = SSJPaths.getGeneratedHtmlDir() + permalink;
		path = path.replaceAll("/+", "/").replaceAll("/", File.separator);
		File file = new File(path);
		fileService.mkdirs(file.getParentFile());
		fileService.write(file.getAbsolutePath(), content);
	}

	public void generateAuthorsPages(WebsiteInfo websiteInfo, Map<String, List<Page>> authorsPostsMap) {
		if (templateService.isTemplateNotAvailable(websiteInfo, Layouts.author)) {
			return;
		}

		VelocityEngine engine = websiteInfo.getVelocityEngine();
		VelocityContext context = new VelocityContext();
		context.put("site", websiteInfo.getRawConfig());
		context.put("contentType", "author");
		context.put("data", websiteInfo.getData());
		context.put("dateUtils", new DateUtils());
		for (Entry<String, List<Page>> entry : authorsPostsMap.entrySet()) {
			String authorName = entry.getKey();
			List<Page> authorPosts = entry.getValue();
			int maxPosts = websiteInfo.getMaxPosts();
			if (!websiteInfo.isPaginationEnabled()) {
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
					String nextPageUrl = websiteInfo.getBaseUrl() + "/" + websiteInfo.getAuthorBase() + "/" + authorName
							+ "/page/" + i;
					nextPageUrl = StringUtils.removeExtraSlash(nextPageUrl);
					paginator.setNextPageUrl(nextPageUrl);
				} else if (i != 1) {
					String previousPageUrl = websiteInfo.getBaseUrl() + "/" + websiteInfo.getAuthorBase() + "/"
							+ authorName + (i == 2 ? "" : "/page/" + i);
					previousPageUrl = StringUtils.removeExtraSlash(previousPageUrl);
					paginator.setPreviousPageUrl(previousPageUrl);
				}
				String currentPageFilePath = SSJPaths.getGeneratedHtmlDir() + File.separator + websiteInfo.getBaseUrl()
						+ File.separator + websiteInfo.getAuthorBase() + File.separator + authorName + File.separator
						+ (i == 1 ? "index.html" : "/page" + File.separator + i + File.separator + "index.html");
				context.put("paginator", paginator);
				context.put("author", list.getFirst().getAuthor());
				String pageLayoutContent = templateService.formatContent(engine, context, websiteInfo.getAuthorLayout());
				pageLayoutContent = Jsoup.parse(pageLayoutContent).toString();
				fileService.write(currentPageFilePath, pageLayoutContent);
			}
		}
	}

}
