package com.csetutorials.ssj.services;

import com.csetutorials.ssj.beans.Configuration;
import com.csetutorials.ssj.exceptions.ThemeException;
import com.csetutorials.ssj.utils.StringUtils;
import lombok.Cleanup;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ThemeService {

	@Autowired
	Configuration configuration;

	@Autowired
	FileService fileService;

	public String getActiveThemeDir() {
		String activeTheme = configuration.getWebsite().getActiveTheme();
		if (StringUtils.isNotBlank(activeTheme) && activeTheme.startsWith("https://github.com")) {
			String url = activeTheme;
			String themeName = extractThemeName(url);
			File themeDir = new File(StringUtils.removeExtraSlash(configuration.getSsjPaths().getThemesDir() + File.separator + themeName));
			if (!isThemeAlreadyDownloaded(themeDir)) {
				downloadTheme(url, themeDir);
			}
			return themeDir.getAbsolutePath();
		} else if (StringUtils.isNotBlank(activeTheme)) {
			File themeDir = new File(StringUtils.removeExtraSlash(configuration.getSsjPaths().getThemesDir() + File.separator + activeTheme));
			if (!isThemeAlreadyDownloaded(themeDir)) {
				throw new ThemeException("Theme doesn't exist - " + activeTheme + ", in dir - " + themeDir.getAbsolutePath());
			}
			return themeDir.getAbsolutePath();
		} else {
			List<File> themes = fileService.listFiles(configuration.getSsjPaths().getThemesDir());
			if (themes.isEmpty()) {
				throw new ThemeException("No theme found");
			}
			if (themes.size() > 1) {
				throw new ThemeException("Kindly set active theme in configuration");

			}
			return themes.getFirst().getAbsolutePath();
		}
	}

	private void downloadTheme(String url, File themeDir) {
		if (!url.endsWith(".git")) {
			url = url + ".git";
		}

		fileService.mkdirs(themeDir.getParentFile());
		// Create the Git object and set up the CloneCommand
		CloneCommand git = Git.cloneRepository();
		git.setURI(url);
		themeDir.getParentFile().mkdirs();
		git.setDirectory(themeDir);
		// Execute the clone operation
		try {
			@Cleanup Git result = git.call();
		} catch (GitAPIException e) {
			throw new ThemeException("Problem while cloning the theme from url - " + url, e);
		}
	}

	private boolean isThemeAlreadyDownloaded(File themeDir) {
		return !fileService.listFiles(themeDir).isEmpty();
	}

	private String extractThemeName(String url) {
		Pattern pattern = Pattern.compile("http://github\\.com/.+/(.+)(?:.git)?");
		Matcher matcher = pattern.matcher(url);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			throw new ThemeException("Cannot extract the theme name");
		}
	}

}
