package com.csetutorials.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.csetutorials.beans.Author;
import com.csetutorials.beans.OrganizationInfo;
import com.csetutorials.beans.Page;
import com.csetutorials.beans.SiteConfig;

public class JsonSchemaUtils {

	private SiteConfig siteConfig;

	private Map<String, Object> publisherSchema;

	private Map<String, Object> websiteSchema;

	private boolean isPersonItselfAnOrg;

	private String publisherId;

	private Map<String, String> publisherIdWrapper;

	private Map<String, Object> otherAuthors = new LinkedHashMap<>();

	public JsonSchemaUtils(SiteConfig siteConfig) {
		this.siteConfig = siteConfig;
		generatePublisherAndAuthorsSchema();
		generateWebsiteSchema();
	}

	public String generatePageSchema(Page page) {
		List<Object> graphList = new ArrayList<>();
		graphList.add(publisherSchema);
		graphList.add(websiteSchema);
		if (StringUtils.isNotBlank(page.getFeaturedImageAbsoluteUrl())) {
			graphList.add(generatePrimaryImageSchema(page));
		}
		graphList.add(generateWebpageSchema(page));
		graphList.add(generateArticleSchema(page));
		if (page.getAuthor() != null && (!isPersonItselfAnOrg
				|| (!this.siteConfig.getDefaultAuthor().equals(page.getAuthor().get("username"))))) {
			graphList.add(generatePersonSchema((Author) page.getAuthor()));
		}

		Map<String, Object> schemaMap = new LinkedHashMap<>(2);
		schemaMap.put("@context", "https://schema.org");
		schemaMap.put("@graph", graphList);
		return Constants.gson.toJson(schemaMap);
	}

	private Map<String, Object> generateWebpageSchema(Page page) {
		Map<String, Object> webpageSchema = new LinkedHashMap<>();
		webpageSchema.put("@type", "WebPage");
		webpageSchema.put("@id", page.getAbsoluteUrl() + "#webpage");
		webpageSchema.put("url", page.getAbsoluteUrl());
		webpageSchema.put("isPartOf", createMap("@id", this.siteConfig.getUrl() + "/#website"));
		if (StringUtils.isNotBlank(page.getFeaturedImageAbsoluteUrl())) {
			webpageSchema.put("primaryImageOfPage", createMap("@id", page.getAbsoluteUrl() + "#primaryimage"));
		}
		if (page.getCreated() != null) {
			webpageSchema.put("datePublished", DateUtils.getSiteMapString(page.getCreated()));
		}
		if (page.getUpdated() != null) {
			webpageSchema.put("dateModified", DateUtils.getSiteMapString(page.getUpdated()));
		}
		if (StringUtils.isNotBlank(page.getSummary())) {
			webpageSchema.put("description", page.getSummary());
		}
		webpageSchema.put("inLanguage", "en-US");

		Map<String, Object> readActionObj = new LinkedHashMap<>();
		readActionObj.put("@type", "ReadAction");
		readActionObj.put("target", new String[] { page.getAbsoluteUrl() });

		webpageSchema.put("potentialAction", new Object[] { readActionObj });

		return webpageSchema;
	}

	private Map<String, Object> generateArticleSchema(Page post) {
		Map<String, Object> articleSchema = new LinkedHashMap<>();
		articleSchema.put("@type", "Article");
		articleSchema.put("@id", post.getAbsoluteUrl() + "#article");
		articleSchema.put("isPartOf", createMap("@id", post.getAbsoluteUrl() + "#webpage"));
		articleSchema.put("author",
				createMap("@id", siteConfig.getUrl() + "/#/schema/person/" + post.getAuthor().get("username")));
		articleSchema.put("headline", post.getTitle());
		if (post.getCreated() != null) {
			articleSchema.put("datePublished", DateUtils.getSiteMapString(post.getCreated()));
		}
		if (post.getUpdated() != null) {
			articleSchema.put("dateModified", DateUtils.getSiteMapString(post.getUpdated()));
		}

		articleSchema.put("mainEntityOfPage", createMap("@id", post.getAbsoluteUrl() + "#webpage"));
		articleSchema.put("publisher", publisherIdWrapper);
		if (StringUtils.isNotBlank(post.getFeaturedImageAbsoluteUrl())) {
			articleSchema.put("image", createMap("@id", post.getAbsoluteUrl() + "#primaryimage"));
		}
		if (post.getTags() != null) {
			articleSchema.put("keywords",
					post.getTags().stream().map(catTag -> catTag.getShortcode()).collect(Collectors.joining(",")));
		}
		if (post.getCategories() != null) {
			articleSchema.put("articleSection", post.getCategories().stream().map(catTag -> catTag.getShortcode())
					.collect(Collectors.joining(",")));
		}
		articleSchema.put("inLanguage", "en-US");
		return articleSchema;
	}

	private Map<String, Object> generatePrimaryImageSchema(Page page) {
		Map<String, Object> imageSchema = new LinkedHashMap<>(4);
		imageSchema.put("@type", "ImageObject");
		imageSchema.put("@id", page.getAbsoluteUrl() + "#primaryimage");
		imageSchema.put("inLanguage", "en-US");
		imageSchema.put("url", page.getFeaturedImageAbsoluteUrl());
		return imageSchema;
	}

	private Object createMap(String key, String value) {
		Map<String, Object> map = new LinkedHashMap<>(1);
		map.put(key, value);
		return map;
	}

	private void generateWebsiteSchema() {
		this.websiteSchema = new LinkedHashMap<String, Object>(7);
		this.websiteSchema.put("@type", "WebSite");
		this.websiteSchema.put("@id", this.siteConfig.getUrl() + "/#website");
		this.websiteSchema.put("url", this.siteConfig.getUrl());
		this.websiteSchema.put("name", this.siteConfig.getTitle());
		this.websiteSchema.put("description", this.siteConfig.getDescription());
		this.websiteSchema.put("publisher", publisherIdWrapper);
		this.websiteSchema.put("inLanguage", "en-US");
	}

	private void generatePublisherAndAuthorsSchema() {
		this.isPersonItselfAnOrg = siteConfig.getSeoSettings().getIsPerson();

		if (isPersonItselfAnOrg) {
			Author author = siteConfig.getAuthors().get(siteConfig.getSeoSettings().getPersonUsername());
			Map<String, Object> authorSchema = generatePersonSchema(author);
			List<String> roles = new ArrayList<>(2);
			roles.add("Person");
			roles.add("Organization");
			authorSchema.put("@type", roles);

			this.publisherSchema = authorSchema;
		} else {
			Map<String, Object> orgSchema = new LinkedHashMap<>();
			orgSchema.put("@type", "Organization");
			publisherId = siteConfig.getUrl() + "/#organization";
			orgSchema.put("@id", publisherId);
			orgSchema.put("name", siteConfig.getTitle());
			orgSchema.put("url", siteConfig.getUrl());

			OrganizationInfo orgInfo = siteConfig.getSeoSettings().getOrganizationInfo();

			if (orgInfo.getSocialMediaLinks() != null) {
				orgSchema.put("sameAs", orgInfo.getSocialMediaLinks().getSocialLinks());
			}

			if (StringUtils.isNotBlank(orgInfo.getLogo())) {
				Map<String, Object> logoSchema = new LinkedHashMap<>(5);
				logoSchema.put("@type", "ImageObject");
				logoSchema.put("@id", siteConfig.getUrl() + "/#logo");
				logoSchema.put("inLanguage", "en-US");
				logoSchema.put("url", orgInfo.getLogo());
				logoSchema.put("caption", siteConfig.getTitle());

				orgSchema.put("logo", logoSchema);

				orgSchema.put("image", createMap("@id", siteConfig.getUrl() + "/#logo"));

			}
			this.publisherSchema = orgSchema;
		}
		this.publisherId = (String) this.publisherSchema.get("@id");
		this.publisherIdWrapper = new LinkedHashMap<String, String>();
		this.publisherIdWrapper.put("@id", this.publisherId);
	}

	@SuppressWarnings("unchecked")
	private synchronized Map<String, Object> generatePersonSchema(Author author) {
		if (otherAuthors.containsKey(author.getUsername())) {
			return (Map<String, Object>) otherAuthors.get(author.getUsername());
		}
		Map<String, Object> authorSchema = new LinkedHashMap<>();
		authorSchema.put("@type", "Person");
		String id = siteConfig.getUrl() + "/#/schema/person/" + author.getUsername();
		authorSchema.put("@id", id);
		authorSchema.put("name", author.getName());

		if (StringUtils.isNotBlank(author.getImageUrl())) {
			Map<String, Object> authorImageSchema = new LinkedHashMap<>(5);
			authorImageSchema.put("@type", "ImageObject");
			authorImageSchema.put("@id", siteConfig.getUrl() + "/#personlogo");
			authorImageSchema.put("inLanguage", "en-US");
			authorImageSchema.put("url", author.getImageUrl());
			authorImageSchema.put("caption", author.getName());

			authorSchema.put("image", authorImageSchema);

			Map<String, Object> logoSchema = new LinkedHashMap<>();
			logoSchema.put("@id", siteConfig.getUrl() + "/#personlogo");

			authorSchema.put("logo", logoSchema);
		}
		if (author.getSocialMediaLinks() != null) {
			authorSchema.put("sameAs", author.getSocialMediaLinks().getSocialLinks());
		}
		otherAuthors.put(author.getUsername(), authorSchema);
		return authorSchema;
	}

}
