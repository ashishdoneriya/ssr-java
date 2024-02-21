package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.Author;
import com.csetutorials.ssj.beans.CatTag;
import com.csetutorials.ssj.beans.Page;
import com.csetutorials.ssj.beans.SiteConfig;
import com.csetutorials.ssj.contants.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class PageService {

	@Autowired
	PathService pathService;
	@Autowired
	FileService fileService;

	public List<Page> createPostsMetaData(SiteConfig siteConfig) throws Exception {
		List<Page> posts = new ArrayList<>();

		Map<String, CatTag> tagsMap = new HashMap<>();
		Map<String, CatTag> categoriesMap = new HashMap<>();
		Map<String, Author> authorsMap = new HashMap<>();

		List<File> files = fileService.getFilesRecursively(pathService.getPostsDir());
		for (File file : files) {
			Page post = getPageInfo(file, siteConfig, tagsMap, categoriesMap, authorsMap, true);
			if (post != null) {
				posts.add(post);
			}
		}

		posts.sort((page1, page2) -> {
			if (page1.getCreated() == null || page2.getCreated() == null) {
				return page1.getTitle().compareTo(page2.getTitle());
			}
			return page1.getCreated().getTime() >= page2.getCreated().getTime() ? -1 : 1;
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

	public List<Page> createPagesMetaData(SiteConfig siteConfig) throws Exception {
		List<Page> pages = new ArrayList<>();

		Map<String, Author> authorsMap = new HashMap<>();

		for (File file : fileService.getFilesRecursively(pathService.getPagesDir())) {
			pages.add(getPageInfo(file, siteConfig, null, null, authorsMap, false));
		}
		siteConfig.setPages(pages);
		siteConfig.getRawConfig().put("pages", pages);
		return pages;
	}

	private Page getPageInfo(File file, SiteConfig siteConfig, Map<String, CatTag> tagsMap,
			Map<String, CatTag> categoriesMap, Map<String, Author> authorsMap, boolean isPost) throws Exception {
		String fileContent = fileService.getString(file);
		Map<String, Object> rawParams = StringUtils.getRawParams(fileContent);
		Boolean isDraft = (Boolean) rawParams.get("isDraft");
		if (isDraft != null && isDraft) {
			return null;
		}
		Page page = new Page();

		// Setting post title
		String title = (String) rawParams.get("title");
		if (title == null) {
			if (isPost) {
				throw new Exception("Title missing in post - " + file.getAbsolutePath());
			} else {
				title = file.getName();
			}
		}
		page.setTitle(title);

		page.setCreated((Date) rawParams.get("created"));
		page.setUpdated((Date) rawParams.get("updated"));

		page.setLastMod(DateUtils.getSiteMapString(page.getUpdated()));

		// Setting is draft
		String sIsDraft = (String) rawParams.get("isDraft");
		if (sIsDraft != null) {
			page.setDraft(Boolean.parseBoolean(sIsDraft));
		}

		if (isPost) {
			// Setting categories
			page.setCategories(createCategories(categoriesMap, siteConfig, (List<String>) rawParams.get("categories")));
			page.setTags(createTags(tagsMap, siteConfig, (List<String>) rawParams.get("tags")));
		}

		// Setting author info
		String author = (String) rawParams.get("author");
		if (StringUtils.isBlank(author)) {
			author = siteConfig.getDefaultAuthor();
		}
		page.setAuthor(siteConfig.getAuthors().get(author));

		// Setting summary
		page.setSummary((String) rawParams.get("summary"));
		if (page.getSummary() == null) {
			page.setSummary((String) rawParams.get("description"));
		}

		// Setting layout
		String layout = (String) rawParams.get("layout");
		if (StringUtils.isBlank(layout)) {
			layout = isPost ? siteConfig.getPostLayout() : siteConfig.getPageLayout();

		}
		page.setLayout(layout);

		// Setting slug
		String slug = (String) rawParams.get("slug");
		if (StringUtils.isBlank(slug)) {
			slug = file.getName();
			if (slug.contains(".")) {
				slug = slug.substring(0, file.getName().lastIndexOf('.'));
			}
			if (slug.contains(" ")) {
				slug = slug.replaceAll(" ", "-");
			}
			slug = slug.toLowerCase();
		}
		page.setSlug(slug);
		// Setting permalink
		String permalink = (String) rawParams.get("permalink");
		if (StringUtils.isBlank(permalink)) {
			permalink = siteConfig.getPostPermalink();
		}
		permalink = formatPermalink(page, permalink);
		page.setPermalink(permalink);

		// Setting url
		page.setUrl(createUrl(siteConfig, page.getPermalink()));

		page.setAbsoluteUrl(StringUtils.removeExtraSlash(siteConfig.getUrl() + "/" + page.getUrl()));

		// Setting file obj
		page.setFile(file);

		// Setting rawParams
		page.setRawParams(rawParams);

		return page;
	}

	private List<CatTag> createTags(Map<String, CatTag> tagsMap, SiteConfig siteConfig, List<String> sTagsList) {
		List<CatTag> tags = new ArrayList<>();
		if (sTagsList == null) {
			return tags;
		}
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

	private List<CatTag> createCategories(Map<String, CatTag> catsMap, SiteConfig siteConfig,
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
				catObj.setName(StringUtils.toFirstCharUpperAll(sCat).replaceAll("-", " "));
				String url = StringUtils
						.removeExtraSlash(siteConfig.getBaseUrl() + "/" + siteConfig.getCategoryBase() + "/" + sCat);
				catObj.setUrl(url);
				categories.add(catObj);
				catsMap.put(sCat, catObj);
			}
		}
		return categories;
	}

	private String createUrl(SiteConfig siteConfig, String postPermalink) {
		return StringUtils.removeExtraSlash(siteConfig.getBaseUrl() + "/" + postPermalink);
	}

	private String formatPermalink(Page page, String permalink) {
		String slug = page.getSlug();
		// TODO : Added more
		return permalink.replaceAll(":slug", slug).replaceAll("/+", "/");
	}

	public List<CatTag> extractCategories(List<Page> pages) {
		Set<CatTag> categories = new HashSet<>();
		for (Page postInfo : pages) {
			categories.addAll(postInfo.getCategories());
		}
		List<CatTag> list = new ArrayList<>(categories);
		list.sort(Comparator.comparing(CatTag::getShortcode));
		return list;
	}

	public List<CatTag> extractTags(List<Page> pages) {
		Set<CatTag> tags = new HashSet<>();
		for (Page postInfo : pages) {
			tags.addAll(postInfo.getTags());
		}
		return new ArrayList<>(tags);
	}

	public Map<CatTag, List<Page>> extractCategoriesWithRelatedPosts(List<Page> postsMeta) {
		Map<CatTag, List<Page>> catWithPosts = new HashMap<>();
		for (Page postInfo : postsMeta) {
			for (CatTag cat : postInfo.getCategories()) {
				List<Page> cattagPosts = catWithPosts.computeIfAbsent(cat, k -> new ArrayList<>(2));
				cattagPosts.add(postInfo);
			}
		}
		return catWithPosts;
	}

	public Map<CatTag, List<Page>> extractTagsWithRelatedPosts(List<Page> postsMeta) {
		Map<CatTag, List<Page>> tagWithPosts = new HashMap<>();
		for (Page postInfo : postsMeta) {
			List<CatTag> list = postInfo.getTags();
			if (list == null) {
				continue;
			}
			for (CatTag tag : list) {
				List<Page> tagPosts = tagWithPosts.computeIfAbsent(tag, k -> new ArrayList<>(2));
				tagPosts.add(postInfo);
			}
		}
		return tagWithPosts;
	}

	public Map<String, List<Page>> extractAuthorWithRelatedPosts(List<Page> postsMeta) {
		Map<String, List<Page>> authorWithPostsMap = new HashMap<>();
		for (Page postInfo : postsMeta) {
			String author = postInfo.getAuthor().getUsername();
			List<Page> authorPosts = authorWithPostsMap.computeIfAbsent(author, k -> new ArrayList<>(2));
			authorPosts.add(postInfo);
		}
		return authorWithPostsMap;
	}

}
