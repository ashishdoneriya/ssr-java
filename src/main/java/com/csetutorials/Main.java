package com.csetutorials;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csetutorials.beans.CatTag;
import com.csetutorials.beans.Page;
import com.csetutorials.beans.SiteConfig;
import com.csetutorials.utils.Constants;
import com.csetutorials.utils.DataUtils;
import com.csetutorials.utils.FileUtils;
import com.csetutorials.utils.PageUtils;
import com.csetutorials.utils.SiteUtils;
import com.csetutorials.utils.TemplateUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length > 0 && args[0].contains("help")) {
			System.out.println("--generate-config");
			System.exit(0);
		}
		if (args.length > 0 && args[0].contains("generate-config")) {
			generateSampleConfig();
		}
		String root = "/home/ashishdoneriya/projects/workbook-ssr-java";
		if (args.length != 0) {
			root = args[0];
		}

		SiteConfig siteConfig = SiteUtils.getSiteConfig(root);

		DataUtils.readData(siteConfig);

		List<Page> posts = PageUtils.createPostsMetaData(siteConfig);
		List<Page> pages = PageUtils.createPagesMetaData(siteConfig);

		Set<String> layouts = new HashSet<>();
		layouts.addAll(PageUtils.extractLayouts(posts));
		layouts.addAll(PageUtils.extractLayouts(pages));
		layouts.addAll(otherLayouts(siteConfig));
		TemplateUtils.createLayouts(siteConfig, layouts);

		TemplateUtils.setEngine(siteConfig);
		Map<CatTag, List<Page>> tagsPosts = PageUtils.extractTagsWithRelatedPosts(posts);
		Map<CatTag, List<Page>> catsPosts = PageUtils.extractCategoriesWithRelatedPosts(posts);
		Map<String, List<Page>> authorsPosts = PageUtils.extractAuthorWithRelatedPosts(posts);

		siteConfig.getRawConfig().put("tags", PageUtils.extractTags(posts, siteConfig));
		siteConfig.getRawConfig().put("categories", PageUtils.extractCategories(posts, siteConfig));
		siteConfig.getRawConfig().put("tagPosts", tagsPosts);
		siteConfig.getRawConfig().put("categoriesPosts", catsPosts);

		SiteUtils.generatePosts(posts, siteConfig, true);
		SiteUtils.generatePosts(pages, siteConfig, false);

		SiteUtils.generateLatestPostsPages(siteConfig);
		SiteUtils.generateCategoriesPages(siteConfig, catsPosts);
		SiteUtils.generateTagsPages(siteConfig, tagsPosts);
		SiteUtils.generateAuthorsPages(siteConfig, authorsPosts);

		FileUtils.copyDirRecursively(siteConfig.getStaticContentDir(), siteConfig.getGeneratedHtmlDir());
	}

	private static Collection<? extends String> otherLayouts(SiteConfig siteConfig) {
		Set<String> set = new HashSet<>(3);
		String layoutsDir = siteConfig.getLayoutsDir();
		if (new File(layoutsDir + File.separator + siteConfig.getCategoriesLayout()).exists()) {
			set.add(siteConfig.getCategoriesLayout());
		}
		if (new File(layoutsDir + File.separator + siteConfig.getCategoriesLayout()).exists()) {
			set.add(siteConfig.getTagsLayout());
		}
		if (new File(layoutsDir + File.separator + siteConfig.getLatestPostsLayout()).exists()) {
			set.add(siteConfig.getLatestPostsLayout());
		}
		if (new File(layoutsDir + File.separator + siteConfig.getAuthorLayout()).exists()) {
			set.add(siteConfig.getAuthorLayout());
		}
		return set;
	}

	public static void generateSampleConfig() {
		System.out.println(Constants.prettyGson.toJson(new SiteConfig()));
		System.exit(0);
	}

}
