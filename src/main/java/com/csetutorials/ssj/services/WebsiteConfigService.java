package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.WebsiteInfo;
import com.csetutorials.ssj.contants.SSJPaths;
import com.csetutorials.ssj.exceptions.JsonParsingException;
import com.csetutorials.ssj.exceptions.ThemeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class WebsiteConfigService {

	private WebsiteInfo config;

	@Autowired
	SSJPaths SSJPaths;
	@Autowired
	TemplateService templateService;
	@Autowired
	FileService fileService;
	@Autowired
	JsonService jsonService;

	public synchronized WebsiteInfo getSiteConfig() {
		if (config != null) {
			return config;
		}
		String json = fileService.getString(SSJPaths.getSiteConfigDir());
		TypeReference<Map<String, Object>> type = new TypeReference<>(){};
		Map<String, Object> rawConfig;
		try {
			rawConfig = (new ObjectMapper()).readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new JsonParsingException("Problem while parsing json file for - " + SSJPaths.getSiteConfigDir(), e);
		}
		config = jsonService.convert(json, WebsiteInfo.class);
		config.setRawConfig(rawConfig);
		config.setActiveThemeDir(getActiveThemeDir(config));
		return config;
	}

	private String getActiveThemeDir(WebsiteInfo config) {
		String activeTheme = config.getTheme();
		if (activeTheme != null) {

			if (activeTheme.startsWith("https://github.com")) {
				String url = activeTheme;
				if (url.endsWith("/")) {
					url = url.substring(0, url.length() - 1);
				}
				String tree = "master";
				String repo = url;
				if (url.contains("tree")) {
					repo = url.substring(0, url.indexOf("/tree"));
					tree = url.substring(url.lastIndexOf("/") + 1);
				}
				String themeName = repo.substring(repo.lastIndexOf("/") + 1);
				repo = repo + ".git";
				File dir = new File(StringUtils.removeExtraSlash(SSJPaths.getThemesDir() + File.separator + themeName));
				if (!fileService.listFiles(dir).isEmpty()) {
					return dir.getAbsolutePath();
				} else {
					Collection<Ref> remoteRefs;
					try {
						remoteRefs = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(repo).call();
					} catch (GitAPIException e) {
						throw new ThemeException("Problem while fetching theme from [" + repo + "]", e);
					}
					Ref ref = null;
					for (Ref temp : remoteRefs) {
						String tempName = temp.getName();
						tempName = tempName.substring(tempName.lastIndexOf("/") + 1);
						if (tempName.equals(tree)) {
							ref = temp;
							break;
						}
					}
					fileService.mkdirs(dir.getParentFile());
					try (Git result = Git.cloneRepository().setURI(repo).setDirectory(dir).setBranch(ref.getName())
							.call()) {
						// For autoclosing
					} catch (Exception e) {
						System.out.println("Problem while cloning theme from [" + repo + "]");
					}
					return dir.getAbsolutePath();
				}
			} else {
				File dir = new File(StringUtils.removeExtraSlash(SSJPaths.getThemesDir() + File.separator + activeTheme));
				if (dir.exists() && dir.isDirectory()) {
					return dir.getAbsolutePath();
				} else {
					System.out.println("Invalid theme -" + activeTheme);
					System.exit(1);
				}
			}

		}
		List<File> themes = fileService.listFiles(SSJPaths.getThemesDir());
		if (themes.isEmpty()) {
			throw new ThemeException("No theme found");
		}
		if (themes.size() > 1) {
			System.out.println("Kindly set atleast one theme using field 'theme'");
			System.exit(1);
		}
		return themes.getFirst().getAbsolutePath();
	}


}
