package com.csetutorials.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.csetutorials.beans.SiteConfig;

public class TemplateUtils {

	public static void setEngine(SiteConfig config) throws IOException {
		// Initialize the engine.
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(Velocity.RESOURCE_LOADER, "string");
		engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
		engine.addProperty("string.resource.loader.repository.static", "false");
		// engine.addProperty("string.resource.loader.modificationCheckInterval", "1");
		engine.init();
		StringResourceRepository repo = (StringResourceRepository) engine
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		for (File file : FileUtils.getFilesRecursively(config.getTempLayoutsPath())) {
			repo.putStringResource(file.getName(), FileUtils.getString(file.getAbsolutePath()));
		}
		config.setEngine(engine);
	}

	public static void addTemplate(SiteConfig config, String templateName, String templateContent) {
		// Initialize my template repository. You can replace the "Hello $w" with your
		// String.
		StringResourceRepository repo = (StringResourceRepository) config.getEngine()
				.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource(templateName, templateContent);
	}

	public static String formatContent(VelocityEngine engine, VelocityContext context, String templateName) {
		Template template = engine.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

	public static String parseMarkdown(String content) {
		Parser parser = Parser.builder().build();
		Node document = parser.parse(content);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		return renderer.render(document);
	}

	/*
	 * public static VelocityEngine getVelocityEngine(SiteConfig siteConfig) {
	 * VelocityEngine engine = new VelocityEngine();
	 * engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
	 * engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
	 * siteConfig.getTempLayoutsPath()); return engine; }
	 */

	public static void createLayouts(SiteConfig siteConfig, Set<String> layouts) throws IOException {
		List<File> layoutsList = FileUtils.getFilesRecursively(siteConfig.getLayoutsDir());
		for (File layoutFile : layoutsList) {
			FileUtils.copyFile(layoutFile,
					new File(siteConfig.getTempLayoutsPath() + File.separator + layoutFile.getName()));
		}
		for (String templateFileName : layouts) {
			if (!new File(siteConfig.getLayoutsDir() + File.separator + templateFileName).exists()) {
				continue;
			}
			String templateContent = generateTemplate(siteConfig.getLayoutsDir() + File.separator, templateFileName);
			FileUtils.write(siteConfig.getTempLayoutsPath() + File.separator + templateFileName, templateContent);
		}
	}

	private static String generateTemplate(String layoutPath, String templateName) throws IOException {
		String fileContent = FileUtils.getString(layoutPath + templateName);
		while (true) {

			Map<String, String> params = StringUtils.getRawParams(fileContent);
			if (params == null || params.isEmpty() || !params.containsKey("layout")) {
				return fileContent;
			}
			templateName = params.get("layout");
			String parentContent = FileUtils.getString(layoutPath + templateName);
			parentContent.replaceAll("\\$content", "REPLACE_ME_SSR");
			fileContent = parentContent.replace("$content", StringUtils.getContentBody(fileContent));
			fileContent.replace("REPLACE_ME_SSR", "\\$content");
		}
	}

}
