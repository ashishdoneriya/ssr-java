package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.Author;
import com.csetutorials.ssj.beans.WebsiteConfig;
import com.csetutorials.ssj.beans.SocialMediaLinks;
import com.csetutorials.ssj.contants.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataService {

	@Autowired
	PathService pathService;
	@Autowired
	FileService fileService;
	@Autowired
	JsonService jsonService;

	public void readData(WebsiteConfig websiteConfig) {
		Map<String, Object> map = new HashMap<>();
		for (File file : fileService.listFiles(pathService.getDataDir())) {
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
		websiteConfig.setData(map);
	}

	private Object getObject(File file) {
		return jsonService.convert(fileService.getString(file), Object.class);
	}

	private Object readDir(File dir) {
		Map<String, Object> map = new HashMap<>();
		for (File file : fileService.listFiles(dir)) {
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

	public void loadAllAuthors(WebsiteConfig websiteConfig) {
		Map<String, Author> authors = new HashMap<>();
		for (File authorFile : fileService.listFiles(pathService.getAuthorsDir())) {
			String content = fileService.getString(authorFile);
			Author authorObj = jsonService.convert(content, Author.class);
			String username = authorFile.getName().replace(".json", "");
			authors.put(username, authorObj);
			String url = websiteConfig.getBaseUrl() + "/" + websiteConfig.getAuthorBase() + "/" + username;
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

		websiteConfig.setAuthors(authors);
		websiteConfig.setPublisherSocialLinks(
				authors.get(websiteConfig.getDefaultAuthor()).getSocialMediaLinks());
		websiteConfig.getRawConfig().put("publisherSocialLinks", websiteConfig.getPublisherSocialLinks());
		setTwitterUsername(websiteConfig.getPublisherSocialLinks());
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
