package com.csetutorials.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csetutorials.beans.CatTag;
import com.csetutorials.beans.Page;
import com.csetutorials.beans.SiteConfig;

public class PageUtils {

	public static List<Page> createPostsMetaData(SiteConfig siteConfig) throws Exception {
		List<Page> posts = new ArrayList<>();

		Map<String, CatTag> tagsMap = new HashMap<>();
		Map<String, CatTag> categoriesMap = new HashMap<>();
		Map<String, Map<String, Object>> authorsMap = new HashMap<>();

		for (File file : FileUtils.getFilesRecursively(siteConfig.getPostsDir())) {
			posts.add(getPageInfo(file, siteConfig, tagsMap, categoriesMap, authorsMap, true));
		}

		Collections.sort(posts, new Comparator<Page>() {
			@Override
			public int compare(Page page1, Page page2) {
				if (page1.getCreated() == null || page2.getCreated() == null) {
					return page1.getTitle().compareTo(page2.getTitle());
				}
				return page1.getCreated().getTime() <= page2.getCreated().getTime() ? -1 : 1;
			}
		});

		for (int i = 0; i < posts.size() - 1; i++) {
			posts.get(i).setNext(posts.get(i + 1));
		}

		for (int i = 1; i < posts.size(); i++) {
			posts.get(i).setPrevious(posts.get(i - 1));
		}

		List<CatTag> tags = new ArrayList<>(tagsMap.values());
		List<CatTag> cats = new ArrayList<>(categoriesMap.values());

		siteConfig.setTags(tags);
		siteConfig.setCategories(cats);
		siteConfig.setPosts(posts);

		siteConfig.getRawConfig().put("tags", tags);
		siteConfig.getRawConfig().put("categories", cats);
		siteConfig.getRawConfig().put("posts", posts);

		return posts;
	}

	public static List<Page> createPagesMetaData(SiteConfig siteConfig) throws Exception {
		List<Page> pages = new ArrayList<>();

		Map<String, Map<String, Object>> authorsMap = new HashMap<>();

		for (File file : FileUtils.getFilesRecursively(siteConfig.getPagesDir())) {
			pages.add(getPageInfo(file, siteConfig, null, null, authorsMap, false));
		}
		siteConfig.setPages(pages);
		siteConfig.getRawConfig().put("pages", pages);
		return pages;
	}

	private static Page getPageInfo(File file, SiteConfig siteConfig, Map<String, CatTag> tagsMap,
			Map<String, CatTag> categoriesMap, Map<String, Map<String, Object>> authorsMap, boolean isPost)
			throws Exception {
		String fileContent = FileUtils.getString(file);
		Map<String, String> rawParams = StringUtils.getRawParams(fileContent);

		Page page = new Page();

		// Setting post title
		String title = rawParams.get("title");
		if (title == null) {
			if (isPost) {
				throw new Exception("Title missing in post - " + file.getAbsolutePath());
			} else {
				title = file.getName();
			}
		}
		page.setTitle(title);

		// Setting post created date
		String sCreated = rawParams.get("created");
		if (sCreated == null) {
			sCreated = rawParams.get("date");
		}
		if (sCreated != null) {
			page.setCreated(DateUtils.parse(sCreated, siteConfig.getPostWritingDataFormat()));
		}

		// Setting post updated date
		String sUpdated = rawParams.get("updated");
		if (sUpdated == null) {
			sUpdated = rawParams.get("lastmod");
		}
		if (sUpdated != null) {
			page.setUpdated(DateUtils.parse(sUpdated, siteConfig.getPostWritingDataFormat()));
		} else {
			page.setUpdated(page.getCreated());
		}

		// Setting is draft
		String sIsDraft = rawParams.get("isDraft");
		if (sIsDraft != null) {
			page.setDraft(Boolean.valueOf(sIsDraft));
		}

		if (isPost) {
			// Setting categories
			page.setCategories(
					createCategories(categoriesMap, siteConfig, StringUtils.parseList(rawParams.get("categories"))));

			page.setTags(createTags(tagsMap, siteConfig, StringUtils.parseList(rawParams.get("tags"))));
		}

		// Setting author info
		String author = rawParams.get("author");
		if (StringUtils.isBlank(author)) {
			author = siteConfig.getAuthor();
		}
		page.setAuthor(createAuthor(authorsMap, siteConfig, author));

		// Setting summary
		page.setSummary(rawParams.get("summary"));

		// Setting layout
		String layout = rawParams.get("layout");
		if (StringUtils.isBlank(layout)) {
			layout = isPost ? siteConfig.getPostLayout() : siteConfig.getPageLayout();

		}
		page.setLayout(layout);

		// Setting slug
		String slug = rawParams.get("slug");
		if (StringUtils.isBlank(slug)) {
			slug = page.getTitle().toLowerCase().replaceAll(" ", "-");

		}
		page.setSlug(slug);

		// Setting permalink
		String permalink = rawParams.get("permalink");
		if (StringUtils.isBlank(permalink)) {
			permalink = siteConfig.getPostPermalink();
		}
		permalink = formatPermalink(page, permalink);
		page.setPermalink(permalink);

		// Setting url
		page.setUrl(createUrl(siteConfig.getBaseUrl(), page.getPermalink()));

		// Setting file obj
		page.setFile(file);

		// Setting rawParams
		page.setRawParams(rawParams);

		return page;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> createAuthor(Map<String, Map<String, Object>> authorsMap, SiteConfig siteConfig,
			String author) {

		Map<String, Object> temp1 = (Map<String, Object>) siteConfig.getData().get("authors");
		if (temp1 == null) {
			return null;
		}
		Map<String, Object> temp2 = (Map<String, Object>) temp1.get(author);
		String url = siteConfig.getBaseUrl() + "/" + siteConfig.getAuthorBase() + "/" + author;
		url = StringUtils.removeExtraSlash(url);
		temp2.put("url", url);
		return temp2;
	}

	private static List<CatTag> createTags(Map<String, CatTag> tagsMap, SiteConfig siteConfig, List<String> sTagsList) {
		List<CatTag> tags = new ArrayList<>();
		for (String sTag : sTagsList) {
			CatTag tag = tagsMap.get(sTag);
			if (tag != null) {
				tags.add(tag);
			} else {
				CatTag tagObj = new CatTag();
				tagObj.setShortcode(sTag);
				tagObj.setName(StringUtils.toFirstCharUpperAll(sTag));
				String url = siteConfig.getBaseUrl() + "/" + siteConfig.getTagBase() + "/" + sTag;
				tagObj.setUrl(url);
				tags.add(tagObj);
				tagsMap.put(sTag, tagObj);
			}
		}
		return tags;
	}

	private static List<CatTag> createCategories(Map<String, CatTag> catsMap, SiteConfig siteConfig,
			List<String> sCatList) {
		List<CatTag> categories = new ArrayList<>();
		if (sCatList == null) {
			sCatList = new ArrayList<>(1);
			sCatList.add(siteConfig.getCategory());
		}
		for (String sCat : sCatList) {
			CatTag tag = catsMap.get(sCat);
			if (tag != null) {
				categories.add(tag);
			} else {
				CatTag catObj = new CatTag();
				catObj.setShortcode(sCat);
				catObj.setName(StringUtils.toFirstCharUpperAll(sCat));
				String url = StringUtils
						.removeExtraSlash(siteConfig.getBaseUrl() + "/" + siteConfig.getCategoryBase() + "/" + sCat);
				catObj.setUrl(url);
				categories.add(catObj);
				catsMap.put(sCat, catObj);
			}
		}
		return categories;
	}

	private static String createUrl(String baseUrl, String postPermalink) {
		// TODO : Added more
		return StringUtils.removeExtraSlash(baseUrl + postPermalink);

	}

	private static String formatPermalink(Page page, String permalink) {
		String slug = page.getSlug();
		// TODO : Added more
		return permalink.replaceAll(":slug", slug).replaceAll("/+", "/");
	}

	public static Set<String> extractLayouts(List<Page> pages) {
		Set<String> layouts = new HashSet<>();
		for (Page page : pages) {
			layouts.add(page.getLayout());
		}
		return layouts;
	}

	public static List<CatTag> extractCategories(List<Page> pages, SiteConfig siteConfig) {
		Set<CatTag> categories = new HashSet<>();
		for (Page postInfo : pages) {
			categories.addAll(postInfo.getCategories());
		}
		return new ArrayList<>(categories);
	}

	public static List<CatTag> extractTags(List<Page> pages, SiteConfig siteConfig) {
		Set<CatTag> tags = new HashSet<>();
		for (Page postInfo : pages) {
			tags.addAll(postInfo.getTags());
		}
		return new ArrayList<>(tags);
	}

	public static Map<CatTag, List<Page>> extractCategoriesWithRelatedPosts(List<Page> postsMeta) {
		Map<CatTag, List<Page>> catWithPosts = new HashMap<>();
		for (Page postInfo : postsMeta) {
			for (CatTag cat : postInfo.getCategories()) {
				List<Page> cattagPosts = catWithPosts.get(cat);
				if (cattagPosts == null) {
					cattagPosts = new ArrayList<>(2);
					catWithPosts.put(cat, cattagPosts);
				}
				cattagPosts.add(postInfo);
			}
		}
		return catWithPosts;
	}

	public static Map<CatTag, List<Page>> extractTagsWithRelatedPosts(List<Page> postsMeta) {
		Map<CatTag, List<Page>> tagWithPosts = new HashMap<>();
		for (Page postInfo : postsMeta) {
			List<CatTag> list = postInfo.getTags();
			if (list == null) {
				continue;
			}
			for (CatTag tag : list) {
				List<Page> tagPosts = tagWithPosts.get(tag);
				if (tagPosts == null) {
					tagPosts = new ArrayList<>(2);
					tagWithPosts.put(tag, tagPosts);
				}
				tagPosts.add(postInfo);
			}
		}
		return tagWithPosts;
	}

	public static Map<String, List<Page>> extractAuthorWithRelatedPosts(List<Page> postsMeta) {
		Map<String, List<Page>> authorWithPostsMap = new HashMap<>();
		for (Page postInfo : postsMeta) {
			String author = (String) postInfo.getAuthor().get("shortcode");
			List<Page> authorPosts = authorWithPostsMap.get(author);
			if (authorPosts == null) {
				authorPosts = new ArrayList<>(2);
				authorWithPostsMap.put(author, authorPosts);
			}
			authorPosts.add(postInfo);
		}
		return authorWithPostsMap;
	}

}
