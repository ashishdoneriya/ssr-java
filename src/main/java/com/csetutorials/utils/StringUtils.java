package com.csetutorials.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StringUtils {

	public static String removeQuotesAndBlocks(String str) {
		if (str.startsWith("\"") || str.startsWith("'") || str.startsWith("[")) {
			return str.substring(1, str.length() - 1);
		} else {
			return str;
		}
	}

	public static List<String> parseList(String str) {
		if (str == null) {
			return new ArrayList<>(1);
		}
		return Constants.gson.fromJson(str, Constants.stringListType);
	}

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
		String temp1 = content.substring(index4 + 1).trim();
		return temp1;
	}

	public static Map<String, String> getRawParams(String content) {
		int index1 = content.indexOf("---");
		Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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
		String[] temp2 = temp1.split("\n");

		for (String temp3 : temp2) {
			if (!temp3.contains(":")) {
				continue;
			}
			String key = temp3.substring(0, temp3.indexOf(":")).trim();
			if (temp3.length() == temp3.indexOf(":") + 1) {
				continue;
			}
			String value = temp3.substring(temp3.indexOf(":") + 1).trim();
			if (value.isEmpty()) {
				continue;
			}
			if (value.startsWith("\"")) {
				value = value.substring(1, value.length() - 1);
			}
			map.put(key, value);
		}
		return map;
	}

	public static String removeExtraSlash(String str) {
		if (str.endsWith("/")) {
			str = str.substring(0, str.length() - 1);
		}
		return str.replaceAll("/+", "/");
	}

	public static String toFirstCharUpperAll(String string) {
		StringBuffer sb = new StringBuffer(string);
		for (int i = 0; i < sb.length(); i++)
			if (i == 0 || sb.charAt(i - 1) == '-')
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
		return sb.toString();
	}

	public static Object parseToObject(String str) {
		if (isBlank(str)) {
			return null;
		}
		return Constants.gson.fromJson(str, Object.class);
	}

	public static String parseToString(String str) {
		if (isBlank(str)) {
			return null;
		}
		str = str.trim();
		return Constants.gson.fromJson(str, String.class);
	}

	public static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

}
