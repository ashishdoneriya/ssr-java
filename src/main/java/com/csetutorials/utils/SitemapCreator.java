package com.csetutorials.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.csetutorials.beans.Page;
import com.csetutorials.beans.SiteConfig;
import com.csetutorials.contants.Paths;

public class SitemapCreator {

	public static void createSiteMap(SiteConfig siteConfig, List<Page> posts, List<Page> pages) throws IOException {
		String currentPageFilePath = Paths.getGeneratedHtmlDir() + File.separator + "main-sitemap.xsl";
		FileUtils.write(currentPageFilePath, FileUtils.getResourceContent("main-sitemap.xsl"));
		Date postsLastUpdated = createPageSiteMap(siteConfig, posts, true);
		Date pagesLastUpdated = createPageSiteMap(siteConfig, pages, false);
		createIndex(siteConfig, postsLastUpdated, pagesLastUpdated);
	}

	private static void createIndex(SiteConfig siteConfig, Date postsLastUpdated, Date pagesLastUpdated)
			throws FileNotFoundException {
		String xslPath = StringUtils.removeExtraSlash(siteConfig.getUrl() + siteConfig.getBaseUrl())
				+ "/main-sitemap.xsl";

		List<Map<String, String>> siteMaps = new ArrayList<>(2);

		Map<String, String> postSiteMap = new HashMap<>(2);
		postSiteMap.put("url", StringUtils
				.removeExtraSlash(siteConfig.getUrl() + "/" + siteConfig.getBaseUrl() + "/post-sitemap.xml"));
		postSiteMap.put("lastMod", DateUtils.getSiteMapString(postsLastUpdated));

		siteMaps.add(postSiteMap);

		Map<String, String> pageSiteMap = new HashMap<>(2);
		pageSiteMap.put("url", StringUtils
				.removeExtraSlash(siteConfig.getUrl() + "/" + siteConfig.getBaseUrl() + "/page-sitemap.xml"));
		pageSiteMap.put("lastMod", DateUtils.getSiteMapString(pagesLastUpdated));

		siteMaps.add(pageSiteMap);

		VelocityEngine engine = siteConfig.getEngine();
		VelocityContext context = new VelocityContext();

		context.put("xslPath", xslPath);
		context.put("siteMaps", siteMaps);

		String content = TemplateUtils.formatContent(engine, context, "sitemap_index.xml");

		String currentPageFilePath = Paths.getGeneratedHtmlDir() + File.separator + "sitemap_index.xml";
		FileUtils.write(currentPageFilePath, content);
	}

	private static Date createPageSiteMap(SiteConfig siteConfig, List<Page> pages, boolean arePosts)
			throws FileNotFoundException {

		String xslPath = StringUtils.removeExtraSlash(siteConfig.getUrl() + siteConfig.getBaseUrl())
				+ "/main-sitemap.xsl";

		String websiteUrl = null;
		if (arePosts) {
			websiteUrl = StringUtils.removeExtraSlash(siteConfig.getUrl() + "/" + siteConfig.getIndexPostsBase());
		} else {
			websiteUrl = StringUtils.removeExtraSlash(siteConfig.getUrl());
		}

		VelocityEngine engine = siteConfig.getEngine();
		VelocityContext context = new VelocityContext();
		Date lastUpdated = getLastUpdated(pages);

		context.put("xslPath", xslPath);
		context.put("websiteUrl", websiteUrl);
		context.put("websiteUpdated", DateUtils.getSiteMapString(lastUpdated));
		context.put("posts", pages);

		String content = TemplateUtils.formatContent(engine, context, "page-sitemap.xml");

		String currentPageFilePath = Paths.getGeneratedHtmlDir() + File.separator
				+ (arePosts ? "post-sitemap.xml" : "page-sitemap.xml");
		FileUtils.write(currentPageFilePath, content);
		return lastUpdated;
	}

	private static Date getLastUpdated(List<Page> pages) {
		Date date = null;
		for (Page page : pages) {
			if (date == null || date.getTime() > page.getUpdated().getTime()) {
				date = page.getUpdated();
			}
		}
		return date;
	}

}
