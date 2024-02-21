package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.Author;
import com.csetutorials.ssj.beans.SiteConfig;
import com.csetutorials.ssj.beans.SocialMediaLinks;
import com.csetutorials.ssj.contants.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

	public void readData(SiteConfig siteConfig) throws IOException {
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
		siteConfig.setData(map);
	}

	private Object getObject(File file) throws IOException {
		return jsonService.convert(fileService.getString(file), Object.class);
	}

	private Object readDir(File dir) throws IOException {
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

	public void loadAllAuthors(SiteConfig siteConfig) throws IOException {
		Map<String, Author> authors = new HashMap<>();
		for (File authorFile : fileService.listFiles(pathService.getAuthorsDir())) {
			String content;
			try {
				content = fileService.getString(authorFile);
			} catch (IOException e) {
				throw new IOException("Problem while reading the file - " + authorFile.getAbsolutePath(), e);
			}
			Author authorObj = jsonService.convert(content, Author.class);
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
