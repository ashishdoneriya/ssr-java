package com.csetutorials.ssj.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {

	public static boolean isNotBlank(String str) {
		return str != null && !str.trim().isEmpty();
	}

	public static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static String removeExtraSlash(String str) {
		if (str.endsWith("/")) {
			str = str.substring(0, str.length() - 1);
		}
		return str.replaceAll("/+", "/")
				.replace("http:/", "http://")
				.replace("https:/", "https://");
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
