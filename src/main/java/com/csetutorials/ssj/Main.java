package com.csetutorials.ssj;

import antlr.HTMLCodeGenerator;
import com.csetutorials.ssj.beans.*;
import com.csetutorials.ssj.contants.DefaultDirs;
import com.csetutorials.ssj.contants.SSJPaths;
import com.csetutorials.ssj.services.*;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service
public class Main {

	@Autowired
	SSJPaths SSJPaths;
	@Autowired
	SiteService siteService;
	@Autowired
	TemplateService templateService;
	@Autowired
	PageService pageService;

	@Autowired
	JsonService jsonService;


	public void process(String[] args) {

		dataLoader.load(CommandLineService.getCommands(args).getOptionValue("build", new File("").getAbsolutePath()));
		htmlPagesGenerator.generateHtmlPages();

		templateService.createEngine(websiteInfo);
		Map<Category, List<Page>> tagsPosts = pageService.extractTagsWithRelatedPosts(posts);
		Map<Category, List<Page>> catsPosts = pageService.extractCategoriesWithRelatedPosts(posts);
		Map<String, List<Page>> authorsPosts = pageService.extractAuthorWithRelatedPosts(posts);

		websiteInfo.getRawConfig().put("tags", pageService.extractTags(posts));
		websiteInfo.getRawConfig().put("categories", pageService.extractCategories(posts));
		websiteInfo.getRawConfig().put("tagPosts", tagsPosts);
		websiteInfo.getRawConfig().put("categoriesPosts", catsPosts);
		siteService.generatePosts(posts, websiteInfo, true);
		siteService.generatePosts(pages, websiteInfo, false);

		siteService.generateLatestPostsPages(websiteInfo);
		siteService.generateCategoriesPages(websiteInfo, catsPosts);
		siteService.generateTagsPages(websiteInfo, tagsPosts);
		siteService.generateAuthorsPages(websiteInfo, authorsPosts);
		sitemapCreator.createSiteMap(websiteInfo, posts, pages);

	}





}
