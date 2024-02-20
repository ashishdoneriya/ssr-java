package com.csetutorials.ssj.contants;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class PathService {

	@Setter
	@Getter
	public String rootDir;

	public String getSiteConfigDir() {
		return rootDir + File.separator + "ssj.json";
	}

	public String getPostsDir() {
		return rootDir + File.separator + "posts";
	}

	public String getPagesDir() {
		return rootDir + File.separator + "pages";
	}

	public String getDataDir() {
		return rootDir + File.separator + "data";
	}

	public String getAuthorsDir() {
		return rootDir + File.separator + "authors";
	}

	public String getTempDir() {
		return rootDir + File.separator + "temp";
	}

	public String getThemesDir() {
		return rootDir + File.separator + "themes";
	}

	public String getGeneratedHtmlDir() {
		return rootDir + File.separator + "dist";
	}

	public String getTempLayoutsDir() {
		return getTempDir() + File.separator + "layouts";
	}

}
