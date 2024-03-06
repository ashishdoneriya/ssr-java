package com.csetutorials.ssj.utils;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

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

	public static String parseMarkdown(String content) {
		List<Extension> extensions = Arrays.asList(TablesExtension.create(), HeadingAnchorExtension.create());
		Parser parser = Parser.builder().extensions(extensions).build();
		Node document = parser.parse(content);
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
		return renderer.render(document);
	}

}
