package com.csetutorials.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.csetutorials.beans.SiteConfig;
import com.google.gson.JsonSyntaxException;

public class DataUtils {

	public static void readData(SiteConfig siteConfig) throws JsonSyntaxException, IOException {
		String dataDir = siteConfig.getDataDir();
		File dir = new File(dataDir);
		Map<String, Object> map = new HashMap<>();
		if (!dir.exists() || dir.list().length == 0) {
			siteConfig.setData(new HashMap<String, Object>(1));
			return;
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

}
