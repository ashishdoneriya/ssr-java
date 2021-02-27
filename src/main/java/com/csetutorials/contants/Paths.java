package com.csetutorials.contants;

public class Paths {

	public static String rootDir;

	public static String getRoot() {
		return rootDir;
	}

	public static String getSiteConfigDir() {
		return rootDir + "/ssj.conf";
	}

	public static String getPostsDir() {
		return rootDir + "/posts";
	}

	public static String getPagesDir() {
		return rootDir + "/pages";
	}

	public static String getDataDir() {
		return rootDir + "/data";
	}

	public static String getAuthorsDir() {
		return rootDir + "/authors";
	}

	public static String getTempDir() {
		return rootDir + "/temp";
	}

	public static String getThemesDir() {
		return rootDir + "/themes";
	}

	public static String getGeneratedHtmlDir() {
		return rootDir + "/dist";
	}

	public static String getTempLayoutsDir() {
		return getTempDir() + "/layouts";
	}

}
