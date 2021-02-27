package com.csetutorials.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.csetutorials.beans.Author;
import com.csetutorials.beans.SiteConfig;
import com.csetutorials.beans.SocialMediaLinks;
import com.csetutorials.contants.Paths;
import com.google.gson.JsonSyntaxException;

public class DataUtils {

	public static void readData(SiteConfig siteConfig) throws JsonSyntaxException, IOException {
		File dataDir = new File(Paths.getDataDir());
		Map<String, Object> map = new HashMap<>();
		if (!dataDir.exists() || dataDir.list().length == 0) {
			siteConfig.setData(new HashMap<String, Object>(1));
			return;
		}

		for (File file : dataDir.listFiles()) {
			if (file.isFile()) {
				String name = file.getName();
				if (name.endsWith(".json")) {
					name = name.substring(0, name.indexOf(".json"));
				}
				map.put(name, getObject(file));
			} else {
				map.put(file.getName(), readDir(file));
			}
		}
		siteConfig.setData(map);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAuthorInfo(Map<String, Object> map, String authorUserName,
			SiteConfig siteConfig) {
		if (authorUserName == null || authorUserName.isEmpty()) {
			return null;
		}
		Map<String, Object> temp1 = (Map<String, Object>) map.get("authors");
		if (temp1 == null) {
			return null;
		}
		Map<String, Object> temp2 = (Map<String, Object>) temp1.get(authorUserName);
		return temp2;
	}

	private static Object getObject(File file) throws JsonSyntaxException, IOException {
		return Constants.gson.fromJson(FileUtils.getString(file.getAbsolutePath()), Object.class);
	}

	private static Object readDir(File dir) throws JsonSyntaxException, IOException {
		Map<String, Object> map = new HashMap<>();
		if (!dir.exists() || dir.list().length == 0) {
			return map;
		}
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				String name = file.getName();
				if (name.endsWith(".json")) {
					name = name.substring(0, name.indexOf(".json"));
				}
				map.put(name, getObject(file));
			} else {
				map.put(file.getName(), readDir(file));
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static void loadAllAuthors(SiteConfig siteConfig) {
		Map<String, Object> map = siteConfig.getData();
		Map<String, Object> authors1 = (Map<String, Object>) map.get("authors");
		Map<String, Author> authors = new HashMap<>();
		for (Map.Entry<String, Object> e : authors1.entrySet()) {
			authors.put(e.getKey(), Constants.gson.fromJson(Constants.gson.toJson(e.getValue()), Author.class));
		}
		siteConfig.setAuthors(authors);

		if (siteConfig.getSeoSettings().getIsPerson()) {
			siteConfig.setPublisherSocialLinks(
					authors.get(siteConfig.getSeoSettings().getPersonUsername()).getSocialMediaLinks());
		} else {
			siteConfig.setPublisherSocialLinks(siteConfig.getSeoSettings().getOrganizationInfo().getSocialMediaLinks());
		}
		siteConfig.getRawConfig().put("publisherSocialLinks", siteConfig.getPublisherSocialLinks());
		setTwitterUsername(siteConfig.getPublisherSocialLinks());
	}

	private static void setTwitterUsername(SocialMediaLinks publisherSocialLinks) {
		if (publisherSocialLinks == null) {
			return;
		}
		String twitterUrl = publisherSocialLinks.getTwitterUrl();
		if (StringUtils.isNotBlank(twitterUrl)) {
			int index = twitterUrl.lastIndexOf("/");
			String twitterUsername = twitterUrl.substring(index + 1);
			if (twitterUsername.contains("?")) {
				twitterUsername = twitterUsername.substring(0, twitterUsername.indexOf('?'));
			}
			publisherSocialLinks.setTwitterUsername(twitterUsername);
		}
	}

}
