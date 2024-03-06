package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.Page;
import com.csetutorials.ssj.beans.WebsiteInfo;
import com.csetutorials.ssj.contants.SSJPaths;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class SitemapCreator {

	@Autowired
	SSJPaths SSJPaths;
	@Autowired
	TemplateService templateService;
	@Autowired
	FileService fileService;

	public void createSiteMap(WebsiteInfo websiteInfo, List<Page> posts, List<Page> pages) {
		String currentPageFilePath = SSJPaths.getGeneratedHtmlDir() + File.separator + "main-sitemap.xsl";
		fileService.write(currentPageFilePath, fileService.getResourceContent("main-sitemap.xsl"));
		Date postsLastUpdated = createPageSiteMap(websiteInfo, posts, true);
		Date pagesLastUpdated = createPageSiteMap(websiteInfo, pages, false);
		createIndex(websiteInfo, postsLastUpdated, pagesLastUpdated);
	}

	private void createIndex(WebsiteInfo websiteInfo, Date postsLastUpdated, Date pagesLastUpdated) {
		String xslPath = StringUtils.removeExtraSlash(websiteInfo.getUrl() + websiteInfo.getBaseUrl())
				+ "/main-sitemap.xsl";

		List<Map<String, String>> siteMaps = new ArrayList<>(2);

		Map<String, String> postSiteMap = new HashMap<>(2);
		postSiteMap.put("url", StringUtils
				.removeExtraSlash(websiteInfo.getUrl() + "/" + websiteInfo.getBaseUrl() + "/post-sitemap.xml"));
		postSiteMap.put("lastMod", DateUtils.getSiteMapString(postsLastUpdated));

		siteMaps.add(postSiteMap);

		Map<String, String> pageSiteMap = new HashMap<>(2);
		pageSiteMap.put("url", StringUtils
				.removeExtraSlash(websiteInfo.getUrl() + "/" + websiteInfo.getBaseUrl() + "/page-sitemap.xml"));
		pageSiteMap.put("lastMod", DateUtils.getSiteMapString(pagesLastUpdated));

		siteMaps.add(pageSiteMap);

		VelocityEngine engine = websiteInfo.getVelocityEngine();
		VelocityContext context = new VelocityContext();

		context.put("xslPath", xslPath);
		context.put("siteMaps", siteMaps);

		String content = templateService.formatContent(engine, context, "sitemap_index.xml");

		String currentPageFilePath = SSJPaths.getGeneratedHtmlDir() + File.separator + "sitemap_index.xml";
		fileService.write(currentPageFilePath, content);
	}

	private Date createPageSiteMap(WebsiteInfo websiteInfo, List<Page> pages, boolean arePosts) {

		String xslPath = StringUtils.removeExtraSlash(websiteInfo.getUrl() + websiteInfo.getBaseUrl())
				+ "/main-sitemap.xsl";

		String websiteUrl;
		if (arePosts) {
			websiteUrl = StringUtils.removeExtraSlash(websiteInfo.getUrl() + "/" + websiteInfo.getLatestPostsBase());
		} else {
			websiteUrl = StringUtils.removeExtraSlash(websiteInfo.getUrl());
		}

		VelocityEngine engine = websiteInfo.getVelocityEngine();
		VelocityContext context = new VelocityContext();
		Date lastUpdated = getLastUpdated(pages);

		context.put("xslPath", xslPath);
		context.put("websiteUrl", websiteUrl);
		context.put("websiteUpdated", DateUtils.getSiteMapString(lastUpdated));
		context.put("posts", pages);

		String content = templateService.formatContent(engine, context, "page-sitemap.xml");

		String currentPageFilePath = SSJPaths.getGeneratedHtmlDir() + File.separator
				+ (arePosts ? "post-sitemap.xml" : "page-sitemap.xml");
		fileService.write(currentPageFilePath, content);
		return lastUpdated;
	}

	private Date getLastUpdated(List<Page> pages) {
		Date date = null;
		for (Page page : pages) {
			if (date == null || date.getTime() > page.getUpdated().getTime()) {
				date = page.getUpdated();
			}
		}
		return date;
	}

}
