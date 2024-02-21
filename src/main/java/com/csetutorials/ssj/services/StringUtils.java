package com.csetutorials.ssj.services;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class StringUtils {

	public static String getContentBody(String content) {
		int index1 = content.indexOf("---");
		if (index1 == -1) {
			return null;
		}
		int index3 = content.indexOf("---", index1 + 1);
		if (index3 == -1) {
			return null;
		}
		int index4 = content.indexOf("\n", index3 + 1);
		if (index4 == -1) {
			return null;
		}
		return content.substring(index4 + 1).trim();
	}
	
	public static Map<String, Object> getRawParams(String content) {
		int index1 = content.indexOf("---");
		Map<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		if (index1 == -1) {
			return map;
		}
		int index2 = content.indexOf("\n", index1 + 1);
		if (index2 == -1) {
			return map;
		}
		int index3 = content.indexOf("---", index2 + 1);
		if (index3 == -1) {
			return map;
		}
		int index4 = content.indexOf("\n", index3 + 1);
		if (index4 == -1) {
			return map;
		}
		String temp1 = content.substring(index2 + 1, index3 - 1).trim();
		if (temp1.isEmpty()) {
			return map;
		}
		Yaml yaml = new Yaml();
		return yaml.load(temp1);
	}

	public static String removeExtraSlash(String str) {
		if (str.endsWith("/")) {
			str = str.substring(0, str.length() - 1);
		}
		return str.replaceAll("/+", "/").replace("http:/", "http://").replace("https:/", "https://");
	}

	public static String toFirstCharUpperAll(String string) {
		StringBuilder sb = new StringBuilder(string);
		for (int i = 0; i < sb.length(); i++)
			if (i == 0 || sb.charAt(i - 1) == '-')
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
		return sb.toString();
	}

	public static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static String getString(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line = bufferedReader.readLine();
			while (line != null) {
				sb.append(line).append("\n");
				line = bufferedReader.readLine();
			}
			return sb.toString();
		}
	}

}
