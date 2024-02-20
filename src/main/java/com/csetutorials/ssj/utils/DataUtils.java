package com.csetutorials.ssj.utils;

import com.csetutorials.ssj.beans.Author;
import com.csetutorials.ssj.beans.SiteConfig;
import com.csetutorials.ssj.beans.SocialMediaLinks;
import com.csetutorials.ssj.contants.Paths;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

	public static void loadAllAuthors(SiteConfig siteConfig) throws IOException {
		File authorsDir = new File(Paths.getAuthorsDir());
		Map<String, Author> authors = new HashMap<>();
		for (File authorFile : authorsDir.listFiles()) {
			String content;
			try {
				content = FileUtils.getString(authorFile);
			} catch (IOException e) {
				throw new IOException("Problem while reading the file - " + authorFile.getAbsolutePath(), e);
			}
			Author authorObj = Constants.gson.fromJson(content, Author.class);

			String username = authorFile.getName().replace(".json", "");
			authors.put(username, authorObj);
			String url = siteConfig.getBaseUrl() + "/" + siteConfig.getAuthorBase() + "/" + username;
			url = StringUtils.removeExtraSlash(url);
			authorObj.setUrl(url);
			SocialMediaLinks socialMediaLinks = authorObj.getSocialMediaLinks();
			if (socialMediaLinks != null) {
				String twitterUrl = socialMediaLinks.getTwitterUrl();
				if (StringUtils.isNotBlank(twitterUrl)) {
					int index = twitterUrl.lastIndexOf("/");
					String twitterUsername = twitterUrl.substring(index + 1);
					if (twitterUsername.contains("?")) {
						twitterUsername = twitterUsername.substring(0, twitterUsername.indexOf('?'));
					}
					socialMediaLinks.setTwitterUsername(twitterUsername);
				}
			}

		}

		siteConfig.setAuthors(authors);
		siteConfig.setPublisherSocialLinks(
				authors.get(siteConfig.getDefaultAuthor()).getSocialMediaLinks());
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
